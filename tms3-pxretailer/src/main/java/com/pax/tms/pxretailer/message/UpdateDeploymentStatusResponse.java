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
public class UpdateDeploymentStatusResponse extends BaseResponse {

	private static final long serialVersionUID = -180289662891030320L;

	private static final String UPDATE_PACKAGE_DEPLOYMENT_STATUS = "UPDATEPACKAGESDEPLOYMENTSTATUS";

	public UpdateDeploymentStatusResponse() {
		super();
		setResponseType(UPDATE_PACKAGE_DEPLOYMENT_STATUS);
	}

	public UpdateDeploymentStatusResponse(int statusCode, String statusMessage) {
		this();
		this.statusCode = statusCode;
		this.statusMessage = statusMessage;
	}

}
