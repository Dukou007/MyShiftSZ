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
package com.pax.common.schedule;

import java.lang.reflect.Method;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class QuartzJobBeanAdapter extends QuartzJobBean {

	private String targetObject;

	private String targetMethod;

	private ApplicationContext ctx;

	public QuartzJobBeanAdapter() {
		super();
	}

	public QuartzJobBeanAdapter(String targetObject, String targetMethod, ApplicationContext ctx) {
		super();
		this.targetObject = targetObject;
		this.targetMethod = targetMethod;
		this.ctx = ctx;
	}

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		try {
			Object otargetObject = ctx.getBean(targetObject);
			Method m = otargetObject.getClass().getMethod(targetMethod, new Class[] {});
			m.invoke(otargetObject, new Object[] {});
		} catch (Exception e) {
			throw new JobExecutionException(e);
		}
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.ctx = applicationContext;
	}

	public void setTargetObject(String targetObject) {
		this.targetObject = targetObject;
	}

	public void setTargetMethod(String targetMethod) {
		this.targetMethod = targetMethod;
	}

}
