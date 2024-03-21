package com.richeninfo.pojo;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @Author : zhouxiaohu
 * @create 2022/8/30 15:31
 */
public class ErrorInfo {
    private String message;
    private String hint;
    private String code;

    @JSONField(name = "Message")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @JSONField(name = "Hint")
    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    @JSONField(name = "Code")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
