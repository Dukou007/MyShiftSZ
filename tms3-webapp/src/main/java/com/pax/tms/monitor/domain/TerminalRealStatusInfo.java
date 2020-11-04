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
package com.pax.tms.monitor.domain;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

public class TerminalRealStatusInfo {

	private String terminalId;

	private BigDecimal deployTime;

	private String tamper;

	private Integer tamperSts;

	private Integer onlineSts;

	private Integer privacyShieldSts;

	private Integer stylusSts;

	private Integer dwnlPendingSts;

	private Integer actvPendingSts;

	private String downloadSts;

	private String activateSts;

	public String getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}

	public BigDecimal getDeployTime() {
		return deployTime;
	}

	public void setDeployTime(BigDecimal deployTime) {
		this.deployTime = deployTime;
	}

	public String getTamper() {
		return tamper;
	}

	public void setTamper(String tamper) {
		this.tamper = tamper;
	}

	public Integer getTamperSts() {
		if (StringUtils.isEmpty(tamper)) {
			tamperSts = 3;
		} else if ("0000".equals(tamper)) {
			tamperSts = 1;
		} else {
			tamperSts = 2;
		}
		return tamperSts;
	}

	public void setTamperSts(Integer tamperSts) {
		this.tamperSts = tamperSts;
	}

	public Integer getOnlineSts() {
		if (onlineSts == null) {
			onlineSts = 2;
		}
		return onlineSts;
	}

	public void setOnlineSts(Integer onlineSts) {
		this.onlineSts = onlineSts;
	}

	public Integer getPrivacyShieldSts() {
		if (privacyShieldSts == null) {
			privacyShieldSts = 3;
		}
		return privacyShieldSts;
	}

	public void setPrivacyShieldSts(Integer privacyShieldSts) {
		this.privacyShieldSts = privacyShieldSts;
	}

	public Integer getStylusSts() {
		if (stylusSts == null) {
			stylusSts = 3;
		}
		return stylusSts;
	}

	public void setStylusSts(Integer stylusSts) {
		this.stylusSts = stylusSts;
	}

	public String getDownloadSts() {
		return downloadSts;
	}

	public void setDownloadSts(String downloadSts) {
		this.downloadSts = downloadSts;
	}

	public String getActivateSts() {
		return activateSts;
	}

	public void setActivateSts(String activateSts) {
		this.activateSts = activateSts;
	}

	public Integer getDwnlPendingSts() {
		if (StringUtils.isEmpty(downloadSts)) {
			dwnlPendingSts = 3;
		} else if ("PENDING".equals(downloadSts)) {
			dwnlPendingSts = 1;
		} else if ("FAILED".equals(downloadSts)) {
			dwnlPendingSts = 2;
		} else {
			dwnlPendingSts = 3;
		}
		return dwnlPendingSts;
	}

	public void setDwnlPendingSts(Integer dwnlPendingSts) {
		this.dwnlPendingSts = dwnlPendingSts;
	}

	public Integer getActvPendingSts() {
		if (StringUtils.isEmpty(activateSts)) {
			actvPendingSts = 3;
		} else if ("PENDING".equals(activateSts)) {
			actvPendingSts = 1;
		} else if ("FAILED".equals(activateSts)) {
			actvPendingSts = 2;
		} else {
			actvPendingSts = 3;
		}
		return actvPendingSts;
	}

	public void setActvPendingSts(Integer actvPendingSts) {
		this.actvPendingSts = actvPendingSts;
	}
}
