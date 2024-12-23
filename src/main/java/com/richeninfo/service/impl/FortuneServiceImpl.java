/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 *
 */

package com.richeninfo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.richeninfo.entity.mapper.entity.*;
import com.richeninfo.entity.mapper.mapper.master.CommonMapper;
import com.richeninfo.entity.mapper.mapper.master.FortuneMapper;
import com.richeninfo.pojo.Constant;
import com.richeninfo.service.CommonService;
import com.richeninfo.service.FortuneService;
import com.richeninfo.util.CommonUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Base64;
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
    private JmsMessagingTemplate jmsMessagingTemplate;
    @Resource
    CommonMapper commonMapper;

    @Override
    public JSONObject initializeUser(String userId, String secToken, String channelId, String actId, String ditch) {
        JSONObject jsonObject = new JSONObject();
        ActivityUser activityUseruser = FortuneMapper.findUserInfo(userId);
        if (activityUseruser == null) {
            activityUseruser = new ActivityUser();
            activityUseruser.setUserId(userId);
            activityUseruser.setPlayNum(1);
            activityUseruser.setAward(0);
            activityUseruser.setDitch(ditch);
            List<ActivityRoster> activityRosterList = FortuneMapper.findListRosterByUserId(userId);
            if (CollectionUtils.isEmpty(activityRosterList)) {
                activityUseruser.setUserType(0);
                activityUseruser.setPlayNum(0);
            } else {
                activityUseruser.setPlayNum(1);
                activityUseruser.setUserType(1);
            }
            activityUseruser.setSecToken(secToken);
            activityUseruser.setChannelId(channelId);
            FortuneMapper.saveUser(activityUseruser);
        } else {
            activityUseruser.setSecToken(secToken);
        }
        activityUseruser.setUserId(Base64.getEncoder().encodeToString(activityUseruser.getUserId().getBytes()));
        jsonObject.put("user", activityUseruser);
        return jsonObject;
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
            log.info("rate=" + giftList.get(i).getProb());
            startRate = endRate;
            log.info("startRate=" + startRate);
            endRate += Double.valueOf(giftList.get(i).getProb());
            log.info("endRate=" + endRate);
            if (randomNum >= startRate && randomNum < endRate) {
                return giftList.get(i);
            }
        }
        return null;
    }

    @Override
    public JSONObject draw(String userId, String secToken, String channelId, String actId, String ditch) {
        JSONObject jsonObject = new JSONObject();
        ActivityUser user = FortuneMapper.findUserInfo(userId);
        if (!commonService.checkUserIsChinaMobile(userId, actId)) {
            jsonObject.put(Constant.MSG, "noShYd");
            return jsonObject;
        }
        if (user != null && commonService.verityTime(actId).equals("underway") && user.getAward() < 1 && user.getPlayNum() > 0 && user.getUserType() == 1) {
            ActivityUserHistory history = new ActivityUserHistory();
            int lostNum = FortuneMapper.LostUserPlayNum(user.getUserId());
            if (lostNum > 0) {
                //查询活动配置礼包
                List<ActivityConfiguration> giftList = FortuneMapper.findGiftList(actId);
                ActivityConfiguration gift = randomGsmGift(giftList);
                int loseGiftNum = 0;
                if (gift.getModule() == 1) {
                    loseGiftNum = FortuneMapper.loseGiftNum(gift.getId());
                } else {
                    loseGiftNum = 1;
                }
                if (loseGiftNum < 1) {
                    gift = FortuneMapper.findGiftByUnlocked(3, "purchase");
                }
                history = new ActivityUserHistory();
                history.setUserId(userId);
                history.setRewardName(gift.getName());
                history.setUnlocked(gift.getUnlocked());
                history.setActId(actId);
                history.setTypeId(gift.getTypeId());
                history.setDitch(ditch);
                history.setChannelId(channelId);
                history.setActivityId(gift.getActivityId());
                history.setItemId(gift.getItemId());
                int status = FortuneMapper.saveHistory(history);
                try {
                    log.info("status=======" + status);
                    if (status > 0) {//异步mq发放礼包
                        jsonObject.put("msg", "success");
                        jsonObject.put("gift",gift);
                        FortuneMapper.updateUserAward(userId);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    jsonObject.put("msg", "error");
                }
            } else {
                jsonObject.put("msg", "noNum");
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
    public void actRecord(String caozuo, String actId, String userId) {
        log.info("userId:" + userId + ",caozuo" + caozuo);
        Map<String, Object> map = new HashMap<>();
        OperationLog record = new OperationLog();
        record.setInstructions(caozuo);
        record.setUserId(userId);
        record.setActId(actId);
        record.setUserId(userId);
        try {
            commonMapper.insertOperationLog(record, "wt_miguflow_operationLog");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public JSONObject userReward(String mobile, String channelId, String actId) {
        List<ActivityUserHistory> activityUserHistoryList=FortuneMapper.findHistoryListByUserId(mobile);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userReward",activityUserHistoryList);
        return jsonObject;
    }
}