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
package com.pax.tms.webservice.pxmaster.form;

import java.io.Serializable;

import javax.persistence.Column;

/**
 * @author Dai.L
 * @date 2014-10-23
 */

public class PubFileInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "FILE_ID")
	private Long id;

	@Column(name = "FILE_UUID")
	private String uuid;

	@Column(name = "FILE_NAME")
	private String name;

	@Column(name = "FILE_SIZE")
	private Long size;

	@Column(name = "FILE_MD5")
	private String md5;

	@Column(name = "FILE_SHA256")
	private String sha256;

	@Column(name = "FILE_VERSION")
	private String version;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
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

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}
