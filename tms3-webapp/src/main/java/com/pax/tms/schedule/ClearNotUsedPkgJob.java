package com.pax.tms.schedule;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.net.URL;
import java.text.SimpleDateFormat;
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

import com.pax.tms.res.service.PkgUsageInfoService;

public class ClearNotUsedPkgJob extends QuartzJobBean implements Serializable {

	private static final long serialVersionUID = 5463403903794682313L;
	public static final String SYS_PROPERTY = "classpath:tms.properties";
	private static final Logger LOGGER = LoggerFactory.getLogger(ClearNotUsedPkgJob.class);

	@Autowired
	private PkgUsageInfoService pkgUsageInfoService;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

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

			String age = prop.getProperty("tms.unusedPackageRetentionTime");
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

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			long startTime = System.currentTimeMillis();
			try {
				LOGGER.info("Clear not used package start");
				pkgUsageInfoService.updateUsageInfo();
				pkgUsageInfoService.clearPkgAndUsageInfo(when);
				LOGGER.info("Clear not used package  completed, spend time: {} second(s), start time: {}",
						((System.currentTimeMillis() - startTime) + 999) / 1000,
						dateFormat.format(new Date(startTime)));
			} catch (Exception e) {
				LOGGER.info("Clear not used package failed, spend time: {} second(s), start time: {}",
						((System.currentTimeMillis() - startTime) + 999) / 1000,
						dateFormat.format(new Date(startTime)));
				LOGGER.error("Failed to clear not used package ", e);
			}

		} catch (Exception e) {
			LOGGER.error("[Clean] system clean failed", e);
		}

	}

}
