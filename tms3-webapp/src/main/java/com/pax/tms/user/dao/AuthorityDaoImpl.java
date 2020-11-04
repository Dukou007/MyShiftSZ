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
import com.pax.tms.user.model.Authority;

@Repository("authorityDaoImpl")
public class AuthorityDaoImpl extends BaseHibernateDao<Authority, Long> implements AuthorityDao {

	@Override
	public List<String> getAuthorityCodeList() {
		String hql = "select auth.code from Authority auth";
		return this.getSession().createQuery(hql, String.class).getResultList();
	}

	@Override
	public List<Authority> list() {
		String hql = "from Authority auth order by auth.order, auth.name";
		Query<Authority> query = getSession().createQuery(hql, Authority.class);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Long> getProvilegeIdByName(String privileges) {
		String hql = "select auth.id from Authority auth where auth.application=:privileges";
		return this.getSession().createQuery(hql).setParameter("privileges", privileges).getResultList();
	}

}