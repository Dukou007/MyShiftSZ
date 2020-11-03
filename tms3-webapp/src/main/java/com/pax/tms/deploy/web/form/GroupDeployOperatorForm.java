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
package com.pax.tms.deploy.web.form;

import com.pax.common.web.form.BaseForm;

public class GroupDeployOperatorForm extends BaseForm {

	private static final long serialVersionUID = 1L;
	private boolean isInherit;
	private Long deployId;
	private String isInherited;

	public String getIsInherited() {
		return isInherited;
	}

	public void setIsInherited(String isInherited) {
		this.isInherited = isInherited;
	}

	public boolean isInherit() {
		return isInherit;
	}

	public void setInherit(boolean isInherit) {
		this.isInherit = isInherit;
	}

	public Long getDeployId() {
		return deployId;
	}

	public void setDeployId(Long deployId) {
		this.deployId = deployId;
	}

}
