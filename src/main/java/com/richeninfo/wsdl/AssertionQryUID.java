/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 *
 */

package com.richeninfo.wsdl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;

/**
 * This class was generated by the JAX-WS RI. JAX-WS RI 2.1.3-hudson-390-
 * Generated source version: 2.0
 * <p>
 * An example of how this class may be used:
 * 
 * <pre>
 * AssertionQryUID service = new AssertionQryUID();
 * AssertionQryUIDWS portType = service.getAssertionQryUID();
 * portType.getAssertInfoByUID(...);
 * </pre>
 * 
 * </p>
 * 
 */
@WebServiceClient(name = "AssertionQryUID", targetNamespace = "http://login.10086.cn", wsdlLocation = "http://actest.10086.cn:18080/services/AssertionQryUID?wsdl")
public class AssertionQryUID extends Service {

	private final static URL ASSERTIONQRYUID_WSDL_LOCATION;
	private final static Logger logger = Logger
			.getLogger(com.richeninfo.wsdl.AssertionQryUID.class.getName());

	static {
		URL url = null;
		try {
			URL baseUrl;
			baseUrl = com.richeninfo.wsdl.AssertionQryUID.class.getResource(".");
			url = new URL(baseUrl,
					"http://actest.10086.cn:18080/services/AssertionQryUID?wsdl");
		} catch (MalformedURLException e) {
			logger.warning("Failed to create URL for the wsdl Location: 'http://actest.10086.cn:18080/services/AssertionQryUID?wsdl', retrying as a local file");
			logger.warning(e.getMessage());
		}
		ASSERTIONQRYUID_WSDL_LOCATION = url;
	}

	public AssertionQryUID(URL wsdlLocation, QName serviceName) {
		super(wsdlLocation, serviceName);
	}

	public AssertionQryUID() {
		super(ASSERTIONQRYUID_WSDL_LOCATION, new QName("http://login.10086.cn",
				"AssertionQryUID"));
	}

	/**
	 * 
	 * @return returns AssertionQryUIDWS
	 */
	@WebEndpoint(name = "AssertionQryUID")
	public AssertionQryUIDWS getAssertionQryUID() {
		return super.getPort(new QName("http://login.10086.cn",
				"AssertionQryUID"), AssertionQryUIDWS.class);
	}

}