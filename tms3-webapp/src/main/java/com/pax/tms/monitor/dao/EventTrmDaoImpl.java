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
package com.pax.tms.monitor.dao;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.tms.monitor.model.EventTrm;
import com.pax.tms.monitor.web.form.OperatorEventForm;
import com.pax.tms.user.security.UTCTime;

@Repository("eventTrmDaoImpl")
public class EventTrmDaoImpl extends BaseHibernateDao<EventTrm, Long> implements EventTrmDao {
	@Override
	public void addEventLog(Collection<String> objects, OperatorEventForm form) {

		String sql = "insert into TMSTEVENT_TRM (EVENT_ID,EVENT_TIME,EVENT_SOURCE,"
				+ " EVENT_SEVERITY,EVENT_MSG) values(?,?,?,?,?) ";

		doBatchInsert(sql, EventTrm.ID_SEQUENCE_NAME, objects, (st, object, relId) -> {
			st.setLong(1, relId);
			st.setTimestamp(2, new Timestamp(form.getRequestTime().getTime()), UTCTime.UTC_CLENDAR);
			st.setString(3, (String) object);
			st.setInt(4, EventTrm.INFO);
			if (StringUtils.isEmpty(form.getAdditionMessage())) {
				st.setString(5, form.getMessage() + " " + object);
			} else {
				st.setString(5, form.getMessage() + " " + object + form.getAdditionMessage());
			}

		});

	}

	@Override
	public List<EventTrm> getEventsBySource(String source) {
		String hql = "select eventTrm from EventTrm eventTrm where eventTrm.source=:source";
		return createQuery(hql, EventTrm.class).setParameter("source", source).getResultList();
	}

	@Override
	public int deleteEventTrmByTime(Date eventTime) {
		String hql = "delete from EventTrm where time<=:eventTime";
		return createQuery(hql).setParameter("eventTime", eventTime).executeUpdate();
	}
}
