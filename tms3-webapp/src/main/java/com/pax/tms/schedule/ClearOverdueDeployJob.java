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

public class ClearOverdueDeployJob extends QuartzJobBean implements Serializable {

	private static final long serialVersionUID = 5836262064853363417L;

	private static final Logger LOGGER = LoggerFactory.getLogger(ClearOverdueDeployJob.class);

	@Autowired
	private GroupDeployService groupDeployService;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

		Date when = new Date();

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		long startTime = System.currentTimeMillis();
		try {
			LOGGER.info("Clear overdue deployment start");
			groupDeployService.deleteOverDueDeployment(when);
			LOGGER.info("Clear overdue deployment  completed, spend time: {} second(s), start time: {}",
					((System.currentTimeMillis() - startTime) + 999) / 1000, dateFormat.format(new Date(startTime)));
		} catch (Exception e) {
			LOGGER.info("Clear overdue deployment failed, spend time: {} second(s), start time: {}",
					((System.currentTimeMillis() - startTime) + 999) / 1000, dateFormat.format(new Date(startTime)));
			LOGGER.error("Failed to clear overdue deployment ", e);
		}

	}

}
