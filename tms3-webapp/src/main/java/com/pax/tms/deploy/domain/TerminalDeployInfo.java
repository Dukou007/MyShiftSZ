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
package com.pax.tms.deploy.domain;

public class TerminalDeployInfo {

	private String tsn;
	private Long deployId;
	private String pkgName;
	private String paramVersion;
	private String paramSet;
	private long deployTime;
	/*
	 * TODO
	 * schema Id 是否需要?
	 */

	public String getTsn() {
		return tsn;
	}

	public void setTsn(String tsn) {
		this.tsn = tsn;
	}

	public Long getDeployId() {
		return deployId;
	}

	public void setDeployId(Long deployId) {
		this.deployId = deployId;
	}

	public String getPkgName() {
		return pkgName;
	}

	public void setPkgName(String pkgName) {
		this.pkgName = pkgName;
	}

	public String getParamVersion() {
		return paramVersion;
	}

	public void setParamVersion(String paramVersion) {
		this.paramVersion = paramVersion;
	}

	public String getParamSet() {
		return paramSet;
	}

	public void setParamSet(String paramSet) {
		this.paramSet = paramSet;
	}

	public long getDeployTime() {
		return deployTime;
	}

	public void setDeployTime(long deployTime) {
		this.deployTime = deployTime;
	}

}
