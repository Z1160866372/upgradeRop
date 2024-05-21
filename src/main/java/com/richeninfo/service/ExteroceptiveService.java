/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 *
 */
package com.richeninfo.service;
import com.alibaba.fastjson.JSONObject;

/**
 * @auth sunxialei
 * @date 2024/3/22 15:43
 */
public interface ExteroceptiveService {

    /**
     * 初始化用户信息
     * @param mobile
     * @param secToken
     * @param channelId
     * @param actId
     * @return
     */
    JSONObject initializeUser(String mobile, String secToken, String channelId, String actId,String ditch);

    /**
     * 去答题
     * @param channelId
     * @param userId
     * @return
     */
    JSONObject toAnswer(String channelId, String userId);

    /**
     * 完成评测
     * @param channelId
     * @param answer
     * @param answerTitle
     * @param userId
     * @return
     */
    JSONObject answer(String channelId, String answer, String answerTitle, String userId);

    /**
     * 完成吹泡泡
     * @param channelId
     * @param paopao
     * @param userId
     * @return
     */
    JSONObject play(String channelId, String paopao, String userId);

    /**
     * 去抽奖
     * @param channelId
     * @param userId
     * @return
     */
    JSONObject tochoujiang(String channelId, String userId);

    /**
     * 完成抽奖
     * @param channelId
     * @param userId
     * @param actId
     * @return
     */
    JSONObject choujiang(String channelId, String userId, String actId,String ditch);

    /**
     * 经验值明细
     * @param channelId
     * @param userId
     * @return
     */
    JSONObject todetail(String channelId, String userId);

    /**
     * 记录用户操作
     * @param caozuo
     * @param actId
     * @param userId
     */
    void changeStatus(String caozuo,String actId, String userId);

    /**
     * 我的奖励
     * @param channelId
     * @param userId
     * @param actId
     * @return
     */
    JSONObject myReceived(String channelId, String userId, String actId);


}
