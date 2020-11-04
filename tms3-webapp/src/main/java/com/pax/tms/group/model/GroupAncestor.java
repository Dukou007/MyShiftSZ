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
import javax.persistence.Transient;

import com.pax.common.model.AbstractModel;

@Entity
@IdClass(GroupAncestor.class)
@Table(name = "PUBTGROUP_PARENTS")
public class GroupAncestor extends AbstractModel {

	private static final long serialVersionUID = 1L;
	@Id
	@ManyToOne
	@JoinColumn(name = "GROUP_ID")
	private Group group;

	@Id
	@ManyToOne
	@JoinColumn(name = "PARENT_ID")
	private Group ancestor;
	
	@Transient
    private Long parentId;

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public Group getAncestor() {
		return ancestor;
	}

	public void setAncestor(Group ancestor) {
		this.ancestor = ancestor;
	}
	
	public Long getParentId() {
        if(null != ancestor){
            return ancestor.getId();
        }else{
            return 0l;
        }
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

	public GroupAncestor() {
		super();
	}

	public GroupAncestor(Group group, Group ancestor) {

		this.group = group;
		this.ancestor = ancestor;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ancestor == null) ? 0 : ancestor.hashCode());
		result = prime * result + ((group == null) ? 0 : group.hashCode());
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
		GroupAncestor other = (GroupAncestor) obj;
		if (ancestor == null) {
			if (other.ancestor != null)
				return false;
		} else if (!ancestor.equals(other.ancestor))
			return false;
		if (group == null) {
			if (other.group != null)
				return false;
		} else if (!group.equals(other.group))
			return false;
		return true;
	}

}
