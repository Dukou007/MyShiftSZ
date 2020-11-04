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
package com.pax.tms.monitor.domain;

import java.util.Date;

// TMSTTRM_USAGE_MSG
public class UsageMessageInfo {

	private String terminalId;

	private String itemName;

	private Integer itemErrors;

	private Integer itemTotals;

	private Date startTime;

	private Date endTime;

	private Date createDate;

	private String msgCycle;

	public String getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public Integer getItemErrors() {
		return itemErrors;
	}

	public void setItemErrors(Integer itemErrors) {
		this.itemErrors = itemErrors;
	}

	public Integer getItemTotals() {
		return itemTotals;
	}

	public void setItemTotals(Integer itemTotals) {
		this.itemTotals = itemTotals;
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

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getMsgCycle() {
		return msgCycle;
	}

	public void setMsgCycle(String msgCycle) {
		this.msgCycle = msgCycle;
	}

}
