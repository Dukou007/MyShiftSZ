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
package com.pax.tms.user.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Parameter;

import com.pax.common.model.AbstractModel;
import com.pax.tms.group.model.Group;

@Entity
@Table(name = "PUBTUSER_GROUP")
// @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class UserGroup extends AbstractModel implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "PUBTUSER_GROUP_ID_GEN")
	@GenericGenerator(name = "PUBTUSER_GROUP_ID_GEN", strategy = "enhanced-table", parameters = {
			@Parameter(name = "table_name", value = "PUBTSEQUENCE"),
			@Parameter(name = "value_column_name", value = "NEXT_VALUE"),
			@Parameter(name = "segment_column_name", value = "SEQ_NAME"),
			@Parameter(name = "segment_value", value = "PUBTUSER_GROUP_ID"),
			@Parameter(name = "increment_size", value = "10"), @Parameter(name = "optimizer", value = "pooled-lo") })
	@Column(name = "REL_ID")
	private Long id;

	@ManyToOne()
	@JoinColumn(name = "USER_ID")
	private User user;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne()
	@JoinColumn(name = "GROUP_ID")
	private Group group;

	@Column(name = "IS_DEFAULT")
	private boolean defaultGroup;

	@Column(name = "LAST_ACCESS_TIME")
	private Date lastAcessTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public boolean isDefaultGroup() {
		return defaultGroup;
	}

	public void setDefaultGroup(boolean defaultGroup) {
		this.defaultGroup = defaultGroup;
	}

	public Date getLastAcessTime() {
		return lastAcessTime;
	}

	public void setLastAcessTime(Date lastAcessTime) {
		this.lastAcessTime = lastAcessTime;
	}

	public Long getGroupId() {
		if (group != null) {
			return group.getId();
		}
		return null;
	}

}
