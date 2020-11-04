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
package com.pax.fastdfs.exception;

/**
 * 封装fastdfs的异常，使用运行时异常
 * 
 * @author yuqih
 * @author tobato
 * 
 */
public class FdfsException extends RuntimeException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	public FdfsException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public FdfsException(String message, Throwable cause) {
		super(message, cause);
	}

}
