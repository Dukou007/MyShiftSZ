package com.pax.tms.user.dao;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.tms.user.model.ACLTUserGroup;

@Repository("aclTUserGroupDaoImpl")
public class ACLTUserGroupDaoImpl extends BaseHibernateDao<ACLTUserGroup, Long> implements ACLTUserGroupDao {

	@Override
	public void saveACLUserGroups(Long loginUserId, Long groupId) {

		String sql = " insert into ACLTUSER_GROUP(USER_ID,GROUP_ID)"
				+ "	 select u.USER_ID, p.PARENT_ID from PUBTGROUP g inner join PUBTGROUP_PARENTS p "
				+ " on g.GROUP_ID=p.GROUP_ID ,PUBTUSER u where g.GROUP_ID=:groupId and u.USER_ID=:loginUserId"
				+ " and not exists (select USER_ID from ACLTUSER_GROUP acl where acl.GROUP_ID=p.PARENT_ID"
				+ "	and acl.USER_ID=u.USER_ID)";

		createNativeQuery(sql).setParameter("groupId", groupId).setParameter("loginUserId", loginUserId)
				.executeUpdate();

	}

	@Override
	public void deleteACLUserGroups(Long loginUserId, Long groupId) {

		String sql = " select acl.GROUP_ID ,acl.USER_ID from ACLTUSER_GROUP acl,PUBTGROUP g,PUBTGROUP_PARENTS p"
				+ " where g.GROUP_ID=p.GROUP_ID and g.GROUP_ID=acl.GROUP_ID"
				+ "	and p.PARENT_ID=:groupId and acl.USER_ID=:loginUserId ";

		List<Object[]> result = createNativeQuery(sql, Object[].class).setParameter("groupId", groupId)
				.setParameter("loginUserId", loginUserId).getResultList();
		String deleteSql = "delete from ACLTUSER_GROUP where GROUP_ID=? and USER_ID=?";

		doBatchExecute(deleteSql, result.iterator(), (st, object) -> {
			BigDecimal aclGroupId = new BigDecimal(object[0].toString());
			BigDecimal aclUserId = new BigDecimal(object[1].toString());
			st.setLong(1, aclGroupId.longValue());
			st.setLong(2, aclUserId.longValue());
		});

	}

	@Override
	public void deleteACLUserGroups(Long userId) {

		String sql = "delete from ACLTUSER_GROUP where USER_ID=:userId";
		createNativeQuery(sql).setParameter("userId", userId).executeUpdate();

	}
	
	@Override
	public List<Long> getGroupsByUserId(Long userId) {
		String hql = "select aCLTUserGroup.group.id from ACLTUserGroup aCLTUserGroup where aCLTUserGroup.user.id=:userId";
		return createQuery(hql, Long.class).setParameter("userId", userId).getResultList();
	}

}
