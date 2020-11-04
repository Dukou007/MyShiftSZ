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
import com.pax.tms.res.model.Model;
import com.pax.tms.res.model.Pkg;
import com.pax.tms.res.model.PkgSchema;

@Entity
@Table(name = "TMSTGROUPDEPLOY_HISTORY")
public class GroupDeployHistory extends AbstractModel {

	private static final long serialVersionUID = 1447006971362838616L;

	public static final String ID_SEQUENCE_NAME = "TMSTDEPLOY_HISTORY_ID";
	public static final String INCREMENT_SIZE = "1";

	@Id
	@GeneratedValue(generator = ID_SEQUENCE_NAME + "_GEN")
	@GenericGenerator(name = ID_SEQUENCE_NAME + "_GEN", strategy = "enhanced-table", parameters = {
			@Parameter(name = "table_name", value = "PUBTSEQUENCE"),
			@Parameter(name = "value_column_name", value = "NEXT_VALUE"),
			@Parameter(name = "segment_column_name", value = "SEQ_NAME"),
			@Parameter(name = "segment_value", value = ID_SEQUENCE_NAME),
			@Parameter(name = "increment_size", value = INCREMENT_SIZE),
			@Parameter(name = "optimizer", value = "pooled-lo") })
	@Column(name = "GROUP_DEPLOY_HIS_ID")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MODEL_ID")
	private Model model;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "GROUP_ID")
	private Group group;

	@Column(name = "DEPLOY_STATUS")
	private Integer status = 1;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PKG_ID")
	private Pkg pkg;

	@Column(name = "PKG_NAME")
	private String pkgName;

	@Column(name = "PKG_VERSION")
	private String pkgVersion;

	@Column(name = "DEPLOY_SOURCE")
	private String deploySource;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SCHEMA_ID")
	private PkgSchema pkgSchema;

	@Column(name = "PARAM_VERSION")
	private String paramVersion;

	@Column(name = "PARAM_SET")
	private String paramSet;

	@Column(name = "DWNL_START_TM")
	private Date dwnlStartTime;

	@Column(name = "DOWN_RETRY_COUNT")
	private Integer downReTryCount;
	@Column(name = "ACTV_RETRY_COUNT")
	private Integer actvReTryCount;

	@Column(name = "DWNL_PERIOD")
	private Integer dwnlPeriod;

	@Column(name = "DWNL_MAX_NUM")
	private Integer dwnlMaxNumber;

	@Column(name = "ACTV_START_TM")
	private Date actvStartTime;

	@Column(name = "DWNL_ORDER")
	private boolean dwnlOrder = false;

	@Column(name = "FORCE_UPDATE")
	private boolean forceUpdate = false;

	@Column(name = "ONLY_PARAM")
	private boolean onlyParam = false;

	@Column(name = "DESCRIPTION")
	private String description;

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

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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

	public Pkg getPkg() {
		return pkg;
	}

	public void setPkg(Pkg pkg) {
		this.pkg = pkg;
	}

	public String getDeploySource() {
		return deploySource;
	}

	public void setDeploySource(String deploySource) {
		this.deploySource = deploySource;
	}

	public PkgSchema getPkgSchema() {
		return pkgSchema;
	}

	public void setPkgSchema(PkgSchema pkgSchema) {
		this.pkgSchema = pkgSchema;
	}

	public String getParamVersion() {
		return paramVersion;
	}

	public void setParamVersion(String paramVersion) {
		this.paramVersion = paramVersion;
	}

	public String getParamSet() {
		return paramSet;
	}

	public void setParamSet(String paramSet) {
		this.paramSet = paramSet;
	}

	public Date getDwnlStartTime() {
		return dwnlStartTime;
	}

	public void setDwnlStartTime(Date dwnlStartTime) {
		this.dwnlStartTime = dwnlStartTime;
	}

	public Integer getDwnlPeriod() {
		return dwnlPeriod;
	}

	public void setDwnlPeriod(Integer dwnlPeriod) {
		this.dwnlPeriod = dwnlPeriod;
	}

	public Integer getDwnlMaxNumber() {
		return dwnlMaxNumber;
	}

	public void setDwnlMaxNumber(Integer dwnlMaxNumber) {
		this.dwnlMaxNumber = dwnlMaxNumber;
	}

	public Date getActvStartTime() {
		return actvStartTime;
	}

	public void setActvStartTime(Date actvStartTime) {
		this.actvStartTime = actvStartTime;
	}

	public boolean isDwnlOrder() {
		return dwnlOrder;
	}

	public void setDwnlOrder(boolean dwnlOrder) {
		this.dwnlOrder = dwnlOrder;
	}

	public Integer getDownReTryCount() {
		return downReTryCount;
	}

	public void setDownReTryCount(Integer downReTryCount) {
		this.downReTryCount = downReTryCount;
	}

	public Integer getActvReTryCount() {
		return actvReTryCount;
	}

	public void setActvReTryCount(Integer actvReTryCount) {
		this.actvReTryCount = actvReTryCount;
	}

	public boolean isForceUpdate() {
		return forceUpdate;
	}

	public void setForceUpdate(boolean forceUpdate) {
		this.forceUpdate = forceUpdate;
	}

	public boolean isOnlyParam() {
		return onlyParam;
	}

	public void setOnlyParam(boolean onlyParam) {
		this.onlyParam = onlyParam;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
