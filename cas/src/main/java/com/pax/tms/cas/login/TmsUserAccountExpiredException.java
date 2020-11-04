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

import javax.security.auth.login.AccountExpiredException;

public class TmsUserAccountExpiredException extends AccountExpiredException {

	private static final long serialVersionUID = 4051223062962253049L;

	public TmsUserAccountExpiredException() {
		super();
	}

	public TmsUserAccountExpiredException(String msg) {
		super(msg);
	}

}
