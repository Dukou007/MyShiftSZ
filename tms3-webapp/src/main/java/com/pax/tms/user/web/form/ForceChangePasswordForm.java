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
package com.pax.tms.user.web.form;

import org.hibernate.validator.constraints.NotBlank;

@SuppressWarnings("serial")
public class ForceChangePasswordForm extends ChangePasswordForm {
	@NotBlank(message = "{forceChangePassword.request.illegal}")
	private String random;

	public String getRandom() {
		return random;
	}

	public void setRandom(String random) {
		this.random = random;
	}
}
