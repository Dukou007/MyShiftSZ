/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description: ConditionForm
 * Revision History:		
 * Date	                 Author	                Action
 * 20170111  	         Frank           	    Modify 
 * ============================================================================		
 */
package com.pax.tms.monitor.web.form;

import com.pax.common.web.form.BaseForm;

public class EditConditionForm extends BaseForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 37603505723356381L;
	private Long condId;
	private int alertSeverity;
	private String condName;
	private String groupName;
	private String alertItem;
	private String alertThreshold;
	private String alertMessage;
	private String scbSms;
	private String scbEmail;

	public Long getCondId() {
		return condId;
	}

	public void setCondId(Long condId) {
		this.condId = condId;
	}

	public String getCondName() {
		return condName;
	}

	public void setCondName(String condName) {
		this.condName = condName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getAlertItem() {
		return alertItem;
	}

	public void setAlertItem(String alertItem) {
		this.alertItem = alertItem;
	}

	public String getAlertThreshold() {
		return alertThreshold;
	}

	public void setAlertThreshold(String alertThreshold) {
		this.alertThreshold = alertThreshold;
	}

	public String getAlertMessage() {
		return alertMessage;
	}

	public void setAlertMessage(String alertMessage) {
		this.alertMessage = alertMessage;
	}

	public int getAlertSeverity() {
		return alertSeverity;
	}

	public void setAlertSeverity(int alertSeverity) {
		this.alertSeverity = alertSeverity;
	}

	public String getScbSms() {
		return scbSms;
	}

	public void setScbSms(String scbSms) {
		this.scbSms = scbSms;
	}

	public String getScbEmail() {
		return scbEmail;
	}

	public void setScbEmail(String scbEmail) {
		this.scbEmail = scbEmail;
	}

}
