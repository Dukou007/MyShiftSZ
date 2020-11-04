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
package com.pax.tms.webservice.pxmaster.form;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author Tiangel
 * @date 2014.10.30
 */
public class BaseResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * the responseCode of success
	 */
	public static final String RESPONSECODE_SUCCESS = "0000000";
	/**
	 * the responseMessage of success
	 */
	public static final String RESPONSEMESSAGE_SUCCESS = "success";

	public static final String RESPONSECODE_DATABASEERROR = "ELS1001";

	public static final String RESPONSEMESSAGE_DATABASEERROR = "Database error";

	/**
	 * the responseCode of Parameter format invalid
	 */
	public static final String RESPONSECODE_FORMATINVALID = "ELS1002";
	/**
	 * the responseMessage of Parameter format invalid
	 */
	public static final String RESPONSEMESSAGE_FORMATINVALID = "Parameter format invalid";
	/**
	 * the responseCode of Get event list error
	 */
	public static final String RESPONSECODE_GETEVENTLISTERROR = "ELS1003";
	/**
	 * the responseMessage of Get event list error
	 */
	public static final String RESPONSEMESSAGE_GETEVENTLISTERROR = "Get event list error";



	private String uuid; // package uuid
	private String responseCode;// responseCode
	private String responseMessage;// responseMessage

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getResponseCode() {
		return StringUtils.isEmpty(responseCode) ? RESPONSECODE_SUCCESS : responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseMessage() {
		return StringUtils.isEmpty(responseMessage) ? RESPONSEMESSAGE_SUCCESS : responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

}
