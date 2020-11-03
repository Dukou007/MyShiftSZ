package com.pax.tms.pxretailer.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GetConfigsResponse {

	private String name;
	private String version;
	private String chsAddress;
	private String hmsAddress;
	private String adsAddress;
	private int servicePort;
	private int chsDelay;
	private int hmsDelay;
	private int adsDelay;
	private long offlineDuration;
	private int maxSimultaneousDownloads;
	private int fileDownloadChunkSize;

	public void setName(String name) {
		this.name = name;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setChsAddress(String chsAddress) {
		this.chsAddress = chsAddress;
	}

	public void setHmsAddress(String hmsAddress) {
		this.hmsAddress = hmsAddress;
	}

	public void setAdsAddress(String adsAddress) {
		this.adsAddress = adsAddress;
	}

	public void setServicePort(int servicePort) {
		this.servicePort = servicePort;
	}

	public void setChsDelay(int chsDelay) {
		this.chsDelay = chsDelay;
	}

	public void setHmsDelay(int hmsDelay) {
		this.hmsDelay = hmsDelay;
	}

	public void setAdsDelay(int adsDelay) {
		this.adsDelay = adsDelay;
	}

	public void setOfflineDuration(long offlineDuration) {
		this.offlineDuration = offlineDuration;
	}

	public void setMaxSimultaneousDownloads(int maxSimultaneousDownloads) {
		this.maxSimultaneousDownloads = maxSimultaneousDownloads;
	}

	public void setFileDownloadChunkSize(int fileDownloadChunkSize) {
		this.fileDownloadChunkSize = fileDownloadChunkSize;
	}

	@Override
	public String toString() {
		return "GetConfigsResponse [name=" + name + ", version=" + version + ", chsAddress=" + chsAddress
				+ ", hmsAddress=" + hmsAddress + ", adsAddress=" + adsAddress + ", servicePort=" + servicePort
				+ ", chsDelay=" + chsDelay + ", hmsDelay=" + hmsDelay + ", adsDelay=" + adsDelay
				+ ", offlineTimerDuration=" + offlineDuration + ", maxSimultaneousDownloads=" + maxSimultaneousDownloads
				+ ", fileDownloadChunkSize=" + fileDownloadChunkSize + "]";

	}

}
