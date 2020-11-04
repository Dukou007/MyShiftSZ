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
package com.pax.common.util;

import org.springframework.context.ApplicationContext;

public class ApplicationContextUtils {

	private static ApplicationContext context;

	private ApplicationContextUtils() {
	}

	public static void setApplicationContext(ApplicationContext context) {
		ApplicationContextUtils.context = context;
	}

	public static ApplicationContext getApplicationContext() {
		return context;
	}
}
