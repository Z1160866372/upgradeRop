/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 *
 */
package com.richeninfo.wsdl;

/**
 * 数字签名相关异常.
 * @author 赵才文
 * @version 1.0.0, 2009-7-1
 * @since 1.0
 */
public class SignException extends Exception {
    
    public static final String TYPE_SIG = "签名异常";
    public static final String TYPE_VRF = "验证签名异常";
    
    private String type;
    
    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * the constructor.
     * @param type must be sig/vrf.
     * @param message the error message.
     * @param ex the original exception.
     */
    public SignException(String type,String message, Throwable ex) {
        super(type+":"+message, ex);
        this.type = type; 
    }

}
