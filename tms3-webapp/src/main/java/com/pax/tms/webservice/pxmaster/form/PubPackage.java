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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
 * @author Dai.L
 * @date 2014-10-23
 */

public class PubPackage implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "PACKAGE_ID")
	private Long id;

	@Column(name = "PACKAGE_UUID")
	private String uuid;

	@Column(name = "PACKAGE_NAME")
	private String name;

	@Column(name = "PACKAGE_TYPE")
	private String type;

	@Column(name = "VERSION")
	private String version;

	@Column(name = "DATE_ADDED")
	private Date dateAdded;

	@Column(name = "TERMINAL_TYPE_ID")
	private Long terminal_type_id;

	@Column(name = "PARENT_ID")
	private Long parent_id;

	@OneToOne
	@JoinColumn(name = "FILE_ID")
	private PubFileInfo file_id;

	@OneToOne
	@JoinColumn(name = "MANIFEST_ID")
	private PubFileInfo manifest_id;

	@Column(name = "USER_ID")
	private Long user_id;

	@Column(name = "ORGANIZATION_ID")
	private Long org_id;

	@Column(name = "ISDELETED")
	private String isdeleted = "N";

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Date getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(Date dateAdded) {
		this.dateAdded = dateAdded;
	}

	public Long getTerminal_type_id() {
		return terminal_type_id;
	}

	public void setTerminal_type_id(Long terminal_type_id) {
		this.terminal_type_id = terminal_type_id;
	}

	public Long getParent_id() {
		return parent_id;
	}

	public void setParent_id(Long parent_id) {
		this.parent_id = parent_id;
	}

	public PubFileInfo getFile_id() {
		return file_id;
	}

	public void setFile_id(PubFileInfo file_id) {
		this.file_id = file_id;
	}

	public PubFileInfo getManifest_id() {
		return manifest_id;
	}

	public void setManifest_id(PubFileInfo manifest_id) {
		this.manifest_id = manifest_id;
	}

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public Long getOrg_id() {
		return org_id;
	}

	public void setOrg_id(Long org_id) {
		this.org_id = org_id;
	}

	public String getIsdeleted() {
		return isdeleted;
	}

	public void setIsdeleted(String isdeleted) {
		this.isdeleted = isdeleted;
	}

}
