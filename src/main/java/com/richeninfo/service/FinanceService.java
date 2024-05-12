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
import com.richeninfo.entity.mapper.entity.ActivityShare;
import com.richeninfo.entity.mapper.entity.ActivityUser;
import com.richeninfo.entity.mapper.entity.OperationLog;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @Author : zhouxiaohu
 * @create 2024/4/29 14:54
 */
public interface FinanceService {


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
     * 用户点击领取
     *
     * @param secToken
     * @param actId
     * @param unlocked
     * @return
     * @throws Exception
     */
    JSONObject submit(String secToken, String actId, int unlocked, String channelId) throws Exception;

    /**
     * 我的奖励
     * @param channelId
     * @param actId
     * @return
     */
    JSONObject getMyReward(String secToken,String channelId,String actId);

    /**
     * 保存用户操作记录
     * @param operationLog
     */
    JSONObject insertOperationLog(@ModelAttribute OperationLog operationLog);
}
