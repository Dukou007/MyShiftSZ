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
package com.pax.tms.app.phoenix;

import com.pax.common.exception.BusinessException;

public class PackageException extends BusinessException {

	private static final long serialVersionUID = -3059806235813435914L;

	public PackageException() {
		super();
	}

	public PackageException(String errorCode, String defaultMessage) {
		super(errorCode, defaultMessage);
	}

	public PackageException(String errorCode, String[] args, String defaultMessage) {
		super(errorCode, args, defaultMessage);
	}

	public PackageException(String errorCode, String[] args, Throwable cause) {
		super(errorCode, args, cause);
	}

	public PackageException(String errorCode, String[] args) {
		super(errorCode, args);
	}

	public PackageException(String errorCode, Throwable cause) {
		super(errorCode, cause);
	}

	public PackageException(String errorCode) {
		super(errorCode);
	}

	public PackageException(Throwable cause) {
		super(cause);
	}

}
