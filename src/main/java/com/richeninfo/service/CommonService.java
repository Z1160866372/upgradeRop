package com.richeninfo.service;

import com.alibaba.fastjson.JSONObject;
import com.richeninfo.entity.mapper.entity.ActivityConfiguration;
import com.richeninfo.entity.mapper.entity.ActivityShare;
import com.richeninfo.entity.mapper.entity.ActivityUserHistory;
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
     *
     * 短信发送
     * @param userId
     * @param session
     * @return
     */
    JSONObject sendMsgCode(String userId, HttpSession session);
    /**
     * 短信校验
     * @param userId
     * @param session
     * @return
     */
    boolean valSendMsgCode(String userId,String smsCode,HttpSession session);
    /**
     * 判断用户是否  中国移动用户
     * @param userId
     * @return
     */
    boolean  checkUserIsChinaMobile(String userId);
    /**
     * 判断用户是否是 wap20卡用户
     * @param mobile
     * @return 返回true 代表是 否则不是 如果是 则用户无法进行游戏
     */
    boolean isWap20User(String mobile);
    /**
     * 判断用户是否有测试权限
     * @param mobile
     * @return
     */
    boolean isTestWhite(String mobile);
    /**
     * 查验活动状态
     * @param actId
     * @return
     */
    String  verityTime(String actId);
    /**
     * 活动公共内容校验
     * @param actId
     * @param isTestWhite
     * @param session
     * @return
     */
    JSONObject verityActive(String actId,boolean isTestWhite,HttpSession session,String channelId);
    /**
     * 3066业务办理
     * @param config
     * @param history
     * @param channelId
     * @return
     * @throws Exception
     */
    JSONObject transact3066Business(ActivityConfiguration config, ActivityUserHistory history, String channelId);
    /**
     * 4147礼包奖励发放
     * @param config
     * @param history
     * @return
     */
    String issueReward(ActivityConfiguration config,ActivityUserHistory history);
    /**
     * 获取事务id
     * @return
     */
    String generateTransactionId();
    /**
     * 获取sessionKey
     * @param value
     */
    void saveJedisByExpire(String  key, String value, int time);
    /**
     * 隐藏手机号中间四位
     * @param mobilePhone
     * @return
     */
    String hideMidPhone(String mobilePhone);
    /**
     * 处理微信昵称表情
     * @param source
     * @return
     */
    String filterEmoji(String source);
    /**
     * 转参数
     * @param map
     * @return
     */
    JSONObject multipleParmToJSON(Map<String, String[]> map);

    /**
     * 根据渠道和secToken获取手机号
     * @param secToken
     * @param channelId
     * @return
     */
    String getMobile(String secToken,String channelId);

    /**
     * 保存分享记录
     * @param share
     */
    void saveShare(@ModelAttribute ActivityShare share);
}
