/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 */
package com.richeninfo.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author: xiaohu.zhou
 * @Created: 2022/8/30 14:30
 */
public class Base64 {

    private static final char[] legalChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
    private static final byte[] encodingTable = {65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81,
            82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111,
            112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47};

    private static final byte[] decodingTable = new byte[128];

    public static void main(String[] args) {
        System.out.println(88 * 30 * 2.5);
    }

    static {
        for (int i = 65; i <= 90; ++i) {
            decodingTable[i] = (byte) (i - 65);
        }

        for (int i = 97; i <= 122; ++i) {
            decodingTable[i] = (byte) (i - 97 + 26);
        }

        for (int i = 48; i <= 57; ++i) {
            decodingTable[i] = (byte) (i - 48 + 52);
        }

        decodingTable[43] = 62;
        decodingTable[47] = 63;
    }

    public static String encode(String data) {
        return encode(data.getBytes());
    }

    public static String encode(byte[] data) {
        int start = 0;
        int len = data.length;
        StringBuffer buf = new StringBuffer(data.length * 3 / 2);

        int end = len - 3;
        int i = start;
        int n = 0;

        while (i <= end) {
            int d = (((data[i]) & 0x0ff) << 16) | (((data[i + 1]) & 0x0ff) << 8) | ((data[i + 2]) & 0x0ff);

            buf.append(legalChars[(d >> 18) & 63]);
            buf.append(legalChars[(d >> 12) & 63]);
            buf.append(legalChars[(d >> 6) & 63]);
            buf.append(legalChars[d & 63]);

            i += 3;
        }

        if (i == start + len - 2) {
            int d = (((data[i]) & 0x0ff) << 16) | (((data[i + 1]) & 255) << 8);

            buf.append(legalChars[(d >> 18) & 63]);
            buf.append(legalChars[(d >> 12) & 63]);
            buf.append(legalChars[(d >> 6) & 63]);
            buf.append("=");
        } else if (i == start + len - 1) {
            int d = ((data[i]) & 0x0ff) << 16;

            buf.append(legalChars[(d >> 18) & 63]);
            buf.append(legalChars[(d >> 12) & 63]);
            buf.append("==");
        }

        return buf.toString();
    }

    private static int decode(char c) {
        if (c >= 'A' && c <= 'Z') {
            return (c) - 65;
        } else if (c >= 'a' && c <= 'z') {
            return (c) - 97 + 26;
        } else if (c >= '0' && c <= '9') {
            return (c) - 48 + 26 + 26;
        } else {
            switch (c) {
                case '+':
                    return 62;
                case '/':
                    return 63;
                case '=':
                    return 0;
                default:
                    throw new RuntimeException("unexpected code: " + c);
            }
        }
    }

    public static byte[] encoded(byte[] data) {
        int modulus = data.length % 3;
        byte[] bytes;
        if (modulus == 0) {
            bytes = new byte[4 * data.length / 3];
        } else {
            bytes = new byte[4 * (data.length / 3 + 1)];
        }

        int dataLength = data.length - modulus;

        int i = 0;
        for (int j = 0; i < dataLength; j += 4) {
            int a1 = data[i] & 0xFF;
            int a2 = data[(i + 1)] & 0xFF;
            int a3 = data[(i + 2)] & 0xFF;

            bytes[j] = encodingTable[(a1 >>> 2 & 0x3F)];
            bytes[(j + 1)] = encodingTable[((a1 << 4 | a2 >>> 4) & 0x3F)];
            bytes[(j + 2)] = encodingTable[((a2 << 2 | a3 >>> 6) & 0x3F)];
            bytes[(j + 3)] = encodingTable[(a3 & 0x3F)];

            i += 3;
        }
        int d1;
        int b1;
        int b2;
        switch (modulus) {
            case 0:
                break;
            case 1:
                d1 = data[(data.length - 1)] & 0xFF;
                b1 = d1 >>> 2 & 0x3F;
                b2 = d1 << 4 & 0x3F;

                bytes[(bytes.length - 4)] = encodingTable[b1];
                bytes[(bytes.length - 3)] = encodingTable[b2];
                bytes[(bytes.length - 2)] = 61;
                bytes[(bytes.length - 1)] = 61;
                break;
            case 2:
                d1 = data[(data.length - 2)] & 0xFF;
                int d2 = data[(data.length - 1)] & 0xFF;

                b1 = d1 >>> 2 & 0x3F;
                b2 = (d1 << 4 | d2 >>> 4) & 0x3F;
                int b3 = d2 << 2 & 0x3F;

                bytes[(bytes.length - 4)] = encodingTable[b1];
                bytes[(bytes.length - 3)] = encodingTable[b2];
                bytes[(bytes.length - 2)] = encodingTable[b3];
                bytes[(bytes.length - 1)] = 61;
        }

        return bytes;
    }

    public static byte[] decode(String s) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            decode(s, bos);
        } catch (IOException e) {
            throw new RuntimeException();
        }
        byte[] decodedBytes = bos.toByteArray();
        try {
            bos.close();
            bos = null;
        } catch (IOException ex) {
            System.err.println("Error while decoding BASE64: " + ex.toString());
        }
        return decodedBytes;
    }

    private static void decode(String s, OutputStream os) throws IOException {
        int i = 0;

        int len = s.length();

        while (true) {
            while (i < len && s.charAt(i) <= ' ') {
                i++;
            }

            if (i == len) {
                break;
            }

            int tri = (decode(s.charAt(i)) << 18) + (decode(s.charAt(i + 1)) << 12) + (decode(s.charAt(i + 2)) << 6) + (decode(s.charAt(i + 3)));

            os.write((tri >> 16) & 255);
            if (s.charAt(i + 2) == '=') {
                break;
            }
            os.write((tri >> 8) & 255);
            if (s.charAt(i + 3) == '=') {
                break;
            }
            os.write(tri & 255);

            i += 4;
        }
    }

    public static byte[] decode(byte[] data) {
        byte[] bytes;
        if (data[(data.length - 2)] == 61) {
            bytes = new byte[(data.length / 4 - 1) * 3 + 1];
        } else if (data[(data.length - 1)] == 61) {
            bytes = new byte[(data.length / 4 - 1) * 3 + 2];
        } else {
            bytes = new byte[data.length / 4 * 3];
        }

        int i = 0;
        byte b1;
        byte b2;
        byte b3;
        byte b4;
        for (int j = 0; i < data.length - 4; j += 3) {
            b1 = decodingTable[data[i]];
            b2 = decodingTable[data[(i + 1)]];
            b3 = decodingTable[data[(i + 2)]];
            b4 = decodingTable[data[(i + 3)]];

            bytes[j] = (byte) (b1 << 2 | b2 >> 4);
            bytes[(j + 1)] = (byte) (b2 << 4 | b3 >> 2);
            bytes[(j + 2)] = (byte) (b3 << 6 | b4);

            i += 4;
        }

        if (data[(data.length - 2)] == 61) {
            b1 = decodingTable[data[(data.length - 4)]];
            b2 = decodingTable[data[(data.length - 3)]];

            bytes[(bytes.length - 1)] = (byte) (b1 << 2 | b2 >> 4);
        } else if (data[(data.length - 1)] == 61) {
            b1 = decodingTable[data[(data.length - 4)]];
            b2 = decodingTable[data[(data.length - 3)]];
            b3 = decodingTable[data[(data.length - 2)]];

            bytes[(bytes.length - 2)] = (byte) (b1 << 2 | b2 >> 4);
            bytes[(bytes.length - 1)] = (byte) (b2 << 4 | b3 >> 2);
        } else {
            b1 = decodingTable[data[(data.length - 4)]];
            b2 = decodingTable[data[(data.length - 3)]];
            b3 = decodingTable[data[(data.length - 2)]];
            b4 = decodingTable[data[(data.length - 1)]];

            bytes[(bytes.length - 3)] = (byte) (b1 << 2 | b2 >> 4);
            bytes[(bytes.length - 2)] = (byte) (b2 << 4 | b3 >> 2);
            bytes[(bytes.length - 1)] = (byte) (b3 << 6 | b4);
        }

        return bytes;
    }


}
