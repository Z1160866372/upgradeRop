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
import com.richeninfo.entity.mapper.mapper.master.PlentifulMapper;
import com.richeninfo.pojo.Constant;
import com.richeninfo.pojo.Packet;
import com.richeninfo.pojo.Result;
import com.richeninfo.pojo.VasOfferInfo;
import com.richeninfo.service.CommonService;
import com.richeninfo.service.PlentifulService;
import com.richeninfo.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

/**
 * @auth sunxiaolei
 * @date 2024/5/7 16:55
 */
@Service
@Slf4j
public class PlentifulServiceImpl  implements PlentifulService {

   @Resource
   private CommonMapper commonMapper;
   @Resource
   private PlentifulMapper plentifulMapper;
   @Resource
   private PacketHelper packetHelper;
   @Resource
   private RopServiceManager ropService;
   @Resource
   private JmsMessagingTemplate jmsMessagingTemplate;
   @Resource
   private CommonService commonService;
   @Resource
   private RSAUtils rsaUtils;
   @Resource
   private HttpServletRequest request;
   SimpleDateFormat month = new SimpleDateFormat("yyyy-MM");
   SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
   SimpleDateFormat day = new SimpleDateFormat("yyyy-MM-dd");


   /**
    * 初始化用户
    *
    * @param user
    * @return
    */
   public ActivityUser insertUser(ActivityUser user) {
      ActivityUser select_user = plentifulMapper.selectUserByCreateDate(user.getUserId());
      if (select_user == null) {
         ActivityUser new_user = new ActivityUser();
         new_user.setSecToken(user.getSecToken());
         new_user.setUserId(user.getUserId());
         new_user.setActId(user.getActId());
         new_user.setChannelId(user.getChannelId());
         new_user.setCreateDate(day.format(new Date()));
         new_user.setDitch(user.getDitch());
         List<ActivityRoster> selectRoster = plentifulMapper.selectRoster(user.getUserId(), "feedback", "wt_feedback_roster");
         if (!CollectionUtils.isEmpty(selectRoster)) {
            new_user.setMark(1);
            new_user.setPlayNum(0);
         }else{
            new_user.setMark(0);
            new_user.setPlayNum(3);
         }
         new_user.setUserType(1);
         plentifulMapper.insertUser(new_user);
         user = new_user;
      } else {
         select_user.setSecToken(user.getSecToken());
         user = select_user;
      }
      user.setUserId(Base64.getEncoder().encodeToString(user.getUserId().getBytes()));
      return user;
   }

   @Override
   public JSONObject  getConfiguration() throws Exception {
      JSONObject object = new JSONObject();
      List<ActivityUserHistory> historyList = plentifulMapper.selectActivityHistoryList();
      if(historyList.size()>0){
         for (ActivityUserHistory activityUserHistory:historyList) {
            activityUserHistory.setUserId(activityUserHistory.getUserId().substring(0, 3) + "****" + activityUserHistory.getUserId().substring(7));
         }
      }
      object.put(Constant.ObjectList,historyList);
      return object;
   }

   @Override
   public JSONObject submit(String secToken, String actId, String channelId,String ditch) throws Exception {
      JSONObject object = new JSONObject();
      String mobile="";
      if(commonService.verityTime(actId).equals("underway")) {
         if (!StringUtils.isEmpty(secToken)) {
            try {
               mobile = commonService.getMobile(secToken, channelId);
               if (mobile == null || mobile.isEmpty()) {
                  object.put(Constant.MSG, "login");
                  return object;
               }
               if( !commonService.checkUserIsChinaMobile(mobile,actId)){
                  object.put(Constant.MSG,"noShYd");
                  return object;
               }
            } catch (Exception e) {
               object.put(Constant.MSG, "loginError");
               return object;
            }
         } else {
            object.put(Constant.MSG, "login");
            return object;
         }
         ActivityUser select_user = plentifulMapper.selectUserByCreateDate(mobile);
         ActivityUserHistory userHistory =null;
         int unlocked=0;
         int partNum=1;
         List<ActivityConfiguration>  activityConfigurationList=null;
         if(select_user.getPlayNum()>0&&select_user.getMark()==0){
               if(select_user.getPlayNum()==1){
                  partNum=3;
               }
               if(select_user.getPlayNum()==2){
                  partNum=2;
               }
               if(select_user.getPlayNum()==3){
                  partNum=1;
               }
            userHistory= plentifulMapper.selectActivityUserHistoryByUserType(mobile,partNum);
            if (userHistory == null) {
               ActivityConfiguration config = null;
               List<ActivityRoster> selectRoster = plentifulMapper.selectRoster(mobile, "feedback", "wt_feedback_"+select_user.getUserType()+"_"+partNum+"_roster");
               if (!CollectionUtils.isEmpty(selectRoster)) {
                  unlocked=selectRoster.get(0).getUserType();
               }else{
                  if(select_user.getUserType()==1){
                     if(select_user.getPlayNum()==1){
                        unlocked=2;
                        partNum=3;
                     }
                     if(select_user.getPlayNum()==2){
                        unlocked=3;
                        partNum=2;
                     }
                     if(select_user.getPlayNum()==3){
                        unlocked=3;
                        partNum=1;
                     }
                  }
               }
               activityConfigurationList =plentifulMapper.selectActivityConfigurationList(actId, unlocked, select_user.getUserType(),partNum);
               if(activityConfigurationList.size()>1){
                  config = CommonUtil.randomGift(activityConfigurationList);
               }else{
                  config=plentifulMapper.selectActivityConfigurationList(actId, unlocked, select_user.getUserType(),partNum).get(0);
               }
               saveHistory(actId, channelId, object, mobile, config, ditch);
               plentifulMapper.updateUser(select_user);
               object.put("config", config);
               object.put(Constant.MSG, Constant.SUCCESS);
            } else {
               ActivityConfiguration config = new ActivityConfiguration();
               config.setName(userHistory.getRewardName());
               config.setTypeId(userHistory.getTypeId());
               config.setUnlocked(userHistory.getUnlocked());
               config.setWinSrc(userHistory.getWinSrc());
               config.setImgSrc(userHistory.getImgSrc());
               config.setValue(userHistory.getValue());
               config.setModule(userHistory.getModule());
               object.put("config", config);
               object.put(Constant.MSG, Constant.YLQ);
            }
         }else{
            object.put(Constant.MSG, "noNumber");
         }
         return object;
      }else {
         object.put(Constant.MSG, "ActError");
         return object;
      }
   }

   private void saveHistory(String actId, String channelId, JSONObject object, String mobile, ActivityConfiguration activityConfiguration,String ditch) {
      ActivityUserHistory newHistory = new ActivityUserHistory();
      newHistory.setUserId(mobile);
      newHistory.setChannelId(channelId);
      newHistory.setRewardName(activityConfiguration.getName());
      newHistory.setTypeId(activityConfiguration.getTypeId());
      newHistory.setUnlocked(activityConfiguration.getUnlocked());
      newHistory.setCreateDate(day.format(new Date()));
      newHistory.setCreateTime(df.format(new Date()));
      newHistory.setValue(activityConfiguration.getValue());
      newHistory.setActId(actId);
      newHistory.setDitch(ditch);
      newHistory.setUserType(activityConfiguration.getUserType());
      newHistory.setIpScanner(activityConfiguration.getNoProContent());
      newHistory.setActivityId(activityConfiguration.getActivityId());
      newHistory.setItemId(activityConfiguration.getItemId());
      newHistory.setImgSrc(activityConfiguration.getImgSrc());
      newHistory.setWinSrc(activityConfiguration.getWinSrc());
      newHistory.setRemark(activityConfiguration.getRemark());
      newHistory.setModule(activityConfiguration.getModule());
      newHistory.setIp(activityConfiguration.getNoProContent());
      newHistory.setIpScanner(activityConfiguration.getProContent());
      plentifulMapper.insertActivityUserHistory(newHistory);
      if(activityConfiguration.getTypeId()==0){
         String mqMsg = commonService.issueReward(newHistory);
         log.info("4147请求信息：" + mqMsg);
         jmsMessagingTemplate.convertAndSend("commonQueue",mqMsg);
      }
    /*  if(activityConfiguration.getTypeId()==4){
         String mqMsg = commonService.issueCoupon(newHistory);
         log.info("0I1000请求信息：" + mqMsg);
         jmsMessagingTemplate.convertAndSend("commonQueue",mqMsg);
      }*/
        /*object.put("gift",activityConfiguration);
        object.put(Constant.MSG,Constant.SUCCESS);*/
   }

   public JSONObject transact3066Business(ActivityUserHistory history,ActivityConfiguration config,String randCode,String channelId,String wtAcId, String wtAc,String ditch) {
      JSONObject object = new JSONObject();
      boolean transact_result = false;
      Result result = new Result();
      try {
         List<VasOfferInfo> offerList = new ArrayList<VasOfferInfo>();
         if(config.getActivityId().contains(",")){
            for (int i = 0; i < config.getActivityId().split(",").length; i++) {
               VasOfferInfo vasOfferInfo = new VasOfferInfo();
               vasOfferInfo.setOfferId(config.getActivityId().split(",")[i]);
               vasOfferInfo.setEffectiveType("0");
               vasOfferInfo.setOperType("0");
               offerList.add(vasOfferInfo);
            }
         }else{
            VasOfferInfo vasOfferInfo = new VasOfferInfo();
            vasOfferInfo.setOfferId(config.getActivityId());
            vasOfferInfo.setEffectiveType("0");
            vasOfferInfo.setOperType("0");
            offerList.add(vasOfferInfo);
         }
         Packet packet = packetHelper.getCommitPacket306602(history.getUserId(),randCode, offerList, channelId,ditch);
         String message = ropService.execute(packet,history.getUserId(),history.getActId());
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
         }
         history.setMessage(JSON.toJSONString(result));
         history.setCode(JSON.toJSONString(packet));
         object.put("res", res);
         object.put("DoneCode", DoneCode);
           /* if(true){
                object.put("res", "0000");
                object.put("DoneCode", "9999");
                history.setStatus(Constant.STATUS_RECEIVED);
                object.put(Constant.MSG, Constant.SUCCESS);
                transact_result=true;
            }*/
         object.put("update_history", JSON.toJSONString(history));
         plentifulMapper.updateHistory(history);
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
    * 我的奖励
    * @param channelId
    * @param actId
    * @return
    */
   @Override
   public JSONObject getMyReward(String secToken,String channelId, String actId) {
      JSONObject object = new JSONObject();
      String mobile="";
      if (!StringUtils.isEmpty(secToken)) {
         try {
            mobile= commonService.getMobile(secToken,channelId);
         }catch (Exception e){
            object.put(Constant.MSG,"loginError");
         }
      }
      List<ActivityUserHistory> historyList = plentifulMapper.selectActivityUserHistoryList(mobile,actId);
      object.put(Constant.ObjectList,historyList);
      return object;
   }

   @Override
   public JSONObject transaction(String secToken, String actId, int unlocked, String channelId,String wtAcId, String wtAc,String randCode,String ditch) throws Exception {
      JSONObject object = new JSONObject();
      String mobile="";
      ActivityConfiguration config =null;
      if (!StringUtils.isEmpty(secToken)) {
         mobile= commonService.getMobile(secToken,channelId);
      }
      if(!commonService.checkUserIsChinaMobile(mobile,actId)){//非上海移动
         object.put(Constant.MSG,"noShYd");
         return object;
      }
      ActivityUserHistory userHistory  =plentifulMapper.selectActivityUserHistoryByUnlocked(mobile,unlocked);
      if(userHistory!=null){
         if(userHistory.getTypeId()==1){
            if(userHistory.getStatus()==3){//已办理
               object.put(Constant.MSG,"ybl");
            }else{
               config = commonMapper.selectActivitySomeConfigurationByTYpeId(actId,unlocked);
               object = transact3066Business(userHistory,config,randCode,channelId,wtAcId,wtAc,ditch);
               if(object.getString("transact_result")=="true"){
                  config = plentifulMapper.selectActivityConfigurationByModule(actId,unlocked,10);
                  if(config!=null){
                     saveHistory( actId,  channelId,  object,  mobile, config, ditch);
                  }
               }
            }
         }else{//不能办理
            object.put(Constant.MSG,"noTransaction");
         }
      }else{//没有历史数据
         object.put(Constant.MSG,"noData");
      }
      return object;
   }
}