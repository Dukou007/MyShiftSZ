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
package com.pax.common.web.form;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.DelegatingSubject;
import org.pac4j.core.profile.CommonProfile;

import com.pax.login.TmsPac4jPrincipal;

public class BaseForm implements IBaseForm, Serializable {

	private static final long serialVersionUID = -1027472164710183047L;

	private static final String USER_ID = "userId";

	private transient CommonProfile loginUserProfile;

	private transient Date requestTime;

	private Long groupId;

	public final CommonProfile getLoginUserPofile() {
		if (loginUserProfile == null) {
			Object principal = SecurityUtils.getSubject().getPrincipal();
			if (principal instanceof io.buji.pac4j.subject.Pac4jPrincipal) {
				loginUserProfile = ((io.buji.pac4j.subject.Pac4jPrincipal) principal).getProfile();
			} else if (principal instanceof TmsPac4jPrincipal) {
				loginUserProfile = ((TmsPac4jPrincipal) principal).getProfile();
			}
		}
		return loginUserProfile;
	}

	@Override
	public String getLoginUsername() {
		CommonProfile profile = getLoginUserPofile();
		if (profile == null) {
			return null;
		}

		Object obj = profile.getAttribute("loginUsername");
		return obj != null ? obj.toString() : profile.getId();
	}

	@Override
	public Long getLoginUserId() {
		CommonProfile profile = getLoginUserPofile();
		if (profile == null) {
			return null;
		}

		Object userId = profile.getAttribute(USER_ID);
		if (userId instanceof Long) {
			return (Long) userId;
		}

		return userId == null ? null : Long.parseLong(userId.toString());
	}

	public String getClientIp() {
		Subject subject = SecurityUtils.getSubject();
		String host = null;
		if (subject instanceof DelegatingSubject) {
			host = ((DelegatingSubject) subject).getHost();
		}
		if (host == null || host.isEmpty()) {
			CommonProfile profile = getLoginUserPofile();
			if (profile != null) {
				host = (String) profile.getAttribute("host");
			}
		}
		return host;
	}

	@Override
	public Date getRequestTime() {
		if (requestTime == null) {
			requestTime = new Date();
		}
		return requestTime;
	}

	@Override
	public Collection<String> getLoginUserRoles() {
		CommonProfile profile = getLoginUserPofile();
		return profile == null ? null : profile.getRoles();
	}

	public String getLoginUserRolesAsString() {
		Collection<String> roles = getLoginUserRoles();
		return roles == null ? null : StringUtils.join(roles, ",");
	}

	@Override
	public Collection<String> getLoginUserPermissions() {
		CommonProfile profile = getLoginUserPofile();
		return profile == null ? null : profile.getPermissions();
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public void setLoginUserProfile(CommonProfile loginUserProfile) {
		this.loginUserProfile = loginUserProfile;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
