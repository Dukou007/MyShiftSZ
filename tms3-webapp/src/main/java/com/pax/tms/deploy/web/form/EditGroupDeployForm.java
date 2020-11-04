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
package com.pax.tms.deploy.web.form;

import java.util.Date;
import java.util.Map;

public class EditGroupDeployForm extends GroupDeployOperatorForm {

	private static final long serialVersionUID = 1L;
	private Long pkgSchemaId;
	private Date dwnlStartTime;
	private Integer downReTryCount;
	private Integer actvReTryCount;
	private Date actvStartTime;
	private String timeZone;
	private Map<String, String> paraMap;

	public Long getPkgSchemaId() {
		return pkgSchemaId;
	}

	public void setPkgSchemaId(Long pkgSchemaId) {
		this.pkgSchemaId = pkgSchemaId;
	}

	public Date getDwnlStartTime() {
		return dwnlStartTime;
	}

	public void setDwnlStartTime(Date dwnlStartTime) {
		this.dwnlStartTime = dwnlStartTime;
	}

	public Integer getDownReTryCount() {
		return downReTryCount;
	}

	public void setDownReTryCount(Integer downReTryCount) {
		this.downReTryCount = downReTryCount;
	}

	public Integer getActvReTryCount() {
		return actvReTryCount;
	}

	public void setActvReTryCount(Integer actvReTryCount) {
		this.actvReTryCount = actvReTryCount;
	}

	public Date getActvStartTime() {
		return actvStartTime;
	}

	public void setActvStartTime(Date actvStartTime) {
		this.actvStartTime = actvStartTime;
	}
	

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public Map<String, String> getParaMap() {
		return paraMap;
	}

	public void setParaMap(Map<String, String> paraMap) {
		this.paraMap = paraMap;
	}

}
