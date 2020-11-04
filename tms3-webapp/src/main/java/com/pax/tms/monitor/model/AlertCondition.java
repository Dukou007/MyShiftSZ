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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.pax.common.model.AbstractModel;

@Entity
@Table(name = "TMSTALERT_CONDITION")
public class AlertCondition extends AbstractModel {

	private static final long serialVersionUID = -8796811285859038985L;
	public static final String LIST_URL = "/alert/alertCondition/";
	public static final String TITLE = "ALERT";

	@Id
	@GeneratedValue(generator = "TMSTALERT_CONDITION_GEN")
	@GenericGenerator(name = "TMSTALERT_CONDITION_GEN", strategy = "enhanced-table", parameters = {
			@Parameter(name = "table_name", value = "PUBTSEQUENCE"),
			@Parameter(name = "value_column_name", value = "NEXT_VALUE"),
			@Parameter(name = "segment_column_name", value = "SEQ_NAME"),
			@Parameter(name = "segment_value", value = "TMSTALERT_CONDITION_SEQ"),
			@Parameter(name = "increment_size", value = "5"), @Parameter(name = "optimizer", value = "pooled-lo") })
	@Column(name = "COND_ID")
	private Long condId;

	@Column(name = "SETTING_ID")
	private Long settingId;

	@Column(name = "ALERT_ITEM")
	private String alertItem;

	@Column(name = "ALERT_SEVERITY")
	private int alertSeverity;

	@Column(name = "ALERT_THRESHOLD")
	private String alertThreshold;

	@Column(name = "ALERT_MESSAGE")
	private String alertMessage;

	@Column(name = "TOPIC_ARN")
	private String topicArn;

	@Column(name = "CREATOR")
	private String creator;

	@Column(name = "CREATE_DATE")
	private Date createDate;

	@Column(name = "MODIFIER")
	private String modifier;

	@Column(name = "MODIFY_DATE")
	private Date modifyDate;

	@Transient
	private String scbSms;

	@Transient
	private String scbEmail;

	@Transient
	private String groupName;

	public Long getCondId() {
		return condId;
	}

	public void setCondId(Long condId) {
		this.condId = condId;
	}

	public Long getSettingId() {
		return settingId;
	}

	public void setSettingId(Long settingId) {
		this.settingId = settingId;
	}

	public String getAlertItem() {
		return alertItem;
	}

	public void setAlertItem(String alertItem) {
		this.alertItem = alertItem;
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

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public void setAlertMessage(String alertMessage) {
		this.alertMessage = alertMessage;
	}

	public String getAlertMessage() {
		return alertMessage.replace("?0", groupName).replace("?1", alertThreshold);
	}

	public String getTopicArn() {
		return topicArn;
	}

	public void setTopicArn(String topicArn) {
		this.topicArn = topicArn;
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

	public String getScbSms() {
		return scbSms;
	}

	public void setScbSms(String scbSms) {
		this.scbSms = scbSms;
	}

	public String getScbEmail() {
		return scbEmail;
	}

	public void setScbEmail(String scbEmail) {
		this.scbEmail = scbEmail;
	}
}
