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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pax.common.dao.IBaseDao;
import com.pax.common.service.impl.BaseService;
import com.pax.tms.group.model.Group;
import com.pax.tms.group.service.GroupService;
import com.pax.tms.user.dao.UserGroupDao;
import com.pax.tms.user.model.User;
import com.pax.tms.user.model.UserGroup;

@Service("userGroupServiceImpl")
public class UserGroupServiceImpl extends BaseService<UserGroup, Long> implements UserGroupService {

	@Autowired
	private UserGroupDao userGroupDao;

	@Autowired
	private GroupService groupService;

	@Override
	public IBaseDao<UserGroup, Long> getBaseDao() {
		return userGroupDao;
	}

	public void setUserGroupDao(UserGroupDao userGroupDao) {
		this.userGroupDao = userGroupDao;
	}

	public void setGroupService(GroupService groupService) {
		this.groupService = groupService;
	}

	@Override
	public List<Group> validateUserGroups(Long[] groupIds) {
		List<Group> groupList = groupService.validateGroups(groupIds);
		checkUserGroups(groupList);
		return groupList;
	}

	private void checkUserGroups(List<Group> groupList) {
		Set<String> groupIdPathSet = getGroupNamePathSet(groupList);
		Iterator<Group> it = groupList.iterator();
		while (it.hasNext()) {
			String groupIdPath = it.next().getIdPath();
			for (String otherGroupIdPath : groupIdPathSet) {
				if (!StringUtils.equals(otherGroupIdPath, groupIdPath) && groupIdPath.startsWith(otherGroupIdPath)) {
					it.remove();
					break;
				}
			}
		}
	}

	private Set<String> getGroupNamePathSet(List<Group> groupList) {
		Set<String> set = new HashSet<String>(groupList.size());
		for (Group group : groupList) {
			set.add(group.getIdPath());
		}
		return set;
	}

	@Override
	public void addUserGroups(User user, Collection<Group> groupList) {
		if (CollectionUtils.isEmpty(groupList)) {
			return;
		}

		for (Group group : groupList) {
			UserGroup userGroup = new UserGroup();
			userGroup.setUser(user);
			userGroup.setGroup(group);
			userGroupDao.save(userGroup);
		}
	}

	@Override
	public void editUserGroups(User user, Collection<Group> groupList) {
		if (CollectionUtils.isEmpty(user.getUserGroupList())) {
			addUserGroups(user, groupList);
			return;
		}

		Map<String, Group> newIdPathMap = new HashMap<String, Group>();
		for (Group group : groupList) {
			newIdPathMap.put(group.getIdPath(), group);
		}

		Iterator<UserGroup> it = user.getUserGroupList().iterator();
		while (it.hasNext()) {
			UserGroup ug = it.next();
			if (ug.getGroup() == null) {
				userGroupDao.deleleUserGroup(ug.getId());
				continue;
			}

			String idPath = ug.getGroup().getIdPath();
			if (newIdPathMap.containsKey(idPath)) {
				newIdPathMap.remove(idPath);
			} else {
				userGroupDao.deleleUserGroup(ug.getId());
			}
		}

		addUserGroups(user, newIdPathMap.values());
	}

	@Override
	public void deleteUserGroups(User user) {
		userGroupDao.deleleUserGroups(user.getId());
	}

	@Override
	public Long getFavoriteGroup(long userId) {
		return userGroupDao.getFavoriteGroup(userId);
	}

	@Override
	public void setFavoriteGroup(long userId, long favoriteGroupId) {
		userGroupDao.setFavoriteGroup(userId, favoriteGroupId);
	}

	@Override
	public List<User> getAssignedGroupUsers(long groupId) {
		return userGroupDao.getAssignedGroupUsers(groupId);
	}

	@Override
	public List<User> getOnlyAssignedOneGroupUsers(long groupId) {
		return userGroupDao.getOnlyAssignedOneGroupUsers(groupId);
	}

	@Override
	public void deleteUsersFromGroupCascading(long groupId) {
		userGroupDao.deleteUsersFromGroupCascading(groupId);
	}

	@Override
	public String getLogGroup(Long userId) {
		List<String> groupList = userGroupDao.getLogGroup(userId);
		String groups = joinUserGroups(groupList);
		String group = groups.toString();
		if (group != null && group.length() > 253) {
			group = group.substring(0, 252) + " ...";
		}

		return group;
	}

	private String joinUserGroups(List<String> groupList) {
		if (groupList == null || groupList.isEmpty()) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String str : groupList) {
			if (!first) {
				sb.append(",");
			} else {
				first = false;
			}
			sb.append(str);
		}
		return sb.toString();
	}

	@Override
	public boolean isBindByOtherGroup(Long loginUserId) {

		return userGroupDao.isBindByOtherGroup(loginUserId);
	}

	@Override
	public List<Long> getGroupIdsOld(Long userId) {
		return userGroupDao.getGroupIdsOld(userId);
	}

	@Override
	public String getGroupNames(List<Long> groupIdsNew) {
		List<String> groupList = userGroupDao.getGroupNames(groupIdsNew);
		String groups = joinUserGroups(groupList);
		String group = groups.toString();
		if (group != null && group.length() > 253) {
			group = group.substring(0, 252) + " ...";
		}
		return group;
	}

	@Override
	public List<Long> getGroupDecsantUserIds(long groupId) {

		return userGroupDao.getGroupDecsantUserIds(groupId);
	}

	@Override
	public void deleteUserGroup(List<User> users, Long sourceGroupId, Long targetGroupId) {

		for (User user : users) {
			if (userGroupDao.existUserGroup(user.getId(), targetGroupId)) {
				userGroupDao.deleteUserGroup(user.getId(), sourceGroupId);
			}
		}

	}

}
