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

import com.pax.common.web.form.BaseForm;

public class AssignPrivilegeForm extends BaseForm {

	private static final long serialVersionUID = 6501319693314299247L;

	private String[] assignedPrivileges;

	public String[] getAssignedPrivileges() {
		return assignedPrivileges;
	}

	public void setAssignedPrivileges(String[] assignedPrivileges) {
		this.assignedPrivileges = assignedPrivileges;
	}

}
