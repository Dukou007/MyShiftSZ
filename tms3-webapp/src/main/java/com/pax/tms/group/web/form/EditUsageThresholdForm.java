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
package com.pax.tms.group.web.form;

import com.pax.common.web.form.BaseForm;

public class EditUsageThresholdForm extends BaseForm {

	private static final long serialVersionUID = 5715280987071541086L;

	private String []itemName;
	private String []thdValue;
	private String []reportCycle;
	public String[] getItemName() {
		return itemName;
	}
	public void setItemName(String[] itemName) {
		this.itemName = itemName;
	}
	public String[] getThdValue() {
		return thdValue;
	}
	public void setThdValue(String[] thdValue) {
		this.thdValue = thdValue;
	}
	public String[] getReportCycle() {
		return reportCycle;
	}
	public void setReportCycle(String[] reportCycle) {
		this.reportCycle = reportCycle;
	}
	
}
