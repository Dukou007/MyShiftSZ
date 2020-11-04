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
@Table(name = "TMSTTRM_USAGE_MSG")
public class TerminalUsageMessage extends AbstractModel {

	private static final long serialVersionUID = -5052215535415071107L;
	public static final String TABLE_NAME = "TMSTTRM_USAGE_MSG";
	public static final String ID_SEQUENCE_NAME = TABLE_NAME + "_ID";
	public static final String INCREMENT_SIZE = "5";

	@Id
	@GeneratedValue(generator = TABLE_NAME + "_ID_GEN")
	@GenericGenerator(name = TABLE_NAME + "_ID_GEN", strategy = "enhanced-table", parameters = {
			@Parameter(name = "table_name", value = "PUBTSEQUENCE"),
			@Parameter(name = "value_column_name", value = "NEXT_VALUE"),
			@Parameter(name = "segment_column_name", value = "SEQ_NAME"),
			@Parameter(name = "segment_value", value = ID_SEQUENCE_NAME),
			@Parameter(name = "increment_size", value = INCREMENT_SIZE),
			@Parameter(name = "optimizer", value = "pooled-lo") })
	@Column(name = "ID")
	private Long id;

	@Column(name = "TRM_ID")
	private String terminalId;

	@Column(name = "START_TIME")
	private Date startTime;

	@Column(name = "END_TIME")
	private Date endTime;

	@Column(name = "ITEM_NAME")
	private String itemName;

	@Column(name = "ITEM_ERRS")
	private int itemErrors;

	@Column(name = "ITEM_TOTS")
	private int itemTotals;

	@Column(name = "MSG_CYCLE")
	private String msgCycle;

	@Column(name = "CREATE_DATE")
	private Date createDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public int getItemErrors() {
		return itemErrors;
	}

	public void setItemErrors(int itemErrors) {
		this.itemErrors = itemErrors;
	}

	public int getItemTotals() {
		return itemTotals;
	}

	public void setItemTotals(int itemTotals) {
		this.itemTotals = itemTotals;
	}

	public String getMsgCycle() {
		return msgCycle;
	}

	public void setMsgCycle(String msgCycle) {
		this.msgCycle = msgCycle;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
}
