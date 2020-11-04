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

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class DownloadLimiter implements Closeable {

	private static final Logger LOGGER = LoggerFactory.getLogger(DownloadLimiter.class);

	private ConcurrentHashMap<String, Long> terminals = new ConcurrentHashMap<>();

	private CheckDownloadStatusTask checkDownloadStatusTask = new CheckDownloadStatusTask();
	private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

	private int maxSimultaneousDownloads = -1;
	private long inactiveDownloadMaxTime = 60000;
	private long checkInactiveDownloadInterval = 10000;

	public DownloadLimiter() {
		scheduledExecutorService.schedule(checkDownloadStatusTask, checkInactiveDownloadInterval,
				TimeUnit.MILLISECONDS);
	}

	@Override
	public void close() throws IOException {
		scheduledExecutorService.shutdown();
	}

	public boolean hasUncompletedDownload(String terminalId) {
		return terminals.containsKey(terminalId);
	}

	public boolean startDownload(String terminalId) {
		if (maxSimultaneousDownloads > 0 && !terminals.contains(terminalId)) {
			int downloadCount = terminals.size();
			boolean available = downloadCount < maxSimultaneousDownloads;
			if (!available) {
				LOGGER.debug("Downloading limit reached, current count: {}", downloadCount);
				return false;
			}
		}

		LOGGER.debug("Downloading arranged - {}", terminalId);
		terminals.put(terminalId, System.currentTimeMillis());
		return true;
	}

	public void completeDownload(String terminalId) {
		Long lastAccessTime = terminals.remove(terminalId);
		if (lastAccessTime != null) {
			LOGGER.debug("Downloading completed - {}", terminalId);
		}
	}

	public void cancelDownload(String terminalId) {
		Long lastAccessTime = terminals.remove(terminalId);
		if (lastAccessTime != null) {
			LOGGER.debug("Downloading canceled - {}", terminalId);
		}
	}

	public void updateDownloadProgress(String terminalId) {
		terminals.put(terminalId, System.currentTimeMillis());
		LOGGER.debug("Update downloading progress - {}", terminalId);
	}

	private class CheckDownloadStatusTask implements Runnable {
		@Override
		public void run() {
			try {
				removeInactiveDownloading();
			} finally {
				if (!scheduledExecutorService.isShutdown()) {
					scheduledExecutorService.schedule(checkDownloadStatusTask, checkInactiveDownloadInterval,
							TimeUnit.MILLISECONDS);
				}
			}
		}

		private void removeInactiveDownloading() {
			Entry<String, Long> entry;
			Iterator<Entry<String, Long>> it = terminals.entrySet().iterator();
			long currentTime = System.currentTimeMillis();
			while (it.hasNext()) {
				entry = it.next();
				if (currentTime - entry.getValue() >= inactiveDownloadMaxTime) {
					it.remove();
					LOGGER.debug("Remove inactive downloading - {}", entry.getKey());
				}
			}
		}
	}

	public int getMaxSimultaneousDownloads() {
		return maxSimultaneousDownloads;
	}

	public void setMaxSimultaneousDownloads(int maxSimultaneousDownloads) {
		this.maxSimultaneousDownloads = maxSimultaneousDownloads;
	}

	public long getInactiveDownloadMaxTime() {
		return inactiveDownloadMaxTime;
	}

	public void setInactiveDownloadMaxTime(long inactiveDownloadMaxTime) {
		this.inactiveDownloadMaxTime = inactiveDownloadMaxTime;
	}

	public long getCheckInactiveDownloadInterval() {
		return checkInactiveDownloadInterval;
	}

	public void setCheckInactiveDownloadInterval(long checkInactiveDownloadInterval) {
		this.checkInactiveDownloadInterval = checkInactiveDownloadInterval;
	}

}
