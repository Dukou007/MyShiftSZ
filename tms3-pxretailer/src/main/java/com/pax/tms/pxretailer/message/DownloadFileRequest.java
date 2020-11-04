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
public class DownloadFileRequest extends BaseRequest {

	private static final long serialVersionUID = -5686747484722974338L;

	private static final String DOWNLOAD_FILE = "DOWNLOADFILE";

	private String deploymentUUID;

	private String fileUUID;

	public DownloadFileRequest(String deviceType, String deviceSerialNumber) {
		this.version = 1;
		this.requestType = DOWNLOAD_FILE;
		this.deviceType = deviceType;
		this.deviceSerialNumber = deviceSerialNumber;

	}

	public DownloadFileRequest() {
		this.version = 1;
		this.requestType = DOWNLOAD_FILE;
	}

	public String getDeploymentUUID() {
		return deploymentUUID;
	}

	public void setDeploymentUUID(String deploymentUUID) {
		this.deploymentUUID = deploymentUUID;
	}

	public String getFileUUID() {
		return fileUUID;
	}

	public void setFileUUID(String fileUUID) {
		this.fileUUID = fileUUID;
	}

}
