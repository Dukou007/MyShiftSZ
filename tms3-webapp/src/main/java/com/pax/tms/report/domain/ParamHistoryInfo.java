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
package com.pax.tms.report.domain;

import com.pax.common.web.form.BaseForm;

public class ParamHistoryInfo extends BaseForm{

	private static final long serialVersionUID = -1276371561481740688L;

	private String pid;

	private String type;

	private String oldValue;

	private String newValue;

	public ParamHistoryInfo() {
		super();
	}

	public ParamHistoryInfo(String pid, String type, String oldValue, String newValue) {
		super();
		this.pid = pid;
		this.type = type;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getOldValue() {
		return oldValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
