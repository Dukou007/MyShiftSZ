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
package com.pax.tms.monitor.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.pax.common.model.AbstractModel;

public class GroupRealStatus extends AbstractModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5436637940494018537L;
	public static final String TABLE_NAME = "TMSTTRM_USAGE_STS";
	public static final String INCREMENT_SIZE = "5";
	public static final String LIST_URL = "/index/";
	public static final String TERMINAL_LIST_URL = "/dashboard/oneStatusList";
	public static final String TITLE = "DASHBOARD";
	@Id
	@GeneratedValue(generator = TABLE_NAME + "_ID_GEN")
	@GenericGenerator(name = TABLE_NAME + "_ID_GEN", strategy = "enhanced-table", parameters = {
			@Parameter(name = "table_name", value = "PUBTSEQUENCE"),
			@Parameter(name = "value_column_name", value = "NEXT_VALUE"),
			@Parameter(name = "segment_column_name", value = "SEQ_NAME"),
			@Parameter(name = "segment_value", value = TABLE_NAME + "_SEQ"),
			@Parameter(name = "increment_size", value = INCREMENT_SIZE),
			@Parameter(name = "optimizer", value = "pooled-lo") })
	@Column(name = "ID")
	private Long id;
	@Column(name = "GROUP_ID")
	private Long groupId;
	@Column(name = "GROUP_NAME")
	private String groupName;
	@Column(name = "ITEM_NAME")
	private String itemName;
	@Column(name = "TOTAL_TRMS")
	private int totalTrms;
	@Column(name = "ALERT_SEVERITY")
	private int alertSeverity;
	@Column(name = "ALERT_THRESHOID")
	private String alertThreshold;
	@Column(name = "ALERT_VALUE")
	private int alertValue;
	@Column(name = "ABNORMAL_TRMS")
	private int abnormalTrms;
	@Column(name = "NORMAL_TRMS")
	private int normalTrms;
	@Column(name = "UNKNOWN_TRMS")
	private int unKnownTrms;
	@Column(name = "CREATE_DATE")
	private Date createDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public int getAlertSeverity() {
		return alertSeverity;
	}

	public void setAlertSeverity(int alertSeverity) {
		this.alertSeverity = alertSeverity;
	}

	public String getAlertThreshold() {
		return alertThreshold;
	}

	public void setAlertThreshold(String alertThreshold) {
		this.alertThreshold = alertThreshold;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public int getTotalTrms() {
		return totalTrms;
	}

	public void setTotalTrms(int totalTrms) {
		this.totalTrms = totalTrms;
	}

	public int getAlertValue() {
		return alertValue;
	}

	public void setAlertValue(int alertValue) {
		this.alertValue = alertValue;
	}

	public int getAbnormalTrms() {
		return abnormalTrms;
	}

	public void setAbnormalTrms(int abnormalTrms) {
		this.abnormalTrms = abnormalTrms;
	}

	public int getNormalTrms() {
		return normalTrms;
	}

	public void setNormalTrms(int normalTrms) {
		this.normalTrms = normalTrms;
	}

	public int getUnKnownTrms() {
		return unKnownTrms;
	}

	public void setUnKnownTrms(int unKnownTrms) {
		this.unKnownTrms = unKnownTrms;
	}

}
