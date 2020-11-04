/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description: get TerminalUsageStatistics
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
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.tms.monitor.model.TerminalUsageMessage;
import com.pax.tms.monitor.web.form.QueryStatisticsForm;

@Repository("terminalUsageStatisticsDaoImpl")
public class TerminalUsageStatisticsDaoImpl extends BaseHibernateDao<TerminalUsageMessage, Long>
		implements TerminalUsageStatisticsDao {
	private static final String GROUP_ID = "groupId";
	private static final String TERMINAL_ID = "terminalId";
	private static final String USAGE_NAME = "usageName";
	private static final String TERMINAL_MODEL = "terminalModel";
	private static final String TERMINAL_SN = "terminalSN";

	@SuppressWarnings("deprecation")
	@Override
	public <T, S extends Serializable> List<T> page(S command, int start, int length) {
		QueryStatisticsForm form = (QueryStatisticsForm) command;
		String filter = form.getFuzzyCondition();
		StringBuilder sql = null;
		if (form.getTerminalId() == null) {
			sql = new StringBuilder(" SELECT G.GROUP_ID AS GROUPID, M.TRM_ID AS TERMINALID,T.TRM_SN AS TERMINALSN, "
					+ "M.ITEM_NAME AS USAGENAME, M.START_TIME AS STARTTIME, M.END_TIME AS ENDTIME,"
					+ "M.ITEM_ERRS AS ERRORCOUNT,M.ITEM_TOTS AS TOTALCOUNT,"
					+ " Y.MODEL_NAME AS TERMINALMODEL  FROM TMSTTRM_USAGE_MSG M					"
					+ " INNER JOIN TMSTTERMINAL T ON T.TRM_ID=M.TRM_ID 					"
					+ " INNER JOIN TMSTTRM_GROUP G ON G.TRM_ID=T.TRM_ID 					"
					+ " INNER JOIN TMSTMODEL Y ON Y.MODEL_ID=T.MODEL_ID  WHERE G.GROUP_ID=:groupId ");
		} else {
			sql = new StringBuilder(" SELECT G.GROUP_ID AS GROUPID, M.TRM_ID AS TERMINALID,T.TRM_SN AS TERMINALSN, "
					+ "M.ITEM_NAME AS USAGENAME, M.START_TIME AS STARTTIME, M.END_TIME AS ENDTIME,"
					+ "M.ITEM_ERRS AS ERRORCOUNT,M.ITEM_TOTS AS TOTALCOUNT,"
					+ " Y.MODEL_NAME AS TERMINALMODEL  FROM TMSTTRM_USAGE_MSG M	"
					+ " INNER JOIN TMSTTERMINAL T ON T.TRM_ID=M.TRM_ID "
					+ " INNER JOIN TMSTTRM_GROUP G ON G.TRM_ID=T.TRM_ID "
					+ " INNER JOIN TMSTMODEL Y ON Y.MODEL_ID=T.MODEL_ID "
					+ " WHERE G.GROUP_ID=:groupId AND T.TRM_SN=:terminalSN");
		}
		if (StringUtils.isNotEmpty(filter)) {
			sql.append(" AND (LOWER(M.ITEM_NAME) like :usageName ESCAPE '!'							"
					+ " OR M.TRM_ID like :terminalId 												"
					+ " OR T.TRM_SN like :terminalSN 											"
					+ " OR LOWER(Y.MODEL_NAME) like :terminalModel ESCAPE'!')");
		}
		sql.append(" ORDER BY M.START_TIME DESC");
		@SuppressWarnings("unchecked")
		NativeQuery<T> query = super.createNativeQuery(new String(sql));
		if (form.getTerminalId() == null) {
			query.setParameter(GROUP_ID, form.getGroupId());
		} else {
			query.setParameter(GROUP_ID, form.getGroupId()).setParameter(TERMINAL_SN, form.getTerminalId());
		}
		if (StringUtils.isNotEmpty(filter)) {
			query.setParameter(USAGE_NAME, toMatchString(filter, MatchMode.ANYWHERE, true));
			query.setParameter(TERMINAL_ID, toMatchString(filter, MatchMode.ANYWHERE, true));
			query.setParameter(TERMINAL_SN, toMatchString(filter, MatchMode.ANYWHERE, true));
			query.setParameter(TERMINAL_MODEL, toMatchString(filter, MatchMode.ANYWHERE, true));
		}
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return query.setFirstResult(start).setMaxResults(length).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <S extends Serializable> long count(S command) {
		QueryStatisticsForm form = (QueryStatisticsForm) command;
		String filter = form.getFuzzyCondition();
		StringBuilder sql = null;
		if (form.getTerminalId() == null) {
			sql = new StringBuilder(
					" SELECT count(*) as totals 	                      				" + " FROM TMSTTRM_USAGE_MSG M "
							+ " INNER JOIN TMSTTERMINAL T ON T.TRM_ID=M.TRM_ID  						"
							+ " INNER JOIN TMSTTRM_GROUP G ON G.TRM_ID=T.TRM_ID  	"
							+ " INNER JOIN TMSTMODEL Y ON Y.MODEL_ID=T.MODEL_ID						"
							+ " WHERE G.GROUP_ID=:groupId");
		} else {
			sql = new StringBuilder(" SELECT count(*) as totals 								"
					+ " FROM TMSTTRM_USAGE_MSG M 												"
					+ " INNER JOIN TMSTTERMINAL T ON T.TRM_ID=M.TRM_ID "
					+ " INNER JOIN TMSTTRM_GROUP G ON G.TRM_ID=T.TRM_ID  	"
					+ " INNER JOIN TMSTMODEL Y ON Y.MODEL_ID=T.MODEL_ID						"
					+ " WHERE G.GROUP_ID=:groupId AND T.TRM_SN=:terminalSN");
		}
		if (StringUtils.isNotEmpty(filter)) {
			sql.append(" AND (LOWER(M.ITEM_NAME) like :usageName ESCAPE '!'							"
					+ " OR M.TRM_ID like :terminalId 												"
					+ " OR T.TRM_SN like :terminalSN 											"
					+ " OR LOWER(Y.MODEL_NAME) like :terminalModel ESCAPE'!')");
		}

		NativeQuery<Number> query = super.createNativeQuery(new String(sql));
		if (form.getTerminalId() == null) {
			query.setParameter(GROUP_ID, form.getGroupId());
		} else {
			query.setParameter(GROUP_ID, form.getGroupId()).setParameter(TERMINAL_SN, form.getTerminalId());
		}
		if (StringUtils.isNotEmpty(filter)) {
			query.setParameter(USAGE_NAME, toMatchString(filter, MatchMode.ANYWHERE, true));
			query.setParameter(TERMINAL_ID, toMatchString(filter, MatchMode.ANYWHERE, true));
			query.setParameter(TERMINAL_SN, toMatchString(filter, MatchMode.ANYWHERE, true));
			query.setParameter(TERMINAL_MODEL, toMatchString(filter, MatchMode.ANYWHERE, true));
		}
		Number obj = query.getSingleResult();
		return obj.longValue();
	}
}
