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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GetScheduledPackagesRequest extends BaseRequest {

	private static final long serialVersionUID = 9070451184735748696L;

	private static final String GET_SCHEDULED_PACKAGES = "GETSCHEDULEDPACKAGES";

	public GetScheduledPackagesRequest() {
		super();
		this.requestType = GET_SCHEDULED_PACKAGES;
	}

	public GetScheduledPackagesRequest(String deviceType, String deviceSerialNumber) {
		this();
		this.deviceType = deviceType;
		this.deviceSerialNumber = deviceSerialNumber;
	}
}
