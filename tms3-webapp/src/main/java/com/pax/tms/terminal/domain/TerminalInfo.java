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
package com.pax.tms.terminal.domain;

public class TerminalInfo {

	private String tid;
	private String destModel;
	private String country;
	private String province;
	private String city;
	private String zipcode;
	private String timeZone;
	private String address;
	private String daylight;
	private String description;
	private String syncToServerTime;

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public String getDestModel() {
		return destModel;
	}

	public void setDestModel(String destModel) {
		this.destModel = destModel;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDaylight() {
		return daylight;
	}

	public void setDaylight(String daylight) {
		this.daylight = daylight;
	}
	
	public String getSyncToServerTime() {
		return syncToServerTime;
	}

	public void setSyncToServerTime(String syncToServerTime) {
		this.syncToServerTime = syncToServerTime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
