/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 */
package com.richeninfo.service;

import com.alibaba.fastjson.JSONObject;
import com.richeninfo.pojo.Constant;
import com.richeninfo.util.DateUtil;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.richeninfo.util.AES;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * @Author : zhouxiaohu
 * @create 2022/11/18 13:06
 */
@Component
@Slf4j
public class UniapiTokenValidateService {
    /*   static String tokenValidateUrl = "http://120.197.235.102/api/uniTokenValidate/";*/
    static String tokenValidateUrl = "https://token.cmpassport.com:8300/uniapi/uniTokenValidate";

    public JSONObject getPhoneByToken(String token) {
        JSONObject jsonObject = new JSONObject();
        String createSign = UniapiTokenValidateService.createSign("5", Constant.APP_ID, "1", Constant.APP_KEY, new Date().getTime() + "", DateUtil.convertDateToString(new Date(), Constant.YYYYMMDDHH24MMSSSSS), token, "1.0", "");
        String response = null;
        try {
            response = postMethod(createSign);
            log.info("response==========>{}" + response);
            jsonObject.put(Constant.MSG, Constant.SUCCESS);
            jsonObject.put("success", false);
            JSONObject resultJson = JSONObject.parseObject(response);
            if (resultJson.containsKey("header") && resultJson.getJSONObject("header").getString("resultcode").equals("103000")) {
                try {
                    String mobile = AES.deCodeAES(resultJson.getJSONObject("body").getString("msisdn"), Constant.APP_KEY);
                    // jsonObject.put(UserPubConstant.DATA, mobile);
                    jsonObject.put("mobile", mobile);
                    jsonObject.put("success", true);
                    log.info("mobile=====>{}" + mobile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                jsonObject.put(Constant.CODE, resultJson.getJSONObject("header").getString("resultcode"));
            }
        } catch (Exception e) {
            // e.printStackTrace();
            jsonObject.put(Constant.MSG, Constant.FAILURE);
        } finally {
            return jsonObject;
        }
    }


    public JSONObject UniapiTokenValidateMethod(String appid, String appKey, String idType, String token, String userInformation, HttpSession session) {
        JSONObject jsonObject = new JSONObject();
        if (userInformation.length() == 0 && idType == "1") {
            jsonObject.put(Constant.MSG, Constant.ERROR);
            return jsonObject;
        }
        //生成新的请求报文
        String createSign = UniapiTokenValidateService.createSign("5", appid, idType, appKey, new Date().getTime() + "", DateUtil.convertDateToString(new Date(), Constant.YYYYMMDDHH24MMSSSSS), token, "1.0", userInformation);
        String response = null;
        try {
            response = postMethod(createSign);
            jsonObject.put(Constant.MSG, Constant.SUCCESS);
            JSONObject resultJson = JSONObject.parseObject(response);
            if (resultJson.containsKey("header") && resultJson.getJSONObject("header").getString("resultcode").equals("103000")) {
                try {
                    log.info("msisdn=========" + resultJson.getJSONObject("body").getString("msisdn"));
                    String mobile = AES.deCodeAES(resultJson.getJSONObject("body").getString("msisdn"), Constant.APP_KEY);
                    log.info(resultJson.getJSONObject("body").getString("msisdnmask"));

                    log.info("mobile=========" + mobile);
                    jsonObject.put(Constant.KEY_MOBILE, resultJson.getJSONObject("body").getString("msisdnmask"));
                    session.setAttribute("userId", mobile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                jsonObject.put(Constant.CODE, resultJson.getJSONObject("header").getString("resultcode"));
            }
        } catch (Exception e) {
            // e.printStackTrace();
            jsonObject.put(Constant.MSG, Constant.FAILURE);
        } finally {
            return jsonObject;
        }
    }

    /**
     * 将传入的参数自动变成报文
     *
     * @param apptype,id,idtype,key,msgid,systemtime,token,version
     * @return requestJson
     * @Title: 请求排序key不为null
     * @author:wei
     */
    private static JSONObject requestSort(String apptype, String id, String idtype, String key, String msgid, String systemtime, String token, String version, String userInformation) {
        JSONObject requestJson = new JSONObject();
        JSONObject header = new JSONObject();
        JSONObject body = new JSONObject();
        body.put("token", token);
        body.put("userInformation", userInformation);
        header.put("systemtime", systemtime);
        header.put("version", version);
        header.put("id", id);
        header.put("apptype", apptype);
        header.put("idtype", idtype);
        header.put("msgid", msgid);

        header.put("key", key);
        header.put("expandparams", "");
        requestJson.put("header", header);
        requestJson.put("body", body);
        return requestJson;
    }

    /**
     * 将传入的参数自动变成报文
     *
     * @param apptype,id,idtype,msgid,systemtime,token,version
     * @return JSONObject
     * @Title: 请求排序key为null
     * @author:wei
     */
    private static JSONObject requestSort(String apptype, String id, String idtype, String msgid, String systemtime, String token, String version) {
        JSONObject requestJson = new JSONObject();
        JSONObject header = new JSONObject();
        JSONObject body = new JSONObject();
        body.put("token", token);
        header.put("msgid", msgid);
        header.put("systemtime", systemtime);
        header.put("version", version);
        header.put("id", id);
        header.put("apptype", apptype);
        header.put("idtype", idtype);
        requestJson.put("header", header);
        requestJson.put("body", body);
        return requestJson;
    }

    /**
     * 发送post请求；
     *
     * @return String
     * @Title: 发送post请求
     * @author:wei
     */
    private String postMethod(String resultJson) {
        String resp1 = "";
        try {
            HttpClient client = new HttpClient();
            PostMethod postMethod1 = new PostMethod(tokenValidateUrl);
            RequestEntity entity = new StringRequestEntity(resultJson, null, "utf-8");
            postMethod1.setRequestEntity(entity);
            client.executeMethod(postMethod1);
            resp1 = postMethod1.getResponseBodyAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resp1;
    }

    /**
     * 将传入的参数自动变成报文
     *
     * @param apptype,id,idtype,key,msgid,systemtime,token,version
     * @return String
     * @Title: 创建签名
     * @author:wei
     */
    public static String createSign(String apptype, String id, String idtype, String key, String msgid, String systemtime, String token, String version, String userInformation) {
        JSONObject requestSort = requestSort(apptype, id, idtype, key, msgid, systemtime, token, version, userInformation);
        String reauestJson = createSign(requestSort.toJSONString());
        System.out.println(reauestJson);
        return reauestJson;
    }

    /**
     * 将传入的参数自动变成报文
     *
     * @param apptype,id,idtype,msgid,systemtime,token,version
     * @return
     * @Title: 创建签名 key 为null
     * @author:wei
     */
    public static String createSign(String apptype, String id, String idtype, String msgid, String systemtime, String token, String version) {

        JSONObject requestSort = requestSort(apptype, id, idtype, msgid, systemtime, token, version);
        String reauestJson = createSign(requestSort.toJSONString());
        return reauestJson;
    }

    /**
     * 将传入的参数自动变成报文
     *
     * @param JsonString
     * @return
     * @Title: 创建签名
     * @author:
     */
    public static String createSign(String JsonString) {
        JSONObject json = JSONObject.parseObject(JsonString);
        String sign = "";

        if (json == null) {
            throw new IllegalArgumentException("请求数据不能为空");
        }
        JSONObject respheader = json.getJSONObject("header");
        JSONObject respbody = json.getJSONObject("body");
        if (respheader == null) {
            throw new IllegalArgumentException("请header不能为空");
        }
        if (respbody == null) {
            throw new IllegalArgumentException("请body不能为空");
        }
        String apptype = (String) respheader.get("apptype");
        String id = (String) respheader.get("id");
        String idtype = (String) respheader.get("idtype");
        String key = (String) respheader.get("key");
        String msgid = (String) respheader.get("msgid");
        String systemtime = (String) respheader.get("systemtime");
        String version = (String) respheader.get("version");
        String token = (String) respbody.get("token");
        if (StringUtils.isBlank(apptype)) {
            throw new IllegalArgumentException("apptype不能为空");
        }
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("id不能为空");
        }
        if (StringUtils.isBlank(idtype)) {
            throw new IllegalArgumentException("idtype不能为空");
        }
        if (StringUtils.isBlank(msgid)) {
            throw new IllegalArgumentException("msgid不能为空");
        }
        if (StringUtils.isBlank(systemtime)) {
            throw new IllegalArgumentException("systemtime不能为空");
        }
        if (StringUtils.isBlank(version)) {
            throw new IllegalArgumentException("version不能为空");
        }
        if (StringUtils.isBlank(token)) {
            throw new IllegalArgumentException("token不能为空");
        }
        Map<String, Object> map = new TreeMap<String, Object>();
        map.put("version", version);
        map.put("id", id);
        map.put("idtype", idtype);
        map.put("msgid", msgid);
        map.put("token", token);
        map.put("systemtime", systemtime);
        map.put("apptype", apptype);
        if (StringUtils.isBlank(key)) {
            String enStr = mapToString(map);
            sign = md5(enStr);
        } else {
            map.put("key", key);
            String enStr = mapToString(map);
            sign = md5(enStr);
        }
        json.getJSONObject("header").put("sign", sign);
        json.getJSONObject("header").remove("key");
        return json.toJSONString();
    }

    /**
     * 将Map转换为String
     *
     * @param map
     * @return
     * @Title: map2String
     * @author: yanhuajian 2013-7-21下午7:25:08
     */
    private static String mapToString(Map<String, Object> map) {
        if (null == map || map.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            sb.append(entry.getValue());
        }

        return sb.toString();
    }

    private static String md5(String text) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(text.getBytes("UTF-8"));
            byte[] b = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
