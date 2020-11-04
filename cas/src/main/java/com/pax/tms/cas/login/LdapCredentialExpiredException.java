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

public class LdapCredentialExpiredException extends CredentialExpiredException {

	private static final long serialVersionUID = -1420990100076483855L;

	public LdapCredentialExpiredException() {
	}

	public LdapCredentialExpiredException(String msg) {
		super(msg);
	}

}
