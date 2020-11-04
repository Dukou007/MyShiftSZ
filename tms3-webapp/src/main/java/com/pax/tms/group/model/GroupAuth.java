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
package com.pax.tms.group.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.pax.common.model.AbstractModel;

@Entity
@IdClass(GroupAuth.class)
@Table(name = "PUBTGROUP_AUTH")
public class GroupAuth extends AbstractModel {

	private static final long serialVersionUID = 1L;
	@Id
	@ManyToOne
	@JoinColumn(name = "GROUP_ID")
	private Group group;

	@Id
	@ManyToOne
	@JoinColumn(name = "AUTH_GROUP_ID")
	private Group authGroup;

	public GroupAuth() {
		super();
	}

	public GroupAuth(Group group, Group authGroup) {
		this.group = group;
		this.authGroup = authGroup;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public Group getAuthGroup() {
		return authGroup;
	}

	public void setAuthGroup(Group authGroup) {
		this.authGroup = authGroup;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((group == null) ? 0 : group.hashCode());
		result = prime * result + ((authGroup == null) ? 0 : authGroup.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GroupAuth other = (GroupAuth) obj;
		if (group == null) {
			if (other.group != null)
				return false;
		} else if (!group.equals(other.group))
			return false;
		if (authGroup == null) {
			if (other.authGroup != null)
				return false;
		} else if (!authGroup.equals(other.authGroup))
			return false;
		return true;
	}
}
