package com.pax.tms.pxretailer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PxRetailerConfig {

	private long maxHeartBeatInterval;

	private int maxSimultaneousDownloads;

	private int chsDelay = 120;
	private int hmsDelay = 5;
	private int adsDelay = 5;
	private boolean syncToServerTime;
	private int downloadRetryAttempts = 3;
	private long updateOnlineStatusInterval;

	private long inactiveDownloadMaxTime = 60000;
	private long checkInactiveDownloadInterval = 10000;

	private int terminalCacheMaxTime;

	private long updateTerminalOnlineStatusInterval = 10000;
	private long checkOfflineTerminalInterval = 1800000;

	private long updateTerminalTimeInterval = 7200000;

	private int tcpPort;

	private int maxSimultaneousHms = -1;
	private long inactiveHmsMaxTime = 60000;
	private long checkInactiveHmsInterval = 10000;
	
	private String terminalLogPPM;
	private String terminalLogSplunk ;
	private String terminalLogSplunkUrl;
	private String terminalLogSplunkToken ;

	public int getChsDelay() {
		return chsDelay;
	}

	@Autowired
	public void setChsDelay(@Value("${pxretailer.chsDelay:120}") int chsDelay) {
		this.chsDelay = chsDelay;
	}

	public int getHmsDelay() {
		return hmsDelay;
	}

	@Autowired
	public void setHmsDelay(@Value("${pxretailer.hmsDelay:5}") int hmsDelay) {
		this.hmsDelay = hmsDelay;
	}

	public int getAdsDelay() {
		return adsDelay;
	}

	@Autowired
	public void setAdsDelay(@Value("${pxretailer.adsDelay:5}") int adsDelay) {
		this.adsDelay = adsDelay;
	}

	public int getDownloadRetryAttempts() {
		return downloadRetryAttempts;
	}

	@Autowired
	public void setDownloadRetryAttempts(@Value("${pxretailer.downloadRetryAttempts:5}") int downloadRetryAttempts) {
		this.downloadRetryAttempts = downloadRetryAttempts;
	}

	public int getMaxSimultaneousDownloads() {
		return maxSimultaneousDownloads;
	}

	@Autowired
	public void setMaxSimultaneousDownloads(
			@Value("${pxretailer.maxSimultaneousDownloads:300}") int maxSimultaneousDownloads) {
		this.maxSimultaneousDownloads = maxSimultaneousDownloads;
	}

	public long getMaxHeartBeatInterval() {
		return maxHeartBeatInterval;
	}

	@Autowired
	public void setMaxHeartBeatInterval(@Value("${pxretailer.maxHeartBeatInterval:300000}") long maxHeartBeatInterval) {
		this.maxHeartBeatInterval = maxHeartBeatInterval;
	}

	public long getUpdateOnlineStatusInterval() {
		return updateOnlineStatusInterval;
	}

	@Autowired
	public void setUpdateOnlineStatusInterval(
			@Value("${pxretailer.updateOnlineStatusIntervalWhenDownloading:10000}") long updateOnlineStatusInterval) {
		this.updateOnlineStatusInterval = updateOnlineStatusInterval;
	}

	public long getInactiveDownloadMaxTime() {
		return inactiveDownloadMaxTime;
	}

	@Autowired
	public void setInactiveDownloadMaxTime(
			@Value("${pxretailer.inactiveDownloadMaxTime:60000}") long inactiveDownloadMaxTime) {
		this.inactiveDownloadMaxTime = inactiveDownloadMaxTime;
	}

	public long getCheckInactiveDownloadInterval() {
		return checkInactiveDownloadInterval;
	}

	@Autowired
	public void setCheckInactiveDownloadInterval(
			@Value("${pxretailer.checkInactiveDownloadInterval:10000}") long checkInactiveDownloadInterval) {
		this.checkInactiveDownloadInterval = checkInactiveDownloadInterval;
	}

	public int getTerminalCacheMaxTime() {
		return terminalCacheMaxTime;
	}

	@Autowired
	public void setTerminalCacheMaxTime(@Value("${pxretailer.terminalCacheMaxTime:7200}") int terminalCacheMaxTime) {
		this.terminalCacheMaxTime = terminalCacheMaxTime;
	}

	public boolean isSyncToServerTime() {
		return syncToServerTime;
	}

	@Autowired
	public void setSyncToServerTime(@Value("${pxretailer.syncToServerTime:false}") boolean syncToServerTime) {
		this.syncToServerTime = syncToServerTime;
	}

	public long getUpdateTerminalOnlineStatusInterval() {
		return updateTerminalOnlineStatusInterval;
	}

	@Autowired
	public void setUpdateTerminalOnlineStatusInterval(
			@Value("${pxretailer.updateTerminalOnlineStatusInterval:10000}") long updateTerminalOnlineStatusInterval) {
		this.updateTerminalOnlineStatusInterval = updateTerminalOnlineStatusInterval;
	}

	public long getCheckOfflineTerminalInterval() {
		return checkOfflineTerminalInterval;
	}

	@Autowired
	public void setCheckOfflineTerminalInterval(
			@Value("${tms.checkOfflineTerminalInterval:1800000}") long checkOfflineTerminalInterval) {
		this.checkOfflineTerminalInterval = checkOfflineTerminalInterval;
	}

	public long getUpdateTerminalTimeInterval() {
		return updateTerminalTimeInterval;
	}

	@Autowired
	public void setUpdateTerminalTimeInterval(
			@Value("${pxretailer.terminalTimeSynInterval:7200000}") long updateTerminalTimeInterval) {
		this.updateTerminalTimeInterval = updateTerminalTimeInterval;
	}

	public int getTcpPort() {
		return tcpPort;
	}

	@Autowired
	public void setTcpPort(@Value("${pxretailer.tcp.port:9070}") int tcpPort) {
		this.tcpPort = tcpPort;
	}

	public int getMaxSimultaneousHms() {
		return maxSimultaneousHms;
	}

	@Autowired
	public void setMaxSimultaneousHms(@Value("${pxretailer.maxSimultaneousHms:-1}") int maxSimultaneousHms) {
		this.maxSimultaneousHms = maxSimultaneousHms;
	}

	public long getInactiveHmsMaxTime() {
		return inactiveHmsMaxTime;
	}

	@Autowired
	public void setInactiveHmsMaxTime(@Value("${pxretailer.inactiveHmsMaxTime:60000}") long inactiveHmsMaxTime) {
		this.inactiveHmsMaxTime = inactiveHmsMaxTime;
	}

	public long getCheckInactiveHmsInterval() {
		return checkInactiveHmsInterval;
	}

	@Autowired
	public void setCheckInactiveHmsInterval(
			@Value("${pxretailer.checkInactiveHmsInterval:10000}") long checkInactiveHmsInterval) {
		this.checkInactiveHmsInterval = checkInactiveHmsInterval;
	}

    public String getTerminalLogPPM() {
        return terminalLogPPM;
    }

    @Autowired
    public void setTerminalLogPPM(@Value("${tms.terminal.log.ppm}") String terminalLogPPM) {
        this.terminalLogPPM = terminalLogPPM;
    }

    public String getTerminalLogSplunk() {
        return terminalLogSplunk;
    }

    @Autowired
    public void setTerminalLogSplunk(@Value("${tms.terminal.log.splunk}") String terminalLogSplunk) {
        this.terminalLogSplunk = terminalLogSplunk;
    }

    public String getTerminalLogSplunkUrl() {
        return terminalLogSplunkUrl;
    }

    @Autowired
    public void setTerminalLogSplunkUrl(@Value("${tms.terminal.log.splunk.url}") String terminalLogSplunkUrl) {
        this.terminalLogSplunkUrl = terminalLogSplunkUrl;
    }

    public String getTerminalLogSplunkToken() {
        return terminalLogSplunkToken;
    }

    @Autowired
    public void setTerminalLogSplunkToken(@Value("${tms.terminal.log.splunk.token}") String terminalLogSplunkToken) {
        this.terminalLogSplunkToken = terminalLogSplunkToken;
    }

}
