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
package com.pax.tms.user.service;

import java.util.List;

import com.pax.common.service.IBaseService;
import com.pax.tms.user.model.Role;
import com.pax.tms.user.model.User;
import com.pax.tms.user.model.UserRole;

public interface UserRoleService extends IBaseService<UserRole, Long> {

	void addUserRoles(User user, List<Role> roleList);

	void editUserRoles(User user, List<Role> roleList);

	void deleteUserRoles(User user);

	List<Role> getAssignedRoles(long userId);

	List<String> getAssignedRoleIds(long userId, String appName);

	List<Role> getUnsignedRoles(long userId);

	List<User> getUsersHasRole(long roleId);

	List<String> getAssignedTMSRoleNames(long userId);
}
