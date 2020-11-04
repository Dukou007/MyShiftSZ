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

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.pax.fastdfs.conn.ConnectionManager;
import com.pax.fastdfs.domain.FileInfo;
import com.pax.fastdfs.domain.MateData;
import com.pax.fastdfs.domain.StorageNode;
import com.pax.fastdfs.domain.StorageNodeInfo;
import com.pax.fastdfs.domain.StorePath;
import com.pax.fastdfs.proto.storage.DownloadCallback;
import com.pax.fastdfs.proto.storage.StorageDeleteFileCommand;
import com.pax.fastdfs.proto.storage.StorageDownloadCommand;
import com.pax.fastdfs.proto.storage.StorageGetMetadataCommand;
import com.pax.fastdfs.proto.storage.StorageQueryFileInfoCommand;
import com.pax.fastdfs.proto.storage.StorageSetMetadataCommand;
import com.pax.fastdfs.proto.storage.StorageUploadFileCommand;
import com.pax.fastdfs.proto.storage.StorageUploadSlaveFileCommand;
import com.pax.fastdfs.proto.storage.enums.StorageMetdataSetType;

/**
 * 基本存储客户端操作实现
 * 
 * @author tobato
 *
 */
@Component
public class DefaultGenerateStorageClient implements GenerateStorageClient {

	/** trackerClient */
	@Resource
	protected TrackerClient trackerClient;

	/** connectManager */
	@Resource
	protected ConnectionManager connectionManager;

	/**
	 * 上传不支持断点续传的文件
	 */
	@Override
	public StorePath uploadFile(String groupName, InputStream inputStream, long fileSize, String fileExtName) {
		StorageNode client = trackerClient.getStoreStorage(groupName);
		StorageUploadFileCommand command = new StorageUploadFileCommand(client.getStoreIndex(), inputStream,
				fileExtName, fileSize, false);
		return connectionManager.executeFdfsCmd(client.getInetSocketAddress(), command);
	}

	/**
	 * 上传从文件
	 */
	@Override
	public StorePath uploadSlaveFile(String groupName, String masterFilename, InputStream inputStream, long fileSize,
			String prefixName, String fileExtName) {
		StorageNodeInfo client = trackerClient.getUpdateStorage(groupName, masterFilename);
		StorageUploadSlaveFileCommand command = new StorageUploadSlaveFileCommand(inputStream, fileSize, masterFilename,
				prefixName, fileExtName);
		return connectionManager.executeFdfsCmd(client.getInetSocketAddress(), command);
	}

	/**
	 * 获取metadata
	 */
	@Override
	public Set<MateData> getMetadata(String groupName, String path) {
		StorageNodeInfo client = trackerClient.getFetchStorage(groupName, path);
		StorageGetMetadataCommand command = new StorageGetMetadataCommand(groupName, path);
		return connectionManager.executeFdfsCmd(client.getInetSocketAddress(), command);
	}

	/**
	 * 覆盖metadata
	 */
	@Override
	public void overwriteMetadata(String groupName, String path, Set<MateData> metaDataSet) {
		StorageNodeInfo client = trackerClient.getUpdateStorage(groupName, path);
		StorageSetMetadataCommand command = new StorageSetMetadataCommand(groupName, path, metaDataSet,
				StorageMetdataSetType.STORAGE_SET_METADATA_FLAG_OVERWRITE);
		connectionManager.executeFdfsCmd(client.getInetSocketAddress(), command);
	}

	/**
	 * 合并metadata
	 */
	@Override
	public void mergeMetadata(String groupName, String path, Set<MateData> metaDataSet) {
		StorageNodeInfo client = trackerClient.getUpdateStorage(groupName, path);
		StorageSetMetadataCommand command = new StorageSetMetadataCommand(groupName, path, metaDataSet,
				StorageMetdataSetType.STORAGE_SET_METADATA_FLAG_MERGE);
		connectionManager.executeFdfsCmd(client.getInetSocketAddress(), command);
	}

	/**
	 * 查询文件信息
	 */
	@Override
	public FileInfo queryFileInfo(String groupName, String path) {
		StorageNodeInfo client = trackerClient.getFetchStorage(groupName, path);
		StorageQueryFileInfoCommand command = new StorageQueryFileInfoCommand(groupName, path);
		return connectionManager.executeFdfsCmd(client.getInetSocketAddress(), command);
	}

	/**
	 * 删除文件
	 */
	@Override
	public void deleteFile(String groupName, String path) {
		StorageNodeInfo client = trackerClient.getUpdateStorage(groupName, path);
		StorageDeleteFileCommand command = new StorageDeleteFileCommand(groupName, path);
		connectionManager.executeFdfsCmd(client.getInetSocketAddress(), command);
	}

	/**
	 * 下载整个文件
	 */
	@Override
	public <T> T downloadFile(String groupName, String path, DownloadCallback<T> callback) {
		long fileOffset = 0;
		long fileSize = 0;
		return downloadFile(groupName, path, fileOffset, fileSize, callback);
	}

	/**
	 * 下载文件片段
	 */
	@Override
	public <T> T downloadFile(String groupName, String path, long fileOffset, long fileSize,
			DownloadCallback<T> callback) {
		StorageNodeInfo client = trackerClient.getFetchStorage(groupName, path);
		StorageDownloadCommand<T> command = new StorageDownloadCommand<T>(groupName, path, fileOffset, fileSize,
				callback);
		return connectionManager.executeFdfsCmd(client.getInetSocketAddress(), command);
	}

	public void setTrackerClientService(TrackerClient trackerClientService) {
		this.trackerClient = trackerClientService;
	}

	public void setConnectionManager(ConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}

}
