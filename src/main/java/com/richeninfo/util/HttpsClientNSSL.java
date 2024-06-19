/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 *
 */

package com.richeninfo.util;

import lombok.extern.log4j.Log4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zst
 * 
 */
@Log4j
public class HttpsClientNSSL {

	static final String CHARSET = "UTF-8";

	static Logger logger =  LoggerFactory.getLogger(HttpsClientNSSL.class);

	/**
	 *  上海移动 校验sim卡 请求
	 * @param url
	 * @return
	 */
	public static String doPostJSON(String url, String jsonString){
		if (StringUtils.isEmpty(url)) {
			return null;
		}
		log.info("jsonString============>"+jsonString);
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(url);
			httpPost.addHeader("Content-type","application/json");
			httpPost.addHeader("Api-Version","1");
			httpPost.setEntity(new StringEntity(jsonString, CHARSET));
			CloseableHttpResponse response = (CloseableHttpResponse) httpclient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				httpPost.abort();
				throw new RuntimeException("HttpClient,error status code :" + statusCode);
			}
			HttpEntity entity = response.getEntity();
			String result = null;
			if (entity != null) {
				result = EntityUtils.toString(entity, "utf-8");
				logger.info("doPostJSON respond  result== {}", result);
				
			}
			EntityUtils.consume(entity);
			response.close();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
