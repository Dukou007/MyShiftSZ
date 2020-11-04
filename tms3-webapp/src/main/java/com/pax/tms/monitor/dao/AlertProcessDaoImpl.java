/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description: Process Group Alert
 * Revision History:		
 * Date	                 Author	                Action
 * 20161214  	         Crazy.W           	    Create
 * ============================================================================
 */
package com.pax.tms.monitor.dao;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.TemporalType;

import org.hibernate.query.NativeQuery;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.springframework.stereotype.Repository;

import com.pax.common.dao.DbHelper;
import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.common.dao.hibernate.MyAliasToBeanResultTransformer;
import com.pax.common.util.DateTimeUtils;
import com.pax.tms.group.model.UsageThreshold;
import com.pax.tms.monitor.domain.ResultCount;
import com.pax.tms.monitor.domain.TimeZoneInfo;
import com.pax.tms.monitor.domain.UserSubscribeInfo;
import com.pax.tms.monitor.domain.UsageMessageInfo;
import com.pax.tms.monitor.domain.UsageStatusInfo;
import com.pax.tms.monitor.model.GroupUsageStatus;
import com.pax.tms.monitor.model.TerminalReportMessage;
import com.pax.tms.monitor.model.TerminalUsageMessage;
import com.pax.tms.monitor.web.AlertConstants;
import com.pax.tms.monitor.web.form.UsagePie;
import com.pax.tms.user.security.UTCTime;

@Repository("alertProcessDaoImpl")
public class AlertProcessDaoImpl extends BaseHibernateDao<TerminalReportMessage, Long> implements AlertProcessDao {

	private ResultTransformer usageMessageTransformer = new MyAliasToBeanResultTransformer(UsageMessageInfo.class);
	private ResultTransformer usageStatusTransformer = new MyAliasToBeanResultTransformer(UsageStatusInfo.class);
	private ResultTransformer resultCountTransformer = new MyAliasToBeanResultTransformer(ResultCount.class);

	private static final String GROUP_ID = "groupId";
	private static final String ITEM_NAME = "itemName";
	private static final String START_TIME = "startTime";
	private static final String END_TIME = "endTime";
	private static final String COND_ID = "condId";
	private static final String FULLNAME = "fullname";
	private static final String USER_NAME = "username";
	private static final String EMAIL = "email";
	private static final String USER_ID = "userId";
	private static final String TRM_ID = "terminalId";
	private static final String THD_ID = "thdId";
	private static final String REPORT_CYCLE = "reportCycle";

	@Override
	public boolean isGetUsageMessage(String itemName, Date startTime, Date endTime) {
		String sql = "SELECT 1 FROM TMSTTRM_USAGE_MSG WHERE ITEM_NAME=:itemName     "
				+ " AND START_TIME>=:startTime AND END_TIME<=:endTime ";

		@SuppressWarnings("rawtypes")
		NativeQuery query = super.createNativeQuery(sql).setParameter(ITEM_NAME, itemName)
				.setParameter(START_TIME, startTime).setParameter(END_TIME, endTime);
		return super.uniqueResult(query.setMaxResults(1)) != null ? true : false;
	}

	@Override
	public boolean isGetUsageStatus(UsageThreshold thd, Date startTime, Date endTime) {
		String sql = " SELECT 1 FROM TMSTTRM_USAGE_STS "
				+ " WHERE ITEM_NAME=:itemName AND THD_ID=:thdId AND REPORT_CYCLE=:reportCycle"
				+ " AND START_TIME>=:startTime AND END_TIME<=:endTime ";

		@SuppressWarnings("rawtypes")
		NativeQuery query = super.createNativeQuery(sql).setParameter(ITEM_NAME, thd.getItemName())
				.setParameter(THD_ID, thd.getId()).setParameter(REPORT_CYCLE, thd.getReportCycle())
				.setParameter(START_TIME, startTime).setParameter(END_TIME, endTime);
		return super.uniqueResult(query.setMaxResults(1)) != null ? true : false;
	}

	@Override
	public boolean isGetGroupUsageStatus(long groupId, String itemName, Date startTime, Date endTime,
			String reportCycle) {
		String sql = " SELECT 1 FROM TMSTGROUP_USAGE_STS "
				+ " WHERE ITEM_NAME=:itemName AND START_TIME>=:startTime AND END_TIME<=:endTime "
				+ " AND GROUP_ID=:groupId AND REPORT_CYCLE=:reportCycle";

		@SuppressWarnings("rawtypes")
		NativeQuery query = super.createNativeQuery(sql).setParameter(ITEM_NAME, itemName)
				.setParameter(REPORT_CYCLE, reportCycle).setParameter(START_TIME, startTime)
				.setParameter(END_TIME, endTime).setParameter(GROUP_ID, groupId);
		return super.uniqueResult(query.setMaxResults(1)) != null ? true : false;
	}

	@Override
	public List<UsageMessageInfo> getUsageMessageList(String itemName, Date startTime, Date endTime, String msgCycle) {
		String sumSql = "";
		switch (itemName) {
		case AlertConstants.MSR_READ:
			sumSql = "SUM(MSR_ERRS) AS itemErrors, SUM(MSR_TOTS) AS itemTotals,";
			break;
		case AlertConstants.CONTACT_IC_READ:
			sumSql = "SUM(ICR_ERRS) AS itemErrors, SUM(ICR_TOTS) AS itemTotals,";
			break;
		case AlertConstants.PIN_ENCRYPTION_FAILURE:
			sumSql = "SUM(PIN_FAILS) AS itemErrors, SUM(PIN_TOTS) AS itemTotals,";
			break;
		case AlertConstants.SIGNATURE:
			sumSql = "SUM(SIGN_ERRS) AS itemErrors, SUM(SIGN_TOTS) AS itemTotals,";
			break;
		case AlertConstants.DOWNLOAD_HISTORY:
			sumSql = "SUM(DOWN_FAILS) AS itemErrors, SUM(DOWN_TOTS) AS itemTotals,";
			break;
		case AlertConstants.ACTIVATION_HISTORY:
			sumSql = "SUM(ACTV_FAILS) AS itemErrors, SUM(ACTV_TOTS) AS itemTotals,";
			break;
		case AlertConstants.CONTACTLESS_IC_READ:
			sumSql = "SUM(CL_ICR_ERRS) AS itemErrors, SUM(CL_ICR_TOTS) AS itemTotals,";
			break;
		case AlertConstants.TRANSACTIONS:
			sumSql = "SUM(TXN_ERRS) AS itemErrors, SUM(TXN_TOTS) AS itemTotals,";
			break;
		case AlertConstants.POWER_CYCLES:
			sumSql = " SUM(0) AS itemErrors, SUM(POWER_NO) AS itemTotals,";
			break;
		}
		String queryReport = "SELECT TRM_ID as terminalId, " + sumSql + " :itemName as itemName "
				+ " FROM TMSTTRM_REPORT_MSG														"
				+ " WHERE REPORT_TM >= :startTime AND REPORT_TM<= :endTime GROUP BY TRM_ID		";

		NativeQuery<UsageMessageInfo> query = super.createNativeQuery(queryReport, UsageMessageInfo.class);
		query.setParameter(ITEM_NAME, itemName).setParameter(START_TIME, startTime, TemporalType.TIMESTAMP)
				.setParameter(END_TIME, endTime, TemporalType.TIMESTAMP);

		super.addScalar(query, TRM_ID, StringType.INSTANCE);
		super.addScalar(query, "itemErrors", IntegerType.INSTANCE);
		super.addScalar(query, "itemTotals", IntegerType.INSTANCE);
		super.addScalar(query, ITEM_NAME, StringType.INSTANCE);

		super.setResultTransformer(query, usageMessageTransformer);

		return query.getResultList();
	}

	@Override
	public void insertUsageMessage(List<UsageMessageInfo> usageList, Date startTime, Date endTime, String msgCycle) {
		Timestamp timestamp = new Timestamp(new Date().getTime());
		String insertUsage = "INSERT INTO TMSTTRM_USAGE_MSG (ID, TRM_ID, START_TIME, END_TIME, "
				+ "ITEM_NAME, ITEM_ERRS, ITEM_TOTS, MSG_CYCLE, CREATE_DATE) VALUES (?,?,?,?,?,?,?,?,?)";
		doWork(conn -> {
			try (PreparedStatement ps = conn.prepareStatement(insertUsage)) {
				Long startId = DbHelper.generateId(TerminalUsageMessage.ID_SEQUENCE_NAME, usageList.size());
				for (UsageMessageInfo entity : usageList) {
					if (entity.getItemTotals() != null) {
						ps.setLong(1, startId);
						ps.setString(2, entity.getTerminalId());
						ps.setTimestamp(3, new Timestamp(startTime.getTime()), UTCTime.UTC_CLENDAR);
						ps.setTimestamp(4, new Timestamp(endTime.getTime()), UTCTime.UTC_CLENDAR);
						ps.setString(5, entity.getItemName());
						ps.setInt(6, entity.getItemErrors() == null ? 0 : entity.getItemErrors());
						ps.setInt(7, entity.getItemTotals());
						ps.setString(8, msgCycle);
						ps.setTimestamp(9, timestamp, UTCTime.UTC_CLENDAR);
						ps.addBatch();
						if (startId % 100 == 0) {
							ps.executeBatch();
							ps.clearBatch();
						}
						startId = startId + 1L;
					}
				}
				if (startId % 100 != 0) {
					ps.executeBatch();
					ps.clearBatch();
				}
			}
		});
	}

	// 0 - normal, 1 - abnormal
	@Override
	public List<UsageStatusInfo> getUsageStatusList(UsageThreshold thd, Date startTime, Date endTime) {
		String rateSql = "SUM(U.ITEM_ERRS)*100/SUM(U.ITEM_TOTS) AS itemRate ";
		if (AlertConstants.POWER_CYCLES.equals(thd.getItemName())) {
			rateSql = "SUM(U.ITEM_TOTS) AS itemRate ";
		}
		String queryUsage = "SELECT TG.TRM_ID AS terminalId, " + rateSql
				+ "	FROM TMSTTRM_GROUP TG INNER JOIN TMSTTRM_USAGE_MSG U ON TG.TRM_ID=U.TRM_ID		"
				+ "	WHERE TG.GROUP_ID=:groupId														"
				+ "				AND U.START_TIME>=:startTime AND U.END_TIME<=:endTime				"
				+ "				AND U.ITEM_NAME=:itemName											"
				+ "	GROUP BY TG.TRM_ID																";

		NativeQuery<UsageStatusInfo> query = super.createNativeQuery(queryUsage, UsageStatusInfo.class);
		query.setParameter(GROUP_ID, thd.getGroup().getId()).setParameter(START_TIME, startTime, TemporalType.TIMESTAMP)
				.setParameter(END_TIME, endTime, TemporalType.TIMESTAMP).setParameter(ITEM_NAME, thd.getItemName());

		super.addScalar(query, TRM_ID, StringType.INSTANCE);
		super.addScalar(query, "itemRate", IntegerType.INSTANCE);
		super.setResultTransformer(query, usageStatusTransformer);
		return query.getResultList();
	}

	@Override
	public void insertUsageStatus(List<UsageStatusInfo> usageList) {
		Timestamp timestamp = new Timestamp(new Date().getTime());
		String insertSql = "INSERT INTO TMSTTRM_USAGE_STS (ID, TRM_ID, THD_ID, START_TIME, END_TIME, "
				+ "ITEM_NAME, ITEM_STS, REPORT_CYCLE, CREATE_DATE) VALUES (?,?,?,?,?,?,?,?,?)";
		doWork(conn -> {
			try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
				Long startId = DbHelper.generateId("TMSTTRM_USAGE_STS_ID", usageList.size());
				for (UsageStatusInfo entity : usageList) {
					ps.setLong(1, startId);
					ps.setString(2, entity.getTerminalId());
					ps.setLong(3, entity.getThdId());
					ps.setTimestamp(4, new Timestamp(entity.getStartTime().getTime()), UTCTime.UTC_CLENDAR);
					ps.setTimestamp(5, new Timestamp(entity.getEndTime().getTime()), UTCTime.UTC_CLENDAR);
					ps.setString(6, entity.getItemName());
					ps.setInt(7, entity.getItemStatus());
					ps.setString(8, entity.getReportCycle());
					ps.setTimestamp(9, timestamp, UTCTime.UTC_CLENDAR);
					ps.addBatch();
					if (startId % 100 == 0) {
						ps.executeBatch();
						ps.clearBatch();
					}
					startId = startId + 1L;
				}
				if (startId % 100 != 0) {
					ps.executeBatch();
					ps.clearBatch();
				}
			}
		});
	}

	@Override
	public List<ResultCount> getGroupUsageStatus(Long groupId, String itemName, Date startTime, Date endTime,
			String reportCycle) {
		String statusCountSql = "	SELECT U.ITEM_STS as itemStatus, COUNT(1) as itemCount					"
				+ "	FROM TMSTTRM_GROUP TG																	"
				+ "		LEFT JOIN TMSTTRM_USAGE_STS U ON TG.TRM_ID=U.TRM_ID									"
				+ "		AND U.START_TIME>=:startTime AND U.END_TIME<=:endTime								"
				+ "		AND U.ITEM_NAME=:itemName															"
				+ "		WHERE TG.GROUP_ID=:groupId AND U.REPORT_CYCLE=:reportCycle							"
				+ "	GROUP BY U.ITEM_STS																		";

		NativeQuery<ResultCount> query = super.createNativeQuery(statusCountSql, ResultCount.class)
				.setParameter(ITEM_NAME, itemName).setParameter(GROUP_ID, groupId)
				.setParameter(START_TIME, startTime, TemporalType.TIMESTAMP)
				.setParameter(END_TIME, endTime, TemporalType.TIMESTAMP).setParameter(REPORT_CYCLE, reportCycle);
		super.addScalar(query, "itemStatus", IntegerType.INSTANCE);
		super.addScalar(query, "itemCount", IntegerType.INSTANCE);
		super.setResultTransformer(query, resultCountTransformer);
		return query.getResultList();
	}

	@Override
	public void insertGroupUsageStatus(List<UsagePie> pieList) {
		Timestamp timestamp = new Timestamp(new Date().getTime());
		String insertSql = "INSERT INTO TMSTGROUP_USAGE_STS (ID, GROUP_ID, GROUP_NAME, ITEM_NAME, TOTAL_TRMS, "
				+ " ABNORMAL_TRMS, NORMAL_TRMS, UNKNOWN_TRMS, ALERT_SEVERITY, ALERT_THRESHOLD, ALERT_VALUE, "
				+ " START_TIME, END_TIME,CYCLE_DATE, REPORT_CYCLE, CREATE_DATE) " + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
		doWork(conn -> {
			try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
				Long startId = DbHelper.generateId(GroupUsageStatus.ID_SEQUENCE_NAME, pieList.size());
				for (UsagePie entity : pieList) {
					ps.setLong(1, startId);
					ps.setLong(2, entity.getGroupId());
					ps.setString(3, entity.getGroupName());
					ps.setString(4, entity.getName());
					ps.setInt(5, entity.getTotal());
					ps.setInt(6, entity.getRedCount());
					ps.setInt(7, entity.getGreenCount());
					ps.setInt(8, entity.getGreyCount());
					ps.setInt(9, entity.getAlertLevel());
					ps.setInt(10, entity.getThreshold());
					ps.setString(11, entity.getAlertValue());
					ps.setString(12, DateTimeUtils.format(entity.getStartTime(),DateTimeUtils.PATTERN_STANDARD));
					ps.setString(13,  DateTimeUtils.format(entity.getEndTime(),DateTimeUtils.PATTERN_STANDARD));
					ps.setString(14, DateTimeUtils.format(entity.getStartTime(),DateTimeUtils.PATTERN_DATE));
					ps.setString(15, entity.getReportCycle());
					ps.setTimestamp(16, timestamp, UTCTime.UTC_CLENDAR);

					ps.addBatch();
					if (startId % 100 == 0) {
						ps.executeBatch();
						ps.clearBatch();
					}
					startId = startId + 1L;
				}
				if (startId % 100 != 0) {
					ps.executeBatch();
					ps.clearBatch();
				}
			}
		});
	}

	/**
	 * 1 - normal, 2 - removed, 3 - unknown
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getGroupRealStatus(Long groupId, String itemName, Date dayStart) {
		StringBuilder sql = new StringBuilder();
		String fromSql = " FROM  TMSTTRM_GROUP TG LEFT JOIN TMSTTRMSTATUS TS ON TG.TRM_ID=TS.TRM_ID				"
				+ " WHERE TG.GROUP_ID=:groupId ";

		// query table : TMSTTRMDWNL
		String fromPendSql = " FROM TMSTTRM_GROUP TG INNER JOIN TMSTTRMDWNL TD ON TG.TRM_ID=TD.TRM_ID			";
		String gSql = " AND TG.GROUP_ID=:groupId ";

		switch (itemName) {
		case AlertConstants.TAMPERS:
			sql.append(" SELECT TS.TAMPER as ITEMSTATUS, count(1) as ITEMCOUNT " + fromSql + " GROUP BY TS.TAMPER ");
			break;
		case AlertConstants.OFFLINE:
			sql.append(
					" SELECT TS.IS_ONLINE as ITEMSTATUS, count(1) as ITEMCOUNT " + fromSql + " GROUP BY TS.IS_ONLINE ");
			break;
		case AlertConstants.PRIVACY_SHIELD:
			sql.append(" SELECT TS.PRIVACY_SHIELD as ITEMSTATUS, count(1) as ITEMCOUNT " + fromSql
					+ " GROUP BY TS.PRIVACY_SHIELD ");
			break;
		case AlertConstants.SRED:
            sql.append(" SELECT TS.SRED as ITEMSTATUS, count(1) as ITEMCOUNT " + fromSql  + " GROUP BY TS.SRED ");
            break;
		case AlertConstants.RKI:
            sql.append(" SELECT TS.RKI as ITEMSTATUS, count(1) as ITEMCOUNT " + fromSql  + " GROUP BY TS.RKI ");
            break;
		case AlertConstants.STYLUS:
			sql.append(" SELECT TS.STYLUS as ITEMSTATUS, count(1) as ITEMCOUNT " + fromSql + " GROUP BY TS.STYLUS ");
			break;
		case AlertConstants.DOWNLOADS:
			sql.append(" SELECT TD.DWNL_STATUS as ITEMSTATUS, count(1) as ITEMCOUNT " + fromPendSql
					+ " WHERE ((TD.DWNL_END_TIME > :startTime OR "
					+ "(((TD.EXPIRE_DATE >:nowDate OR TD.EXPIRE_DATE is NULL) AND TD.DWNL_END_TIME is NULL) "
					+ "AND TD.DWNL_STATUS IN('PENDING','DOWNLOADING'))))" + gSql + " GROUP BY TD.DWNL_STATUS ");
			break;
		case AlertConstants.ACTIVATIONS:
			sql.append(" SELECT TD.ACTV_STATUS as ITEMSTATUS, count(1) as ITEMCOUNT " + fromPendSql
					+ " WHERE((TD.DWNL_STATUS = 'SUCCESS') AND    "
					+ " (TD.ACTV_TIME > :startTime OR TD.ACTV_TIME IS NULL ) OR (TD.DWNL_status IN ('PENDING','DOWNLOADING') AND  "
					+ "(TD.EXPIRE_DATE > :nowDate OR TD.EXPIRE_DATE IS NULL)))" + gSql + "GROUP BY TD.ACTV_STATUS ");
			break;
		}
		NativeQuery<Object[]> query = super.createNativeQuery(sql.toString()).setParameter(GROUP_ID, groupId);
		if (AlertConstants.DOWNLOADS.equals(itemName) || AlertConstants.ACTIVATIONS.equals(itemName)) {
			query.setParameter(START_TIME, dayStart, TemporalType.TIMESTAMP).setParameter("nowDate", new Date(),
					TemporalType.TIMESTAMP);
		}
		return query.getResultList();
	}

	@Override
	public List<UserSubscribeInfo> getByCond(Long condId) {
		String sql = "SELECT U.USER_ID AS USERID, S.COND_ID AS CONDID, U.USERNAME AS USERNAME,		"
				+ " U.FULLNAME AS FULLNAME, U.EMAIL AS EMAIL, U.PHONE AS phone, S.SMS AS SMSSTATUS,	"
				+ " S.EMAIL AS EMAILSTATUS															"
				+ " FROM PUBTUSER U INNER JOIN TMSTALERT_SBSCRB S ON S.USER_ID=U.USER_ID			"
				+ " WHERE S.COND_ID=:condId";
		Map<String, Type> scalarMap = new HashMap<String, Type>();
		scalarMap.put(USER_ID, StandardBasicTypes.LONG);
		scalarMap.put(COND_ID, StandardBasicTypes.LONG);
		scalarMap.put(USER_NAME, StandardBasicTypes.STRING);
		scalarMap.put(FULLNAME, StandardBasicTypes.STRING);
		scalarMap.put(EMAIL, StandardBasicTypes.STRING);
		scalarMap.put("phone", StandardBasicTypes.STRING);
		scalarMap.put("smsStatus", StandardBasicTypes.INTEGER);
		scalarMap.put("emailStatus", StandardBasicTypes.INTEGER);
		NativeQuery<UserSubscribeInfo> query = super.createNativeQuery(sql, UserSubscribeInfo.class, scalarMap);
		query.setParameter(COND_ID, condId);
		return query.getResultList();
	}

	@Override
	public List<TimeZoneInfo> listTimeZone() {
		String sql = " SELECT TIME_ZONE AS TIMEZONE, DAYLIGHT_SAVING AS DAYLIGHTSAVING				"
				+ " FROM TMSTTERMINAL GROUP BY TIME_ZONE, DAYLIGHT_SAVING							";
		NativeQuery<TimeZoneInfo> query = super.createNativeQuery(sql, TimeZoneInfo.class, false);
		return query.getResultList();
	}

	@Override
	public int deleteTerminalReportMessage(Date date) {
		String sql = "DELETE FROM TMSTTRM_REPORT_MSG WHERE REPORT_TM <= :startTime";
		return createNativeQuery(sql).setParameter(START_TIME, date, TemporalType.TIMESTAMP).executeUpdate();
	}

	@Override
	public int deleteTerminalUsageMessage(Date date) {
		String sql = "DELETE FROM TMSTTRM_USAGE_MSG WHERE START_TIME <= :startTime";
		return createNativeQuery(sql).setParameter(START_TIME, date, TemporalType.TIMESTAMP).executeUpdate();
	}

	@Override
	public int deleteTerminalUsageSts(Date date) {
		String sql = "DELETE FROM TMSTTRM_USAGE_STS WHERE START_TIME <= :startTime";
		return createNativeQuery(sql).setParameter(START_TIME, date, TemporalType.TIMESTAMP).executeUpdate();
	}

	@Override
	public int deleteGroupUsageSts(Date date) {
		String sql = "DELETE FROM TMSTGROUP_USAGE_STS WHERE START_TIME <= :startTime";
		return createNativeQuery(sql).setParameter(START_TIME, date, TemporalType.TIMESTAMP).executeUpdate();
	}
}
