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
package com.pax.common.util;

public class DecoderException extends RuntimeException {

	private static final long serialVersionUID = 4045814203478034513L;

	public DecoderException() {

	}

	public DecoderException(String message) {
		super(message);

	}

	public DecoderException(Throwable cause) {
		super(cause);

	}

	public DecoderException(String message, Throwable cause) {
		super(message, cause);

	}

	public DecoderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);

	}

}
