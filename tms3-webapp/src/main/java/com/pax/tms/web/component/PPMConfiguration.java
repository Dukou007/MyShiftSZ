package com.pax.tms.web.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.pax.tms.web.controller.PPMVersion;

@Component
public class PPMConfiguration {

	private String casServerPrefix;
	private String tmsServerPrefix;
	private String serverIp;
	private String loginCallbackURL;

	private String httpPort;
	private String maxSimultaneousDownloads;
	private String downloadRetryAttempts;
	private String SyncToServerTime;
	private String chsDelay;
	private String hmsDelay;
	private String adsDelay;
	private String maxHeartBeatInterval;

	private String dbUser;
	private String dbUrl;
	private String dbDriverClass;
	private String validationQuery;
	private String hibernateDialect;
	private String quartzConfig;

	private String redisMaster;
	private String redisNodes;

	private String emailHost;
	private String emailPort;
	private String emailUser;
	private String emailFrom;
	private String emailName;
	private String emailAuth;
	private String emailSocket;
	private String emailTimeout;

	private String serviceEnabled;
	private String profilesConfigFile;
	private String profileName;
	private String disableCertChecking;

	private String ldapEnabled;
	private String ldapUrl;
	private String ldapBaseDN;
	private String searchFilter;
	private String managerDN;
	private String allowMultipleDN;
	private String subtreeSearch;
	private String ldapSSL;
	private String useStartTLS;

	private String warnAll;
	private String warningDays;
	private String ldapPwdPolucyUrl;

	private String lockUserAccountAfterFailedLoginTimes;
	private String inactiveUserLockDays;
	private String inactiveUserLockWarningDays;
	private String inactiveUserDeleteDays;
	private String passwordExpirationDays;
	private String passwordExpirationWarningDays;
	private String oldPasswordPreserveNumber;

	private String auditLogRetentionTime;
	private String userLogRetentionTime;
	private String logFileRetentionTime;
	private String logFileDirs;
	private String terminalEventLogRetentionTime;
	private String groupEventLogRetentionTime;
	private String terminalUsageDataRetentionTime;
	private String unusedPackageRetentionTime;
	private long terminalTimeSynInterval;

	private String tmsMainVersion;

	private String tmsVersion;
	
    private String terminalLogPPM;
    
    private String terminalLogSplunk ;
    
    private String terminalLogSplunkUrl;
    
    private String terminalLogSplunkToken ;

	public String getTmsMainVersion() {
		return tmsMainVersion;
	}

	@Autowired
	public void setTmsMainVersion(@Value("${tms.mainVersion}") String tmsMainVersion) {
		this.tmsMainVersion = tmsMainVersion;
		PPMVersion.setPPMMainVersion(tmsMainVersion);
	}

	public String getTmsVersion() {
		return tmsVersion;
	}

	@Autowired
	public void setTmsVersion(@Value("${tms.version}") String tmsVersion) {
		this.tmsVersion = tmsVersion;
		PPMVersion.setPPMVersion(tmsVersion);
	}

	@Autowired
	public void setCasServerPrefix(@Value("${cas.server.prefix:9999}") String casServerPrefix) {
		this.casServerPrefix = casServerPrefix;
	}

	@Autowired
	public void setTmsServerPrefix(@Value("${tms.server.prefix:1000}") String tmsServerPrefix) {
		this.tmsServerPrefix = tmsServerPrefix;
	}

	@Autowired
	public void setServerIp(@Value("${pxdesigner.server.ip}") String serverIp) {
		this.serverIp = serverIp;
	}

	@Autowired
	public void setLoginCallbackURL(@Value("${pxdesigner.login.callbackUrl}") String loginCallbackURL) {
		this.loginCallbackURL = loginCallbackURL;
	}

	@Autowired
	public void setHttpPort(@Value("${pxretailer.tcp.port}") String httpPort) {
		this.httpPort = httpPort;
	}

	@Autowired
	public void setMaxSimultaneousDownloads(
			@Value("${pxretailer.maxSimultaneousDownloads}") String maxSimultaneousDownloads) {
		this.maxSimultaneousDownloads = maxSimultaneousDownloads;
	}

	@Autowired
	public void setDownloadRetryAttempts(@Value("${pxretailer.downloadRetryAttempts}") String downloadRetryAttempts) {
		this.downloadRetryAttempts = downloadRetryAttempts;
	}

	@Autowired
	public void setSyncToServerTime(@Value("${pxretailer.syncToServerTime}") String syncToServerTime) {
		SyncToServerTime = syncToServerTime;
	}

	@Autowired
	public void setChsDelay(@Value("${pxretailer.chsDelay}") String chsDelay) {
		this.chsDelay = chsDelay;
	}

	@Autowired
	public void setHmsDelay(@Value("${pxretailer.hmsDelay}") String hmsDelay) {
		this.hmsDelay = hmsDelay;
	}

	@Autowired
	public void setAdsDelay(@Value("${pxretailer.adsDelay}") String adsDelay) {
		this.adsDelay = adsDelay;
	}

	@Autowired
	public void setMaxHeartBeatInterval(@Value("${pxretailer.maxHeartBeatInterval}") String maxHeartBeatInterval) {
		this.maxHeartBeatInterval = maxHeartBeatInterval;
	}

	@Autowired
	public void setDbUser(@Value("${database.user}") String dbUser) {
		this.dbUser = dbUser;
	}

	@Autowired
	public void setDbUrl(@Value("${database.url}") String dbUrl) {
		this.dbUrl = dbUrl;
	}

	@Autowired
	public void setDbDriverClass(@Value("${database.driverClass}") String dbDriverClass) {
		this.dbDriverClass = dbDriverClass;
	}

	@Autowired
	public void setValidationQuery(@Value("${database.validationQuery}") String validationQuery) {
		this.validationQuery = validationQuery;
	}

	@Autowired
	public void setHibernateDialect(@Value("${hibernate.dialect}") String hibernateDialect) {
		this.hibernateDialect = hibernateDialect;
	}

	@Autowired
	public void setQuartzConfig(@Value("${quartz.config}") String quartzConfig) {
		this.quartzConfig = quartzConfig;
	}

	@Autowired
	public void setRedisMaster(@Value("${spring.redis.sentinel.master:}") String redisMaster) {
		this.redisMaster = redisMaster;
	}

	@Autowired
	public void setRedisNodes(@Value("${spring.redis.sentinel.nodes:}") String redisNodes) {
		this.redisNodes = redisNodes;
	}

	@Autowired
	public void setEmailHost(@Value("${email.host}") String emailHost) {
		this.emailHost = emailHost;
	}

	@Autowired
	public void setEmailPort(@Value("${email.port}") String emailPort) {
		this.emailPort = emailPort;
	}

	@Autowired
	public void setEmailUser(@Value("${email.user}") String emailUser) {
		this.emailUser = emailUser;
	}

	@Autowired
	public void setEmailFrom(@Value("${email.from}") String emailFrom) {
		this.emailFrom = emailFrom;
	}

	@Autowired
	public void setEmailName(@Value("${email.name}") String emailName) {
		this.emailName = emailName;
	}

	@Autowired
	public void setEmailAuth(@Value("${email.auth}") String emailAuth) {
		this.emailAuth = emailAuth;
	}

	@Autowired
	public void setEmailSocket(@Value("${email.socketFactory}") String emailSocket) {
		this.emailSocket = emailSocket;
	}

	@Autowired
	public void setEmailTimeout(@Value("${email.timeout}") String emailTimeout) {
		this.emailTimeout = emailTimeout;
	}

	@Autowired
	public void setServiceEnabled(@Value("${sms.amazon.service.enable}") String serviceEnabled) {
		this.serviceEnabled = serviceEnabled;
	}

	@Autowired
	public void setProfilesConfigFile(@Value("${sms.amazon.profilesConfigFile}") String profilesConfigFile) {
		this.profilesConfigFile = profilesConfigFile;
	}

	@Autowired
	public void setProfileName(@Value("${sms.amazon.profileName}") String profileName) {
		this.profileName = profileName;
	}

	@Autowired
	public void setDisableCertChecking(@Value("${sms.amazon.disableCertChecking}") String disableCertChecking) {
		this.disableCertChecking = disableCertChecking;
	}

	@Autowired
	public void setLdapEnabled(@Value("${tms.ldap.enabled}") String ldapEnabled) {
		this.ldapEnabled = ldapEnabled;
	}

	@Autowired
	public void setLdapUrl(@Value("${tms.ldap.url}") String ldapUrl) {
		this.ldapUrl = ldapUrl;
	}

	@Autowired
	public void setLdapBaseDN(@Value("${tms.ldap.baseDn}") String ldapBaseDN) {
		this.ldapBaseDN = ldapBaseDN;
	}

	@Autowired
	public void setSearchFilter(@Value("${tms.ldap.searchFilter}") String searchFilter) {
		this.searchFilter = searchFilter;
	}

	@Autowired
	public void setManagerDN(@Value("${tms.ldap.managerDn}") String managerDN) {
		this.managerDN = managerDN;
	}

	@Autowired
	public void setAllowMultipleDN(@Value("${tms.ldap.allowMultipleDns}") String allowMultipleDN) {
		this.allowMultipleDN = allowMultipleDN;
	}

	@Autowired
	public void setSubtreeSearch(@Value("${tms.ldap.subtreeSearch}") String subtreeSearch) {
		this.subtreeSearch = subtreeSearch;
	}

	@Autowired
	public void setLdapSSL(@Value("${tms.ldap.use.ssl}") String ldapSSL) {
		this.ldapSSL = ldapSSL;
	}

	@Autowired
	public void setUseStartTLS(@Value("${tms.ldap.useStartTLS}") String useStartTLS) {
		this.useStartTLS = useStartTLS;
	}

	@Autowired
	public void setWarnAll(@Value("${tms.ldap.password.policy.warnAll}") String warnAll) {
		this.warnAll = warnAll;
	}

	@Autowired
	public void setWarningDays(@Value("${tms.ldap.password.policy.warningDays}") String warningDays) {
		this.warningDays = warningDays;
	}

	@Autowired
	public void setLdapPwdPolucyUrl(@Value("${tms.ldap.password.policy.url}") String ldapPwdPolucyUrl) {
		this.ldapPwdPolucyUrl = ldapPwdPolucyUrl;
	}

	@Autowired
	public void setLockUserAccountAfterFailedLoginTimes(
			@Value("${tms.user.lockUserAccountAfterFailedLoginTimes}") String lockUserAccountAfterFailedLoginTimes) {
		this.lockUserAccountAfterFailedLoginTimes = lockUserAccountAfterFailedLoginTimes;
	}

	@Autowired
	public void setInactiveUserLockDays(@Value("${tms.user.inactiveUser.lock.days}") String inactiveUserLockDays) {
		this.inactiveUserLockDays = inactiveUserLockDays;
	}

	@Autowired
	public void setInactiveUserLockWarningDays(
			@Value("${tms.user.inactiveUser.lock.warningDays}") String inactiveUserLockWarningDays) {
		this.inactiveUserLockWarningDays = inactiveUserLockWarningDays;
	}

	@Autowired
	public void setInactiveUserDeleteDays(
			@Value("${tms.user.inactiveUser.delete.days}") String inactiveUserDeleteDays) {
		this.inactiveUserDeleteDays = inactiveUserDeleteDays;
	}

	@Autowired
	public void setPasswordExpirationDays(@Value("${tms.user.passwordExpiration.days}") String passwordExpirationDays) {
		this.passwordExpirationDays = passwordExpirationDays;
	}

	@Autowired
	public void setPasswordExpirationWarningDays(
			@Value("${tms.user.passwordExpiration.warningDays}") String passwordExpirationWarningDays) {
		this.passwordExpirationWarningDays = passwordExpirationWarningDays;
	}
	
	@Autowired
	public void setOldPasswordPreserveNumber(
			@Value("${tms.user.oldPassword.preserveNumber}") String oldPasswordPreserveNumber) {
		this.oldPasswordPreserveNumber = oldPasswordPreserveNumber;
	}

	@Autowired
	public void setAuditLogRetentionTime(@Value("${tms.auditLogRetentionTime}") String auditLogRetentionTime) {
		this.auditLogRetentionTime = auditLogRetentionTime;
	}

	@Autowired
	public void setUserLogRetentionTime(@Value("${tms.userLogRetentionTime}") String userLogRetentionTime) {
		this.userLogRetentionTime = userLogRetentionTime;
	}

	@Autowired
	public void setLogFileRetentionTime(@Value("${tms.logFileRetentionTime}") String logFileRetentionTime) {
		this.logFileRetentionTime = logFileRetentionTime;
	}

	@Autowired
	public void setLogFileDirs(@Value("${tms.logFileDirs}") String logFileDirs) {
		this.logFileDirs = logFileDirs;
	}

	@Autowired
	public void setTerminalEventLogRetentionTime(
			@Value("${tms.terminalEventLogRetentionTime}") String terminalEventLogRetentionTime) {
		this.terminalEventLogRetentionTime = terminalEventLogRetentionTime;
	}

	@Autowired
	public void setGroupEventLogRetentionTime(
			@Value("${tms.groupEventLogRetentionTime}") String groupEventLogRetentionTime) {
		this.groupEventLogRetentionTime = groupEventLogRetentionTime;
	}

	@Autowired
	public void setTerminalUsageDataRetentionTime(
			@Value("${tms.terminalUsageDataRetentionTime}") String terminalUsageDataRetentionTime) {
		this.terminalUsageDataRetentionTime = terminalUsageDataRetentionTime;
	}

	@Autowired
	public void setUnusedPackageRetentionTime(
			@Value("${tms.unusedPackageRetentionTime}") String unusedPackageRetentionTime) {
		this.unusedPackageRetentionTime = unusedPackageRetentionTime;
	}

	public String getCasServerPrefix() {
		return casServerPrefix;
	}

	public String getTmsServerPrefix() {
		return tmsServerPrefix;
	}

	public String getServerIp() {
		return serverIp;
	}

	public String getLoginCallbackURL() {
		return loginCallbackURL;
	}

	public String getHttpPort() {
		return httpPort;
	}

	public String getMaxSimultaneousDownloads() {
		return maxSimultaneousDownloads;
	}

	public String getDownloadRetryAttempts() {
		return downloadRetryAttempts;
	}

	public String getSyncToServerTime() {
		return SyncToServerTime;
	}

	public String getChsDelay() {
		return chsDelay;
	}

	public String getHmsDelay() {
		return hmsDelay;
	}

	public String getAdsDelay() {
		return adsDelay;
	}

	public String getMaxHeartBeatInterval() {
		return maxHeartBeatInterval;
	}

	public String getDbUser() {
		return dbUser;
	}

	public String getDbUrl() {
		return dbUrl;
	}

	public String getDbDriverClass() {
		return dbDriverClass;
	}

	public String getValidationQuery() {
		return validationQuery;
	}

	public String getHibernateDialect() {
		return hibernateDialect;
	}

	public String getQuartzConfig() {
		return quartzConfig;
	}

	public String getRedisMaster() {
		return redisMaster;
	}

	public String getRedisNodes() {
		return redisNodes;
	}

	public String getEmailHost() {
		return emailHost;
	}

	public String getEmailPort() {
		return emailPort;
	}

	public String getEmailUser() {
		return emailUser;
	}

	public String getEmailFrom() {
		return emailFrom;
	}

	public String getEmailName() {
		return emailName;
	}

	public String getEmailAuth() {
		return emailAuth;
	}

	public String getEmailSocket() {
		return emailSocket;
	}

	public String getEmailTimeout() {
		return emailTimeout;
	}

	public String getServiceEnabled() {
		return serviceEnabled;
	}

	public String getProfilesConfigFile() {
		return profilesConfigFile;
	}

	public String getProfileName() {
		return profileName;
	}

	public String getDisableCertChecking() {
		return disableCertChecking;
	}

	public String getLdapEnabled() {
		return ldapEnabled;
	}

	public String getLdapUrl() {
		return ldapUrl;
	}

	public String getLdapBaseDN() {
		return ldapBaseDN;
	}

	public String getSearchFilter() {
		return searchFilter;
	}

	public String getManagerDN() {
		return managerDN;
	}

	public String getAllowMultipleDN() {
		return allowMultipleDN;
	}

	public String getSubtreeSearch() {
		return subtreeSearch;
	}

	public String getLdapSSL() {
		return ldapSSL;
	}

	public String getUseStartTLS() {
		return useStartTLS;
	}

	public String getWarnAll() {
		return warnAll;
	}

	public String getWarningDays() {
		return warningDays;
	}

	public String getLdapPwdPolucyUrl() {
		return ldapPwdPolucyUrl;
	}

	public String getLockUserAccountAfterFailedLoginTimes() {
		return lockUserAccountAfterFailedLoginTimes;
	}

	public String getInactiveUserLockDays() {
		return inactiveUserLockDays;
	}

	public String getInactiveUserLockWarningDays() {
		return inactiveUserLockWarningDays;
	}

	public String getInactiveUserDeleteDays() {
		return inactiveUserDeleteDays;
	}

	public String getPasswordExpirationDays() {
		return passwordExpirationDays;
	}

	public String getPasswordExpirationWarningDays() {
		return passwordExpirationWarningDays;
	}
	
	public String getOldPasswordPreserveNumber(){
		return oldPasswordPreserveNumber;
	}

	public String getAuditLogRetentionTime() {
		return auditLogRetentionTime;
	}

	public String getUserLogRetentionTime() {
		return userLogRetentionTime;
	}

	public String getLogFileRetentionTime() {
		return logFileRetentionTime;
	}

	public String getLogFileDirs() {
		return logFileDirs;
	}

	public String getTerminalEventLogRetentionTime() {
		return terminalEventLogRetentionTime;
	}

	public String getGroupEventLogRetentionTime() {
		return groupEventLogRetentionTime;
	}

	public String getTerminalUsageDataRetentionTime() {
		return terminalUsageDataRetentionTime;
	}

	public String getUnusedPackageRetentionTime() {
		return unusedPackageRetentionTime;
	}

	public long getTerminalTimeSynInterval() {
		return terminalTimeSynInterval;
	}

	@Autowired
	public void setTerminalTimeSynInterval(
			@Value("${pxretailer.terminalTimeSynInterval:7200000}") long terminalTimeSynInterval) {
		this.terminalTimeSynInterval = terminalTimeSynInterval;
	}

    public String getTerminalLogPPM() {
        return terminalLogPPM;
    }
    @Autowired
    public void setTerminalLogPPM(@Value("${tms.terminal.log.ppm}") String terminalLogPPM) {
        this.terminalLogPPM = terminalLogPPM;
    }

    public String getTerminalLogSplunk() {
        return terminalLogSplunk;
    }
    @Autowired
    public void setTerminalLogSplunk(@Value("${tms.terminal.log.splunk}") String terminalLogSplunk) {
        this.terminalLogSplunk = terminalLogSplunk;
    }

    public String getTerminalLogSplunkUrl() {
        return terminalLogSplunkUrl;
    }
    @Autowired
    public void setTerminalLogSplunkUrl(@Value("${tms.terminal.log.splunk.url}") String terminalLogSplunkUrl) {
        this.terminalLogSplunkUrl = terminalLogSplunkUrl;
    }

    public String getTerminalLogSplunkToken() {
        return terminalLogSplunkToken;
    }
    
    @Autowired
    public void setTerminalLogSplunkToken(@Value("${tms.terminal.log.splunk.token}") String terminalLogSplunkToken) {
        this.terminalLogSplunkToken = terminalLogSplunkToken;
    }

	
}
