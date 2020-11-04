package com.pax.tms.open.api.req;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;

public class AddTerminalRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -329776017263912707L;

	@ApiModelProperty(value = "group id ", required = true)
	private Long groupId;

	@ApiModelProperty(value = "terminal sn", required = true)
	private String terminalSN;

	@ApiModelProperty(value = "terminal type", required = true)
	private String terminalType;

	@ApiModelProperty(value = "country name", required = true)
	private String countryName;

	@ApiModelProperty(value = "state province name", required = true)
	private String stateProvinceName;

	@ApiModelProperty(value = "city name", required = true)
	private String cityName;

	@ApiModelProperty(value = "zipcode", required = true)
	private String zipCode;

	@ApiModelProperty(value = "timezone", required = true)
	private String timeZone;

	@ApiModelProperty(value = "daylight saving 0|false,1|true, default value is false", required = false)
	private boolean daylightSaving = false;

	@ApiModelProperty(value = "synchronize server time  0|false,1|true,default value is false", required = false)
	private boolean syncToServerTime = false;

	@ApiModelProperty(value = "description", required = false)
	private String description;
	@ApiModelProperty(value = "address", required = false)
	private String address;

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public String getTerminalSN() {
        return terminalSN;
    }

    public void setTerminalSN(String terminalSN) {
        this.terminalSN = terminalSN;
    }

    public String getTerminalType() {
		return terminalType;
	}

	public void setTerminalType(String terminalType) {
		this.terminalType = terminalType;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}


	public String getStateProvinceName() {
		return stateProvinceName;
	}

	public void setStateProvinceName(String stateProvinceName) {
		this.stateProvinceName = stateProvinceName;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
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

	public boolean isDaylightSaving() {
		return daylightSaving;
	}

	public void setDaylightSaving(boolean daylightSaving) {
		this.daylightSaving = daylightSaving;
	}

	public boolean isSyncToServerTime() {
		return syncToServerTime;
	}

	public void setSyncToServerTime(boolean syncToServerTime) {
		this.syncToServerTime = syncToServerTime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

}
