/*
 * =============================================================================
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

import com.pax.common.web.form.BaseForm;
import com.pax.common.web.support.editor.UTCDateEditor;

public class TerminalDeployForm extends BaseForm {

	private static final long serialVersionUID = 1L;
	private String tsn;
	private Long pkgId;
	private Long pkgSchemaId;
	private String dwnlStartTime;
	private String dwnlEndTime;
	private boolean daylightSaving = true;
	private String timeZone;
	private Integer downReTryCount;
	private Integer actvReTryCount;
	private String actvStartTime;
	private String actvEndTime;
	private Map<String, String> paraMap;


	public String getTsn() {
		return tsn;
	}

	public void setTsn(String tsn) {
		this.tsn = tsn;
	}

	public Long getPkgId() {
		return pkgId;
	}

	public void setPkgId(Long pkgId) {
		this.pkgId = pkgId;
	}

	public Long getPkgSchemaId() {
		return pkgSchemaId;
	}

	public void setPkgSchemaId(Long pkgSchemaId) {
		this.pkgSchemaId = pkgSchemaId;
	}

	public Date getDwnlStartTime() {
		return UTCDateEditor.parseDate(dwnlStartTime, timeZone, true);
	}

	public Date getDwnlEndTime() {
		return UTCDateEditor.parseDate(dwnlEndTime, timeZone, true);
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
		if("now".equals(actvStartTime.trim().toLowerCase())){
			return null;
		}
		return UTCDateEditor.parseDate(actvStartTime, timeZone, true);
	}

	public Date getActvEndTime() {
		return UTCDateEditor.parseDate(actvEndTime, timeZone, true);
	}

	public Map<String, String> getParaMap() {
		return paraMap;
	}

	public void setParaMap(Map<String, String> paraMap) {
		this.paraMap = paraMap;
	}

	public boolean isDaylightSaving() {
		return daylightSaving;
	}

	public void setDaylightSaving(boolean daylightSaving) {
		this.daylightSaving = daylightSaving;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public void setDwnlStartTime(String dwnlStartTime) {
		this.dwnlStartTime = dwnlStartTime;
	}

	public void setDwnlEndTime(String dwnlEndTime) {
		this.dwnlEndTime = dwnlEndTime;
	}

	public void setActvStartTime(String actvStartTime) {
		this.actvStartTime = actvStartTime;
	}

	public void setActvEndTime(String actvEndTime) {
		this.actvEndTime = actvEndTime;
	}
	
}
