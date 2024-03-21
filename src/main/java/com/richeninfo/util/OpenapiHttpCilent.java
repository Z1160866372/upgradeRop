package com.richeninfo.util;

import com.chinamobile.cn.openapi.sdk.v2.manage.DefalutSecurity;
import com.chinamobile.cn.openapi.sdk.v2.manage.SecurityI;
import com.chinamobile.cn.openapi.sdk.v2.model.OpenapiResponse;
import com.chinamobile.cn.openapi.sdk.v2.model.ResponseStatus;
import com.chinamobile.cn.openapi.sdk.v2.util.ConfSerializeUtil;
import com.chinamobile.cn.openapi.sdk.v2.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
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
        securiytManager = new DefalutSecurity(securityUrl, appCode, apk_new);
        // 1.对报文签名
        String publiceKey = securiytManager.getAsk().getPublicKeyStr();
        Long aedkId = securiytManager.getAedk().getId();
        requestBody = securiytManager.encrypt(requestBody);
        String signValue = securiytManager.sign(requestBody);
        HttpPost httpPost = null;
        CloseableHttpResponse response = null;
        String tmp = null;
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
                if (securiytManager.verify(result, sr.getSignValue(), sr.getPublicKey())) {
                    String otext = securiytManager.decrypt(result);
                    return "{\"status\":\"SUCCESS\",\"result\":\"" + otext.replace("\"", "\\\"") + "\"}";
                } else {
                    return "{\"status\":\"ERROR\",\"errorCode\":\"060101\",\"exceptionCode\":\"sign error\"}";
                }
            } else {
                return responseBody;
            }
        } finally {
            if (response != null) {
                response.close();
            }
            if (httpPost != null) {
                httpPost.abort();
            }
        }
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
