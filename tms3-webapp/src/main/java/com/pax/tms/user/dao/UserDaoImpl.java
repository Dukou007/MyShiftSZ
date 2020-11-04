/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) YYYY-? PAX Technology, Inc. All rights reserved.		
 * Description: user dao interfaces	implementation	
 * Revision History:		
 * Date	        Author	          Action
 * 20170111     Carry       user dao interfaces implementation and query user list
 * ============================================================================		
 */
package com.pax.tms.user.dao;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.TemporalType;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.common.dao.hibernate.CriteriaWrapper;
import com.pax.common.web.form.BaseForm;
import com.pax.tms.user.model.Role;
import com.pax.tms.user.model.User;
import com.pax.tms.user.model.UserGroup;
import com.pax.tms.user.model.UserRole;
import com.pax.tms.user.web.form.QueryUserForm;

@Repository("userDaoImpl")
public class UserDaoImpl extends BaseHibernateDao<User, Long> implements UserDao {
	private static final String LAST_LOGIN_TIME = "lastLoginTime";

	// query user list and fuzzy search
	@Override
	protected <S extends Serializable> CriteriaWrapper getCriteria(S command, boolean ordered) {
		QueryUserForm form = (QueryUserForm) command;

		CriteriaWrapper wrapper = createCriteriaWrapper(User.class, "u");
//		wrapper.eq("u.sys", false);
		DetachedCriteria userCriteria = wrapper.subquery(UserRole.class, "ur", "ur.user.id", "u.id");
		userCriteria.createAlias("ur.role", "r");
		if (form.getActiveRoleId() != null) {
			userCriteria.add(Restrictions.eq("r.id", form.getActiveRoleId()));
		}
		userCriteria.add(Restrictions.eq("r.appName", form.getAppName()));
		userCriteria.setProjection(Projections.property("ur.user.id"));
		wrapper.exists(userCriteria);

		DetachedCriteria privilegeCriteria = wrapper.subquery(UserGroup.class, "ug", "ug.user.id", "u.id");
		privilegeCriteria.createAlias("ug.group", "g");
		privilegeCriteria.createAlias("g.ancestors", "gas");
		privilegeCriteria.createAlias("gas.ancestor", "ag");
		privilegeCriteria.createAlias("ag.userGroupList", "aug");
		privilegeCriteria.add(Restrictions.eq("aug.user.id", form.getLoginUserId()));
		privilegeCriteria.setProjection(Projections.property("ug.group.id"));
		wrapper.exists(privilegeCriteria);
		DetachedCriteria criteria = wrapper.subquery(UserGroup.class, "ug", "ug.user.id", "u.id");
		criteria.createAlias("ug.group", "g");
		criteria.createAlias("g.ancestors", "gas");
		criteria.add(Restrictions.in("gas.ancestor.id", form.getGroupId()));
		criteria.setProjection(Projections.property("ug.group.id"));
		wrapper.exists(criteria);

		wrapper.fuzzy(form.getFuzzyCondition(), "username", "email", "directory");

		if (form.getRoleId() != null) {
			wrapper.eq("userRole.role.id", form.getRoleId());
		}

		wrapper.setProjection(Arrays.asList("id", "username", "fullname", "email", "locked", "enabled", "ldap",
				"directory", "lastLoginDate","sys"));
		if (ordered) {
			wrapper.addOrder(form, "modifyDate", ORDER_DESC);
			wrapper.addOrder("id", ORDER_ASC);
		}
		return wrapper;
	}

	@Override
	public User findByUsername(String username) {
		String hql = "from User user where LOWER(user.username)=?";
		return uniqueResult(hql, username.toLowerCase());
	}

	@Override
	public User findByUsernameAndEmail(String username, String email) {
		String hql = "from User user where LOWER(user.username)=? and LOWER(user.email)=?";
		return uniqueResult(hql, username.toLowerCase(), email.toLowerCase());
	}

	@Override
	public List<User> getInactiveUsers(Date lastLoginTime) {
		String hql = "from User user where user.enabled=true and user.sys=false and user.locked=false"
				+ " and ((user.lastLoginDate is null and user.createDate<=:lastLoginTime)"
				+ " or user.lastLoginDate<=:lastLoginTime)";
		return createQuery(hql, User.class).setParameter(LAST_LOGIN_TIME, lastLoginTime, TemporalType.TIMESTAMP)
				.getResultList();
	}

	@Override
	public List<User> getInactiveUsers(Date startLastLoginDate, Date endLastLoginDate) {
		String hql = "from User user where user.enabled=true and user.sys=false and user.locked=false"
				+ " and ((user.lastLoginDate is null and user.createDate>=:startDate and user.createDate<:endDate)"
				+ " or (user.lastLoginDate<:endDate and user.lastLoginDate>=:startDate))";

		return createQuery(hql, User.class).setParameter("endDate", endLastLoginDate, TemporalType.TIMESTAMP)
				.setParameter("startDate", startLastLoginDate, TemporalType.TIMESTAMP).getResultList();

	}

	@Override
	public void disableInactiveUsers(Long userId) {
		String hql = "update User user set user.enabled=false where user.id=:userId";
		this.getSession().createQuery(hql).setParameter("userId", userId).executeUpdate();
	}

	@Override
	public List<User> getDisabledInactiveUser(Date lastLoginTime) {
		String hql = "from User user where user.enabled=false and user.sys=false"
				+ " and ((user.lastLoginDate is null and user.createDate<=:lastLoginTime)"
				+ " or user.lastLoginDate<=:lastLoginTime)";
		return createQuery(hql, User.class).setParameter(LAST_LOGIN_TIME, lastLoginTime, TemporalType.TIMESTAMP)
				.getResultList();
	}

	@Override
	public List<User> getPasswordExpiredActiveUsers(Date endLastChangeDate, Date startLastChangeDate) {
		String hql = "from User user where user.enabled=true and user.locked=false and user.ldap=false"
				+ " and user.pwdChgDate<=:endLastChangeDate and user.pwdChgDate>=:startLastChangeDate";
		return getSession().createQuery(hql, User.class)
				.setParameter("endLastChangeDate", endLastChangeDate, TemporalType.TIMESTAMP)
				.setParameter("startLastChangeDate", startLastChangeDate, TemporalType.TIMESTAMP).getResultList();
	}

	@Override
	public List<User> getPasswordExpiredUser(Date lastChangeDate) {
		String hql = "from User user where user.enabled=true and user.locked=false and user.ldap=false"
				+ " and user.pwdChgDate<=:lastChangeDate";
		return getSession().createQuery(hql, User.class)
				.setParameter("lastChangeDate", lastChangeDate, TemporalType.TIMESTAMP).getResultList();
	}

	@Override
	public void setPasswordExpired(Long userId) {
		String hql = "update User user set user.forceChgPwd=:fcp where user.id=:userId";
		this.getSession().createQuery(hql).setParameter("fcp", true).setParameter("userId", userId).executeUpdate();
	}
	//site administrator在user management需要看到其他的site administrator
	//排除当前登录的site administrator
	@Override
	public int getUserCount(String appName, Long groupId, BaseForm command) {
		String hql = "select count(distinct ur.user.id) from UserRole ur,Role r where ur.role.id=r.id and r.appName=:appName "
				+ "and exists (select ug.group.id from UserGroup ug inner join Group g on ug.group.id=g.id inner join GroupAncestor gas on g.id=gas.group.id "
				+ " inner join Group ag on gas.ancestor.id=ag.id inner join UserGroup aug on ag.id=aug.group.id"
				+ " where ug.user.id=ur.user.id and aug.user.id=:userId) and exists (select ug1.group.id from UserGroup ug1 inner join Group g1 on ug1.group.id=g1.id "
				+ " inner join GroupAncestor gas1 on g1.id=gas1.group.id "
				+ " where ug1.user.id = ur.user.id and ug1.user.id!=:userId2 and gas1.ancestor.id in (:groupId))";
		return createQuery(hql, Long.class).setParameter("userId", command.getLoginUserId()).setParameter("userId2", command.getLoginUserId())
				.setParameter("appName", appName).setParameter("groupId", groupId).getSingleResult().intValue();
	}

	@Override
	public List<User> getLdapUser() {
		String hql = "from User user where user.ldap=true";
		return createQuery(hql, User.class).getResultList();
	}

	@Override
	public void deleteUserCascading(List<Long> needDeleteUserIds) {

		String sql1 = "delete from PUBTUSER_ROLE where USER_ID=?";
		doBatchExecute(sql1, needDeleteUserIds.iterator(), (st, userId) -> st.setLong(1, userId));

		String sql2 = "delete from PUBTPWDRESETTOKEN where USER_ID=?";
		doBatchExecute(sql2, needDeleteUserIds.iterator(), (st, userId) -> st.setLong(1, userId));

		String sql3 = "delete from PUBTPWDCHGHST where USER_ID=?";
		doBatchExecute(sql3, needDeleteUserIds.iterator(), (st, userId) -> st.setLong(1, userId));
		String sql4 = "delete from PUBTUSER where USER_ID=?";
		doBatchExecute(sql4, needDeleteUserIds.iterator(), (st, userId) -> st.setLong(1, userId));

	}

	@Override
	public boolean hasUsers(List<Long> groupIds) {
		String hql = "select count(*) from UserGroup ug where ug.group.id in (:groupIds)";
		long count = createQuery(hql, Long.class).setParameterList("groupIds", groupIds).getSingleResult();
		return count > 0 ? true : false;
	}

	@Override
	public List<String> getUserRolesByUserName(String username) {
		String hql = "select distinct r.appName from Role r where r.id in (select ur.role.id from UserRole ur where ur.user.id=(select u.id  from User u where u.username=:username))";
		return createQuery(hql, String.class).setParameter("username", username).getResultList();
	}

	@Override
	public void updateRealAndUsageDashboardPie(String data, Long userId, String type) {
		if (type.contains("realTime")) {
			String hql = "update User user set user.realTime=:realTime where user.id=:userId";
			this.getSession().createQuery(hql).setParameter("userId", userId).setParameter("realTime", data)
					.executeUpdate();
		} else {
			String hql = "update User user set user.usage=:usage where user.id=:userId";
			this.getSession().createQuery(hql).setParameter("userId", userId).setParameter("usage", data)
					.executeUpdate();
		}

	}
	//site administrator在user management需要看到其他的site administrator
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<Map<String, Object>> getRoleAndCount(String appName, Long groupId, BaseForm command) {
		String hql = "select count(ur.user.id) as count,r.id as id,r.name as name from Role r left join UserRole ur on r.id=ur.role.id where r.appName=:appName "
				+ "and exists (select ug.group.id from UserGroup ug inner join Group g on ug.group.id=g.id inner join GroupAncestor gas on g.id=gas.group.id "
				+ "inner join Group ag on gas.ancestor.id=ag.id inner join UserGroup aug on ag.id=aug.group.id"
				+ " where ug.user.id=ur.user.id and aug.user.id=:userId) and exists (select ug1.group.id from UserGroup ug1 inner join Group g1 on ug1.group.id=g1.id "
				+ " inner join GroupAncestor gas1 on g1.id=gas1.group.id "
				+ " where ug1.user.id = ur.user.id and ug1.user.id!=:userId2 and gas1.ancestor.id in (:groupId)) group by r.id,r.name ";
		Query query = createQuery(hql).setParameter("appName", appName).setParameter("userId", command.getLoginUserId()).setParameter("userId2", command.getLoginUserId())
				.setParameter("groupId", groupId);
		return mapResult(query).getResultList();
	}

	@Override
	public List<Role> findRole(String appName) {
		String hql = "from Role r where r.appName=:appName and r.name !='Site Administrator' ";
		return createQuery(hql, Role.class).setParameter("appName", appName).getResultList();
	}

	@Override
	public Role getRole(long roleId) {
		String hql = "from Role role where role.id=?";
		return uniqueResult(hql, roleId);
	}

	@Override
	public void updateLastLoginDate(String userName) {

		String sql = "update PUBTUSER set LAST_LOGIN_DATE=:lastLoginDate where USERNAME=:userName";
		createNativeQuery(sql).setParameter("lastLoginDate", new Date()).setParameter("userName", userName)
				.executeUpdate();

	}

}
