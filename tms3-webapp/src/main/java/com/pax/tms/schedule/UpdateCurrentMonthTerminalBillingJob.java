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

import com.pax.tms.report.service.BillingService;
/**
 * 每个月月底最后一天 23:50:00统计终端计费详情 billingreport
 * @author zengpeng
 *
 */
public class UpdateCurrentMonthTerminalBillingJob extends QuartzJobBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1960282706513082841L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ClearGroupDownloadedTask.class);
	
	@Autowired
	private BillingService billingService;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		long startTime = System.currentTimeMillis();
		try {
			LOGGER.info("Update terminal billing start");
			billingService.getCountBillingListTask();
			LOGGER.info("Update terminal billing  completed, spend time: {} second(s), start time: {}",
					((System.currentTimeMillis() - startTime) + 999) / 1000, dateFormat.format(new Date(startTime)));
		} catch (Exception e) {
			billingService.sendEmailTaskTailedToSizeAdmin();
			LOGGER.info("Update terminal billing failed, spend time: {} second(s), start time: {}",
					((System.currentTimeMillis() - startTime) + 999) / 1000, dateFormat.format(new Date(startTime)));
			LOGGER.error("Failed to update terminal billing ", e);
		}

	}

}
