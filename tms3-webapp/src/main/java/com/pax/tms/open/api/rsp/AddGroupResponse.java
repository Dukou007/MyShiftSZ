package com.pax.tms.open.api.rsp;

import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class AddGroupResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -630135501098341714L;
	@ApiModelProperty(value = "group id ")
	private Long groupId;
	@ApiModelProperty(value = "group name ")
	private String groupName;
	@ApiModelProperty(value = "group id path ")
	private String idPath;
	@ApiModelProperty(value = "group name path ")
	private String namePath;
	@ApiModelProperty(value = "country name ")
	private String countryName;
	@ApiModelProperty(value = "state province name ")
	private String stateProvinceName;
	@ApiModelProperty(value = "city name ")
	private String cityName;
	@ApiModelProperty(value = "zipode ")
	private String zipCode;
	@ApiModelProperty(value = "daylight saving")
	private boolean daylightSaving;
	@ApiModelProperty(value = "timezone")
	private String timeZone;
	@ApiModelProperty(value = "address")
	private String address;
	@ApiModelProperty(value = "description")
	private String description;
	@ApiModelProperty(value = "create user")
	private String createdBy;
	@ApiModelProperty(value = "create time")
	private Date createdOn;
	@ApiModelProperty(value = "update user")
	private String updatedBy;
	@ApiModelProperty(value = "update time")
	private Date updatedOn;

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getIdPath() {
		return idPath;
	}

	public void setIdPath(String idPath) {
		this.idPath = idPath;
	}

	public String getNamePath() {
		return namePath;
	}

	public void setNamePath(String namePath) {
		this.namePath = namePath;
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

	public boolean isDaylightSaving() {
		return daylightSaving;
	}

	public void setDaylightSaving(boolean daylightSaving) {
		this.daylightSaving = daylightSaving;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

}
