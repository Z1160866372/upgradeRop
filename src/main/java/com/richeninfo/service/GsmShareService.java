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
 * @auth sunxiaolei
 * @date 2024/4/26 11:12
 */
public interface GsmShareService {
    /**
     * 初始化用户信息
     * @param userId
     * @param secToken
     * @param channelId
     * @param actId
     * @return
     */
    JSONObject initializeUser(String userId, String secToken, String channelId, String actId,String ditch);

    /**
     * 领取礼包
     * @param userId
     * @param secToken
     * @param channelId
     * @param actId
     * @return
     */
    JSONObject getActGift(String userId, String secToken, String channelId, String actId,String ditch);

    /**
     * 办理业务短信下发
     * @param userId
     * @param secToken
     * @param channelId
     * @param actId
     * @return
     */
    JSONObject sendMessage5956(String userId, String secToken, String channelId, String actId);


    /**
     *
     * @param userId
     * @param secToken
     * @param channelId
     * @param actId
     * @param randCode
     * @return
     */
    JSONObject transact(String userId, String secToken, String channelId, String actId,String randCode);

}
