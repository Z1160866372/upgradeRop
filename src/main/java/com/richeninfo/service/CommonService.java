/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 */
package com.richeninfo.service;

import com.alibaba.fastjson.JSONObject;
import com.richeninfo.entity.mapper.entity.*;
import com.richeninfo.pojo.Packet;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * @Author : zhouxiaohu
 * @create 2022/9/19 15:09
 */
@Service("commonService")
public interface CommonService {
    /**
     * 短信发送
     *
     * @param userId
     * @return
     */
    JSONObject sendMsgCode(String userId);

    /**
     * 短信校验
     *
     * @param userId
     * @return
     */
    boolean valSendMsgCode(String userId, String smsCode);

    /**
     * 判断用户是否  中国移动用户
     *
     * @param userId
     * @return
     */
    boolean checkUserIsChinaMobile(String userId);

    /**
     * 判断用户是否是 wap20卡用户
     *
     * @param mobile
     * @return 返回true 代表是 否则不是 如果是 则用户无法进行游戏
     */
    boolean isWap20User(String mobile);

    /**
     * 判断用户是否有测试权限
     *
     * @param mobile
     * @return
     */
    boolean isTestWhite(String mobile);

    /**
     * 查验活动状态
     *
     * @param actId
     * @return
     */
    String verityTime(String actId);

    /**
     * 活动公共内容校验
     *
     * @param actId
     * @param isTestWhite
      * @return
     */
    JSONObject verityActive(String secToken,String actId, boolean isTestWhite, String channelId);
    /**
     * 初始化用户
     *
     * @param user
     * @return
     */
    ActivityUser insertUser(@ModelAttribute ActivityUser user);

    /**
     * 二次短信下发
     * @param userId
     * @param actId
     * @param unlocked
     * @return
     */
    JSONObject sendSms5956(String userId,String actId, int unlocked);
    /**
     * 3066业务办理
     *
     * @param config
     * @param history
     * @param channelId
     * @return
     * @throws Exception
     */
    JSONObject transact3066Business(ActivityConfiguration config, ActivityUserHistory history,String randCode, String channelId);

    /**
     * 4147礼包奖励发放
     *
     * @param config
     * @param history
     * @return
     */
    String issueReward(ActivityConfiguration config, ActivityUserHistory history);

    /**
     * 根据渠道和secToken获取手机号
     *
     * @param secToken
     * @param channelId
     * @return
     */
    String getMobile(String secToken, String channelId);

    /**
     * 保存分享记录
     *
     * @param share
     */
    void insertShare(@ModelAttribute ActivityShare share);

    /**
     * 保存用户操作记录
     * @param operationLog
     */
    void insertOperationLog(@ModelAttribute OperationLog operationLog);

    /**
     * 我的奖励
     * @param channelId
     * @param actId
     * @return
     */
    JSONObject getMyReward(String secToken,String channelId,String actId);

}
