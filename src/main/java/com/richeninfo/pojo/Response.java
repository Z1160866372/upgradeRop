package com.richeninfo.pojo;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * @Author : zhouxiaohu
 * @create 2022/8/30 15:30
 */
public class Response {
    private ErrorInfo errorInfo;
    private JSONObject retInfo;

    @JSONField(name = "ErrorInfo")
    public ErrorInfo getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(ErrorInfo errorInfo) {
        this.errorInfo = errorInfo;
    }

    @JSONField(name = "RetInfo")
    public JSONObject getRetInfo() {
        return retInfo;
    }

    public void setRetInfo(JSONObject retInfo) {
        this.retInfo = retInfo;
    }
}
