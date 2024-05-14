/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 *
 */

package com.richeninfo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.richeninfo.entity.mapper.entity.ActivityConfiguration;
import com.richeninfo.entity.mapper.entity.ActivityUser;
import com.richeninfo.entity.mapper.entity.ActivityUserHistory;
import com.richeninfo.entity.mapper.entity.OperationLog;
import com.richeninfo.entity.mapper.mapper.master.CommonMapper;
import com.richeninfo.entity.mapper.mapper.master.FortuneMapper;
import com.richeninfo.service.CommonService;
import com.richeninfo.service.FortuneService;
import com.richeninfo.util.CommonUtil;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @auth sunxiaolei
 * @date 2024/5/7 16:55
 */
@Service
@Log4j
public class FortuneServiceImpl implements FortuneService {

   @Resource
   FortuneMapper FortuneMapper;
   @Resource
   CommonService commonService;
   @Resource
   CommonUtil commonUtil;
   @Resource
   CommonMapper commonMapper;
   @Override
   public JSONObject initializeUser(String userId, String secToken, String channelId, String actId) {
      JSONObject jsonObject = new JSONObject();
      ActivityUser activityUseruser = FortuneMapper.findUserInfo(userId);
      if (activityUseruser == null) {
         activityUseruser = new ActivityUser();
         activityUseruser.setUserId(userId);
         activityUseruser.setPlayNum(1);
         activityUseruser.setAward(0);
         FortuneMapper.saveUser(activityUseruser);
      } else {
         activityUseruser.setSecToken(secToken);
      }
      jsonObject.put("user", activityUseruser);
      return jsonObject;
   }

   @Override
   public JSONObject getActGift(String userId, String secToken, String channelId, String actId) {
      JSONObject jsonObject = new JSONObject();
      ActivityUser user = FortuneMapper.findUserInfo(userId);
      if (user != null && commonService.verityTime(actId).equals("underway") && user.getAward() < 2&&user.getPlayNum()>0) {
         ActivityUserHistory history = new ActivityUserHistory();
            //查询活动配置礼包
            List<ActivityConfiguration> giftList = FortuneMapper.findGiftList(actId);
            ActivityConfiguration gift=  giftList.get(0);
            int  lostNum=FortuneMapper.LostUserPlayNum(user.getUserId());
            if(lostNum>0){
               history = new ActivityUserHistory();
               history.setUserId(userId);
               history.setRewardName(gift.getName());
               history.setUnlocked(gift.getUnlocked());
               history.setActId(actId);
               history.setTypeId(gift.getTypeId());
               history.setChannelId(channelId);
               int status = FortuneMapper.saveHistory(history);
               try {
                  if (status > 0) {//异步mq发放礼包
                     FortuneMapper.updateUserAward(userId);
                     jsonObject.put("msg", "success");
                  }
               } catch (Exception e) {
                  e.printStackTrace();
               }
            }else{
               jsonObject.put("msg", "error");
            }
      } else {
         jsonObject.put("msg", "error");
      }
      return jsonObject;
   }

   /**
    * 用户记录操作
    *
    * @param caozuo
    * @param userId
    */
   @Override
   public void actRecord(String caozuo,String actId, String userId) {
      log.info("userId:" + userId + ",caozuo" + caozuo);
      Map<String, Object> map = new HashMap<>();
      OperationLog record = new OperationLog();
      record.setInstructions(caozuo);
      record.setUserId(userId);
      record.setActId(actId);
      record.setUserId(userId);
      try {
         commonMapper.insertOperationLog(record,"wt_miguflow_operationLog");
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}