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
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pax.tms.pxretailer.PxRetailerException;
import com.pax.tms.pxretailer.PxRetailerProtocol;

import io.vertx.core.json.Json;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseRequest implements Serializable {

	private static final long serialVersionUID = 7016007143810054007L;

	protected static final int PROTOCOL_VERSION_1 = 1;

	protected int version;

	protected String requestType;

	protected String deviceType;

	protected String deviceSerialNumber;

	protected Date requestTime = new Date();

	public BaseRequest() {
		this.version = PROTOCOL_VERSION_1;
	}

	public void validateInput() {
		if (version < 1) {
			throw new PxRetailerException(PxRetailerProtocol.INVALID_REQUEST_RESPONSE, "The version is unsupported");
		}

		deviceType = StringUtils.trimToNull(deviceType);
		if (deviceType == null) {
			throw new PxRetailerException(PxRetailerProtocol.INVALID_REQUEST_RESPONSE, "The deviceType is empty");
		}

		deviceSerialNumber = StringUtils.trimToNull(deviceSerialNumber);
		if (deviceSerialNumber == null) {
			throw new PxRetailerException(PxRetailerProtocol.INVALID_REQUEST_RESPONSE,
					"The deviceSerialNumber is empty");
		}
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getDeviceSerialNumber() {
		return deviceSerialNumber;
	}

	public void setDeviceSerialNumber(String deviceSerialNumber) {
		this.deviceSerialNumber = deviceSerialNumber;
	}

	public Date getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(Date requestTime) {
		this.requestTime = requestTime;
	}

	@Override
	public String toString() {
		return Json.encodePrettily(this);
	}

}
