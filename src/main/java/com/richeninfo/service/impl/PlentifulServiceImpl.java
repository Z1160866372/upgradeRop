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
import com.richeninfo.entity.mapper.mapper.master.PlentifulMapper;
import com.richeninfo.service.CommonService;
import com.richeninfo.service.PlentifulService;
import com.richeninfo.util.CommonUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @auth sunxiaolei
 * @date 2024/5/7 16:55
 */
@Service
public class PlentifulServiceImpl  implements PlentifulService {

   @Resource
   PlentifulMapper plentifulMapper;
   @Resource
   CommonService commonService;
   @Resource
   CommonUtil commonUtil;
   @Override
   public JSONObject initializeUser(String userId, String secToken, String channelId, String actId) {
      JSONObject jsonObject = new JSONObject();
      ActivityUser activityUser = plentifulMapper.findUserInfo(userId);
      if (activityUser == null) {
         activityUser = new ActivityUser();
         activityUser.setUserId(userId);
         activityUser.setPlayNum(2);
         activityUser.setAward(0);
         plentifulMapper.saveUser(activityUser);
      } else {
         activityUser.setSecToken(secToken);
      }
      jsonObject.put("user", activityUser);
      return jsonObject;
   }

   @Override
   public JSONObject getActGift(String userId, String secToken, String channelId, String actId) {
      JSONObject jsonObject = new JSONObject();
      ActivityUser user = plentifulMapper.findUserInfo(userId);
      if (user != null && commonService.verityTime(actId).equals("underway") && user.getAward() < 2&&user.getPlayNum()>0) {
         ActivityUserHistory history = new ActivityUserHistory();
            //查询活动配置礼包
            List<ActivityConfiguration> giftList = plentifulMapper.findGiftList(actId);
            ActivityConfiguration gift=  commonUtil.randomGift(giftList);
            int  lostNum=plentifulMapper.LostUserPlayNum(user.getUserId());
            if(lostNum>0){
               history = new ActivityUserHistory();
               history.setUserId(userId);
               history.setRewardName(gift.getName());
               history.setUnlocked(gift.getUnlocked());
               history.setActId(actId);
               history.setTypeId(gift.getTypeId());
               history.setChannelId(channelId);
               int status = plentifulMapper.saveHistory(history);
               try {
                  if (status > 0) {//异步mq发放礼包
                     plentifulMapper.updateUserAward(userId);
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
}