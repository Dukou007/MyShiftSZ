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
package com.pax.tms.cas.login;

import javax.servlet.http.HttpServletRequest;

import org.jasig.cas.authentication.Credential;
import org.jasig.cas.web.flow.AuthenticationViaFormAction;
import org.jasig.cas.web.support.WebUtils;
import org.springframework.binding.message.MessageContext;
import org.springframework.stereotype.Component;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import com.pax.common.web.HttpUtils;

@Component("tmsUserAuthenticationViaFormAction")
public class TmsUserAuthenticationViaFormAction extends AuthenticationViaFormAction {

	@Override
	protected Event createTicketGrantingTicket(RequestContext context, Credential credential,
			MessageContext messageContext) {
		setIp(context, credential);
		return super.createTicketGrantingTicket(context, credential, messageContext);
	}

	@Override
	protected Event grantServiceTicket(RequestContext context, Credential credential) {
		setIp(context, credential);
		return super.grantServiceTicket(context, credential);
	}

	private void setIp(RequestContext context, Credential credential) {
		if (credential instanceof TmsUserCredential) {
			TmsUserCredential tmsUserCredential = (TmsUserCredential) credential;
			HttpServletRequest request = WebUtils.getHttpServletRequest(context);
			tmsUserCredential.setIp(HttpUtils.getRemoteAddr(request));
		}
	}

}
