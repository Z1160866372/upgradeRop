/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 */
package com.richeninfo.wsdl;

import com.richeninfo.util.EncodeUtil;

import java.security.MessageDigest;

/**
 * @Author : zhouxiaohu
 * @create 2022/9/19 14:40
 */
public class DigestForString {
    /**ENCODING_BASE64.*/
    public static final String ENCODING_BASE64 = "BASE64";

    /**
     * @parameters @param strInput strInput
     * @parameters @param strArithmetic strArithmetic
     * @parameters @param encoding encoding
     * @parameters @return
     * @parameters @throws Exception 参数说明
     * @returns @return  返回说明
     * @throws
     */
    public static String message(String strInput, String strArithmetic, String encoding) throws Exception {
        if ((strArithmetic == null) || ("".equals(strArithmetic)) || (strInput == null)) {
            throw new Exception("must have message content and arithmetic!\n");
        }

        if ((encoding == null) || ("".equals(encoding))) {
            encoding = "RAW";
        }

        String strOut = "";
        byte[] bOut = null;
        byte[] bIn = strInput.getBytes("UTF-8");

        MessageDigest md = MessageDigest.getInstance(strArithmetic);

        md.update(bIn);
        bOut = md.digest();

        if ("BASE64".equalsIgnoreCase(encoding)) {
            bOut = EncodeUtil.encodeBase64(bOut);
        }
        strOut = new String(bOut);

        return strOut;
    }

    /**
     * @parameters @param strInput  strInput
     * @parameters @param encoding encoding
     * @parameters @return
     * @parameters @throws Exception 参数说明
     * @returns @return 返回说明  String
     * @throws
     */
    public static String message(String strInput, String encoding) throws Exception {
        return message(strInput, "SHA-1", encoding);
    }

    /**
     * @parameters @param newMD newMD
     * @parameters @param oldMD oldMD
     * @parameters @return 参数说明
     * @returns
     * @throws
     */
    public static boolean verify(byte[] newMD, byte[] oldMD) {
        boolean bResult = true;

        int len = newMD.length;
        if (len != oldMD.length) {
            bResult = false;
        }
        else {
            for (int i = 0; i < len; ++i) {
                if (oldMD[i] == newMD[i]) {
                    continue;
                }
                bResult = false;
                break;
            }
        }

        return bResult;
    }

    /**
     * @parameters @param strNewDigest strNewDigest
     * @parameters @param strOldDigest strOldDigest
     * @parameters @return
     * @parameters @throws Exception 参数说明
     * @returns @return  返回说明
     * @throws
     */
    public static boolean verify(String strNewDigest, String strOldDigest) throws Exception {
        return verify(strNewDigest.getBytes("UTF-8"), strOldDigest.getBytes("UTF-8"));
    }
}
