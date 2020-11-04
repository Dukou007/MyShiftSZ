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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GetFilesInfoRequest extends BaseRequest {

	private static final long serialVersionUID = -5828427822524516695L;

	private static final String GET_FILES_INFO = "GETFILESINFORMATION";

	private List<FileInfo> filesInformation;

	public GetFilesInfoRequest() {
		super();
		this.requestType = GET_FILES_INFO;
	}

	public GetFilesInfoRequest(String deviceType, String deviceSerialNumber) {
		this();
		this.deviceType = deviceType;
		this.deviceSerialNumber = deviceSerialNumber;
	}

	public List<FileInfo> getFilesInformation() {
		return filesInformation;
	}

	public void setFilesInformation(List<FileInfo> filesInformation) {
		this.filesInformation = filesInformation;
	}

}
