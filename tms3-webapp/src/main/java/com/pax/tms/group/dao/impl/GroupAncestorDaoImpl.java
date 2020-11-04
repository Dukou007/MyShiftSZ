package com.pax.tms.group.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.tms.group.dao.GroupAncestorDao;
import com.pax.tms.group.model.GroupAncestor;

@Repository("GroupAncestorDaoImpl")
public class GroupAncestorDaoImpl extends BaseHibernateDao<GroupAncestor, Long> implements GroupAncestorDao {

	@Override
	public List<Long> getGroupIdByParentId(Long parentId) {
		String hql = "select ga.group.id from GroupAncestor ga where ga.ancestor.id=:parentId";
		List<Long> result = createQuery(hql, Long.class).setParameter("parentId", parentId).getResultList();
		return result;
	}

}
