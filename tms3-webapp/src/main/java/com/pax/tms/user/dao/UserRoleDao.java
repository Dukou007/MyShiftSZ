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
package com.pax.tms.user.dao;

import java.util.List;

import com.pax.common.dao.IBaseDao;
import com.pax.tms.user.model.Role;
import com.pax.tms.user.model.User;
import com.pax.tms.user.model.UserRole;

public interface UserRoleDao extends IBaseDao<UserRole, Long> {

	void deleteUserRoles(long userId);

	void deleteUserRole(long userRoleId);

	List<Role> getUnsignedRoles(long userId);

	List<Role> getAssignedRoles(long userId);

	List<String> getAssignedTMSRoleNames(long userId);

	List<String> getAssignedRoleIds(long userId, String appName);

	List<User> getUsersHasRole(long roleId);

	UserRole getUserRole(Long userId, Long roleId, Long siteAdminId);

}
