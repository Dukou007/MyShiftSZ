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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.pax.fastdfs.proto.ErrorCodeConstants;

/**
 * fastdfs服务端返回的错误码构成的异常
 * 
 * @author yuqih
 * @author tobato
 */
public class FdfsServerException extends FdfsException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/** 错误对照表 */
	private static final Map<Integer, String> CODE_MESSAGE_MAPPING;

	static {
		Map<Integer, String> mapping = new HashMap<Integer, String>();
		mapping.put((int) ErrorCodeConstants.ERR_NO_ENOENT, "Can't find the FastDFS server node or files");
		mapping.put((int) ErrorCodeConstants.ERR_NO_EIO, "The FastDFS server side IO exception");
		mapping.put((int) ErrorCodeConstants.ERR_NO_EINVAL, "Invalid parameter");
		mapping.put((int) ErrorCodeConstants.ERR_NO_EBUSY, "The FastDFS server is busy");
		mapping.put((int) ErrorCodeConstants.ERR_NO_ENOSPC, "Not enough FastDFS storage space");
		mapping.put((int) ErrorCodeConstants.ERR_NO_CONNREFUSED, "The FastDFS server rejected the connection");
		mapping.put((int) ErrorCodeConstants.ERR_NO_EALREADY, "File already exists?");
		CODE_MESSAGE_MAPPING = Collections.unmodifiableMap(mapping);
	}

	private final int errorCode;

	/**
	 * 
	 */
	private FdfsServerException(int errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public static FdfsServerException byCode(int errorCode) {
		String message = CODE_MESSAGE_MAPPING.get(errorCode);
		if (message == null) {
			message = "Unknow exeception";
		}
		message = "ErrorCode: " + errorCode + "，ErrorMessage: " + message;

		return new FdfsServerException(errorCode, message);
	}

	/**
	 * @return the errorCode
	 */
	public int getErrorCode() {
		return errorCode;
	}

}
