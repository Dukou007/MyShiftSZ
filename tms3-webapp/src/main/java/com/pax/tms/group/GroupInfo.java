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
package com.pax.tms.group;

import java.util.ArrayList;
import java.util.List;

import com.pax.tms.terminal.domain.TerminalInfo;

public class GroupInfo {
	private String name;
	private String country;
	private String stateOrProvice;
	private String city;
	private String zipCode;
	private String timeZone;
	private String daylight;
	private String address;
	private String description;
	private List<TerminalInfo> terminalInfos;
	private List<GroupInfo> childens;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getStateOrProvice() {
		return stateOrProvice;
	}

	public void setStateOrProvice(String stateOrProvice) {
		this.stateOrProvice = stateOrProvice;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public String getDaylight() {
		return daylight;
	}

	public void setDaylight(String daylight) {
		this.daylight = daylight;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<GroupInfo> getChildens() {
		return childens;
	}

	public void setChildens(List<GroupInfo> childens) {
		this.childens = childens;
	}

	public List<TerminalInfo> getTerminalInfos() {
		return terminalInfos;
	}

	public void setTerminalInfos(List<TerminalInfo> terminalInfos) {
		this.terminalInfos = terminalInfos;
	}

	public void addChildTerminal(TerminalInfo terminalInfo) {
		if (terminalInfos == null) {
			terminalInfos = new ArrayList<TerminalInfo>();
		}
		terminalInfos.add(terminalInfo);

	}

	public void addChildGroup(GroupInfo groupInfo) {
		if (childens == null) {
			childens = new ArrayList<GroupInfo>();
		}
		childens.add(groupInfo);

	}

}
