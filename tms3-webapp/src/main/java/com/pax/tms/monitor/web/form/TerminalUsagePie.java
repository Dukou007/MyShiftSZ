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

public class TerminalUsagePie extends UsagePie {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6112934999405848888L;
	private String terminalId;
	private Long itemErrors;
	private Long itemTotals;

	public TerminalUsagePie() {
		super();
	}

	public String getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}

	public Long getItemErrors() {
		return itemErrors;
	}

	public void setItemErrors(Long itemErrors) {
		this.itemErrors = itemErrors;
	}

	public Long getItemTotals() {
		return itemTotals;
	}

	public void setItemTotals(Long itemTotals) {
		this.itemTotals = itemTotals;
	}

}
