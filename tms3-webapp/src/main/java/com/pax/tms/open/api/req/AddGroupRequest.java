package com.pax.tms.open.api.req;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class AddGroupRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1304604205832042147L;

	@ApiModelProperty(value = "parent group id ", required = true)
	private Long parentId;

	@ApiModelProperty(value = "group name", required = true)
	private String groupName;

	@ApiModelProperty(value = "timezone", required = true)
	private String timeZone;

	@ApiModelProperty(value = "daylight saving 0|false,1|true,default value is false", required = false)
	private boolean daylightSaving = false;

	@ApiModelProperty(value = "country name", required = true)
	private String countryName;

	@ApiModelProperty(value = "state province name", required = true)
	private String stateProvinceName;

	@ApiModelProperty(value = "city name", required = true)
	private String cityName;

	@ApiModelProperty(value = "zipcode", required = true)
	private String zipCode;
	@ApiModelProperty(value = "address", required = false)
	private String address;
	@ApiModelProperty(value = "description", required = false)
	private String description;

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
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

}
