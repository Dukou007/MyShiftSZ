/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) YYYY-? PAX Technology, Inc. All rights reserved.		
 * Description: user dao interface  implementation  		
 * Revision History:		
 * Date	        Author	                  Action
 * 20170111     Carry            user dao interface implementation 
 * ============================================================================		
 */
package com.pax.tms.user.dao;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.TemporalType;

import org.springframework.stereotype.Repository;

import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.common.dao.hibernate.CriteriaWrapper;
import com.pax.tms.user.model.UserLog;
import com.pax.tms.user.web.form.QueryUserLogForm;

@Repository("userLogDaoImpl")
public class UserLogDaoImpl extends BaseHibernateDao<UserLog, Long> implements UserLogDao {

	private static final String EVENT_TIME = "eventTime";

	// query user log list
	@Override
	protected <S extends Serializable> CriteriaWrapper getCriteria(S command, boolean ordered) {
		QueryUserLogForm form = (QueryUserLogForm) command;

		CriteriaWrapper wrapper = createCriteriaWrapper(UserLog.class, "userLog");
		wrapper.fuzzy(form.getFuzzyCondition(), "username", "role", "eventAction", EVENT_TIME);
		wrapper.setProjection(Arrays.asList("id", "username", "role", "clientIp", "eventAction", EVENT_TIME));

		if (ordered) {
			wrapper.addOrder(form, EVENT_TIME, ORDER_DESC);
			wrapper.addOrder("id", ORDER_ASC);
		}
		return wrapper;
	}

	@Override
	public List<String> getUserRoles(Long userId) {
		String hql = "select r.name from UserRole ur inner join Role r on ur.role.id=r.id where r.appName='TMS' and ur.user.id=:userId";
		return createQuery(hql, String.class).setParameter("userId", userId).getResultList();
	}

	@Override
	public void systemClearUserLog(Date when) {
		String hql = "delete from UserLog where eventTime<=:when ";
		this.getSession().createQuery(hql).setParameter("when", when, TemporalType.TIMESTAMP).executeUpdate();
	}
}
