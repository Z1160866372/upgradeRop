/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 */
package com.richeninfo.service;

import com.alibaba.fastjson.JSONObject;
import com.richeninfo.entity.mapper.entity.ActivityConfiguration;
import com.richeninfo.entity.mapper.entity.ActivityUser;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * @Author : zhouxiaohu
 * @create 2022/11/15 17:16
 */
@Service("proMemberService")
public interface ProMemberService {

    /**
     * 初始化用户
     *
     * @param user
     * @return
     */
    ActivityUser insertUser(@ModelAttribute ActivityUser user);

    /**
     * 获取滚动奖励列表
     * @return
     */
    JSONObject getConfiguration() throws Exception;

    /**
     * 用户点击领取
     *
     * @param secToken
     * @param actId
     * @param unlocked
     * @return
     * @throws Exception
     */
    JSONObject submit(String secToken, String actId, String channelId,String ditch) throws Exception;

    /**
     * 我的奖励
     * @param channelId
     * @param actId
     * @return
     */
    JSONObject getMyReward(String secToken,String channelId,String actId);


    /**
     * 业务办理
     * @param secToken
     * @param actId
     * @param unlocked
     * @param channelId
     * @param ditch
     * @return
     * @throws Exception
     */
    JSONObject transaction(String secToken, String actId, int unlocked, String channelId,String wtAcId, String wtAc,String randCode,String ditch) throws Exception;


}
