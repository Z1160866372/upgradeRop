package com.richeninfo.util;

import org.springframework.stereotype.Component;

import java.security.MessageDigest;

/**
 * @author: xiaohu.zhou
 * @Created: 2022/8/30 14:30
 */
public class MD5 {
    private final String inStr;
    private MessageDigest md5;
    private static String key = "BA7610FE864A193DDC9E31FB320E0D852F858C136EE943BC";

    public MD5(String inStr) {
        this.inStr = inStr;
        try {
            md5 = MessageDigest.getInstance("MD5");
        }
        catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }

    public static void setKey(String key) {
        MD5.key = key;
    }

    public String compute() {
        char[] charArray = inStr.toCharArray();

        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; ++i) {
            byteArray[i] = (byte) charArray[i];
        }
        byte[] md5Bytes = md5.digest(byteArray);

        StringBuffer hexValue = new StringBuffer();

        for (int i = 0; i < md5Bytes.length; ++i) {
            int val = md5Bytes[i] & 0xFF;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    public static void main(String[] args) {
        String postString = getMd5String("123$456$");
        System.out.println(postString);
    }

    /**
     * MD5加密.
     * @author zhangqs
     * @since 2013-3-27
     * @parameters @param inStr
     * @parameters @return 参数说明
     * @returns  返回说明
     * @throws
     */
    public static String getMd5String(String inStr) {
        MD5 md5 = new MD5(inStr + key);
        return md5.compute().toUpperCase();
    }
    /**
     * MD5 32位小写加密
     */
    public static String encrypt32(String encryptStr) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] md5Bytes = md5.digest(encryptStr.getBytes());
            StringBuffer hexValue = new StringBuffer();
            for (int i = 0; i < md5Bytes.length; i++) {
                int val = ((int) md5Bytes[i]) & 0xff;
                if (val < 16)
                    hexValue.append("0");
                hexValue.append(Integer.toHexString(val));
            }
            //默认小写，在tostring后加toUpperCase()即为大写加密
            encryptStr = hexValue.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return encryptStr;
    }
}
