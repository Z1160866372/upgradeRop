package com.richeninfo.wsdl;

import java.io.ByteArrayInputStream;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.dom4j.io.SAXReader;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author YUTAO 下午4:20:30 2017年4月11日
 * 
 */
public class WinXMLUtils {
	public static void main(String[] args) {
//		String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"><soap:Body><CallESBResponse xmlns=\"http://esb.shca.org/\"><CallESBResult>&lt;response&gt; &lt;resultCode&gt;0&lt;/resultCode&gt; &lt;resultMessage&gt;&lt;/resultMessage&gt; &lt;result&gt; &lt;item&gt; &lt;ksdm&gt;007&lt;/ksdm&gt; &lt;ksmc&gt;急诊观察&lt;/ksmc&gt; &lt;ksjj&gt;&lt;/ksjj&gt; &lt;py&gt;jzgc&lt;/py&gt; &lt;wb&gt;qycp&lt;/wb&gt; &lt;ghf&gt;6.0000&lt;/ghf&gt; &lt;zlf&gt;18.0000&lt;/zlf&gt; &lt;zzlx&gt;&lt;/zzlx&gt; &lt;zzlxmc&gt;&lt;/zzlxmc&gt; &lt;czlx&gt;0&lt;/czlx&gt; &lt;czlxmc&gt;普通&lt;/czlxmc&gt; &lt;jzdz&gt;&lt;/jzdz&gt; &lt;/item&gt; &lt;item&gt; &lt;ksdm&gt;305&lt;/ksdm&gt; &lt;ksmc&gt;中西医结合科&lt;/ksmc&gt; &lt;ksjj&gt;&lt;/ksjj&gt; &lt;py&gt;zxyjhk&lt;/py&gt; &lt;wb&gt;ksaxwt&lt;/wb&gt; &lt;ghf&gt;6.0000&lt;/ghf&gt; &lt;zlf&gt;16.0000&lt;/zlf&gt; &lt;zzlx&gt;&lt;/zzlx&gt; &lt;zzlxmc&gt;&lt;/zzlxmc&gt; &lt;czlx&gt;0&lt;/czlx&gt; &lt;czlxmc&gt;普通&lt;/czlxmc&gt; &lt;jzdz&gt;&lt;/jzdz&gt; &lt;/item&gt; &lt;item&gt; &lt;ksdm&gt;305&lt;/ksdm&gt; &lt;ksmc&gt;中西医结合科&lt;/ksmc&gt; &lt;ksjj&gt;&lt;/ksjj&gt; &lt;py&gt;zxyjhk&lt;/py&gt; &lt;wb&gt;ksaxwt&lt;/wb&gt; &lt;ghf&gt;6.0000&lt;/ghf&gt; &lt;zlf&gt;16.0000&lt;/zlf&gt; &lt;zzlx&gt;&lt;/zzlx&gt; &lt;zzlxmc&gt;&lt;/zzlxmc&gt; &lt;czlx&gt;0&lt;/czlx&gt; &lt;czlxmc&gt;普通&lt;/czlxmc&gt; &lt;jzdz&gt;中西医结合科&lt;/jzdz&gt; &lt;/item&gt; &lt;item&gt; &lt;ksdm&gt;309&lt;/ksdm&gt; &lt;ksmc&gt;针灸&lt;/ksmc&gt; &lt;ksjj&gt;&lt;/ksjj&gt; &lt;py&gt;zj&lt;/py&gt; &lt;wb&gt;qq&lt;/wb&gt; &lt;ghf&gt;6.0000&lt;/ghf&gt; &lt;zlf&gt;16.0000&lt;/zlf&gt; &lt;zzlx&gt;&lt;/zzlx&gt; &lt;zzlxmc&gt;&lt;/zzlxmc&gt; &lt;czlx&gt;0&lt;/czlx&gt; &lt;czlxmc&gt;普通&lt;/czlxmc&gt; &lt;jzdz&gt;&lt;/jzdz&gt; &lt;/item&gt; &lt;item&gt; &lt;ksdm&gt;317&lt;/ksdm&gt; &lt;ksmc&gt;保健科(本院)&lt;/ksmc&gt; &lt;ksjj&gt;&lt;/ksjj&gt; &lt;py&gt;bjkby&lt;/py&gt; &lt;wb&gt;wwtsb&lt;/wb&gt; &lt;ghf&gt;6.0000&lt;/ghf&gt; &lt;zlf&gt;16.0000&lt;/zlf&gt; &lt;zzlx&gt;&lt;/zzlx&gt; &lt;zzlxmc&gt;&lt;/zzlxmc&gt; &lt;czlx&gt;0&lt;/czlx&gt; &lt;czlxmc&gt;普通&lt;/czlxmc&gt; &lt;jzdz&gt;&lt;/jzdz&gt; &lt;/item&gt; &lt;item&gt; &lt;ksdm&gt;318&lt;/ksdm&gt; &lt;ksmc&gt;核医学&lt;/ksmc&gt; &lt;ksjj&gt;&lt;/ksjj&gt; &lt;py&gt;hyx&lt;/py&gt; &lt;wb&gt;sai&lt;/wb&gt; &lt;ghf&gt;6.0000&lt;/ghf&gt; &lt;zlf&gt;16.0000&lt;/zlf&gt; &lt;zzlx&gt;&lt;/zzlx&gt; &lt;zzlxmc&gt;&lt;/zzlxmc&gt; &lt;czlx&gt;0&lt;/czlx&gt; &lt;czlxmc&gt;普通&lt;/czlxmc&gt; &lt;jzdz&gt;&lt;/jzdz&gt; &lt;/item&gt; &lt;item&gt; &lt;ksdm&gt;324&lt;/ksdm&gt; &lt;ksmc&gt;肝脏外科&lt;/ksmc&gt; &lt;ksjj&gt;&lt;/ksjj&gt; &lt;py&gt;gzwk&lt;/py&gt; &lt;wb&gt;eeqt&lt;/wb&gt; &lt;ghf&gt;6.0000&lt;/ghf&gt; &lt;zlf&gt;16.0000&lt;/zlf&gt; &lt;zzlx&gt;&lt;/zzlx&gt; &lt;zzlxmc&gt;&lt;/zzlxmc&gt; &lt;czlx&gt;0&lt;/czlx&gt; &lt;czlxmc&gt;普通&lt;/czlxmc&gt; &lt;jzdz&gt;&lt;/jzdz&gt; &lt;/item&gt; &lt;item&gt; &lt;ksdm&gt;391&lt;/ksdm&gt; &lt;ksmc&gt;PICC维护(上午)&lt;/ksmc&gt; &lt;ksjj&gt;&lt;/ksjj&gt; &lt;py&gt;piccwhsw&lt;/py&gt; &lt;wb&gt;piccxrht&lt;/wb&gt; &lt;ghf&gt;6.0000&lt;/ghf&gt; &lt;zlf&gt;16.0000&lt;/zlf&gt; &lt;zzlx&gt;&lt;/zzlx&gt; &lt;zzlxmc&gt;&lt;/zzlxmc&gt; &lt;czlx&gt;0&lt;/czlx&gt; &lt;czlxmc&gt;普通&lt;/czlxmc&gt; &lt;jzdz&gt;PICC维护(上午)&lt;/jzdz&gt; &lt;/item&gt; &lt;item&gt; &lt;ksdm&gt;392&lt;/ksdm&gt; &lt;ksmc&gt;PICC维护(下午)&lt;/ksmc&gt; &lt;ksjj&gt;&lt;/ksjj&gt; &lt;py&gt;piccwhxw&lt;/py&gt; &lt;wb&gt;piccxrgt&lt;/wb&gt; &lt;ghf&gt;6.0000&lt;/ghf&gt; &lt;zlf&gt;16.0000&lt;/zlf&gt; &lt;zzlx&gt;&lt;/zzlx&gt; &lt;zzlxmc&gt;&lt;/zzlxmc&gt; &lt;czlx&gt;0&lt;/czlx&gt; &lt;czlxmc&gt;普通&lt;/czlxmc&gt; &lt;jzdz&gt;PICC维护(下午)&lt;/jzdz&gt; &lt;/item&gt; &lt;item&gt; &lt;ksdm&gt;393&lt;/ksdm&gt; &lt;ksmc&gt;PICC置管&lt;/ksmc&gt; &lt;ksjj&gt;&lt;/ksjj&gt; &lt;py&gt;picczg&lt;/py&gt; &lt;wb&gt;picclt&lt;/wb&gt; &lt;ghf&gt;6.0000&lt;/ghf&gt; &lt;zlf&gt;16.0000&lt;/zlf&gt; &lt;zzlx&gt;&lt;/zzlx&gt; &lt;zzlxmc&gt;&lt;/zzlxmc&gt; &lt;czlx&gt;0&lt;/czlx&gt; &lt;czlxmc&gt;普通&lt;/czlxmc&gt; &lt;jzdz&gt;&lt;/jzdz&gt; &lt;/item&gt; &lt;item&gt; &lt;ksdm&gt;501&lt;/ksdm&gt; &lt;ksmc&gt;头颈部肿瘤&lt;/ksmc&gt; &lt;ksjj&gt;&lt;/ksjj&gt; &lt;py&gt;tjbzl&lt;/py&gt; &lt;wb&gt;ucueu&lt;/wb&gt; &lt;ghf&gt;6.0000&lt;/ghf&gt; &lt;zlf&gt;16.0000&lt;/zlf&gt; &lt;zzlx&gt;&lt;/zzlx&gt; &lt;zzlxmc&gt;&lt;/zzlxmc&gt; &lt;czlx&gt;0&lt;/czlx&gt; &lt;czlxmc&gt;普通&lt;/czlxmc&gt; &lt;jzdz&gt;头颈部肿瘤&lt;/jzdz&gt; &lt;/item&gt; &lt;item&gt; &lt;ksdm&gt;502&lt;/ksdm&gt; &lt;ksmc&gt;胸部肿瘤&lt;/ksmc&gt; &lt;ksjj&gt;&lt;/ksjj&gt; &lt;py&gt;xbzl&lt;/py&gt; &lt;wb&gt;eueu&lt;/wb&gt; &lt;ghf&gt;6.0000&lt;/ghf&gt; &lt;zlf&gt;16.0000&lt;/zlf&gt; &lt;zzlx&gt;&lt;/zzlx&gt; &lt;zzlxmc&gt;&lt;/zzlxmc&gt; &lt;czlx&gt;0&lt;/czlx&gt; &lt;czlxmc&gt;普通&lt;/czlxmc&gt; &lt;jzdz&gt;&lt;/jzdz&gt; &lt;/item&gt; &lt;item&gt; &lt;ksdm&gt;502&lt;/ksdm&gt; &lt;ksmc&gt;胸部肿瘤&lt;/ksmc&gt; &lt;ksjj&gt;&lt;/ksjj&gt; &lt;py&gt;xbzl&lt;/py&gt; &lt;wb&gt;eueu&lt;/wb&gt; &lt;ghf&gt;6.0000&lt;/ghf&gt; &lt;zlf&gt;16.0000&lt;/zlf&gt; &lt;zzlx&gt;&lt;/zzlx&gt; &lt;zzlxmc&gt;&lt;/zzlxmc&gt; &lt;czlx&gt;0&lt;/czlx&gt; &lt;czlxmc&gt;普通&lt;/czlxmc&gt; &lt;jzdz&gt;胸部肿瘤&lt;/jzdz&gt; &lt;/item&gt; &lt;item&gt; &lt;ksdm&gt;503&lt;/ksdm&gt; &lt;ksmc&gt;乳腺肿瘤&lt;/ksmc&gt; &lt;ksjj&gt;&lt;/ksjj&gt; &lt;py&gt;rxzl&lt;/py&gt; &lt;wb&gt;eeeu&lt;/wb&gt; &lt;ghf&gt;6.0000&lt;/ghf&gt; &lt;zlf&gt;16.0000&lt;/zlf&gt; &lt;zzlx&gt;&lt;/zzlx&gt; &lt;zzlxmc&gt;&lt;/zzlxmc&gt; &lt;czlx&gt;0&lt;/czlx&gt; &lt;czlxmc&gt;普通&lt;/czlxmc&gt; &lt;jzdz&gt;乳腺肿瘤&lt;/jzdz&gt; &lt;/item&gt; &lt;item&gt; &lt;ksdm&gt;504&lt;/ksdm&gt; &lt;ksmc&gt;大肠肿瘤&lt;/ksmc&gt; &lt;ksjj&gt;&lt;/ksjj&gt; &lt;py&gt;dczl&lt;/py&gt; &lt;wb&gt;deeu&lt;/wb&gt; &lt;ghf&gt;6.0000&lt;/ghf&gt; &lt;zlf&gt;16.0000&lt;/zlf&gt; &lt;zzlx&gt;&lt;/zzlx&gt; &lt;zzlxmc&gt;&lt;/zzlxmc&gt; &lt;czlx&gt;0&lt;/czlx&gt; &lt;czlxmc&gt;普通&lt;/czlxmc&gt; &lt;jzdz&gt;&lt;/jzdz&gt; &lt;/item&gt; &lt;item&gt; &lt;ksdm&gt;504&lt;/ksdm&gt; &lt;ksmc&gt;大肠肿瘤&lt;/ksmc&gt; &lt;ksjj&gt;&lt;/ksjj&gt; &lt;py&gt;dczl&lt;/py&gt; &lt;wb&gt;deeu&lt;/wb&gt; &lt;ghf&gt;6.0000&lt;/ghf&gt; &lt;zlf&gt;16.0000&lt;/zlf&gt; &lt;zzlx&gt;&lt;/zzlx&gt; &lt;zzlxmc&gt;&lt;/zzlxmc&gt; &lt;czlx&gt;0&lt;/czlx&gt; &lt;czlxmc&gt;普通&lt;/czlxmc&gt; &lt;jzdz&gt;大肠肿瘤&lt;/jzdz&gt; &lt;/item&gt; &lt;item&gt; &lt;ksdm&gt;505&lt;/ksdm&gt; &lt;ksmc&gt;肝胆胰肿瘤&lt;/ksmc&gt; &lt;ksjj&gt;&lt;/ksjj&gt; &lt;py&gt;gdyzl&lt;/py&gt; &lt;wb&gt;eeeeu&lt;/wb&gt; &lt;ghf&gt;6.0000&lt;/ghf&gt; &lt;zlf&gt;16.0000&lt;/zlf&gt; &lt;zzlx&gt;&lt;/zzlx&gt; &lt;zzlxmc&gt;&lt;/zzlxmc&gt; &lt;czlx&gt;0&lt;/czlx&gt; &lt;czlxmc&gt;普通&lt;/czlxmc&gt; &lt;jzdz&gt;&lt;/jzdz&gt; &lt;/item&gt; &lt;item&gt; &lt;ksdm&gt;505&lt;/ksdm&gt; &lt;ksmc&gt;肝胆胰肿瘤&lt;/ksmc&gt; &lt;ksjj&gt;&lt;/ksjj&gt; &lt;py&gt;gdyzl&lt;/py&gt; &lt;wb&gt;eeeeu&lt;/wb&gt; &lt;ghf&gt;6.0000&lt;/ghf&gt; &lt;zlf&gt;16.0000&lt;/zlf&gt; &lt;zzlx&gt;&lt;/zzlx&gt; &lt;zzlxmc&gt;&lt;/zzlxmc&gt; &lt;czlx&gt;0&lt;/czlx&gt; &lt;czlxmc&gt;普通&lt;/czlxmc&gt; &lt;jzdz&gt;肝胆胰肿瘤&lt;/jzdz&gt; &lt;/item&gt; &lt;item&gt; &lt;ksdm&gt;507&lt;/ksdm&gt; &lt;ksmc&gt;泌尿系统肿瘤&lt;/ksmc&gt; &lt;ksjj&gt;&lt;/ksjj&gt; &lt;py&gt;mnxtzl&lt;/py&gt; &lt;wb&gt;intxeu&lt;/wb&gt; &lt;ghf&gt;6.0000&lt;/ghf&gt; &lt;zlf&gt;16.0000&lt;/zlf&gt; &lt;zzlx&gt;&lt;/zzlx&gt; &lt;zzlxmc&gt;&lt;/zzlxmc&gt; &lt;czlx&gt;0&lt;/czlx&gt; &lt;czlxmc&gt;普通&lt;/czlxmc&gt; &lt;jzdz&gt;&lt;/jzdz&gt; &lt;/item&gt; &lt;item&gt; &lt;ksdm&gt;507&lt;/ksdm&gt; &lt;ksmc&gt;泌尿系统肿瘤&lt;/ksmc&gt; &lt;ksjj&gt;&lt;/ksjj&gt; &lt;py&gt;mnxtzl&lt;/py&gt; &lt;wb&gt;intxeu&lt;/wb&gt; &lt;ghf&gt;6.0000&lt;/ghf&gt; &lt;zlf&gt;16.0000&lt;/zlf&gt; &lt;zzlx&gt;&lt;/zzlx&gt; &lt;zzlxmc&gt;&lt;/zzlxmc&gt; &lt;czlx&gt;0&lt;/czlx&gt; &lt;czlxmc&gt;普通&lt;/czlxmc&gt; &lt;jzdz&gt;泌尿系统肿瘤&lt;/jzdz&gt; &lt;/item&gt; &lt;item&gt; &lt;ksdm&gt;508&lt;/ksdm&gt; &lt;ksmc&gt;妇科肿瘤&lt;/ksmc&gt; &lt;ksjj&gt;&lt;/ksjj&gt; &lt;py&gt;fkzl&lt;/py&gt; &lt;wb&gt;vteu&lt;/wb&gt; &lt;ghf&gt;6.0000&lt;/ghf&gt; &lt;zlf&gt;16.0000&lt;/zlf&gt; &lt;zzlx&gt;&lt;/zzlx&gt; &lt;zzlxmc&gt;&lt;/zzlxmc&gt; &lt;czlx&gt;0&lt;/czlx&gt; &lt;czlxmc&gt;普通&lt;/czlxmc&gt; &lt;jzdz&gt;&lt;/jzdz&gt; &lt;/item&gt; &lt;item&gt; &lt;ksdm&gt;508&lt;/ksdm&gt; &lt;ksmc&gt;妇科肿瘤&lt;/ksmc&gt; &lt;ksjj&gt;&lt;/ksjj&gt; &lt;py&gt;fkzl&lt;/py&gt; &lt;wb&gt;vteu&lt;/wb&gt; &lt;ghf&gt;6.0000&lt;/ghf&gt; &lt;zlf&gt;16.0000&lt;/zlf&gt; &lt;zzlx&gt;&lt;/zzlx&gt; &lt;zzlxmc&gt;&lt;/zzlxmc&gt; &lt;czlx&gt;0&lt;/czlx&gt; &lt;czlxmc&gt;普通&lt;/czlxmc&gt; &lt;jzdz&gt;妇科肿瘤&lt;/jzdz&gt; &lt;/item&gt; &lt;item&gt; &lt;ksdm&gt;509&lt;/ksdm&gt; &lt;ksmc&gt;门诊放射治疗&lt;/ksmc&gt; &lt;ksjj&gt;&lt;/ksjj&gt; &lt;py&gt;mzfszl&lt;/py&gt; &lt;wb&gt;uyytiu&lt;/wb&gt; &lt;ghf&gt;6.0000&lt;/ghf&gt; &lt;zlf&gt;16.0000&lt;/zlf&gt; &lt;zzlx&gt;&lt;/zzlx&gt; &lt;zzlxmc&gt;&lt;/zzlxmc&gt; &lt;czlx&gt;0&lt;/czlx&gt; &lt;czlxmc&gt;普通&lt;/czlxmc&gt; &lt;jzdz&gt;&lt;/jzdz&gt; &lt;/item&gt; &lt;item&gt; &lt;ksdm&gt;510&lt;/ksdm&gt; &lt;ksmc&gt;便民门诊&lt;/ksmc&gt; &lt;ksjj&gt;&lt;/ksjj&gt; &lt;py&gt;bmmz&lt;/py&gt; &lt;wb&gt;wnuy&lt;/wb&gt; &lt;ghf&gt;6.0000&lt;/ghf&gt; &lt;zlf&gt;16.0000&lt;/zlf&gt; &lt;zzlx&gt;&lt;/zzlx&gt; &lt;zzlxmc&gt;&lt;/zzlxmc&gt; &lt;czlx&gt;0&lt;/czlx&gt; &lt;czlxmc&gt;普通&lt;/czlxmc&gt; &lt;jzdz&gt;&lt;/jzdz&gt; &lt;/item&gt; &lt;item&gt; &lt;ksdm&gt;513&lt;/ksdm&gt; &lt;ksmc&gt;妇科门诊放疗&lt;/ksmc&gt; &lt;ksjj&gt;&lt;/ksjj&gt; &lt;py&gt;fkmzfl&lt;/py&gt; &lt;wb&gt;vtuyyu&lt;/wb&gt; &lt;ghf&gt;6.0000&lt;/ghf&gt; &lt;zlf&gt;16.0000&lt;/zlf&gt; &lt;zzlx&gt;&lt;/zzlx&gt; &lt;zzlxmc&gt;&lt;/zzlxmc&gt; &lt;czlx&gt;0&lt;/czlx&gt; &lt;czlxmc&gt;普通&lt;/czlxmc&gt; &lt;jzdz&gt;&lt;/jzdz&gt; &lt;/item&gt; &lt;item&gt; &lt;ksdm&gt;516&lt;/ksdm&gt; &lt;ksmc&gt;胃肿瘤&lt;/ksmc&gt; &lt;ksjj&gt;&lt;/ksjj&gt; &lt;py&gt;wzl&lt;/py&gt; &lt;wb&gt;leu&lt;/wb&gt; &lt;ghf&gt;6.0000&lt;/ghf&gt; &lt;zlf&gt;16.0000&lt;/zlf&gt; &lt;zzlx&gt;&lt;/zzlx&gt; &lt;zzlxmc&gt;&lt;/zzlxmc&gt; &lt;czlx&gt;0&lt;/czlx&gt; &lt;czlxmc&gt;普通&lt;/czlxmc&gt; &lt;jzdz&gt;&lt;/jzdz&gt; &lt;/item&gt; &lt;item&gt; &lt;ksdm&gt;516&lt;/ksdm&gt; &lt;ksmc&gt;胃肿瘤&lt;/ksmc&gt; &lt;ksjj&gt;&lt;/ksjj&gt; &lt;py&gt;wzl&lt;/py&gt; &lt;wb&gt;leu&lt;/wb&gt; &lt;ghf&gt;6.0000&lt;/ghf&gt; &lt;zlf&gt;16.0000&lt;/zlf&gt; &lt;zzlx&gt;&lt;/zzlx&gt; &lt;zzlxmc&gt;&lt;/zzlxmc&gt; &lt;czlx&gt;0&lt;/czlx&gt; &lt;czlxmc&gt;普通&lt;/czlxmc&gt; &lt;jzdz&gt;胃及软组织肿瘤&lt;/jzdz&gt; &lt;/item&gt; &lt;item&gt; &lt;ksdm&gt;517&lt;/ksdm&gt; &lt;ksmc&gt;大肠肿瘤(造口)&lt;/ksmc&gt; &lt;ksjj&gt;&lt;/ksjj&gt; &lt;py&gt;dczlzk&lt;/py&gt; &lt;wb&gt;deeutk&lt;/wb&gt; &lt;ghf&gt;6.0000&lt;/ghf&gt; &lt;zlf&gt;16.0000&lt;/zlf&gt; &lt;zzlx&gt;&lt;/zzlx&gt; &lt;zzlxmc&gt;&lt;/zzlxmc&gt; &lt;czlx&gt;0&lt;/czlx&gt; &lt;czlxmc&gt;普通&lt;/czlxmc&gt; &lt;jzdz&gt;&lt;/jzdz&gt; &lt;/item&gt; &lt;item&gt; &lt;ksdm&gt;519&lt;/ksdm&gt; &lt;ksmc&gt;中西医乳腺肿瘤&lt;/ksmc&gt; &lt;ksjj&gt;&lt;/ksjj&gt; &lt;py&gt;zxyrxzl&lt;/py&gt; &lt;wb&gt;ksaeeeu&lt;/wb&gt; &lt;ghf&gt;6.0000&lt;/ghf&gt; &lt;zlf&gt;16.0000&lt;/zlf&gt; &lt;zzlx&gt;&lt;/zzlx&gt; &lt;zzlxmc&gt;&lt;/zzlxmc&gt; &lt;czlx&gt;0&lt;/czlx&gt; &lt;czlxmc&gt;普通&lt;/czlxmc&gt; &lt;jzdz&gt;&lt;/jzdz&gt; &lt;/item&gt; &lt;item&gt; &lt;ksdm&gt;520&lt;/ksdm&gt; &lt;ksmc&gt;一期临床&lt;/ksmc&gt; &lt;ksjj&gt;&lt;/ksjj&gt; &lt;py&gt;yqlc&lt;/py&gt; &lt;wb&gt;gajy&lt;/wb&gt; &lt;ghf&gt;6.0000&lt;/ghf&gt; &lt;zlf&gt;16.0000&lt;/zlf&gt; &lt;zzlx&gt;&lt;/zzlx&gt; &lt;zzlxmc&gt;&lt;/zzlxmc&gt; &lt;czlx&gt;0&lt;/czlx&gt; &lt;czlxmc&gt;普通&lt;/czlxmc&gt; &lt;jzdz&gt;&lt;/jzdz&gt; &lt;/item&gt; &lt;item&gt; &lt;ksdm&gt;529&lt;/ksdm&gt; &lt;ksmc&gt;淋巴瘤专病门诊&lt;/ksmc&gt; &lt;ksjj&gt;&lt;/ksjj&gt; &lt;py&gt;lblzbmz&lt;/py&gt; &lt;wb&gt;icufuuy&lt;/wb&gt; &lt;ghf&gt;6.0000&lt;/ghf&gt; &lt;zlf&gt;16.0000&lt;/zlf&gt; &lt;zzlx&gt;&lt;/zzlx&gt; &lt;zzlxmc&gt;&lt;/zzlxmc&gt; &lt;czlx&gt;0&lt;/czlx&gt; &lt;czlxmc&gt;普通&lt;/czlxmc&gt; &lt;jzdz&gt;&lt;/jzdz&gt; &lt;/item&gt; &lt;item&gt; &lt;ksdm&gt;529&lt;/ksdm&gt; &lt;ksmc&gt;淋巴瘤专病门诊&lt;/ksmc&gt; &lt;ksjj&gt;&lt;/ksjj&gt; &lt;py&gt;lblzbmz&lt;/py&gt; &lt;wb&gt;icufuuy&lt;/wb&gt; &lt;ghf&gt;6.0000&lt;/ghf&gt; &lt;zlf&gt;16.0000&lt;/zlf&gt; &lt;zzlx&gt;&lt;/zzlx&gt; &lt;zzlxmc&gt;&lt;/zzlxmc&gt; &lt;czlx&gt;0&lt;/czlx&gt; &lt;czlxmc&gt;普通&lt;/czlxmc&gt; &lt;jzdz&gt;淋巴瘤专病门诊&lt;/jzdz&gt; &lt;/item&gt; &lt;item&gt; &lt;ksdm&gt;531&lt;/ksdm&gt; &lt;ksmc&gt;中西医结合肝胆胰腺专科门诊&lt;/ksmc&gt; &lt;ksjj&gt;&lt;/ksjj&gt; &lt;py&gt;zxyjhgdy&lt;/py&gt; &lt;wb&gt;ksaxweee&lt;/wb&gt; &lt;ghf&gt;6.0000&lt;/ghf&gt; &lt;zlf&gt;16.0000&lt;/zlf&gt; &lt;zzlx&gt;&lt;/zzlx&gt; &lt;zzlxmc&gt;&lt;/zzlxmc&gt; &lt;czlx&gt;0&lt;/czlx&gt; &lt;czlxmc&gt;普通&lt;/czlxmc&gt; &lt;jzdz&gt;&lt;/jzdz&gt; &lt;/item&gt; &lt;item&gt; &lt;ksdm&gt;531&lt;/ksdm&gt; &lt;ksmc&gt;中西医结合肝胆胰腺专科门诊&lt;/ksmc&gt; &lt;ksjj&gt;&lt;/ksjj&gt; &lt;py&gt;zxyjhgdy&lt;/py&gt; &lt;wb&gt;ksaxweee&lt;/wb&gt; &lt;ghf&gt;6.0000&lt;/ghf&gt; &lt;zlf&gt;16.0000&lt;/zlf&gt; &lt;zzlx&gt;&lt;/zzlx&gt; &lt;zzlxmc&gt;&lt;/zzlxmc&gt; &lt;czlx&gt;0&lt;/czlx&gt; &lt;czlxmc&gt;普通&lt;/czlxmc&gt; &lt;jzdz&gt;中西医结合肝胆胰腺专科门诊&lt;/jzdz&gt; &lt;/item&gt; &lt;item&gt; &lt;ksdm&gt;532&lt;/ksdm&gt; &lt;ksmc&gt;肿瘤内科胃肠肿瘤&lt;/ksmc&gt; &lt;ksjj&gt;&lt;/ksjj&gt; &lt;py&gt;zlnkwczl&lt;/py&gt; &lt;wb&gt;eumtleeu&lt;/wb&gt; &lt;ghf&gt;6.0000&lt;/ghf&gt; &lt;zlf&gt;16.0000&lt;/zlf&gt; &lt;zzlx&gt;&lt;/zzlx&gt; &lt;zzlxmc&gt;&lt;/zzlxmc&gt; &lt;czlx&gt;0&lt;/czlx&gt; &lt;czlxmc&gt;普通&lt;/czlxmc&gt; &lt;jzdz&gt;&lt;/jzdz&gt; &lt;/item&gt; &lt;item&gt; &lt;ksdm&gt;532&lt;/ksdm&gt; &lt;ksmc&gt;肿瘤内科胃肠肿瘤&lt;/ksmc&gt; &lt;ksjj&gt;&lt;/ksjj&gt; &lt;py&gt;zlnkwczl&lt;/py&gt; &lt;wb&gt;eumtleeu&lt;/wb&gt; &lt;ghf&gt;6.0000&lt;/ghf&gt; &lt;zlf&gt;16.0000&lt;/zlf&gt; &lt;zzlx&gt;&lt;/zzlx&gt; &lt;zzlxmc&gt;&lt;/zzlxmc&gt; &lt;czlx&gt;0&lt;/czlx&gt; &lt;czlxmc&gt;普通&lt;/czlxmc&gt; &lt;jzdz&gt;肿瘤内科胃肠肿瘤&lt;/jzdz&gt; &lt;/item&gt; &lt;item&gt; &lt;ksdm&gt;533&lt;/ksdm&gt; &lt;ksmc&gt;肿瘤内科乳腺肿瘤&lt;/ksmc&gt; &lt;ksjj&gt;&lt;/ksjj&gt; &lt;py&gt;zlnkrxzl&lt;/py&gt; &lt;wb&gt;eumteeeu&lt;/wb&gt; &lt;ghf&gt;6.0000&lt;/ghf&gt; &lt;zlf&gt;16.0000&lt;/zlf&gt; &lt;zzlx&gt;&lt;/zzlx&gt; &lt;zzlxmc&gt;&lt;/zzlxmc&gt; &lt;czlx&gt;0&lt;/czlx&gt; &lt;czlxmc&gt;普通&lt;/czlxmc&gt; &lt;jzdz&gt;&lt;/jzdz&gt; &lt;/item&gt; &lt;item&gt; &lt;ksdm&gt;533&lt;/ksdm&gt; &lt;ksmc&gt;肿瘤内科乳腺肿瘤&lt;/ksmc&gt; &lt;ksjj&gt;&lt;/ksjj&gt; &lt;py&gt;zlnkrxzl&lt;/py&gt; &lt;wb&gt;eumteeeu&lt;/wb&gt; &lt;ghf&gt;6.0000&lt;/ghf&gt; &lt;zlf&gt;16.0000&lt;/zlf&gt; &lt;zzlx&gt;&lt;/zzlx&gt; &lt;zzlxmc&gt;&lt;/zzlxmc&gt; &lt;czlx&gt;0&lt;/czlx&gt; &lt;czlxmc&gt;普通&lt;/czlxmc&gt; &lt;jzdz&gt;肿瘤内科乳腺肿瘤&lt;/jzdz&gt; &lt;/item&gt; &lt;item&gt; &lt;ksdm&gt;534&lt;/ksdm&gt; &lt;ksmc&gt;肿瘤内科胸部肿瘤&lt;/ksmc&gt; &lt;ksjj&gt;&lt;/ksjj&gt; &lt;py&gt;zlnkxbzl&lt;/py&gt; &lt;wb&gt;eumteueu&lt;/wb&gt; &lt;ghf&gt;6.0000&lt;/ghf&gt; &lt;zlf&gt;16.0000&lt;/zlf&gt; &lt;zzlx&gt;&lt;/zzlx&gt; &lt;zzlxmc&gt;&lt;/zzlxmc&gt; &lt;czlx&gt;0&lt;/czlx&gt; &lt;czlxmc&gt;普通&lt;/czlxmc&gt; &lt;jzdz&gt;&lt;/jzdz&gt; &lt;/item&gt; &lt;item&gt; &lt;ksdm&gt;534&lt;/ksdm&gt; &lt;ksmc&gt;肿瘤内科胸部肿瘤&lt;/ksmc&gt; &lt;ksjj&gt;&lt;/ksjj&gt; &lt;py&gt;zlnkxbzl&lt;/py&gt; &lt;wb&gt;eumteueu&lt;/wb&gt; &lt;ghf&gt;6.0000&lt;/ghf&gt; &lt;zlf&gt;16.0000&lt;/zlf&gt; &lt;zzlx&gt;&lt;/zzlx&gt; &lt;zzlxmc&gt;&lt;/zzlxmc&gt; &lt;czlx&gt;0&lt;/czlx&gt; &lt;czlxmc&gt;普通&lt;/czlxmc&gt; &lt;jzdz&gt;肿瘤内科胸部肿瘤&lt;/jzdz&gt; &lt;/item&gt; &lt;item&gt; &lt;ksdm&gt;537&lt;/ksdm&gt; &lt;ksmc&gt;门诊手术&lt;/ksmc&gt; &lt;ksjj&gt;&lt;/ksjj&gt; &lt;py&gt;mzss&lt;/py&gt; &lt;wb&gt;uyrs&lt;/wb&gt; &lt;ghf&gt;6.0000&lt;/ghf&gt; &lt;zlf&gt;16.0000&lt;/zlf&gt; &lt;zzlx&gt;&lt;/zzlx&gt; &lt;zzlxmc&gt;&lt;/zzlxmc&gt; &lt;czlx&gt;0&lt;/czlx&gt; &lt;czlxmc&gt;普通&lt;/czlxmc&gt; &lt;jzdz&gt;&lt;/jzdz&gt; &lt;/item&gt; &lt;item&gt; &lt;ksdm&gt;542&lt;/ksdm&gt; &lt;ksmc&gt;脑脊柱肿瘤&lt;/ksmc&gt; &lt;ksjj&gt;&lt;/ksjj&gt; &lt;py&gt;njzzl&lt;/py&gt; &lt;wb&gt;eiseu&lt;/wb&gt; &lt;ghf&gt;6.0000&lt;/ghf&gt; &lt;zlf&gt;16.0000&lt;/zlf&gt; &lt;zzlx&gt;&lt;/zzlx&gt; &lt;zzlxmc&gt;&lt;/zzlxmc&gt; &lt;czlx&gt;0&lt;/czlx&gt; &lt;czlxmc&gt;普通&lt;/czlxmc&gt; &lt;jzdz&gt;&lt;/jzdz&gt; &lt;/item&gt; &lt;item&gt; &lt;ksdm&gt;543&lt;/ksdm&gt; &lt;ksmc&gt;肿瘤微创治疗(上午专家)&lt;/ksmc&gt; &lt;ksjj&gt;&lt;/ksjj&gt; &lt;py&gt;zlwczlsw&lt;/py&gt; &lt;wb&gt;eutwiuht&lt;/wb&gt; &lt;ghf&gt;14.0000&lt;/ghf&gt; &lt;zlf&gt;16.0000&lt;/zlf&gt; &lt;zzlx&gt;&lt;/zzlx&gt; &lt;zzlxmc&gt;&lt;/zzlxmc&gt; &lt;czlx&gt;0&lt;/czlx&gt; &lt;czlxmc&gt;普通&lt;/czlxmc&gt; &lt;jzdz&gt;肿瘤微创治疗(上午专家)&lt;/jzdz&gt; &lt;/item&gt; &lt;item&gt; &lt;ksdm&gt;544&lt;/ksdm&gt; &lt;ksmc&gt;肿瘤微创治疗(下午专家)&lt;/ksmc&gt; &lt;ksjj&gt;&lt;/ksjj&gt; &lt;py&gt;zlwczlxw&lt;/py&gt; &lt;wb&gt;eutwiugt&lt;/wb&gt; &lt;ghf&gt;14.0000&lt;/ghf&gt; &lt;zlf&gt;16.0000&lt;/zlf&gt; &lt;zzlx&gt;&lt;/zzlx&gt; &lt;zzlxmc&gt;&lt;/zzlxmc&gt; &lt;czlx&gt;0&lt;/czlx&gt; &lt;czlxmc&gt;普通&lt;/czlxmc&gt; &lt;jzdz&gt;肿瘤微创治疗(下午专家)&lt;/jzdz&gt; &lt;/item&gt; &lt;item&gt; &lt;ksdm&gt;545&lt;/ksdm&gt; &lt;ksmc&gt;乳腺癌综合治疗&lt;/ksmc&gt; &lt;ksjj&gt;&lt;/ksjj&gt; &lt;py&gt;rxazhzl&lt;/py&gt; &lt;wb&gt;eeuxwiu&lt;/wb&gt; &lt;ghf&gt;6.0000&lt;/ghf&gt; &lt;zlf&gt;16.0000&lt;/zlf&gt; &lt;zzlx&gt;&lt;/zzlx&gt; &lt;zzlxmc&gt;&lt;/zzlxmc&gt; &lt;czlx&gt;0&lt;/czlx&gt; &lt;czlxmc&gt;普通&lt;/czlxmc&gt; &lt;jzdz&gt;&lt;/jzdz&gt; &lt;/item&gt; &lt;item&gt; &lt;ksdm&gt;549&lt;/ksdm&gt; &lt;ksmc&gt;术前麻醉评估门诊&lt;/ksmc&gt; &lt;ksjj&gt;&lt;/ksjj&gt; &lt;py&gt;sqmzpgmz&lt;/py&gt; &lt;wb&gt;suysywuy&lt;/wb&gt; &lt;ghf&gt;6.0000&lt;/ghf&gt; &lt;zlf&gt;16.0000&lt;/zlf&gt; &lt;zzlx&gt;&lt;/zzlx&gt; &lt;zzlxmc&gt;&lt;/zzlxmc&gt; &lt;czlx&gt;0&lt;/czlx&gt; &lt;czlxmc&gt;普通&lt;/czlxmc&gt; &lt;jzdz&gt;&lt;/jzdz&gt; &lt;/item&gt; &lt;item&gt; &lt;ksdm&gt;552&lt;/ksdm&gt; &lt;ksmc&gt;脊柱及骨转移肿瘤专病&lt;/ksmc&gt; &lt;ksjj&gt;&lt;/ksjj&gt; &lt;py&gt;jzjgzyzl&lt;/py&gt; &lt;wb&gt;isemlteu&lt;/wb&gt; &lt;ghf&gt;6.0000&lt;/ghf&gt; &lt;zlf&gt;16.0000&lt;/zlf&gt; &lt;zzlx&gt;&lt;/zzlx&gt; &lt;zzlxmc&gt;&lt;/zzlxmc&gt; &lt;czlx&gt;0&lt;/czlx&gt; &lt;czlxmc&gt;普通&lt;/czlxmc&gt; &lt;jzdz&gt;&lt;/jzdz&gt; &lt;/item&gt; &lt;item&gt; &lt;ksdm&gt;553&lt;/ksdm&gt; &lt;ksmc&gt;输液港(上午)&lt;/ksmc&gt; &lt;ksjj&gt;&lt;/ksjj&gt; &lt;py&gt;sygsw&lt;/py&gt; &lt;wb&gt;liiht&lt;/wb&gt; &lt;ghf&gt;6.0000&lt;/ghf&gt; &lt;zlf&gt;16.0000&lt;/zlf&gt; &lt;zzlx&gt;&lt;/zzlx&gt; &lt;zzlxmc&gt;&lt;/zzlxmc&gt; &lt;czlx&gt;0&lt;/czlx&gt; &lt;czlxmc&gt;普通&lt;/czlxmc&gt; &lt;jzdz&gt;&lt;/jzdz&gt; &lt;/item&gt; &lt;item&gt; &lt;ksdm&gt;554&lt;/ksdm&gt; &lt;ksmc&gt;输液港(下午)&lt;/ksmc&gt; &lt;ksjj&gt;&lt;/ksjj&gt; &lt;py&gt;sygxw&lt;/py&gt; &lt;wb&gt;liigt&lt;/wb&gt; &lt;ghf&gt;6.0000&lt;/ghf&gt; &lt;zlf&gt;16.0000&lt;/zlf&gt; &lt;zzlx&gt;&lt;/zzlx&gt; &lt;zzlxmc&gt;&lt;/zzlxmc&gt; &lt;czlx&gt;0&lt;/czlx&gt; &lt;czlxmc&gt;普通&lt;/czlxmc&gt; &lt;jzdz&gt;&lt;/jzdz&gt; &lt;/item&gt; &lt;/result&gt; &lt;/response&gt;</CallESBResult></CallESBResponse></soap:Body></soap:Envelope>";
//		String begin = "<CallESBResult>";
//		String end = "</CallESBResult>";
//		String regex = begin + "(.*)" + end;
//		Pattern p = Pattern.compile(regex);
//		Matcher m = p.matcher(xml);
//		if (m.find()) {
//			xml = m.group(1);
//		}
//		System.out.println(xml);
//		xml = xml.replaceAll("&lt;", "<");
//		xml = xml.replaceAll("&gt;", ">");
//		xml = xml.replaceAll("> <", "><");
//		System.out.println(xml);
//
//		System.out.println(WinXMLUtils.getNodeValue(xml, "/response/result/item[2]/ksmc"));

		// System.out.println(WinXMLUtils.getNodeXMLNoRootString(xml, "/soap:Envelope/soap:Body/CallESBResponse"));
		// System.out.println(WinXMLUtils.getNodeXMLString(xml, "/soap:Envelope/soap:Body/CallESBResponse"));
		// System.out.println(WinXMLUtils.getNode(xml, "/soap:Envelope/soap:Body").getTextContent());

	}

	private static Logger logger = Logger.getLogger(WinXMLUtils.class);

	public static String getNodeXMLNoRootString(String outXML, String xpath) {
		if (outXML == null || outXML.equals("")) {
			return null;
		}
		String xmlStr = "";
		try {
			SAXBuilder sb = new SAXBuilder();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			org.jdom2.Document doc = sb.build(new ByteArrayInputStream(outXML.getBytes("UTF-8")));
			Element e = (Element) org.jdom2.xpath.XPath.selectSingleNode(doc, xpath);

			if (e != null) {
				XMLOutputter xmlout = new XMLOutputter(format);
				xmlStr = xmlout.outputString(e.getChildren());
				xmlStr = xmlStr.replaceAll(" ", "");
				xmlStr = xmlStr.replaceAll("\r", "");
				xmlStr = xmlStr.replaceAll("\n", "");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return xmlStr;
	}

	public static String getNodeXMLString(String outXML, String xpath) {
		if (outXML == null || outXML.equals("")) {
			return null;
		}
		String xmlStr = "";
		try {
			SAXBuilder sb = new SAXBuilder();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			org.jdom2.Document doc = sb.build(new ByteArrayInputStream(outXML.getBytes("UTF-8")));
			Element e = (Element) org.jdom2.xpath.XPath.selectSingleNode(doc, xpath);

			if (e != null) {
				XMLOutputter xmlout = new XMLOutputter(format);
				xmlStr = xmlout.outputString(e);
				xmlStr = xmlStr.replaceAll(" ", "");
				xmlStr = xmlStr.replaceAll("\r", "");
				xmlStr = xmlStr.replaceAll("\n", "");
			}
		} catch (UnsupportedEncodingException e) {
			logger.error(outXML);
			e.printStackTrace();
		} catch (JDOMException e) {
			logger.error(outXML);
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(outXML);
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(outXML);
			e.printStackTrace();
		}
		return xmlStr;
	}

	public static NodeList getNodeList(String outXML, String xpath) {
		if (outXML == null || outXML.equals("")) {
			return null;
		}
		NodeList nodeList = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(false);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new ByteArrayInputStream(outXML.getBytes("UTF-8")));
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpathn = factory.newXPath();
			nodeList = (NodeList) xpathn.evaluate(xpath, doc, XPathConstants.NODESET);
		} catch (Exception e) {
			logger.error(outXML);
			e.printStackTrace();
		}
		return nodeList;
	}

	public static List<org.dom4j.Node> getNodeListDom4j(String outXML, String xpath) {
		if (outXML == null || outXML.equals("")) {
			return null;
		}
		List<org.dom4j.Node> nodelist = null;
		try {
			SAXReader saxReader = new SAXReader();
			org.dom4j.Document doc = saxReader.read(new ByteArrayInputStream(outXML.getBytes("UTF-8")));
			nodelist = doc.selectNodes(xpath);
		} catch (Exception e) {
			logger.error(outXML);
			e.printStackTrace();
		}
		return nodelist;
	}

	public static String getNodeValueDom4j(String outXML, String xpath) {
		if (outXML == null || outXML.equals("")) {
			return null;
		}
		org.dom4j.Node node = null;
		try {
			SAXReader saxReader = new SAXReader();
			org.dom4j.Document doc = saxReader.read(new ByteArrayInputStream(outXML.getBytes("UTF-8")));
			node = doc.selectSingleNode(xpath);

		} catch (Exception e) {
			logger.error(outXML);
			e.printStackTrace();
		}
		if (node != null) {
			return node.getText();
		} else {
			return null;
		}
	}

	public static String getNodeValue(String outXML, String xpath) {
		if (outXML == null || outXML.equals("")) {
			return null;
		}
		Node node = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(false);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new ByteArrayInputStream(outXML.getBytes("UTF-8")));
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpathn = factory.newXPath();
			node = (Node) xpathn.evaluate(xpath, doc, XPathConstants.NODE);
		} catch (Exception e) {
			logger.error(outXML);
			e.printStackTrace();
		}
		if (node != null) {
			return node.getTextContent();
		} else {
			return null;
		}
	}

	public static Node getNode(String outXML, String xpath) {
		if (outXML == null || outXML.equals("")) {
			return null;
		}
		Node node = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(false);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new ByteArrayInputStream(outXML.getBytes("UTF-8")));
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpathn = factory.newXPath();
			node = (Node) xpathn.evaluate(xpath, doc, XPathConstants.NODE);
		} catch (Exception e) {
			logger.error(outXML);
			e.printStackTrace();
		}
		return node;
	}

}
