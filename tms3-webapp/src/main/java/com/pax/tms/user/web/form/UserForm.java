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

import com.pax.common.web.form.AddressForm;

public class UserForm extends AddressForm {

	private static final long serialVersionUID = -4960068345926707615L;

	private Long userId;

	private String username;

	private String fullname;

	private String email;

	private String phone;

	private Date expirationDate;

	private String description;

	private Long[] roleIds;

	private String[] groupPaths;

	private Long[] groupIds;
	
	private String status;
	
	private boolean ldapEnabled;
	

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long[] getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(Long[] roleIds) {
		this.roleIds = roleIds;
	}

	public String[] getGroupPaths() {
		return groupPaths;
	}

	public void setGroupPaths(String[] groupPaths) {
		this.groupPaths = groupPaths;
	}

	public Long[] getGroupIds() {
		return groupIds;
	}

	public void setGroupIds(Long[] groupIds) {
		this.groupIds = groupIds;
	}

	public boolean isLdapEnabled() {
		return ldapEnabled;
	}

	public void setLdapEnabled(boolean ldapEnabled) {
		this.ldapEnabled = ldapEnabled;
	}
	
	

}
