/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 */
package com.richeninfo.pojo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author : zhouxiaohu
 * @create 2022/9/19 13:21
 */
@Configuration
@ConfigurationProperties(prefix = "nengkai")
public class OpcProperties {
    private String ip;
    private String config_context;
    private String appCode_new;
    private String apk_new;
    private String apk;
    private String openApiURL;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getConfig_context() {
        return config_context;
    }

    public void setConfig_context(String config_context) {
        this.config_context = config_context;
    }

    public String getAppCode_new() {
        return appCode_new;
    }

    public void setAppCode_new(String appCode_new) {
        this.appCode_new = appCode_new;
    }

    public String getApk_new() {
        return apk_new;
    }

    public void setApk_new(String apk_new) {
        this.apk_new = apk_new;
    }

    public String getApk() {
        return apk;
    }

    public void setApk(String apk) {
        this.apk = apk;
    }

    public String getOpenApiURL() {
        return openApiURL;
    }

    public void setOpenApiURL(String openApiURL) {
        this.openApiURL = openApiURL;
    }
}
