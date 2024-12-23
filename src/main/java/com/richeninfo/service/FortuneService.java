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
 * @date 2024/5/7 16:54
 */
public interface FortuneService {


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
     * 领取礼包
     * @param mobile
     * @param secToken
     * @param channelId
     * @param actId
     * @return
     */
    JSONObject draw(String mobile, String secToken, String channelId, String actId,String ditch);

    void actRecord(String caozuo,String actId, String userId);

    JSONObject userReward(String mobile,  String channelId, String actId);

}
