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

import java.util.List;

import com.pax.common.exception.BusinessException;
import com.pax.tms.group.model.Group;
import com.pax.tms.group.model.UsageThreshold;
import com.pax.tms.res.model.Pkg;
import com.pax.tms.terminal.model.Terminal;
import com.pax.tms.user.model.User;
import com.pax.tms.user.service.UserPrivilegeService;

public class AclManager {

	private static UserPrivilegeService userPrivilegeService;

	private AclManager() {
	}

	public static void setUserPrivilegeService(UserPrivilegeService userPrivilegeService) {
		AclManager.userPrivilegeService = userPrivilegeService;
	}

	public static void checkPermissionOfGroup(long operatorId, Group group) {
		if (userPrivilegeService != null && !userPrivilegeService.hasPermissionOfGroup(operatorId, group.getId())) {
			throw new BusinessException("msg.user.notGrantedGroup", new String[] { group.getNamePath() });
		}
	}

	public static boolean hasPermissionOfGroup(long operatorId, long groupId) {
		if (userPrivilegeService != null) {
			return userPrivilegeService.hasPermissionOfGroup(operatorId, groupId);
		}
		return true;
	}

	public static void checkPermissionOfGroup(long operatorId, List<Group> groupList) {
		if (userPrivilegeService != null) {
			for (Group group : groupList) {
				if (!userPrivilegeService.hasPermissionOfGroup(operatorId, group.getId())) {
					throw new BusinessException("msg.user.notGrantedGroup", new String[] { group.getNamePath() });
				}
			}
		}
	}

	public static void checkPermissionOfUsage(long operatorId, List<UsageThreshold> usageList) {
		if (userPrivilegeService != null) {
			for (UsageThreshold usageThreshold : usageList) {
				if (!userPrivilegeService.hasPermissionOfUsage(operatorId, usageThreshold.getId())) {
					throw new BusinessException("msg.user.notGrantedUsage",
							new String[] { String.valueOf(usageThreshold.getId()) });
				}
			}
		}
	}

	public static void checkPermissionOfTerminal(long operatorId, Terminal terminal) {
		if (userPrivilegeService != null
				&& !userPrivilegeService.hasPermissionOfTerminal(operatorId, terminal.getTid())) {
			throw new BusinessException("msg.user.notGrantedTerminal", new String[] { terminal.getTid() });
		}
	}

	public static void checkPermissionOfUser(Long operatorId, User user) {
		if (userPrivilegeService != null && !userPrivilegeService.hasPermissionOfUser(operatorId, user.getId())) {
			throw new BusinessException("msg.user.notGrantedUser", new String[] { user.getUsername() });
		}
	}

	public static void checkPermissionOfPkg(Long loginUserId, Pkg pkg) {
		if (userPrivilegeService != null && !userPrivilegeService.hasPermissionOfPkg(loginUserId, pkg.getId())) {
			throw new BusinessException("msg.pkg.notGrantedPkg", new String[] { String.valueOf(pkg.getId()) });
		}

	}

	public static void checkPermissionOfPkgByGroup(Long groupId, Pkg pkg) {
		if (userPrivilegeService != null && !userPrivilegeService.hasPermissionOfPkgByGroup(groupId, pkg.getId())) {
			throw new BusinessException("msg.pkg.notGrantedPkg", new String[] { String.valueOf(pkg.getId()) });
		}

	}

	public static void checkPermissionOfTerminalByGroup(long groupId, Terminal terminal) {
		if (userPrivilegeService != null
				&& !userPrivilegeService.hasPermissionOfTerminalByGroup(groupId, terminal.getTid())) {
			throw new BusinessException("msg.user.notGrantedTerminal", new String[] { terminal.getTid() });
		}
	}
	
	public static boolean keyCheckPermissionOfTerminal(long operatorId, Terminal terminal) {
        if (userPrivilegeService != null
                && !userPrivilegeService.hasPermissionOfTerminal(operatorId, terminal.getTid())) {
            return false;
        }else {
            return true;
        }
    }
	
	public static boolean keyCheckPermissionOfTerminalByGroup(long groupId, Terminal terminal) {
        if (userPrivilegeService != null
                && !userPrivilegeService.hasPermissionOfTerminalByGroup(groupId, terminal.getTid())) {
            return false;
        }else {
            return true;
        }
    }
	
	public static void checkPermissionOfImportFileByGroup(long groupId,Long fileId) {
        if (userPrivilegeService != null
                && !userPrivilegeService.hasPermissionOfImportFileByGroup(groupId, fileId)) {
            throw new BusinessException("msg.user.notGrantedImportFile", new String[] { fileId+"" });
        }
    }

}