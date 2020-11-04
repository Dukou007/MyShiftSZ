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
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.pax.common.model.AbstractModel;
import com.pax.tms.res.model.Model;

@Entity
@Table(name = "tmsttrmstatus_temp")
public class TerminalStatusTemp extends AbstractModel {

	private static final long serialVersionUID = -1526344731464694479L;

	@Id
	@Column(name = "TRM_ID")
	private String tid;

	@ManyToOne
	@JoinColumn(name = "TRM_ID", insertable = false, updatable = false)
	@NotFound(action=NotFoundAction.IGNORE)
	private Terminal terminal;

	@Column(name = "TRM_SN")
	private String tsn;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MODEL_ID")
	@NotFound(action=NotFoundAction.IGNORE)
	private Model model;

	@Column(name = "LAST_CONN_TIME")
	private Date lastConnTime;

	@Column(name = "LAST_DWNL_TIME")
	private Date lastDwnlTime;

	@Column(name = "LAST_DWNL_STATUS")
	private String lastDwnlStatus;

	@Column(name = "LAST_ACTV_TIME")
	private Date lastActvTime;

	@Column(name = "LAST_ACTV_STATUS")
	private String lastActvStatus;

	@Column(name = "LAST_SOURCE_IP")
	private String lastSourceIP;
	
	@Column(name = "IS_ONLINE")
	private Integer isOnline;

	@Column(name = "TAMPER")
	private String tamper;

	@Column(name = "PRIVACY_SHIELD")
	private Integer privacyShield;

	@Column(name = "STYLUS")
	private Integer stylus;
	
	@Column(name = "SRED")
	private Integer sred;
	
	@Column(name = "RKI")
    private Integer rki;
	
	@Column(name = "ONLINE_SINCE")
	private Date onlineSince;
	@Column(name = "OFFLINE_SINCE")
	private Date offlineSince;

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public String getTsn() {
		return tsn;
	}

	public void setTsn(String tsn) {
		this.tsn = tsn;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public Date getLastConnTime() {
		return lastConnTime;
	}

	public void setLastConnTime(Date lastConnTime) {
		this.lastConnTime = lastConnTime;
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

	public String getLastSourceIP() {
		return lastSourceIP;
	}

	public void setLastSourceIP(String lastSourceIP) {
		this.lastSourceIP = lastSourceIP;
	}

	public Terminal getTerminal() {
		return terminal;
	}

	public void setTerminal(Terminal terminal) {
		this.terminal = terminal;
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

	public Integer getIsOnline() {
		return isOnline;
	}

	public void setIsOnline(Integer isOnline) {
		this.isOnline = isOnline;
	}

	public Integer getSred() {
		return sred;
	}
	
	public Integer getRki() {
        return rki;
    }

    public void setRki(Integer rki) {
        this.rki = rki;
    }

    public void setSred(Integer sred) {
		this.sred = sred;
	}

	public Date getOnlineSince() {
		return onlineSince;
	}

	public void setOnlineSince(Date onlineSince) {
		this.onlineSince = onlineSince;
	}

	public Date getOfflineSince() {
		return offlineSince;
	}

	public void setOfflineSince(Date offlineSince) {
		this.offlineSince = offlineSince;
	}

	@Override
	public String toString() {
		return "TerminalStatus [tid=" + tid + ", terminal=" + terminal + ", tsn=" + tsn + ", model=" + model
				+ ", lastConnTime=" + lastConnTime + ", lastDwnlTime=" + lastDwnlTime + ", lastDwnlStatus="
				+ lastDwnlStatus + ", lastActvTime=" + lastActvTime + ", lastActvStatus=" + lastActvStatus
				+ ", lastSourceIP=" + lastSourceIP + ", isOnline=" + isOnline + ", tamper=" + tamper
				+ ", privacyShield=" + privacyShield + ", stylus=" + stylus + ", sred=" + sred + ", onlineSince="
				+ onlineSince + ", offlineSince=" + offlineSince + "]";
	}
	
}
