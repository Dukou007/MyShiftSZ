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

import java.util.Date;

import com.pax.common.web.form.QueryForm;

public class QueryAuditLogForm extends QueryForm {
	private static final long serialVersionUID = 278446794604506992L;
	private String roleName;
	private Date startTime;
	private Date endTime;
	private Long[] groupIds;

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Long[] getGroupIds() {
		return groupIds;
	}

	public void setGroupIds(Long[] groupIds) {
		this.groupIds = groupIds;
	}
}
