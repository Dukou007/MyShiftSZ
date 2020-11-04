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
package com.pax.tms.terminal.web.form;

import com.pax.common.web.form.AddressForm;

public class EditTerminalForm extends AddressForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String description;

	private String timeZone;

	private boolean daylightSaving;

	private boolean syncToServerTime;

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

	public boolean isSyncToServerTime() {
		return syncToServerTime;
	}

	public void setSyncToServerTime(boolean syncToServerTime) {
		this.syncToServerTime = syncToServerTime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
