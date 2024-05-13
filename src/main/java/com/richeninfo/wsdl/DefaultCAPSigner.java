/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 */
package com.richeninfo.wsdl;

import com.richeninfo.util.ICAPSigner;
import org.apache.commons.lang.StringUtils;
import com.richeninfo.util.HexUtils;
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
    public synchronized String signatureCAP(String cap) throws SignException {
        try {
            ///// 此处请验证多线程环境下是否需要加上同步处理（zhaocw） /////
            this.sfs.update(cap.getBytes());
            byte[] signedText = this.sfs.sign();
            return StringUtils.replace(cap,"<DigitalSign/>", "<DigitalSign>"
                    + HexUtils.toHexString(signedText) + "</DigitalSign>");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new SignException(SignException.TYPE_SIG,ex.getMessage(),ex);
        }
    }

    /**
     * 普通字符串签名
     * @param simpleStr
     * @return
     * @throws SignException
     */
    public synchronized String signatureSimple(String simpleStr) throws SignException {
        try {
            this.sfs.update(simpleStr.getBytes());
            byte[] signedText = this.sfs.sign();
            return  HexUtils.toHexString(signedText);
        } catch (Exception ex) {
            throw new SignException(SignException.TYPE_SIG,ex.getMessage(),ex);
        }
    }

    /**
     * 验证.
     *
     * @param cap String CAP协议字符串
     * @return boolean 验证是否通过
     */
    public synchronized boolean verifyCAP(String cap) throws SignException {
        try {
            String originalText = StringUtils.replace(cap,StringUtils.substring(cap,StringUtils
                    .indexOf(cap,"<DigitalSign>"), StringUtils.indexOf(cap,"</DigitalSign>")
                    + "</DigitalSign>".length()), "<DigitalSign/>");
            String signedText = StringUtils.substring(cap,StringUtils.indexOf(cap,"<DigitalSign>")
                    + "<DigitalSign>".length(), StringUtils.indexOf(cap,"</DigitalSign>"));
            this.sfv.update(originalText.getBytes());
            return this.sfv.verify(HexUtils.fromHexString(signedText));
        } catch (Exception ex) {
            throw new SignException(SignException.TYPE_VRF,ex.getMessage(),ex);
        }
    }

    /**
     * 普通字符串验签
     *
     * @param originalText
     * @param signedText
     * @return
     * @throws SignException
     */
    public synchronized boolean verifySimple(String originalText, String signedText) throws SignException {
        try{
            this.sfv.update(originalText.getBytes());
            return this.sfv.verify(HexUtils.fromHexString(signedText));
        }catch(Exception e){
            throw new SignException(SignException.TYPE_VRF,e.getMessage(),e);
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


    public static void main(String[] args) throws SignException {
        DefaultCAPSigner s = new DefaultCAPSigner("123456", "00210", "/static/ydsc.keystore", "/static/00210.cer");
        System.out.println(s.verifyCAP("<?xml version=\"1.0\" encoding=\"UTF-8\"?><CARoot><SessionHeader><ServiceCode>CA03003</ServiceCode><Version>CA-2.0</Version><ActionCode>0</ActionCode><TransactionID>12003201205221624261113765372600</TransactionID><SrcSysID>70210</SrcSysID><DstSysID>10000</DstSysID><ReqTime>20240513100254</ReqTime><DigitalSign>302d0215008cfe2e5f52f390647a5ad572f97114533286497e02141c1b66518424b2f80ece9c8e31a2a8391dc1e394</DigitalSign><Request/></SessionHeader><SessionBody><AssertionQryReq><UID>K3O3FKxV3vnyOmV+UaMvVqSAU8lfFdw05/pT68YYukoZa6TLVls3b2kezlQgcbRNX9r624Q196kLLpU5aPJkaAYiV7mzUD8Svit/hSfNBQH6OpnjtKyxEMEm/qnbjv+48ZAcSu1+2A84cDszwfcS1ocgg9zHWcgxYysBat53fHU=</UID></AssertionQryReq></SessionBody></CARoot>"));
        System.out.println(s.signatureCAP("<?xml version=\"1.0\" encoding=\"UTF-8\"?><CARoot><SessionHeader><ServiceCode>CA03003</ServiceCode><Version>CA-2.0</Version><ActionCode>0</ActionCode><TransactionID>12003201205221624261113765372600</TransactionID><SrcSysID>70210</SrcSysID><DstSysID>10000</DstSysID><ReqTime>20240513100254</ReqTime><DigitalSign>302d0215008cfe2e5f52f390647a5ad572f97114533286497e02141c1b66518424b2f80ece9c8e31a2a8391dc1e394</DigitalSign><Request/></SessionHeader><SessionBody><AssertionQryReq><UID>K3O3FKxV3vnyOmV+UaMvVqSAU8lfFdw05/pT68YYukoZa6TLVls3b2kezlQgcbRNX9r624Q196kLLpU5aPJkaAYiV7mzUD8Svit/hSfNBQH6OpnjtKyxEMEm/qnbjv+48ZAcSu1+2A84cDszwfcS1ocgg9zHWcgxYysBat53fHU=</UID></AssertionQryReq></SessionBody></CARoot>"));
    }
}

