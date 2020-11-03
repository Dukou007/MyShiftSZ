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
public class BaseResponse implements Serializable {

	private static final long serialVersionUID = 2204533406831898481L;

	protected static final int PROTOCOL_VERSION_1 = 1;
	protected static final int SUCCESS_STATUS_CODE = 0;
	protected static final String SUCCESS_STAUTS_MESSAGE = "SUCCESS";

	protected int version;

	protected String responseType;

	protected int statusCode;

	protected String statusMessage;

	public BaseResponse() {
		this.version = PROTOCOL_VERSION_1;
		this.statusCode = SUCCESS_STATUS_CODE;
		this.statusMessage = SUCCESS_STAUTS_MESSAGE;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getResponseType() {
		return responseType;
	}

	public void setResponseType(String responseType) {
		this.responseType = responseType;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	@Override
	public String toString() {
		return Json.encodePrettily(this);
	}
}
