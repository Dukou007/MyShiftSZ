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

public class PxRetailerException extends RuntimeException {

	private static final long serialVersionUID = -892316955196753107L;

	private final int statusCode;

	public PxRetailerException(int statusCode) {
		this.statusCode = statusCode;
	}

	public PxRetailerException(int statusCode, String message) {
		super(message);
		this.statusCode = statusCode;
	}

	public PxRetailerException(int statusCode, Throwable cause) {
		super(cause);
		this.statusCode = statusCode;

	}

	public PxRetailerException(int statusCode, String message, Throwable cause) {
		super(message, cause);
		this.statusCode = statusCode;
	}

	public int getStatusCode() {
		return statusCode;
	}

}
