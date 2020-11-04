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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pax.common.dao.IBaseDao;
import com.pax.common.service.impl.BaseService;
import com.pax.tms.user.dao.UserPrivilegeDao;
import com.pax.tms.user.model.Authority;

@Service("userPrivilgeServiceImpl")
public class UserPrivilegeServiceImpl extends BaseService<Authority, Long> implements UserPrivilegeService {

	@Autowired
	private UserPrivilegeDao userPrivilegeDao;

	@Override
	public IBaseDao<Authority, Long> getBaseDao() {
		return userPrivilegeDao;
	}

	@Override
	public List<Authority> getAssignedPrivileges(long userId) {
		return userPrivilegeDao.getAssignedPrivileges(userId);
	}

	@Override
	public List<Long> getAssignedPrivilegeIds(long userId) {
		return userPrivilegeDao.getAssignedPrivilegeIds(userId);
	}

	@Override
	public List<String> getAssignedPrivilegeCodes(long userId) {
		return userPrivilegeDao.getAssignedPrivilegeCodes(userId);
	}

	@Override
	public boolean hasPermissionOfGroup(long operatorId, long groupId) {
		return userPrivilegeDao.hasPermissionOfGroup(operatorId, groupId);
	}

	@Override
	public boolean hasPermissionOfUser(long operatorId, long userId) {
		return userPrivilegeDao.hasPermissionOfUser(operatorId, userId);
	}

	@Override
	public boolean hasPermissionOfTerminal(long operatorId, String tid) {
		return userPrivilegeDao.hasPermissionOfTerminal(operatorId, tid);
	}

	@Override
	public boolean hasPermissionOfUsage(long operatorId, long uid) {
		return userPrivilegeDao.hasPermissionOfUsage(operatorId, uid);
	}

	@Override
	public boolean hasPermissionOfPkg(Long loginUserId, Long pkgId) {

		return userPrivilegeDao.hasPermissionOfPkg(loginUserId, pkgId);
	}

	@Override
	public boolean hasPermissionOfPkgByGroup(Long groupId, Long pkgId) {

		return userPrivilegeDao.hasPermissionOfPkgByGroup(groupId, pkgId);
	}

	@Override
	public boolean hasPermissionOfTerminalByGroup(Long groupId, String tid) {
		return userPrivilegeDao.hasPermissionOfTerminalByGroup(groupId, tid);
	}
	
	@Override
    public boolean hasPermissionOfImportFileByGroup(Long groupId, Long fileId) {
        return userPrivilegeDao.hasPermissionOfImportFileByGroup(groupId, fileId);
    }
	
}
