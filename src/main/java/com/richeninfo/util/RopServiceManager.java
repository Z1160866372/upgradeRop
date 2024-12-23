/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 */
package com.richeninfo.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinamobile.cn.openapi.sdk.v2.manage.DefalutSecurity;
import com.chinamobile.cn.openapi.sdk.v2.manage.SecurityI;
import com.chinamobile.cn.openapi.sdk.v2.model.OpenapiResponse;
import com.chinamobile.cn.openapi.sdk.v2.util.JsonUtil;
import com.richeninfo.entity.mapper.entity.OpenapiLog;
import com.richeninfo.entity.mapper.mapper.master.CommonMapper;
import com.richeninfo.pojo.OpcProperties;
import com.richeninfo.pojo.Packet;
import com.richeninfo.pojo.WsMessage;
import com.richeninfo.wsdl.AssertionQryUIDWS;
import com.richeninfo.wsdl.Security;
import com.richeninfo.wsdl.WinXMLUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
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
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import javax.net.ssl.*;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Date;

/**
 * @Author : zhouxiaohu
 * @create 2022/9/19 10:29
 */
@Slf4j
@Component
public class RopServiceManager {
    @Resource
    private OpcProperties opcProperties;
    @Value("${nengKai.appCode}")
    private String appCode;
    @Value("${nengKai.apk_new}")
    private String apk_new;
    @Resource
    private HttpServletRequest request;
    @Resource
    private CommonMapper commonMapper;
    @Resource
    private OpenapiHttpCilent openapiHttpCilent;

    private SecurityI securiytManager;

    private CloseableHttpClient client = HttpClients.createDefault();
    public String execute(Packet reqPack, String userId, String actId) throws Exception {
        String response = "";
        try {
            String tmp = null;
            log.info("appCode:" + appCode);
            log.info("apk:" + apk_new);
            String message = JSON.toJSONString(reqPack.getPost());
            log.info("Request:\n" + message);
            response=openapiHttpCilent.HttpsURLConnection(reqPack.getApiCode(), reqPack.getPost().getPubInfo().getTransactionId(), message);
            // response = openapiHttpCilent.call(reqPack.getApiCode(), reqPack.getPost().getPubInfo().getTransactionId(), message);
            log.info("Response(" + reqPack.getPost().getRequest().getBusiCode() + "|" + reqPack.getPost().getRequest().getBusiParams().getString("billId") + "):\n" + response);
            String status = JSON.parseObject(response).getString("status");
            saveOpenapiLog(reqPack, message, response, userId, actId);//保存用户调用记录
            if (!"SUCCESS".equals(status)) {
                throw new RuntimeException(JSON.parseObject(response).getString("result"));
            }
        } catch (Exception e) {
            StringBuffer message = new StringBuffer();
            if (e != null) {
                message.append(e.getClass()).append(": ").append(e.getMessage()).append("\n");
                StackTraceElement[] elements = e.getStackTrace();
                for (StackTraceElement stackTraceElement : elements) {
                    message.append("\t").append(stackTraceElement.toString()).append("\n");
                }
            }
            log.error("Exception : " + message.toString());
            saveOpenapiLog(reqPack, message.toString(), response, userId, actId);//保存用户调用记录
            throw e;
        }
        return JSON.parseObject(response).getString("result");
    }

    public String executes(Packet reqPack, String userId, String actId) throws Exception {
        String response = "";
        try {
            //String message = reqPack.getPost().getRequest().getBusiParams().toString();
            String message = reqPack.getObject().toString();
            log.info("card Request:" + message);
            // OpenapiHttpCilent client = new OpenapiHttpCilent(appCode, apk_new);
            response = openapiHttpCilent.call(reqPack.getApiCode(), null, message);
            log.info("card Response " + response);
            saveOpenapiLog(reqPack, message, response, userId, actId);//保存用户调用记录
        } catch (Exception e) {
            StringBuffer message = new StringBuffer();
            if (e != null) {
                message.append(e.getClass()).append(": ").append(e.getMessage()).append("\n");
                StackTraceElement[] elements = e.getStackTrace();
                for (StackTraceElement stackTraceElement : elements) {
                    message.append("\t").append(stackTraceElement.toString()).append("\n");
                }
            }
            log.error("Exception : " + message.toString());
            saveOpenapiLog(reqPack, message.toString(), response, userId, actId);//保存用户调用记录
            throw e;
        }
        return JSON.parseObject(response).getString("result");
    }

    //接口调用日志
    public void saveOpenapiLog(Packet reqPack, String message, String response, String userId, String actId) {
        OpenapiLog log = new OpenapiLog();
        log.setAppCode(appCode);
        log.setApiCode(reqPack.getApiCode());
        log.setCode(message);
        log.setMessage(response);
        log.setUserId(userId);
        log.setActId(actId);
        commonMapper.insertOpenapiLog(log);
    }

    public static String validServiceNum(String serviceNum, String token) {
        String APP_VALIDSERVICENUM_URL = "https://professorhe.sh.chinamobile.com:18443/datau/datau/thirdpartKeysOthers.du?serviceNum=${SERVICE_NUM}&token=${TOKEN}";
        String url = APP_VALIDSERVICENUM_URL.replace("${SERVICE_NUM}", serviceNum).replace("${TOKEN}", token);
        log.info("接口[validServiceNum]url===" + url);

        String userId = CommonUtil.decryptFromAES(CommonUtil.parseHexStr2Byte(serviceNum),
                CommonUtil.DATAU_THIRDPART_PWD_KEY);
        log.info("接口[validServiceNum]入参===[" + userId + "]" + serviceNum + "," + token);
        String rep = HttpClientTool.doGet(url, null);
        log.info("接口[validServiceNum]出参===" + rep);
        return rep;
    }

    public static WsMessage sendPush(String transactionID, String UID) throws Exception {
        try {
            String url = "http://login.10086.cn:18080/services/AssertionQryUID?wsdl";
            String reqTime = DateUtil.convertDateToString(new Date(), "yyyyMMddHHmmss");
            String SrcSysID = "70210";
            StringBuilder xmlParam = new StringBuilder();
            xmlParam.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append("<CARoot>").append("<SessionHeader>")
                    .append("<ServiceCode>").append("CA03003").append("</ServiceCode>").append("<Version>")
                    .append("CA-2.0").append("</Version>").append("<ActionCode>").append("0").append("</ActionCode>")
                    .append("<TransactionID>").append(transactionID).append("</TransactionID>").append("<SrcSysID>")
                    .append(SrcSysID).append("</SrcSysID>").append("<DstSysID>").append("10000").append("</DstSysID>")
                    .append("<ReqTime>").append(reqTime).append("</ReqTime>").append("<DigitalSign/>")
                    .append("<Request/>").append("</SessionHeader>").append("<SessionBody>").append("<AssertionQryReq>")
                    .append("<UID>").append(UID).append("</UID>").append("</AssertionQryReq>").append("</SessionBody>")
                    .append("</CARoot>");
            String xmlReq = xmlParam.toString();
            String xmlRequst = HttpClientTool.EncodeString(xmlReq, "00210");
            log.info("接口[sendPush]url===" + url);
            log.info("接口[sendPush]入参===" + xmlRequst);
            JaxWsDynamicClientFactory clientFactory = JaxWsDynamicClientFactory.newInstance();
           /* clientFactory.getJaxbContextProperties().put("com.sun.xml.ws.request.timeout", 10000);
            clientFactory.getJaxbContextProperties().put("com.sun.xml.ws.connect.timeout", 10000);
            clientFactory.getJaxbContextProperties().put("com.sun.xml.internal.ws.connection.timeout", 10000);//建立连接的超时时间为10秒
            clientFactory.getJaxbContextProperties().put("com.sun.xml.internal.ws.request.timeout", 10000);//指定请求的响应超时时间为10秒*/

            Client client = clientFactory.createClient(url);

            HTTPConduit conduit = (HTTPConduit) client.getConduit();
            HTTPClientPolicy policy = new HTTPClientPolicy();
            policy.setConnectionTimeout(2000); //连接超时时间
            policy.setReceiveTimeout(2000);//请求超时时间.
            conduit.setClient(policy);

            clientFactory.getJaxbContextProperties();
            client.getEndpoint().getEndpointInfo().setAddress("http://login.10086.cn:18080/services/AssertionQryUID");
            Object[] result = client.invoke("getAssertInfoByUID", xmlRequst);
            String outxml = (String) result[0];
            /*JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
            factory.setServiceClass(AssertionQryUIDWS.class);
            factory.setAddress("http://login.10086.cn:18080/services/AssertionQryUID");
            AssertionQryUIDWS service = (AssertionQryUIDWS) factory.create();
            Client proxy = ClientProxy.getClient(service);
            HTTPConduit contduit = (HTTPConduit) proxy.getConduit();
            HTTPClientPolicy policy = new HTTPClientPolicy();
            policy.setConnectionTimeout(20 * 1000);
            policy.setReceiveTimeout(120 * 1000);
            contduit.setClient(policy);k
            String outxml = service.getAssertInfoByUID(xmlRequst);*/
            log.info("接口[sendPush]出参===" + outxml);
            String c = Security.getDecryptString(outxml);
            log.info("接口[sendPush]解密后的返回报文===" + c);
            String UserName = WinXMLUtils.getNodeValue(c, "/CARoot/SessionBody/AssertionQryRsp/UserInfo/UserName");
            String RspCode = WinXMLUtils.getNodeValue(c, "/CARoot/SessionHeader/Response/RspCode");
            String RspDesc = WinXMLUtils.getNodeValue(c, "/CARoot/SessionHeader/Response/RspDesc");
            System.out.println(UserName);
            WsMessage msgObj = new WsMessage();
            msgObj.setUserId(UserName);
            msgObj.setRspCode(RspCode);
            msgObj.setRspDesc(RspDesc);
            return msgObj;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static void trustAllCertificates() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
        };

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // 还需要忽略主机名验证
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
    }

    public static String httpPostToString(HttpPost httppost) {
        StringBuilder sb = new StringBuilder();
        sb.append("\nRequestLine:");
        sb.append(httppost.getRequestLine().toString());
        int i = 0;
        for (Header header : httppost.getAllHeaders()) {
            if (i == 0) {
                sb.append("\nHeader:");
            }
            i++;
            for (HeaderElement element : header.getElements()) {
                for (NameValuePair nvp : element.getParameters()) {
                    sb.append(nvp.getName());
                    sb.append("=");
                    sb.append(nvp.getValue());
                    sb.append(";");
                }
            }
        }
        HttpEntity entity = httppost.getEntity();
        String content = "";
        if (entity != null) {
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

    private static SSLContext createSSLContext() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        return sslContext;
    }
}
