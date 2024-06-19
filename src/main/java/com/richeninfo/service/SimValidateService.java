/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 *
 */

package com.richeninfo.service;/*
 * Project: eurekaclient
 * 
 * File Created at 2018年8月3日
 * 
 * Copyright 2016 CMCC Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * ZYHY Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license.
 */


import com.alibaba.fastjson.JSONObject;
import com.richeninfo.pojo.Constant;

import com.richeninfo.util.CommonUtil;
import com.richeninfo.util.DateUtil;
import com.richeninfo.util.HttpsClientNSSL;
import lombok.extern.log4j.Log4j;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.util.Date;
import java.util.UUID;


/**
 * @Type 
 * @Desc 
 * @author Administrator
 * @date 2018年8月3日 上午9:08:09
 * @version 
 */

@Service
@Log4j
public class SimValidateService {

    @Resource
    private CommonUtil commonUtil;

    private Logger logger =  LoggerFactory.getLogger(SimValidateService.class);

    /**
     * 获取accessToken
     */
    public  String getAccessToken(String phone){
        JSONObject params = new  JSONObject();
        params.put("appId", Constant.APP_ID);
        params.put("appKey", Constant.APP_KEY);
        params.put("timestamp",System.currentTimeMillis());
        params.put("reqId", UUID.randomUUID());
        String result = this.requestChinaMobile(params, Constant.GET_ACCESS_TOKEN_URL);
        try{
            JSONObject jsonObject = (JSONObject) JSONObject.parse(result);
            if(jsonObject.containsKey("accessToken")){
                commonUtil.saveJedisByExpire( Constant.ACCESS_TOKEN.replace(Constant.PHONE,phone), jsonObject.getString("accessToken"),Constant.REFRESH_TOKEN_TIME_EXPIRES);
                commonUtil.saveJedisByExpire( Constant.REFRESH_TOKEN.replace(Constant.PHONE,phone), jsonObject.getString("refreshToken"), Constant.REFRESH_TOKEN_TIME_EXPIRES);
                commonUtil.saveJedisByExpire( Constant.REFRESH_TOKEN_TIME.replace(Constant.PHONE,phone), DateUtil.convertDateToString(new Date(),Constant.YYYYMMDDHH24MMSS),Constant.REFRESH_TOKEN_TIME_EXPIRES);
                return  jsonObject.getString("accessToken");
            }
        }catch (Exception e){
            return  null;
        }
        return "";
    }

    /**
     * 刷新accessToken
     */
    public  String refreshAccessToken(String phone){
        String refreshToken = "";
        JSONObject params = new  JSONObject();
        params.put("appId", Constant.APP_ID);
        params.put("appKey", Constant.APP_KEY);
        params.put("timestamp",System.currentTimeMillis());
        params.put("reqId", UUID.randomUUID());
        params.put("refreshToken", refreshToken);
        String resultStr = this.requestChinaMobile(params,  Constant.REFRESH_ACCESS_TOKEN_URL);
        try{
            JSONObject jsonObject = (JSONObject) JSONObject.parse(resultStr);
            if(jsonObject.containsKey("accessToken")){
                commonUtil.saveJedisByExpire( Constant.REFRESH_TOKEN.replace(Constant.PHONE,phone), jsonObject.getString("refreshToken"), Constant.REFRESH_TOKEN_TIME_EXPIRES);
                commonUtil.saveJedisByExpire( Constant.ACCESS_TOKEN.replace(Constant.PHONE,phone), jsonObject.getString("accessToken"),Constant.REFRESH_TOKEN_TIME_EXPIRES);
                commonUtil.saveJedisByExpire( Constant.REFRESH_TOKEN_TIME.replace(Constant.PHONE,phone), DateUtil.convertDateToString(new Date(),Constant.YYYYMMDDHH24MMSS),Constant.REFRESH_TOKEN_TIME_EXPIRES);
                return  jsonObject.getString("accessToken");
            }
        }catch (Exception e){
            return  null;
        }
        return "";
    }

    /**
     * 获取授权
     * @param phone
     */
    public  JSONObject getAuthMsg(String phone){
        JSONObject resultJson = new JSONObject();
        String accessToken = commonUtil.getValueByJedis(Constant.ACCESS_TOKEN.replace(Constant.PHONE,phone));
        if(accessToken == null || accessToken.equals("")){
            accessToken = getAccessToken(phone);
        }
        String accessTokenTime = commonUtil.getValueByJedis(Constant.REFRESH_TOKEN_TIME.replace(Constant.PHONE,phone));
        long curr = new Date().getTime();
        try {
            long diff = (curr - DateUtil.convertStringToDate(Constant.YYYYMMDDHH24MMSS,accessTokenTime).getTime()) / 1000;
            if(diff > Constant.REFRESH_TOKEN_TIME_EXPIRES){
                accessToken = getAccessToken(phone);
            }
            if(accessToken == null || accessToken.equals("")){
                resultJson.put(Constant.MSG, Constant.ERROR);
                resultJson.put(Constant.CODE, -1); // accessToken 不能为空
                return  resultJson;
            }
            JSONObject params = new  JSONObject();
            params.put("appId", Constant.APP_ID);
            params.put("appKey", Constant.APP_KEY);
            params.put("timestamp",System.currentTimeMillis());
            params.put("reqId", UUID.randomUUID());
            params.put("accessToken", accessToken);
            params.put("phone", phone);
            params.put("templateId", Constant.TEMP_ID);
           //  params.put("templateParam", Constant.TEMP_ID);
            params.put("callBackUrl", Constant.CALL_BACK_URL);
            String result = this.requestChinaMobile(params, Constant.SEND_AUTH_URL);
            JSONObject resultJson2 = JSONObject.parseObject(result);
            /*  String resultDes=resultJson2.getString("resultDes");
            String status=resultJson2.getJSONObject("phoneSendStat").getString("state");*/
            log.info("resultJson2="+resultJson2);
            resultJson.put(Constant.MSG, Constant.SUCCESS);
            return resultJson;
        } catch (Exception e) {
            resultJson.put(Constant.CODE, -2); // accessToken 不能为空
        }
        resultJson.put(Constant.MSG, Constant.ERROR);
        return resultJson;
    }

    /**
     * 请求接口
     * @param params
     * @return
     */
    public String requestChinaMobile(JSONObject params, String url){
            return HttpsClientNSSL.doPostJSON(url, params.toJSONString());
    }


}





