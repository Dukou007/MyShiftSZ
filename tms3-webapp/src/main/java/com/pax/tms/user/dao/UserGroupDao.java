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
import com.pax.tms.user.model.User;
import com.pax.tms.user.model.UserGroup;

public interface UserGroupDao extends IBaseDao<UserGroup, Long> {

	void deleleUserGroups(long userId);

	void deleleUserGroup(long id);

	void deleteUserGroup(Long userId, Long groupId);

	void setFavoriteGroup(long userId, long favoriteGroupId);

	Long getFavoriteGroup(long userId);

	List<User> getAssignedGroupUsers(long groupId);

	List<User> getOnlyAssignedOneGroupUsers(long groupId);

	void deleteUsersFromGroupCascading(long groupId);

	List<String> getLogGroup(Long userId);

	boolean isBindByOtherGroup(Long loginUserId);

	List<String> getGroupNames(List<Long> groupIdsNew);

	List<Long> getGroupIdsOld(Long userId);

	List<Long> getGroupDecsantUserIds(long groupId);

	boolean existUserGroup(Long userId, Long groupId);
}
