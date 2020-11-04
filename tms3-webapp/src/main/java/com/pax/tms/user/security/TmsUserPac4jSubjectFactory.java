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
package com.pax.tms.user.security;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;
import org.apache.shiro.web.subject.WebSubjectContext;
import org.apache.shiro.web.subject.support.WebDelegatingSubject;

import com.pax.common.web.HttpUtils;

import io.buji.pac4j.token.Pac4jToken;

public class TmsUserPac4jSubjectFactory extends DefaultWebSubjectFactory {

	@Override
	public Subject createSubject(SubjectContext context) {
		if (!(context instanceof WebSubjectContext)) {
			return super.createSubject(context);
		}
		WebSubjectContext wsc = (WebSubjectContext) context;
		SecurityManager securityManager = wsc.resolveSecurityManager();
		Session session = wsc.resolveSession();
		boolean sessionEnabled = wsc.isSessionCreationEnabled();
		PrincipalCollection principals = wsc.resolvePrincipals();
		boolean authenticated = wsc.resolveAuthenticated();
		ServletRequest request = wsc.resolveServletRequest();
		ServletResponse response = wsc.resolveServletResponse();
		String host = HttpUtils.getRemoteAddr((HttpServletRequest) request);

		return new WebDelegatingSubject(principals, authenticated, host, session, sessionEnabled, request, response,
				securityManager);
	}

	protected void setAuthenticated(SubjectContext context, boolean allowRememberMe) {
		if (!context.isAuthenticated()) {
			return;
		}

		AuthenticationToken token = context.getAuthenticationToken();
		if (token != null && token instanceof Pac4jToken) {
			final Pac4jToken clientToken = (Pac4jToken) token;
			if (clientToken.isRememberMe() && !allowRememberMe) {
				context.setAuthenticated(false);
			}
		}
	}

}
