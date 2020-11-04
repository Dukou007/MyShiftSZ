/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description: get UsageMessageInfo/TerminalRealStatusInfo/UsageStatusBar/TerminalRealStatus
 * Revision History:		
 * Date	                 Author	                Action
 * 20170111  	         Frank           	    Modify 
 * ============================================================================		
 */
package com.pax.tms.monitor.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.query.NativeQuery;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;
import org.springframework.stereotype.Repository;

import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.common.dao.hibernate.MyAliasToBeanResultTransformer;
import com.pax.tms.monitor.domain.TerminalRealStatusInfo;
import com.pax.tms.monitor.domain.UsageMessageInfo;
import com.pax.tms.monitor.model.TerminalReportMessage;
import com.pax.tms.monitor.web.AlertConstants;

@Repository("terminalStatusDaoImpl")
public class TerminalStatusDaoImpl extends BaseHibernateDao<TerminalReportMessage, String>
		implements TerminalStatusDao {

	private ResultTransformer usageMessageTransformer = new MyAliasToBeanResultTransformer(UsageMessageInfo.class);

	private static final String TERMINAL_ID = "terminalId";
	private static final String ITEM_NAME = "itemName";
	private static final String START_TIME = "startTime";
	private static final String END_TIME = "endTime";
	private static final String ITEM_ERRORS = "itemErrors";
	private static final String ITEM_TOTALS = "itemTotals";
	private static final String MSG_CYCLE = "msgCycle";
	private static final String CREATE_DATE = "createDate";

	@Override
	public UsageMessageInfo getUsageStatus(String terminalId, String itemName) {
		String sql = "SELECT T.START_TIME AS STARTTIME,T.END_TIME AS ENDTIME,T.TRM_ID AS TERMINALID,"
				+ "T.ITEM_NAME AS ITEMNAME,T.MSG_CYCLE AS MSGCYCLE,T.CREATE_DATE AS CREATEDATE,"
				+ "T.ITEM_ERRS AS ITEMERRORS,T.ITEM_TOTS AS ITEMTOTALS FROM TMSTTRM_USAGE_MSG T WHERE "
				+ "T.TRM_ID=:terminalId AND T.ITEM_NAME=:itemName ORDER BY T.END_TIME ";

		NativeQuery<UsageMessageInfo> query = super.createNativeQuery(sql, UsageMessageInfo.class);
		query.setParameter(TERMINAL_ID, terminalId).setParameter(ITEM_NAME, itemName);

		super.addScalar(query, TERMINAL_ID, StringType.INSTANCE);
		super.addScalar(query, ITEM_ERRORS, IntegerType.INSTANCE);
		super.addScalar(query, ITEM_TOTALS, IntegerType.INSTANCE);
		super.addScalar(query, ITEM_NAME, StringType.INSTANCE);
		super.addScalar(query, START_TIME, TimestampType.INSTANCE);
		super.addScalar(query, END_TIME, TimestampType.INSTANCE);
		super.addScalar(query, CREATE_DATE, TimestampType.INSTANCE);
		super.addScalar(query, MSG_CYCLE, StringType.INSTANCE);

		super.setResultTransformer(query, usageMessageTransformer);
		return super.uniqueResult(query.setMaxResults(1));
	}

	@Override
	public List<UsageMessageInfo> getUsageStatusBar(String terminalId, String itemName) {
		String sql = "select T.START_TIME as startTime,T.END_TIME as endTime,T.TRM_ID as terminalId,"
				+ " T.ITEM_NAME as itemName,T.MSG_CYCLE as msgCycle,T.CREATE_DATE as createDate,"
				+ " T.ITEM_ERRS as itemErrors,T.ITEM_TOTS as itemTotals "
				+ " from TMSTTRM_USAGE_MSG T where  T.TRM_ID=:terminalId and T.ITEM_NAME=:itemName "
				+ " order by T.END_TIME";

		NativeQuery<UsageMessageInfo> query = super.createNativeQuery(sql, UsageMessageInfo.class);
		query.setParameter(TERMINAL_ID, terminalId).setParameter(ITEM_NAME, itemName);

		super.addScalar(query, TERMINAL_ID, StringType.INSTANCE);
		super.addScalar(query, ITEM_ERRORS, IntegerType.INSTANCE);
		super.addScalar(query, ITEM_TOTALS, IntegerType.INSTANCE);
		super.addScalar(query, ITEM_NAME, StringType.INSTANCE);
		super.addScalar(query, START_TIME, TimestampType.INSTANCE);
		super.addScalar(query, END_TIME, TimestampType.INSTANCE);
		super.addScalar(query, CREATE_DATE, TimestampType.INSTANCE);
		super.addScalar(query, MSG_CYCLE, StringType.INSTANCE);

		super.setResultTransformer(query, usageMessageTransformer);
		return query.setMaxResults(5).getResultList();
	}

	@Override
	public TerminalRealStatusInfo getRealStatus(String terminalId) {
		String sql = "SELECT t.trm_id AS terminalId, td.deploy_time AS deployTime, ts.tamper AS tamper, 	"
				+ " TS.IS_ONLINE AS onlineSts, ts.privacy_shield AS privacyShieldSts, ts.stylus AS stylusSts, 	"
				+ " td.DWNL_STATUS AS downloadSts, td.ACTV_STATUS AS activateSts							"
				+ " FROM TMSTTERMINAL T																		"
				+ " LEFT JOIN TMSTTRM_DEPLOY TD ON T.TRM_ID=TD.TRM_ID										"
				+ " LEFT JOIN TMSTTRMSTATUS TS ON T.TRM_ID=TS.TRM_ID										"
				+ " WHERE T.TRM_ID=:terminalId																"
				+ " ORDER BY DEPLOY_TIME DESC																";
		NativeQuery<TerminalRealStatusInfo> query = super.createNativeQuery(sql, TerminalRealStatusInfo.class, false);
		query.setParameter(TERMINAL_ID, terminalId).setMaxResults(1);
		return super.uniqueResult(query);
	}

	@Override
	public List<Object[]> getTerminalRealStatus(String terminalId, String itemName, Date dayStart) {
		String sql = "";
		String fromSql = "FROM TMSTTERMINAL T LEFT JOIN TMSTTRMSTATUS TS ON T.TRM_ID=TS.TRM_ID "
				+ " WHERE T.TRM_ID=:terminalId ";
		switch (itemName) {
		case AlertConstants.TAMPERS:
			sql = " SELECT TS.TAMPER as itemStatus, count(1) as itemCount " + fromSql + " GROUP BY TS.TAMPER";
			break;
		case AlertConstants.OFFLINE:
			sql = " SELECT TS.IS_ONLINE as itemStatus, count(1) as itemCount " + fromSql + " GROUP BY TS.IS_ONLINE";
			break;
		case AlertConstants.PRIVACY_SHIELD:
			sql = " SELECT TS.PRIVACY_SHIELD as itemStatus, count(1) as itemCount " + fromSql
					+ " GROUP BY TS.PRIVACY_SHIELD";
			break;
		case AlertConstants.STYLUS:
			sql = " SELECT TS.STYLUS as itemStatus, count(1) as itemCount " + fromSql + " GROUP BY TS.STYLUS";
			break;
		case AlertConstants.SRED:
            sql = " SELECT TS.SRED as itemStatus, count(1) as itemCount " + fromSql + " GROUP BY TS.SRED";
            break;
		case AlertConstants.RKI:
            sql = " SELECT TS.RKI as itemStatus, count(1) as itemCount " + fromSql + " GROUP BY TS.RKI";
            break;
		case AlertConstants.DOWNLOADS:
			sql = " SELECT DWNL_STATUS as itemStatus, COUNT(1) as itemCount	 FROM TMSTTRMDWNL TD		"
					+ " WHERE TD.TRM_ID=:terminalId														"
					+ " AND (TD.DWNL_STATUS='DOWNLOADING' OR (TD.DWNL_END_TIME > :startTime OR "
					+ "(((TD.EXPIRE_DATE >:nowDate OR TD.EXPIRE_DATE is NULL) AND TD.DWNL_END_TIME is NULL) "
					+ "AND TD.DWNL_STATUS='PENDING')))" + " GROUP BY DWNL_STATUS";
			break;
		case AlertConstants.ACTIVATIONS:
			sql = " SELECT ACTV_STATUS as itemStatus, COUNT(1) as itemCount	 FROM TMSTTRMDWNL TD		"
					+ " WHERE TD.TRM_ID=:terminalId														"
					+ " AND((TD.DWNL_STATUS = 'DOWNLOADING' OR TD.DWNL_STATUS = 'SUCCESS') AND    "
					+ " (TD.ACTV_TIME > :startTime OR TD.ACTV_TIME IS NULL ) OR (TD.DWNL_status = 'PENDING' AND  "
					+ "(TD.EXPIRE_DATE > :nowDate OR TD.EXPIRE_DATE IS NULL))) GROUP BY ACTV_STATUS";
			break;
		}

		NativeQuery<Object[]> query = super.createNativeQuery(sql, Object[].class);
		query.setParameter(TERMINAL_ID, terminalId);
		if (AlertConstants.DOWNLOADS.equals(itemName) || AlertConstants.ACTIVATIONS.equals(itemName)) {
			query.setParameter(START_TIME, dayStart).setParameter("nowDate", new Date());
		}
		return query.getResultList();
	}
}
