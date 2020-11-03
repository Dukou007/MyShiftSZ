package com.pax.tms.open.api.info;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "TerminalDetail", description = "Terminal details")
public class TerminalDetail implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "Tsn", required = true)
	private String tsn;
	
	@ApiModelProperty(value = "TerminalType", required = true)
	private String terminalType;
	
	@ApiModelProperty(value = "Status", example = "Active", required = true)
	private String status = "Active";
	
	@ApiModelProperty(value = "Country", required = true)
	private String country;
	
	@ApiModelProperty(value = "Province", required = true)
	private String province;
	
	@ApiModelProperty(value = "City", required = true)
	private String city;
	
	@ApiModelProperty(value = "ZipCode", required = true)
	private String zipCode;
	
	@ApiModelProperty(value = "TimeZone id", required = true)
	private String timeZone;
	
	@ApiModelProperty(value = "DaylightSaving", example = "Disable", required = true)
	private String daylightSaving = "Disable";
	
	@ApiModelProperty(value = "SyncToServerTime", example = "Disable", required = true)
	private String syncToServerTime = "Disable";
	
	@ApiModelProperty(value = "Address", required = false)
	private String address;
	
	@ApiModelProperty(value = "groupPath", required = true)
	private List<String> groupPath;
	
	@ApiModelProperty(value = "description", required = false)
	private String description;
	
	@ApiModelProperty(value = "installApps", required = true)
	private String installApps;

	public String getTsn() {
		return tsn;
	}

	public void setTsn(String tsn) {
		this.tsn = tsn;
	}

	public String getTerminalType() {
		return terminalType;
	}

	public void setTerminalType(String terminalType) {
		this.terminalType = terminalType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public String getDaylightSaving() {
		return daylightSaving;
	}

	public void setDaylightSaving(String daylightSaving) {
		this.daylightSaving = daylightSaving;
	}

	public String getSyncToServerTime() {
		return syncToServerTime;
	}

	public void setSyncToServerTime(String syncToServerTime) {
		this.syncToServerTime = syncToServerTime;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public List<String> getGroupPath() {
		return groupPath;
	}

	public void setGroupPath(List<String> groupPath) {
		this.groupPath = groupPath;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getInstallApps() {
		return installApps;
	}

	public void setInstallApps(String installApps) {
		this.installApps = installApps;
	}

}
