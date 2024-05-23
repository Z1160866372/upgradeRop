/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 *
 */

package com.richeninfo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.richeninfo.entity.mapper.entity.*;
import com.richeninfo.entity.mapper.mapper.master.CommonMapper;
import com.richeninfo.entity.mapper.mapper.master.MiguFlowMapper;
import com.richeninfo.pojo.*;
import com.richeninfo.service.CommonService;
import com.richeninfo.service.MiguFlowService;
import com.richeninfo.util.PacketHelper;
import com.richeninfo.util.ReqWorker;
import com.richeninfo.util.RopServiceManager;
import lombok.extern.log4j.Log4j;
import net.sf.json.JSONArray;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @auth sunxiaolei
 * @date 2024/4/26 11:19
 */
@Log4j
@Service
public class MiguFlowServiceImpl implements MiguFlowService {

    @Resource
    private MiguFlowMapper miguFlowMapper;
    @Resource
    private CommonService commonService;

    @Resource
    private PacketHelper packetHelper;
    @Resource
    private CommonMapper commonMapper;

    @Resource
    private RopServiceManager ropServiceManager;

    @Override
    public JSONObject initializeUser(String userId, String secToken, String channelId, String actId, String ditch) {
        JSONObject jsonObject = new JSONObject();
        ActivityUser activityUser = miguFlowMapper.findCurMonthUserInfo(userId);
        if (activityUser == null) {
            activityUser = new ActivityUser();
            activityUser.setUserId(userId);
            activityUser.setAward(0);
            activityUser.setUserType(isBlack(userId));
            activityUser.setDitch(ditch);
            miguFlowMapper.saveUser(activityUser);
        }
        activityUser.setSecToken(secToken);
        jsonObject.put("user", activityUser);
        return jsonObject;
    }

    @Override
    public JSONObject selectVideoList(String secToken, String channelId, String actId) {
        JSONObject jsonObject = new JSONObject();
        List<ActivityConfiguration> list = miguFlowMapper.findGiftByTypeId(actId);
        jsonObject.put("list", list);
        return jsonObject;
    }

    @Override
    public JSONObject getActGift(String userId, String secToken, String channelId, String actId, String randCode, String wtAcId, String wtAc, String ditch) {
        JSONObject jsonObject = new JSONObject();
        ActivityUser user = miguFlowMapper.findCurMonthUserInfo(userId);
        JSONObject newJsonObject = new JSONObject();
        if (user != null && commonService.verityTime(actId).equals("underway") && user.getAward() < 1 && user.getUserType() < 1) {
            //查询是否领取过当月的礼包
            ActivityUserHistory history = miguFlowMapper.findCurYwHistory(userId);
            ActivityConfiguration gift = miguFlowMapper.findGiftByUnlocked(0, actId);
            if (history == null) {
                //查询活动配置礼包
                history = new ActivityUserHistory();
                history.setUserId(userId);
                history.setRewardName(gift.getName());
                history.setUnlocked(gift.getUnlocked());
                history.setActId(actId);
                history.setKeyword(actId);
                history.setTypeId(gift.getTypeId());
                history.setChannelId(channelId);
                history.setDitch(ditch);
                history.setActivityId(gift.getActivityId());
                history.setItemId(gift.getItemId());
                int status = miguFlowMapper.saveHistory(history);
                history = miguFlowMapper.findCurYwHistory(userId);
                try {
                    if (status > 0) {//业务发放
                        newJsonObject = transact3066Business(history, gift, randCode, channelId, wtAcId, wtAc, actId);
                        Boolean ywStatus = newJsonObject.getBoolean("transact_result");
                        jsonObject.put("msg", ywStatus);
                        jsonObject.put("newJsonObject", newJsonObject);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (history.getStatus() == 3) {
                    jsonObject.put("msg", "ybl");
                } else {
                    newJsonObject = transact3066Business(history, gift, randCode, channelId, wtAcId, wtAc, actId);
                    Boolean ywStatus = newJsonObject.getBoolean("transact_result");
                    jsonObject.put("msg", ywStatus);
                    jsonObject.put("newJsonObject", newJsonObject);
                }
            }
        } else {
            jsonObject.put("msg", "error");
        }
        return jsonObject;
    }

    @Override
    public JSONObject sendMessage5956(String userId, String secToken, String channelId, String actId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject = commonService.sendSms5956(userId, actId, 0);
        return jsonObject;
    }

    public JSONObject transact3066Business(ActivityUserHistory history, ActivityConfiguration config, String randCode, String channelId, String wtAcId, String wtAc, String actId) {
        JSONObject object = new JSONObject();
        boolean transact_result = false;
        Result result = new Result();
        try {
            List<VasOfferInfo> offerList = new ArrayList<VasOfferInfo>();
            if (config.getActivityId().contains(",")) {
                for (int i = 0; i < config.getActivityId().split(",").length; i++) {
                    VasOfferInfo vasOfferInfo = new VasOfferInfo();
                    vasOfferInfo.setOfferId(config.getActivityId().split(",")[i]);
                    vasOfferInfo.setEffectiveType("0");
                    vasOfferInfo.setOperType("0");
                    offerList.add(vasOfferInfo);
                }
            } else {
                VasOfferInfo vasOfferInfo = new VasOfferInfo();
                vasOfferInfo.setOfferId(config.getActivityId());
                vasOfferInfo.setEffectiveType("0");
                vasOfferInfo.setOperType("0");
                offerList.add(vasOfferInfo);
            }
            Packet packet = packetHelper.getCommitPacket306602(history.getUserId(), randCode, offerList, channelId, history.getDitch());
            String message = ropServiceManager.execute(packet, history.getUserId(), actId);
            message = ReqWorker.replaceMessage(message);
            result = JSON.parseObject(message, Result.class);
            String res = result.getResponse().getErrorInfo().getCode();
            String DoneCode = result.getResponse().getRetInfo().getString("DoneCode");
            if (Constant.SUCCESS_CODE.equals(res)) {
                transact_result = true;
                miguFlowMapper.updateUserAward(history.getUserId());
                history.setStatus(Constant.STATUS_RECEIVED);
                object.put(Constant.MSG, Constant.SUCCESS);
            } else {
                transact_result = false;
                history.setStatus(Constant.STATUS_RECEIVED_ERROR);
                object.put(Constant.MSG, Constant.FAILURE);
            }
                history.setCode(JSONObject.toJSONString(packet));
                object.put("res", res);
                history.setMessage(message);
                object.put("DoneCode", DoneCode);
                object.put("update_history", JSON.toJSONString(history));
                miguFlowMapper.updateHistory(history);
            if (transact_result) {
                Packet new_packet = packetHelper.orderReporting(config, packet, wtAcId, wtAc);
                System.out.println(new_packet.toString());
                String result_String = ropServiceManager.executes(new_packet, history.getUserId(), actId);
                ActivityOrder order = new ActivityOrder();
                order.setName(commonMapper.selectActivityByActId(config.getActId()).getName());
                String packetThirdTradeId = packet.getPost().getPubInfo().getTransactionId();
                order.setThirdTradeId(packetThirdTradeId);
                order.setOrderItemId("JYRZ" + packetThirdTradeId.substring(packetThirdTradeId.length() - 21));
                order.setBossId(config.getActivityId());
                order.setCommodityName(config.getName());
                order.setUserId(history.getUserId());
                order.setCode(JSON.toJSONString(new_packet));
                order.setMessage(result_String);
                order.setChannelId(channelId);
                commonMapper.insertActivityOrder(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        object.put("transact_result", transact_result);
        return object;
    }

    /**
     * 用户记录操作
     *
     * @param caozuo
     * @param userId
     */
    @Override
    public void actRecord(String caozuo, String actId, String userId) {
        log.info("userId:" + userId + ",caozuo" + caozuo);
        Map<String, Object> map = new HashMap<>();
        OperationLog record = new OperationLog();
        record.setInstructions(caozuo);
        record.setUserId(userId);
        record.setActId(actId);
        record.setUserId(userId);
        try {
            commonMapper.insertOperationLog(record, "wt_miguflow_operationLog");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //更新黑名单
    public int isBlack(String userId) {
        int balck = 0;
        List<String> isBlack = miguFlowMapper.findIsBlack(userId);
        balck = null == isBlack || isBlack.isEmpty() ? 0 : 1;
        log.info("balck===" + balck);
        return balck;

    }
}
