package com.richeninfo.wsdl;

import com.sun.crypto.provider.SunJCE;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;
import java.security.SecureRandom;
import java.security.Security;

import org.bouncycastle.util.encoders.Hex;

/**
 * @Author : zhouxiaohu
 * @create 2022/9/19 14:41
 */
public class DesEdeKeyTool {
    private static String m_strKeyArithmetic = "DESEDE";
    private static int m_nKeyLength = 168;

    static {
        Security.addProvider(new SunJCE());
    }

    public static Key generateKey(byte[] seed) throws Exception {
        SecureRandom sr = new SecureRandom(seed);
        KeyGenerator kGen = KeyGenerator.getInstance(m_strKeyArithmetic);
        kGen.init(m_nKeyLength, sr);
        Key key = kGen.generateKey();
        return key;
    }

    public static byte[] generateKeyBytes(byte[] seed) throws Exception {
        Key key = generateKey(seed);
        return key.getEncoded();
    }

    public static void generateKeyFile(String fileName, byte[] seed) throws Exception {
        Key key = generateKey(seed);
        FileOutputStream out = new FileOutputStream(fileName);
        out.write(Hex.encode(key.getEncoded()));
        out.close();
    }

    public static Key byte2Key(byte[] keyBytes) throws Exception {
        DESedeKeySpec spec = new DESedeKeySpec(keyBytes);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance(m_strKeyArithmetic);
        return keyfactory.generateSecret(spec);
    }

    public static Key getKeyFromFile(String fileName) throws Exception {
        FileInputStream in = new FileInputStream(fileName);
        byte[] keybytes = new byte[in.available()];
        in.read(keybytes);
        in.close();
        return byte2Key(Hex.decode(new String(keybytes)));
    }

    public static void main(String[] args) {
        try {
            System.out.println(new String(Hex.encode(generateKeyBytes(("key123456").getBytes()))));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
