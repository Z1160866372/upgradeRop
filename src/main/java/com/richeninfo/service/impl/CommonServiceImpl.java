package com.richeninfo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.richeninfo.entity.mapper.entity.*;
import com.richeninfo.entity.mapper.mapper.master.CommonMapper;
import com.richeninfo.pojo.*;
import com.richeninfo.service.CommonService;
import com.richeninfo.service.UniapiTokenValidateService;
import com.richeninfo.util.RopServiceManager;
import com.richeninfo.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
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
    private HttpSession session;
    @Resource
    private RedisUtil redisUtil;

    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date current_time = new Date();

    /**
     * 短信发送
     *
     * @param userId
     * @param session
     * @return
     */
    @Override
    public JSONObject sendMsgCode(String userId, HttpSession session) {
        JSONObject resultObj = new JSONObject();
        //TODO 校验是否中国移动客户
        String content = Constant.USER_SEND_MSG_TEXT;
        Object tmpCode = redisUtil.get(userId);
        if(!Objects.isNull(tmpCode)){
            resultObj.put(Constant.MSG, Constant.SEND_MSG_SEPARATION_NOT_ENOUGH);
            return resultObj;
        }
        String smsCode = commonUtil.generateSmsCode();
        log.info("start sendMsg ==== {},content = {}", smsCode, content.replace("${code}", smsCode));
        content = content.replace("${code}", smsCode);
        redisUtil.set(userId, smsCode,60);
        try {
            Packet packet = packetHelper.getCommitPacket1638(userId, content);
            Result result = JSON.parseObject(ropService.execute(packet, userId), Result.class);
            String code = result.getResponse().getErrorInfo().getCode();
            if (Constant.SUCCESS_CODE.equals(code)) {
                resultObj.put(Constant.MSG, Constant.SUCCESS);
            } else {
                resultObj.put(Constant.MSG, Constant.ERROR);
                resultObj.put(Constant.CODE, code);
                return resultObj;
            }
            resultObj.put(Constant.MSG, Constant.SUCCESS);
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
     * @param session
     * @return
     */
    @Override
    public boolean valSendMsgCode(String userId, String smsCode, HttpSession session) {
        Object tmpCode = redisUtil.get(userId);
        if(!Objects.isNull(tmpCode)){
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
    public boolean checkUserIsChinaMobile(String userId) {
        Packet packet = packetHelper.getCommitPacket2329(userId);
        try {
           /* Result result = JSON.parseObject(ropService.execute(packet,userId), Result.class);
            // 1是中国移动  0不是
            Integer flag = Integer.parseInt(result.getResponse().getRetInfo().getString("flag"));
            if(flag == 1){
                return true;
            }*/
            return true;
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
    public boolean isWap20User(String mobile) {
        boolean isWap20User = false;
        ActivityRoll wap = commonMapper.selectRoll(mobile, Constant.wap_figure);
        try {
            if (wap != null) {
                Packet packet = packetHelper.getCommitPacket0808(mobile);
                Result result = JSON.parseObject(ropService.execute(packet, mobile), Result.class);
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
     * @param session
     * @return
     */
    @Override
    public JSONObject verityActive(String actId, boolean isTestWhite, HttpSession session, String channelId) {
        JSONObject object = new JSONObject();
        try {
            if (verityTime(actId).equals("underway")) {
                ActivityList activity = commonMapper.selectActivityByActId(actId);
                if (activity.getIsWhiteList() == 3) {
                    object.put(Constant.MSG, verityTime(actId));
                    return object;
                }
                String mobilePhone = session.getAttribute(Constant.KEY_MOBILE) == null ? "" : (String) session.getAttribute(Constant.KEY_MOBILE);
                if (mobilePhone.isEmpty()) {
                    object.put(Constant.MSG, "login");
                } else {
                    if (isTestWhite) {
                        if (!isTestWhite(mobilePhone)) {
                            object.put(Constant.MSG, "noTestWhite");
                            return object;
                        }
                    }
                    if (isWap20User(mobilePhone)) {
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
        }
        return object;
    }


    /**
     * 3066业务办理
     *
     * @param config
     * @param history
     * @param channelId
     * @return
     * @throws Exception
     */
    @Override
    public JSONObject transact3066Business(ActivityConfiguration config, ActivityUserHistory history, String channelId) {
        JSONObject object = new JSONObject();
        boolean transact_result = false;
        Result result = new Result();
        try {
            List<VasOfferInfo> offerList = new ArrayList<VasOfferInfo>();
            VasOfferInfo vasOfferInfo = new VasOfferInfo();
            vasOfferInfo.setOfferId(config.getActivityId());
            vasOfferInfo.setEffectiveType("0");
            vasOfferInfo.setOperType("0");
            offerList.add(vasOfferInfo);
            Packet packet = packetHelper.getCommitPacket3066(history.getUserId(), offerList, channelId);
            /*String message = ropService.execute(packet,history.getUserId());
            message = ReqWorker.replaceMessage(message);
            result = JSON.parseObject(message,Result.class);
            String res = result.getResponse().getErrorInfo().getCode();
            if(Constant.SUCCESS_CODE.equals(res)){
                transact_result = true;
                history.setStatus(Constant.STATUS_RECEIVED);
            }else{
                transact_result = false;
                history.setStatus(Constant.STATUS_RECEIVED_ERROR);
            }*/
            if (true) {
                transact_result = true;
                history.setStatus(Constant.STATUS_RECEIVED);
            }
            history.setMessage("message");
            history.setCode(JSON.toJSONString(packet));
            object.put("update_history", JSON.toJSONString(history));
            object.put("transact_result", transact_result);
        } catch (Exception e) {
            e.printStackTrace();
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
     * 获取事务id
     *
     * @return
     */
    @Override
    public String generateTransactionId() {
        String timestamp = DateUtil.convertDateToString(new Date(), Constant.YYYYMMDDHH24MMSSSSS);
        SecureRandom rand = new SecureRandom();
        int randNum = rand.nextInt(10000);
        String pattern = "0000";
        DecimalFormat df = new DecimalFormat(pattern);
        return "WXHJS" + timestamp + df.format(randNum);
    }

    /**
     * 获取sessionKey
     *
     * @param value
     */
    @Override
    public void saveJedisByExpire(String key, String value, int time) {
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtils.getJedis();
            jedis.set(key, value);
            jedis.expire(key, time);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JedisPoolUtils.returnRes(jedis);
        }
    }

    public String getValueByJedis(String key) {
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtils.getJedis();
            return jedis.get(key);
        } catch (Exception e) {
            return null;
        } finally {
            JedisPoolUtils.returnRes(jedis);
        }
    }

    /**
     * 隐藏手机号中间四位
     *
     * @param mobilePhone
     * @return
     */
    @Override
    public String hideMidPhone(String mobilePhone) {
        return mobilePhone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

    /**
     * 处理微信昵称表情
     *
     * @param source
     * @return
     */
    @Override
    public String filterEmoji(String source) {
        if (source == null) {
            return source;
        }
        Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
        Matcher emojiMatcher = emoji.matcher(source);
        if (emojiMatcher.find()) {
            source = emojiMatcher.replaceAll("*");
        }
        return source;
    }

    /**
     * 转参数
     *
     * @param map
     * @return
     */
    @Override
    public JSONObject multipleParmToJSON(Map<String, String[]> map) {
        JSONObject result = new JSONObject();
        for (Map.Entry<String, String[]> entry : map.entrySet()) {
            String[] array = entry.getValue();
            if (array != null && array.length > 1) {
                result.put(entry.getKey(), array);
            } else if (array != null && array.length == 1) {
                result.put(entry.getKey(), array[0]);
            }
        }
        return result;
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
            log.info("接收内容："+secToken);
            if (!secToken.isEmpty()) {
                String key = commonMapper.selectTheDayKey().getSecretKey();
                if (channelId.equals("h5") || channelId.equals("weiting") || channelId.equals("xcx")||channelId.equals("shydhn")) {//中国移动上海｜微信渠道||小程序
                    String source[] = Des3SSL.decodeDC(secToken, key);
                    mobile = source[0];
                } else if (channelId.equals("leadeon")) {//中国移动APP
                    String UID = URLDecoder.decode(secToken, "utf-8");
                    log.info("解码后："+UID);
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
                redisUtil.set(Constant.KEY_MOBILE, mobile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mobile;
    }

    /**
     * 保存用户分享记录
     *
     * @param share
     */
    @Override
    public void saveShare(ActivityShare share) {
        String secToken = (String) session.getAttribute("secToken");
        if (!secToken.isEmpty() || !share.getSecToken().isEmpty()) {
            share.setUserId(getMobile(secToken, share.getChannelId()));
            commonMapper.insertShareUser(share);
        }
    }
}
