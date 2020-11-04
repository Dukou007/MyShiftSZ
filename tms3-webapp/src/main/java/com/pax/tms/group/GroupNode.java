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
package com.pax.tms.group;

import java.io.Serializable;
import java.util.Set;

public class GroupNode implements Serializable {

	private static final long serialVersionUID = -2336835852531580595L;

	private long groupId;

	private Long linkTo;

	private Set<GroupNode> subGroups;

	public GroupNode(long groupId) {
		super();
		this.groupId = groupId;
	}

	public GroupNode(long groupId, Long linkTo) {
		super();
		this.groupId = groupId;
		this.linkTo = linkTo;
	}

	public long getGroupId() {
		return groupId;
	}

	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}

	public Long getLinkTo() {
		return linkTo;
	}

	public void setLinkTo(Long linkTo) {
		this.linkTo = linkTo;
	}

	public Set<GroupNode> getSubGroups() {
		return subGroups;
	}

	public void setSubGroups(Set<GroupNode> subGroups) {
		this.subGroups = subGroups;
	}

}
