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
public class DeploymentStatus implements Serializable {

	private static final long serialVersionUID = -3025316622694938046L;

	private String deploymentUUID;
	private String downloadStatus;
	private String activationStatus;
	private String activationCode;

	public DeploymentStatus() {
	}

	public DeploymentStatus(String deploymentUUID, String downloadStatus, String activationStatus, String activationCode) {
		this.deploymentUUID = deploymentUUID;
		this.downloadStatus = downloadStatus;
		this.activationStatus = activationStatus;
		this.activationCode = activationCode;
	}

	public String getDeploymentUUID() {
		return deploymentUUID;
	}

	public void setDeploymentUUID(String deploymentUUID) {
		this.deploymentUUID = deploymentUUID;
	}

	public String getDownloadStatus() {
		return downloadStatus;
	}

	public void setDownloadStatus(String downloadStatus) {
		this.downloadStatus = downloadStatus;
	}

	public String getActivationStatus() {
		return activationStatus;
	}

	public void setActivationStatus(String activationStatus) {
		this.activationStatus = activationStatus;
	}

	public String getActivationCode() {
		return activationCode;
	}

	public void setActivationCode(String activationCode) {
		this.activationCode = activationCode;
	}

	@Override
	public String toString() {
		return Json.encodePrettily(this);
	}

}
