/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 */
package com.richeninfo.pojo;

/**
 * @Author : zhouxiaohu
 * @create 2022/9/19 13:41
 */
public class WsMessage {
    private String userId;
    private String RspDesc;
    private String RspCode;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRspDesc() {
        return RspDesc;
    }

    public void setRspDesc(String rspDesc) {
        RspDesc = rspDesc;
    }

    public String getRspCode() {
        return RspCode;
    }

    public void setRspCode(String rspCode) {
        RspCode = rspCode;
    }
}
