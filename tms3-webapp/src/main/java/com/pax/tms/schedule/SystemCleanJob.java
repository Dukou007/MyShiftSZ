package com.pax.tms.schedule;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.util.ResourceUtils;

import com.pax.tms.monitor.service.AlertProcessService;
import com.pax.tms.monitor.service.EventGrpService;
import com.pax.tms.monitor.service.EventTrmService;
import com.pax.tms.user.service.AuditLogService;
import com.pax.tms.user.service.UserLogService;

public class SystemCleanJob extends QuartzJobBean implements Serializable {

	private static final long serialVersionUID = 5444518353194373268L;

	public static final String SYS_PROPERTY = "classpath:tms.properties";

	@Autowired
	private AuditLogService auditLogService;

	@Autowired
	private UserLogService userLogService;

	@Autowired
	private EventTrmService eventTrmLogService;

	@Autowired
	private EventGrpService eventGrpLogService;

	@Autowired
	private AlertProcessService alertProcessService;

	private static volatile boolean running = false;
	private static Logger logger = LoggerFactory.getLogger(SystemCleanJob.class);

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		if (running) {
			return;
		}
		running = true;
		try {
			Properties prop = new Properties();
			URL url = ResourceUtils.getURL(SYS_PROPERTY);
			if (url != null) {
				FileInputStream input = new FileInputStream(new File(url.toURI()));
				prop.load(input);
				input.close();
			} else {
				throw new Exception("system properties file not found. file: " + SYS_PROPERTY);
			}
			new Thread(new CleanOperationAuditLogTask(prop)).run();

			new Thread(new CleanOperationUserLogTask(prop)).run();

			new Thread(new CleanOperationEventTrmLogTask(prop)).run();

			new Thread(new CleanOperationEventGrpLogTask(prop)).run();

			new Thread(new CleanOperationUsageDataTask(prop)).run();

			new Thread(new CleanLogFileTask(prop)).run();

		} catch (Exception e) {
			logger.error("[Clean] system clean failed", e);
		} finally {
			running = false;
		}
	}

	public class CleanOperationAuditLogTask implements Runnable {
		private Properties prop;

		public CleanOperationAuditLogTask(Properties prop) {
			this.prop = prop;
		}

		@Override
		public void run() {
			if (auditLogService == null) {
				return;
			}

			String age = prop.getProperty("tms.auditLogRetentionTime");
			if (StringUtils.isEmpty(age)) {
				return;
			}
			int month = Integer.parseInt(age);
			if (month <= 0) {
				return;
			}
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, -month);
			Date when = cal.getTime();

			logger.info("[Clean] start to clean operation audit logs.");
			try {
				auditLogService.systemClear(when);
				logger.info("[Clean] finish to clean operation audit logs.");
			} catch (Exception e) {
				logger.error("[Clean] failed to clean operation audit logs.", e);
			}
		}
	}

	public class CleanOperationUserLogTask implements Runnable {
		private Properties prop;

		public CleanOperationUserLogTask(Properties prop) {
			this.prop = prop;
		}

		@Override
		public void run() {
			if (userLogService == null) {
				return;
			}

			String age = prop.getProperty("tms.userLogRetentionTime");
			if (StringUtils.isEmpty(age)) {
				return;
			}
			int month = Integer.parseInt(age);
			if (month <= 0) {
				return;
			}
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, -month);
			Date when = cal.getTime();

			logger.info("[Clean] start to clean operation user logs.");
			try {
				userLogService.systemClearUserLog(when);
				logger.info("[Clean] finish to clean operation user logs.");
			} catch (Exception e) {
				logger.error("[Clean] failed to clean operation user logs.", e);
			}
		}
	}

	public class CleanOperationEventTrmLogTask implements Runnable {
		private Properties prop;

		public CleanOperationEventTrmLogTask(Properties prop) {
			this.prop = prop;
		}

		@Override
		public void run() {
			if (eventTrmLogService == null) {
				return;
			}

			String age = prop.getProperty("tms.terminalEventLogRetentionTime");
			if (StringUtils.isEmpty(age)) {
				return;
			}
			int month = Integer.parseInt(age);
			if (month <= 0) {
				return;
			}
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, -month);
			Date when = cal.getTime();

			logger.info("[Clean] start to clean operation eventTerminal logs.");
			try {
				eventTrmLogService.deleteEventTrm(when);
				logger.info("[Clean] finish to clean operation eventTerminal logs.");
			} catch (Exception e) {
				logger.error("[Clean] failed to clean operation eventTerminal logs.", e);
			}
		}
	}

	public class CleanOperationEventGrpLogTask implements Runnable {
		private Properties prop;

		public CleanOperationEventGrpLogTask(Properties prop) {
			this.prop = prop;
		}

		@Override
		public void run() {
			if (eventGrpLogService == null) {
				return;
			}

			String age = prop.getProperty("tms.groupEventLogRetentionTime");
			if (StringUtils.isEmpty(age)) {
				return;
			}
			int month = Integer.parseInt(age);
			if (month <= 0) {
				return;
			}
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, -month);
			Date when = cal.getTime();

			logger.info("[Clean] start to clean operation eventGroup logs.");
			try {
				eventGrpLogService.deleteEventGrp(when);
				logger.info("[Clean] finish to clean operation eventGroup logs.");
			} catch (Exception e) {
				logger.error("[Clean] failed to clean operation eventGroup logs.", e);
			}
		}
	}

	public class CleanOperationUsageDataTask implements Runnable {
		private Properties prop;

		public CleanOperationUsageDataTask(Properties prop) {
			this.prop = prop;
		}

		@Override
		public void run() {
			if (eventGrpLogService == null) {
				return;
			}

			String age = prop.getProperty("tms.terminalUsageDataRetentionTime");
			if (StringUtils.isEmpty(age)) {
				return;
			}
			int month = Integer.parseInt(age);
			if (month <= 0) {
				return;
			}
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, -month);
			Date when = cal.getTime();

			logger.info("[Clean] start to clean usage data");
			try {
				alertProcessService.deleteUsageData(when);
				logger.info("[Clean] finish to clean usage data.");
			} catch (Exception e) {
				logger.error("[Clean] failed to clean usage data.", e);
			}
		}
	}

	public class CleanLogFileTask implements Runnable {
		private Properties prop;

		public CleanLogFileTask(Properties prop) {
			this.prop = prop;
		}

		@Override
		public void run() {
			String age = prop.getProperty("system.clean.logFileAge");
			if (StringUtils.isEmpty(age)) {
				return;
			}
			int month = Integer.parseInt(age);
			if (month <= 0) {
				return;
			}
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, -month);
			Date when = cal.getTime();

			String dirs = prop.getProperty("tms.logFileDirs");
			if (StringUtils.isEmpty(dirs)) {
				return;
			}

			logger.info("[Clean] start to clean log files.");
			try {
				String[] dirArray = dirs.split(",");
				for (String path : dirArray) {
					File dir = new File(path);
					if (!dir.exists()) {
						return;
					}
					if (!dir.isDirectory()) {
						return;
					}
					cleanDirectory(dir, when);
				}
				logger.info("[Clean] finish to clean log files.");
			} catch (Exception e) {
				logger.info("[Clean] failed to clean log files.", e);
			}
		}
	}

	public static void cleanDirectory(File directory, Date age) {
		File[] files = directory.listFiles();

		if (files == null) {
			return;
		}
		for (File file : files) {
			try {
				cleanFile(file, age);
			} catch (Exception ioe) {
				logger.warn("Clean log file failed - " + file.getPath(), ioe);
			}
		}
	}

	public static void cleanFile(File file, Date age) {
		if (file == null) {
			return;
		}
		if (!file.exists()) {
			return;
		}

		long lastModified = file.lastModified();
		if (lastModified == 0) {
			return;
		}
		if (new Date(lastModified).after(age)) {
			return;
		}

		if (file.isFile()) {
			try {
				file.delete();
			} catch (Exception ignored) {
				logger.warn("Delete log file failed - " + file.getPath(), ignored);
			}
		} else if (file.isDirectory()) {
			cleanDirectory(file, age);
			File[] files = file.listFiles();
			if (files == null || files.length == 0) {
				try {
					file.delete();
				} catch (Exception ignored) {
					logger.warn("Delete log file failed - " + file.getPath(), ignored);
				}
			}
		}
	}

}
