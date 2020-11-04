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
package com.pax.common.web.form;

public class SystemForm extends BaseForm {

	private static final long serialVersionUID = 5317872180933711361L;

	public static final SystemForm instance = new SystemForm();

	private SystemForm() {
	}

	@Override
	public String getLoginUsername() {
		return "system";
	}

}
