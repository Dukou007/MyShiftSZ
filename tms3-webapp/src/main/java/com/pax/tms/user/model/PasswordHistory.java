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
package com.pax.tms.user.model;

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
@Table(name = "PUBTPWDCHGHST")
public class PasswordHistory extends AbstractModel {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "PUBHPWDHST_ID_GEN")
	@GenericGenerator(name = "PUBHPWDHST_ID_GEN", strategy = "enhanced-table", parameters = {
			@Parameter(name = "table_name", value = "PUBTSEQUENCE"),
			@Parameter(name = "value_column_name", value = "NEXT_VALUE"),
			@Parameter(name = "segment_column_name", value = "SEQ_NAME"),
			@Parameter(name = "segment_value", value = "PUBHPWDHST_ID"),
			@Parameter(name = "increment_size", value = "5"), @Parameter(name = "optimizer", value = "pooled-lo") })
	@Column(name = "HST_ID")
	private Long id;

	@Column(name = "USER_ID")
	private Long userId;

	@Column(name = "CHANGED_DATE")
	private Date changeDate;

	@Column(name = "USER_PWD")
	private String password;

	@Column(name = "ENCRYPT_SALT")
	private String pwdEncSalt;

	@Column(name = "ENCRYPT_ALG")
	private String pwdEncAlg;

	@Column(name = "ENCRYPT_ITERATION_COUNT")
	private Integer pwdEncIt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getChangeDate() {
		return changeDate;
	}

	public void setChangeDate(Date changeDate) {
		this.changeDate = changeDate;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getPwdEncSalt() {
		return pwdEncSalt;
	}

	public void setPwdEncSalt(String pwdEncSalt) {
		this.pwdEncSalt = pwdEncSalt;
	}

	public String getPwdEncAlg() {
		return pwdEncAlg;
	}

	public void setPwdEncAlg(String pwdEncAlg) {
		this.pwdEncAlg = pwdEncAlg;
	}

	public Integer getPwdEncIt() {
		return pwdEncIt;
	}

	public void setPwdEncIt(Integer pwdEncIt) {
		this.pwdEncIt = pwdEncIt;
	}

}
