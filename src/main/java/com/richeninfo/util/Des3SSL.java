package com.richeninfo.util;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.Key;
import java.util.Random;

/**
 * @author: xiaohu.zhou
 * @Created: 2022/8/30 14:29
 */
@Component
public class Des3SSL {
    public final static String secretKeySSL = "EX43@#%XDAHOFC12faolwefs";
    // 向量
    private final static String iv = "01234567";
    // 加解密统一使用的编码方式
    private final static String encoding = "utf-8";
    private static Random r = new Random();
    public static String encode(String plainText)  {
        try {
            Key deskey = null;
            DESedeKeySpec spec = new DESedeKeySpec(secretKeySSL.getBytes());
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
            deskey = keyfactory.generateSecret(spec);

            Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
            IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);
            byte[] encryptData = cipher.doFinal(plainText.getBytes(encoding));
            return org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString(encryptData);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    public static String decode(String encryptText) throws Exception {
        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(secretKeySSL.getBytes());
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
        IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, deskey, ips);
        byte[] decryptData = cipher.doFinal(org.apache.commons.codec.binary.Base64.decodeBase64(encryptText));
        return new String(decryptData, encoding);
    }

    /**
     * 获取用户
     * overdue 过期的时间单位秒 5分钟是 300秒
     */
    public static String getUser(String userSec,int overdue){
        if(userSec != null){
            try {
                String[] sources = decode(userSec).split("-");
                String user = sources[0];
                long time = Long.parseLong(sources[1]);

                //超时
                long overdueLong = overdue*1000;
                long c = System.currentTimeMillis();
                if(c - time > overdueLong){
                    return null;
                }
                return user;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return 	null;
    }
    public static String encodeDC(String mobileNo, String secretKeySSL)
    {
        try
        {
            mobileNo = encodeMobileNo(mobileNo);

            if (mobileNo != null)
                return encode(mobileNo, secretKeySSL);

            return null;
        }
        catch (Exception e) {
            e.printStackTrace(); }
        return null;
    }

    public static String[] decodeDC(String secMobileNo, String secretKeySSL)
    {
        try
        {
            if (StringUtils.isEmpty(secMobileNo))
                return null;

            secMobileNo = decode(secMobileNo, secretKeySSL);
            return decodeMobileNo(secMobileNo);
        } catch (Exception e) {
            e.printStackTrace(); }
        return null;
    }

    public static String encode(String plainText, String secretKeySSL)
            throws Exception
    {
        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(secretKeySSL.getBytes());
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        deskey = keyfactory.generateSecret(spec);

        Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
        IvParameterSpec ips = new IvParameterSpec("01234567".getBytes());
        cipher.init(1, deskey, ips);
        byte[] encryptData = cipher.doFinal(plainText.getBytes("utf-8"));
        return org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString(encryptData);
    }

    public static String decode(String encryptText, String secretKeySSL) throws Exception {
        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(secretKeySSL.getBytes());
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
        IvParameterSpec ips = new IvParameterSpec("01234567".getBytes());
        cipher.init(2, deskey, ips);

        byte[] decryptData = cipher.doFinal(Base64.decodeBase64(encryptText));

        return new String(decryptData, "utf-8");
    }

    public static String[] decodeMobileNo(String sec)
    {
        String p1 = sec.substring(26, 28);
        String p2 = sec.substring(28, 31);
        String p3 = sec.substring(31, 34);

        int p1s = Integer.parseInt(p1.substring(0, 1));
        int p1l = Integer.parseInt(p1.substring(1, 2));
        int p2s = Integer.parseInt(p2.substring(0, 2));
        int p2l = Integer.parseInt(p2.substring(2, 3));
        int p3s = Integer.parseInt(p3.substring(0, 2));
        int p3l = Integer.parseInt(p3.substring(2, 3));

        String timestamp = sec.substring(34);

        String mp1 = sec.substring(p1s, p1s + p1l);
        String mp2 = sec.substring(p2s, p2s + p2l);
        String mp3 = sec.substring(p3s, p3s + p3l);
        String a[]={mp1 + mp2 + mp3,timestamp};
        return a;
    }

    public static String encodeMobileNo(String mobileNo)
    {
        if ((StringUtils.isEmpty(mobileNo)) || (mobileNo.length() != 11)) {
            return null;
        }

        StringBuffer bf = new StringBuffer();

        int p1l = r.nextInt(2) + 2;
        int p2l = r.nextInt(3) + 2;
        int p3l = 11 - p1l - p2l;

        String p1 = mobileNo.substring(0, p1l);
        String p2 = mobileNo.substring(p1l, p1l + p2l);
        String p3 = mobileNo.substring(p1l + p2l);

        int r1l = r.nextInt(2) + 4;
        int r2l = r.nextInt(2) + 4;
        int r3l = r.nextInt(2) + 2;
        int r4l = 15 - r1l - r2l - r3l;

        int r1 = createRandomStr(r1l);
        int r2 = createRandomStr(r2l);
        int r3 = createRandomStr(r3l);
        int r4 = createRandomStr(r4l);

        bf.append(r1).append(p1).append
                (r2).append(p2).append
                (r3).append(p3).append
                (r4).append
                (r1l).append(p1l).append
                (r1l + p1l + r2l).append(p2l).append
                (r1l + p1l + r2l + p2l + r3l).append(p3l).append
                (System.currentTimeMillis());

        return bf.toString();
    }

    public static int createRandomStr(int s) {
        return (new Double(Math.pow(10.0D, s - 1)).intValue() + r.nextInt(new Double(Math.pow(10.0D, s) - Math.pow(10.0D, s - 1)).intValue()));
    }

}
