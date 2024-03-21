package com.richeninfo.util;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author : zhouxiaohu
 * @create 2022/8/30 15:36
 */
@Component
public class RSAUtils {
    private static final String RSA_KEY_ALGORITHM = "RSA";
    private static final String SIGNATURE_ALGORITHM = "MD5withRSA";
    private static final int KEY_SIZE = 1024;
    public static String pub_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCLBvFleieY6NlzVi0PnFQl3bIc+sdXyEwg3ohOW5ZJ82NHczZpJtqJE6+WUB0viMAAtmpzq3QkC4wDuhvcnPLkVGJ8V8U+WBYHPvqjHbyepWFmx49vycfEt6ys9m2U3BV/A/XbSDUUtRDWUrrLfe+d2ORdZM2e8UT9Q1oQ4LLi0QIDAQAB";
    public static String pri_key = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAIsG8WV6J5jo2XNWLQ+cVCXdshz6x1fITCDeiE5blknzY0dzNmkm2okTr5ZQHS+IwAC2anOrdCQLjAO6G9yc8uRUYnxXxT5YFgc++qMdvJ6lYWbHj2/Jx8S3rKz2bZTcFX8D9dtINRS1ENZSust9753Y5F1kzZ7xRP1DWhDgsuLRAgMBAAECgYA8O9PWbWg+fZPIvqtjOg3TIgwLNjOruqIRE5jmqhOhEowO5sHKKQVBon/OlgMvFwZoyJ+yYbQ98arF6porqOwqj3A53/qaCMvzDvH7fXf+x4tFoBdGB43gWGCyf2xQAsns4OCS8jw1469yCqjg+vNkvIGyq+iNJUL0vuTBJFwlaQJBANuYx9d2yg0RIiecITIgvtJTSXYLqTkWpcSwci+bj4c2Mv8tNQu3EVC6mcB5jntARVCkwOVJ3TG+GwcsXHoSqtcCQQCiEvUJKjW6PZIZnSDuaJIzb4NCSaOzsVsxE/hGC/Zx+fggbobxinjjpgxxaQnD4E4zZ3ONEE59S2VrSFZtGxKXAkEAwwoPdO+sxa5SB5xEX6F12pjexlnVEz9qPCf7Qw2HjTA2Wy5rztvqJSmXJbzRL+cFqXqrWPsh2uxeVSCxgjrSQQJAfhqh7W+aPj6J3sQTS2yx1LN/PaRuavIkMhuHoXjNWAFWvmVBOc60IOyh4dwIbQ+pyPVFQwzdBUAUuVSfamUWIQJBAI0P8azouhqeIk6+jB5vOoliR+77XDqoEs5RonfLkcG//gLY6ncHURonS4ssJsVBhi2Mb3PPzfzNFkyOf54vN+4=";
    private static final String PUBLIC_KEY = "publicKey";
    private static final String PRIVATE_KEY = "privateKey";
    private static final int MAX_ENCRYPT_BLOCK = 117;
    private static final int MAX_DECRYPT_BLOCK = 128;

    private static Map<String, String> initKey()
            throws Exception {
        KeyPairGenerator keygen =
                KeyPairGenerator.getInstance("RSA");
        SecureRandom secrand = new SecureRandom();
        secrand.setSeed("richeninfo".getBytes());
        keygen.initialize(1024, secrand);
        KeyPair keys = keygen.genKeyPair();
        String pub_key = org.apache.commons.codec.binary.Base64.encodeBase64String(keys.getPublic().getEncoded());
        String pri_key = org.apache.commons.codec.binary.Base64.encodeBase64String(keys.getPrivate().getEncoded());
        Map keyMap = new HashMap();
        keyMap.put("publicKey", pub_key);
        keyMap.put("privateKey", pri_key);
        System.out.println("公钥：" + pub_key);
        System.out.println("私钥：" + pri_key);
        return keyMap;
    }

    public static String getPublicKey(Map<String, String> keyMap)
            throws Exception {
        return (keyMap.get("publicKey"));
    }

    public static String getPrivateKey(Map<String, String> keyMap)
            throws Exception {
        return (keyMap.get("privateKey"));
    }

    public static String sign(byte[] data, String pri_key)
            throws Exception {
        byte[] pri_key_bytes = org.apache.commons.codec.binary.Base64.decodeBase64(pri_key);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(pri_key_bytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);

        Signature signature = Signature.getInstance("MD5withRSA");

        signature.initSign(priKey);

        signature.update(data);

        return org.apache.commons.codec.binary.Base64.encodeBase64String(signature.sign());
    }

    public boolean verify(byte[] data, byte[] sign, String pub_key)
            throws Exception {
        byte[] pub_key_bytes = org.apache.commons.codec.binary.Base64.decodeBase64(pub_key);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(pub_key_bytes);

        PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);

        Signature signature = Signature.getInstance("MD5withRSA");

        signature.initVerify(pubKey);

        signature.update(data);

        return signature.verify(sign);
    }

    private static byte[] encryptByPubKey(byte[] data, byte[] pub_key)
            throws Exception {
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(pub_key);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);

        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(1, publicKey);
        return cipher.doFinal(data);
    }

    public static String encryptByPubKey(String data)
            throws Exception {
        byte[] pub_key_bytes = org.apache.commons.codec.binary.Base64.decodeBase64(pub_key);
        byte[] enSign = encryptByPubKey(data.getBytes(), pub_key_bytes);
        return org.apache.commons.codec.binary.Base64.encodeBase64String(enSign);
    }

    public static byte[] encryptByPublicKey(byte[] data) throws Exception {
        byte[] keyBytes = org.apache.commons.codec.binary.Base64.decodeBase64(pub_key);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Key publicK = keyFactory.generatePublic(x509KeySpec);

        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(1, publicK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;

        int i = 0;

        while (inputLen - offSet > 0) {
            byte[] cache;
            if (inputLen - offSet > 117)
                cache = cipher.doFinal(data, offSet, 117);
            else
                cache = cipher.doFinal(data, offSet, inputLen - offSet);

            out.write(cache, 0, cache.length);
            ++i;
            offSet = i * 117;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

    private static byte[] encryptByPriKey(byte[] data, byte[] pri_key)
            throws Exception {
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(pri_key);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(1, privateKey);
        return cipher.doFinal(data);
    }

    public static String encryptByPriKey(String data, String pri_key)
            throws Exception {
        byte[] pri_key_bytes = org.apache.commons.codec.binary.Base64.decodeBase64(pri_key);
        byte[] enSign = encryptByPriKey(data.getBytes(), pri_key_bytes);
        return org.apache.commons.codec.binary.Base64.encodeBase64String(enSign);
    }

    private static byte[] decryptByPubKey(byte[] data, byte[] pub_key)
            throws Exception {
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(pub_key);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);

        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(2, publicKey);
        return cipher.doFinal(data);
    }

    public static String decryptByPubKey(String data, String pub_key)
            throws Exception {
        byte[] pub_key_bytes = org.apache.commons.codec.binary.Base64.decodeBase64(pub_key);
        byte[] design = decryptByPubKey(org.apache.commons.codec.binary.Base64.decodeBase64(data), pub_key_bytes);
        return new String(design);
    }

    private static byte[] decryptByPriKey(byte[] data, byte[] pri_key)
            throws Exception {
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(pri_key);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(2, privateKey);
        return cipher.doFinal(data);
    }

    public static String decryptByPriKey(String data)
            throws Exception {
        byte[] pri_key_bytes = org.apache.commons.codec.binary.Base64.decodeBase64(pri_key);
        byte[] design = decryptByPriKey(org.apache.commons.codec.binary.Base64.decodeBase64(data), pri_key_bytes);
        return new String(design);
    }

    public static byte[] decryptByPrivateKey(byte[] encryptedData) throws Exception {
        byte[] keyBytes = Base64.decodeBase64(pub_key);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(2, privateK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;

        int i = 0;

        while (inputLen - offSet > 0) {
            byte[] cache;
            if (inputLen - offSet > 128)
                cache = cipher.doFinal(encryptedData, offSet, 128);
            else
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);

            out.write(cache, 0, cache.length);
            ++i;
            offSet = i * 128;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    public static void main(String[] args)
            throws Exception {
    }
}
