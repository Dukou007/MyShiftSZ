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
package com.pax.tms.monitor.web.form;

import java.io.Serializable;
import java.util.Date;

public class TerminalUsageMessageForm implements Serializable {

	private static final long serialVersionUID = -5052215535415071107L;

	private String terminalId;

	private Date startTime;

	private Date endTime;

	private String itemName;

	private int itemErrors;

	private int itemTotals;

	public String getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
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

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public int getItemErrors() {
		return itemErrors;
	}

	public void setItemErrors(int itemErrors) {
		this.itemErrors = itemErrors;
	}

	public int getItemTotals() {
		return itemTotals;
	}

	public void setItemTotals(int itemTotals) {
		this.itemTotals = itemTotals;
	}

}
