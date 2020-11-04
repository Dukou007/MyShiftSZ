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
package com.pax.tms.pxretailer;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pax.tms.download.DownloadLimiter;

@Component
public class PxRetailerDownloadLimiter extends DownloadLimiter {

	private PxRetailerConfig config;

	@Autowired
	public void setConfig(PxRetailerConfig config) {
		this.config = config;
	}

	@PostConstruct
	public void init() {
		super.setMaxSimultaneousDownloads(config.getMaxSimultaneousDownloads());
		super.setInactiveDownloadMaxTime(config.getInactiveDownloadMaxTime());
		super.setCheckInactiveDownloadInterval(config.getCheckInactiveDownloadInterval());
	}
}
