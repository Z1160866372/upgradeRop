package com.richeninfo.wsdl;

import org.apache.tomcat.util.buf.HexUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * @Author : zhouxiaohu
 * @create 2022/9/19 14:19
 */
public class CtSignature {

    /**
     * 私有构造函数.
     */
    private CtSignature() {}

    /**
     * 生成签名用的签名对象.
     *
     * @param pwd String 证书库密码
     * @param alias String 证书库别名
     * @param priKeyFile 私钥文件名
     * @return Signature 签名对象
     */
    public static Signature createSignatureForSign(String pwd, String alias,
                                                   String priKeyFile) {
        try {
            KeyStore ks = KeyStore.getInstance("JKS");
            InputStream ksfis = null;
            if(priKeyFile.indexOf(":")!=-1) {//绝对路径方式定义的文件.
                ksfis = new FileInputStream(priKeyFile);
            }else {
                //load from classpath
                ksfis = CtSignature.class.getResourceAsStream(priKeyFile);
            }
            BufferedInputStream ksbufin = new BufferedInputStream(ksfis);
            char[] kpass = pwd.toCharArray();
            ks.load(ksbufin, kpass);
            PrivateKey priKey = (PrivateKey) ks.getKey(alias, kpass);
            Signature rsa = Signature.getInstance("SHA1withDSA");
            rsa.initSign(priKey);
            return rsa;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 生成验证签名的签名对象.
     *
     * @param pubKeyFile String 公钥保存文件名
     * @return Signature 签名对象
     */
    public static Signature createSignatureForVerify(String pubKeyFile) {
        try {
            CertificateFactory certificatefactory = CertificateFactory
                    .getInstance("X.509");

            InputStream fin = null;
            if(pubKeyFile.indexOf(":")!=-1) {
                fin = new FileInputStream(pubKeyFile);
            }else {
                //load from classpath
                fin = CtSignature.class.getResourceAsStream(pubKeyFile);
            }
            X509Certificate certificate = (X509Certificate) certificatefactory
                    .generateCertificate(fin);
            PublicKey pub = certificate.getPublicKey();
            Signature dsa = Signature.getInstance("SHA1withDSA");
            dsa.initVerify(pub);
            return dsa;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * @param originalText String 明文数据
     * @param pwd String 证书库密码
     * @param alias String 证书库别名
     * @param priKeyFile 私钥文件名
     * @return String 签名后的
     */
    public static String signature(String originalText, String pwd,
                                   String alias, String priKeyFile) {
        try {
            KeyStore ks = KeyStore.getInstance("JKS");
            InputStream ksfis = null;
            if(priKeyFile.indexOf(":")!=-1) {
                ksfis = new FileInputStream(priKeyFile);
            }else {
                //load from classpath
                ksfis = CtSignature.class.getResourceAsStream(priKeyFile);
            }
            BufferedInputStream ksbufin = new BufferedInputStream(ksfis);
            char[] kpass = pwd.toCharArray();
            ks.load(ksbufin, kpass);
            PrivateKey priKey = (PrivateKey) ks.getKey(alias, kpass);
            Signature rsa = Signature.getInstance("SHA1withDSA");
            rsa.initSign(priKey);
            rsa.update(originalText.getBytes());
            byte[] signedText = rsa.sign();
            return HexUtils.toHexString(signedText);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 验证签名.
     *
     * @param originalText String 原字符串的字节码
     * @param signedText String 签名后的字符串的字节码
     * @param pubKeyFile String 公钥保存文件名
     * @return boolean 验证是否通过
     */
    public static boolean verify(String originalText, String signedText,
                                 String pubKeyFile) {
        try {
            CertificateFactory certificatefactory = CertificateFactory
                    .getInstance("X.509");
            InputStream fin = null;
            if(pubKeyFile.indexOf(":")!=-1) {
                fin = new FileInputStream(pubKeyFile);
            }else {
                //load from classpath
                fin = CtSignature.class.getResourceAsStream(pubKeyFile);
            }
            X509Certificate certificate = (X509Certificate) certificatefactory
                    .generateCertificate(fin);
            PublicKey pub = certificate.getPublicKey();
            Signature dsa = Signature.getInstance("SHA1withDSA");
            dsa.initVerify(pub);
            dsa.update(originalText.getBytes());
            return dsa.verify(HexUtils.fromHexString(signedText));
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * 签名CAP协议.
     *
     * @param cap String CAP协议的字符串
     * @param pwd String 密码
     * @param alias String 别名
     * @param keystorePath 私钥文件所在路径
     * @return String 签名后的协议.
     */
    public static String signatureCAP(String cap, String pwd, String alias,
                                      String keystorePath) {
        String priKeyFile = keystorePath.lastIndexOf(File.separator) < keystorePath
                .length() - 1 ? (keystorePath + File.separator + "uacKeystorer")
                : (keystorePath + "uacKeystore");
        return cap.replaceAll("<DigitalSign/>", "<DigitalSign>"
                + CtSignature.signature(cap, pwd, alias, priKeyFile)
                + "</DigitalSign>");
    }

    /**
     * 验证CAP协议.
     *
     * @param cap String CAP协议的字符串
     * @param keystorePath String 公钥(CER证书)文件所在路径
     * @return boolean 是否通过验证
     */
    public static boolean verifyCAP(String cap, String keystorePath) {
        String originalText = cap.replaceAll(cap.substring(cap
                .indexOf("<DigitalSign>"), cap.indexOf("</DigitalSign>")
                + "</DigitalSign>".length()), "<DigitalSign/>");
        String signedText = cap.substring(cap.indexOf("<DigitalSign>")
                + "<DigitalSign>".length(), cap.indexOf("</DigitalSign>"));
        String pubKeyFile = keystorePath.lastIndexOf(File.separator) < keystorePath
                .length() - 1 ? (keystorePath + File.separator + "uac.cer")
                : (keystorePath + "uac.cer");
        return CtSignature.verify(originalText, signedText, pubKeyFile);
    }

    public static void main(String[] args) {
        String KeyStorePath = "D:\\work\\SEclipse\\workspace\\MBOSS\\com.lianchuang.sso.ct.lib\\keystore\\ct10000Keystore";
        String CertPath = "D:\\work\\SEclipse\\workspace\\MBOSS\\com.lianchuang.sso.ct.lib\\keystore\\ct10000.cer";
        String sigedData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<CAPRoot><SessionHeader><ServiceCode>CAP02001</ServiceCode><Version>1230001234567890</Version><ActionCode>0</ActionCode><TransactionID>11123200902190000000002</TransactionID><SrcSysID>123</SrcSysID><DstSysID>11</DstSysID><ReqTime>20090219200657</ReqTime><DigitalSign/></SessionHeader><SessionBody><AssertionQueryReq><Ticket>ddddddddddddddddddddddd</Ticket></AssertionQueryReq></SessionBody></CAPRoot>";
        System.out.println("修改前，明文内容为:" + sigedData);
        String res = signature(sigedData, "12345678", "ct10000",
                KeyStorePath);
        if (res != null) {
//            String strRes = HexUtils.toHexString(res);
            System.out.println("对明文签名后，数据格式为:" + res);
            System.out.println(verify(sigedData, res, CertPath));

            // 明文修改后
            sigedData = sigedData + "1";
            System.out.println("修改内容后，明文内容为:" + sigedData);
            System.out.println(verify(sigedData, res, CertPath));
        }

    }

}
