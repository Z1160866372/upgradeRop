package com.richeninfo.pojo;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @Author : zhouxiaohu
 * @create 2022/8/30 15:34
 */
public class Post {
    private PubInfo PubInfo;
    private Request Request;

    @JSONField(name="PubInfo")
    public PubInfo getPubInfo() {
        return PubInfo;
    }

    public void setPubInfo(PubInfo pubInfo) {
        PubInfo = pubInfo;
    }
    @JSONField(name="Request")
    public Request getRequest() {
        return Request;
    }
    public void setRequest(Request request) {
        Request = request;
    }
}
