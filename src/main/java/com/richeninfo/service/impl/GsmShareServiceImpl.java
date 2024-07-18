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
import com.richeninfo.entity.mapper.mapper.master.GsmShareMapper;
import com.richeninfo.pojo.Constant;
import com.richeninfo.pojo.Packet;
import com.richeninfo.pojo.Result;
import com.richeninfo.pojo.VasOfferInfo;
import com.richeninfo.service.CommonService;
import com.richeninfo.service.GsmShareService;
import com.richeninfo.util.CommonUtil;
import com.richeninfo.util.PacketHelper;
import com.richeninfo.util.ReqWorker;
import com.richeninfo.util.RopServiceManager;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @auth sunxiaolei
 * @date 2024/4/26 11:19
 */
@Log4j
@Service
public class GsmShareServiceImpl implements GsmShareService {

    @Resource
   private GsmShareMapper gsmShareMapper;
    @Resource
    private CommonService commonService;

    @Resource
    private CommonMapper commonMapper;

    @Resource
    private PacketHelper packetHelper;

    @Resource
    private RopServiceManager ropService;

    @Resource
    CommonUtil commonUtil;

    @Override
    public JSONObject initializeUser(String userId, String secToken, String channelId, String actId,String ditch) {
        JSONObject jsonObject = new JSONObject();
        ActivityUser activityUser = gsmShareMapper.findUserInfo(userId);
        if (activityUser == null) {
            activityUser=new ActivityUser();
            activityUser.setUserId(userId);
            activityUser.setAward(0);
            activityUser.setPlayNum(1);
            activityUser.setUnlocked(0);
            activityUser.setChannelId(channelId);
            activityUser.setDitch(ditch);
            activityUser.setUserType(userType(userId));
            gsmShareMapper.saveUser(activityUser);
        }
            activityUser.setSecToken(secToken);
            jsonObject.put("user", activityUser);
            return jsonObject;
    }

    @Override
    public JSONObject getActGift(String userId, String secToken, String channelId, String actId,String ditch) {
        JSONObject jsonObject = new JSONObject();
        ActivityUser user = gsmShareMapper.findUserInfo(userId);
        if (user != null && commonService.verityTime(actId).equals("underway") && user.getAward() < 1 ) {
               if( !commonService.checkUserIsChinaMobile(userId,actId)){
                   jsonObject.put(Constant.MSG,"noShYd");
                   return jsonObject;
               }
            String code="";
                    ActivityConfiguration gift=new ActivityConfiguration();
                    int lostPlayNum=gsmShareMapper.lostPlayNum(userId);
                    if(lostPlayNum>0){
                        if(user.getUserType()==0){//名单外用户 谢谢惠顾
                            gift=gsmShareMapper.findGiftByUnlocked(7,actId);
                        }else{
                            List<ActivityConfiguration> giftList=gsmShareMapper.findGiftListByActId(actId);
                              gift=  randomGsmGift(giftList);
                            if(gift.getUnlocked()!=7){//抽中了 卡券或者业务
                                log.info(userId+"抽中卡券或者业务检查数量是否足够==========="+gift);
                                List<ActivityCardList>  bindlist=gsmShareMapper.findBindUserIdIsNullByTypeId(gift.getUnlocked());
                                int  scount=0;
                                if(user.getUserType()==1) {//白金，钻石卡用户对应的数量
                                    scount=gift.getAllAmount();
                                }else {//金卡、银卡用户对应的数量
                                    scount=gift.getAmount();
                                }
                                if(scount>0){
                                    int loseNum=0;
                                    if(user.getUserType()==1) {//白金，钻石卡用户对应的数量
                                        loseNum=gsmShareMapper.lostGiftAllAmount(gift.getId());
                                    }else {//金卡、银卡用户对应的数量
                                        loseNum=gsmShareMapper.lostGiftAmount(gift.getId());
                                    }
                                    if(loseNum<1){
                                        gift=gsmShareMapper.findGiftByUnlocked(7,actId);
                                    }else{
                                        if(gift.getUnlocked()!=6&&bindlist.size()>0) {
                                            //查询是否还有剩余门票
                                            int updateBindUserId=gsmShareMapper.updateBindUserId(userId, bindlist.get(0).getId());
                                            if(updateBindUserId<1){
                                                gift=gsmShareMapper.findGiftByUnlocked(7,actId);
                                            }else {
                                                if(gift.getUnlocked()<6) {//需要发送短信
                                                    code=bindlist.get(0).getCouponCode();
                                                    gsmShareMapper.updateUserNickName(code,userId);
                                                    String msg=gift.getRemark();
                                                    String message= msg.replace("code",code);
                                                    log.info("获奖短信内容==============="+message);
                                                    sendNote(userId,message);
                                                }
                                            }
                                        }
                                    }
                                }else{
                                    gift=gsmShareMapper.findGiftByUnlocked(7,actId);
                                }
                            }
                        }
                        gsmShareMapper.updateUserAward(userId);
                        gsmShareMapper.updateCurMark(String.valueOf(gift.getUnlocked()),userId);
                        int status=saveHistory(gift, userId, channelId,code,ditch);
                        if(status>0){
                            jsonObject.put("giftName", gift.getName());
                            jsonObject.put("gift", gift);
                            jsonObject.put("code", code);
                            jsonObject.put("msg", "success");
                        }else{
                            jsonObject.put("msg", "timeout");
                        }
                    }else{
                        jsonObject.put("msg", "timeout");
                    }
            ActivityUser users = gsmShareMapper.findUserInfo(userId);
            jsonObject.put("user", users);
        } else {
            jsonObject.put("msg", "error");
        }
        return jsonObject;
    }

    @Override
    public JSONObject transact(String userId, String secToken, String channelId, String actId,String randCode,String wtAcId, String wtAc, String ditch) {
        JSONObject jsonObject = new JSONObject();
        ActivityUser user = gsmShareMapper.findUserInfo(userId);
        JSONObject newJsonObject = new JSONObject();
        if (user != null && commonService.verityTime(actId).equals("underway") && user.getUnlocked() < 1) {
            //查询是否领取过当月的礼包
            ActivityUserHistory history = gsmShareMapper.findCurYwHistory(userId);
            ActivityConfiguration gift = gsmShareMapper.findGiftByUnlocked(6, actId);
            if (history == null) {
                history = gsmShareMapper.findSaveYwHistory(userId);
                newJsonObject = transact3066Business(history, gift, randCode, channelId, wtAcId, wtAc, actId);
                Boolean ywStatus = newJsonObject.getBoolean("transact_result");
                jsonObject.put("msg", ywStatus);
                jsonObject.put("newJsonObject", newJsonObject);
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
        JSONObject jsonObject=new JSONObject();
        jsonObject= commonService.sendSms5956(userId,actId,0);
        return jsonObject;
    }


    public int userType(String userId){
        ActivityRoster activityRoster=gsmShareMapper.findActWhiteList(userId);
        int type=activityRoster==null ?0:activityRoster.getUserType();
        return type;
    }

    public void sendNote(String userId,String message) {
        log.info("短信内容："+message);
        try {
            Packet packet = packetHelper.getCommitPacket1638(userId,message);
            Result result = JSON.parseObject(ropService.execute(packet, userId, "gsmshare"), Result.class);
            log.info("短信返回："+result.getResponse().toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public int saveHistory(ActivityConfiguration gift, String userId, String channel_id, String code,String ditch) {
        ActivityUserHistory history = new ActivityUserHistory();
        history.setUserId(userId);
        history.setChannelId(channel_id);
        history.setRewardName(gift.getName());
        history.setTypeId(gift.getTypeId());
        history.setUnlocked(gift.getUnlocked());
        history.setRemark(gift.getRemark());
        history.setWinSrc(gift.getWinSrc());
        history.setCode(String.valueOf(code));
        history.setDitch(ditch);
        history.setActivityId(gift.getActivityId());
        history.setItemId(gift.getItemId());
        int status = gsmShareMapper.saveHistory(history);
        return status;
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
            String message = ropService.execute(packet, history.getUserId(), actId);
            message = ReqWorker.replaceMessage(message);
            result = JSON.parseObject(message, Result.class);
            String res = result.getResponse().getErrorInfo().getCode();
            String DoneCode = result.getResponse().getRetInfo().getString("DoneCode");
            history.setMessage(message);
               history.setCode(JSONObject.toJSONString(packet));
            if (Constant.SUCCESS_CODE.equals(res)) {
                transact_result = true;
                gsmShareMapper.updateUnlocked(history.getUserId());
                history.setStatus(Constant.STATUS_RECEIVED);
                object.put(Constant.MSG, Constant.SUCCESS);
            } else {
                transact_result = false;
                history.setStatus(Constant.STATUS_RECEIVED_ERROR);
                object.put(Constant.MSG, Constant.FAILURE);
            }
            object.put("res", res);
            object.put("DoneCode", DoneCode);
      /*      object.put("res", "0000");
            object.put("DoneCode", "11111");
            transact_result = true;
            object.put(Constant.MSG, Constant.SUCCESS);
            history.setStatus(Constant.STATUS_RECEIVED);
            gsmShareMapper.updateUnlocked(history.getUserId());*/
            object.put("update_history", JSON.toJSONString(history));
            gsmShareMapper.updateHistory(history);
            if (transact_result) {
                //业务办理成功 接口上报
                Packet new_packet = packetHelper.orderReporting(config,packet,wtAcId,wtAc);
                System.out.println(new_packet.toString());
                String result_String="";
                try {
                    result_String =ropService.executes(new_packet, history.getUserId(),history.getActId());
                }catch (Exception e){
                    result_String="ERROR";
                }
                ActivityOrder order = new ActivityOrder();
                order.setName(commonMapper.selectActivityByActId(config.getActId()).getName());
                String packetThirdTradeId= packet.getPost().getPubInfo().getTransactionId();
                order.setThirdTradeId(packetThirdTradeId);
                order.setOrderItemId("JYRZ"+packetThirdTradeId.substring(packetThirdTradeId.length()-21));
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
     * 概率
     *
     * @param giftList
     * @return
     */
    public static ActivityConfiguration randomGsmGift(List<ActivityConfiguration> giftList) {
        double randomNum = RandomUtils.nextDouble();
        log.info("随机数=" + randomNum);
        double startRate = 0;
        double endRate = 0;
        for (int i = 0; i < giftList.size(); i++) {
            log.info("rate=" + giftList.get(i).getWtEvent());
            startRate = endRate;
            log.info("startRate=" + startRate);
            endRate += Double.valueOf(giftList.get(i).getWtEvent());
            log.info("endRate=" + endRate);
            if (randomNum >= startRate && randomNum < endRate) {
                return giftList.get(i);
            }
        }
        return null;
    }
}
