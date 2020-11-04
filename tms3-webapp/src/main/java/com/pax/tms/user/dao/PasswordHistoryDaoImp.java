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
import com.pax.tms.user.model.PasswordHistory;

@Repository("passwordHistoryDaoImp")
public class PasswordHistoryDaoImp extends BaseHibernateDao<PasswordHistory, Long> implements PasswordHistoryDao {

	@Override
	public void deletePasswordHistory(long userId) {
		String hql = "delete from PasswordHistory where userId=:userId";
		createQuery(hql).setParameter("userId", userId).executeUpdate();
	}

	@Override
	public List<PasswordHistory> getLastPasswords(long userId, int count) {
		String hql = "select ph from PasswordHistory ph where ph.userId=:userId order by ph.changeDate desc";
		return createQuery(hql, PasswordHistory.class).setParameter("userId", userId).setMaxResults(count)
				.getResultList();
	}

}
