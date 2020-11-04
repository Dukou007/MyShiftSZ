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
public class UpdateDeploymentStatusRequest extends BaseRequest {

	private static final long serialVersionUID = -5833174008591261002L;

	private static final String UPDATE_PACKAGE_DEPLOYMENT_STATUS = "UPDATEPACKAGESDEPLOYMENTSTATUS";

	private List<DeploymentStatus> deploymentStatus = new ArrayList<>();

	public UpdateDeploymentStatusRequest() {
		super();
		this.requestType = UPDATE_PACKAGE_DEPLOYMENT_STATUS;
	}

	public UpdateDeploymentStatusRequest(String deviceType, String deviceSerialNumber) {
		this();
		this.deviceType = deviceType;
		this.deviceSerialNumber = deviceSerialNumber;

	}

	public List<DeploymentStatus> getDeploymentStatus() {
		return deploymentStatus;
	}

	public void setDeploymentStatus(List<DeploymentStatus> deploymentStatus) {
		this.deploymentStatus = deploymentStatus;
	}

}
