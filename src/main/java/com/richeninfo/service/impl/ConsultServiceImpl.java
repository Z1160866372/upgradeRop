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
import com.richeninfo.entity.mapper.mapper.master.ConsultMapper;
import com.richeninfo.entity.mapper.mapper.master.PlentifulMapper;
import com.richeninfo.service.CommonService;
import com.richeninfo.service.ConsultService;
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
public class ConsultServiceImpl implements ConsultService {

   @Resource
    ConsultMapper cnsultMapper;
   @Resource
   CommonService commonService;
   @Resource
   CommonUtil commonUtil;
   @Override
   public JSONObject initializeUser(String userId, String secToken, String channelId, String actId) {
      JSONObject jsonObject = new JSONObject();
      ActivityUser activityUserUser =cnsultMapper.findUserInfo(userId);
      if (activityUserUser == null) {
         activityUserUser = new ActivityUser();
         activityUserUser.setUserId(userId);
         activityUserUser.setPlayNum(1);
         activityUserUser.setAward(0);
         activityUserUser.setSecToken(secToken);
         cnsultMapper.saveUser(activityUserUser);
      } else {
         activityUserUser.setSecToken(secToken);
      }
      jsonObject.put("user", activityUserUser);
      return jsonObject;
   }

   @Override
   public JSONObject getActGift(String userId, String secToken, String channelId, String actId,String answer) {
      JSONObject jsonObject = new JSONObject();
      ActivityUser user =cnsultMapper.findUserInfo(userId);
      if (user != null && commonService.verityTime(actId).equals("underway") && user.getAward() < 1) {
         int a = cnsultMapper.updateUserAnswer(userId,answer);
         String status= a>0 ? "success":"error";
         jsonObject.put("msg", status);
         cnsultMapper.updateUserAward(userId);
      } else {
         jsonObject.put("msg", "error");
      }
      return jsonObject;
   }
}