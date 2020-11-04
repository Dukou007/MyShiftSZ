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

import org.jasig.cas.authentication.InvalidLoginTimeException;

public class LdapInvalidLoginTimeException extends InvalidLoginTimeException {

	private static final long serialVersionUID = 8722419720611485148L;

	public LdapInvalidLoginTimeException() {
		super();
	}

	public LdapInvalidLoginTimeException(String message) {
		super(message);
	}

}
