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

import com.pax.tms.user.service.UserService;

public class UpdateLdapUserInfoJob extends QuartzJobBean implements Serializable {

	private static final long serialVersionUID = -8566399978885358896L;

	private static final Logger LOGGER = LoggerFactory.getLogger(UpdateLdapUserInfoJob.class);

	@Autowired
	@Qualifier("userServiceImpl")
	private UserService userService;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		long startTime = System.currentTimeMillis();
		try {
			LOGGER.info("Synchronize LDAP user information start");
			userService.updateAllLdapUsersInfo();
			LOGGER.info("Synchronize LDAP user information completed, spend time: {} second(s), start time: {}",
					((System.currentTimeMillis() - startTime) + 999) / 1000, dateFormat.format(new Date(startTime)));
		} catch (Exception e) {
			LOGGER.info("Synchronize LDAP user information failed, spend time: {} second(s), start time: {}",
					((System.currentTimeMillis() - startTime) + 999) / 1000, dateFormat.format(new Date(startTime)));
			LOGGER.error("Failed synchronize LDAP user information ", e);
		}
	}
}
