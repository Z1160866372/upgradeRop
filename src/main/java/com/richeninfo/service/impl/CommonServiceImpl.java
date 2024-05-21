package com.richeninfo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.richeninfo.entity.mapper.entity.*;
import com.richeninfo.entity.mapper.mapper.master.CommonMapper;
import com.richeninfo.pojo.*;
import com.richeninfo.service.CommonService;
import com.richeninfo.service.UniapiTokenValidateService;
import com.richeninfo.util.RopServiceManager;
import com.richeninfo.util.*;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLDecoder;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @Author : zhouxiaohu
 * @create 2022/11/17 13:22
 */
@Slf4j
@Service("commonService")
public class CommonServiceImpl implements CommonService {

    @Resource
    private CommonMapper commonMapper;
    @Resource
    private PacketHelper packetHelper;
    @Resource
    private RopServiceManager ropService;
    @Resource
    private UniapiTokenValidateService uniapiTokenValidateService;
    @Resource
    private CommonUtil commonUtil;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private RSAUtils rsaUtils;
    @Resource
    private HttpServletRequest request;

    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    DateFormat month = new SimpleDateFormat("yyyy-MM");
    DateFormat day = new SimpleDateFormat("yyyy-MM-dd");
    Date current_time = new Date();


    /**
     * 二次短信下发
     *
     * @param actId
     * @param unlocked
     * @return
     */
    @Override
    public JSONObject sendSms5956(String userId, String actId, int unlocked) {
        JSONObject resultObj = new JSONObject();
        Object tmpCode = redisUtil.get(userId);
        if (!Objects.isNull(tmpCode)) {
            resultObj.put(Constant.MSG, Constant.SEND_MSG_SEPARATION_NOT_ENOUGH);
            return resultObj;
        }
        ActivityConfiguration configuration = commonMapper.selectActivitySomeConfiguration(actId, unlocked);
        redisUtil.set(userId, configuration.getActivityId(), 60);
        try {
            if (configuration != null) {
                OpenapiLog log = new OpenapiLog();
                log.setAddress(request.getLocalAddr() + ":" + request.getLocalPort());
                log.setUserId(userId);
                log.setActId(actId);
                log.setApiCode("CRM5956");
                int smsLog = commonMapper.insertSMSLog(log);
                if (smsLog > 0) {
                    Packet packet = packetHelper.getCommitPacket5956(userId, configuration.getActivityId());
                    Result result = JSON.parseObject(ropService.execute(packet, userId, actId), Result.class);
                    String code = result.getResponse().getErrorInfo().getCode();
                    String bizCode = result.getResponse().getRetInfo().getString("bizCode");
                    if (Constant.SUCCESS_CODE.equals(code) && Constant.SUCCESS_CODE.equals(bizCode)) {
                        resultObj.put(Constant.MSG, Constant.SUCCESS);
                    } else {
                        resultObj.put(Constant.MSG, Constant.ERROR);
                        resultObj.put(Constant.CODE, code);
                        return resultObj;
                    }
                } else {
                    resultObj.put(Constant.MSG, Constant.ERROR);
                    return resultObj;
                }
                //resultObj.put(Constant.MSG, Constant.SUCCESS);
            } else {
                resultObj.put(Constant.MSG, "noConfig");
            }
        } catch (Exception e) {
            log.info("Exception message = {}", e);
            resultObj.put(Constant.MSG, Constant.FAILURE);
        }
        return resultObj;
    }

    /**
     * 短信发送
     *
     * @param userId
     * @return
     */
    @Override
    public JSONObject sendMsgCode(String userId, String actId) {
        JSONObject resultObj = new JSONObject();
        String content = Constant.USER_SEND_MSG_TEXT;
        Object tmpCode = redisUtil.get(userId);
        if (!Objects.isNull(tmpCode)) {
            resultObj.put(Constant.MSG, Constant.SEND_MSG_SEPARATION_NOT_ENOUGH);
            return resultObj;
        }
        String smsCode = commonUtil.generateSmsCode();
        log.info("start sendMsg ==== {},content = {}", smsCode, content.replace("${code}", smsCode));
        content = content.replace("${code}", smsCode);
        redisUtil.set(userId, smsCode, 60);
        try {
            OpenapiLog log = new OpenapiLog();
            log.setAddress(request.getLocalAddr() + ":" + request.getLocalPort());
            log.setUserId(userId);
            log.setActId(actId);
            log.setApiCode("CRM1638");
            int smsLog = commonMapper.insertSMSLog(log);
            if (smsLog > 0) {
                Packet packet = packetHelper.getCommitPacket1638(userId, content);
                Result result = JSON.parseObject(ropService.execute(packet, userId, actId), Result.class);
                String code = result.getResponse().getErrorInfo().getCode();
                if (Constant.SUCCESS_CODE.equals(code)) {
                    resultObj.put(Constant.MSG, Constant.SUCCESS);
                } else {
                    resultObj.put(Constant.MSG, Constant.ERROR);
                    resultObj.put(Constant.CODE, code);
                    return resultObj;
                }
                resultObj.put(Constant.MSG, Constant.SUCCESS);
            } else {
                resultObj.put(Constant.MSG, Constant.ERROR);
                return resultObj;
            }

        } catch (Exception e) {
            log.info("Exception message = {}", e);
            resultObj.put(Constant.MSG, Constant.FAILURE);
        }
        return resultObj;
    }

    /**
     * 短信校验
     *
     * @param userId
     * @return
     */
    @Override
    public boolean valSendMsgCode(String userId, String smsCode) {
        Object tmpCode = redisUtil.get(userId);
        if (!Objects.isNull(tmpCode)) {
            return true;
        }
        return false;
    }

    /**
     * 判断用户是否  中国移动用户
     *
     * @param userId
     * @return
     */
    @Override
    public boolean checkUserIsChinaMobile(String userId, String actId) {
        Packet packet = packetHelper.getCommitPacket2329(userId);
        try {
            Result result = JSON.parseObject(ropService.execute(packet, userId, actId), Result.class);
            // 1是中国移动  0不是
            Integer flag = Integer.parseInt(result.getResponse().getRetInfo().getString("flag"));
            if (flag == 1) {
                return true;
            }
            //return true;
        } catch (Exception e) {
            log.error("验证失败：===" + e.getMessage());
        }
        return false;
    }

    /**
     * 判断用户是否是 wap20卡用户
     *
     * @param mobile
     * @return 返回true 代表是 否则不是 如果是 则用户无法进行游戏
     */
    @Override
    public boolean isWap20User(String mobile, String actId) {
        boolean isWap20User = false;
        ActivityRoll wap = commonMapper.selectRoll(mobile, Constant.wap_figure);
        try {
            if (wap != null) {
                Packet packet = packetHelper.getCommitPacket0808(mobile);
                Result result = JSON.parseObject(ropService.execute(packet, mobile, actId), Result.class);
                Integer res = result.getResponse().getRetInfo().getInteger("nFlag");
                if (res == 1) {
                    log.info("接口调用结果 = {}", res);
                    isWap20User = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isWap20User;
    }

    /**
     * 判断用户是否有测试权限
     *
     * @param mobile
     * @return
     */
    @Override
    public boolean isTestWhite(String mobile) {
        return commonMapper.selectRoll(mobile, Constant.white_figure) == null ? false : true;
    }

    /**
     * 查验活动状态
     *
     * @param actId
     * @return
     */
    @Override
    public String verityTime(String actId) {
        String msg = "";
        try {
            ActivityList activity = commonMapper.selectActivityByActId(actId);
            if (activity != null) {
                Date nowTime = df.parse(df.format(current_time));
                //log.info("当前时间"+nowTime.getTime());
                Date startTime = df.parse(activity.getStartTime());
                //log.info("开始时间"+startTime.getTime());
                Date endTime = df.parse(activity.getEndTime());
                // log.info("结束时间"+endTime.getTime());
                if (nowTime.getTime() < startTime.getTime()) {//活动还未开始
                    msg = "NotStarted";
                } else {
                    if (nowTime.getTime() <= endTime.getTime()) {//活动进行中
                        msg = "underway";
                    } else {//活动已结束
                        msg = "over";
                    }
                }
            } else {//无此活动
                msg = "NoActive";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msg;
    }

    /**
     * 活动公共内容校验
     *
     * @param actId
     * @param isTestWhite
     * @return
     */
    @Override
    public JSONObject verityActive(String secToken, String actId, boolean isTestWhite, String channelId) {
        JSONObject object = new JSONObject();
        try {
            if (verityTime(actId).equals("underway")) {
                ActivityList activity = commonMapper.selectActivityByActId(actId);
                object.put(Constant.RulesText, activity.getRulesText());
                if (activity.getIsWhiteList() == 3) {
                    object.put(Constant.MSG, verityTime(actId));
                    return object;
                }
                if (StringUtils.isEmpty(secToken)) {
                    object.put(Constant.MSG, "login");
                } else {
                    String mobilePhone ="";
                    if(actId.equals("finance")){
                        mobilePhone = rsaUtils.decryptByPriKey(secToken).trim();
                    }else{
                        mobilePhone = getMobile(secToken, channelId);
                    }
                    if (isTestWhite) {
                        if (!isTestWhite(mobilePhone)) {
                            object.put(Constant.MSG, "noTestWhite");
                            return object;
                        }
                    }
                    if (isWap20User(mobilePhone, actId)) {
                        object.put(Constant.MSG, "isWap20");
                        return object;
                    }
                    object.put(Constant.MSG, "success");
                }
            } else {
                object.put(Constant.MSG, verityTime(actId));
            }
        } catch (Exception e) {
            e.printStackTrace();
            object.put(Constant.MSG, "noError");
        }
        return object;
    }

    /**
     * 4147礼包奖励发放
     *
     * @param config
     * @param history
     * @return
     */
    @Override
    public String issueReward(ActivityConfiguration config, ActivityUserHistory history) {
        String mqMsg = "";
        PacketMq mq = new PacketMq();
        String out_order_id = commonUtil.getRandomCode(14, 0);
        Packet packet = packetHelper.getCommitPacket4147(history.getUserId(), config.getActivityId(), config.getItemId(), out_order_id);
        mq.setHistory(history);
        mq.setPacket(packet);
        mqMsg = JSON.toJSONString(mq);
        return mqMsg;
    }

    /**
     * 根据渠道和secToken获取手机号
     *
     * @param secToken
     * @param channelId
     * @return
     */
    @Override
    public String getMobile(String secToken, String channelId) {
        String mobile = "";
        try {
            log.info("接收内容：" + secToken);
            if (!StringUtils.isEmpty(secToken)) {
                if (channelId.equals("h5") || channelId.equals("weiting") || channelId.equals("xcx") || channelId.equals("shydhn")) {//中国移动上海｜微信渠道||小程序
                    String key = commonMapper.selectTheDayKey().getSecretKey();
                    String source[] = Des3SSL.decodeDC(secToken, key);
                    mobile = source[0];
                } else if (channelId.equals("leadeon")) {//中国移动APP
                    String UID = URLDecoder.decode(secToken, "utf-8");
                    // String UID =secToken;
                    log.info("解码后：" + UID);
                    String TransactionId = "12003201205221624261113765372600";
                    WsMessage msg = RopServiceManager.sendPush(TransactionId, UID);
                    mobile = msg.getUserId();
                } else if (channelId.equals("leadeonyp")) {//中国移动云盘
                    JSONObject object = uniapiTokenValidateService.getPhoneByToken(secToken);
                    boolean success = object.getBooleanValue(Constant.SUCCESS);
                    if (success) {
                        mobile = object.getString("mobile");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mobile;
    }


    /**
     * 保存用户操作记录
     *
     * @param operationLog
     */
    @Override
    public JSONObject insertOperationLog(OperationLog operationLog) {
        JSONObject object = new JSONObject();
        try {
            if (!StringUtils.isEmpty(operationLog.getSecToken())) {
                if(operationLog.getActId().equals("finance")){
                    operationLog.setUserId(rsaUtils.decryptByPriKey(operationLog.getSecToken()).trim());
                }else{
                    operationLog.setUserId(getMobile(operationLog.getSecToken(), operationLog.getChannelId()));
                }
                operationLog.setAddress(IPUtil.getRealRequestIp(request));
                operationLog.setName(commonMapper.selectActivityByActId(operationLog.getActId()).getName());
                String keyword = "wt_" + operationLog.getActId() + "_operationLog";
                commonMapper.insertOperationLog(operationLog, keyword);
                //object.put("operationLog", operationLog);
                object.put(Constant.MSG, Constant.SUCCESS);
            } else {
                object.put(Constant.MSG, "noMobile");
            }
        } catch (Exception exception) {
            object.put(Constant.MSG, "error");
        }
        return object;
    }

    @Override
    public JSONObject verityOa(String secToken, String channelId) {
        JSONObject object = new JSONObject();
        if (StringUtils.isEmpty(secToken)) {
            object.put(Constant.MSG, "login");
        } else {
            String mobilePhone = getMobile(secToken, channelId);
            ActivityRoll activityRoll = commonMapper.selectRoll(mobilePhone, 3);
            if (activityRoll != null) {
                object.put(Constant.MSG, "isOa");
            } else {
                object.put(Constant.MSG, "noOa");
            }
        }
        return object;
    }

    @Override
    public JSONObject insertActivityShare(ActivityShare activityShare) {
        JSONObject object = new JSONObject();
        try {
            if (!StringUtils.isEmpty(activityShare.getSecToken())) {
                activityShare.setUserId(getMobile(activityShare.getSecToken(), activityShare.getChannelId()));
                String keyword = "wt_" + activityShare.getActId() + "_share";
                commonMapper.insertActivityShare(activityShare, keyword);
                //object.put("activityShare", activityShare);
                object.put(Constant.MSG, Constant.SUCCESS);
            } else {
                object.put(Constant.MSG, "noMobile");
            }
        } catch (Exception exception) {
            object.put(Constant.MSG, "error");
            exception.printStackTrace();
        }
        return object;
    }

}
