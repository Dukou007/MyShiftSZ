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
package com.pax.tms.app.phoenix;

import java.io.Serializable;

public class FileInfo implements Serializable {

	private static final long serialVersionUID = -6210112690283030230L;

	private String name;

	private String version;

	private String filePath;

	private Long fileSize;

	private String md5;

	private String sha256;

	private boolean manifest;

	private String pgmType;
	private String pgmName;
	private String pgmVersion;
	private String pgmDesc;

	public FileInfo() {
	}

	public FileInfo(String name, String version, String filePath, Long fileSize, String md5, String sha256,
			boolean manifest) {
		super();
		this.name = name;
		this.version = version;
		this.filePath = filePath;
		this.fileSize = fileSize;
		this.md5 = md5;
		this.sha256 = sha256;
		this.manifest = manifest;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
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

	public boolean isManifest() {
		return manifest;
	}

	public void setManifest(boolean manifest) {
		this.manifest = manifest;
	}

	public String getPgmType() {
		return pgmType;
	}

	public void setPgmType(String pgmType) {
		this.pgmType = pgmType;
	}

	public String getPgmName() {
		return pgmName;
	}

	public void setPgmName(String pgmName) {
		this.pgmName = pgmName;
	}

	public String getPgmVersion() {
		return pgmVersion;
	}

	public void setPgmVersion(String pgmVersion) {
		this.pgmVersion = pgmVersion;
	}

	public String getPgmDesc() {
		return pgmDesc;
	}

	public void setPgmDesc(String pgmDesc) {
		this.pgmDesc = pgmDesc;
	}

}
