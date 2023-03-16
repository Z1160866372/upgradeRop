package com.richeninfo.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.richeninfo.wsdl.DefaultCAPSigner;
import com.richeninfo.wsdl.Security;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

/**
 * @Author : zhouxiaohu
 * @create 2022/8/30 15:41
 */
@Component
public class HttpClientTool {
    public boolean isUseProxy=false;//不使用代理
    static final String CHARSET = "UTF-8";
    static HttpClient httpClient;
    /**
     * 获取httpclient
     *
     * @return
     */
    static {
        if (httpClient == null) {
            httpClient = HttpClients.createDefault();
        }
    }
    /**
     * HTTP Get 获取内容
     *
     * @param url
     *            请求的url地址 ?之前的地址
     * @param params
     *            请求的参数
     * @param charset
     *            编码格式
     * @return 页面内容
     */
    public static String doGet(String url, Map<String, String> params,
                               String charset) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        try {
            if (params != null && !params.isEmpty()) {
                List<NameValuePair> pairs = new ArrayList<NameValuePair>(
                        params.size());
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String value = entry.getValue();
                    if (value != null) {
                        pairs.add(new BasicNameValuePair(entry.getKey(), value));
                    }
                }
                url += "?"
                        + EntityUtils.toString(new UrlEncodedFormEntity(pairs,
                        charset));
            }
            HttpGet httpGet = new HttpGet(url);
            CloseableHttpResponse response = (CloseableHttpResponse)httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpGet.abort();
                throw new RuntimeException("HttpClient,error status code :"
                        + statusCode);
            }
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, "utf-8");
            }
            EntityUtils.consume(entity);
            response.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * HTTP Post 获取内容
     *
     * @param url
     *            请求的url地址 ?之前的地址
     * @param params
     *            请求的参数
     * @param charset
     *            编码格式
     * @return 页面内容
     */
    public static String doPost(String url, Map<String, String> params,String charset) {
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        try {
            List<NameValuePair> pairs = null;
            if (params != null && !params.isEmpty()) {
                pairs = new ArrayList<NameValuePair>(params.size());
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String value = entry.getValue();
                    if (value != null) {
                        pairs.add(new BasicNameValuePair(entry.getKey(), value));
                    }
                }
            }
            HttpPost httpPost = new HttpPost(url);
            if (pairs != null && pairs.size() > 0) {
                httpPost.setEntity(new UrlEncodedFormEntity(pairs, CHARSET));
            }
            CloseableHttpResponse response = (CloseableHttpResponse) httpClient
                    .execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpPost.abort();
                throw new RuntimeException("HttpClient,error status code :"
                        + statusCode);
            }
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, "utf-8");
            }
            EntityUtils.consume(entity);
            response.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * post xml到服务器
     * @param url
     * @param xml
     * @return
     */
    public static String doPostXml(String url, String xml){
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Content-Type", "text/xml;charset=UTF-8");
            httpPost.setEntity(new StringEntity(xml,CHARSET));

            CloseableHttpResponse response = (CloseableHttpResponse) httpClient
                    .execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpPost.abort();
                throw new RuntimeException("HttpClient,error status code :"
                        + statusCode);
            }
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, "utf-8");
            }
            EntityUtils.consume(entity);
            response.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String doGet(String url, Map<String, String> params) {
        return doGet(url, params, CHARSET);
    }

    public static String doPost(String url, Map<String, String> params) {
        return doPost(url, params, CHARSET);
    }

    /**
     * post string到服务器
     * @param url
     * @param param
     * @return
     */
    public static String doPostString(String url, String param){
        String CONTENT_TYPE_TEXT_JSON = "text/json";
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Content-Type", "text/plain;charset=UTF-8");
            StringEntity se = new StringEntity(param);
            se.setContentType(CONTENT_TYPE_TEXT_JSON);
            httpPost.setEntity(se);
            //httpPost.setEntity(new StringEntity(param,CHARSET));
            CloseableHttpResponse response = (CloseableHttpResponse) httpClient.execute(httpPost);
            System.out.println(response);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpPost.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, "utf-8");
            }
            EntityUtils.consume(entity);
            response.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String EncodeString(String xml, String SrcSysID) throws Exception {
        DefaultCAPSigner dcs = new DefaultCAPSigner("123456", "00210",
                "/ydsc.keystore",
                "");
        DefaultCAPSigner dcsv = new DefaultCAPSigner("123456", "00210",
                "/ydsc.keystore",
                "/00210.cer");
        String encyptXml = dcs.signatureCAP(xml);
        System.out.println("加密之前的报文：" + encyptXml);
        if (dcsv.verifyCAP(encyptXml))
            System.out.println("验证签名通过");

        String a = Security.getEncryptString(encyptXml, "8997FB5B40319E9EFBD6F119C152E52CABAB37926419A4AB");
        System.out.println("加密之后的报文：" + a);

        return a;
    }
}
