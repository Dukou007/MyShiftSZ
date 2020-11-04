/*		
 * ============================================================================		
 *  COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.	
 * Description:search user maintenance data
 * Revision History:		
 * Date	                 Author	                Action
 * 20170111  	         Alan Jiang           	Modify
 * ============================================================================		
 */
package com.pax.tms.report.dao;

import java.io.Serializable;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.common.dao.hibernate.CriteriaWrapper;
import com.pax.tms.report.web.form.QueryUserMaintenanceForm;
import com.pax.tms.user.model.User;
import com.pax.tms.user.model.UserLog;

@Repository("userMaintenanceDaoImpl")
public class UserMaintenanceDaoImpl extends BaseHibernateDao<UserLog, Long> implements UserMaintenanceDao {

	@Override
	protected <S extends Serializable> CriteriaWrapper getCriteria(S command, boolean ordered) {
		QueryUserMaintenanceForm form = (QueryUserMaintenanceForm) command;
		CriteriaWrapper wrapper = createCriteriaWrapper(UserLog.class, "ul");
		DetachedCriteria privilegeCriteria = wrapper.subquery(User.class, "u", "u.username", "ul.username");
		privilegeCriteria.createAlias("u.userGroupList", "ug");
		privilegeCriteria.createAlias("ug.group", "g");
		privilegeCriteria.createAlias("g.ancestors", "gas");
		privilegeCriteria.createAlias("gas.ancestor", "ag");
		privilegeCriteria.createAlias("ag.userGroupList", "aug");
		privilegeCriteria.add(Restrictions.eq("aug.user.id", form.getLoginUserId()));
		privilegeCriteria.setProjection(Projections.property("u.id"));
		wrapper.exists(privilegeCriteria);

		DetachedCriteria criteria = wrapper.subquery(User.class, "ur", "ur.username", "ul.username");
		criteria.createAlias("ur.userGroupList", "ug");
		criteria.createAlias("ug.group", "g");
		criteria.createAlias("g.ancestors", "ga");
		criteria.add(Restrictions.eq("ga.ancestor.id", form.getGroupId()));
		criteria.setProjection(Projections.property("ur.username"));
		wrapper.exists(criteria);

		if (form.getStartTime() != null && form.getEndTime() != null) {
			wrapper.ge("eventTime", form.getStartTime());
			wrapper.le("eventTime", form.getEndTime());
		} else if (form.getStartTime() != null) {
			wrapper.ge("eventTime", form.getStartTime());
		} else if (form.getEndTime() != null) {
			wrapper.le("eventTime", form.getEndTime());
		}
		if (StringUtils.isNotEmpty(form.getSelectedStatus())) {
			if ("Activate User".equalsIgnoreCase(form.getSelectedStatus())) {
				wrapper.like_start("eventAction", form.getSelectedStatus());
			} else {
				wrapper.like("eventAction", form.getSelectedStatus());
			}
		}

		wrapper.fuzzy(form.getFuzzyCondition(), "username", "role", "eventAction");
		wrapper.setProjection(Arrays.asList("id", "username", "role", "clientIp", "eventAction", "eventTime"));

		if (ordered) {
			wrapper.addOrder(form, "eventTime", ORDER_DESC);
			wrapper.addOrder("username", ORDER_ASC);
		}
		return wrapper;
	}

}
