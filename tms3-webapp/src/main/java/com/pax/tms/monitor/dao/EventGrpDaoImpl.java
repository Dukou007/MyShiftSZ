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
import com.pax.tms.monitor.model.EventGrp;
import com.pax.tms.monitor.web.form.OperatorEventForm;
import com.pax.tms.user.security.UTCTime;

@Repository("eventGrpDaoImpl")
public class EventGrpDaoImpl extends BaseHibernateDao<EventGrp, Long> implements EventGrpDao {
	@Override
	public void addEventLog(Collection<String> objects, OperatorEventForm form) {

		String sql = "insert into TMSTEVENT_GRP (EVENT_ID,EVENT_TIME,EVENT_SOURCE,"
				+ " EVENT_SEVERITY,EVENT_MSG) values(?,?,?,?,?) ";

		doBatchInsert(sql, EventGrp.ID_SEQUENCE_NAME, objects, (st, object, relId) -> {
			st.setLong(1, relId);
			st.setTimestamp(2, new Timestamp(form.getRequestTime().getTime()), UTCTime.UTC_CLENDAR);
			st.setString(3, (String) object);
			st.setInt(4, EventGrp.INFO);
			if (StringUtils.isEmpty(form.getAdditionMessage())) {
				st.setString(5, form.getMessage() + ":" + object);
			} else {
				st.setString(5, form.getMessage() + ":" + object + form.getAdditionMessage());
			}

		});

	}

	@Override
	public List<EventGrp> getEventsBySource(String source) {
		String hql = "select eventGrp from EventGrp eventGrp where eventGrp.source=:source";
		return createQuery(hql, EventGrp.class).setParameter("source", source).getResultList();
	}

	@Override
	public int deleteEventGrpByTime(Date eventTime) {
		String sql = "DELETE FROM TMSTEVENT_GRP WHERE EVENT_TIME<=:eventTime";
		return createNativeQuery(sql).setParameter("eventTime", eventTime).executeUpdate();
	}

}
