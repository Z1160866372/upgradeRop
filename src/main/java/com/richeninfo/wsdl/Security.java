/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 */
package com.richeninfo.wsdl;

import com.richeninfo.util.Bases64;
import org.bouncycastle.util.encoders.Hex;

import java.security.Key;

/**
 * @Author : zhouxiaohu
 * @create 2022/9/19 14:23
 */
public class Security {
    public static String ECSKEY = "8997FB5B40319E9EFBD6F119C152E52CABAB37926419A4AB";

    public static final byte[] iv = { 1, 2, 3, 4, 5, 6, 7, 8 };

    public static String generalStringFor3DES(String keyValue, String For3DES, String ForDigest, byte[] keyIV,
                                              String linkString) throws Exception {
        String rtn = null;
        String tempcheck = null;
        String _For3DES = null;
        try {
            Key key = get3DESKey(keyValue);

            if ((ForDigest != null) && (ForDigest.length() > 0)) {
                tempcheck = DigestForString.message(ForDigest, "BASE64");
                _For3DES = For3DES + linkString + tempcheck;
            }
            else {
                _For3DES = For3DES;
            }
            byte[] encryptStr = Bases64.encode(EncryptionForString.encrypt(keyIV, _For3DES, key, "RAW"));

            rtn = new String(encryptStr);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return rtn;
    }

    public static String Decrypt3DES2String(String keyValue, String DES2String, byte[] keyIV) throws Exception {
        String DecryptString = new String(decrypt(DES2String.getBytes(), keyValue, keyIV), "UTF-8");
        return DecryptString;
    }

    public static Key get3DESKey(String keyValue) throws Exception {
        Key key = null;
        key = DesEdeKeyTool.byte2Key(Hex.decode(keyValue));
        return key;
    }

    public static byte[] decrypt(byte[] encryptByBase64Str, String keyValue, byte[] keyIV) throws Exception {
        byte[] encryptStr = Bases64.decode(encryptByBase64Str);
        Key key = get3DESKey(keyValue);

        byte[] decryptStr = EncryptionForString.decrypt(keyIV, encryptStr, key);
        return decryptStr;
    }

    public static String getEncryptString(String in) {
        try {
            return generalStringFor3DES(ECSKEY, (in == null) ? "" : in, null, iv, "$");
        }
        catch (Exception e) {
        }
        return null;
    }

    public static String getEncryptString(String in, String key) {
        try {
            return generalStringFor3DES(key, (in == null) ? "" : in, null, iv, "$");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getEncryptString(String in, boolean ifUseAuthenticator) {
        try {
            if (ifUseAuthenticator) {
                return generalStringFor3DES(ECSKEY, (in == null) ? "" : in, in, iv, "$");
            }
            return generalStringFor3DES(ECSKEY, (in == null) ? "" : in, null, iv, "$");
        }
        catch (Exception e) {
        }
        return null;
    }

    public static String getDecryptString(String in) {
        try {
            return Decrypt3DES2String(ECSKEY, in, iv);
        }
        catch (Exception e) {
        }
        return null;
    }

    public static String getDecryptString(String in,String key) {
        try {
            return Decrypt3DES2String(key, in, iv);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        try {
            String enc = getEncryptString("123456$789", false);
            System.out.println(enc);
            System.out.println(getDecryptString(enc));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String byteArr2HexStr(byte[] bs) {
        int x = 0;
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < bs.length; ++i) {
            x = bs[i] & 0xFF;

            if (x < 16) {
                b.append('0');
            }
            b.append(Integer.toString(x, 16).toUpperCase());
        }
        return b.toString();
    }

    public static byte[] hexStr2ByteArr(String strIn) throws Exception {
        byte[] arrB = strIn.getBytes();
        int iLen = arrB.length;

        byte[] arrOut = new byte[iLen / 2];
        for (int i = 0; i < iLen; i += 2) {
            arrOut[(i / 2)] = (byte) Integer.parseInt(strIn.substring(i, i + 2), 16);
        }
        return arrOut;
    }

    public String getECSKEY() {
        return ECSKEY;
    }

    public static void setECSKEY(String ECSKEY) {
        Security.ECSKEY = ECSKEY;
    }
}
