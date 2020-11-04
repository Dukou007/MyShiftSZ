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
import com.pax.tms.user.model.Authority;

public interface UserPrivilegeService extends IBaseService<Authority, Long> {

	List<Authority> getAssignedPrivileges(long userId);

	List<Long> getAssignedPrivilegeIds(long userId);

	List<String> getAssignedPrivilegeCodes(long userId);

	boolean hasPermissionOfGroup(long operatorId, long groupId);

	boolean hasPermissionOfUser(long operatorId, long userId);

	boolean hasPermissionOfTerminal(long operatorId, String tid);

	boolean hasPermissionOfUsage(long operatorId, long uid);

	boolean hasPermissionOfPkg(Long loginUserId, Long pkgId);

	boolean hasPermissionOfPkgByGroup(Long groupId, Long pkgId);

	boolean hasPermissionOfTerminalByGroup(Long groupId, String tid);

	boolean hasPermissionOfImportFileByGroup(Long groupId, Long fileId);
}
