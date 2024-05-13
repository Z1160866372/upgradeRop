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
import com.richeninfo.entity.mapper.mapper.master.FortuneMapper;
import com.richeninfo.service.CommonService;
import com.richeninfo.service.FortuneService;
import com.richeninfo.util.CommonUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @auth sunxiaolei
 * @date 2024/5/7 16:55
 */
@Service
public class FortuneServiceImpl implements FortuneService {

   @Resource
   FortuneMapper FortuneMapper;
   @Resource
   CommonService commonService;
   @Resource
   CommonUtil commonUtil;
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
         if (!secToken.equals(activityUseruser.getSecToken())) {
            FortuneMapper.updateUserSecToken(userId, secToken);
         }
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
}