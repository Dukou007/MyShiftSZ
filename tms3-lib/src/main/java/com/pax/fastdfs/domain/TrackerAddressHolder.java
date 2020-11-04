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
package com.pax.fastdfs.domain;

import java.net.InetSocketAddress;

/**
 * 管理TrackerAddress当前状态
 * 
 * @author tobato
 *
 */
public class TrackerAddressHolder {
	/** 连接地址 */
	private InetSocketAddress address;
	/** 当前是否有效 */
	private boolean available;
	/** 上次无效时间 */
	private long lastUnavailableTime;

	/**
	 * 构造函数
	 * 
	 * @param address
	 */
	public TrackerAddressHolder(InetSocketAddress address) {
		super();
		this.address = address;
		// 默认Tracker有效
		this.available = true;
	}

	/**
	 * 有效
	 */
	public void setActive() {
		this.available = true;
	}

	/**
	 * 无效
	 */
	public void setInActive() {
		this.available = false;
		this.lastUnavailableTime = System.currentTimeMillis();
	}

	public boolean isAvailable() {
		return available;
	}

	public long getLastUnavailableTime() {
		return lastUnavailableTime;
	}

	/**
	 * 是否可以尝试连接
	 * 
	 * @param retryAfterSecend
	 *            在n秒后重试
	 * @return
	 */
	public boolean canTryToConnect(int retryAfterSecend) {
		// 如果是有效连接, 或者 如果连接无效，并且达到重试时间
		return (this.available || retryAfterSecend <= 0)
				|| ((System.currentTimeMillis() - lastUnavailableTime) > retryAfterSecend * 1000);
	}

	public InetSocketAddress getAddress() {
		return address;
	}

}
