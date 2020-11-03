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
package com.pax.tms.report.web.form;

import java.util.Date;

import com.pax.common.web.form.QueryForm;

public class QueryTerminalDownloadForm extends QueryForm {

	private static final long serialVersionUID = 4666541374523038892L;

	private String timeType;
	private Date startTime;
	private Date endTime;
	private String pkgType;
	private String terminalType;
	private String downStatusType;
	private String actiStatusType;
	private Long[] groupIds;
	private String tsn;
	private Date dayStart;

	public String getTimeType() {
		return timeType;
	}

	public void setTimeType(String timeType) {
		this.timeType = timeType;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getPkgType() {
		return pkgType;
	}

	public void setPkgType(String pkgType) {
		this.pkgType = pkgType;
	}

	public String getTerminalType() {
		return terminalType;
	}

	public void setTerminalType(String terminalType) {
		this.terminalType = terminalType;
	}

	public String getDownStatusType() {
		return downStatusType;
	}

	public void setDownStatusType(String downStatusType) {
		this.downStatusType = downStatusType;
	}

	public String getActiStatusType() {
		return actiStatusType;
	}

	public void setActiStatusType(String actiStatusType) {
		this.actiStatusType = actiStatusType;
	}

	public Long[] getGroupIds() {
		return groupIds;
	}

	public void setGroupIds(Long[] groupIds) {
		this.groupIds = groupIds;
	}

	public String getTsn() {
		return tsn;
	}

	public void setTsn(String tsn) {
		this.tsn = tsn;
	}

	public Date getDayStart() {
		return dayStart;
	}

	public void setDayStart(Date dayStart) {
		this.dayStart = dayStart;
	}

}
