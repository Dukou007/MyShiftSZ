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

import com.pax.common.web.form.BaseForm;

public class EditAlertOffConfigForm extends BaseForm {

	private static final long serialVersionUID = -6375263899118616898L;

	private Long cdtId;

	private Long condId;

	private String timeZone;

	private int excludeWeekend;

	String nbDateListJson;

	String nbWeekListJson;

	public Long getCdtId() {
		return cdtId;
	}

	public void setCdtId(Long cdtId) {
		this.cdtId = cdtId;
	}

	public Long getCondId() {
		return condId;
	}

	public void setCondId(Long condId) {
		this.condId = condId;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public int getExcludeWeekend() {
		return excludeWeekend;
	}

	public void setExcludeWeekend(int excludeWeekend) {
		this.excludeWeekend = excludeWeekend;
	}

	public String getNbDateListJson() {
		return nbDateListJson;
	}

	public void setNbDateListJson(String nbDateListJson) {
		this.nbDateListJson = nbDateListJson;
	}

	public String getNbWeekListJson() {
		return nbWeekListJson;
	}

	public void setNbWeekListJson(String nbWeekListJson) {
		this.nbWeekListJson = nbWeekListJson;
	}
}
