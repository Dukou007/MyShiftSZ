package com.pax.tms.schedule;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.pax.tms.deploy.service.GroupDeployService;

public class ClearGroupDownloadedTask extends QuartzJobBean implements Serializable {

	private static final long serialVersionUID = -6835714452492011782L;

	private static final Logger LOGGER = LoggerFactory.getLogger(ClearGroupDownloadedTask.class);

	@Autowired
	private GroupDeployService groupDeployService;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		long startTime = System.currentTimeMillis();
		try {
			LOGGER.info("Clear downloaded deployment start");
			groupDeployService.deleteDownloadedTask();
			LOGGER.info("Clear downloaded deployment  completed, spend time: {} second(s), start time: {}",
					((System.currentTimeMillis() - startTime) + 999) / 1000, dateFormat.format(new Date(startTime)));
		} catch (Exception e) {
			LOGGER.info("Clear downloaded deployment failed, spend time: {} second(s), start time: {}",
					((System.currentTimeMillis() - startTime) + 999) / 1000, dateFormat.format(new Date(startTime)));
			LOGGER.error("Failed to clear downloaded deployment ", e);
		}

	}

}
