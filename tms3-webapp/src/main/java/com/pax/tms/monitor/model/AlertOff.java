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

import io.vertx.core.json.Json;

@Entity
@Table(name = "TMSTALERT_OFF")
public class AlertOff extends AbstractModel {

	private static final long serialVersionUID = -1555989223133946673L;
	public static final String LIST_URL ="/alert/alertOff/";
	public static final String TITLE ="ALERT";

	@Id
	@GeneratedValue(generator = "TMSTALERT_OFF_GEN")
	@GenericGenerator(name = "TMSTALERT_OFF_GEN", strategy = "enhanced-table", parameters = {
			@Parameter(name = "table_name", value = "PUBTSEQUENCE"),
			@Parameter(name = "value_column_name", value = "NEXT_VALUE"),
			@Parameter(name = "segment_column_name", value = "SEQ_NAME"),
			@Parameter(name = "segment_value", value = "TMSTALERT_OFF_SEQ"),
			@Parameter(name = "increment_size", value = "5"), @Parameter(name = "optimizer", value = "pooled-lo") })
	@Column(name = "OFF_ID")
	private Long offId;

	@Column(name = "SETTING_ID")
	private Long settingId;

	@Column(name = "REPEAT_TYPE")
	private int repeatType;

	@Column(name = "OFF_DATE")
	private String offDate;
	
	@Column(name = "OFF_START_TIME")
	private String offStartTime;
	
	@Column(name = "OFF_END_TIME")
	private String offEndTime;

	@Column(name = "CREATOR")
	private String creator;

	@Column(name = "CREATE_DATE")
	private Date createDate;

	@Column(name = "MODIFIER")
	private String modifier;

	@Column(name = "MODIFY_DATE")
	private Date modifyDate;
	
	@Override
	public String toString() {
		return Json.encodePrettily(this);
	}

	public Long getOffId() {
		return offId;
	}

	public void setOffId(Long offId) {
		this.offId = offId;
	}

	public Long getSettingId() {
		return settingId;
	}

	public void setSettingId(Long settingId) {
		this.settingId = settingId;
	}

	public int getRepeatType() {
		return repeatType;
	}

	public void setRepeatType(int repeatType) {
		this.repeatType = repeatType;
	}

	public String getOffDate() {
		return offDate;
	}

	public void setOffDate(String offDate) {
		this.offDate = offDate;
	}

	public String getOffStartTime() {
		return offStartTime;
	}

	public void setOffStartTime(String offStartTime) {
		this.offStartTime = offStartTime;
	}

	public String getOffEndTime() {
		return offEndTime;
	}

	public void setOffEndTime(String offEndTime) {
		this.offEndTime = offEndTime;
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
