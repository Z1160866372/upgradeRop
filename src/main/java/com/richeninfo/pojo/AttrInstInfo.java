package com.richeninfo.pojo;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @Author : zhouxiaohu
 * @create 2022/8/30 16:35
 */
public class AttrInstInfo {
    private String attrId;
    private String attrValue;

    @JSONField(name = "AttrId")
    public String getAttrId() {
        return attrId;
    }

    public void setAttrId(String attrId) {
        this.attrId = attrId;
    }

    @JSONField(name = "AttrValue")
    public String getAttrValue() {
        return attrValue;
    }

    public void setAttrValue(String attrValue) {
        this.attrValue = attrValue;
    }
}
