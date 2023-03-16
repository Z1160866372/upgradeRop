package com.richeninfo.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.richeninfo.entity.mapper.entity.OpenapiLog;
import com.richeninfo.entity.mapper.mapper.master.CommonMapper;
import com.richeninfo.pojo.OpcProperties;
import com.richeninfo.pojo.Packet;
import com.richeninfo.pojo.WsMessage;
import com.richeninfo.wsdl.Security;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.util.Date;

/**
 * @Author : zhouxiaohu
 * @create 2022/9/19 10:29
 */
@Slf4j
@Component
public class RopServiceManager {
    @Resource
    private  OpcProperties opcProperties;
    @Value("${nengKai.appCode}")
    private  String appCode;
    @Value("${nengKai.apk_new}")
    private  String apk_new;
    @Resource
    private  HttpServletRequest request;
    @Resource
    private  CommonMapper commonMapper;
    @Resource
    private OpenapiHttpCilent openapiHttpCilent;
    public  String execute(Packet reqPack, String userId) throws Exception {
        String response = "";
        try {
            log.info("appCode:"+appCode);
            log.info("apk:"+apk_new);
            String message = JSON.toJSONString(reqPack.getPost());
            log.info("Request:\n" + message);
           // OpenapiHttpCilent client = new OpenapiHttpCilent(appCode, apk_new);
            response = openapiHttpCilent.call(reqPack.getApiCode(),reqPack.getPost().getPubInfo().getTransactionId(),message);
            log.info("Response("+reqPack.getPost().getRequest().getBusiCode()+"|"+reqPack.getPost().getRequest().getBusiParams().getString("billId")+"):\n" + response);
            String status = JSON.parseObject(response).getString("status");
            saveOpenapiLog(reqPack,message,response,userId);//保存用户调用记录
            if(!"SUCCESS".equals(status)){
                throw new RuntimeException(JSON.parseObject(response).getString("result"));
            }
        } catch (Exception e) {
            StringBuffer message = new StringBuffer();
            if (e != null) {
                message.append(e.getClass()).append(": ").append(e.getMessage()).append("\n");
                StackTraceElement[] elements = e.getStackTrace();
                for (StackTraceElement stackTraceElement : elements) {
                    message.append("\t").append(stackTraceElement.toString()).append("\n");
                }
            }
            log.error("Exception : " + message.toString());
            throw e;
        }
        return JSON.parseObject(response).getString("result");
    }

    public JSONObject executes(Packet reqPack,String userId) throws Exception {
        String response = "";
        try {
            String message = reqPack.getPost().getRequest().getBusiParams().toString();
            log.info("card Request:" + message);
           // OpenapiHttpCilent client = new OpenapiHttpCilent(appCode, apk_new);
            response = openapiHttpCilent.call(reqPack.getApiCode(),null,message);
            log.info("card Response " + response);
            saveOpenapiLog(reqPack,message,response,userId);//保存用户调用记录
        } catch (Exception e) {
            log.error("Exception : " + e.getMessage());
            throw e;
        }
        return JSON.parseObject(response);
    }
    //接口调用日志
    public  void saveOpenapiLog(Packet reqPack, String message, String response, String userId){
        OpenapiLog log = new OpenapiLog();
        log.setAddress(request.getLocalAddr()+":"+request.getLocalPort());
        log.setAppCode(appCode);
        log.setApiCode(reqPack.getApiCode());
        log.setCode(message);
        log.setMessage(response);
        log.setUserId(userId);
        commonMapper.insertOpenapiLog(log);
    }

    public static String validServiceNum(String serviceNum, String token) {
        String APP_VALIDSERVICENUM_URL = "https://professorhe.sh.chinamobile.com:18443/datau/datau/thirdpartKeysOthers.du?serviceNum=${SERVICE_NUM}&token=${TOKEN}";
        String url = APP_VALIDSERVICENUM_URL.replace("${SERVICE_NUM}", serviceNum).replace("${TOKEN}", token);
        log.info("接口[validServiceNum]url===" + url);

        String userId = CommonUtil.decryptFromAES(CommonUtil .parseHexStr2Byte(serviceNum),
                CommonUtil.DATAU_THIRDPART_PWD_KEY);
        log.info("接口[validServiceNum]入参===[" + userId + "]" + serviceNum + "," + token);
        String rep = HttpClientTool.doGet(url, null);
        log.info("接口[validServiceNum]出参===" + rep);
        return rep;
    }

    public static WsMessage sendPush(String transactionID, String UID) throws Exception {
        try {
            String url = "http://login.10086.cn:18080/services/AssertionQryUID?wsdl";
            String reqTime = DateUtil.convertDateToString(new Date(), "yyyyMMddHHmmss");
            String SrcSysID = "70210";
            StringBuilder xmlParam = new StringBuilder();
            xmlParam.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append("<CARoot>").append("<SessionHeader>")
                    .append("<ServiceCode>").append("CA03003").append("</ServiceCode>").append("<Version>")
                    .append("CA-2.0").append("</Version>").append("<ActionCode>").append("0").append("</ActionCode>")
                    .append("<TransactionID>").append(transactionID).append("</TransactionID>").append("<SrcSysID>")
                    .append(SrcSysID).append("</SrcSysID>").append("<DstSysID>").append("10000").append("</DstSysID>")
                    .append("<ReqTime>").append(reqTime).append("</ReqTime>").append("<DigitalSign/>")
                    .append("<Request/>").append("</SessionHeader>").append("<SessionBody>").append("<AssertionQryReq>")
                    .append("<UID>").append(UID).append("</UID>").append("</AssertionQryReq>").append("</SessionBody>")
                    .append("</CARoot>");
            String xmlReq = xmlParam.toString();
            String xmlRequst = HttpClientTool.EncodeString(xmlReq, "00210");
            log.info("接口[sendPush]url===" + url);
            log.info("接口[sendPush]入参===" + xmlRequst);
            JaxWsDynamicClientFactory clientFactory = JaxWsDynamicClientFactory.newInstance();
            Client client = clientFactory.createClient(url);
            client.getEndpoint().getEndpointInfo().setAddress("http://login.10086.cn:18080/services/AssertionQryUID");
            Object[] result = client.invoke("getAssertInfoByUID", xmlRequst);
            String outxml = (String)result[0];
            log.info("接口[sendPush]出参===" +outxml);
            String c = Security.getDecryptString(outxml);
            log.info("接口[sendPush]解密后的返回报文===" + c);
            String UserName = CommonUtil.getNodeValue(c, "/CARoot/SessionBody/AssertionQryRsp/UserInfo/UserName");
            String RspCode = CommonUtil.getNodeValue(c, "/CARoot/SessionHeader/Response/RspCode");
            String RspDesc = CommonUtil.getNodeValue(c, "/CARoot/SessionHeader/Response/RspDesc");
            System.out.println(UserName);
            WsMessage msgObj=new WsMessage();
            msgObj.setUserId(UserName);
            msgObj.setRspCode(RspCode);
            msgObj.setRspDesc(RspDesc);
            return msgObj;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;

    }
}
