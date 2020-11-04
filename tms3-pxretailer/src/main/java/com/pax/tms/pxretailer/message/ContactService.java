/*
 * ============================================================================
 * = COPYRIGHT				
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *   	Copyright (C) 2009-2020 PAX Technology, Inc. All rights reserved.		
 * ============================================================================		
 */
package com.pax.tms.pxretailer.message;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.vertx.core.json.Json;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ContactService implements Serializable {

	private static final long serialVersionUID = 9017070645949931868L;

	private String serviceName;
	private String serviceURI;
	private int contactAfter;

	public ContactService() {
	}

	public ContactService(String name, String uri, int contactafter) {
		serviceName = name;
		serviceURI = uri;
		contactAfter = contactafter;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceURI() {
		return serviceURI;
	}

	public void setServiceURI(String serviceURI) {
		this.serviceURI = serviceURI;
	}

	public int getContactAfter() {
		return contactAfter;
	}

	public void setContactAfter(int contactAfter) {
		this.contactAfter = contactAfter;
	}

	@Override
	public String toString() {
		return Json.encodePrettily(this);
	}

}