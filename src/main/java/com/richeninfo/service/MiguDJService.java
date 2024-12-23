/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 *
 */

package com.richeninfo.service;

import com.alibaba.fastjson.JSONObject;
import com.richeninfo.entity.mapper.entity.ActivityConfiguration;

import java.util.List;
import java.util.Map;

/**
 * @auth sunxiaolei
 * @date 2024/4/26 11:12
 */
public interface MiguDJService {
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
     * 多媒体展示
     * @param secToken
     * @param channelId
     * @param actId
     * @return
     */
    JSONObject selectVideoList( String secToken, String channelId, String actId);

    /**
     * 领取礼包
     * @param secToken
     * @param channelId
     * @param actId
     * @return
     */
    JSONObject draw( String secToken, String channelId, String actId,String answer,int mark,String ditch);



}
