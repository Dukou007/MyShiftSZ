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

import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.tms.user.model.User;
import com.pax.tms.user.model.UserGroup;

@Repository("userGroupDaoImpl")
public class UserGroupDaoImpl extends BaseHibernateDao<UserGroup, Long> implements UserGroupDao {

	private static final String DEFAULT_GROUP = "defaultGroup";
	private static final String GROUP_ID = "groupId";
	private static final String USER_ID = "userId";

	@Override
	public void deleleUserGroups(long userId) {
		String hql = "delete from UserGroup where user.id=:userId";
		createQuery(hql).setParameter(USER_ID, userId).executeUpdate();
	}

	@Override
	public void deleleUserGroup(long id) {
		String hql = "delete from UserGroup where id=:id";
		createQuery(hql).setParameter("id", id).executeUpdate();
	}
	
	@Override
	public void deleteUserGroup(Long userId, Long groupId) {
		String hql = "delete from UserGroup where user.id=:userId and group.id=:groupId";
		createQuery(hql).setParameter("userId", userId).setParameter("groupId", groupId).executeUpdate();
	}

	@Override
	public void setFavoriteGroup(long userId, long favoriteGroupId) {
		String resetDefaultGroupSql = "update UserGroup userGroup set userGroup.defaultGroup=:defaultGroup where userGroup.user.id=:userId";
		createQuery(resetDefaultGroupSql).setParameter(DEFAULT_GROUP, false).setParameter(USER_ID, userId)
				.executeUpdate();

		String setDefaultGroupSql = "update UserGroup userGroup set userGroup.defaultGroup=:defaultGroup where userGroup.user.id=:userId and userGroup.group.id=:groupId";
		createQuery(setDefaultGroupSql).setParameter(DEFAULT_GROUP, true).setParameter(USER_ID, userId)
				.setParameter(GROUP_ID, favoriteGroupId).executeUpdate();
	}

	@Override
	public Long getFavoriteGroup(long userId) {
		String hql = "select group.id from UserGroup where user.id=:userId and defaultGroup=:defaultGroup";
		Query<Long> query = createQuery(hql, Long.class).setParameter(USER_ID, userId).setParameter(DEFAULT_GROUP,
				true);
		return uniqueResult(query);
	}

	@Override
	public List<User> getAssignedGroupUsers(long groupId) {
		String hql = "select ug.user from UserGroup ug where ug.group.id=:groupId";
		return createQuery(hql, User.class).setParameter(GROUP_ID, groupId).getResultList();
	}

	@Override
	public List<User> getOnlyAssignedOneGroupUsers(long groupId) {
		String hql = "select ug.user from UserGroup ug where ug.group.id=:groupId"
				+ " and not exists (select ug2.group.id from UserGroup ug2 where ug2.user.id=ug.user.id and ug2.group.id!=:groupId)";
		return createQuery(hql, User.class).setParameter(GROUP_ID, groupId).getResultList();
	}

	@Override
	public void deleteUsersFromGroupCascading(long groupId) {
		String hql = "delete from UserGroup ug  where ug.group.id in"
				+ " (select ga.group.id from GroupAncestor ga where  ga.ancestor.id=:groupId)";
		createQuery(hql).setParameter(GROUP_ID, groupId).executeUpdate();
	}

	@Override
	public List<Long> getGroupDecsantUserIds(long groupId) {
		String hql = "select ug.user.id from UserGroup ug  where ug.group.id in"
				+ " (select ga.group.id from GroupAncestor ga where  ga.ancestor.id=:groupId)";
		return createQuery(hql, Long.class).setParameter(GROUP_ID, groupId).getResultList();

	}

	@Override
	public List<String> getLogGroup(Long userId) {
		String hql = "select g.namePath from UserGroup ug inner join Group g on ug.group.id=g.id where ug.user.id=:userId";
		return createQuery(hql, String.class).setParameter("userId", userId).getResultList();
	}

	@Override
	public boolean isBindByOtherGroup(Long loginUserId) {
		String hql = "select count(ug.id) from UserGroup ug where " + " ug.user.id=:loginUserId";
		return createQuery(hql, Long.class).setParameter("loginUserId", loginUserId).getSingleResult().intValue() > 0
				? true : false;
	}

	@Override
	public List<String> getGroupNames(List<Long> groupIdsNew) {
		String hql = "select g.namePath from Group g  where g.id in(:groupId)";
		return createQuery(hql, String.class).setParameter("groupId", groupIdsNew).getResultList();
	}

	@Override
	public List<Long> getGroupIdsOld(Long userId) {
		String hql = "select ug.group.id from UserGroup ug where ug.user.id=:userId";
		return createQuery(hql, Long.class).setParameter("userId", userId).getResultList();
	}

	@Override
	public boolean existUserGroup(Long userId, Long groupId) {
		String hql = "select count(id) from UserGroup ug where ug.user.id=:userId and ug.group.id=:groupId";
		Query<Long> query = createQuery(hql, Long.class).setParameter("userId", userId).setParameter("groupId",
				groupId);
		return query.getSingleResult() > 0;
	}

}
