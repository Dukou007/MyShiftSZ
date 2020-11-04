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

@Entity
@Table(name = "PUBTROLE_AUTHORITY")
public class RoleAuthority extends AbstractModel implements Serializable{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "PUBTROLE_AUTHORITY_ID_GEN")
	@GenericGenerator(name = "PUBTROLE_AUTHORITY_ID_GEN", strategy = "enhanced-table", parameters = {
			@Parameter(name = "table_name", value = "PUBTSEQUENCE"),
			@Parameter(name = "value_column_name", value = "NEXT_VALUE"),
			@Parameter(name = "segment_column_name", value = "SEQ_NAME"),
			@Parameter(name = "segment_value", value = "PUBTROLE_AUTHORITY_ID"),
			@Parameter(name = "increment_size", value = "50"), @Parameter(name = "optimizer", value = "pooled-lo") })
	@Column(name = "REL_ID")
	private Long id;

	@ManyToOne()
	@JoinColumn(name = "ROLE_ID")
	private Role role;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne()
	@JoinColumn(name = "AUTH_ID")
	private Authority authority;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Authority getAuthority() {
		return authority;
	}

	public void setAuthority(Authority authority) {
		this.authority = authority;
	}

	public Long getAuthId() {
		if (authority != null) {
			return authority.getId();
		}
		return null;
	}

}
