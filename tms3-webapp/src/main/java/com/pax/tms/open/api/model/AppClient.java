package com.pax.tms.open.api.model;

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
@Table(name = "APP_CLIENT")
public class AppClient extends AbstractModel {

	private static final long serialVersionUID = 2785577287084004996L;
	public static final String ID_SEQUENCE_NAME = "APPCLIENT_ID";
	public static final String INCREMENT_SIZE = "1";
	/**
	 * user id
	 */
	@Id
	@GeneratedValue(generator = ID_SEQUENCE_NAME + "_GEN")
	@GenericGenerator(name = ID_SEQUENCE_NAME + "_GEN", strategy = "enhanced-table", parameters = {
			@Parameter(name = "table_name", value = "PUBTSEQUENCE"),
			@Parameter(name = "value_column_name", value = "NEXT_VALUE"),
			@Parameter(name = "segment_column_name", value = "SEQ_NAME"),
			@Parameter(name = "segment_value", value = ID_SEQUENCE_NAME),
			@Parameter(name = "increment_size", value = INCREMENT_SIZE),
			@Parameter(name = "optimizer", value = "pooled-lo") })
	@Column(name = "APP_CLIENT_ID")
	private Long appClientId;

	@Column(name = "USER_ID")
	private Long userId;

	/**
	 * user name
	 */
	@Column(name = "USER_NAME")
	private String userName;

	/**
	 * user name
	 */
	@Column(name = "APP_KEY")
	private String appKey;

	/**
	 * the last modification time
	 */
	@Column(name = "UPDATED_ON")
	private Date updatedOn;

	public Long getAppClientId() {
		return appClientId;
	}

	public void setAppClientId(Long appClientId) {
		this.appClientId = appClientId;
	}

	/**
	 * 获取user id
	 *
	 * @return USER_ID - user id
	 */
	public Long getUserId() {
		return userId;
	}

	/**
	 * 设置user id
	 *
	 * @param userId
	 *            user id
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	/**
	 * 获取user name
	 *
	 * @return USER_NAME - user name
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * 设置user name
	 *
	 * @param userName
	 *            user name
	 */
	public void setUserName(String userName) {
		this.userName = userName == null ? null : userName.trim();
	}

	/**
	 * 获取user name
	 *
	 * @return APP_KEY - user name
	 */
	public String getAppKey() {
		return appKey;
	}

	/**
	 * 设置user name
	 *
	 * @param appKey
	 *            user name
	 */
	public void setAppKey(String appKey) {
		this.appKey = appKey == null ? null : appKey.trim();
	}

	/**
	 * 获取the last modification time
	 *
	 * @return UPDATED_ON - the last modification time
	 */
	public Date getUpdatedOn() {
		return updatedOn;
	}

	/**
	 * 设置the last modification time
	 *
	 * @param updatedOn
	 *            the last modification time
	 */
	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}
}