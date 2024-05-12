/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 *
 */

package com.richeninfo.wsdl;

import javax.xml.bind.annotation.XmlRegistry;

/**
 * This object contains factory methods for each Java content interface and Java
 * element interface generated in the cn.com.wsdl package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the
 * Java representation for XML content. The Java representation of XML content
 * can consist of schema derived interfaces and classes representing the binding
 * of schema type definitions, element declarations and model groups. Factory
 * methods for each of these are provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

	/**
	 * Create a new ObjectFactory that can be used to create new instances of
	 * schema derived classes for package: cn.com.wsdl
	 * 
	 */
	public ObjectFactory() {
	}

	/**
	 * Create an instance of {@link GetAssertInfoByUID }
	 * 
	 */
	public GetAssertInfoByUID createGetAssertInfoByUID() {
		return new GetAssertInfoByUID();
	}

	/**
	 * Create an instance of {@link GetAssertInfoByUIDResponse }
	 * 
	 */
	public GetAssertInfoByUIDResponse createGetAssertInfoByUIDResponse() {
		return new GetAssertInfoByUIDResponse();
	}

}
