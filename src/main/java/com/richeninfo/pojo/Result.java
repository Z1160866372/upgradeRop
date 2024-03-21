package com.richeninfo.pojo;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @Author : zhouxiaohu
 * @create 2022/8/30 15:35
 */
public class Result {
    private Response response;

    @JSONField(name = "Response")
    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
}
