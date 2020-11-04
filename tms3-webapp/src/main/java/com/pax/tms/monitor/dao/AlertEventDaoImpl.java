/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description: List Condition/find Setting
 * Revision History:		
 * Date	                 Author	                Action
 * 20170111  	         Frank           	    Modify 
 * ============================================================================		
 */
package com.pax.tms.monitor.dao;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.MatchMode;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.tms.monitor.model.AlertEvent;
import com.pax.tms.monitor.web.form.QueryAlertEventForm;

@Repository("alertEentDaoImpl")
public class AlertEventDaoImpl extends BaseHibernateDao<AlertEvent, Long> implements AlertEventDao {
	private static final String GROUP_ID = "groupId";
	private static final String GROUP_NAME = "groupName";
	private static final String ALERT_MSG = "alertMsg";

	@SuppressWarnings("deprecation")
	@Override
	public <T, S extends Serializable> List<T> page(S command, int start, int length) {
		QueryAlertEventForm form = (QueryAlertEventForm) command;
		String filter = form.getFuzzyCondition();
		StringBuilder sql = new StringBuilder(
				" SELECT G.NAME_PATH AS NAMEPATH,G.GROUP_NAME AS GROUPNAME, E.EVENT_ID AS EVENTID, E.GROUP_ID AS GROUPID, 		"
						+ " E.COND_ID AS CONDID, E.ALERT_TM AS ALERTTIME, E.ALERT_VALUE AS ALERTVALUE, 	"
						+ " E.ALERT_SEVERITY AS ALERTSEVERITY, E.ALERT_MESSAGE AS ALERTMSG				"
						+ " FROM TMSTALERT_EVENT E 														"
						+ " INNER JOIN PUBTGROUP G ON G.GROUP_ID=E.GROUP_ID 							"
						+ " INNER JOIN PUBTGROUP_PARENTS P ON G.GROUP_ID=P.GROUP_ID						"
						+ " WHERE P.PARENT_ID=:groupId ");
		if (StringUtils.isNotEmpty(filter)) {
			sql.append(" AND (LOWER(G.GROUP_NAME) like :groupName ESCAPE '!' ");
			sql.append(" OR LOWER(E.ALERT_MESSAGE) like :alertMsg ESCAPE'!')");
		}
		sql.append(" ORDER BY E.ALERT_TM DESC");
		@SuppressWarnings("unchecked")
		NativeQuery<T> query = super.createNativeQuery(new String(sql));
		query.setFirstResult(start).setMaxResults(length).setParameter(GROUP_ID, form.getGroupId());
		if (StringUtils.isNotEmpty(filter)) {
			query.setParameter(GROUP_NAME, toMatchString(filter, MatchMode.ANYWHERE, true));
			query.setParameter(ALERT_MSG, toMatchString(filter, MatchMode.ANYWHERE, true));
		}
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <S extends Serializable> long count(S command) {
		QueryAlertEventForm form = (QueryAlertEventForm) command;
		String filter = form.getFuzzyCondition();
		StringBuilder sql = new StringBuilder(
				" SELECT count(*) as totals 															"
						+ " FROM TMSTALERT_EVENT E 														"
						+ " INNER JOIN PUBTGROUP G ON G.GROUP_ID=E.GROUP_ID 							"
						+ " INNER JOIN PUBTGROUP_PARENTS P ON G.GROUP_ID=P.GROUP_ID						"
						+ " WHERE P.PARENT_ID=:groupId ");
		if (StringUtils.isNotEmpty(filter)) {
			sql.append(" AND (LOWER(G.GROUP_NAME) like :groupName ESCAPE '!' ");
			sql.append(" OR LOWER(E.ALERT_MESSAGE) like :alertMsg ESCAPE'!' )");
		}
		NativeQuery<Number> query = super.createNativeQuery(new String(sql));
		query.setParameter("groupId", form.getGroupId());
		if (StringUtils.isNotEmpty(filter)) {
			query.setParameter(GROUP_NAME, toMatchString(filter, MatchMode.ANYWHERE, true));
			query.setParameter(ALERT_MSG, toMatchString(filter, MatchMode.ANYWHERE, true));
		}
		Number obj = query.getSingleResult();
		return obj.longValue();
	}

	@Override
	public AlertEvent findByGroupId(Long groupId) {
		String hql = "from AlertEvent  where groupId=:groupId";
		Query<AlertEvent> query = createQuery(hql, AlertEvent.class).setParameter(GROUP_ID, groupId);
		return super.uniqueResult(query);
	}
}
