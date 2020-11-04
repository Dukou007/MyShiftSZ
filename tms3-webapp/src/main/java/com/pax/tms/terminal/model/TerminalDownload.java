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
package com.pax.tms.terminal.model;

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
import com.pax.tms.deploy.model.Deploy;
import com.pax.tms.deploy.model.DownOrActvStatus;
import com.pax.tms.res.model.Model;
import com.pax.tms.terminal.model.Terminal;

/**
 * @author Jaden.C
 *
 */
@Entity
@Table(name = "TMSTTRMDWNL")
public class TerminalDownload extends AbstractModel {

	private static final long serialVersionUID = -8331013099440228683L;
	public static final String ID_SEQUENCE_NAME = "TMSTTRMDWNL_ID";
	public static final String INCREMENT_SIZE = "5";

	@Id
	@GeneratedValue(generator = ID_SEQUENCE_NAME + "_GEN")
	@GenericGenerator(name = ID_SEQUENCE_NAME + "_GEN", strategy = "enhanced-table", parameters = {
			@Parameter(name = "table_name", value = "PUBTSEQUENCE"),
			@Parameter(name = "value_column_name", value = "NEXT_VALUE"),
			@Parameter(name = "segment_column_name", value = "SEQ_NAME"),
			@Parameter(name = "segment_value", value = ID_SEQUENCE_NAME),
			@Parameter(name = "increment_size", value = INCREMENT_SIZE),
			@Parameter(name = "optimizer", value = "pooled-lo") })
	@Column(name = "LOG_ID")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TRM_ID")
	private Terminal tid;

	@Column(name = "TRM_SN")
	private String tsn;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MODEL_ID")
	private Model model;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DEPLOY_ID")
	private Deploy deploy;

	@Column(name = "PKG_NAME")
	private String pkgName;

	@Column(name = "PKG_VERSION")
	private String pkgVersion;

	@Column(name = "PKG_TYPE")
	private String pkgType;

	@Column(name = "DWNL_START_TIME")
	private Date downStartTime;

	@Column(name = "DWNL_END_TIME")
	private Date downEndTime;

	@Column(name = "DWNL_SCHEDULE")
	private Date downSchedule;

	@Column(name = "ACTV_SCHEDULE")
	private Date actvSchedule;

	@Column(name = "DWNL_STATUS")
	@Enumerated(EnumType.STRING)
	private DownOrActvStatus downStatus = DownOrActvStatus.PENDING;

	@Column(name = "ACTV_TIME")
	private Date actvTime;

	@Column(name = "ACTV_STATUS")
	@Enumerated(EnumType.STRING)
	private DownOrActvStatus actvStatus = DownOrActvStatus.PENDING;

	@Column(name = "CREATE_DATE")
	private Date createDate;

	@Column(name = "MODIFY_DATE")
	private Date modifyDate;
	
	@Column(name="EXPIRE_DATE")
	private Date expireDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Terminal getTid() {
		return tid;
	}

	public void setTid(Terminal tid) {
		this.tid = tid;
	}

	public String getTsn() {
		return tsn;
	}

	public void setTsn(String tsn) {
		this.tsn = tsn;
	}

	public String getPkgName() {
		return pkgName;
	}

	public void setPkgName(String pkgName) {
		this.pkgName = pkgName;
	}

	public String getPkgVersion() {
		return pkgVersion;
	}

	public void setPkgVersion(String pkgVersion) {
		this.pkgVersion = pkgVersion;
	}

	public String getPkgType() {
		return pkgType;
	}

	public void setPkgType(String pkgType) {
		this.pkgType = pkgType;
	}

	public Date getDownStartTime() {
		return downStartTime;
	}

	public void setDownStartTime(Date downStartTime) {
		this.downStartTime = downStartTime;
	}

	public Date getDownEndTime() {
		return downEndTime;
	}

	public void setDownEndTime(Date downEndTime) {
		this.downEndTime = downEndTime;
	}

	public Date getDownSchedule() {
		return downSchedule;
	}

	public void setDownSchedule(Date downSchedule) {
		this.downSchedule = downSchedule;
	}

	public Date getActvSchedule() {
		return actvSchedule;
	}

	public void setActvSchedule(Date actvSchedule) {
		this.actvSchedule = actvSchedule;
	}

	public DownOrActvStatus getDownStatus() {
		return downStatus;
	}

	public void setDownStatus(DownOrActvStatus downStatus) {
		this.downStatus = downStatus;
	}

	public DownOrActvStatus getActvStatus() {
		return actvStatus;
	}

	public void setActvStatus(DownOrActvStatus actvStatus) {
		this.actvStatus = actvStatus;
	}

	public Date getActvTime() {
		return actvTime;
	}

	public void setActvTime(Date actvTime) {
		this.actvTime = actvTime;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}
	
	public Date getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public Deploy getDeploy() {
		return deploy;
	}

	public void setDeploy(Deploy deploy) {
		this.deploy = deploy;
	}

}
