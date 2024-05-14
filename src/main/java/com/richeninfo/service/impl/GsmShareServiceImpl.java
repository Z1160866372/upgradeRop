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
import lombok.extern.log4j.Log4j;
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
    CommonUtil commonUtil;

    @Override
    public JSONObject initializeUser(String userId, String secToken, String channelId, String actId) {
        JSONObject jsonObject = new JSONObject();
        ActivityUser activityUser = gsmShareMapper.findUserInfo(userId);
        if (activityUser == null) {
            activityUser=new ActivityUser();
            activityUser.setUserId(userId);
            activityUser.setAward(0);
            activityUser.setPlayNum(1);
            activityUser.setUserType(userType(userId));
            gsmShareMapper.saveUser(activityUser);
        }else{
            activityUser.setSecToken(secToken);
        }
        jsonObject.put("user", activityUser);
        return jsonObject;
    }



    @Override
    public JSONObject getActGift(String userId, String secToken, String channelId, String actId) {
        JSONObject jsonObject = new JSONObject();
        ActivityUser user = gsmShareMapper.findUserInfo(userId);
        if (user != null && commonService.verityTime(actId).equals("underway") && user.getAward() < 1) {
            String code="";
                    ActivityConfiguration gift=new ActivityConfiguration();
                    int lostPlayNum=gsmShareMapper.lostPlayNum(userId);
                    if(lostPlayNum>0){
                        if(user.getUserType()==0){//名单外用户 谢谢惠顾
                            gift=gsmShareMapper.findGiftByUnlocked(7,actId);
                        }else{
                            List<ActivityConfiguration> giftList=gsmShareMapper.findGiftListByActId(actId);
                              gift=  commonUtil.randomGift(giftList);
                            if(gift.getTypeId()!=7){//抽中了 卡券或者业务
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
                                        if(gift.getTypeId()!=6&&bindlist.size()>0) {
                                            //查询是否还有剩余门票
                                            int updateBindUserId=gsmShareMapper.updateBindUserId(userId, bindlist.get(0).getId());
                                            if(updateBindUserId<1){
                                                gift=gsmShareMapper.findGiftByUnlocked(7,actId);
                                            }else {
                                                if(gift.getTypeId()<6) {//需要发送短信
                                                    code=bindlist.get(0).getCouponCode();
                                                    gsmShareMapper.updateUserNickName(code,userId);
                                                    String msg=gift.getRemark();
                                                    String message= msg.replace("code",code);
                                                    log.info("获奖短信内容==============="+message);
                                                    //sendNote(userId,message);
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
                        gsmShareMapper.updateCurMark(userId,String.valueOf(gift.getTypeId()));
                        int status=saveHistory(gift, userId, channelId,code);
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
    public JSONObject transact(String userId, String secToken, String channelId, String actId,String randCode) {
        JSONObject jsonObject = new JSONObject();
        ActivityUser user = gsmShareMapper.findUserInfo(userId);
        if (user != null && commonService.verityTime(actId).equals("underway") && user.getAward() < 1) {
            //查询是否领取过当月的礼包
            ActivityUserHistory history = gsmShareMapper.findCurYwHistory(userId);
            if (history == null) {
                //查询活动配置礼包
                ActivityConfiguration gift = gsmShareMapper.findGiftByUnlocked(0, actId);
                history=new ActivityUserHistory();
                history.setUserId(userId);
                history.setRewardName(gift.getName());
                history.setUnlocked(gift.getUnlocked());
                history.setActId(actId);
                history.setKeyword(actId);
                history.setTypeId(gift.getTypeId());
                history.setChannelId(channelId);
                int status = gsmShareMapper.saveHistory(history);

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

    public void sendNote(String userId,String message){
        log.info("短信内容："+message);
      /*  Packet packet = packetHelper.getKQPacketds(userId,message);
        Result result =packetHelper.execute(packet, false);
        log.info("短信返回："+result.getResponse().toString());*/

    }

    public int saveHistory(ActivityConfiguration gift, String userId, String channel_id, String code) {
        ActivityUserHistory history = new ActivityUserHistory();
        history.setUserId(userId);
        history.setChannelId(channel_id);
        history.setRewardName(gift.getName());
        history.setTypeId(gift.getTypeId());
        history.setUnlocked(gift.getUnlocked());
        history.setRemark(gift.getRemark());
        history.setWinSrc(gift.getWinSrc());
        history.setCode(String.valueOf(code));
        int status = gsmShareMapper.saveHistory(history);
        try {
            if (status > 0 && Double.valueOf(gift.getValue()) > 0) {
                String mqMsg = commonService.issueReward(gift, history);
                log.info("4147请求信息：" + mqMsg);
                // jmsMessagingTemplate.convertAndSend("proemMQ", mqMsg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return status;
    }


    public JSONObject transact3066Business(ActivityUserHistory history, ActivityConfiguration config, String randCode, String channelId, String wtAcId, String wtAc) {
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
            Packet packet = packetHelper.getCommitPacket306602(history.getUserId(), randCode, offerList, channelId);
           /* String message = ropService.execute(packet,history.getUserId());
            message = ReqWorker.replaceMessage(message);
            result = JSON.parseObject(message,Result.class);
            String res = result.getResponse().getErrorInfo().getCode();
            String DoneCode = result.getResponse().getRetInfo().getString("DoneCode");
            if(Constant.SUCCESS_CODE.equals(res)){
                transact_result = true;
                history.setStatus(Constant.STATUS_RECEIVED);
                object.put(Constant.MSG, Constant.SUCCESS);
            }else{
                transact_result = false;
                history.setStatus(Constant.STATUS_RECEIVED_ERROR);
                object.put(Constant.MSG, Constant.FAILURE);
            }*/
            if (true) {
                gsmShareMapper.updateUserAward(history.getUserId());
                transact_result = false;
                history.setStatus(Constant.STATUS_RECEIVED_ERROR);
                object.put(Constant.MSG, Constant.FAILURE);
            }
            history.setMessage("");
            history.setCode(JSON.toJSONString(packet));
            object.put("res", "0000");
            object.put("DoneCode", "12343242343A");
            object.put("update_history", JSON.toJSONString(history));
           // gsmShareMapper.updateHistory(history);
            //Packet new_packet = packetHelper.orderReporting(config,packet,wtAcId,wtAc);
            // System.out.println(new_packet.toString());
            /*  String result_String =ropService.execute(new_packet, history.getUserId());*/
           /* ActivityOrder order = new ActivityOrder();
            order.setName(commonMapper.selectActivityByActId(config.getActId()).getName());
            String packetThirdTradeId= packet.getPost().getPubInfo().getTransactionId();
            order.setThirdTradeId(packetThirdTradeId);
            order.setOrderItemId("JYRZ"+packetThirdTradeId.substring(packetThirdTradeId.length()-21));
            order.setBossId(config.getActivityId());
            order.setCommodityName(config.getName());
            order.setUserId(history.getUserId());
            order.setCode(JSONArray.fromObject(new_packet).toString());
            *//* order.setMessage(result_String);*//*
            order.setChannelId(channelId);
            commonMapper.insertActivityOrder(order);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        object.put("transact_result", transact_result);
        return object;
    }
}
