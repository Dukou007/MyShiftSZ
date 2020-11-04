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
package com.pax.tms.cas.login;

import javax.security.auth.login.CredentialExpiredException;

public class TmsUserCredentialExpiredException extends CredentialExpiredException {

	private static final long serialVersionUID = 6724649100273812190L;

	public TmsUserCredentialExpiredException() {
		super();
	}

	public TmsUserCredentialExpiredException(String msg) {
		super(msg);
	}

}
