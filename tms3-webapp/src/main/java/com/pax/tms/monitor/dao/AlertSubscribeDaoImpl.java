/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description: get/delete AlertSubscribe
 * Revision History:		
 * Date	                 Author	                Action
 * 20170111  	         Frank           	    Modify 
 * ============================================================================		
 */
package com.pax.tms.monitor.dao;

import org.springframework.stereotype.Repository;

import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.tms.monitor.model.AlertSubscribe;

@Repository("alertSubscribeDaoImpl")
public class AlertSubscribeDaoImpl extends BaseHibernateDao<AlertSubscribe, Long> implements AlertSubscribeDao {
	private static final String COND_ID = "condId";
	private static final String USER_ID = "userId";

	@Override
	public AlertSubscribe getByUserCond(Long userId, Long condId) {
		String hql = " from AlertSubscribe where userId=:userId and condId=:condId";
		return super.uniqueResult(this.createQuery(hql, AlertSubscribe.class).setParameter(USER_ID, userId)
				.setParameter(COND_ID, condId));
	}

	@Override
	public int deleteUserScribe(Long condId, Long userId) {
		String hql = " delete from AlertSubscribe where condId=:condId and userId=:userId";
		return this.createQuery(hql).setParameter(USER_ID, userId).setParameter(COND_ID, condId).executeUpdate();
	}

	@Override
	public int deleteByCondId(Long condId) {
		String hql = " delete from AlertSubscribe where condId=:condId";
		return this.createQuery(hql).setParameter(COND_ID, condId).executeUpdate();
	}
}
