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
import com.richeninfo.entity.mapper.entity.ActivityUserHistory;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

/**
 * @Author : zhouxiaohu
 * @create 2024/4/29 14:54
 */
public interface FoodietService {


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
    JSONObject getActivityUserList(String secToken, String actId, String channelId,int page,int limit,int typeId,String ip);

    /**
     * 用户点击确认办理
     *
     * @param secToken
     * @param actId
     * @param unlocked
     * @return
     * @throws Exception
     */
    JSONObject submit(String secToken, String actId, int unlocked, String channelId,String code, String message,String remark,String ditch,String ipScanner) throws Exception;

}
