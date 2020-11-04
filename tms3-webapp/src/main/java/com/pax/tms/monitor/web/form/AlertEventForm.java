/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description: AlertEventForm
 * Revision History:		
 * Date	                 Author	                Action
 * 20170111  	         Frank           	    Modify 
 * ============================================================================		
 */
package com.pax.tms.monitor.web.form;

import java.util.Date;

import com.pax.common.web.form.QueryForm;

public class AlertEventForm extends QueryForm{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7051459120767841486L;
	private String groupName;
	private int alertSeverity;
	private String alertMsg;
	private Date alertTime;
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public int getAlertSeverity() {
		return alertSeverity;
	}
	public void setAlertSeverity(int alertSeverity) {
		this.alertSeverity = alertSeverity;
	}
	public String getAlertMsg() {
		return alertMsg;
	}
	public void setAlertMsg(String alertMsg) {
		this.alertMsg = alertMsg;
	}
	public Date getAlertTime() {
		return alertTime;
	}
	public void setAlertTime(Date alertTime) {
		this.alertTime = alertTime;
	} 

}
