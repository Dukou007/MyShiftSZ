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
package com.pax.tms.schedule;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.pax.tms.monitor.service.AlertProcessService;

public class ProcessUsageStatusJob extends QuartzJobBean implements Serializable {

	private static final long serialVersionUID = 1184801660414399608L;

	private static final Logger LOGGER = LoggerFactory.getLogger("com.pax.tms.monitor.schedule");

	@Autowired
	@Qualifier("alertProcessServiceImpl")
	private AlertProcessService alertProcessService;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		long startTime = System.currentTimeMillis();
		try {
			LOGGER.info("Generate terminal usage report start");
			LOGGER.debug("##### ProcessUsageStatusJob start #####");
//			alertProcessService.usageMessageByDay();
			//保存每个终端根据组设置的周期失败率阈值与终端失败率对比，保存每个终端的失败率状态
			alertProcessService.usageStatusByThreshold();
			alertProcessService.doProcessTerminalUsageStatus();
			LOGGER.debug("##### ProcessUsageStatusJob end #####");
			LOGGER.info("Generate terminal usage report completed, spend time: {} second(s), start time: {}",
					((System.currentTimeMillis() - startTime) + 999) / 1000, dateFormat.format(new Date(startTime)));
		} catch (Exception e) {
			LOGGER.info("Generate terminal usage report failed, spend time: {} second(s), start time: {}",
					((System.currentTimeMillis() - startTime) + 999) / 1000, dateFormat.format(new Date(startTime)));
			LOGGER.error("Failed to generate terminal usage report", e);
		}
	}
}
