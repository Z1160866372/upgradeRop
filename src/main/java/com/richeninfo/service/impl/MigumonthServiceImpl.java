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
import com.richeninfo.entity.mapper.mapper.master.MigumonthMapper;
import com.richeninfo.service.CommonService;
import com.richeninfo.service.MigumonthService;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @auth sunxiaolei
 * @date 2024/4/25 13:21
 */
@Log4j
@Service
public class MigumonthServiceImpl implements MigumonthService {


    private DateFormat df = new SimpleDateFormat("yyyy-MM");

    String newtime = df.format(new Date());
    @Resource
    MigumonthMapper migumonthMapper;
    @Resource
    CommonService commonService;

    @Resource
    CommonMapper commonMapper;

    @Resource
    private JmsMessagingTemplate jmsMessagingTemplate;

    @Override
    public JSONObject initializeUser(String userId, String secToken, String channelId, String actId, String ditch) {
        JSONObject jsonObject = new JSONObject();
        ActivityUser activityUser = migumonthMapper.findCurMonthUserInfo(userId, newtime);
        if (activityUser == null) {
            activityUser=new ActivityUser();
            activityUser.setUserId(userId);
            activityUser.setAward(0);
            activityUser.setDitch(ditch);
            migumonthMapper.saveUser(activityUser);
        }
            activityUser.setSecToken(secToken);
            jsonObject.put("user", activityUser);
            return jsonObject;
    }

    @Override
    public JSONObject getActGift(String userId, String secToken, String channelId, String actId,String ditch) {
        JSONObject jsonObject = new JSONObject();
        ActivityUser user = migumonthMapper.findCurMonthUserInfo(userId, newtime);
        if (user != null && commonService.verityTime(actId).equals("underway") && user.getAward() < 1) {
            //查询是否领取过当月的礼包
            ActivityUserHistory history = migumonthMapper.findCurMonthHistory(userId, newtime);
            if (history == null) {
                //查询活动配置礼包
                ActivityConfiguration gift = migumonthMapper.findGiftByUnlocked(0, actId);
                history=new ActivityUserHistory();
                history.setUserId(userId);
                history.setRewardName(gift.getName());
                history.setUnlocked(gift.getUnlocked());
                history.setActId(actId);
                history.setTypeId(gift.getTypeId());
                history.setChannelId(channelId);
                history.setDitch(ditch);
                history.setActivityId(gift.getActivityId());
                history.setItemId(gift.getItemId());
                int status = migumonthMapper.saveHistory(history);
                try {
                    if (status > 0) {//异步mq发放礼包
                        migumonthMapper.updateUserAward(userId);
                        jsonObject.put("msg", "success");
                        String mqMsg = commonService.issueReward(history);
                        log.info("4147请求信息：" + mqMsg);
                        jmsMessagingTemplate.convertAndSend("commonQueue",mqMsg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            jsonObject.put("msg", "error");
        }
        return jsonObject;
    }

    @Override
    public JSONObject selectVideoList(String secToken, String channelId, String actId) {
        JSONObject jsonObject = new JSONObject();
        List<ActivityConfiguration>  list=migumonthMapper.findGiftByTypeId(actId);
        jsonObject.put("list",list);
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
            commonMapper.insertOperationLog(record,"wt_migumonth_operationLog");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
