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
import com.richeninfo.entity.mapper.mapper.master.CommonMapper;
import com.richeninfo.entity.mapper.mapper.master.MiguXcMapper;
import com.richeninfo.service.CommonService;
import com.richeninfo.service.MiguXcService;
import com.richeninfo.util.PacketHelper;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @auth sunxiaolei
 * @date 2024/4/26 11:19
 */
@Log4j
@Service
public class MiguXcServiceImpl implements MiguXcService {

    @Resource
   private  MiguXcMapper miguXcMapper;
    @Resource
    private CommonService commonService;

    @Resource
    private CommonMapper commonMapper;
    @Resource
    private PacketHelper packetHelper;
    @Override
    public JSONObject initializeUser(String userId, String secToken, String channelId, String actId) {
        JSONObject jsonObject = new JSONObject();
        ActivityUser activityUseruser = miguXcMapper.findCurMonthUserInfo(userId);
        if (activityUseruser == null) {
            activityUseruser=new ActivityUser();
            activityUseruser.setUserId(userId);
            activityUseruser.setAward(0);
            miguXcMapper.saveUser(activityUseruser);
        }
        jsonObject.put("user", activityUseruser);
        return jsonObject;
    }

    @Override
    public JSONObject selectVideoList(String mobile, String secToken, String channelId, String actId) {
        JSONObject jsonObject = new JSONObject();
        List<ActivityConfiguration> list=miguXcMapper.findGiftByTypeId(actId);
        jsonObject.put("list",list);
        return jsonObject;
    }

    @Override
    public JSONObject getActGift(String userId, String secToken, String channelId, String actId,String randCode) {
        JSONObject jsonObject = new JSONObject();
        ActivityUser user = miguXcMapper.findCurMonthUserInfo(userId);
        if (user != null && commonService.verityTime(actId).equals("underway") && user.getAward() < 1) {
            //查询是否领取过当月的礼包
            ActivityUserHistory history = miguXcMapper.findCurYwHistory(userId);
            if (history == null) {
                //查询活动配置礼包
                ActivityConfiguration gift = miguXcMapper.findGiftByUnlocked(0, actId);
                history=new ActivityUserHistory();
                history.setUserId(userId);
                history.setRewardName(gift.getName());
                history.setUnlocked(gift.getUnlocked());
                history.setActId(actId);
                history.setKeyword(actId);
                history.setTypeId(gift.getTypeId());
                history.setChannelId(channelId);
                int status = miguXcMapper.saveHistory(history);

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
}
