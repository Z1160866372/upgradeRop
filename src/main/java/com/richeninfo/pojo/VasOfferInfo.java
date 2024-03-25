/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 */
package com.richeninfo.pojo;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * @Author : zhouxiaohu
 * @create 2022/8/30 16:33
 */
public class VasOfferInfo {
    private String offerId;
    private String effectiveType;
    private String effectiveDate;
    private String expireDate;
    private String operType;
    private List<AttrInstInfo> attrInstInfo;

    @JSONField(name = "OfferId")
    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    @JSONField(name = "EffectiveType")
    public String getEffectiveType() {
        return effectiveType;
    }

    public void setEffectiveType(String effectiveType) {
        this.effectiveType = effectiveType;
    }

    @JSONField(name = "ExpireDate")
    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }


    @JSONField(name = "EffectiveDate")
    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    @JSONField(name = "OperType")
    public String getOperType() {
        return operType;
    }

    public void setOperType(String operType) {
        this.operType = operType;
    }

    @JSONField(name = "AttrInstInfo")
    public List<AttrInstInfo> getAttrInstInfo() {
        return attrInstInfo;
    }

    public void setAttrInstInfo(List<AttrInstInfo> attrInstInfo) {
        this.attrInstInfo = attrInstInfo;
    }
}
