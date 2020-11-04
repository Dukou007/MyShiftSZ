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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.pax.common.model.AbstractModel;
import com.pax.tms.deploy.model.TerminalDeploy;

@Entity
@Table(name = "TMSTPARAMETER_HISTORY")
public class ParameterChangeHistory extends AbstractModel {

	private static final long serialVersionUID = -8596459171113071013L;

	public static final String ID_SEQUENCE_NAME = "TMSTPARAMETER_HISTORY_ID";
	
	@Id
	@GeneratedValue(generator = "TMSTPARAMETER_HISTORY_ID_GEN")
	@GenericGenerator(name = "TMSTPARAMETER_HISTORY_ID_GEN", strategy = "enhanced-table", parameters = {
			@Parameter(name = "table_name", value = "PUBTSEQUENCE"),
			@Parameter(name = "value_column_name", value = "NEXT_VALUE"),
			@Parameter(name = "segment_column_name", value = "SEQ_NAME"),
			@Parameter(name = "segment_value", value = ID_SEQUENCE_NAME),
			@Parameter(name = "increment_size", value = "5"), @Parameter(name = "optimizer", value = "pooled-lo") })
	@Column(name = "HIS_ID")
	private Long id;

	@Column(name = "TRM_ID")
	private String tid;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "REL_ID")
	private TerminalDeploy trmDeploy;

	@Column(name = "PARAMETER")
	private String parameter;

	@Column(name = "OLD_VALUE")
	private String oldValue;

	@Column(name = "NEW_VALUE")
	private String newValue;

	@Column(name = "CREATOR")
	private String creator;

	@Column(name = "CREATE_DATE")
	private Date createDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public TerminalDeploy getTrmDeploy() {
		return trmDeploy;
	}

	public void setTrmDeploy(TerminalDeploy trmDeploy) {
		this.trmDeploy = trmDeploy;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	public String getOldValue() {
		return oldValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
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

}
