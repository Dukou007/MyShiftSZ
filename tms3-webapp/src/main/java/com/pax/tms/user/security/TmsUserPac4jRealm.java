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

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.pac4j.core.profile.CommonProfile;
import org.springframework.beans.factory.annotation.Autowired;

import com.pax.login.TmsPac4jPrincipal;
import com.pax.tms.user.model.User;
import com.pax.tms.user.service.UserPrivilegeService;
import com.pax.tms.user.service.UserRoleService;
import com.pax.tms.user.service.UserService;

import io.buji.pac4j.realm.Pac4jRealm;
import io.buji.pac4j.token.Pac4jToken;

public class TmsUserPac4jRealm extends Pac4jRealm {

	private static final String USER_ID = "userId";
	private static final String USER_ROLES = "userRoles";
	private static final String USER_PROFILE_LOADED = "userProfileLoaded";
	private static final String IS_LDAP = "isLdap";
	private static final String DISPLAY_NAME = "displayName";
	private static final String LOGIN_NAME = "loginUsername";

	@Autowired
	private UserPrivilegeService userPrivilegeService;

	@Autowired
	private UserRoleService userRoleService;

	@Autowired
	private UserService userService;

	private UserSessionHandler userSessionHandler = UserSessionHandler.getInstance();

	public TmsUserPac4jRealm() {
		super();
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(final AuthenticationToken authenticationToken) {

		final Pac4jToken token = (Pac4jToken) authenticationToken;
		final LinkedHashMap<String, CommonProfile> profiles = token.getProfiles();
		final TmsPac4jPrincipal principal = new TmsPac4jPrincipal(profiles);

		CommonProfile profile = principal.getProfile();
		if (profile != null) {
			profile = loadTmsUserProfile(profile);
			if (profile == null) {
				return null;
			}
		}

		final PrincipalCollection principalCollection = new SimplePrincipalCollection(principal, getName());
		return new SimpleAuthenticationInfo(principalCollection, profiles.hashCode());
	}

	private CommonProfile loadTmsUserProfile(CommonProfile profile) {
		if ("1".equals(profile.getAttribute(USER_PROFILE_LOADED))) {
			return profile;
		}
		String username = profile.getId();
		User user = userService.findByUsername(username);
		if (user == null) {
			return null;
		}

		profile.removeAttribute(USER_ID);
		profile.addAttribute(USER_ID, user.getId());
		profile.addAttribute(IS_LDAP, user.isLdap());
		profile.addAttribute(DISPLAY_NAME, null == user.getFullname()?""+" ("+user.getUsername()+")":user.getFullname()+" ("+user.getUsername()+")");
		profile.addAttribute(LOGIN_NAME, user.getUsername());
		
		List<String> assignedRoleNames = userRoleService.getAssignedTMSRoleNames(user.getId());
		if (assignedRoleNames != null) {
			profile.addRoles(assignedRoleNames);
		}

		Collection<String> roles = profile.getRoles();
		profile.addAttribute(USER_ROLES, roles == null ? null : StringUtils.join(roles, ","));

		List<String> assignedPvgCodes = userPrivilegeService.getAssignedPrivilegeCodes(user.getId());
		if (assignedPvgCodes != null) {
			profile.addPermissions(assignedPvgCodes);
		}

		profile.addAttribute(USER_PROFILE_LOADED, "1");

		if (userSessionHandler != null) {
			Subject subject = SecurityUtils.getSubject();
			Session session = subject.getSession(false);
			if (session instanceof HttpSession) {
				userSessionHandler.login(user.getId().toString(), (HttpSession) session);
			}
		}
		return profile;
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(final PrincipalCollection principals) {
		final Set<String> roles = new HashSet<>();
		final Set<String> permissions = new HashSet<>();
		final TmsPac4jPrincipal principal = principals.oneByType(TmsPac4jPrincipal.class);
		if (principal != null) {
			final List<CommonProfile> profiles = principal.getProfiles();
			for (CommonProfile profile : profiles) {
				if (profile != null) {
					roles.addAll(profile.getRoles());
					permissions.addAll(profile.getPermissions());
				}
			}
		}

		final SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
		simpleAuthorizationInfo.addRoles(roles);
		simpleAuthorizationInfo.addStringPermissions(permissions);
		return simpleAuthorizationInfo;
	}

	public UserPrivilegeService getUserPrivilegeService() {
		return userPrivilegeService;
	}

	public void setUserPrivilegeService(UserPrivilegeService userPrivilegeService) {
		this.userPrivilegeService = userPrivilegeService;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public UserRoleService getUserRoleService() {
		return userRoleService;
	}

	public void setUserRoleService(UserRoleService userRoleService) {
		this.userRoleService = userRoleService;
	}

}
