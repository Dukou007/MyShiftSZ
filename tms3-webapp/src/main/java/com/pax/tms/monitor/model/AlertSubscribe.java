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

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.pax.common.model.AbstractModel;

@Entity
@Table(name = "TMSTALERT_SBSCRB")
public class AlertSubscribe extends AbstractModel {

	private static final long serialVersionUID = -8908405899518160749L;

	@Id
	@GeneratedValue(generator = "TMSTALERT_SBSCRB_GEN")
	@GenericGenerator(name = "TMSTALERT_SBSCRB_GEN", strategy = "enhanced-table", parameters = {
			@Parameter(name = "table_name", value = "PUBTSEQUENCE"),
			@Parameter(name = "value_column_name", value = "NEXT_VALUE"),
			@Parameter(name = "segment_column_name", value = "SEQ_NAME"),
			@Parameter(name = "segment_value", value = "TMSTALERT_SBSCRB_SEQ"),
			@Parameter(name = "increment_size", value = "5"), @Parameter(name = "optimizer", value = "pooled-lo") })
	@Column(name = "SBSCRB_ID")
	private Long sbscrbId;

	@Column(name = "USER_ID")
	private Long userId;

	@Column(name = "COND_ID")
	private Long condId;

	@Column(name = "SMS")
	private String scbSms;

	@Column(name = "EMAIL")
	private String scbEmail;

	@Column(name = "SUBSCRIBE_ARN")
	private String subscribeArn;

	@Column(name = "CREATOR")
	private String creator;

	@Column(name = "CREATE_DATE")
	private Date createDate;

	@Column(name = "MODIFIER")
	private String modifier;

	@Column(name = "MODIFY_DATE")
	private Date modifyDate;

	public Long getSbscrbId() {
		return sbscrbId;
	}

	public void setSbscrbId(Long sbscrbId) {
		this.sbscrbId = sbscrbId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getCondId() {
		return condId;
	}

	public void setCondId(Long condId) {
		this.condId = condId;
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

	public String getSubscribeArn() {
		return subscribeArn;
	}

	public void setSubscribeArn(String subscribeArn) {
		this.subscribeArn = subscribeArn;
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
