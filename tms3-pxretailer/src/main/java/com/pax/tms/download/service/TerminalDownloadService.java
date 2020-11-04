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
package com.pax.tms.download.service;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import com.pax.tms.deploy.model.DownOrActvStatus;
import com.pax.tms.download.model.Deployment;
import com.pax.tms.download.model.PkgFile;
import com.pax.tms.download.model.SystemConf;
import com.pax.tms.download.model.Terminal;
import com.pax.tms.download.model.TerminalLog;

public interface TerminalDownloadService {

	// get
	Terminal getTerminalBySn(String deviceType, String deviceSerialNumber);

	Deployment getScheduledPackage(Terminal terminal);

	PkgFile getPackageFile(long pkgId);

	PkgFile getProgramFile(long pkgId, long fileId);

	PkgFile getProgramFile(long pkgId, String fileName);

	// update

	void addUnregisteredTerminal(String deviceType, String deviceSerialNumber, String sourceIp, Date accessTime);

	void updateUnregisteredTerminal(String deviceType, String deviceSerialNumber, String sourceIp, Date accessTime);

	void setTerminalOnline(Terminal terminal, String sourceIp, Date accessTime);

	void saveHealthMonitorInfo(Terminal terminal);

	void startDownload(Terminal terminal, Deployment deployment, Date accessTime);

	void updateDeploymentStatus(Terminal terminal, Date accessTime, long deployId, DownOrActvStatus downloadStatus,
			DownOrActvStatus activationStatus,String activationCode);

	// batch update

	void setTerminalOffline(Set<String> terminals, long maxLastAccessTime);

	void setTerminalOffline(long maxLastAccessTime);

	void updateTerminalAccessTime(Map<String, String> map);

	void updateStorgeTerminalInstallAppsAndEvent(Terminal terminal);

	void updateTerminalInstallAppReportTime(String terminalSn);

	String getTerminalInstalledApps(String terminalSn);
	
	void saveTerminalLog(TerminalLog terminalLog);
	
	void sendTerminalLogToSplunk(String splunkUrl, String content,String splunkToken);
	
	SystemConf findByParaKey(String paraKey);
	
	void updateStorgeTerminalSysmetricKeysAndEvent(Terminal terminal);
	
	void updateTerminalSysmetricKeysReportTime(String terminalSn);
	
	String getTerminalSysmetricKeys(String terminalSn);
}
