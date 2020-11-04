/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description: TerminalRealStatusForm
 * Revision History:		
 * Date	                 Author	                Action
 * 20170111  	         Frank           	    Modify 
 * ============================================================================		
 */
package com.pax.tms.monitor.web.form;

import java.util.Date;

import com.pax.common.model.AbstractModel;

public class TerminalRealStatusForm extends AbstractModel {
	
	private static final long serialVersionUID = 5012673347626832756L;

	private String terminalId;
	
	private Date reportTime;
	
	private String tamper;
	
	private String online;
	
	private String shield;
	
	private String stylus;
	
	private String downStatus;
	
	private String actvStatus;

	public String getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}

	public Date getReportTime() {
		return reportTime;
	}

	public void setReportTime(Date reportTime) {
		this.reportTime = reportTime;
	}

	public String getTamper() {
		return tamper;
	}

	public void setTamper(String tamper) {
		this.tamper = tamper;
	}

	public String getOnline() {
		return online;
	}

	public void setOnline(String online) {
		this.online = online;
	}

	public String getShield() {
		return shield;
	}

	public void setShield(String shield) {
		this.shield = shield;
	}

	public String getStylus() {
		return stylus;
	}

	public void setStylus(String stylus) {
		this.stylus = stylus;
	}

	public String getDownStatus() {
		return downStatus;
	}

	public void setDownStatus(String downStatus) {
		this.downStatus = downStatus;
	}

	public String getActvStatus() {
		return actvStatus;
	}

	public void setActvStatus(String actvStatus) {
		this.actvStatus = actvStatus;
	}
}
