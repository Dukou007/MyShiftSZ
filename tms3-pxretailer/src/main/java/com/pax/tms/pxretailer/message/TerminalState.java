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
package com.pax.tms.pxretailer.message;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.vertx.core.json.Json;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TerminalState implements Serializable {

	private static final long serialVersionUID = 3749545603851564936L;

	private int online = 0;
	private int privacyShieldAttached = 0;
	private int stylusAttached = 0;
	private Integer tampered;
	private Integer SREDEnabled;
	private Integer RKICapable;
	private String localtime;

	public TerminalState() {
		online = 0;
		privacyShieldAttached = 0;
		stylusAttached = 0;
	}

	public int getOnline() {
		return online;
	}

	public void setOnline(int online) {
		this.online = online;
	}

	public int getPrivacyShieldAttached() {
		return privacyShieldAttached;
	}

	public void setPrivacyShieldAttached(int privacyShieldAttached) {
		this.privacyShieldAttached = privacyShieldAttached;
	}

	public int getStylusAttached() {
		return stylusAttached;
	}

	public void setStylusAttached(int stylusAttached) {
		this.stylusAttached = stylusAttached;
	}

	public Integer getTampered() {
		return tampered;
	}

	public void setTampered(Integer tampered) {
		this.tampered = tampered;
	}
	@JsonProperty("SREDEnabled")
	public Integer getSREDEnabled() {
		return SREDEnabled;
	}
	@JsonProperty("SREDEnabled")
	public void setSREDEnabled(Integer sREDEnabled) {
		SREDEnabled = sREDEnabled;
	}
	
	@JsonProperty("RKICapable")
	public Integer getRKICapable() {
        return RKICapable;
    }

	@JsonProperty("RKICapable")
    public void setRKICapable(Integer rKICapable) {
        RKICapable = rKICapable;
    }

    public String getLocaltime() {
		return localtime;
	}

	public void setLocaltime(String localtime) {
		this.localtime = localtime;
	}

	@Override
	public String toString() {
		return Json.encodePrettily(this);
	}

}
