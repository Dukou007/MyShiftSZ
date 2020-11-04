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

public class QueryUserForm extends QueryForm {

	private static final long serialVersionUID = 1677982395098384902L;

	private Long activeRoleId;
	private String appName;
	private String username;
	private String fullname;
	private String role;
	private String status;
	private Date expirationDate;
	private Long organizationId;
	private String creator;
	private String underCreator;
	private String email;
	private String searchText;
	private String directory;
	private Long roleId;
	private Long groupId;

	public Long getActiveRoleId() {
		return activeRoleId;
	}

	public void setActiveRoleId(Long activeRoleId) {
		this.activeRoleId = activeRoleId;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getSearchText() {
		return searchText;
	}

	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getUsername() {
		return username;
	}

	public Long getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getUnderCreator() {
		return underCreator;
	}

	public void setUnderCreator(String underCreator) {
		this.underCreator = underCreator;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	@Override
	public Long getGroupId() {
		return groupId;
	}

	@Override
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

}
