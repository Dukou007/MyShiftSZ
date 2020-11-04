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
package com.pax.fastdfs.proto.storage.internal;

import com.pax.fastdfs.proto.CmdConstants;
import com.pax.fastdfs.proto.FdfsRequest;
import com.pax.fastdfs.proto.OtherConstants;
import com.pax.fastdfs.proto.ProtoHead;
import com.pax.fastdfs.proto.mapper.DynamicFieldType;
import com.pax.fastdfs.proto.mapper.FdfsColumn;

/**
 * 文件下载请求
 * 
 * @author tobato
 *
 */
public class StorageDownloadRequest extends FdfsRequest {

	/** 开始位置 */
	@FdfsColumn(index = 0)
	private long fileOffset;
	/** 读取文件长度 */
	@FdfsColumn(index = 1)
	private long downloadBytes;
	/** 组名 */
	@FdfsColumn(index = 2, max = OtherConstants.FDFS_GROUP_NAME_MAX_LEN)
	private String groupName;
	/** 文件路径 */
	@FdfsColumn(index = 3, dynamicField = DynamicFieldType.ALLRESTBYTE)
	private String path;

	/**
	 * 文件下载请求
	 * 
	 * @param groupName
	 * @param path
	 * @param fileOffset
	 * @param fileSize
	 */
	public StorageDownloadRequest(String groupName, String path, long fileOffset, long downloadBytes) {
		super();
		this.groupName = groupName;
		this.downloadBytes = downloadBytes;
		this.path = path;
		this.fileOffset = fileOffset;
		head = new ProtoHead(CmdConstants.STORAGE_PROTO_CMD_DOWNLOAD_FILE);

	}

	public long getFileOffset() {
		return fileOffset;
	}

	public String getGroupName() {
		return groupName;
	}

	public String getPath() {
		return path;
	}

	public long getDownloadBytes() {
		return downloadBytes;
	}

}
