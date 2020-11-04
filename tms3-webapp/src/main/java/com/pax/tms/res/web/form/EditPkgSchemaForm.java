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

public class EditPkgSchemaForm extends BaseForm {

	private static final long serialVersionUID = -2533714002887500364L;

	private String name;
	private Long pkgSchemaId;
	private Long pkgId;
	private Map<String, String> parasMap;

	public Long getPkgSchemaId() {
		return pkgSchemaId;
	}

	public void setPkgSchemaId(Long pkgSchemaId) {
		this.pkgSchemaId = pkgSchemaId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, String> getParasMap() {
		return parasMap;
	}

	public void setParasMap(Map<String, String> parasMap) {
		this.parasMap = parasMap;
	}
	
	public Long getPkgId() {
		return pkgId;
	}

	public void setPkgId(Long pkgId) {
		this.pkgId = pkgId;
	}
}
