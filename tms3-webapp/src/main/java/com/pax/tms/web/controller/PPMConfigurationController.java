package com.pax.tms.web.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pax.fastdfs.conn.TrackerConnectionManager;
import com.pax.tms.web.component.PPMConfiguration;

@Controller
@RequestMapping("/ppmConfiguration")
public class PPMConfigurationController {

	@Autowired
	private PPMConfiguration config;

	@Autowired
	private TrackerConnectionManager fastConfig;

	@RequiresPermissions(value = { "tms:config:view" })
	@ResponseBody
	@RequestMapping("/list")
	public Map<String, Map<String, Object>> getPPMConfiguration(HttpServletRequest request) {

		Map<String, Object> ssoMap = new LinkedHashMap<>();

		ssoMap.put("CAS Server Prefix", config.getCasServerPrefix());
		ssoMap.put("TMS Server Prefix", config.getTmsServerPrefix());

		Map<String, Object> pxMap = new LinkedHashMap<>();
		pxMap.put("Server IP", config.getServerIp());
		pxMap.put("Login Callback URL", config.getLoginCallbackURL());

		Map<String, Object> pxMsgMap = new LinkedHashMap<>();
		pxMsgMap.put("HTTP Port", config.getHttpPort());
		pxMsgMap.put("Max Simultaneous Downloads", config.getMaxSimultaneousDownloads());
		pxMsgMap.put("Download Retry Attempts", config.getDownloadRetryAttempts());
		pxMsgMap.put("Sync to Server Time", config.getSyncToServerTime());
		pxMsgMap.put("Terminal Time Sync Interval", config.getTerminalTimeSynInterval());
		pxMsgMap.put("chsDelay", config.getChsDelay());
		pxMsgMap.put("hmsDelay", config.getHmsDelay());
		pxMsgMap.put("adsDelay", config.getAdsDelay());
		pxMsgMap.put("Max Heart Beat Interval", config.getMaxHeartBeatInterval());

		Map<String, Object> dbMap = new LinkedHashMap<>();
		dbMap.put("User", config.getDbUser());
		dbMap.put("URL", config.getDbUrl());
		dbMap.put("Driver Class", config.getDbDriverClass());
		dbMap.put("Validation Query", config.getValidationQuery());
		dbMap.put("Hibernate Dialect", config.getHibernateDialect());
		dbMap.put("Quartz Config", config.getQuartzConfig());

		Map<String, Object> redisMap = new LinkedHashMap<>();
		redisMap.put("Master", config.getRedisMaster());
		redisMap.put("Sentinels", config.getRedisNodes());

		Map<String, Object> fastDFSMap = new LinkedHashMap<>();
		StringBuilder sb = new StringBuilder();
		for (String str : fastConfig.getTrackerList()) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(str);
		}
		fastDFSMap.put("Tracker", sb.toString());

		Map<String, Object> emailMap = new LinkedHashMap<>();
		emailMap.put("Host", config.getEmailHost());
		emailMap.put("Port", config.getEmailPort());
		emailMap.put("User", config.getEmailUser());
		emailMap.put("From", config.getEmailFrom());
		emailMap.put("Name", config.getEmailName());
		emailMap.put("Auth", config.getEmailAuth());
		emailMap.put("Socket", config.getEmailSocket());
		emailMap.put("Timeout", config.getEmailTimeout());

		Map<String, Object> smsMap = new LinkedHashMap<>();
		smsMap.put("Service Enabled", config.getServiceEnabled());
		smsMap.put("Profiles Config File", config.getProfilesConfigFile());
		smsMap.put("Profile Name", config.getProfileName());
		smsMap.put("Disable Cert Checking", config.getDisableCertChecking());

		Map<String, Object> ldapMap = new LinkedHashMap<>();
		ldapMap.put("Enabled", config.getLdapEnabled());
		ldapMap.put("URL", config.getLdapUrl());
		ldapMap.put("Base DN", config.getLdapBaseDN());
		ldapMap.put("Search Filter", config.getSearchFilter());
		ldapMap.put("Manager DN", config.getManagerDN());
		ldapMap.put("Allow Multiple DN", config.getAllowMultipleDN());
		ldapMap.put("Subtree Search", config.getSubtreeSearch());
		ldapMap.put("SSL", config.getLdapSSL());
		ldapMap.put("Use Start TLS", config.getUseStartTLS());

		Map<String, Object> ldapPwdPolucyMap = new LinkedHashMap<>();
		ldapPwdPolucyMap.put("Warn All", config.getWarnAll());
		ldapPwdPolucyMap.put("Warning Days", config.getWarningDays());
		ldapPwdPolucyMap.put("URL", config.getLdapPwdPolucyUrl());

		Map<String, Object> userManagementMap = new LinkedHashMap<>();
		userManagementMap.put("Lock User After Times of Password Verification Failure",
				config.getLockUserAccountAfterFailedLoginTimes());
		userManagementMap.put("Suspend User After Days of Inactivity", config.getInactiveUserLockDays());
		userManagementMap.put("Warning Days Before User Suspended", config.getInactiveUserLockWarningDays());
		userManagementMap.put("Delete User After Days of Inactivity", config.getInactiveUserDeleteDays());
		userManagementMap.put("Password Expiration Days", config.getPasswordExpirationDays());
		userManagementMap.put("Warning Days Before Password Expired", config.getPasswordExpirationWarningDays());
		userManagementMap.put("Old Password Preserve Number", config.getOldPasswordPreserveNumber());

		Map<String, Object> sysCleanMap = new LinkedHashMap<>();
		sysCleanMap.put("Audit Log Retention Time", config.getAuditLogRetentionTime());
		sysCleanMap.put("User Log Retention Time", config.getUserLogRetentionTime());
		sysCleanMap.put("Log File Retention Time", config.getLogFileRetentionTime());
		sysCleanMap.put("Log File Dirs", config.getLogFileDirs());
		sysCleanMap.put("Terminal Event Log Retention Time", config.getTerminalEventLogRetentionTime());
		sysCleanMap.put("Group Event Log Retention Time", config.getGroupEventLogRetentionTime());
		sysCleanMap.put("Terminal Usage Data Retention Time", config.getTerminalUsageDataRetentionTime());
		sysCleanMap.put("Unused Package Retention Time", config.getUnusedPackageRetentionTime());

		Map<String, Map<String, Object>> mapJson = new LinkedHashMap<>();
		mapJson.put("SSO AUTHENTICATION", ssoMap);
		mapJson.put("PXDesigner", pxMap);
		mapJson.put("PXRetailer Message Server", pxMsgMap);
		mapJson.put("Database", dbMap);
		mapJson.put("Redis", redisMap);
		mapJson.put("FastDFS", fastDFSMap);
		mapJson.put("Email", emailMap);
		mapJson.put("Amazon SMS", smsMap);
		mapJson.put("LDAP", ldapMap);
		mapJson.put("LDAP Password Policy", ldapPwdPolucyMap);
		mapJson.put("User Management", userManagementMap);
		mapJson.put("System Cleaner", sysCleanMap);
		return mapJson;
	}

}
