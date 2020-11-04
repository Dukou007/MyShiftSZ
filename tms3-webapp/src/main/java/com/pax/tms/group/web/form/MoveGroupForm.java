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
package com.pax.tms.group.web.form;

import com.pax.common.web.form.BaseForm;

public class MoveGroupForm extends BaseForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long sourceGroupId;
	private Long targetGroupId;

	public Long getSourceGroupId() {
		return sourceGroupId;
	}

	public void setSourceGroupId(Long sourceGroupId) {
		this.sourceGroupId = sourceGroupId;
	}

	public Long getTargetGroupId() {
		return targetGroupId;
	}

	public void setTargetGroupId(Long targetGroupId) {
		this.targetGroupId = targetGroupId;
	}

}
