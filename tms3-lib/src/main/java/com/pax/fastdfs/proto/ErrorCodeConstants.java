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
package com.pax.fastdfs.proto;

/**
 * fastdfs协议错误码的常量
 * 
 * @author yuqih
 * 
 */
public final class ErrorCodeConstants {

	public static final byte SUCCESS = 0;
	public static final byte ERR_NO_ENOENT = 2;
	public static final byte ERR_NO_EIO = 5;
	public static final byte ERR_NO_EBUSY = 16;
	public static final byte ERR_NO_EINVAL = 22;
	public static final byte ERR_NO_ENOSPC = 28;
	public static final byte ERR_NO_CONNREFUSED = 61;
	public static final byte ERR_NO_EALREADY = 114;

	private ErrorCodeConstants() {
	}

}
