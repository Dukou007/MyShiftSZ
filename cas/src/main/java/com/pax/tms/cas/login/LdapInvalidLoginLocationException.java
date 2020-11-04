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

import org.jasig.cas.authentication.InvalidLoginLocationException;

public class LdapInvalidLoginLocationException extends InvalidLoginLocationException {

	private static final long serialVersionUID = 8165200233138314050L;

	public LdapInvalidLoginLocationException() {
	}

	public LdapInvalidLoginLocationException(String message) {
		super(message);
	}

}
