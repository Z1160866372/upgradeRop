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
public interface TasteOfService {

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
     * 完成抽奖
     * @param channelId
     * @param userId
     * @param actId
     * @return
     */
    JSONObject choujiang(String channelId, String userId, String actId,String ditch);



    /**
     * 我的奖励
     * @param channelId
     * @param userId
     * @param actId
     * @return
     */
    JSONObject myReceived(String channelId, String userId, String actId);

    void changeLevel(String channelId, String secToken, String actId);


}
