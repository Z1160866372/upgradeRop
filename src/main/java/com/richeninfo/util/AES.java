package com.richeninfo.util;

import com.richeninfo.pojo.Constant;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @Author : zhouxiaohu
 * @create 2022/11/18 13:27
 */
@Component
public class AES {
    //使用AES-128-CBC加密模式，key需要为16位,key和iv可以相同！
    private static String KEY = "Richeninfo123456";

    private static String IV = "Richeninfo123456";


    /**
     * 加密方法
     * @param data  要加密的数据
     * @param key 加密key
     * @param iv 加密iv
     * @return 加密的结果
     * @throws Exception
     */
    public static String encrypt(String data, String key, String iv) throws Exception {
        try {

            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");//"算法/模式/补码方式"
            int blockSize = cipher.getBlockSize();

            byte[] dataBytes = data.getBytes();
            int plaintextLength = dataBytes.length;
            if (plaintextLength % blockSize != 0) {
                plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
            }

            byte[] plaintext = new byte[plaintextLength];
            System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);

            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());

            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
            byte[] encrypted = cipher.doFinal(plaintext);

            return new org.apache.tomcat.util.codec.binary.Base64().encodeToString(encrypted);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解密方法
     * @param data 要解密的数据
     * @param key  解密key
     * @param iv 解密iv
     * @return 解密的结果
     * @throws Exception
     */
    public static String desEncrypt(String data, String key, String iv) throws Exception {
        try {
            byte[] encrypted1 = new org.apache.tomcat.util.codec.binary.Base64().decode(data);

            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());

            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original).trim();
            return originalString;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 使用默认的key和iv加密
     * @param data
     * @return
     * @throws Exception
     */
    public static String encrypt(String data) throws Exception {
        return encrypt(data, KEY, IV);
    }

    /**
     * 使用默认的key和iv解密
     * @param data
     * @return
     * @throws Exception
     */
    public static String desEncrypt(String data) throws Exception {
        return desEncrypt(data, KEY, IV);
    }


    private static byte[] encrypt(byte[] text, byte[] key) throws Exception {
        SecretKeySpec aesKey = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
        return cipher.doFinal(text);
    }
    private static byte[] decrypt(byte[] text, byte[] key) throws Exception {
        SecretKeySpec aesKey = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, aesKey);
        return cipher.doFinal(text);
    }

    /**
     * @date 2017年8月1日
     * @desc 加密
     * @param text 明文
     * @param key 密钥
     */
    public static String encodeAES(String text, String key) throws Exception {
        byte[] keybBytes = DigestUtils.md5(key);
        byte[] passwdBytes = text.getBytes();
        byte[] aesBytyes = encrypt(passwdBytes, keybBytes);
        return new String(org.apache.commons.codec.binary.Base64.encodeBase64(aesBytyes));
    }
    /**
     * @date 2017年8月1日
     * @desc 解密
     * @param password 密文
     * @param key 密钥
     */
    public static String deCodeAES(String password, String key) throws Exception {
        System.out.println("password == " + password + "\nkey == " + key);
        byte[] keybBytes = DigestUtils.md5(key);
        byte[] debase64Bytes = Base64.decodeBase64(password.getBytes());
        return new String(decrypt(debase64Bytes, keybBytes));
    }

    public static String decrypt(String encrypted,String key,String initVector) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));
            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        try {
            System.out.println(deCodeAES("jKvcxFb8tYLn7uNoT43SPQ==", Constant.APP_KEY));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
