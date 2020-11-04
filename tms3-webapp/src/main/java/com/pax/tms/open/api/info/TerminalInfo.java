package com.pax.tms.open.api.info;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel
public class TerminalInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "terminalSN", required = true)
	private String terminalSN;
	
	@ApiModelProperty(value = "status", required = true)
	private boolean status;
	
	@ApiModelProperty(value = "terminalType", required = true)
	private String terminalType;
	
	@ApiModelProperty(value = "online", required = true)
	private boolean online;
	
	@ApiModelProperty(value = "groupNamePath", required = true)
	private String groupNamePath;
	
	@ApiModelProperty(value = "lastSourceIp", required = true)
	private String lastSourceIp;

	public String getTerminalSN() {
		return terminalSN;
	}

	public void setTerminalSN(String terminalSN) {
		this.terminalSN = terminalSN;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getTerminalType() {
		return terminalType;
	}

	public void setTerminalType(String terminalType) {
		this.terminalType = terminalType;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	public String getGroupNamePath() {
		return groupNamePath;
	}

	public void setGroupNamePath(String groupNamePath) {
		this.groupNamePath = groupNamePath;
	}

	public String getLastSourceIp() {
		return lastSourceIp;
	}

	public void setLastSourceIp(String lastSourceIp) {
		this.lastSourceIp = lastSourceIp;
	}

}
