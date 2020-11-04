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
@Table(name = "PUBTAUTHORITY")
// @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Authority extends AbstractModel {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "AUTHORITY_ID_GEN")
	@GenericGenerator(name = "AUTHORITY_ID_GEN", strategy = "enhanced-table", parameters = {
			@Parameter(name = "table_name", value = "PUBTSEQUENCE"),
			@Parameter(name = "value_column_name", value = "NEXT_VALUE"),
			@Parameter(name = "segment_column_name", value = "SEQ_NAME"),
			@Parameter(name = "segment_value", value = "PUBTAUTHORITY_ID"),
			@Parameter(name = "increment_size", value = "1"), @Parameter(name = "optimizer", value = "pooled-lo") })
	@Column(name = "AUTH_ID")
	private Long id;

	@Column(name = "AUTH_CODE")
	private String code;

	@Column(name = "AUTH_NAME")
	private String name;

	@Column(name = "AUTH_DESC")
	private String description;

	@Column(name = "APP_NAME")
	private String application;

	@Column(name = "MODULE_NAME")
	private String module;

	@Column(name = "SORT_ORDER")
	private int order = 0;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "authority")
	private List<RoleAuthority> roleAuthorityList;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public List<RoleAuthority> getRoleAuthorityList() {
		return roleAuthorityList;
	}

	public void setRoleAuthorityList(List<RoleAuthority> roleAuthorityList) {
		this.roleAuthorityList = roleAuthorityList;
	}

}
