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
package com.pax.tms.deploy.model;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.pax.common.model.AbstractModel;
import com.pax.tms.group.model.Group;

@Entity
@Table(name = "TMSTGROUP_DEPLOY")
public class GroupDeploy extends AbstractModel {

	private static final long serialVersionUID = 3183438153327689501L;

	public static final String ID_SEQUENCE_NAME = "TMSTGROUP_DEPLOY_ID";
	public static final String INCREMENT_SIZE = "1";
	public static final String LIST_URL = "/groupDeploy/list/";
	public static final String DEPLOY_URL = "/groupDeploy/toDeploy/";
	public static final String KEY_LIST_URL = "/groupDeploykey/list/";
    public static final String KEY_DEPLOY_URL = "/groupDeploykey/toDeploy/";
	public static final String TITLE = "PACKAGE";
	public static final String KEY_TITLE = "OFFLINEKEY";

	@Id
	@GeneratedValue(generator = ID_SEQUENCE_NAME + "_GEN")
	@GenericGenerator(name = ID_SEQUENCE_NAME + "_GEN", strategy = "enhanced-table", parameters = {
			@Parameter(name = "table_name", value = "PUBTSEQUENCE"),
			@Parameter(name = "value_column_name", value = "NEXT_VALUE"),
			@Parameter(name = "segment_column_name", value = "SEQ_NAME"),
			@Parameter(name = "segment_value", value = ID_SEQUENCE_NAME),
			@Parameter(name = "increment_size", value = INCREMENT_SIZE),
			@Parameter(name = "optimizer", value = "pooled-lo") })
	@Column(name = "REL_ID")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "GROUP_ID")
	private Group group;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DEPLOY_ID")
	private Deploy deploy;
	
	@Column(name = "DEPLOY_TIME")
	private Long deployTime;

	@Column(name = "CREATOR")
	private String creator;

	@Column(name = "CREATE_DATE")
	private Date createDate;

	@Column(name = "MODIFIER")
	private String modifier;

	@Column(name = "MODIFY_DATE")
	private Date modifyDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public Deploy getDeploy() {
		return deploy;
	}

	public void setDeploy(Deploy deploy) {
		this.deploy = deploy;
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

	public Long getDeployTime() {
		return deployTime;
	}

	public void setDeployTime(Long deployTime) {
		this.deployTime = deployTime;
	}
	

}
