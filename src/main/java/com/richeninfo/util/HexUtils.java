/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 *
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.richeninfo.util;



public class HexUtils {
    public static byte[] toHex(byte[] digestByte) {
        byte[] rtChar = new byte[digestByte.length * 2];
        for (int i = 0; i < digestByte.length; i++) {
            byte b1 = (byte) (digestByte[i] >> 4 & 0x0f);
            byte b2 = (byte) (digestByte[i] & 0x0f);
            rtChar[i * 2] = (byte) (b1 < 10 ? b1 + 48 : b1 + 55);
            rtChar[i * 2 + 1] = (byte) (b2 < 10 ? b2 + 48 : b2 + 55);
        }
        return rtChar;
    }

    public static String toHexString(byte[] digestByte) {
        return new String(toHex(digestByte));
    }

    public static byte[] fromHex(byte[] sc) {
        byte[] res = new byte[sc.length / 2];
        for (int i = 0; i < sc.length; i++) {
            byte c1 = (byte) (sc[i] - 48 < 17 ? sc[i] - 48 : sc[i] - 55);
            i++;
            byte c2 = (byte) (sc[i] - 48 < 17 ? sc[i] - 48 : sc[i] - 55);
            res[i / 2] = (byte) (c1 * 16 + c2);
        }
        return res;
    }

    public static byte[] fromHexString(String hex) {
        return fromHex(hex.getBytes());
    }

    public static String encode(String in) {
        return new String(toHex(in.getBytes()));
    }

    public static String decode(String in) {
        return new String(fromHex(in.getBytes()));
    }
    
  //zhaoxin add
    /*public static byte[] fromHexString2(String hex) {
    	BASE64Decoder base64De = new BASE64Decoder();
    	try {
			return base64De.decodeBuffer(hex);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
    }*/
    /*public static String toHexString2(byte[] digestByte) {
    	BASE64Encoder base64en = new BASE64Encoder();
    	return base64en.encode(digestByte);
    }*///commented by zhaocw,2010-1-24,for compile warning of SUN base64encoder.
}
