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

import javax.annotation.PostConstruct;

import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.pax.fastdfs.FdfsClientConstants;

/**
 * 连接池配置
 * 
 */
@Component
@ConfigurationProperties(prefix = FdfsClientConstants.ROOT_CONFIG_PREFIX)
public class ConnectionPoolConfig extends GenericKeyedObjectPoolConfig {

	private long maxWaitMillis = 10000;
	private int maxIdlePerKey = 120000;
	private int maxTotalPerKey = 200;
	private long minEvictableIdleTimeMillis = 120000;
	private long timeBetweenEvictionRunsMillis = 30000;
	private int numTestsPerEvictionRun = 6;

	public ConnectionPoolConfig() {
		setBlockWhenExhausted(true);// default
		setMaxTotal(-1);
		setTestOnCreate(true);// default false
		setTestOnBorrow(false);// default false
		setTestOnReturn(true);// default false
		setTestWhileIdle(true);// default false
	}

	@PostConstruct
	public void initConfig() {
		// 1000L * 60L * 30L;
		setSoftMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
	}

	@Override
	public long getMaxWaitMillis() {
		return maxWaitMillis;
	}

	@Override
	public void setMaxWaitMillis(long maxWaitMillis) {
		this.maxWaitMillis = maxWaitMillis;
	}

	@Override
	public int getMaxIdlePerKey() {
		return maxIdlePerKey;
	}

	@Override
	public void setMaxIdlePerKey(int maxIdlePerKey) {
		this.maxIdlePerKey = maxIdlePerKey;
	}

	@Override
	public int getMaxTotalPerKey() {
		return maxTotalPerKey;
	}

	@Override
	public void setMaxTotalPerKey(int maxTotalPerKey) {
		this.maxTotalPerKey = maxTotalPerKey;
	}

	@Override
	public long getMinEvictableIdleTimeMillis() {
		return minEvictableIdleTimeMillis;
	}

	@Override
	public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
		this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
	}

	@Override
	public long getTimeBetweenEvictionRunsMillis() {
		return timeBetweenEvictionRunsMillis;
	}

	@Override
	public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
		this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
	}

	@Override
	public int getNumTestsPerEvictionRun() {
		return numTestsPerEvictionRun;
	}

	@Override
	public void setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
		this.numTestsPerEvictionRun = numTestsPerEvictionRun;
	}

}