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

import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Component;

import com.pax.fastdfs.domain.MateData;
import com.pax.fastdfs.domain.StorageNode;
import com.pax.fastdfs.domain.StorePath;
import com.pax.fastdfs.proto.storage.StorageSetMetadataCommand;
import com.pax.fastdfs.proto.storage.StorageUploadFileCommand;
import com.pax.fastdfs.proto.storage.enums.StorageMetdataSetType;

/**
 * 面向应用的接口实现
 * 
 * @author tobato
 *
 */
@Component
public class DefaultFastFileStorageClient extends DefaultGenerateStorageClient implements FastFileStorageClient {

	/**
	 * 上传文件
	 */
	@Override
	public StorePath uploadFile(InputStream inputStream, long fileSize, String fileExtName, Set<MateData> metaDataSet) {
		Validate.notNull(inputStream, "上传文件流不能为空");
		Validate.notBlank(fileExtName, "文件扩展名不能为空");
		StorageNode client = trackerClient.getStoreStorage();
		return uploadFileAndMateData(client, inputStream, fileSize, fileExtName, metaDataSet);
	}

	/**
	 * 检查是否有MateData
	 * 
	 * @param metaDataSet
	 * @return
	 */
	private boolean hasMateData(Set<MateData> metaDataSet) {
		return null != metaDataSet && !metaDataSet.isEmpty();
	}

	/**
	 * 上传文件和元数据
	 * 
	 * @param client
	 * @param inputStream
	 * @param fileSize
	 * @param fileExtName
	 * @param metaDataSet
	 * @return
	 */
	private StorePath uploadFileAndMateData(StorageNode client, InputStream inputStream, long fileSize,
			String fileExtName, Set<MateData> metaDataSet) {
		// 上传文件
		StorageUploadFileCommand command = new StorageUploadFileCommand(client.getStoreIndex(), inputStream,
				fileExtName, fileSize, false);
		StorePath path = connectionManager.executeFdfsCmd(client.getInetSocketAddress(), command);
		// 上传matadata
		if (hasMateData(metaDataSet)) {
			StorageSetMetadataCommand setMDCommand = new StorageSetMetadataCommand(path.getGroup(), path.getPath(),
					metaDataSet, StorageMetdataSetType.STORAGE_SET_METADATA_FLAG_OVERWRITE);
			connectionManager.executeFdfsCmd(client.getInetSocketAddress(), setMDCommand);
		}
		return path;
	}

	/**
	 * 删除文件
	 */
	@Override
	public void deleteFile(String filePath) {
		StorePath storePath = StorePath.praseFromUrl(filePath);
		super.deleteFile(storePath.getGroup(), storePath.getPath());
	}

}
