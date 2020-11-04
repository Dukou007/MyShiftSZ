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
package com.pax.tms.download;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pax.tms.pxretailer.PxRetailerConfig;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

@Component
public class HmsLimiter implements Closeable {

	private static final Logger LOGGER = LoggerFactory.getLogger(HmsLimiter.class);

	private ConcurrentHashMap<String, Long> terminals = new ConcurrentHashMap<>();

	private CheckHmsStatusTask checkHmsStatusTask = new CheckHmsStatusTask();
	private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

	private int maxSimultaneousHms = -1;
	private long inactiveHmsMaxTime = 60000;
	private long checkInactiveHmsInterval = 10000;
	private PxRetailerConfig config;

	public HmsLimiter() {
		scheduledExecutorService.schedule(checkHmsStatusTask, checkInactiveHmsInterval, TimeUnit.MILLISECONDS);
	}

	@Autowired
	public void setConfig(PxRetailerConfig config) {
		this.config = config;
	}

	@PostConstruct
	public void init() {
		setMaxSimultaneousHms(config.getMaxSimultaneousHms());
		setInactiveHmsMaxTime(config.getInactiveHmsMaxTime());
		setCheckInactiveHmsInterval(config.getCheckInactiveHmsInterval());
	}

	@Override
	public void close() throws IOException {
		scheduledExecutorService.shutdown();
	}

	public boolean startHms(String terminalId) {
		if (maxSimultaneousHms > 0 && !terminals.contains(terminalId)) {
			int hmsCount = terminals.size();
			boolean available = hmsCount < maxSimultaneousHms;
			if (!available) {
				LOGGER.debug("HMS limit reached, current count: {}", hmsCount);
				return false;
			}
		}

		LOGGER.debug("HMS arranged - {}", terminalId);
		terminals.put(terminalId, System.currentTimeMillis());
		return true;
	}

	public void completeHms(String terminalId) {
		Long lastAccessTime = terminals.remove(terminalId);
		if (lastAccessTime != null) {
			LOGGER.debug("HMS completed - {}", terminalId);
		}
	}

	private class CheckHmsStatusTask implements Runnable {
		@Override
		public void run() {
			try {
				removeInactiveHms();
			} finally {
				if (!scheduledExecutorService.isShutdown()) {
					scheduledExecutorService.schedule(checkHmsStatusTask, checkInactiveHmsInterval,
							TimeUnit.MILLISECONDS);
				}
			}
		}

		private void removeInactiveHms() {
			Entry<String, Long> entry;
			Iterator<Entry<String, Long>> it = terminals.entrySet().iterator();
			long currentTime = System.currentTimeMillis();
			while (it.hasNext()) {
				entry = it.next();
				if (currentTime - entry.getValue() >= inactiveHmsMaxTime) {
					it.remove();
					LOGGER.debug("Remove inactive HMS - {}", entry.getKey());
				}
			}
		}
	}

	public int getMaxSimultaneousHms() {
		return maxSimultaneousHms;
	}

	public void setMaxSimultaneousHms(int maxSimultaneousHms) {
		this.maxSimultaneousHms = maxSimultaneousHms;
	}

	public long getInactiveHmsMaxTime() {
		return inactiveHmsMaxTime;
	}

	public void setInactiveHmsMaxTime(long inactiveHmsMaxTime) {
		this.inactiveHmsMaxTime = inactiveHmsMaxTime;
	}

	public long getCheckInactiveHmsInterval() {
		return checkInactiveHmsInterval;
	}

	public void setCheckInactiveHmsInterval(long checkInactiveHmsInterval) {
		this.checkInactiveHmsInterval = checkInactiveHmsInterval;
	}

}
