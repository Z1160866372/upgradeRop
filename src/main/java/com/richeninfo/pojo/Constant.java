/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 */
package com.richeninfo.pojo;

/**
 * @Author : zhouxiaohu
 * @create 2022/9/19 15:41
 */
public class Constant {
    //格式化时间
    public static final String YYYYMMDDHH24MMSS = "yyyyMMddHHmmss", YYYYMMDDHH24MMSSSSS = "yyyyMMddHHmmssSSS";
    //随机码 本地存储session  发送短信间隔不足
    public static final String RANDOM_CODE = "${code}", SMS_CODE_MAP = "smsCodeMap", SEND_MSG_SEPARATION_NOT_ENOUGH = "SEND_MSG_SEPARATION_NOT_ENOUGH",
            SMS_CODE_NOt_MATCHED = "SMS_CODE_NOt_MATCHED", MOBILE_ERROR = "MOBILE_ERROR", SMS_MOBIEL_OR_CODE_IS_NULL = "SMS_MOBIEL_OR_CODE_IS_NULL";
    //发送短信 间隔毫秒 数
    public static final long SEPARATION_MILLISECOND = 60000;
    public static final String SMS_RANDOM = "smsRandom", KEY_CODE = "keyCode";
    //用户手机号  解密字段
    public static final String KEY_MOBILE = "mobilePhone", OPEN_ID = "openid", KEY_SEC_TOKEN = "secToken", KEY_WECHAT_NUM = "wechatNum", KEY_WECHAT_USER = "wechatUser",
            KEY_CHANNEL_ID = "channel_id", KEY_UUID = "uuid", IS_SYNC = "belongFlag";
    //接口返回参数
    public static final String SUCCESS_CODE = "0000", SUCCESS_MSG = "SUCCESS";
    public static final int STATUS_RECEIVED = 3, STATUS_RECEIVED_ERROR = 4;
    public static final String USER_SEND_MSG_TEXT = "动态密码：${code}，30分钟内有效，请您尽快输入动态密码登录，此动态密码将在您登录后失效。【中国移动】";
    //返回json数据时状态 及key
    public static final String MSG = "msg", YZM_ERROR = "yzm_error", ERROR = "error", SUCCESS = "success", FAILURE = "failure", NO_DATA = "noDate", LOGIN = "login", YLQ = "ylq", NO_RIGHTS = "noRights", DATA = "data", CODE = "code", IS_SEND_PRIZE = "isSendPrize";
    //错误key  用户类型错误
    public static final String ERROR_CODE = "errorCode", USER_TYPE_IS_ERROR = "userTypeError", CHANNEL_CODE_STR = "channelCodeStr";
    //名单类型
    public static final int wap_figure = 0, white_figure = 1;
    /**
     * 中国移动统一免登陆
     */
    public final static String APP_ID = "300012034035";
    public final static String APP_KEY = "B3FD6015E60E11FC44AAA38F50D9BD20";
    public final static String TEMP_ID = "DF20210323152009c1e739";
    //public final static String CALL_BACK_URL = "https://rop.richeninfo.com/rop/china/mobile/sim/callback.do";
    public final static String CALL_BACK_URL = "https://rop.richeninfo.com/roptest/china/mobile/sim/callback.do";
    public final static String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCxa7/NlbmeIPLd6IHxXtmmSBjz8XEjWWq8cdAp657xCyDk3lq/CpNN/Yeogib3mE2TacQNvWHUTeBBO0CWwluAwRK1Km+sdiQdX+PI8E+efboOUAeaKVkCYkz+82HnQOuzmVHsfQUO7wq28k8CNk2zmUwbpompOTLEf+Jv7jYsqwIDAQAB";
    public final static String PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALFrv82VuZ4g8t3ogfFe2aZIGPPxcSNZarxx0CnrnvELIOTeWr8Kk039h6iCJveYTZNpxA29YdRN4EE7QJbCW4DBErUqb6x2JB1f48jwT559ug5QB5opWQJiTP7zYedA67OZUex9BQ7vCrbyTwI2TbOZTBumiak5MsR/4m/uNiyrAgMBAAECgYEAkPmq0SL3ee8ShaHoDIOk9esT+MHh4TC/txyJLLgMOIsPZfUL0e+iTqhMobU4gjlBqqNSQKfvuLup86OnM4m9mimLqaOVtZqNts34kvS/bxH4tglvAJL6RNSHDs5MzwTozqvXz/yba8IPDs9/n8MORFujPEJdFD3kLWZkd3ileAECQQDvM612IriLrr8ds0Oemk/Wo0X9wwyi/cEngGsvNP8D5tOl6nhJONTmETjueio1301t/ur+gUCf2Av/3+ZyZxhBAkEAveFizZ7m/QdfzxSZS4BcQrOeKsbWe7/fo4dXUQIC2O/6FkA8f8VNjRdk7zAE4uq7BY6zf3NOFRGy3bJjtpCp6wJASt6w/5YUj1wN49z1YFJKhRMr2CbR8L5eU9/lLKx42Z7JRlhieAC62pwvZq2EQsJ3OOOeaDTKpn14HAnX1p0gQQJAFWTHlRNKQ0gJa3OOsgsMWheSwo8Uq2y67dnZ5K6tU3P2YXtrVZoNWtA/9xMZ+dcoqz+gycNcENOlq4Tl5zP1cQJBAJ6rb7VV0904pgz1O6TdSCcuepjpJnWIFCg9THvIvLyvKGeRYVBMh8ZCPBumASUz/3omShGyP9z/XbcBUsf0zlY=";

    public final static String PHONE = "${phone}",
            ACCESS_TOKEN = "accessToken_jt${phone}",
            REFRESH_TOKEN = "refreshToken_jt${phone}",
            REFRESH_TOKEN_TIME = "accessTokenTime_jt${phone}";

    public final static Integer REFRESH_TOKEN_TIME_EXPIRES = 20;

    // 获取token url   刷新token url, 获取授权url
    public final static String GET_ACCESS_TOKEN_URL = "https://117.161.4.206:443/simmessageapi/esapi/accessToken",
            REFRESH_ACCESS_TOKEN_URL = "https://117.161.4.206:443/simmessageapi/esapi/refreshToken",
            SEND_AUTH_URL = "https://117.161.4.206:443/simmessageapi/esapi/sendAuthMsg";

}
