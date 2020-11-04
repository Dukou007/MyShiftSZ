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
package com.pax.tms.download.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pax.tms.deploy.model.DownOrActvStatus;
import com.pax.tms.download.model.Deployment;
import com.pax.tms.download.model.Event;
import com.pax.tms.download.model.GroupMsg;
import com.pax.tms.download.model.PkgFile;
import com.pax.tms.download.model.Terminal;
import com.pax.tms.download.model.TerminalLog;
import com.pax.tms.download.model.TerminalStatus;
import com.pax.tms.download.model.TerminalUsageMessage;
import com.pax.tms.download.model.TerminalUsageReport;

public interface TerminalDownloadDao {

	// query
	Terminal getTerminalBySn(String deviceType, String deviceSerialNumber);

	TerminalStatus getTerminalStatus(String terminalId);

	boolean isUnregisteredTerminal(String deviceType, String deviceSerialNumber);

	PkgFile getPackageFile(long pkgId);

	PkgFile getProgramFile(long pkgId, long fileId);

	PkgFile getProgramFile(long pkgId, String fileName);

	Deployment getScheduledPackage(Terminal terminal);

	Deployment getDeployment(long deployId, String terminalId);

	Deployment getDeploymentHistory(long deployId, String terminalId);

	// update

	void addUnregisteredTerminal(String deviceType, String deviceSerialNumber, String sourceIp, Date accessTime);

	int updateUnregisteredTerminal(String deviceType, String deviceSerialNumber, String sourceIp, Date lastAccessTime);

	void addTerminalStatus(TerminalStatus terminalStatus);

	void updateTerminalStatus(Terminal terminal, Map<String, Object> changedFields);

	void setTerminalOnline(Terminal terminal, String sourceIp, Date accessTime);

	void updateDownloadStatus(Terminal terminal, DownOrActvStatus downloadStatus, Date accessTime);

	void updateActivationStatus(Terminal terminal, DownOrActvStatus activationStatus, Date accessTime);

	void saveUsageReport(TerminalUsageReport usageReport);

	void saveEvent(Event event);

	void saveEventList(List<Event> events);

	void updateDeployStatusAtDwnlStart(Terminal terminal, Deployment deployment, Date accessTime);

	void updateTerminalStatusAtDwnlStart(Terminal terminal, Deployment deployment, Date accessTime);

	void updateDownloadReportAtDwnlStart(Terminal terminal, Deployment deploy, Date startTime);

	void updateDeploymentStatusAtDwnlFinish(Terminal terminal, Deployment deploy, DownOrActvStatus downloadStatus,
			Date updateTime);

	void updateDownloadReportAtDwnlFinish(Terminal terminal, Deployment deploy, DownOrActvStatus downloadStatus,
			Date updateTime);

	void updateDeploymentStatusAtActvFinish(Terminal terminal, Deployment deploy, DownOrActvStatus activationStatus,
			Date updateTime);

	void deleteDeploymentAtActvFinish(Terminal terminal, Deployment deploy, DownOrActvStatus activationStatus,
			Date updateTime);

	void updateDownloadReportAtActvFinish(Terminal terminal, Deployment deploy, DownOrActvStatus activationStatus,
			Date updateTime);

	void addDownloadRetryCount(Terminal terminal, Deployment deployment);

	// batch
	void updateTerminalOnlineStatus(Set<String> terminals, long maxLastAccessTime);

	void setTerminalOffline(long maxLastAccessTime);

	void updateTerminalLastAccessTime(Map<String, String> map);

	void flush();

	void updateTerminalInstallApps(String terminalSn, String terminalInstalledApps);

	void updateTerminalInstallAppReportTime(String terminalSn);

	String getTerminalInstalledApps(String terminalSn);
	

	Integer getDownloadAndActivation(String itemName,String status,String terminalSN);
	
	TerminalUsageReport geTerminalUsageReport(String terminalSn,Long groupId,String startTime, String endTime);
    
    List<TerminalUsageMessage> getTerminalUsageMessage(String terminalSn,Long groupId,String msgCycle);
    
    void addTerminalUsageReport(TerminalUsageReport terminalUsageReport);
    
    void addTerminalUsageMessage(TerminalUsageMessage terminalUsageMessage);
    
    void updateTerminalUsageReport(TerminalUsageReport terminalUsageReport);
    
    void updateTerminalUsageMessage(TerminalUsageMessage terminalUsageMessage);
    
    List<GroupMsg> getGroupMsgByTerminalId(String terminalId);
    
    void addTerminalLog(TerminalLog terminalLog);
    
    void updateTerminalLocalTime(String terminalSn, String localTime);
    
    String getTerminalSysmetricKeys(String terminalSn);
    
    void updateTerminalSysmetricKeys(String terminalSn, String terminalSysmetricKeys);
    
    void updateTerminalSysmetricKeysReportTime(String terminalSn);
}
