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

import java.util.Collection;
import java.util.List;

import com.pax.common.service.IBaseService;
import com.pax.tms.group.model.Group;
import com.pax.tms.user.model.User;
import com.pax.tms.user.model.UserGroup;

public interface UserGroupService extends IBaseService<UserGroup, Long> {

	List<Group> validateUserGroups(Long[] groupIds);

	void addUserGroups(User user, Collection<Group> groupList);

	void editUserGroups(User user, Collection<Group> groupList);

	void deleteUserGroups(User user);

	Long getFavoriteGroup(long userId);

	void setFavoriteGroup(long userId, long favoriteGroupId);

	List<User> getAssignedGroupUsers(long groupId);

	List<User> getOnlyAssignedOneGroupUsers(long groupId);

	void deleteUsersFromGroupCascading(long groupId);

	String getLogGroup(Long id);

	boolean isBindByOtherGroup(Long loginUserId);

	String getGroupNames(List<Long> groupIdsNew);

	List<Long> getGroupIdsOld(Long userId);

	List<Long> getGroupDecsantUserIds(long groupId);

	void deleteUserGroup(List<User> users, Long sourceGroupId, Long targetGroupId);
}
