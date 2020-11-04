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

@JsonIgnoreProperties(ignoreUnknown = true)
public class TerminalLogsResponse extends BaseResponse {

	private static final long serialVersionUID = -3763807092579857636L;

	private static final String CLS = "CENTRALLOGGING";


	public TerminalLogsResponse() {
		super();
		setResponseType(CLS);
	}

	public TerminalLogsResponse(int statusCode, String statusMessage) {
		this();
		this.statusCode = statusCode;
		this.statusMessage = statusMessage;
	}


}