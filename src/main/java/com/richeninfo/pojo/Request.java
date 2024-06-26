/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 */
package com.richeninfo.pojo;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * @Author : zhouxiaohu
 * @create 2022/8/30 15:25
 */
public class Request {
    private String BusiCode;
    private JSONObject BusiParams;

    @JSONField(name = "BusiCode")
    public String getBusiCode() {
        return BusiCode;
    }

    public void setBusiCode(String busiCode) {
        BusiCode = busiCode;
    }


    @JSONField(name = "BusiParams")
    public JSONObject getBusiParams() {
        return BusiParams;
    }

    public void setBusiParams(JSONObject busiParams) {
        BusiParams = busiParams;
    }
}
