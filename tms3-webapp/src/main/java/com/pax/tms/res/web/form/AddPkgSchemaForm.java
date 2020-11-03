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
package com.pax.tms.res.web.form;

import java.util.Map;

import com.pax.common.web.form.BaseForm;

public class AddPkgSchemaForm extends BaseForm {

	private static final long serialVersionUID = -2533714002887500364L;

	private Long pkgId;
	private String name;
	private String pkgName;
	private String pkgVersion;
	private boolean isCheckPermission=true;
	private boolean isSys = false;
	private Map<String, String> parasMap;

	public Long getPkgId() {
		return pkgId;
	}

	public void setPkgId(Long pkgId) {
		this.pkgId = pkgId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPkgName() {
		return pkgName;
	}

	public void setPkgName(String pkgName) {
		this.pkgName = pkgName;
	}

	public String getPkgVersion() {
		return pkgVersion;
	}

	public void setPkgVersion(String pkgVersion) {
		this.pkgVersion = pkgVersion;
	}

	public boolean isSys() {
		return isSys;
	}

	public void setSys(boolean isSys) {
		this.isSys = isSys;
	}

	public Map<String, String> getParasMap() {
		return parasMap;
	}

	public void setParasMap(Map<String, String> parasMap) {
		this.parasMap = parasMap;
	}

	public boolean isCheckPermission() {
		return isCheckPermission;
	}

	public void setCheckPermission(boolean isCheckPermission) {
		this.isCheckPermission = isCheckPermission;
	}

}
