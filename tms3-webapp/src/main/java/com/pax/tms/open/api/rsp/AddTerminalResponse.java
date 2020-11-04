package com.pax.tms.open.api.rsp;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "AddTerminalResponse")
public class AddTerminalResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7393469319074775507L;
	@ApiModelProperty(value = "total terminal sn")
	private Set<String> totalTsns;

	@ApiModelProperty(value = "exist terminal sn and need to assign current group id")
	private List<String> existTsnNeedToAssignGroup;

	@ApiModelProperty(value = "exist terminal sn and doesn't need to assign current group id")
	private List<String> existTsnIgnoreToAssignGroup;

	@ApiModelProperty(value = "new terminal tsn")
	private Collection<String> newTsns;

	@ApiModelProperty(value = "terminal sn that does not have permission in current user")
	private List<String> ownByOtherParallerGroupTsn;

	@ApiModelProperty(value = "terminal type")
	private String terminalType;

	@ApiModelProperty(value = "country name")
	private String countryName;

	@ApiModelProperty(value = "state province name")
	private String stateProvinceName;

	@ApiModelProperty(value = "city name")
	private String cityName;

	@ApiModelProperty(value = "zipcode")
	private String zipCode;

	@ApiModelProperty(value = "timezone")
	private String timeZone;

	@ApiModelProperty(value = "if start daylight saving")
	private boolean daylightSaving;

	@ApiModelProperty(value = "if synchronize to server")
	private boolean syncToServerTime;

	@ApiModelProperty(value = "group id")
	private Long groupId;

	@ApiModelProperty(value = "group name path")
	private String groupNamePath;

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

	public Set<String> getTotalTsns() {
		return totalTsns;
	}

	public void setTotalTsns(Set<String> totalTsns) {
		this.totalTsns = totalTsns;
	}

	public List<String> getExistTsnNeedToAssignGroup() {
		return existTsnNeedToAssignGroup;
	}

	public void setExistTsnNeedToAssignGroup(List<String> existTsnNeedToAssignGroup) {
		this.existTsnNeedToAssignGroup = existTsnNeedToAssignGroup;
	}

	public List<String> getExistTsnIgnoreToAssignGroup() {
		return existTsnIgnoreToAssignGroup;
	}

	public void setExistTsnIgnoreToAssignGroup(List<String> existTsnIgnoreToAssignGroup) {
		this.existTsnIgnoreToAssignGroup = existTsnIgnoreToAssignGroup;
	}

	public Collection<String> getNewTsns() {
		return newTsns;
	}

	public void setNewTsns(Collection<String> newTsns) {
		this.newTsns = newTsns;
	}

	public List<String> getOwnByOtherParallerGroupTsn() {
		return ownByOtherParallerGroupTsn;
	}

	public void setOwnByOtherParallerGroupTsn(List<String> ownByOtherParallerGroupTsn) {
		this.ownByOtherParallerGroupTsn = ownByOtherParallerGroupTsn;
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

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public String getGroupNamePath() {
		return groupNamePath;
	}

	public void setGroupNamePath(String groupNamePath) {
		this.groupNamePath = groupNamePath;
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
