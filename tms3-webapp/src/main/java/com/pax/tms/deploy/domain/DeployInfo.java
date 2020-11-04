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
package com.pax.tms.deploy.domain;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.pax.tms.deploy.model.DownOrActvStatus;

public class DeployInfo {

	private String deploySource;
	private String deploySourceNamePath;
	private String tsn;
	private Long deployId;
	private String operator;
	private String destModel;
	private Long groupId;
	private Long pkgId;
	private Integer status = 1;
	private String pkgName;
	private String pkgVersion;
	private String pgmType;
	private String paramVersion;
	private String paramSet;
	private Long pkgSchemaId;
	private Date dwnlStartTime;
	private String dwnlStartTimeText;
	private Date dwnlEndTime;
	private String dwnlEndTimeText;
	private Date dwnlTime;
	private Date actvTime;
	private DownOrActvStatus dwnStatus;
	private DownOrActvStatus actvStatus;
	private Integer downReTryCount;
	private Integer actvReTryCount;
	private Date actvStartTime;
	private String actvStartTimeText;
	private String timeZone;
	private boolean daylightSaving;
	private boolean dwnlOrder = false;
	private boolean forceUpdate = false;
	private boolean onlyParam = false;
	private boolean isInherit = true;
	private Date modifyDate;
	private Integer latestType;

	public String getDeploySource() {
		return deploySource;
	}

	public void setDeploySource(String deploySource) {
		this.deploySource = deploySource;
	}

	public String getDeploySourceNamePath() {
		return deploySourceNamePath;
	}

	public void setDeploySourceNamePath(String deploySourceNamePath) {
		this.deploySourceNamePath = deploySourceNamePath;
	}

	public String getTsn() {
		return tsn;
	}

	public Long getDeployId() {
		return deployId;
	}

	public void setDeployId(Long deployId) {
		this.deployId = deployId;
	}

	public void setTsn(String tsn) {
		this.tsn = tsn;
	}

	public String getDestModel() {
		return destModel;
	}

	public void setDestModel(String destModel) {
		this.destModel = destModel;
	}

	public Long getPkgId() {
		return pkgId;
	}

	public void setPkgId(Long pkgId) {
		this.pkgId = pkgId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public String getPkgName() {
		return pkgName;
	}

	public void setPkgName(String pkgName) {
		this.pkgName = pkgName;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getPkgVersion() {
		return pkgVersion;
	}

	public void setPkgVersion(String pkgVersion) {
		this.pkgVersion = pkgVersion;
	}

	public String getPgmType() {
		return pgmType;
	}

	public void setPgmType(String pgmType) {
		this.pgmType = pgmType;
	}

	public String getParamVersion() {
		return paramVersion;
	}

	public void setParamVersion(String paramVersion) {
		this.paramVersion = paramVersion;
	}

	public String getParamSet() {
		return paramSet;
	}

	public void setParamSet(String paramSet) {
		this.paramSet = paramSet;
	}

	public Long getPkgSchemaId() {
		return pkgSchemaId;
	}

	public void setPkgSchemaId(Long pkgSchemaId) {
		this.pkgSchemaId = pkgSchemaId;
	}

	public Date getDwnlStartTime() {
		return dwnlStartTime;
	}

	public void setDwnlStartTime(Date dwnlStartTime) {
		this.dwnlStartTime = dwnlStartTime;
	}

	public Integer getDownReTryCount() {
		return downReTryCount;
	}

	public void setDownReTryCount(Integer downReTryCount) {
		this.downReTryCount = downReTryCount;
	}

	public Integer getActvReTryCount() {
		return actvReTryCount;
	}

	public void setActvReTryCount(Integer actvReTryCount) {
		this.actvReTryCount = actvReTryCount;
	}

	public DownOrActvStatus getDwnStatus() {
		return dwnStatus;
	}

	public void setDwnStatus(DownOrActvStatus dwnStatus) {
		this.dwnStatus = dwnStatus;
	}

	public DownOrActvStatus getActvStatus() {
		return actvStatus;
	}

	public void setActvStatus(DownOrActvStatus actvStatus) {
		this.actvStatus = actvStatus;
	}

	public Date getActvStartTime() {
		return actvStartTime;
	}

	public void setActvStartTime(Date actvStartTime) {
		this.actvStartTime = actvStartTime;
	}

	public Date getDwnlTime() {
		return dwnlTime;
	}

	public void setDwnlTime(Date dwnlTime) {
		this.dwnlTime = dwnlTime;
	}

	public Date getActvTime() {
		return actvTime;
	}

	public void setActvTime(Date actvTime) {
		this.actvTime = actvTime;
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

	public boolean isDwnlOrder() {
		return dwnlOrder;
	}

	public void setDwnlOrder(boolean dwnlOrder) {
		this.dwnlOrder = dwnlOrder;
	}

	public boolean isForceUpdate() {
		return forceUpdate;
	}

	public void setForceUpdate(boolean forceUpdate) {
		this.forceUpdate = forceUpdate;
	}

	public boolean isOnlyParam() {
		return onlyParam;
	}

	public void setOnlyParam(boolean onlyParam) {
		this.onlyParam = onlyParam;
	}

	public boolean isInherit() {
		return isInherit;
	}

	public void setInherit(boolean isInherit) {
		this.isInherit = isInherit;
	}

	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	public static void updateGroupInheritStatus(Long groupId, List<DeployInfo> deployInfos) {
		if (CollectionUtils.isEmpty(deployInfos)) {
			return;
		}
		for (DeployInfo deployInfo : deployInfos) {
			if (groupId == deployInfo.getGroupId()) {
				deployInfo.setInherit(false);

			}

		}

	}

	public static void updateTerminalInheritStatus(List<DeployInfo> deployInfos) {
		if (CollectionUtils.isEmpty(deployInfos)) {
			return;
		}
		for (DeployInfo deployInfo : deployInfos) {
			if (StringUtils.isEmpty(deployInfo.getDeploySource())) {
				deployInfo.setInherit(false);

			}

		}

	}

	public Date getDwnlEndTime() {
		return dwnlEndTime;
	}

	public void setDwnlEndTime(Date dwnlEndTime) {
		this.dwnlEndTime = dwnlEndTime;
	}

	public String getDwnlStartTimeText() {
		return dwnlStartTimeText;
	}

	public void setDwnlStartTimeText(String dwnlStartTimeText) {
		this.dwnlStartTimeText = dwnlStartTimeText;
	}

	public String getDwnlEndTimeText() {
		return dwnlEndTimeText;
	}

	public void setDwnlEndTimeText(String dwnlEndTimeText) {
		this.dwnlEndTimeText = dwnlEndTimeText;
	}

	public String getActvStartTimeText() {
		return actvStartTimeText;
	}

	public void setActvStartTimeText(String actvStartTimeText) {
		this.actvStartTimeText = actvStartTimeText;
	}

    public Integer getLatestType() {
        return latestType;
    }

    public void setLatestType(Integer latestType) {
        this.latestType = latestType;
    }

}
