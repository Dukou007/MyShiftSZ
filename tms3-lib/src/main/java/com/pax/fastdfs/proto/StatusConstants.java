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
 * fastdfs协议服务端状态的常量
 * 
 * @author yuqih
 * 
 */
public final class StatusConstants {
	public static final byte FDFS_STORAGE_STATUS_INIT = 0;
	public static final byte FDFS_STORAGE_STATUS_WAIT_SYNC = 1;
	public static final byte FDFS_STORAGE_STATUS_SYNCING = 2;
	public static final byte FDFS_STORAGE_STATUS_IP_CHANGED = 3;
	public static final byte FDFS_STORAGE_STATUS_DELETED = 4;
	public static final byte FDFS_STORAGE_STATUS_OFFLINE = 5;
	public static final byte FDFS_STORAGE_STATUS_ONLINE = 6;
	public static final byte FDFS_STORAGE_STATUS_ACTIVE = 7;
	public static final byte FDFS_STORAGE_STATUS_NONE = 99;

	private StatusConstants() {
	}
}
