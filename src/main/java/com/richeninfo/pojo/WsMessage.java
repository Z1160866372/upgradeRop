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
