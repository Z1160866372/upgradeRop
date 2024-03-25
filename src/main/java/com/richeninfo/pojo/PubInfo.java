/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 */
package com.richeninfo.pojo;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @Author : zhouxiaohu
 * @create 2022/8/30 15:33
 */
public class PubInfo {

    private String RegionCode = "021";
    private String TransactionTime;
    private String ClientIP = "120.55.182.176";
    private String OrgId = "0";
    private String CountyCode = "210";
    private String OpId = "999990144";
    private String InterfaceType = "00";
    private String TransactionId;
    private String InterfaceId = "94";
    private String strCcsOpId = "sys_bossmobile";

    @JSONField(name = "RegionCode")
    public String getRegionCode() {
        return RegionCode;
    }

    public void setRegionCode(String regionCode) {
        RegionCode = regionCode;
    }

    @JSONField(name = "TransactionTime")
    public String getTransactionTime() {
        return TransactionTime;
    }

    public void setTransactionTime(String transactionTime) {
        TransactionTime = transactionTime;
    }

    @JSONField(name = "ClientIP")
    public String getClientIP() {
        return ClientIP;
    }

    public void setClientIP(String clientIP) {
        ClientIP = clientIP;
    }

    @JSONField(name = "OrgId")
    public String getOrgId() {
        return OrgId;
    }

    public void setOrgId(String orgId) {
        OrgId = orgId;
    }

    @JSONField(name = "CountyCode")
    public String getCountyCode() {
        return CountyCode;
    }

    public void setCountyCode(String countyCode) {
        CountyCode = countyCode;
    }

    @JSONField(name = "OpId")
    public String getOpId() {
        return OpId;
    }

    public void setOpId(String opId) {
        OpId = opId;
    }

    @JSONField(name = "InterfaceType")
    public String getInterfaceType() {
        return InterfaceType;
    }

    public void setInterfaceType(String interfaceType) {
        InterfaceType = interfaceType;
    }

    @JSONField(name = "TransactionId")
    public String getTransactionId() {
        return TransactionId;
    }

    public void setTransactionId(String transactionId) {
        TransactionId = transactionId;
    }

    @JSONField(name = "InterfaceId")
    public String getInterfaceId() {
        return InterfaceId;
    }

    public void setInterfaceId(String interfaceId) {
        InterfaceId = interfaceId;
    }

    @JSONField(name = "strCcsOpId")
    public String getStrCcsOpId() {
        return strCcsOpId;
    }

    public void setStrCcsOpId(String strCcsOpId) {
        this.strCcsOpId = strCcsOpId;
    }
}
