package com.richeninfo.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.SecureRandom;

/**
 * @Author : zhouxiaohu
 * @create 2022/12/15 17:40
 */
public class DESHelper {

    private byte[] desKey;

    public DESHelper(String desKey) {
        this.desKey = desKey.getBytes();
    }

    public byte[] desEncrypt(byte[] plainText) throws Exception {
        SecureRandom sr = new SecureRandom();
        byte rawKeyData[] = desKey;
        DESKeySpec dks = new DESKeySpec(rawKeyData);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey key = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, key, sr);
        byte data[] = plainText;
        byte encryptedData[] = cipher.doFinal(data);
        return encryptedData;
    }

    public byte[] desDecrypt(byte[] encryptText) throws Exception {
        SecureRandom sr = new SecureRandom();
        byte rawKeyData[] = desKey;
        DESKeySpec dks = new DESKeySpec(rawKeyData);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey key = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.DECRYPT_MODE, key, sr);
        byte encryptedData[] = encryptText;
        byte decryptedData[] = cipher.doFinal(encryptedData);
        return decryptedData;
    }

    public String encrypt(String input) throws Exception {
        return base64Encode(desEncrypt(input.getBytes()));
    }

    public String decrypt(String input) throws Exception {
        byte[] result = base64Decode(input);
        return new String(desDecrypt(result));
    }

    public static String base64Encode(byte[] s) {
        if (s == null) {
            return null;
        }
        // BASE64Encoder b = new BASE64Encoder();
        // return b.encode(s);
        return org.apache.commons.codec.binary.Base64.encodeBase64String(s);
    }

    public static byte[] base64Decode(String s) throws IOException {
        if (s == null) {
            return null;
        }
        // BASE64Decoder decoder = new BASE64Decoder();
        // byte[] b = decoder.decodeBuffer(s);
        byte[] b = Base64.decodeBase64(s);
        return b;
    }




    public static void main(String[] args) throws Exception {
        String input = "270|851228|JieZi|13012341234";
        String key="AssistantInfo";
        String name="";
        DESHelper crypt = new DESHelper(key);
        String encrypt = crypt.encrypt(input);
        System.out.println("encrypt: "+encrypt);
        String encode = URLEncoder.encode(encrypt, "UTF-8");
        System.out.println("Encode: " + encode);
        System.out.println("Decode: " + crypt.decrypt(URLDecoder.decode(encrypt, "UTF-8")));
        String data = crypt.decrypt(URLDecoder.decode(encrypt, "UTF-8"));
        System.out.println("data: " +data);
        String data_list[] = data.split("\\|");
        for (int i = 0; i < data_list.length; i++) {
            if(i==0){
                name="省份编码";
            }if(i==1){
                name="员工工号";
            }if(i==2){
                name="客户经理ID";
            }if(i==3){
                name="客户经理手机号";
            }
            System.out.println(""+name+": " +data_list[i]);
        }
    }
}
