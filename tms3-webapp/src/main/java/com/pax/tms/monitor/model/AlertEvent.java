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
@Table(name = "TMSTALERT_EVENT")
public class AlertEvent extends AbstractModel {

	private static final long serialVersionUID = -2899831824652956876L;

	public static final String ID_SEQUENCE_NAME = "TMSTALERT_EVENT_ID";
	public static final String LIST_URL = "/events/alertEvents/";
	private static final String INCREMENT_SIZE = "1";
	
	@Id
	@GeneratedValue(generator = "TMSTALERT_EVENT_GEN")
	@GenericGenerator(name = "TMSTALERT_EVENT_GEN", strategy = "enhanced-table", parameters = {
			@Parameter(name = "table_name", value = "PUBTSEQUENCE"),
			@Parameter(name = "value_column_name", value = "NEXT_VALUE"),
			@Parameter(name = "segment_column_name", value = "SEQ_NAME"),
			@Parameter(name = "segment_value", value = ID_SEQUENCE_NAME),
			@Parameter(name = "increment_size", value = INCREMENT_SIZE),
			@Parameter(name = "optimizer", value = "pooled-lo") })
	@Column(name = "EVENT_ID")
	private Long eventId;

	@Column(name = "COND_ID")
	private Long condId;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "GROUP_ID")
	private Group group;

	@Column(name = "ALERT_TM")
	private Date alertTime;

	@Column(name = "ALERT_VALUE")
	private String alertValue;

	@Column(name = "ALERT_SEVERITY")
	private int alertSeverity;

	@Column(name = "ALERT_MESSAGE")
	private String alertMsg;

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

	public Date getAlertTime() {
		return alertTime;
	}

	public void setAlertTime(Date alertTime) {
		this.alertTime = alertTime;
	}

	public String getAlertValue() {
		return alertValue;
	}

	public void setAlertValue(String alertValue) {
		this.alertValue = alertValue;
	}

	public String getAlertMsg() {
		return alertMsg;
	}

	public void setAlertMsg(String alertMsg) {
		this.alertMsg = alertMsg;
	}

	public Long getCondId() {
		return condId;
	}

	public void setCondId(Long condId) {
		this.condId = condId;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public int getAlertSeverity() {
		return alertSeverity;
	}

	public void setAlertSeverity(int alertSeverity) {
		this.alertSeverity = alertSeverity;
	}
}
