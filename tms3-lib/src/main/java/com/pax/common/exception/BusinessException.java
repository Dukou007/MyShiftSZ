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
package com.pax.common.exception;

public class BusinessException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private final String code;

	private final String[] args;

	private final String defaultMessage;

	public BusinessException() {
		super();
		this.code = null;
		this.args = null;
		this.defaultMessage = null;
	}

	public BusinessException(String errorCode, Throwable cause) {
		super(errorCode, cause);
		this.code = errorCode;
		this.args = null;
		this.defaultMessage = null;
	}

	public BusinessException(String errorCode, String[] args, Throwable cause) {
		super(errorCode, cause);
		this.code = errorCode;
		this.args = args;
		this.defaultMessage = null;
	}

	public BusinessException(String errorCode) {
		super(errorCode);
		this.code = errorCode;
		this.args = null;
		this.defaultMessage = null;
	}

	public BusinessException(String errorCode, String[] args) {
		super(errorCode);
		this.code = errorCode;
		this.args = args;
		this.defaultMessage = null;
	}

	public BusinessException(String errorCode, String[] args, String defaultMessage) {
		super(errorCode);
		this.code = errorCode;
		this.args = args;
		this.defaultMessage = defaultMessage;
	}

	public BusinessException(String errorCode, String defaultMessage) {
		super(errorCode);
		this.code = errorCode;
		this.defaultMessage = defaultMessage;
		this.args = null;
	}

	public BusinessException(Throwable cause) {
		super(cause);
		this.code = null;
		this.args = null;
		this.defaultMessage = null;
	}

	public String getErrorCode() {
		return code;
	}

	public String[] getArgs() {
		return args;
	}

	public String getDefaultMessage() {
		return defaultMessage;
	}

}
