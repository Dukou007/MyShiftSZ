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
package com.pax.tms.app.broadpos.para;

import com.pax.tms.app.phoenix.PackageException;

public class ParameterSchemaException extends PackageException {

	private static final long serialVersionUID = 204871434365651105L;

	public ParameterSchemaException() {
	}

	public ParameterSchemaException(String errorCode, String defaultMessage) {
		super(errorCode, defaultMessage);
	}

	public ParameterSchemaException(String errorCode, String[] args, String defaultMessage) {
		super(errorCode, args, defaultMessage);
	}

	public ParameterSchemaException(String errorCode, String[] args, Throwable cause) {
		super(errorCode, args, cause);
	}

	public ParameterSchemaException(String errorCode, String[] args) {
		super(errorCode, args);
	}

	public ParameterSchemaException(String errorCode, Throwable cause) {
		super(errorCode, cause);
	}

	public ParameterSchemaException(String errorCode) {
		super(errorCode);
	}

	public ParameterSchemaException(Throwable cause) {
		super(cause);
	}

}
