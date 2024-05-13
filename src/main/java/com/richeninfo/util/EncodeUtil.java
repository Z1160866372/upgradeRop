/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 */
package com.richeninfo.util;

import org.springframework.stereotype.Component;

/**
 * @Author : zhouxiaohu
 * @create 2022/9/19 14:27
 */
@Component
public class EncodeUtil {
    public static byte[] decodeBase64(String strIn) {
        return Bases64.decode(strIn);
    }

    public static byte[] decodeBase64(byte[] bytes) {
        return Bases64.decode(bytes);
    }

    public static byte[] encodeBase64(byte[] bytes) {
        return Bases64.encode(bytes);
    }

    public static byte[] encodeBase64(String str) {
        return encodeBase64(str.getBytes());
    }

    public static String encodeBase64ToString(byte[] bytes) {
        return new String(encodeBase64(bytes));
    }
}
