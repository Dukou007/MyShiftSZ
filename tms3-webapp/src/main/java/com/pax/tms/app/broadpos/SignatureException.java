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
package com.pax.tms.app.broadpos;

public class SignatureException extends RuntimeException {

	private static final long serialVersionUID = -2879708854616860457L;

	public SignatureException() {
	}

	public SignatureException(String message) {
		super(message);
	}

	public SignatureException(Throwable cause) {
		super(cause);
	}

	public SignatureException(String message, Throwable cause) {
		super(message, cause);
	}

	public SignatureException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
