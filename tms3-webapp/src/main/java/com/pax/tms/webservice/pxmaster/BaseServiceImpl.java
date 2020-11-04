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
package com.pax.tms.webservice.pxmaster;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.pac4j.core.profile.CommonProfile;
import org.springframework.beans.factory.annotation.Autowired;

import com.pax.common.web.form.BaseForm;
import com.pax.tms.user.model.User;
import com.pax.tms.user.service.UserRoleService;
import com.pax.tms.user.service.UserService;
import com.pax.tms.webservice.pxmaster.form.PubUser;

public class BaseServiceImpl {

	public static final String USER_ID = "userId";
	public static final String USER_ROLES = "userRoles";

	@Autowired
	protected UserRoleService userRoleService;

	@Autowired
	protected UserService userService;

	// private GroupDeployService groupDeployService;
	// private TerminalDeployService terminalDeployService;

	protected CommonProfile loadTmsUserProfile(PubUser pu) {
		return loadTmsUserProfile(pu.getId());
	}

	protected CommonProfile loadTmsUserProfile(Long userId) {
		CommonProfile profile = new CommonProfile();
		User user = userService.get(userId);
		if (user == null) {
			return null;
		}

		profile.addAttribute(USER_ID, user.getId());
		profile.setId(user.getUsername());

		List<String> assignedRoleNames = userRoleService.getAssignedTMSRoleNames(user.getId());
		if (assignedRoleNames != null) {
			profile.addRoles(assignedRoleNames);
		}

		Collection<String> roles = profile.getRoles();
		profile.addAttribute(USER_ROLES, roles == null ? null : StringUtils.join(roles, ","));
		return profile;
	}

	protected int getPageStart(int totalCount, int pageNumber, int pageSize) {
		int start = (pageNumber - 1) * pageSize;
		if (start >= totalCount) {
			start = 0;
		}
		return start;
	}

	protected void setTmsUserProfile(BaseForm form, CommonProfile userProfile) {
		form.setLoginUserProfile(userProfile);
	}

	public UserRoleService getUserRoleService() {
		return userRoleService;
	}

	public void setUserRoleService(UserRoleService userRoleService) {
		this.userRoleService = userRoleService;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

}
