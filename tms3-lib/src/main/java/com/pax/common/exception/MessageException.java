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

public class MessageException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	/** 错误返回码, 默认为-1 */
	private final int retCode;

	public MessageException(int retCode, String message) {
		super(message);
		this.retCode = retCode;
	}

	public MessageException(int retCode, String message, Throwable nestedException) {
		super(message, nestedException);
		this.retCode = retCode;
	}

	/**
	 * 取得错误返回类型
	 * 
	 * @return
	 */
	public int getRetCode() {
		return retCode;
	}

	@Override
	public String toString() {
		return MessageException.class.getCanonicalName() + ": " + this.retCode + " - " + super.toString();
	}
}
