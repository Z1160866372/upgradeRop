package com.richeninfo.pojo;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * @Author : zhouxiaohu
 * @create 2022/8/30 15:33
 */
public class Packet implements Serializable {
    private String apiCode;
    private Post post;
    private JSONObject Object;

    @JSONField(name="apiCode")
    public String getApiCode() {
        return apiCode;
    }

    public void setApiCode(String apiCode) {
        this.apiCode = apiCode;
    }

    @JSONField(name="post")
    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
    @JSONField(name="Object")
    public Object getObject() {
        return Object;
    }
    public void setObject(JSONObject object) {
        Object = object;
    }
}
