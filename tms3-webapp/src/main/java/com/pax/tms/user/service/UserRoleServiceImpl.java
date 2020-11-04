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

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pax.common.dao.IBaseDao;
import com.pax.common.service.impl.BaseService;
import com.pax.tms.user.dao.UserRoleDao;
import com.pax.tms.user.model.Role;
import com.pax.tms.user.model.User;
import com.pax.tms.user.model.UserRole;

@Service("userRoleServiceImpl")
public class UserRoleServiceImpl extends BaseService<UserRole, Long> implements UserRoleService {

	@Autowired
	private UserRoleDao userRoleDao;

	@Override
	public IBaseDao<UserRole, Long> getBaseDao() {
		return userRoleDao;
	}

	public void setUserRoleDao(UserRoleDao userRoleDao) {
		this.userRoleDao = userRoleDao;
	}

	@Override
	public void addUserRoles(User user, List<Role> roleList) {
		if (CollectionUtils.isEmpty(roleList)) {
			return;
		}

		for (Role role : roleList) {
			UserRole userRole = new UserRole();
			userRole.setRole(role);
			userRole.setUser(user);
			userRoleDao.save(userRole);
		}
	}

	@Override
	public void editUserRoles(User user, List<Role> roleList) {
		userRoleDao.deleteUserRoles(user.getId());
		addUserRoles(user, roleList);
	}

	@Override
	public void deleteUserRoles(User user) {
		userRoleDao.deleteUserRoles(user.getId());
	}

	@Override
	public List<Role> getUnsignedRoles(long userId) {
		return userRoleDao.getUnsignedRoles(userId);
	}

	@Override
	public List<Role> getAssignedRoles(long userId) {
		return userRoleDao.getAssignedRoles(userId);
	}

	@Override
	public List<String> getAssignedRoleIds(long userId, String appName) {
		return userRoleDao.getAssignedRoleIds(userId, appName);
	}

	@Override
	public List<User> getUsersHasRole(long roleId) {
		return userRoleDao.getUsersHasRole(roleId);
	}

	@Override
	public List<String> getAssignedTMSRoleNames(long userId) {
		return userRoleDao.getAssignedTMSRoleNames(userId);
	}

}
