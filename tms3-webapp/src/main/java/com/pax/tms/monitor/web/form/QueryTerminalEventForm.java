/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description: TerminalEventForm
 * Revision History:		
 * Date	                 Author	                Action
 * 20170111  	         Frank           	    Modify 
 * ============================================================================		
 */
package com.pax.tms.monitor.web.form;

import java.util.Date;

import com.pax.common.web.form.QueryForm;

@SuppressWarnings("serial")
public class QueryTerminalEventForm extends QueryForm {
	private String terminalId;
	private Long eventTime;
	private String sourceType;
	private String eventSource;
	private String eventSeverity;
	private String eventMsg;
	private String paginationStatus;
	private Long eventId;
	private Long totalCount;
	/*
	 * 0 - last 1 day 1 - last 3 day 2 - last week 3 - last month
	 */
	private int days;

	private Date fromDate;

	private Date toDate;
	
	private String activeEvent;//search type
	
	private int activeTime;//days

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	public String getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}

	public Date getEventTime() {
		return new Date(eventTime);
	}

	public void setEventTime(Long eventTime) {
		this.eventTime = eventTime;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public String getEventSource() {
		return eventSource;
	}

	public void setEventSource(String eventSource) {
		this.eventSource = eventSource;
	}

	public String getEventSeverity() {
		return eventSeverity;
	}

	public void setEventSeverity(String eventSeverity) {
		this.eventSeverity = eventSeverity;
	}

	public String getEventMsg() {
		return eventMsg;
	}

	public void setEventMsg(String eventMsg) {
		this.eventMsg = eventMsg;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public String getPaginationStatus() {
		return paginationStatus;
	}

	public void setPaginationStatus(String paginationStatus) {
		this.paginationStatus = paginationStatus;
	}

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

	public Long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Long totalCount) {
		this.totalCount = totalCount;
	}

	public String getActiveEvent() {
		return activeEvent;
	}

	public void setActiveEvent(String activeEvent) {
		this.activeEvent = activeEvent;
	}

	public int getActiveTime() {
		return activeTime;
	}

	public void setActiveTime(int activeTime) {
		this.activeTime = activeTime;
	}
	
}
