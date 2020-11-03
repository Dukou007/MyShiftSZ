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
package com.pax.common.web.listener;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang3.StringUtils;

public class WebContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		if (StringUtils.equalsIgnoreCase("true",
				sce.getServletContext().getInitParameter("isJavaElMethodInvocationsEnabled"))) {
			System.setProperty("javax.el.methodInvocations", "true");
		}
		if (StringUtils.equalsIgnoreCase("true",
				sce.getServletContext().getInitParameter("isHttpsHostnameVerifierDisabled"))) {
			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
				@Override
				public boolean verify(String arg0, SSLSession arg1) {
					return true;
				}
			});
		}

		if (StringUtils.isEmpty(System.getProperty("TMS_HOME"))) {
			System.setProperty("TMS_HOME", (System.getProperty("user.dir")));
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// do nothing
	}

}
