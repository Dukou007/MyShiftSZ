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
@Table(name = "TMSTTRM_BILLING_DETAIL")
public class TerminalBillingDetail extends AbstractModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5973487559765841355L;
	
	public static final String ID_SEQUENCE_NAME = "TMSTTRM_BILLING_DETAIL_ID";
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
	@Column(name = "BILLING_DETAIL_ID")
	private Long id;
	
	@Column(name = "BILLING_DETAIL_GROUP_ID")
	private Long groupId;
	
	@Column(name = "BILLING_DETAIL_GROUP_NAME")
	private String groupName;
	
	@Column(name = "BILLING_DETAIL_MONTH")
	private String month;
	
	@Column(name = "BILLING_DETAIL_TRM_SN")
	private String tsn;
	
	@Column(name = "BILLING_DETAIL_TRM_TYPE")
	private String type;
	
	@Column(name = "LAST_ACCESS_TIME")
	private Date lastAccessTime;
	
	@Column(name = "CREATE_TIME")
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

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getTsn() {
		return tsn;
	}

	public void setTsn(String tsn) {
		this.tsn = tsn;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getLastAccessTime() {
		return lastAccessTime;
	}

	public void setLastAccessTime(Date lastAccessTime) {
		this.lastAccessTime = lastAccessTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Override
	public String toString() {
		return "TerminalBillingDetail [id=" + id + ", groupId=" + groupId + ", groupName=" + groupName + ", month="
				+ month + ", tsn=" + tsn + ", type=" + type + ", lastAccessTime=" + lastAccessTime + ", createTime="
				+ createTime + "]";
	}

}
