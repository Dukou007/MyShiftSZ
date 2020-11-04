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
package com.pax.fastdfs.conn;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.pax.fastdfs.FdfsClientConstants;
import com.pax.fastdfs.domain.TrackerLocator;
import com.pax.fastdfs.exception.FdfsConnectException;
import com.pax.fastdfs.exception.FdfsException;
import com.pax.fastdfs.proto.FdfsCommand;

/**
 * 管理TrackerClient连接池分配
 *
 */
@Component
@ConfigurationProperties(prefix = FdfsClientConstants.ROOT_CONFIG_PREFIX)
public class TrackerConnectionManager extends ConnectionManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(TrackerConnectionManager.class);

	/** Tracker定位 */
	private TrackerLocator trackerLocator;

	/** tracker服务配置地址列表 */
	@NotNull
	private List<String> trackerList = new ArrayList<String>();

	private int retryAfterSecond = -1;

	/** 构造函数 */
	public TrackerConnectionManager() {
		super();
	}

	/** 构造函数 */
	public TrackerConnectionManager(FdfsConnectionPool pool) {
		super(pool);
	}

	/** 初始化方法 */
	@PostConstruct
	public void initTracker() {
		LOGGER.debug("init trackerLocator {}", trackerList);
		trackerLocator = new TrackerLocator(trackerList);
		trackerLocator.setRetryAfterSecond(retryAfterSecond);
	}

	/**
	 * 获取连接并执行交易
	 * 
	 * @param address
	 * @param command
	 * @return
	 */
	public <T> T executeFdfsTrackerCmd(FdfsCommand<T> command) {
		Connection conn = null;
		InetSocketAddress address = null;
		// 获取连接
		try {
			address = trackerLocator.getTrackerAddress();
			LOGGER.debug("obtain tracker address {}", address);
			conn = getConnection(address);
			trackerLocator.setActive(address);
		} catch (FdfsConnectException e) {
			trackerLocator.setInActive(address);
			throw e;
		} catch (Exception e) {
			LOGGER.error("Unable to borrow buffer from pool", e);
			throw new FdfsException("Unable to borrow buffer from pool", e);
		}
		// 执行交易
		return execute(address, conn, command);
	}

	public List<String> getTrackerList() {
		return trackerList;
	}

	public void setTrackerList(List<String> trackerList) {
		this.trackerList = trackerList;
	}

	public int getRetryAfterSecond() {
		return retryAfterSecond;
	}

	public void setRetryAfterSecond(int retryAfterSecond) {
		this.retryAfterSecond = retryAfterSecond;
	}

}
