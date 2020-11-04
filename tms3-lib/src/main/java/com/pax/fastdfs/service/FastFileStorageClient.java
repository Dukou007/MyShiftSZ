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
package com.pax.fastdfs.service;

import java.io.InputStream;
import java.util.Set;

import com.pax.fastdfs.domain.MateData;
import com.pax.fastdfs.domain.StorePath;

/**
 * 面向普通应用的文件操作接口封装
 * 
 * @author tobato
 *
 */
public interface FastFileStorageClient extends GenerateStorageClient {

	/**
	 * 上传一般文件
	 * 
	 * @param inputStream
	 * @param fileSize
	 * @param fileExtName
	 * @param metaDataSet
	 * @return
	 */
	StorePath uploadFile(InputStream inputStream, long fileSize, String fileExtName, Set<MateData> metaDataSet);

	/**
	 * 删除文件
	 * 
	 * @param filePath
	 *            文件路径(groupName/path)
	 */
	void deleteFile(String filePath);

}
