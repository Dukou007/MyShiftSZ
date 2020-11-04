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
package com.pax.tms.location.web.form;

import com.pax.common.web.form.BaseForm;

public class EditCountryForm extends BaseForm {

	private static final long serialVersionUID = 7415565306535836843L;

	private String code;

	private String abbrName;

	private String name;

	private String description;

	private String transCurrencyCode;

	private Integer transCurrentExp;

	private String transReferCurrencyCode;

	private Integer transReferCurrencyExp;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getAbbrName() {
		return abbrName;
	}

	public void setAbbrName(String abbrName) {
		this.abbrName = abbrName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTransCurrencyCode() {
		return transCurrencyCode;
	}

	public void setTransCurrencyCode(String transCurrencyCode) {
		this.transCurrencyCode = transCurrencyCode;
	}

	public Integer getTransCurrentExp() {
		return transCurrentExp;
	}

	public void setTransCurrentExp(Integer transCurrentExp) {
		this.transCurrentExp = transCurrentExp;
	}

	public String getTransReferCurrencyCode() {
		return transReferCurrencyCode;
	}

	public void setTransReferCurrencyCode(String transReferCurrencyCode) {
		this.transReferCurrencyCode = transReferCurrencyCode;
	}

	public Integer getTransReferCurrencyExp() {
		return transReferCurrencyExp;
	}

	public void setTransReferCurrencyExp(Integer transReferCurrencyExp) {
		this.transReferCurrencyExp = transReferCurrencyExp;
	}

}
