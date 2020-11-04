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

import javax.security.auth.login.AccountLockedException;

public class LdapAccountLockedException extends AccountLockedException {

	private static final long serialVersionUID = 2831770691860344657L;

	public LdapAccountLockedException() {
	}

	public LdapAccountLockedException(String msg) {
		super(msg);
	}

}
