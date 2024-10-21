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
import com.richeninfo.entity.mapper.entity.ActivityRecord;
import com.richeninfo.entity.mapper.entity.OpenapiLog;
import com.richeninfo.entity.mapper.mapper.master.CommonMapper;
import com.richeninfo.pojo.Packet;
import com.richeninfo.pojo.Post;
import com.richeninfo.pojo.Result;
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
import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.List;
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
    @Value("${nengKai.httpUrl}")
    private String httpUrl;
    @Value("${nengKai.apk_new}")
    private String apk_new;
    private CloseableHttpClient client = HttpClients.createDefault();
    private SecurityI securiytManager;
    @Resource
    private CommonMapper commonMapper;
    @Resource
    private PacketHelper packetHelper;
    @Resource
    private RopServiceManager ropService;

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
        }else if(apiCode.equals("addOrder")){
            String busInfo = JSON.parseObject(requestBody).getString("busInfo");
            userId=JSON.parseObject(busInfo).getString("customerId");
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

    public String  HttpsURLConnection(String apiCode, String transactionId, String requestBody)  throws Exception{
        String response = null;
        log.info("call----requestBody"+requestBody);
        String userId="";
        if(apiCode.equals("exchange")){
            userId=JSONObject.parseObject(requestBody).getString("phone");
        }else if(apiCode.equals("addOrder")){
            String busInfo = JSON.parseObject(requestBody).getString("busInfo");
            userId=JSON.parseObject(busInfo).getString("customerId");
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
        OpenapiLog openapiLog = new OpenapiLog();
        // 忽略证书验证的SSLSocketFactory
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
        };
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        // 忽略主机名验证的HostnameVerifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        // 设置自定义的SSLSocketFactory和HostnameVerifier
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        // 发送HTTPS POST请求
        URL url = new URL(httpUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("appCode", appCode);
        connection.setRequestProperty("apiCode", apiCode);
        connection.setRequestProperty("transactionId", transactionId);
        connection.setRequestProperty("accessToken", "");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        try (OutputStream os = connection.getOutputStream()) {
            byte[] inputBytes = requestBody.getBytes("utf-8");
            os.write(inputBytes, 0, inputBytes.length);
        }
        try (InputStream responseStream = connection.getInputStream()) {
            BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream, "utf-8"));
            String line;
            StringBuilder responseBuilder = new StringBuilder();

            while ((line = responseStreamReader.readLine()) != null) {
                responseBuilder.append(line).append("\n");
            }
            System.out.println(responseBuilder.toString());
            response = responseBuilder.toString();
            log.info("call=====result=="+response);
            openapiLog.setCode(connection.toString());
            openapiLog.setUserId(userId);
            openapiLog.setMessage(response);
            openapiLog.setApiCode(apiCode);
            commonMapper.insertOpenapiLog(openapiLog);
        }catch (Exception e){
            e.printStackTrace();
            openapiLog.setCode(e.getMessage());
            List<OpenapiLog>  openapiLogList=commonMapper.selectCurDateList();
            ActivityRecord activityRecord=commonMapper.selectWarning();
            if(openapiLogList.size()==activityRecord.getStatus()){
                List<ActivityRecord> activityRecordList=commonMapper.selectWarningUser();
                for(ActivityRecord record :activityRecordList){
                    sendNote(record.getUserId(),activityRecord.getActionName());
                }
            }
            commonMapper.insertOpenapiLog(openapiLog);
        }
        return  response;
    }

    public void sendNote(String userId,String message) {
        log.info("短信内容："+message);
        try {
            Packet packet = packetHelper.getCommitPacket1638(userId,message);
            Result result = JSON.parseObject(ropService.execute(packet, userId, "gsmshare"), Result.class);
            log.info("短信返回："+result.getResponse().toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
