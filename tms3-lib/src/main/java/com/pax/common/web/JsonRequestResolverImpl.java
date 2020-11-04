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
package com.pax.common.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

public class JsonRequestResolverImpl implements JsonRequestResolver {

	@Override
	public boolean isJsonRequest(HttpServletRequest request) {
		String requestType = request.getHeader("X-Requested-With");
		if (requestType != null && requestType.equalsIgnoreCase("XMLHttpRequest")) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isSwaggerRequest(HttpServletRequest request, Object handler, HttpServletResponse response) {

		if (StringUtils.isNotEmpty(request.getHeader("app_key"))) {
			response.setStatus(500);
			return true;
		}
		return false;
	}

}
