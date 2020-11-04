package com.pax.tms.monitor.domain;

public class TimeZoneInfo {
	private String timezone;
	private boolean daylightSaving;

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public boolean isDaylightSaving() {
		return daylightSaving;
	}

	public void setDaylightSaving(boolean daylightSaving) {
		this.daylightSaving = daylightSaving;
	}

}
