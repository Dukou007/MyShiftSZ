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

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.pax.tms.deploy.model.DownOrActvStatus;

public class Deployment implements Serializable {

	private static final long serialVersionUID = 8474121329987171239L;

	private Long relId;
	private String trmId;

	private Long deployId;
	private Date dwnlStartTm;
	private Date dwnlEndTm;
	private int dwnlFailCount;
	private String dwnlStatus;
	private Date actvStartTm;
	private String actvStatus;

	private Long pkgId;
	private String pkgName;
	private String pkgVersion;
	private String pkgType;
	private String pgmType;

	private boolean isHistoryRecord;

	public Long getRelId() {
		return relId;
	}

	public void setRelId(Long relId) {
		this.relId = relId;
	}

	public String getTrmId() {
		return trmId;
	}

	public void setTrmId(String trmId) {
		this.trmId = trmId;
	}

	public Long getDeployId() {
		return deployId;
	}

	public void setDeployId(Long deployId) {
		this.deployId = deployId;
	}

	public Date getDwnlStartTm() {
		return dwnlStartTm;
	}

	public void setDwnlStartTm(Date dwnlStartTm) {
		this.dwnlStartTm = dwnlStartTm;
	}

	public Date getDwnlEndTm() {
		return dwnlEndTm;
	}

	public void setDwnlEndTm(Date dwnlEndTm) {
		this.dwnlEndTm = dwnlEndTm;
	}

	public int getDwnlFailCount() {
		return dwnlFailCount;
	}

	public void setDwnlFailCount(int dwnlFailCount) {
		this.dwnlFailCount = dwnlFailCount;
	}

	public String getDwnlStatus() {
		return dwnlStatus;
	}

	public void setDwnlStatus(String dwnlStatus) {
		this.dwnlStatus = dwnlStatus;
	}

	public Date getActvStartTm() {
		return actvStartTm;
	}

	public void setActvStartTm(Date actvStartTm) {
		this.actvStartTm = actvStartTm;
	}

	public String getActvStatus() {
		return actvStatus;
	}

	public void setActvStatus(String actvStatus) {
		this.actvStatus = actvStatus;
	}

	public Long getPkgId() {
		return pkgId;
	}

	public void setPkgId(Long pkgId) {
		this.pkgId = pkgId;
	}

	public String getPkgName() {
		return pkgName;
	}

	public void setPkgName(String pkgName) {
		this.pkgName = pkgName;
	}

	public String getPkgVersion() {
		return pkgVersion;
	}

	public void setPkgVersion(String pkgVersion) {
		this.pkgVersion = pkgVersion;
	}

	public String getPkgType() {
		return pkgType;
	}

	public void setPkgType(String pkgType) {
		this.pkgType = pkgType;
	}

	public String getPgmType() {
		return pgmType;
	}

	public void setPgmType(String pgmType) {
		this.pgmType = pgmType;
	}

	public boolean isInEffect() {
		if (DownOrActvStatus.DOWNLOADING.name().equals(dwnlStatus)) {
			return true;
		}

		Date now = new Date();
		if (dwnlStartTm != null && dwnlStartTm.after(now)) {
			return false;
		}

		if (dwnlEndTm != null && dwnlEndTm.before(now)) {
			return false;
		}

		return true;
	}

	public boolean isHistoryRecord() {
		return isHistoryRecord;
	}

	public void setHistoryRecord(boolean isHistoryRecord) {
		this.isHistoryRecord = isHistoryRecord;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
