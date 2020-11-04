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

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CallHomeResponse extends BaseResponse {

	private static final long serialVersionUID = -3763807092579857636L;

	private static final String CALL_HOME = "CALLHOME";

	private String terminalTime;

	private List<ContactService> contactServices = new ArrayList<>();

	public CallHomeResponse() {
		super();
		setResponseType(CALL_HOME);
	}

	public CallHomeResponse(int statusCode, String statusMessage) {
		this();
		this.statusCode = statusCode;
		this.statusMessage = statusMessage;
	}

	public void addService(String name, String uri, int contactafter) {
		contactServices.add(new ContactService(name, uri, contactafter));
	}

	public String getTerminalTime() {
		return terminalTime;
	}

	public void setTerminalTime(String terminalTime) {
		this.terminalTime = terminalTime;
	}

	public List<ContactService> getContactServices() {
		return contactServices;
	}

	public void setContactServices(List<ContactService> contactServices) {
		this.contactServices = contactServices;
	}

}