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

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
@Table(name = "TMSTTRMSTATUS")
public class TerminalStatus implements Serializable {

	private static final long serialVersionUID = -7121489293044812493L;

	public static final int TERMINAL_ACCESSORY_DETACHED = 2;
	public static final int TERMINAL_ACCESSORY_ATTACHED = 1;

	public static final int ONLINE_STATUS = 1;
	public static final int OFFLINE_STATUS = 2;
	
	public static final int TERMINAL_SRED_ENCRYPTED = 1;
	public static final int TERMINAL_SRED_NOTENCRYPTED = 0;
	
	public static final int TERMINAL_RKICAPABLE_YES = 1;
    public static final int TERMINAL_RKICAPABLE_NO = 0;

	public static final String NOTUPDATE = "NOTUPDATE";
	public static final String MUSTUPDATE = "MUSTUPDATE";
	public static final String UPDATE_SUCCESS = "SUCCESS";
	public static final String UPDATE_FAILED = "FAILED";

	public static final String TRM_ID_FIELD = "TRM_ID";
	public static final String TRM_SN_FIELD = "TRM_SN";
	public static final String MODEL_ID_FIELD = "MODEL_ID";
	public static final String LAST_CONN_TIME_FIELD = "LAST_CONN_TIME";
	public static final String LAST_DWNL_TIME_FIELD = "LAST_DWNL_TIME";
	public static final String LAST_DWNL_STATUS_FIELD = "LAST_DWNL_STATUS";
	public static final String LAST_ACTV_TIME_FIELD = "LAST_ACTV_TIME";
	public static final String LAST_ACTV_STATUS_FIELD = "LAST_ACTV_STATUS";
	public static final String LAST_SOURCE_IP_FIELD = "LAST_SOURCE_IP";
	public static final String TAMPER_FIELD = "TAMPER";
	public static final String PRIVACY_SHIELD_FIELD = "PRIVACY_SHIELD";
	public static final String STYLUS_FIELD = "STYLUS";
	public static final String ONLINE_FIELD = "IS_ONLINE";
	public static final String LAST_DWNL_TASK_FIELD = "LAST_DWNL_TASK";
	public static final String SRED_FIELD = "SRED";
	public static final String RKI_FIELD = "RKI";

	@Id
	@Column(name = TRM_ID_FIELD)
	private String trmId;

	@Column(name = TRM_SN_FIELD)
	private String trmSn;

	@Column(name = MODEL_ID_FIELD)
	private String modelId;

	@Column(name = LAST_DWNL_TIME_FIELD)
	private Date lastDwnlTime;

	@Column(name = LAST_DWNL_STATUS_FIELD)
	private String lastDwnlStatus;

	@Column(name = LAST_ACTV_TIME_FIELD)
	private Date lastActvTime;

	@Column(name = LAST_ACTV_STATUS_FIELD)
	private String lastActvStatus;

	@Column(name = LAST_CONN_TIME_FIELD)
	private Date lastConnTime;

	@Column(name = LAST_SOURCE_IP_FIELD)
	private String lastSourceIp;

	@Column(name = ONLINE_FIELD)
	private Integer onlineStatus;

	@Column(name = TAMPER_FIELD)
	private String tamper;

	@Column(name = PRIVACY_SHIELD_FIELD)
	private Integer privacyShield;

	@Column(name = STYLUS_FIELD)
	private Integer stylus;
	
	@Column(name = SRED_FIELD)
    private Integer sred;
	
	@Column(name = RKI_FIELD)
    private Integer rki;

	@Column(name = LAST_DWNL_TASK_FIELD)
	private Long lastDwnlTask;

	public TerminalStatus() {
	}

	public TerminalStatus(Terminal terminal) {
		this.trmId = terminal.getTerminalId();
		this.trmSn = terminal.getTerminalSn();
		this.modelId = terminal.getModelId();
	}

	public String getTrmId() {
		return trmId;
	}

	public void setTrmId(String trmId) {
		this.trmId = trmId;
	}

	public String getTrmSn() {
		return trmSn;
	}

	public void setTrmSn(String trmSn) {
		this.trmSn = trmSn;
	}

	public String getModelId() {
		return modelId;
	}

	public void setModelId(String modelId) {
		this.modelId = modelId;
	}

	public Date getLastDwnlTime() {
		return lastDwnlTime;
	}

	public void setLastDwnlTime(Date lastDwnlTime) {
		this.lastDwnlTime = lastDwnlTime;
	}

	public String getLastDwnlStatus() {
		return lastDwnlStatus;
	}

	public void setLastDwnlStatus(String lastDwnlStatus) {
		this.lastDwnlStatus = lastDwnlStatus;
	}

	public Date getLastActvTime() {
		return lastActvTime;
	}

	public void setLastActvTime(Date lastActvTime) {
		this.lastActvTime = lastActvTime;
	}

	public String getLastActvStatus() {
		return lastActvStatus;
	}

	public void setLastActvStatus(String lastActvStatus) {
		this.lastActvStatus = lastActvStatus;
	}

	public Date getLastConnTime() {
		return lastConnTime;
	}

	public void setLastConnTime(Date lastConnTime) {
		this.lastConnTime = lastConnTime;
	}

	public String getLastSourceIp() {
		return lastSourceIp;
	}

	public void setLastSourceIp(String lastSourceIp) {
		this.lastSourceIp = lastSourceIp;
	}

	public Integer getOnline() {
		return onlineStatus;
	}

	public void setOnline(Integer online) {
		this.onlineStatus = online;
	}

	public String getTamper() {
		return tamper;
	}

	public void setTamper(String tamper) {
		this.tamper = tamper;
	}

	public Integer getPrivacyShield() {
		return privacyShield;
	}

	public void setPrivacyShield(Integer privacyShield) {
		this.privacyShield = privacyShield;
	}

	public Integer getStylus() {
		return stylus;
	}

	public void setStylus(Integer stylus) {
		this.stylus = stylus;
	}

	public Integer getSred() {
        return sred;
    }

    public void setSred(Integer sred) {
        this.sred = sred;
    }
    
    public Integer getRki() {
        return rki;
    }

    public void setRki(Integer rki) {
        this.rki = rki;
    }

    public Long getLastDwnlTask() {
		return lastDwnlTask;
	}

	public void setLastDwnlTask(Long lastDwnlTask) {
		this.lastDwnlTask = lastDwnlTask;
	}

	public boolean isOffline() {
		return onlineStatus == null || onlineStatus != TerminalStatus.ONLINE_STATUS;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
