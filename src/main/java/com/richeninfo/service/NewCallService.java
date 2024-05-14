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
import com.richeninfo.entity.mapper.entity.ActivityUser;
import com.richeninfo.entity.mapper.entity.OperationLog;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

/**
 * @Author : zhouxiaohu
 * @create 2024/4/29 14:54
 */
public interface NewCallService {


    /**
     * 初始化用户
     *
     * @param user
     * @return
     */
    ActivityUser insertUser(@ModelAttribute ActivityUser user);

    /**
     * 获取奖励列表
     *
     * @param secToken
     * @param actId
     * @return
     */
    List<ActivityConfiguration> getConfiguration(String secToken, String actId, String channelId);

    /**
     * 用户点击确认办理
     *
     * @param secToken
     * @param actId
     * @param unlocked
     * @return
     * @throws Exception
     */
    JSONObject submit(String secToken, String actId, int unlocked, String channelId,String wtAcId, String wtAc,String randCode) throws Exception;

}
