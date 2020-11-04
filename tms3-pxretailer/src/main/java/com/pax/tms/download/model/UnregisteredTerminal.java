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
package com.pax.tms.download.model;

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
@Table(name = "TMSTTRM_UNREG")
public class UnregisteredTerminal extends AbstractModel {

	private static final long serialVersionUID = 2069021498369224511L;

	public static final String ID_SEQUENCE_NAME = "TMSTTRM_UNREG_ID";
	public static final String INCREMENT_SIZE = "20";

	@Id
	@GeneratedValue(generator = ID_SEQUENCE_NAME + "_GEN")
	@GenericGenerator(name = ID_SEQUENCE_NAME + "_GEN", strategy = "enhanced-table", parameters = {
			@Parameter(name = "table_name", value = "PUBTSEQUENCE"),
			@Parameter(name = "value_column_name", value = "NEXT_VALUE"),
			@Parameter(name = "segment_column_name", value = "SEQ_NAME"),
			@Parameter(name = "segment_value", value = ID_SEQUENCE_NAME),
			@Parameter(name = "increment_size", value = INCREMENT_SIZE),
			@Parameter(name = "optimizer", value = "pooled-lo") })
	@Column(name = "ID")
	private Long id;

	@Column(name = "MODEL_ID")
	private String modelId;

	@Column(name = "TRM_SN")
	private String trmSn;

	@Column(name = "TRM_ID")
	private String trmId;

	@Column(name = "SOURCE_IP")
	private String sourceIp;

	@Column(name = "LAST_DATE")
	private Date lastDate;

	@Column(name = "CREATE_DATE")
	private Date createDate;

	public UnregisteredTerminal() {
		super();
	}

	public UnregisteredTerminal(String modelId, String trmSn, String sourceIp, Date lastDate) {
		super();
		this.modelId = modelId;
		this.trmSn = trmSn;
		this.sourceIp = sourceIp;
		this.lastDate = lastDate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getModelId() {
		return modelId;
	}

	public void setModelId(String modelId) {
		this.modelId = modelId;
	}

	public String getTrmSn() {
		return trmSn;
	}

	public void setTrmSn(String trmSn) {
		this.trmSn = trmSn;
	}

	public String getTrmId() {
		return trmId;
	}

	public void setTrmId(String trmId) {
		this.trmId = trmId;
	}

	public String getSourceIp() {
		return sourceIp;
	}

	public void setSourceIp(String sourceIp) {
		this.sourceIp = sourceIp;
	}

	public Date getLastDate() {
		return lastDate;
	}

	public void setLastDate(Date lastDate) {
		this.lastDate = lastDate;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

}
