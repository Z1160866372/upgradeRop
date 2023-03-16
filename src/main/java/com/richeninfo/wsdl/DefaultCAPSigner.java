package com.richeninfo.wsdl;

import com.richeninfo.entity.mapper.mapper.master.ICAPSigner;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.buf.HexUtils;

import java.security.Signature;

/**
 * @Author : zhouxiaohu
 * @create 2022/9/19 13:56
 */
public class DefaultCAPSigner implements ICAPSigner {

    private Signature sfs;
    private Signature sfv;

    /**
     *
     * 构造函数.
     *
     * @param pwd String 证书库密码
     * @param alias String 证书库别名
     * @param priKeyFile 私钥文件名
     * @param pubKeyFile String 公钥保存文件名
     */
    public DefaultCAPSigner(String pwd, String alias, String priKeyFile,
                            String pubKeyFile) {
        if(pwd != null && !pwd.equals("")){
            this.sfs = CtSignature.createSignatureForSign(pwd, alias, priKeyFile);
        }

        this.sfv = CtSignature.createSignatureForVerify(pubKeyFile);
    }

    /**
     * 签名.
     *
     * @param cap String 待签名CAP协议字符串
     * @return 签名后的字符串
     * @throws Exception
     */
    @Override
    public synchronized String signatureCAP(String cap) throws Exception {
        try {
            ///// 此处请验证多线程环境下是否需要加上同步处理（zhaocw） /////
            this.sfs.update(cap.getBytes());
            byte[] signedText = this.sfs.sign();
            return StringUtils.replace(cap,"<DigitalSign/>", "<DigitalSign>"
                    + HexUtils.toHexString(signedText) + "</DigitalSign>");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception(ex.getMessage(),ex);
        }
    }

    /**
     * 普通字符串签名
     * @param simpleStr
     * @return
     */
    @Override
    public synchronized String signatureSimple(String simpleStr) throws Exception {
        try {
            this.sfs.update(simpleStr.getBytes());
            byte[] signedText = this.sfs.sign();
            return  HexUtils.toHexString(signedText);
        } catch (Exception ex) {
            throw new Exception(ex.getMessage(),ex);
        }
    }

    /**
     * 验证.
     *
     * @param cap String CAP协议字符串
     * @return boolean 验证是否通过
     */
    @Override
    public synchronized boolean verifyCAP(String cap) throws Exception {
        try {
            String originalText = StringUtils.replace(cap,StringUtils.substring(cap,StringUtils
                    .indexOf(cap,"<DigitalSign>"), StringUtils.indexOf(cap,"</DigitalSign>")
                    + "</DigitalSign>".length()), "<DigitalSign/>");
            String signedText = StringUtils.substring(cap,StringUtils.indexOf(cap,"<DigitalSign>")
                    + "<DigitalSign>".length(), StringUtils.indexOf(cap,"</DigitalSign>"));
            this.sfv.update(originalText.getBytes());
            return this.sfv.verify(HexUtils.fromHexString(signedText));
        } catch (Exception ex) {
            throw new Exception(ex.getMessage(),ex);
        }
    }

    /**
     * 普通字符串验签
     *
     * @param originalText
     * @param signedText
     * @return
     */
    @Override
    public synchronized boolean verifySimple(String originalText, String signedText) throws Exception {
        try{
            this.sfv.update(originalText.getBytes());
            return this.sfv.verify(HexUtils.fromHexString(signedText));
        }catch(Exception e){
            throw new Exception(e.getMessage(),e);
        }
    }

    /**
     * 获得sfs.
     *
     * @return sfs
     */
    public Signature getSfs() {
        return sfs;
    }

    /**
     * 设置sfs.
     *
     * @param sfs 要设置的 sfs
     */
    public void setSfs(Signature sfs) {
        this.sfs = sfs;
    }

    /**
     * 获得sfv.
     *
     * @return sfv
     */
    public Signature getSfv() {
        return sfv;
    }

    /**
     * 设置sfv.
     *
     * @param sfv 要设置的 sfv
     */
    public void setSfv(Signature sfv) {
        this.sfv = sfv;
    }


    public static void main(String[] args) throws Exception {
        DefaultCAPSigner s = new DefaultCAPSigner("12345678", "ydsc", "D:/ydsc.keystore", "D:/ydsc.cer");
        System.out.println(s.verifyCAP("<?xml version=\"1.0\" encoding=\"UTF-8\"?><CAPRoot><SessionHeader><ServiceCode>CAP01001</ServiceCode><Version>2009062411025800</Version><ActionCode>0</ActionCode><TransactionID>11062201307124501978113</TransactionID><SrcSysID>11062</SrcSysID><DstSysID>11000</DstSysID><ReqTime>20130712214951</ReqTime><DigitalSign>302C021433BD45D34D4970225B2AF23913BE49CE6534AC6102142E77E0F300400619664C8ADF6D3C966A3343F10A</DigitalSign></SessionHeader><SessionBody><AuthReq><AuthInfo><AccountType>2000004</AccountType><AccountID>15301586546</AccountID><PWDType>01</PWDType><Password>U/niP0i6IU0=</Password></AuthInfo></AuthReq></SessionBody></CAPRoot>"));
        System.out.println(s.signatureCAP("<?xml version=\"1.0\" encoding=\"UTF-8\"?><CAPRoot><SessionHeader><ServiceCode>CAP01001</ServiceCode><Version>2009062411025800</Version><ActionCode>0</ActionCode><TransactionID>11062201307124501978113</TransactionID><SrcSysID>11062</SrcSysID><DstSysID>11000</DstSysID><ReqTime>20130712214951</ReqTime><DigitalSign/></SessionHeader><SessionBody><AuthReq><AuthInfo><AccountType>2000004</AccountType><AccountID>15301586546</AccountID><PWDType>01</PWDType><Password>U/niP0i6IU0=</Password></AuthInfo></AuthReq></SessionBody></CAPRoot>"));
    }
}

