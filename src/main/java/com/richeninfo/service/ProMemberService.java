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
     * 获取奖励列表
     *
     * @param secToken
     * @param actId
     * @return
     */
    List<ActivityConfiguration> getConfiguration(String secToken, String actId,String channelId);

    /**
     * 用户点击领取
     *
     * @param secToken
     * @param actId
     * @param unlocked
     * @param session
     * @return
     * @throws Exception
     */
    JSONObject submit(String secToken, String actId, int unlocked, HttpSession session, String channelId);

}
