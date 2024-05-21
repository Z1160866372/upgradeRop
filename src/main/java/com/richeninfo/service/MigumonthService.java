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
 * @date 2024/4/25 10:27
 */
public interface MigumonthService {

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
    JSONObject getActGift(String mobile, String secToken, String channelId, String actId,String ditch);

    /**
     * 多媒体展示
     * @param secToken
     * @param channelId
     * @param actId
     * @return
     */
    JSONObject selectVideoList( String secToken, String channelId, String actId);

    void actRecord(String caozuo,String actId, String userId);


}
