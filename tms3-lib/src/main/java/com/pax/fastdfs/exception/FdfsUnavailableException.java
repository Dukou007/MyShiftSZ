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
 * 非fastdfs本身的错误码抛出的异常，取服务端连接取不到时抛出的异常
 * 
 * @author yuqihuang
 * 
 */
public class FdfsUnavailableException extends FdfsException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 */
	public FdfsUnavailableException(String message) {
		super("Unabled to get connection to FastDFS server: " + message);
	}

	/**
	 * @param message
	 */
	public FdfsUnavailableException(String message, Throwable t) {
		super("Unabled to get connection to FastDFS server: " + message, t);
	}

}
