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
public class FileInfo implements Serializable {

	private static final long serialVersionUID = -3665855966314943734L;

	private String deploymentUUID;

	private String fileUUID;

	private String fileName;

	private int fileSize;

	private String fileVersion;

	private String md5;

	private String sha256;

	private Long fileId;

	private String filePath;

	private Long pkgId;

	public FileInfo() {
	}

	public FileInfo(String deploymentUUID, String fileUUID, String fileName, int fileSize, String fileVersion,
			String md5, String sha256) {
		this.deploymentUUID = deploymentUUID;
		this.fileUUID = fileUUID;
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.fileVersion = fileVersion;
		this.md5 = md5;
		this.sha256 = sha256;
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

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public String getFileVersion() {
		return fileVersion;
	}

	public void setFileVersion(String fileVersion) {
		this.fileVersion = fileVersion;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getSha256() {
		return sha256;
	}

	public void setSha256(String sha256) {
		this.sha256 = sha256;
	}

	public Long getFileId() {
		return fileId;
	}

	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	@Override
	public String toString() {
		return Json.encodePrettily(this);
	}

	public Long getPkgId() {
		return pkgId;
	}

	public void setPkgId(Long pkgId) {
		this.pkgId = pkgId;
	}

}
