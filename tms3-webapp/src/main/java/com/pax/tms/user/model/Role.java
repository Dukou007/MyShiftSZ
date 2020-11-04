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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.pax.common.model.AbstractModel;

@Entity
@Table(name = "PUBTROLE")
public class Role extends AbstractModel {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "PUBTROLE_ID_GEN")
	@GenericGenerator(name = "PUBTROLE_ID_GEN", strategy = "enhanced-table", parameters = {
			@Parameter(name = "table_name", value = "PUBTSEQUENCE"),
			@Parameter(name = "value_column_name", value = "NEXT_VALUE"),
			@Parameter(name = "segment_column_name", value = "SEQ_NAME"),
			@Parameter(name = "segment_value", value = "PUBTROLE_ID"), @Parameter(name = "increment_size", value = "5"),
			@Parameter(name = "optimizer", value = "pooled-lo") })
	@Column(name = "ROLE_ID")
	private Long id;

	@Column(name = "ROLE_NAME")
	private String name;

	@Column(name = "ROLE_DESC")
	private String description;

	@Column(name = "IS_SYS")
	private boolean sys;

	@Column(name = "APP_NAME")
	private String appName;

	@Column(name = "CREATOR")
	private String creator;

	@Column(name = "CREATE_DATE")
	private Date createDate;

	@Column(name = "MODIFIER")
	private String modifier;

	@Column(name = "MODIFY_DATE")
	private Date modifyDate;

	@OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
	private List<UserRole> userRoleList;

	@OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
	private List<RoleAuthority> roleAuthorityList;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isSys() {
		return sys;
	}

	public void setSys(boolean sys) {
		this.sys = sys;
	}

	public List<UserRole> getUserRoleList() {
		return userRoleList;
	}

	public void setUserRoleList(List<UserRole> userRoleList) {
		this.userRoleList = userRoleList;
	}

	public List<RoleAuthority> getRoleAuthorityList() {
		return roleAuthorityList;
	}

	public void setRoleAuthorityList(List<RoleAuthority> roleAuthorityList) {
		this.roleAuthorityList = roleAuthorityList;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	public boolean hasPrivilege(long privilegeId) {
		if (roleAuthorityList == null) {
			return false;
		}
		for (RoleAuthority roleAuthority : roleAuthorityList) {
			if (roleAuthority.getAuthority().getId().equals(privilegeId)) {
				return true;
			}
		}
		return false;
	}

	public void addAuthority(RoleAuthority roleAuth) {
		if (roleAuthorityList == null) {
			roleAuthorityList = new ArrayList<RoleAuthority>();
		}
		roleAuth.setRole(this);
		roleAuthorityList.add(roleAuth);
	}
}
