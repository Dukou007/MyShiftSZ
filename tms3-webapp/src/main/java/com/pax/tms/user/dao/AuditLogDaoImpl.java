/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description:  query audit log info and add audit log 
 * Revision History:		
 * Date	        Author	            Action
 * 20170111     Carry         query audit log info and add audit log
 * ============================================================================		
 */
package com.pax.tms.user.dao;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import javax.persistence.TemporalType;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.pax.common.dao.DbHelper;
import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.common.dao.hibernate.CriteriaWrapper;
import com.pax.tms.user.model.PubAuditLog;
import com.pax.tms.user.model.User;
import com.pax.tms.user.model.UserGroup;
import com.pax.tms.user.security.UTCTime;
import com.pax.tms.user.web.form.OperatorLogForm;
import com.pax.tms.user.web.form.QueryAuditLogForm;

@Repository("auditLogDaoImpl")
public class AuditLogDaoImpl extends BaseHibernateDao<PubAuditLog, Long> implements AuditLogDao {

	private static final String ACTION_DATE = "actionDate";

	// query audit log list and fuzzy search
	@Override
	protected <S extends Serializable> CriteriaWrapper getCriteria(S command, boolean ordered) {
		QueryAuditLogForm form = (QueryAuditLogForm) command;
		CriteriaWrapper wrapper = createCriteriaWrapper(PubAuditLog.class, "al");
		DetachedCriteria privilegeCriteria = wrapper.subquery(UserGroup.class, "ug", "ug.user.id", "al.user.id");
		privilegeCriteria.createAlias("ug.group", "g");
		privilegeCriteria.createAlias("g.ancestors", "gas");
		privilegeCriteria.createAlias("gas.ancestor", "ag");
		privilegeCriteria.createAlias("ag.userGroupList", "aug");
		privilegeCriteria.add(Restrictions.eq("aug.user.id", form.getLoginUserId()));
		privilegeCriteria.setProjection(Projections.property("ug.group.id"));
		wrapper.exists(privilegeCriteria);

		DetachedCriteria criteria = wrapper.subquery(User.class, "u", "u.id", "al.user.id");
		criteria.createAlias("u.userGroupList", "ug");
		criteria.createAlias("ug.group", "g");
		criteria.createAlias("g.ancestors", "ga");
		criteria.add(Restrictions.eq("ga.ancestor.id", form.getGroupId()));
		criteria.setProjection(Projections.property("u.id"));
		wrapper.exists(criteria);

		if (form.getStartTime() != null && form.getEndTime() != null) {
			wrapper.ge("actionDate", form.getStartTime());
			wrapper.le("actionDate", form.getEndTime());
		} else if (form.getStartTime() != null) {
			wrapper.ge("actionDate", form.getStartTime());
		} else if (form.getEndTime() != null) {
			wrapper.le("actionDate", form.getEndTime());
		}
		if (StringUtils.isNotEmpty(form.getRoleName())) {
			if ("Administrator".equalsIgnoreCase(form.getRoleName())) {
				wrapper.like_start("role", form.getRoleName());
			} else {
				wrapper.like("role", form.getRoleName());
			}
		}

		wrapper.fuzzy(form.getFuzzyCondition(), "username", "role", "actionName");
		wrapper.setProjection(Arrays.asList("id", "username", "role", "actionName", "clientIp", ACTION_DATE));

		if (ordered) {
			wrapper.addOrder(form, ACTION_DATE, ORDER_DESC);
			wrapper.addOrder("id", ORDER_ASC);
		}
		return wrapper;
	}

	@Override
	public void addAuditLog(Collection<String> objects, OperatorLogForm form) {
		if (CollectionUtils.isEmpty(objects)) {
			return;
		}

		long auditLogRelId = DbHelper.generateId("PUBTAUDITLOG_ID", objects.size());

		String sql = "insert into PUBTAUDITLOG (LOG_ID,USERNAME,ROLE,USER_ID,CILENT_IP,ACTION_NAME,ACTION_DATE)"
				+ " values(?,?,?,?,?,?,?) ";
		doWork((conn) -> {
			int count = 0;
			long auditLoglId = auditLogRelId;
			try (PreparedStatement auditLogSt = conn.prepareStatement(sql)) {
				for (String object : objects) {
					processAuditLog(auditLoglId++, auditLogSt, object, form);
					auditLogSt.addBatch();
					count++;
					if (count % 100 == 0) {
						auditLogSt.executeBatch();
						auditLogSt.clearBatch();
					}
				}
				if (count % 100 != 0) {
					auditLogSt.executeBatch();
					auditLogSt.clearBatch();
				}
			}
		});

	}

	// add audit log info
	private void processAuditLog(long auditLoglId, PreparedStatement auditLogSt, String object, OperatorLogForm form)
			throws SQLException {

		auditLogSt.setLong(1, auditLoglId);
		auditLogSt.setString(2, form.getUsername());
		auditLogSt.setString(3, form.getRoles());
		auditLogSt.setLong(4, form.getUserId());
		auditLogSt.setString(5, form.getClientIp());
		if (StringUtils.isEmpty(form.getAdditionMessage())) {
			if (StringUtils.isEmpty(object)) {
				auditLogSt.setString(6, form.getMessage());
			} else {
				auditLogSt.setString(6, form.getMessage() + object);
			}
		} else {
			auditLogSt.setString(6, form.getMessage() + object + form.getAdditionMessage());
		}
		auditLogSt.setTimestamp(7, new Timestamp(form.getRequestTime().getTime()), UTCTime.UTC_CLENDAR);

	}

	@Override
	public boolean checkAuditLogs(String action) {
		String hql = "select count(*) from PubAuditLog al where al.actionName=:action";
		long count = createQuery(hql, Long.class).setParameter("action", action).getSingleResult();
		return count > 0 ? true : false;
	}

	@Override
	public void systemClear(Date when) {
		String hql = "delete from PubAuditLog where actionDate<=:when ";
		this.getSession().createQuery(hql).setParameter("when", when, TemporalType.TIMESTAMP).executeUpdate();
	}
}
