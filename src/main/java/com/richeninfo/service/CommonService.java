/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 */
package com.richeninfo.service;

import com.alibaba.fastjson.JSONObject;
import com.richeninfo.entity.mapper.entity.*;
import com.richeninfo.pojo.Packet;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * @Author : zhouxiaohu
 * @create 2022/9/19 15:09
 */
@Service("commonService")
public interface CommonService {

    /**
     * 短信发送
     *
     * @param userId
     * @return
     */
    JSONObject sendMsgCode(String userId,String actId);

    /**
     * 短信校验
     *
     * @param userId
     * @return
     */
    boolean valSendMsgCode(String userId, String smsCode);

    /**
     * 判断用户是否  中国移动用户
     *
     * @param userId
     * @return
     */
    boolean checkUserIsChinaMobile(String userId,String actId);

    /**
     * 判断用户是否是 wap20卡用户
     *
     * @param mobile
     * @return 返回true 代表是 否则不是 如果是 则用户无法进行游戏
     */
    boolean isWap20User(String mobile,String actId);

    /**
     * 判断用户是否有测试权限
     *
     * @param mobile
     * @return
     */
    boolean isTestWhite(String mobile);

    /**
     * 查验活动状态
     *
     * @param actId
     * @return
     */
    String verityTime(String actId);

    /**
     * 活动公共内容校验
     *
     * @param actId
     * @param isTestWhite
     * @return
     */
    JSONObject verityActive(String secToken,String actId, boolean isTestWhite, String channelId);

    /**
     * 二次短信下发
     * @param userId
     * @param actId
     * @param unlocked
     * @return
     */
    JSONObject sendSms5956(String userId,String actId, int unlocked);

    /**
     * 4147礼包奖励发放
     *
     * @param history
     * @return
     */
    String issueReward(ActivityUserHistory history);

    /**
     * 卡券奖励发放
     *
     * @param history
     * @return
     */
    String issueCoupon(ActivityUserHistory history);

    /**
     * 根据渠道和secToken获取手机号
     *
     * @param secToken
     * @param channelId
     * @return
     */
    String getMobile(String secToken, String channelId);


    /**
     * 保存用户操作记录
     * @param operationLog
     */
    JSONObject insertOperationLog(@ModelAttribute OperationLog operationLog);


    /**
     * 验证OA
     * @param secToken
     * @param channelId
     * @return
     */
    JSONObject verityOa(String secToken, String channelId);

    /**
     * 保存用户分享记录
     * @param activityShare
     */
    JSONObject insertActivityShare(@ModelAttribute ActivityShare activityShare);

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
    List<ActivityConfiguration> getConfiguration(String secToken, String actId, String channelId) throws Exception;

    /**
     * 用户点击领取
     *
     * @param secToken
     * @param actId
     * @param unlocked
     * @return
     * @throws Exception
     */
    JSONObject submit(String secToken, String actId, int unlocked, String channelId,String ditch,int grade) throws Exception;

    /**
     * 我的奖励｜排行榜
     * @param channelId
     * @param actId
     * @return
     */
    JSONObject getMyReward(String secToken,String channelId,String actId, int unlocked);

    /**
     * 集团卡券发放
     * @param batchID
     * @param actId
     * @param loginNo
     * @return
     * @throws Exception
     */
    String jtGetCommitPacket1000(String batchID,String actId, String loginNo) throws Exception;

}
