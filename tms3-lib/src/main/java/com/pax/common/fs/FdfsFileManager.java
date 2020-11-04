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

import com.pax.fastdfs.domain.StorePath;
import com.pax.fastdfs.proto.storage.DownloadCallback;
import com.pax.fastdfs.service.DefaultFastFileStorageClient;

public class FdfsFileManager extends DefaultFastFileStorageClient implements FileManager {

	@Override
	public String uploadFile(InputStream inputStream, long fileSize, String fileExtName) {
		StorePath storePath = super.uploadFile(inputStream, fileSize, fileExtName, null);
		return storePath.getFullPath();
	}

	@Override
	public <T> T downloadFile(String filePath, DownloadCallback<T> callback) {
		StorePath storePath = StorePath.praseFromUrl(filePath);
		return super.downloadFile(storePath.getGroup(), storePath.getPath(), callback);
	}

	@Override
	public <T> T downloadFile(String filePath, long fileOffset, long fileSize, DownloadCallback<T> callback) {
		StorePath storePath = StorePath.praseFromUrl(filePath);
		return super.downloadFile(storePath.getGroup(), storePath.getPath(), fileOffset, fileSize, callback);
	}

}
