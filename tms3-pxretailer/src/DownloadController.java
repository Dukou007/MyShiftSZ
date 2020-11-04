package com.pax.tms.pxretailer;

import java.util.concurrent.ConcurrentHashMap;

import io.vertx.core.Vertx;

public class DownloadController {

	private ConcurrentHashMap<String, Long> activeDownloads = new ConcurrentHashMap<String, Long>();
	private ConcurrentHashMap<String, Long> penddingDownloads = new ConcurrentHashMap<String, Long>();

	private Vertx vertx;
	private int maxConcurrentDownloads;
	private long penddingDownloadIdleTime;
	private long activeDownloadIdleTime;

	public DownloadController(Vertx vertx, int maxConcurrentDownloads, long penddingDownloadIdleTime,
			long activeDownloadIdleTime) {
		this.vertx = vertx;
		this.maxConcurrentDownloads = maxConcurrentDownloads;
		this.penddingDownloadIdleTime = penddingDownloadIdleTime;
		this.activeDownloadIdleTime = activeDownloadIdleTime;
	}

	public boolean downloadAvailable() {
		return maxConcurrentDownloads == -1
				|| penddingDownloads.size() + activeDownloads.size() < maxConcurrentDownloads;
	}

	public boolean penddingDownload(String terminalId) {
		if (!downloadAvailable()) {
			return false;
		}
		penddingDownloads.put(terminalId, System.currentTimeMillis());
		setPenddingDownloadTimer(terminalId);
		return true;
	}

	private void setPenddingDownloadTimer(String terminalId) {
		if (penddingDownloadIdleTime > 0) {
			vertx.setTimer(System.currentTimeMillis() + penddingDownloadIdleTime, timerId -> {
				penddingDownloads.remove(terminalId);
			});
		}
	}

	public void cancelDownload(String terminalId) {
		Long lastTime = activeDownloads.remove(terminalId);
		if (lastTime == null) {
			penddingDownloads.remove(terminalId);
		}
	}

	public void downloading(String terminalId) {
		Long lastTime = activeDownloads.put(terminalId, System.currentTimeMillis());
		if (lastTime == null) {
			// start download
			penddingDownloads.remove(terminalId);
			setActiveDownloadTimer(terminalId);
		}
	}

	private void setActiveDownloadTimer(String terminalId) {
		if (activeDownloadIdleTime > 0) {
			vertx.setPeriodic(activeDownloadIdleTime, timerId -> {
				Long lastDownloadTime = activeDownloads.get(terminalId);
				if (lastDownloadTime == null) {
					vertx.cancelTimer(timerId);
				} else if (System.currentTimeMillis() - lastDownloadTime >= activeDownloadIdleTime) {
					vertx.cancelTimer(timerId);
					activeDownloads.remove(terminalId);
				}
			});
		}
	}
}
