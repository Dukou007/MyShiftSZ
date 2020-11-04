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

import org.springframework.stereotype.Repository;

import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.tms.user.model.Role;
import com.pax.tms.user.model.User;
import com.pax.tms.user.model.UserRole;

@Repository("userRoleDaoImpl")
public class UserRoleDaoImpl extends BaseHibernateDao<UserRole, Long> implements UserRoleDao {
	private static final String USER_ID = "userId";

	@Override
	public void deleteUserRoles(long userId) {
		String hql = "delete from UserRole where user.id=:userId";
		createQuery(hql).setParameter(USER_ID, userId).executeUpdate();
	}

	@Override
	public void deleteUserRole(long userRoleId) {
		String hql = "delete from UserRole ur where ur.id=:userRoleId";
		this.getSession().createQuery(hql).setParameter("userRoleId", userRoleId).executeUpdate();
	}

	@Override
	public List<Role> getUnsignedRoles(long userId) {
		String hql = "select role from Role role where not exists(from UserRole userRole where userRole.role.id=role.id and userRole.user.id=:userId) order by role.name";
		return createQuery(hql, Role.class).setParameter(USER_ID, userId).getResultList();
	}

	@Override
	public List<Role> getAssignedRoles(long userId) {
		String hql = "select r from Role r, UserRole ur where ur.role.id=r.id and ur.user.id=:userId order by r.name";
		return createQuery(hql, Role.class).setParameter(USER_ID, userId).getResultList();
	}

	@Override
	public List<String> getAssignedTMSRoleNames(long userId) {
		String hql = "select r.name from UserRole ur inner join Role r on ur.role.id=r.id where r.appName='TMS' and ur.user.id=:userId";
		return createQuery(hql, String.class).setParameter(USER_ID, userId).getResultList();
	}

	@Override
	public List<String> getAssignedRoleIds(long userId, String appName) {
		String hql = "select r.name from Role r right join UserRole userRole  on r.id=userRole.role.id where r.appName=:appName and  userRole.user.id=:userId";
		return createQuery(hql, String.class).setParameter(USER_ID, userId).setParameter("appName", appName)
				.getResultList();
	}

	@Override
	public List<User> getUsersHasRole(long roleId) {
		String hql = "select user from User as user join user.userRoleList as userRole where userRole.role.id=:roleId order by user.username";
		return createQuery(hql, User.class).setParameter("roleId", roleId).getResultList();
	}
	//roleId可以为null
	@Override
	public UserRole getUserRole(Long userId, Long roleId, Long siteAdminId) {
		String hql="";
		if(null==roleId||0l==roleId){
			hql = "select userRole from UserRole userRole where userRole.user.id=:userId and userRole.role.id=:siteAdminId";
			return uniqueResult(
					getSession().createQuery(hql).setParameter("userId", userId).setParameter("siteAdminId", siteAdminId));
		}
		hql = "select userRole from UserRole userRole where userRole.user.id=:userId and userRole.role.id=:roleId";
		return uniqueResult(
				getSession().createQuery(hql).setParameter("userId", userId).setParameter("roleId", roleId));

	}

}
