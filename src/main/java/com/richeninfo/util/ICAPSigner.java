/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 */
package com.richeninfo.util;

import com.richeninfo.wsdl.SignException;

/**
 * @Author : zhouxiaohu
 * @create 2022/9/19 13:57
 */
public interface ICAPSigner {
    /**普通字符串解签
     * @param originalText
     * @param signedText
     * @return
     * @throws SignException
     */
    public boolean verifySimple(String originalText, String signedText) throws SignException;

    /**普通字符串签名
     * @param simpleStr
     * @return
     * @throws SignException
     */
    public String signatureSimple(String simpleStr) throws SignException;
    /**
     * 签名.
     * 如果cap为empty，将会抛出runtimeexception.
     *
     * @param cap String 待签名CAP协议字符串
     * @throws SignException 数字签名异常.
     * @return 签名后的字符串
     */
    String signatureCAP(String cap) throws SignException;

    /**
     * 验证.
     * 如果cap为empty，将会抛出runtimeexception.
     *
     * @param cap String CAP协议字符串
     * @throws SignException 数字签名异常.
     * @return boolean 验证是否通过
     */
    boolean verifyCAP(String cap) throws SignException;
}
