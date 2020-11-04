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
public class GetFileInfoResponse extends BaseResponse {

	private static final long serialVersionUID = -5102926211167543314L;

	private List<FileInfo> filesInformation;
	private static final String GET_FILES_INFO = "GETFILESINFORMATION";

	public GetFileInfoResponse() {
		super();
		this.responseType = GET_FILES_INFO;
	}

	public GetFileInfoResponse(int statusCode, String statusMessage) {
		this();
		this.statusCode = statusCode;
		this.statusMessage = statusMessage;
	}

	public void addFileInformation(String deploymentUUID, String fileUUID, String fileName, int fileSize,
			String fileVersion, String md5, String sha256) {
		if (filesInformation == null) {
			filesInformation = new ArrayList<>();
		}
		filesInformation.add(new FileInfo(deploymentUUID, fileUUID, fileName, fileSize, fileVersion, md5, sha256));
	}

	public List<FileInfo> getFilesInformation() {
		return filesInformation;
	}

	public void setFilesInformation(List<FileInfo> fileInformationList) {
		this.filesInformation = fileInformationList;
	}

}
