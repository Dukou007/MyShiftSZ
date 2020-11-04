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
public class PackageInformation implements Serializable {

	private static final long serialVersionUID = 2647486260027616518L;

	private String deploymentUUID;
	private String packageVersion;
	private String packageName;
	private String fileUUID;
	private String activationTime;

	public PackageInformation() {
	}

	public PackageInformation(String deploymentUUID, String packageName, String packageVersion, String fileUUID,
			String activationTime) {
		this.deploymentUUID = deploymentUUID;
		this.packageName = packageName;
		this.packageVersion = packageVersion;
		this.fileUUID = fileUUID;
		this.activationTime = activationTime;
	}

	public String getDeploymentUUID() {
		return deploymentUUID;
	}

	public void setDeploymentUUID(String deploymentUUID) {
		this.deploymentUUID = deploymentUUID;
	}

	public String getPackageVersion() {
		return packageVersion;
	}

	public void setPackageVersion(String packageVersion) {
		this.packageVersion = packageVersion;
	}

	public String getFileUUID() {
		return fileUUID;
	}

	public void setFileUUID(String fileUUID) {
		this.fileUUID = fileUUID;
	}

	public String getActivationTime() {
		return activationTime;
	}

	public void setActivationTime(String activationTime) {
		this.activationTime = activationTime;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	@Override
	public String toString() {
		return Json.encodePrettily(this);
	}
};