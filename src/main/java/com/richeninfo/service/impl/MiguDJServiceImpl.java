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
import com.richeninfo.entity.mapper.mapper.master.MiguDJMapper;
import com.richeninfo.pojo.Constant;
import com.richeninfo.pojo.Packet;
import com.richeninfo.pojo.Result;
import com.richeninfo.pojo.VasOfferInfo;
import com.richeninfo.service.CommonService;
import com.richeninfo.service.MiguDJService;
import com.richeninfo.util.PacketHelper;
import com.richeninfo.util.ReqWorker;
import com.richeninfo.util.RopServiceManager;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @auth sunxiaolei
 * @date 2024/4/26 11:19
 */
@Log4j
@Service
public class MiguDJServiceImpl implements MiguDJService {

    @Resource
    private MiguDJMapper miguDJMapper;
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
        ActivityUser activityUser = miguDJMapper.findCurMonthUserInfo(userId);
        if (activityUser == null) {
            activityUser = new ActivityUser();
            activityUser.setUserId(userId);
            activityUser.setAward(0);
            ActivityRoster activityRoster = miguDJMapper.findActivityRoster(userId, 1);
            if (activityRoster == null) {
                activityUser.setUserType(0);
                activityUser.setNickName("");
            } else {
                activityUser.setNickName(activityRoster.getActId());
                activityUser.setUserType(1);
            }
            activityUser.setDitch(ditch);
            activityUser.setChannelId(channelId);
            miguDJMapper.saveUser(activityUser);
        }
        jsonObject.put(Constant.MSG, Constant.SUCCESS);
        activityUser.setSecToken(secToken);
        activityUser.setUserId(Base64.getEncoder().encodeToString(activityUser.getUserId().getBytes()));
        jsonObject.put("user", activityUser);
        return jsonObject;
    }

    @Override
    public JSONObject selectVideoList(String secToken, String channelId, String actId) {
        List<ActivityConfiguration> list = miguDJMapper.findGiftByTypeId(actId);
        JSONObject jsonObject = new JSONObject();
        Map<Integer, List<ActivityConfiguration>> groups = list.stream().collect(Collectors.groupingBy(ActivityConfiguration::getUserType));
        log.info(groups.toString());
        // jsonObject.put("list",groups);
        jsonObject.put("Newlist", list);
        log.info(groups.size());
        return jsonObject;
    }


    @Override
    public JSONObject draw(String secToken, String channelId, String actId, String answer, int mark, String ditch) {
        JSONObject jsonObject = new JSONObject();
        String userId = "";
        if (commonService.verityTime(actId).equals("underway")) {
            if (!StringUtils.isEmpty(secToken)) {
                try {
                    userId = commonService.getMobile(secToken, channelId);
                    if (userId == null || userId.isEmpty()) {
                        jsonObject.put(Constant.MSG, "login");
                        return jsonObject;
                    }
                } catch (Exception e) {
                    jsonObject.put(Constant.MSG, "loginError");
                    return jsonObject;
                }
            } else {
                jsonObject.put(Constant.MSG, "login");
                return jsonObject;
            }
            ActivityUser user = miguDJMapper.findCurMonthUserInfo(userId);
            if (user != null && user.getMark() < 11 && user.getUserType() > 0) {
                if(mark!= user.getMark()){
                    //更新题目和阶段
                    String newAnswer;
                    if(StringUtils.isEmpty(user.getAnswer())){
                        newAnswer=answer;
                    }else{
                        newAnswer=user.getAnswer()+"#"+answer;
                    }
                   int status= miguDJMapper.updateUserMarkAndAnswer(newAnswer,mark,userId);
                   if(status>0){
                       jsonObject.put("msg", "success");
                   }else{
                       jsonObject.put("msg", "error");
                   }
                }
            } else {
                jsonObject.put("msg", "error");
            }
        } else {
            jsonObject.put(Constant.MSG, "ActError");
            return jsonObject;
        }
        return jsonObject;
    }

    public JSONObject transact3066Business(ActivityUserHistory history, ActivityConfiguration config, String randCode, String channelId, String wtAcId, String wtAc, String actId, String ditch) {
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
            Packet packet = packetHelper.getCommitPacket306602(history.getUserId(), randCode, offerList, channelId, ditch);
         /*   String message = ropServiceManager.execute(packet, history.getUserId(), actId);
            message = ReqWorker.replaceMessage(message);
            result = JSON.parseObject(message, Result.class);
            String res = result.getResponse().getErrorInfo().getCode();
            String DoneCode = result.getResponse().getRetInfo().getString("DoneCode");*/
            String res = "0000";
            String DoneCode = "9999";
            String message = "测试";
            if (Constant.SUCCESS_CODE.equals(res)) {
                transact_result = true;
                miguDJMapper.updateUserAward(history.getUserId());
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
            miguDJMapper.updateHistory(history);
            /*if (transact_result) {
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
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        object.put("transact_result", transact_result);
        return object;
    }


    //更新黑名单
    public int isBlack(String userId) {
        int balck = 0;
        List<String> isBlack = miguDJMapper.findIsBlack(userId);
        balck = null == isBlack || isBlack.isEmpty() ? 0 : 1;
        log.info("balck===" + balck);
        return balck;

    }
}
