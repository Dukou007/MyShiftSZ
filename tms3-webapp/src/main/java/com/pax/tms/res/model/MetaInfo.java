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
package com.pax.tms.res.model;

public class MetaInfo {
	private Long metaId;
	private String filePath;
	private String apmName;
	public Long getId() {
		return metaId;
	}
	public void setId(Long id) {
		this.metaId = id;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getApmName() {
		return apmName;
	}
	public void setApmName(String apmName) {
		this.apmName = apmName;
	}
}
