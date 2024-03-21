package com.richeninfo.util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Security;

/**
 * @Author : zhouxiaohu
 * @create 2023/2/23 09:20
 */
public class AESCBC {
    /*
     * 加密用的Key 可以用26个字母和数字组成 使用AES-128-CBC加密模式，key需要为16位。
     */
    private static final String iv = "0102030405060708";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * @param data 明文
     * @return 密文
     * @author miracle.qu
     * @Description AES算法加密明文
     */
    public static String encryptAES(String data, String key) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            int blockSize = cipher.getBlockSize();
            byte[] dataBytes = data.getBytes();
            int plaintextLength = dataBytes.length;
            if (plaintextLength % blockSize != 0) {
                plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
            }

            byte[] plaintext = new byte[plaintextLength];
            System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);

            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());  // CBC模式，需要一个向量iv，可增加加密算法的强度

            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
            byte[] encrypted = cipher.doFinal(plaintext);
            return CommonUtil.parseByte2HexStr(encrypted); // BASE64做转码。

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param data 密文
     * @return 明文
     * @author miracle.qu
     * @Description AES算法解密密文
     */
    public static String decryptAES(String data, String key) throws Exception {
        try {
            byte[] encrypted1 = CommonUtil.parseHexStr2Byte(data);//先用base64解密
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original);
            return originalString.trim();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 编码
     *
     * @param byteArray
     * @return
     */
    public static String encode(byte[] byteArray) {
        return new String(new Base64().encode(byteArray));
    }

    /**
     * 解码
     *
     * @param base64EncodedString
     * @return
     */
    public static byte[] decode(String base64EncodedString) {
        return new Base64().decode(base64EncodedString);
    }

}
