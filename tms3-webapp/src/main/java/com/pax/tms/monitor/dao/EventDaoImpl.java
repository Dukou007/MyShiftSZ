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

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.MatchMode;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.tms.monitor.model.EventGrp;
import com.pax.tms.monitor.web.form.QueryTerminalEventForm;

@Repository("eventDaoImpl")
public class EventDaoImpl extends BaseHibernateDao<EventGrp, Long> implements EventDao {
	private static final String EVENT_SOURCE = "eventSource";
	private static final String EVENT_MSG = "eventMsg";
	private static final String TERMINAL_ID = "terminalId";
	private static final String GROUP_ID = "groupId";
	private static final String EVENT_ID = "eventId";
	private static final String EVENT_TIME = "eventTime";
	private static final String END_TIME = "endTime";
	private static final String START_TIME = "startTime";

	@SuppressWarnings("unused")
	private int maxPageNumber = 100;

	@Override
	public <T, S extends Serializable> List<T> page(S command, int start, int length) {
		QueryTerminalEventForm form = (QueryTerminalEventForm) command;
		List<T> result = null;
		if (StringUtils.isNotEmpty(form.getTerminalId())) {
			result = this.getOneTerminalPage(form, start, length);
		} else {
			if ("Group".equals(form.getSearchType())) {
				result = this.getGroupPage(form, start, length);
			} else if ("Terminal".equals(form.getSearchType())) {
				result = this.getTerminalPage(form, start, length);
			}
		}
		return result;
	}

	@Override
	public <S extends Serializable> long count(S command) {
		QueryTerminalEventForm form = (QueryTerminalEventForm) command;
		if (StringUtils.isNotEmpty(form.getTerminalId())) {
			return this.getOneTerminalCount(form);
		} else {
			if ("Group".equals(form.getSearchType())) {
				return this.getGroupCount(form);
			} else if ("Terminal".equals(form.getSearchType())) {
				return this.getTerminalCount(form);
			} else {
				return this.getAllCount(form);
			}
		}
	}

	/* ******************Query group event****************** */
	@SuppressWarnings("unchecked")
	public <T> List<T> getGroupPage(QueryTerminalEventForm command, int start, int length) {
		NativeQuery<T> query = this.getGroupQuery(command, false);
		query.setFirstResult(start).setMaxResults(length);
		return super.mapResult(query).getResultList();
	}

	public long getGroupCount(QueryTerminalEventForm command) {
		String pageStatus = command.getPaginationStatus();
		if ("lastPage".equals(pageStatus)) {
			NativeQuery<Number> query = this.getGroupQuery(command, true);
			return query.getResultList().size();
		} else if ("firstPage".equals(pageStatus)){
			NativeQuery<Number> query = this.getGroupQuery(command, true);
			//query.setFirstResult(0).setMaxResults(command.getPageSize() * maxPageNumber+1);
			return query.getResultList().size();
		} else {
			return command.getTotalCount();
		}
	}

	@SuppressWarnings("unchecked")
	private <T> NativeQuery<T> getGroupQuery(QueryTerminalEventForm command, boolean isCount) {
		String filter = command.getFuzzyCondition();
		String selectColumns = "SELECT G.NAME_PATH AS NAMEPATH,EV.EVENT_ID AS EVENTID, EV.EVENT_TIME AS EVENTTIME, "
				+ " EV.EVENT_SEVERITY AS EVENTSEVERITY, EV.EVENT_MSG AS EVENTMSG, G.GROUP_NAME AS EVENTSOURCE,"
				+ "G.GROUP_ID as GROUPID,'GROUP' as TYPE	";
		if (isCount) {
			selectColumns = " SELECT 1 AS TOTALS ";
		}
		StringBuilder sql = new StringBuilder(selectColumns + " FROM TMSTEVENT_GRP EV ");
		sql.append(" INNER JOIN PUBTGROUP G ON EV.EVENT_SOURCE=G.GROUP_ID "
				+ " INNER JOIN PUBTGROUP_PARENTS GP ON GP.GROUP_ID=G.GROUP_ID ");
		sql.append(" WHERE GP.PARENT_ID=:groupId ");
		sql.append(" AND EV.EVENT_TIME >=:startTime AND EV.EVENT_TIME <=:endTime ");
		if (StringUtils.isNotEmpty(filter)) {
			sql.append(" AND (LOWER(G.GROUP_NAME) like :eventSource ESCAPE '!' "
					+ " OR LOWER(EV.EVENT_MSG) like :eventMsg ESCAPE'!' ) ");
		}
		if (!isCount) {
			sql.append(" ORDER BY EV.EVENT_TIME DESC,EV.EVENT_ID DESC ");
		}
		NativeQuery<T> query = super.createNativeQuery(sql.toString());
		query.setParameter(GROUP_ID, command.getGroupId()).setParameter("startTime", command.getFromDate())
				.setParameter(END_TIME, command.getToDate());
		if (StringUtils.isNotEmpty(filter)) {
			query.setParameter(EVENT_SOURCE, toMatchString(filter, MatchMode.ANYWHERE, true));
			query.setParameter(EVENT_MSG, toMatchString(filter, MatchMode.ANYWHERE, true));
		}
		return query;
	}
	/* ******************Query group event****************** */

	/* ******************Query Terminal event****************** */
	@SuppressWarnings("unchecked")
	public <T> List<T> getTerminalPage(QueryTerminalEventForm command, int start, int length) {
		NativeQuery<T> query = this.getTerminalQuery(command, false);
		query.setFirstResult(start).setMaxResults(length);
		return super.mapResult(query).getResultList();
	}

	public long getTerminalCount(QueryTerminalEventForm command) {
		String pageStatus = command.getPaginationStatus();
		if ("lastPage".equals(pageStatus)) {
			NativeQuery<Number> query = this.getTerminalQuery(command, true);
			return query.getResultList().size();
		} else if ("firstPage".equals(pageStatus)){
			NativeQuery<Number> query = this.getTerminalQuery(command, true);
			//query.setFirstResult(0).setMaxResults(command.getPageSize() * maxPageNumber+1);
			return query.getResultList().size();
		} else {
			return command.getTotalCount();
		}
	}

	@SuppressWarnings("unchecked")
	private <T> NativeQuery<T> getTerminalQuery(QueryTerminalEventForm command, boolean isCount) {
		String filter = command.getFuzzyCondition();
		String selectColumns = "SELECT EV.EVENT_ID as EVENTID,EV.EVENT_TIME as EVENTTIME, "
				+ "EV.EVENT_SOURCE as EVENTSOURCE, EV.EVENT_SEVERITY as EVENTSEVERITY, EV.EVENT_MSG as EVENTMSG,"
				+ "'TERMINAL' as TYPE";
		if (isCount) {
			selectColumns = "SELECT 1 AS TOTALS ";
		}
		StringBuilder sql = new StringBuilder(
				selectColumns + " FROM TMSTTRM_GROUP TG INNER JOIN TMSTEVENT_TRM EV ON EV.EVENT_SOURCE=TG.TRM_ID ");
		sql.append(" WHERE TG.GROUP_ID=:groupId AND EVENT_TIME >=:startTime AND EVENT_TIME <=:endTime ");
		if (StringUtils.isNotEmpty(filter)) {
			sql.append(" AND (LOWER(EVENT_SOURCE) like :eventSource ESCAPE '!'"
					+ " OR LOWER(EVENT_MSG) like :eventMsg ESCAPE'!' ) ");
		}
		if (!isCount) {
			sql.append(" ORDER BY EV.EVENT_TIME DESC,EV.EVENT_ID DESC");
		}
		NativeQuery<T> query = super.createNativeQuery(sql.toString());
		query.setParameter(GROUP_ID, command.getGroupId());
		query.setParameter(START_TIME, command.getFromDate());
		query.setParameter(END_TIME, command.getToDate());
		if (StringUtils.isNotEmpty(filter)) {
			query.setParameter(EVENT_SOURCE, toMatchString(filter, MatchMode.ANYWHERE, true));
			query.setParameter(EVENT_MSG, toMatchString(filter, MatchMode.ANYWHERE, true));
		}
		return query;
	}
	/* ******************Query Terminal event****************** */

	/* ******************Query One Terminal event****************** */
	@SuppressWarnings("unchecked")
	public <T> List<T> getOneTerminalPage(QueryTerminalEventForm command, int start, int length) {
		NativeQuery<T> query = this.getOneTerminalQuery(command, false);
		query.setFirstResult(start).setMaxResults(length);
		return super.mapResult(query).getResultList();
	}

	public long getOneTerminalCount(QueryTerminalEventForm command) {
		NativeQuery<Number> query = this.getOneTerminalQuery(command, true);
		return query.getSingleResult().longValue();
	}

	@SuppressWarnings("unchecked")
	private <T> NativeQuery<T> getOneTerminalQuery(QueryTerminalEventForm command, boolean isCount) {
		String filter = command.getFuzzyCondition();
		String selectColumns = "SELECT EV.EVENT_ID as EVENTID, EV.EVENT_TIME as EVENTTIME, "
				+ " EV.EVENT_SOURCE as EVENTSOURCE, EV.EVENT_SEVERITY as EVENTSEVERITY, EV.EVENT_MSG as EVENTMSG ,"
				+ "'TERMINAL' as TYPE";
		if (isCount) {
			selectColumns = "SELECT COUNT(EV.EVENT_SOURCE) AS TOTALS ";
		}
		StringBuilder sql = new StringBuilder(selectColumns + " FROM TMSTEVENT_TRM EV ");
		sql.append(" WHERE EV.EVENT_SOURCE=:terminalId AND EVENT_TIME >=:startTime AND EVENT_TIME <=:endTime ");
		if (StringUtils.isNotEmpty(filter)) {
			sql.append(" AND (LOWER(EVENT_SOURCE) like :eventSource ESCAPE '!' ");
			sql.append(" OR LOWER(EVENT_MSG) like :eventMsg ESCAPE'!' ) ");
		}
		if (!isCount) {
			sql.append(" ORDER BY EVENT_TIME DESC");
		}
		NativeQuery<T> query = super.createNativeQuery(sql.toString());
		query.setParameter(TERMINAL_ID, command.getTerminalId());
		query.setParameter(START_TIME, command.getFromDate());
		query.setParameter(END_TIME, command.getToDate());
		if (StringUtils.isNotEmpty(filter)) {
			query.setParameter(EVENT_SOURCE, toMatchString(filter, MatchMode.ANYWHERE, true));
			query.setParameter(EVENT_MSG, toMatchString(filter, MatchMode.ANYWHERE, true));
		}
		return query;
	}
	/* ******************Query One Terminal event****************** */

	/* ******************Query ALL event****************** */
	@Override
	public long getAllCount(QueryTerminalEventForm command) {
		String pageStatus = command.getPaginationStatus();
		if ("firstPage".equals(pageStatus)) {
			NativeQuery<Number> query1 = this.getGroupQuery(command, true);
			NativeQuery<Number> query2 = this.getTerminalQuery(command, true);
			//query1.setFirstResult(0).setMaxResults(command.getPageSize() * maxPageNumber+1);
			//query2.setFirstResult(0).setMaxResults(command.getPageSize() * maxPageNumber+1);
			return query1.getResultList().size() + query2.getResultList().size();
		} else if ("lastPage".equals(pageStatus)) {
			NativeQuery<Number> query1 = this.getGroupQuery(command, true);
			NativeQuery<Number> query2 = this.getTerminalQuery(command, true);
			return query1.getResultList().size() + query2.getResultList().size();
		}
		return command.getTotalCount();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getAllGroupPage(QueryTerminalEventForm command, int start, int length) {
		String pageStatus = command.getPaginationStatus();
		String filter = command.getFuzzyCondition();
		StringBuilder sql = new StringBuilder(
				" SELECT EV.EVENT_ID AS EVENTID, EV.EVENT_TIME AS EVENTTIME, G.GROUP_NAME AS EVENTSOURCE,G.NAME_PATH AS NAMEPATH,"
						+ "G.GROUP_ID AS GROUPID, EV.EVENT_SEVERITY AS EVENTSEVERITY, EV.EVENT_MSG AS EVENTMSG, 'GROUP' AS TYPE			"
						+ " FROM TMSTEVENT_GRP EV INNER JOIN PUBTGROUP G ON EV.EVENT_SOURCE=G.GROUP_ID				"
						+ " INNER JOIN PUBTGROUP_PARENTS GP ON GP.GROUP_ID=G.GROUP_ID								"
						+ " WHERE GP.PARENT_ID=:groupId																"
						+ " AND EV.EVENT_TIME >=:startTime AND EV.EVENT_TIME <=:endTime");
		if (StringUtils.isNotEmpty(filter)) {
			sql.append(" AND (LOWER(G.GROUP_NAME) like :eventSource ESCAPE '!' "
					+ " OR LOWER(EV.EVENT_MSG) like :eventMsg ESCAPE'!' ) ");
		}

		boolean isMidPage = false;
		if ("lastPage".equals(pageStatus)) {
			sql.append(" ORDER BY  EV.EVENT_TIME ASC,EV.EVENT_ID ASC");
		} else if ("nextPage".equals(pageStatus)) {
			isMidPage = true;
			sql.append(" AND (EV.EVENT_TIME <:eventTime OR (EV.EVENT_TIME=:eventTime AND EV.EVENT_ID <:eventId))");
			sql.append(" ORDER BY EV.EVENT_TIME DESC, EV.EVENT_ID DESC");
		} else if ("previousPage".equals(pageStatus)) {
			isMidPage = true;
			sql.append(" AND (EV.EVENT_TIME >:eventTime OR (EV.EVENT_TIME=:eventTime AND EV.EVENT_ID >:eventId))");
			sql.append(" ORDER BY EV.EVENT_TIME ASC, EV.EVENT_ID ASC");
		} else if ("firstPage".equals(pageStatus)) {
			sql.append(" ORDER BY EV.EVENT_TIME DESC,EV.EVENT_ID DESC");
		} else {
			sql.append(" ORDER BY EV.EVENT_TIME DESC,EV.EVENT_ID DESC");
		}
		NativeQuery<Map<String, Object>> query = super.createNativeQuery(sql.toString());
		query.setParameter(GROUP_ID, command.getGroupId());
		query.setParameter(START_TIME, command.getFromDate());
		query.setParameter(END_TIME, command.getToDate());
		if (StringUtils.isNotEmpty(filter)) {
			query.setParameter(EVENT_SOURCE, toMatchString(filter, MatchMode.ANYWHERE, true));
			query.setParameter(EVENT_MSG, toMatchString(filter, MatchMode.ANYWHERE, true));

		}
		if (isMidPage) {
			query.setParameter(EVENT_TIME, command.getEventTime());
			query.setParameter(EVENT_ID, command.getEventId());
		}
		query.setFirstResult(start).setMaxResults(length);
		return super.mapResult(query).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getAllTerminalPage(QueryTerminalEventForm command, int start, int length) {
		String pageStatus = command.getPaginationStatus();
		String filter = command.getFuzzyCondition();
		StringBuilder sql = new StringBuilder(
				"SELECT EV.EVENT_ID as EVENTID,EV.EVENT_TIME as EVENTTIME,EV.EVENT_SOURCE as EVENTSOURCE,"
						+ " EV.EVENT_SEVERITY as EVENTSEVERITY, EV.EVENT_MSG as EVENTMSG, 'TERMINAL' AS TYPE "
						+ " FROM TMSTEVENT_TRM EV INNER JOIN TMSTTRM_GROUP TG ON EV.EVENT_SOURCE=TG.TRM_ID "
						+ " WHERE TG.GROUP_ID=:groupId AND EV.EVENT_TIME >=:startTime AND EV.EVENT_TIME <=:endTime ");
		if (StringUtils.isNotEmpty(filter)) {
			sql.append(" AND (LOWER(EVENT_SOURCE) like :eventSource ESCAPE '!' ");
			sql.append(" OR LOWER(EVENT_MSG) like :eventMsg ESCAPE'!' ) ");
		}

		boolean isMidPage = false;
		if ("lastPage".equals(pageStatus)) {
			sql.append(" ORDER BY EV.EVENT_TIME ASC, EV.EVENT_ID ASC");
		} else if ("nextPage".equals(pageStatus)) {
			isMidPage = true;
			sql.append(" AND (EV.EVENT_TIME <:eventTime OR (EV.EVENT_TIME=:eventTime AND EV.EVENT_ID <:eventId)) ");
			sql.append(" ORDER BY EV.EVENT_TIME DESC, EV.EVENT_ID DESC");
		} else if ("previousPage".equals(pageStatus)) {
			isMidPage = true;
			sql.append(" AND (EV.EVENT_TIME >:eventTime OR (EV.EVENT_TIME=:eventTime AND EV.EVENT_ID >:eventId ))");
			sql.append(" ORDER BY EV.EVENT_TIME ASC, EV.EVENT_ID ASC");
		} else if ("firstPage".equals(pageStatus)) {
			sql.append(" ORDER BY EV.EVENT_TIME DESC, EV.EVENT_ID DESC");
		} else {
			sql.append(" ORDER BY EV.EVENT_TIME DESC, EV.EVENT_ID DESC");
		}
		NativeQuery<Map<String, Object>> query = super.createNativeQuery(sql.toString());
		query.setParameter(GROUP_ID, command.getGroupId());
		query.setParameter(START_TIME, command.getFromDate());
		query.setParameter(END_TIME, command.getToDate());
		if (StringUtils.isNotEmpty(filter)) {
			query.setParameter(EVENT_SOURCE, toMatchString(filter, MatchMode.ANYWHERE, true));
			query.setParameter(EVENT_MSG, toMatchString(filter, MatchMode.ANYWHERE, true));
		}
		if (isMidPage) {
			query.setParameter(EVENT_ID, command.getEventId());
			query.setParameter(EVENT_TIME, command.getEventTime());
		}
		query.setFirstResult(start).setMaxResults(length);
		return super.mapResult(query).getResultList();
	}

	/* ******************Query ALL event****************** */
	@Autowired
	public void setMaxPageNumber(@Value("${tms.monitor.event.maxPageNumber:100}") int maxPageNumber) {
		this.maxPageNumber = maxPageNumber;
	}
}
