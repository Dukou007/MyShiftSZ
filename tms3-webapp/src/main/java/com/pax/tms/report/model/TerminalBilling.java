package com.pax.tms.report.model;

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
@Table(name = "TMSTTRM_BILLING")
public class TerminalBilling extends AbstractModel {

	private static final long serialVersionUID = 6662549625054832436L;

	
	
	public static final String ID_SEQUENCE_NAME = "TMSTTRM_BILLING_ID";
	private static final String INCREMENT_SIZE = "1";
	
	@Id
	@GeneratedValue(generator = ID_SEQUENCE_NAME + "_GEN")
	@GenericGenerator(name = ID_SEQUENCE_NAME + "_GEN", strategy = "enhanced-table", parameters = {
			@Parameter(name = "table_name", value = "PUBTSEQUENCE"),
			@Parameter(name = "value_column_name", value = "NEXT_VALUE"),
			@Parameter(name = "segment_column_name", value = "SEQ_NAME"),
			@Parameter(name = "segment_value", value = ID_SEQUENCE_NAME),
			@Parameter(name = "increment_size", value = INCREMENT_SIZE),
			@Parameter(name = "optimizer", value = "pooled-lo") })
	@Column(name = "BILLING_ID")
	private Long id;
	
	@Column(name = "BILLING_GROUP_ID")
	private Long groupId;
	
	@Column(name = "BILLING_MONTH")
	private String month;
	
	@Column(name = "BILLING_CONNECTED_DEVICES")
	private Long connectedDevices;
	
	@Column(name = "BILLING_STATEMENT")
	private String statement;
	
	@Column(name = "BILLING_CREATE_TIME")
	private Date createTime;

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

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public Long getConnectedDevices() {
		return connectedDevices;
	}

	public void setConnectedDevices(Long connectedDevices) {
		this.connectedDevices = connectedDevices;
	}

	public String getStatement() {
		return statement;
	}

	public void setStatement(String statement) {
		this.statement = statement;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Override
	public String toString() {
		return "TerminalBilling [id=" + id + ", groupId=" + groupId + ", month=" + month + ", connectedDevices="
				+ connectedDevices + ", statement=" + statement + ", createTime=" + createTime + "]";
	}

}
