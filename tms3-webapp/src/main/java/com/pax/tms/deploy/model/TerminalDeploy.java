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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.pax.common.model.AbstractModel;
import com.pax.tms.terminal.model.Terminal;

@Entity
@Table(name = "TMSTTRM_DEPLOY")
public class TerminalDeploy extends AbstractModel {

	private static final long serialVersionUID = 5033531735111847208L;

	public static final String ID_SEQUENCE_NAME = "TMSTTRM_DEPLOY_ID";
	public static final String INCREMENT_SIZE = "1";
	public static final String LIST_URL = "/terminalDeploy/list/";
	public static final String DEPLOY_URL = "/terminalDeploy/toDeploy/";
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
	@JoinColumn(name = "TRM_ID")
	private Terminal terminal;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DEPLOY_ID")
	private Deploy deploy;

	@Enumerated(EnumType.STRING)
	@Column(name = "DWNL_STATUS")
	private DownOrActvStatus downStatus = DownOrActvStatus.PENDING;

	@Column(name = "DWNL_TIME")
	private Date dwnlTime;

	@Column(name = "DWNL_SUCC_COUNT")
	private Integer downSuccCount;

	@Column(name = "DWNL_FAIL_COUNT")
	private Integer downFailCount;

	@Column(name = "ACTV_STATUS")
	@Enumerated(EnumType.STRING)
	private DownOrActvStatus actvStatus = DownOrActvStatus.PENDING;

	@Column(name = "ACTV_TIME")
	private Date actvTime;

	@Column(name = "DEPLOY_TIME")
	private Long deployTime;

	@Column(name = "ACTV_FAIL_COUNT")
	private Integer avtvFailCount;

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

	public Terminal getTerminal() {
		return terminal;
	}

	public void setTerminal(Terminal terminal) {
		this.terminal = terminal;
	}

	public Deploy getDeploy() {
		return deploy;
	}

	public void setDeploy(Deploy deploy) {
		this.deploy = deploy;
	}

	public DownOrActvStatus getDownStatus() {
		return downStatus;
	}

	public void setDownStatus(DownOrActvStatus downStatus) {
		this.downStatus = downStatus;
	}

	public void setActvStatus(DownOrActvStatus actvStatus) {
		this.actvStatus = actvStatus;
	}

	public Integer getDownSuccCount() {
		return downSuccCount;
	}

	public void setDownSuccCount(Integer downSuccCount) {
		this.downSuccCount = downSuccCount;
	}

	public Integer getDownFailCount() {
		return downFailCount;
	}

	public void setDownFailCount(Integer downFailCount) {
		this.downFailCount = downFailCount;
	}

	public Integer getAvtvFailCount() {
		return avtvFailCount;
	}

	public void setAvtvFailCount(Integer avtvFailCount) {
		this.avtvFailCount = avtvFailCount;
	}

	public Date getDwnlTime() {
		return dwnlTime;
	}

	public void setDwnlTime(Date dwnlTime) {
		this.dwnlTime = dwnlTime;
	}

	public DownOrActvStatus getActvStatus() {
		return actvStatus;
	}

	public Date getActvTime() {
		return actvTime;
	}

	public void setActvTime(Date actvTime) {
		this.actvTime = actvTime;
	}

	public Long getDeployTime() {
		return deployTime;
	}

	public void setDeployTime(Long deployTime) {
		this.deployTime = deployTime;
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

}
