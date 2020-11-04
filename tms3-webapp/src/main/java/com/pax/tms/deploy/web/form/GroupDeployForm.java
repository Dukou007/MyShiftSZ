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
import com.pax.common.web.support.editor.UTCDateEditor;

public class GroupDeployForm extends BaseForm implements AbstractGroupDeployForm {

	private static final long serialVersionUID = 1L;
	private String groupName;
	private Long pkgId;
	private Long pkgSchemaId;

	private String destModel;
	private String timeZone;
	private boolean daylightSaving = true;
	private boolean deletedWhenDone = true;
	private String dwnlStartTime;
	private String dwnlEndTime;
	private Integer downReTryCount;
	private Integer actvReTryCount;
	private String actvStartTime;
	private String actvEndTime;
	private Map<String, String> paramMap;
	
	private Integer latestType; // 0 : 查最新上传的  1：查最高版本的

	@Override
	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	@Override
	public Long getPkgId() {
		return pkgId;
	}

	public void setPkgId(Long pkgId) {
		this.pkgId = pkgId;
	}

	@Override
	public Long getPkgSchemaId() {
		return pkgSchemaId;
	}

	public void setPkgSchemaId(Long pkgSchemaId) {
		this.pkgSchemaId = pkgSchemaId;
	}

	@Override
	public String getDestModel() {
		return destModel;
	}

	public void setDestModel(String destModel) {
		this.destModel = destModel;
	}

	@Override
	public Date getDwnlStartTime() {
		return UTCDateEditor.parseDate(dwnlStartTime, timeZone, true);
	}

	public Date getDwnlEndTime() {
		return UTCDateEditor.parseDate(dwnlEndTime, timeZone, true);
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
		if("now".equals(actvStartTime.trim().toLowerCase())){
			return null;
		}
		return UTCDateEditor.parseDate(actvStartTime, timeZone, true);
	}

	public Date getActvEndTime() {
		return UTCDateEditor.parseDate(actvEndTime, timeZone, true);
	}

	@Override
	public Map<String, String> getParamMap() {
		return paramMap;
	}

	public void setParamMap(Map<String, String> paramMap) {
		this.paramMap = paramMap;
	}

	@Override
	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public boolean isDaylightSaving() {
		return daylightSaving;
	}

	public void setDaylightSaving(boolean daylightSaving) {
		this.daylightSaving = daylightSaving;
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

	public void setDeletedWhenDone(boolean deletedWhenDone) {
		this.deletedWhenDone = deletedWhenDone;
	}

	@Override
	public boolean isDeletedWhenDone() {
		return deletedWhenDone;
	}

    public Integer getLatestType() {
        return latestType;
    }

    public void setLatestType(Integer latestType) {
        this.latestType = latestType;
    }

}
