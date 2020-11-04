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
package com.pax.tms.pxretailer.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pax.tms.download.model.TerminalUsageReport;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HmsRequest extends BaseRequest {

	private static final String UPDATE_INFORMATION = "UPDATEINFORMATION";
	private static final long serialVersionUID = 8585321099031506911L;

	private TerminalState terminalState;
	private TerminalUsageReport usageReport;
	private TerminalUsage[] terminalUsage;
	private TerminalInstalledApp[] terminalInstallations;
	private Object sysmetricKeys;

	public HmsRequest() {
		super();
		this.requestType = UPDATE_INFORMATION;
	}

	public TerminalState getTerminalState() {
		return terminalState;
	}

	public void setTerminalState(TerminalState terminalState) {
		this.terminalState = terminalState;
	}

	public TerminalUsageReport getUsageReport() {
		return usageReport;
	}

	public void setUsageReport(TerminalUsageReport usageReport) {
		this.usageReport = usageReport;
	}

	public TerminalUsage[] getTerminalUsage() {
		return terminalUsage;
	}

	public void setTerminalUsage(TerminalUsage[] terminalUsage) {
		this.terminalUsage = terminalUsage;
	}

	public TerminalInstalledApp[] getTerminalInstallations() {
		return terminalInstallations;
	}

	public void setTerminalInstallations(TerminalInstalledApp[] terminalInstallations) {
		this.terminalInstallations = terminalInstallations;
	}

    public Object getSysmetricKeys() {
        return sysmetricKeys;
    }

    public void setSysmetricKeys(Object sysmetricKeys) {
        this.sysmetricKeys = sysmetricKeys;
    }
	

}
