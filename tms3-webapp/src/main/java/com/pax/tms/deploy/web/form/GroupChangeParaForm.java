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

import com.pax.common.web.form.BaseForm;

public class GroupChangeParaForm extends BaseForm implements AbstractGroupDeployForm {

	private static final long serialVersionUID = 1L;
	private String groupName;
	private Long pkgId;
	private String destModel;
	private String timeZone;
	private boolean daylightSaving = true;
	private boolean deletedWhenDone = true;
	private Long pkgSchemaId;
	private Date dwnlStartTime;
	private Date dwnlEndTime;
	private Integer downReTryCount;
	private Integer actvReTryCount;
	private Date actvStartTime;
	private Map<String, String> paramMap;

	@Override
	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	@Override
	public Date getDwnlStartTime() {
		return dwnlStartTime;
	}

	public void setDwnlStartTime(Date dwnlStartTime) {
		this.dwnlStartTime = dwnlStartTime;
	}

	@Override
	public Integer getDownReTryCount() {
		return downReTryCount;
	}

	public void setDownReTryCount(Integer downReTryCount) {
		this.downReTryCount = downReTryCount;
	}

	@Override
	public Integer getActvReTryCount() {
		return actvReTryCount;
	}

	public void setActvReTryCount(Integer actvReTryCount) {
		this.actvReTryCount = actvReTryCount;
	}

	@Override
	public Date getActvStartTime() {
		return actvStartTime;
	}

	public void setActvStartTime(Date actvStartTime) {
		this.actvStartTime = actvStartTime;
	}

	@Override
	public Map<String, String> getParamMap() {
		return paramMap;
	}

	public void setParamMap(Map<String, String> paramMap) {
		this.paramMap = paramMap;
	}

	@Override
	public Long getPkgId() {
		return pkgId;
	}

	public void setPkgId(Long pkgId) {
		this.pkgId = pkgId;
	}

	@Override
	public String getDestModel() {
		return destModel;
	}

	public void setDestModel(String destModel) {
		this.destModel = destModel;
	}

	public boolean isDaylightSaving() {
		return daylightSaving;
	}

	public void setDaylightSaving(boolean daylightSaving) {
		this.daylightSaving = daylightSaving;
	}

	@Override
	public Long getPkgSchemaId() {
		return pkgSchemaId;
	}

	public void setPkgSchemaId(Long pkgSchemaId) {
		this.pkgSchemaId = pkgSchemaId;
	}

	@Override
	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	@Override
	public Date getDwnlEndTime() {
		return dwnlEndTime;
	}

	public void setDwnlEndTime(Date dwnlEndTime) {
		this.dwnlEndTime = dwnlEndTime;
	}

	public void setDeletedWhenDone(boolean deletedWhenDone) {
		this.deletedWhenDone = deletedWhenDone;
	}

	@Override
	public boolean isDeletedWhenDone() {
		return deletedWhenDone;
	}

}
