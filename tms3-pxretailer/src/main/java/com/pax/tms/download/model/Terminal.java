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

public class Terminal implements Serializable {

    private static final long serialVersionUID = -3849479271316736603L;

    public static final int TERMINAL_STATUS_DISABLED = 0;
    public static final int TERMINAL_STATUS_ENABLED = 1;
    public static final int TERMINAL_STATUS_UNREGISTERED = -1;

    private String modelId;
    private String terminalSn;

    private int terminalStatus;
    private String terminalId;

    private String timeZone;
    private boolean useDaylightTime;
    private boolean syncToServerTime;
    private Date lastSyncTime;

    private String lastSourceIp;
    private Integer online;

    protected String scheduled;
    private Date dwnlStartTm;
    private Date dwnlEndTm;
    protected String dwnlStatus;

    protected Long deployId;
    protected Long pkgId;

    protected Integer privacyShield;
    protected Integer stylus;
    private Integer tampered;
    private Integer sredEnabled;
    private Integer rkiCapable;
    private String localtime;
    protected Date lastAccessTime;

    private TerminalUsageReport usageReport;
    
    private String terminalInstallations;
    
    private String terminalSysmetricKeys;

    private boolean needHms;

    public String getTerminalSn() {
        return terminalSn;
    }

    public void setTerminalSn(String terminalSn) {
        this.terminalSn = terminalSn;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public int getTerminalStatus() {
        return terminalStatus;
    }

    public void setTerminalStatus(int terminalStatus) {
        this.terminalStatus = terminalStatus;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public boolean isUseDaylightTime() {
        return useDaylightTime;
    }

    public void setUseDaylightTime(boolean useDaylightTime) {
        this.useDaylightTime = useDaylightTime;
    }

    public boolean isSyncToServerTime() {
        return syncToServerTime;
    }

    public void setSyncToServerTime(boolean syncToServerTime) {
        this.syncToServerTime = syncToServerTime;
    }

    public Date getLastSyncTime() {
        return lastSyncTime;
    }

    public void setLastSyncTime(Date lastSyncTime) {
        this.lastSyncTime = lastSyncTime;
    }

    public String getLastSourceIp() {
        return lastSourceIp;
    }

    public void setLastSourceIp(String lastSourceIp) {
        this.lastSourceIp = lastSourceIp;
    }

    public Integer getOnline() {
        return online;
    }

    public void setOnline(Integer online) {
        this.online = online;
    }

    public String getScheduled() {
        return scheduled;
    }

    public void setScheduled(String scheduled) {
        this.scheduled = scheduled;
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

    public String getDwnlStatus() {
        return dwnlStatus;
    }

    public void setDwnlStatus(String dwnlStatus) {
        this.dwnlStatus = dwnlStatus;
    }

    public Long getDeployId() {
        return deployId;
    }

    public void setDeployId(Long deployId) {
        this.deployId = deployId;
    }

    public Long getPkgId() {
        return pkgId;
    }

    public void setPkgId(Long pkgId) {
        this.pkgId = pkgId;
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

    public Date getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(Date lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public TerminalUsageReport getUsageReport() {
        return usageReport;
    }

    public void setUsageReport(TerminalUsageReport usageReport) {
        this.usageReport = usageReport;
    }

    public String getTerminalInstallations() {
        return terminalInstallations;
    }

    public void setTerminalInstallations(String terminalInstallations) {
        this.terminalInstallations = terminalInstallations;
    }

	public String getTerminalSysmetricKeys() {
        return terminalSysmetricKeys;
    }

    public void setTerminalSysmetricKeys(String terminalSysmetricKeys) {
        this.terminalSysmetricKeys = terminalSysmetricKeys;
    }

    public void setNeedHms(boolean b) {
        this.needHms = b;
    }

    public boolean isNeedHms() {
        return needHms;
    }

    public boolean isDisabled() {
        return TERMINAL_STATUS_DISABLED == this.terminalStatus;
    }

    public boolean isUnregistered() {
        return TERMINAL_STATUS_UNREGISTERED == this.terminalStatus;
    }

    public Integer getTampered() {
        return tampered;
    }

    public void setTampered(Integer tampered) {
        this.tampered = tampered;
    }

    public Integer getSredEnabled() {
        return sredEnabled;
    }

    public void setSredEnabled(Integer sredEnabled) {
        this.sredEnabled = sredEnabled;
    }

    public Integer getRkiCapable() {
        return rkiCapable;
    }

    public void setRkiCapable(Integer rkiCapable) {
        this.rkiCapable = rkiCapable;
    }

    public String getLocaltime() {
		return localtime;
	}

	public void setLocaltime(String localtime) {
		this.localtime = localtime;
	}

	public boolean isOffline() {
        return online == null || online != TerminalStatus.ONLINE_STATUS;
    }

    public boolean hasScheduledPackage() {
        if (!"1".equals(scheduled)) {
            return false;
        }

        Date now = new Date();
        return !(dwnlStartTm != null && dwnlStartTm.after(now));
    }

    public boolean isScheduledPackageExpired() {
        if (!"1".equals(scheduled)) {
            return false;
        }

        Date now = new Date();
        return dwnlStartTm != null && dwnlStartTm.after(now);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
