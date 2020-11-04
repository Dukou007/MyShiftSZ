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
package com.pax.common.fs;

import java.io.InputStream;

import com.pax.fastdfs.proto.storage.DownloadCallback;

public interface FileManager {
	/**
	 * 上传文件(文件不可修改)
	 * 
	 * <pre>
	 * 文件上传后不可以修改，如果要修改则删除以后重新上传
	 * </pre>
	 * 
	 * @param inputStream
	 * @param fileSize
	 * @param fileExtName
	 * @return filePath 文件路径(groupName/path)
	 */
	String uploadFile(InputStream inputStream, long fileSize, String fileExtName);

	/**
	 * 删除文件
	 * 
	 * @param filePath
	 *            文件路径(groupName/path)
	 */
	void deleteFile(String filePath);

	/**
	 * 下载整个文件
	 * 
	 * @param filePath
	 *            文件路径(groupName/path)
	 * @param callback
	 * @return
	 */
	<T> T downloadFile(String filePath, DownloadCallback<T> callback);

	/**
	 * 下载文件片段
	 * 
	 * @param groupName
	 * @param filePath
	 *            文件路径(groupName/path)
	 * @param fileOffset
	 * @param fileSize
	 * @param callback
	 * @return
	 */
	<T> T downloadFile(String filePath, long fileOffset, long fileSize, DownloadCallback<T> callback);

}
