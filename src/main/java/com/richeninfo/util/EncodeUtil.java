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
    private static Base64 Bases64;

    public static byte[] decodeBases64(String strIn) {
        return Bases64.decode(strIn);
    }

    public static byte[] decodeBases64(byte[] bytes) {
        return Bases64.decode(bytes);
    }

    public static byte[] encodeBases64(byte[] bytes) {
        return Bases64.encoded(bytes);
    }

    public static byte[] encodeBases64(String str) {
        return encodeBases64(str.getBytes());
    }

    public static String encodeBases64ToString(byte[] bytes) {
        return new String(encodeBases64(bytes));
    }
}
