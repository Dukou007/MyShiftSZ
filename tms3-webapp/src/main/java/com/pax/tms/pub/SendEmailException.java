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
package com.pax.tms.pub;

public class SendEmailException extends RuntimeException {

	private static final long serialVersionUID = -143237118614888485L;

	public SendEmailException() {
		super();
	}

	public SendEmailException(String message, Throwable cause) {
		super(message, cause);
	}

	public SendEmailException(String message) {
		super(message);
	}

	public SendEmailException(Throwable cause) {
		super(cause);
	}

}
