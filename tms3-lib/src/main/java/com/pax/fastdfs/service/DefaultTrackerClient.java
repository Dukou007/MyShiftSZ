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

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.pax.fastdfs.conn.TrackerConnectionManager;
import com.pax.fastdfs.domain.GroupState;
import com.pax.fastdfs.domain.StorageNode;
import com.pax.fastdfs.domain.StorageNodeInfo;
import com.pax.fastdfs.domain.StorageState;
import com.pax.fastdfs.proto.tracker.TrackerDeleteStorageCommand;
import com.pax.fastdfs.proto.tracker.TrackerGetFetchStorageCommand;
import com.pax.fastdfs.proto.tracker.TrackerGetStoreStorageCommand;
import com.pax.fastdfs.proto.tracker.TrackerListGroupsCommand;
import com.pax.fastdfs.proto.tracker.TrackerListStoragesCommand;

/**
 * 目录服务客户端默认实现
 * 
 * @author tobato
 *
 */
@Service
public class DefaultTrackerClient implements TrackerClient {

	@Resource
	private TrackerConnectionManager trackerConnectionManager;

	/**
	 * 获取存储节点
	 */
	@Override
	public StorageNode getStoreStorage() {
		TrackerGetStoreStorageCommand command = new TrackerGetStoreStorageCommand();
		return trackerConnectionManager.executeFdfsTrackerCmd(command);
	}

	/**
	 * 按组获取存储节点
	 */
	@Override
	public StorageNode getStoreStorage(String groupName) {
		TrackerGetStoreStorageCommand command;
		if (StringUtils.isBlank(groupName)) {
			command = new TrackerGetStoreStorageCommand();
		} else {
			command = new TrackerGetStoreStorageCommand(groupName);
		}

		return trackerConnectionManager.executeFdfsTrackerCmd(command);
	}

	/**
	 * 获取源服务器
	 */
	@Override
	public StorageNodeInfo getFetchStorage(String groupName, String filename) {
		TrackerGetFetchStorageCommand command = new TrackerGetFetchStorageCommand(groupName, filename, false);
		return trackerConnectionManager.executeFdfsTrackerCmd(command);
	}

	/**
	 * 获取更新服务器
	 */
	@Override
	public StorageNodeInfo getUpdateStorage(String groupName, String filename) {
		TrackerGetFetchStorageCommand command = new TrackerGetFetchStorageCommand(groupName, filename, true);
		return trackerConnectionManager.executeFdfsTrackerCmd(command);
	}

	/**
	 * 列出组
	 */
	@Override
	public List<GroupState> listGroups() {
		TrackerListGroupsCommand command = new TrackerListGroupsCommand();
		return trackerConnectionManager.executeFdfsTrackerCmd(command);
	}

	/**
	 * 按组列出存储状态
	 */
	@Override
	public List<StorageState> listStorages(String groupName) {
		TrackerListStoragesCommand command = new TrackerListStoragesCommand(groupName);
		return trackerConnectionManager.executeFdfsTrackerCmd(command);
	}

	/**
	 * 按ip列出存储状态
	 */
	@Override
	public List<StorageState> listStorages(String groupName, String storageIpAddr) {
		TrackerListStoragesCommand command = new TrackerListStoragesCommand(groupName, storageIpAddr);
		return trackerConnectionManager.executeFdfsTrackerCmd(command);
	}

	/**
	 * 删除存储节点
	 */
	@Override
	public void deleteStorage(String groupName, String storageIpAddr) {
		TrackerDeleteStorageCommand command = new TrackerDeleteStorageCommand(groupName, storageIpAddr);
		trackerConnectionManager.executeFdfsTrackerCmd(command);
	}

}
