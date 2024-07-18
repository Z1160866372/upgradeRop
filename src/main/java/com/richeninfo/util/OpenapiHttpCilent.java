/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 */
package com.richeninfo.util;

import com.alibaba.fastjson.JSON;
import com.chinamobile.cn.openapi.sdk.v2.manage.DefalutSecurity;
import com.chinamobile.cn.openapi.sdk.v2.manage.SecurityI;
import com.chinamobile.cn.openapi.sdk.v2.model.OpenapiResponse;
import com.chinamobile.cn.openapi.sdk.v2.model.ResponseStatus;
import com.chinamobile.cn.openapi.sdk.v2.util.ConfSerializeUtil;
import com.chinamobile.cn.openapi.sdk.v2.util.JsonUtil;
import com.richeninfo.entity.mapper.entity.OpenapiLog;
import com.richeninfo.entity.mapper.mapper.master.CommonMapper;
import com.richeninfo.pojo.Packet;
import com.richeninfo.pojo.Post;
import lombok.extern.slf4j.Slf4j;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * @Author : zhouxiaohu
 * @create 2023/1/3 10:02
 */
@Slf4j
@Component
public class OpenapiHttpCilent {
    @Value("${nengKai.appCode}")
    private String appCode;
    @Value("${nengKai.securityUrl}")
    private String securityUrl;
    @Value("${nengKai.openapiUrl}")
    private String openapiUrl;
    @Value("${nengKai.apk_new}")
    private String apk_new;
    private CloseableHttpClient client = HttpClients.createDefault();
    private SecurityI securiytManager;

    @Resource
    private CommonMapper commonMapper;


    /**
     * @param appCode 应用编码
     * @param apk 应用主密钥，在开发者门户中应用管理菜单里获取
     *//*
    public  OpenapiHttpCilent(String appCode, String apk) {
        this.appCode = appCode;
        init();
        securiytManager = new DefalutSecurity(securityUrl, appCode, apk);
    }*/

    /**
     * @param apiCode       能力编码
     * @param transactionId 业务编码
     * @param requestBody   请求
     * @param accessToken   访问令牌
     * @return
     * @throws Exception
     */
    public String call(String apiCode, String transactionId, String requestBody, String accessToken) throws Exception {
        log.info("call----requestBody"+requestBody);
        String userId="";
        if(apiCode.equals("exchange")){
            userId=JSONObject.parseObject(requestBody).getString("phone");
        }else{
            Post post = JSON.parseObject(requestBody, Post.class);
            String busiCode=post.getRequest().getBusiCode();
            if(busiCode.equals("PT-SH-FS-OI4147")){
                userId=post.getRequest().getBusiParams().getString("bill_id");
            }else if(busiCode.equals("PT-SH-FS-OI3066")){
                userId=post.getRequest().getBusiParams().getString("billid");
            }else if(busiCode.equals("PT-SH-FS-OI5956")||busiCode.equals("PT-SH-FS-OI002329")){
                userId=post.getRequest().getBusiParams().getString("billId");
            }else if(busiCode.equals("PT-SH-FS-OI0808")){
                userId=post.getRequest().getBusiParams().getString("strBillId");
            }else if(busiCode.equals("getCommitPacket1638")){
                userId=post.getRequest().getBusiParams().getString("DestNum");
            }
        }
        log.info("call----userId"+userId);
        log.info("securityUrl--"+securityUrl);
        log.info("appCode--"+appCode);
        log.info("apk_new--"+apk_new);
        securiytManager = new DefalutSecurity(securityUrl, appCode, apk_new);
        // 1.对报文签名
        String publiceKey = securiytManager.getAsk().getPublicKeyStr();
        Long aedkId = securiytManager.getAedk().getId();
        requestBody = securiytManager.encrypt(requestBody);
        String signValue = securiytManager.sign(requestBody);
        HttpPost httpPost = null;
        CloseableHttpResponse response = null;
        String tmp = null;
        OpenapiLog openapiLog = new OpenapiLog();
        try {
            httpPost = new HttpPost(openapiUrl + "/access");
            httpPost.addHeader("appCode", appCode);
            httpPost.addHeader("apiCode", apiCode);
            httpPost.addHeader("transactionId", transactionId);
            httpPost.addHeader("aedkId", aedkId + "");
            httpPost.addHeader("signValue", signValue);
            httpPost.addHeader("publicKey", publiceKey);
            httpPost.addHeader("accessToken", accessToken);
            httpPost.addHeader("sdkVersion", "sdk.version.2.2");
            httpPost.setEntity(new StringEntity(requestBody, ContentType.create("text/plain", "UTF-8")));
            log.info("call=====httopost==toString"+httpPost.toString());
            log.info("call=====httopost==httpPostToString"+httpPostToString(httpPost));
            response = client.execute(httpPost);
            InputStreamReader isr = new InputStreamReader(response.getEntity().getContent(), "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            StringBuffer sb = new StringBuffer();
            while ((tmp = br.readLine()) != null) {
                sb.append(tmp);
            }
            String responseBody = sb.toString();
            OpenapiResponse sr = JsonUtil.toBean(responseBody, OpenapiResponse.class);
            if (ResponseStatus.SUCCESS.toString().equals(sr.getStatus())) {
                String result = sr.getResult();
                log.info("call=====result=="+result);
                openapiLog.setCode(httpPostToString(httpPost));
                openapiLog.setMessage(securiytManager.decrypt(result));
                openapiLog.setUserId(userId);
                openapiLog.setMessage(result);
                if (securiytManager.verify(result, sr.getSignValue(), sr.getPublicKey())) {
                    String otext = securiytManager.decrypt(result);
                    return "{\"status\":\"SUCCESS\",\"result\":\"" + otext.replace("\"", "\\\"") + "\"}";
                } else {
                    return "{\"status\":\"ERROR\",\"errorCode\":\"060101\",\"exceptionCode\":\"sign error\"}";
                }
            } else {
                return responseBody;
            }
        }catch (Exception e){
            e.printStackTrace();
            openapiLog.setCode(e.getMessage());
            openapiLog.setApiCode(apiCode);
            commonMapper.insertOpenapiLog(openapiLog);
            return "";
        } finally {
            if (response != null) {
                response.close();
            }
            if (httpPost != null) {
                httpPost.abort();
            }
            openapiLog.setApiCode(apiCode);
            commonMapper.insertOpenapiLog(openapiLog);
        }
    }


    public static String httpPostToString(HttpPost httppost) {
        StringBuilder sb = new StringBuilder();
        sb.append("\nRequestLine:");
        sb.append(httppost.getRequestLine().toString());
        int i = 0;
        for(Header header : httppost.getAllHeaders()){
            if(i == 0){
                sb.append("\nHeader:");
            }
            i++;
            for(HeaderElement element : header.getElements()){
                for(NameValuePair nvp :element.getParameters()){
                    sb.append(nvp.getName());
                    sb.append("=");
                    sb.append(nvp.getValue());
                    sb.append(";");
                }
            }
        }
        HttpEntity entity = httppost.getEntity();
        String content = "";
        if(entity != null){
            try {
                content = IOUtils.toString(entity.getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        sb.append("\nContent:");
        sb.append(content);
        return sb.toString();
    }
    /**
     * @param apiCode       能力编码
     * @param transactionId 业务编码
     * @param requestBody   请求
     * @return
     * @throws Exception
     */
    public String call(String apiCode, String transactionId, String requestBody) throws Exception {
        return call(apiCode, transactionId, requestBody, null);
    }

    private void init() {
        log.info("appCode:" + appCode);
        log.info("securityUrl::" + securityUrl);
        log.info("openapiUrl::" + openapiUrl);
        /*Map<String, String> properties = new ConfSerializeUtil().readConfig("openapi.property");

        this.securityUrl = properties.get("security_server_url");
        this.openapiUrl = properties.get("openapi_server_url");*/

    }
}
