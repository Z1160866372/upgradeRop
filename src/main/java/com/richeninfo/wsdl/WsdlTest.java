/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 *
 */

package com.richeninfo.wsdl;

import com.richeninfo.util.DateUtil;
import com.richeninfo.util.HttpClientTool;
import com.richeninfo.util.RSAUtils;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;

import java.util.Date;

public class WsdlTest {
	public static void main(String[] args) {
		try {
//			String xmlReq="<?xml version=\"1.0\" encoding=\"UTF-8\"?><CARoot><SessionHeader><ActionCode>1</ActionCode><TransactionID>12003201205221624263765372600</TransactionID><RspTime>20120522162426</RspTime><Response><RspType>0</RspType><RspCode>0000</RspCode><RspDesc>success</RspDesc></Response></SessionHeader><SessionBody><AssertionQryRsp><Issuer><NotBefore>2012-05-22 16:24:26</NotBefore><NotOnOrAfter>2012-05-22 16:24:26</NotOnOrAfter></Issuer><UserType>01</UserType><PwdType>01</PwdType><UserInfo><UserName>15850582950</UserName><Province>250</Province><Brand>01</Brand><Status>01</Status><AuthUserID>123123123</AuthUserID><IdentCode>13376750PEkoQhkV</IdentCode><IdentCodeLevel>01</IdentCodeLevel><UID>123456232erfertrtrfgs</UID></UserInfo></AssertionQryRsp></SessionBody></CARoot>";
			/*String url = "http://actest.10086.cn:18080/services/AssertionQryUID";
			String reqTime = DateUtil.convertDateToString(new Date(), "yyyyMMddHHmmss");
			String SrcSysID = "70210";
			StringBuilder xmlParam = new StringBuilder();
			xmlParam.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append("<CARoot>").append("<SessionHeader>")
					.append("<ServiceCode>").append("CA03003").append("</ServiceCode>").append("<Version>")
					.append("CA-2.0").append("</Version>").append("<ActionCode>").append("0").append("</ActionCode>")
					.append("<TransactionID>").append("").append("</TransactionID>").append("<SrcSysID>")
					.append(SrcSysID).append("</SrcSysID>").append("<DstSysID>").append("10000").append("</DstSysID>")
					.append("<ReqTime>").append(reqTime).append("</ReqTime>").append("<DigitalSign/>")
					.append("<Request/>").append("</SessionHeader>").append("<SessionBody>").append("<AssertionQryReq>")
					.append("<UID>").append("").append("</UID>").append("</AssertionQryReq>").append("</SessionBody>")
					.append("</CARoot>");
			String xmlReq = xmlParam.toString();
			String xmlRequst = HttpClientTool.EncodeString(xmlReq, "00210");
			JaxWsDynamicClientFactory clientFactory = JaxWsDynamicClientFactory.newInstance();
	        Client client = clientFactory.createClient(url);
	        client.getEndpoint().getEndpointInfo().setAddress(url);
	        Object[] result = client.invoke("getAssertInfoByUID", xmlRequst);
	        String outxl = (String)result[0];
	        System.out.println(outxl);
			String xml = "<CARoot><SessionHeader><ActionCode>1</ActionCode><TransactionID>12003201205221624263765372600</TransactionID><RspTime>20120522162426</RspTime><Response><RspType>0</RspType><RspCode>0000</RspCode><RspDesc>success</RspDesc></Response></SessionHeader><SessionBody><AssertionQryRsp><Issuer><NotBefore>2012-05-22 16:24:26</NotBefore><NotOnOrAfter>2012-05-22 16:24:26</NotOnOrAfter></Issuer><UserType>01</UserType><PwdType>01</PwdType><UserInfo><UserName>15850582950</UserName><Province>250</Province><Brand>01</Brand><Status>01</Status><AuthUserID>123123123</AuthUserID><IdentCode>13376750PEkoQhkV</IdentCode><IdentCodeLevel>01</IdentCodeLevel><UID>123456232erfertrtrfgs</UID></UserInfo></AssertionQryRsp></SessionBody></CARoot>";
//			String actionCode = WinXMLUtils.getNodeValue(xml, "/CARoot/SessionHeader/ActionCode");
			//if  list（有多个同样节点）
//			NodeList nodeList = WinXMLUtils.getNodeList(xml, "/CARoot/SessionBody/AssertionQryRsp");
//			for(int i=1;i<=nodeList.getLength();i++){
//				String notBefore = WinXMLUtils.getNodeValue(xml, "/CARoot/SessionBody/AssertionQryRsp["+i+"]/UserInfo/UserName");
//			}
			//if obj(没有多个同样节点)
			String notBefore = WinXMLUtils.getNodeValue(xml, "/CARoot/SessionBody/AssertionQryRsp/UserInfo/UserName");
			String RspCode = WinXMLUtils.getNodeValue(xml, "/CARoot/SessionHeader/Response/RspCode");
			String RspDesc = WinXMLUtils.getNodeValue(xml, "/CARoot/SessionHeader/Response/RspDesc");
			
			System.out.println(notBefore+"<br>"+RspCode+"<br>"+RspDesc);*/
			String mobilePhone="akDjy96GsKM0XEZMBt48eI2mpjjTmDxte4Fn0BDqUQPR6EFyDdKejyR2OBDezvD1 V2sSFjZLD5VRnpBsWIVNvJk4omxYxDuXQV2RLqbakVb6K4rv6c4pVqsMKmqcfhyG1lVDGDzVe2xJaMUZtJyt ffzMi3AzIX6sVFKK8Fra8=";
			System.out.println(RSAUtils.decryptByPriKey(mobilePhone).trim());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
