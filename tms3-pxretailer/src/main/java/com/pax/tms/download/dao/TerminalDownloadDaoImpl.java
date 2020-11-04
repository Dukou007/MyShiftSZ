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
package com.pax.tms.download.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.persistence.TemporalType;

import org.hibernate.query.NativeQuery;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.type.BooleanType;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;
import org.springframework.stereotype.Repository;

import com.pax.common.dao.Sequence;
import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.common.dao.hibernate.MyAliasToBeanResultTransformer;
import com.pax.tms.deploy.model.DownOrActvStatus;
import com.pax.tms.download.model.Deployment;
import com.pax.tms.download.model.Event;
import com.pax.tms.download.model.GroupMsg;
import com.pax.tms.download.model.PkgFile;
import com.pax.tms.download.model.Terminal;
import com.pax.tms.download.model.TerminalLog;
import com.pax.tms.download.model.TerminalStatus;
import com.pax.tms.download.model.TerminalUsageMessage;
import com.pax.tms.download.model.TerminalUsageReport;
import com.pax.tms.download.util.TerminalSpliterator;
import com.pax.tms.pxretailer.constant.AlertConstants;

@Repository("terminalDownloadDaoImpl")
public class TerminalDownloadDaoImpl extends BaseHibernateDao<Terminal, String> implements TerminalDownloadDao {

	private static final String MODEL_ID_COLUMN = "modelId";
	private static final String TRM_SN_COLUMN = "trmSn";
	private static final String TRM_ID_COLUMN = "trmId";
	private static final String PKG_ID_COLUMN = "pkgId";
	private static final String PKG_TYPE_COLUMN = "pkgType";
	private static final String DEPLOY_ID_COLUMN = "deployId";
	private static final String DWNL_STATUS_COLUMN = "dwnlStatus";
	private static final String PKG_NAME_COLUMN = "pkgName";
	private static final String PGM_TYPE_COLUMN = "pgmType";
	private static final String ACTV_STATUS_COLUMN = "actvStatus";
	private static final String UPDATE_TIME_PARA = "updateTime";
	private static final String LAST_CONN_TIME_PARA = "lastConnTime";

	private Sequence unregisteredTerminalIds = new Sequence("TMSTTRM_UNREG_ID", 100);

	private Sequence eventId = new Sequence(Event.ID_SEQUENCE_NAME, 100);

	private ResultTransformer terminalResultTransformer = new MyAliasToBeanResultTransformer(Terminal.class);

	private ResultTransformer scheduledPackageResultTransformer = new MyAliasToBeanResultTransformer(Deployment.class);

	private ResultTransformer deploymentResultTransformer = new MyAliasToBeanResultTransformer(Deployment.class);

	private ResultTransformer deploymentHistoryResultTransformer = new MyAliasToBeanResultTransformer(Deployment.class);

	private ResultTransformer pkgFileResultTransformer = new MyAliasToBeanResultTransformer(PkgFile.class);
	
	private ResultTransformer terminalUsageReportResultTransformer = new MyAliasToBeanResultTransformer(TerminalUsageReport.class);
	
	private ResultTransformer terminalUsageResultTransformer = new MyAliasToBeanResultTransformer(TerminalUsageMessage.class);
	
	private ResultTransformer groupMsgResultTransformer = new MyAliasToBeanResultTransformer(GroupMsg.class);

	// query
	@SuppressWarnings("deprecation")
	@Override
	public Terminal getTerminalBySn(String deviceType, String deviceSerialNumber) {
		String sql = "select TRM_ID as terminalSn, MODEL_ID as modelId, TRM_ID as terminalId, TRM_STATUS as terminalStatus, "
				+ "TIME_ZONE as timeZone, DAYLIGHT_SAVING as useDaylightTime, SYNC_TO_SERVER_TIME as syncToServerTime "
				+ "from TMSTTERMINAL where TRM_ID=:trmSn";
		NativeQuery<Terminal> query = createNativeQuery(sql, Terminal.class).setParameter(TRM_SN_COLUMN,
				deviceSerialNumber);
		query.addScalar("terminalSn", StringType.INSTANCE);
		query.addScalar(MODEL_ID_COLUMN, StringType.INSTANCE);
		query.addScalar("terminalId", StringType.INSTANCE);
		query.addScalar("terminalStatus", IntegerType.INSTANCE);

		query.addScalar("timeZone", StringType.INSTANCE);
		query.addScalar("useDaylightTime", BooleanType.INSTANCE);
		query.addScalar("syncToServerTime", BooleanType.INSTANCE);
		setResultTransformer(query, terminalResultTransformer);
		return uniqueResult(query);
	}

	@Override
	public TerminalStatus getTerminalStatus(String terminalId) {
		String hql = "from TerminalStatus where trmId=:trmId";
		return uniqueResult(createQuery(hql, TerminalStatus.class).setParameter(TRM_ID_COLUMN, terminalId));
	}

	@Override
	public boolean isUnregisteredTerminal(String deviceType, String deviceSerialNumber) {
		String sql = "select count(*) from TMSTTRM_UNREG where TRM_SN=:trmSn and MODEL_ID=:modelId";
		return ((Number) getSession().createNativeQuery(sql).setParameter(MODEL_ID_COLUMN, deviceType)
				.setParameter(TRM_SN_COLUMN, deviceSerialNumber).uniqueResult()).intValue() > 0;
	}

	@Override
	public PkgFile getPackageFile(long pkgId) {
		String sql = "select PKG_ID as pkgId, PKG_ID as fileId, FILE_NAME as fileName, PKG_VERSION as fileVersion, FILE_PATH as filePath, FILE_SIZE as fileSize, FILE_MD5 as md5, FILE_SHA256 as sha256 "
				+ "from TMSTPACKAGE where PKG_ID=:pkgId";
		NativeQuery<PkgFile> query = createNativeQuery(sql, PkgFile.class).setParameter(PKG_ID_COLUMN, pkgId);
		addPkgFileScalar(query);
		setResultTransformer(query, pkgFileResultTransformer);
		return uniqueResult(query);
	}
	
	@SuppressWarnings("deprecation")
	private void addPkgFileScalar(NativeQuery<PkgFile> query) {
		query.addScalar(PKG_ID_COLUMN, LongType.INSTANCE);
		query.addScalar("fileId", LongType.INSTANCE);
		query.addScalar("fileName", StringType.INSTANCE);
		query.addScalar("fileVersion", StringType.INSTANCE);

		query.addScalar("filePath", StringType.INSTANCE);
		query.addScalar("fileSize", LongType.INSTANCE);
		query.addScalar("md5", StringType.INSTANCE);
		query.addScalar("sha256", StringType.INSTANCE);
	}

	@Override
	public PkgFile getProgramFile(long pkgId, long fileId) {
		String sql = "select PKG_ID as pkgId, FILE_ID as fileId, FILE_NAME as fileName, FILE_VERSION as fileVersion, FILE_PATH as filePath, FILE_SIZE as fileSize, FILE_MD5 as md5, FILE_SHA256 as sha256 "
				+ "from TMSTPROGRAM_FILE where PKG_ID=:pkgId and FILE_ID=:fileId";
		NativeQuery<PkgFile> query = createNativeQuery(sql, PkgFile.class).setParameter(PKG_ID_COLUMN, pkgId)
				.setParameter("fileId", fileId);
		addPkgFileScalar(query);
		setResultTransformer(query, pkgFileResultTransformer);
		return uniqueResult(query);
	}

	@Override
	public PkgFile getProgramFile(long pkgId, String fileName) {
		String sql = "select PKG_ID as pkgId, FILE_ID as fileId, FILE_NAME as fileName, FILE_VERSION as fileVersion, FILE_PATH as filePath, FILE_SIZE as fileSize, FILE_MD5 as md5, FILE_SHA256 as sha256 "
				+ "from TMSTPROGRAM_FILE where PKG_ID=:pkgId and FILE_NAME=:fileName";
		NativeQuery<PkgFile> query = createNativeQuery(sql, PkgFile.class).setParameter(PKG_ID_COLUMN, pkgId)
				.setParameter("fileName", fileName);
		addPkgFileScalar(query);
		setResultTransformer(query, pkgFileResultTransformer);
		query.setMaxResults(1);
		return uniqueResult(query);
	}

	@SuppressWarnings("deprecation")
	@Override
	public Deployment getScheduledPackage(Terminal terminal) {
		NativeQuery<Deployment> query = null;
		String sql = "select td.REL_ID as relId, td.TRM_ID as trmId, td.DEPLOY_ID as deployId, td.DWNL_STATUS as dwnlStatus, "
				+ "d.DWNL_START_TM as dwnlStartTm, d.DWNL_END_TM as dwnlEndTm, d.ACTV_START_TM as actvStartTm, "
				+ "d.PKG_ID as pkgId, p.PKG_NAME as pkgName, p.PKG_VERSION as pkgVersion, "
				+ "p.PKG_TYPE as pkgType, p.PGM_TYPE as pgmType " + "from TMSTTRM_DEPLOY td "
				+ "join TMSTDEPLOY d on td.DEPLOY_ID=d.DEPLOY_ID " + "join TMSTPACKAGE p on d.PKG_ID=p.PKG_ID "
				+ "where td.TRM_ID=:trmId and "
				+ "d.DEPLOY_STATUS=1 and (d.DWNL_END_TM is null or d.DWNL_END_TM>:currentTime) and (td.DWNL_STATUS=:pending or td.DWNL_STATUS=:downloading) "
				+ "order by d.DWNL_START_TM, d.ACTV_START_TM, td.DEPLOY_TIME, td.REL_ID";
		query = createNativeQuery(sql, Deployment.class, false).setParameter(TRM_ID_COLUMN, terminal.getTerminalId())
				.setParameter("currentTime", new Date(), TemporalType.TIMESTAMP)
				.setParameter("pending", DownOrActvStatus.PENDING.name())
				.setParameter("downloading", DownOrActvStatus.DOWNLOADING.name());

		query.addScalar("relId", LongType.INSTANCE);
		query.addScalar(TRM_ID_COLUMN, StringType.INSTANCE);
		query.addScalar("deployId", LongType.INSTANCE);
		query.addScalar(DWNL_STATUS_COLUMN, StringType.INSTANCE);

		query.addScalar("dwnlStartTm", TimestampType.INSTANCE);
		query.addScalar("dwnlEndTm", TimestampType.INSTANCE);
		query.addScalar("actvStartTm", TimestampType.INSTANCE);

		query.addScalar(PKG_ID_COLUMN, LongType.INSTANCE);
		query.addScalar(PKG_NAME_COLUMN, StringType.INSTANCE);
		query.addScalar("pkgVersion", StringType.INSTANCE);

		query.addScalar(PKG_TYPE_COLUMN, StringType.INSTANCE);
		query.addScalar(PGM_TYPE_COLUMN, StringType.INSTANCE);

		setResultTransformer(query, scheduledPackageResultTransformer);
		query.setMaxResults(1);
		return uniqueResult(query);
	}

	@SuppressWarnings("deprecation")
	@Override
	public Deployment getDeployment(long deployId, String terminalId) {
		String sql = "select td.REL_ID as relId, td.TRM_ID as trmId, td.DEPLOY_ID as deployId, td.DWNL_STATUS as dwnlStatus, "
				+ "td.DWNL_FAIL_COUNT as dwnlFailCount, td.ACTV_STATUS as actvStatus, "
				+ "d.PKG_ID as pkgId, p.PKG_TYPE as pkgType, p.PKG_NAME as pkgName, p.PGM_TYPE as pgmType "
				+ "from TMSTTRM_DEPLOY td " + "join TMSTDEPLOY d on td.DEPLOY_ID=d.DEPLOY_ID "
				+ "join TMSTPACKAGE p on d.PKG_ID=p.PKG_ID " + "where td.DEPLOY_ID=:deployId and td.TRM_ID=:trmId";
		NativeQuery<Deployment> query = createNativeQuery(sql, Deployment.class)
				.setParameter(DEPLOY_ID_COLUMN, deployId).setParameter(TRM_ID_COLUMN, terminalId);

		query.addScalar("relId", LongType.INSTANCE);
		query.addScalar(TRM_ID_COLUMN, StringType.INSTANCE);
		query.addScalar(DEPLOY_ID_COLUMN, LongType.INSTANCE);
		query.addScalar(DWNL_STATUS_COLUMN, StringType.INSTANCE);
		query.addScalar("dwnlFailCount", IntegerType.INSTANCE);
		query.addScalar(ACTV_STATUS_COLUMN, StringType.INSTANCE);

		query.addScalar(PKG_ID_COLUMN, LongType.INSTANCE);
		query.addScalar(PKG_NAME_COLUMN, StringType.INSTANCE);

		query.addScalar(PKG_TYPE_COLUMN, StringType.INSTANCE);
		query.addScalar(PGM_TYPE_COLUMN, StringType.INSTANCE);

		setResultTransformer(query, deploymentResultTransformer);
		query.setMaxResults(1);
		return uniqueResult(query);
	}

	@SuppressWarnings("deprecation")
	@Override
	public Deployment getDeploymentHistory(long deployId, String terminalId) {
		String sql = "select td.TRM_ID as trmId, td.DEPLOY_ID as deployId, td.PKG_TYPE as pkgType, "
				+ "td.PKG_NAME as pkgName, td.PKG_TYPE as pgmType,  td.DWNL_STATUS as dwnlStatus, td.ACTV_STATUS as actvStatus "
				+ "from TMSTTRMDWNL td where td.DEPLOY_ID=:deployId and td.TRM_ID=:trmId";

		NativeQuery<Deployment> query = createNativeQuery(sql, Deployment.class)
				.setParameter(DEPLOY_ID_COLUMN, deployId).setParameter(TRM_ID_COLUMN, terminalId);

		query.addScalar(TRM_ID_COLUMN, StringType.INSTANCE);
		query.addScalar(DEPLOY_ID_COLUMN, LongType.INSTANCE);

		query.addScalar(PKG_NAME_COLUMN, StringType.INSTANCE);

		query.addScalar(PKG_TYPE_COLUMN, StringType.INSTANCE);
		query.addScalar(PGM_TYPE_COLUMN, StringType.INSTANCE);

		query.addScalar(DWNL_STATUS_COLUMN, StringType.INSTANCE);
		query.addScalar(ACTV_STATUS_COLUMN, StringType.INSTANCE);

		setResultTransformer(query, deploymentHistoryResultTransformer);
		query.setMaxResults(1);
		return uniqueResult(query);
	}

	// update
	@Override
	public void addUnregisteredTerminal(String deviceType, String deviceSerialNumber, String sourceIp,
			Date accessTime) {
		String sql = "insert into TMSTTRM_UNREG (ID,MODEL_ID,TRM_SN,SOURCE_IP,LAST_DATE,CREATE_DATE) values "
				+ "(:id,:modelId,:trmSn,:sourceIp,:accessTime,:accessTime)";
		getSession().createNativeQuery(sql).setParameter("id", unregisteredTerminalIds.getId())
				.setParameter(MODEL_ID_COLUMN, deviceType).setParameter(TRM_SN_COLUMN, deviceSerialNumber)
				.setParameter("sourceIp", sourceIp).setParameter("accessTime", accessTime, TemporalType.TIMESTAMP)
				.executeUpdate();
	}

	@Override
	public int updateUnregisteredTerminal(String deviceType, String deviceSerialNumber, String sourceIp,
			Date lastAccessTime) {
		String hql = "update UnregisteredTerminal set sourceIp=:sourceIp, lastDate=:lastAccessTime "
				+ "where trmSn=:trmSn and modelId=:modelId";
		return createQuery(hql).setParameter(TRM_SN_COLUMN, deviceSerialNumber)
				.setParameter(MODEL_ID_COLUMN, deviceType).setParameter("sourceIp", sourceIp)
				.setParameter("lastAccessTime", lastAccessTime, TemporalType.TIMESTAMP).executeUpdate();
	}

	@Override
	public void addTerminalStatus(TerminalStatus terminalStatus) {
		getSession().save(terminalStatus);
	}

	@Override
	public void updateTerminalStatus(Terminal terminal, Map<String, Object> changedFields) {
		if (changedFields.isEmpty()) {
			return;
		}

		List<Object> values = new ArrayList<>(changedFields.size());
		StringBuilder sb = new StringBuilder("update TMSTTRMSTATUS set ");
		boolean appendSplit = false;
		for (Map.Entry<String, Object> entry : changedFields.entrySet()) {
			if (appendSplit) {
				sb.append(',');
			}
			sb.append(entry.getKey());
			sb.append("=? ");
			appendSplit = true;
			values.add(entry.getValue());
		}
		sb.append(',').append(TerminalStatus.LAST_CONN_TIME_FIELD).append("=? ");
		values.add(terminal.getLastAccessTime());
		sb.append("where TRM_ID=?");

		NativeQuery<?> query = getSession().createNativeQuery(sb.toString());
		int i = 1;
		for (Object value : values) {
			if (value instanceof Date) {
				query.setParameter(i++, value, TemporalType.TIMESTAMP);
			} else {
				query.setParameter(i++, value);
			}
		}
		query.setParameter(i, terminal.getTerminalId());
		query.executeUpdate();
	}

	@Override
	public void setTerminalOnline(Terminal terminal, String sourceIp, Date accessTime) {
		if (terminal.isOffline()) {
			String sql = "update TMSTTRMSTATUS set LAST_CONN_TIME=:lastConnTime, LAST_SOURCE_IP=:lastSourceIp, IS_ONLINE=:online, ONLINE_SINCE=:lastConnTime where TRM_ID=:trmId";
			getSession().createNativeQuery(sql).setParameter(TRM_ID_COLUMN, terminal.getTerminalId())
					.setParameter(LAST_CONN_TIME_PARA, accessTime, TemporalType.TIMESTAMP)
					.setParameter("online", TerminalStatus.ONLINE_STATUS).setParameter("lastSourceIp", sourceIp)
					.executeUpdate();
		} else {
			String sql = "update TMSTTRMSTATUS set LAST_CONN_TIME=:lastConnTime, LAST_SOURCE_IP=:lastSourceIp where TRM_ID=:trmId";
			getSession().createNativeQuery(sql).setParameter(TRM_ID_COLUMN, terminal.getTerminalId())
					.setParameter(LAST_CONN_TIME_PARA, accessTime, TemporalType.TIMESTAMP)
					.setParameter("lastSourceIp", sourceIp).executeUpdate();
		}
	}

	@Override
	public void updateDownloadStatus(Terminal terminal, DownOrActvStatus downloadStatus, Date accessTime) {
		String sql = "update TMSTTRMSTATUS set LAST_DWNL_TIME=:lastDwnlTime, LAST_DWNL_STATUS=:lastDwnlStatus, LAST_CONN_TIME=:lastConnTime where TRM_ID=:trmId";
		getSession().createNativeQuery(sql).setParameter(TRM_ID_COLUMN, terminal.getTerminalId())
				.setParameter("lastDwnlTime", accessTime, TemporalType.TIMESTAMP)
				.setParameter(LAST_CONN_TIME_PARA, accessTime, TemporalType.TIMESTAMP)
				.setParameter("lastDwnlStatus", downloadStatus.name()).executeUpdate();
	}

	@Override
	public void updateActivationStatus(Terminal terminal, DownOrActvStatus activationStatus, Date accessTime) {
		String sql = "update TMSTTRMSTATUS set LAST_ACTV_TIME=:lastActvTime, LAST_ACTV_STATUS=:lastActvStatus, LAST_CONN_TIME=:lastConnTime where TRM_ID=:trmId";
		getSession().createNativeQuery(sql).setParameter(TRM_ID_COLUMN, terminal.getTerminalId())
				.setParameter("lastActvTime", accessTime, TemporalType.TIMESTAMP)
				.setParameter(LAST_CONN_TIME_PARA, accessTime, TemporalType.TIMESTAMP)
				.setParameter("lastActvStatus", activationStatus.name()).executeUpdate();
	}

	@Override
	public void saveUsageReport(TerminalUsageReport usageReport) {
		getSession().save(usageReport);
	}
	

	@Override
	public void saveEvent(Event event) {
		getSession().save(event);
	}

	@Override
	public void saveEventList(List<Event> events) {
		for (Event event : events) {
			getSession().save(event);
		}
	}

	@Override
	public void updateTerminalStatusAtDwnlStart(Terminal terminal, Deployment deployment, Date accessTime) {
		String sql = "update TMSTTRMSTATUS set LAST_DWNL_STATUS=:lastDwnlStatus, LAST_ACTV_STATUS=:lastActvStatus, LAST_DWNL_TASK=:lastDwnlTask, LAST_CONN_TIME=:accessTime where TRM_ID=:trmId";
		getSession().createNativeQuery(sql).setParameter(TRM_ID_COLUMN, terminal.getTerminalId())
				.setParameter("lastActvStatus", DownOrActvStatus.PENDING.name())
				.setParameter("lastDwnlStatus", DownOrActvStatus.PENDING.name())
				.setParameter("accessTime", accessTime, TemporalType.TIMESTAMP)
				.setParameter("lastDwnlTask", deployment.getRelId()).executeUpdate();
	}

	@Override
	public void updateDownloadReportAtDwnlStart(Terminal terminal, Deployment deploy, Date startTime) {
		String sql = "update TMSTTRMDWNL set DWNL_STATUS=:dwnlStatus, MODIFY_DATE=:startTime where TRM_ID=:trmId and DEPLOY_ID=:deployId";
		sql = "update TMSTTRMDWNL set DWNL_START_TIME=:startTime "
				+ "where TRM_ID=:trmId and DEPLOY_ID=:deployId and DWNL_START_TIME is null";
		getSession().createNativeQuery(sql).setParameter(TRM_ID_COLUMN, deploy.getTrmId())
				.setParameter(DEPLOY_ID_COLUMN, deploy.getDeployId())
				.setParameter("startTime", startTime, TemporalType.TIMESTAMP).executeUpdate();
	}

	@Override
	public void updateDeployStatusAtDwnlStart(Terminal terminal, Deployment deployment, Date accessTime) {
		String sql = "update TMSTTRM_DEPLOY set DWNL_TIME=:updateTime, DWNL_STATUS=:dwnlStatus where TRM_ID=:trmId and DEPLOY_ID=:deployId";
		getSession().createNativeQuery(sql).setParameter(TRM_ID_COLUMN, deployment.getTrmId())
				.setParameter(DEPLOY_ID_COLUMN, deployment.getDeployId())
				.setParameter(DWNL_STATUS_COLUMN, DownOrActvStatus.DOWNLOADING.name())
				.setParameter(UPDATE_TIME_PARA, accessTime, TemporalType.TIMESTAMP).executeUpdate();
	}

	@Override
	public void updateDeploymentStatusAtDwnlFinish(Terminal terminal, Deployment deploy,
			DownOrActvStatus downloadStatus, Date updateTime) {
		String sql = null;
		switch (downloadStatus) {
		case SUCCESS:
			sql = "update TMSTTRM_DEPLOY set DWNL_TIME=:updateTime, DWNL_STATUS=:dwnlStatus, DWNL_SUCC_COUNT=DWNL_SUCC_COUNT+1 where TRM_ID=:trmId and DEPLOY_ID=:deployId";
			break;
		case FAILED:
			sql = "update TMSTTRM_DEPLOY set DWNL_TIME=:updateTime, DWNL_STATUS=:dwnlStatus, DWNL_FAIL_COUNT=DWNL_FAIL_COUNT+1 where TRM_ID=:trmId and DEPLOY_ID=:deployId";
			break;
		case NOUPDATE:
		case CANCELED:
			sql = "update TMSTTRM_DEPLOY set DWNL_TIME=:updateTime, DWNL_STATUS=:dwnlStatus where TRM_ID=:trmId and DEPLOY_ID=:deployId";
			break;
		default:
		}

		if (sql == null) {
			return;
		}

		getSession().createNativeQuery(sql).setParameter(TRM_ID_COLUMN, deploy.getTrmId())
				.setParameter(DEPLOY_ID_COLUMN, deploy.getDeployId())
				.setParameter(DWNL_STATUS_COLUMN, downloadStatus.name())
				.setParameter(UPDATE_TIME_PARA, updateTime, TemporalType.TIMESTAMP).executeUpdate();
	}

	@Override
	public void updateDownloadReportAtDwnlFinish(Terminal terminal, Deployment deploy, DownOrActvStatus downloadStatus,
			Date updateTime) {
		String sql = "update TMSTTRMDWNL set DWNL_END_TIME=:updateTime, DWNL_STATUS=:dwnlStatus, MODIFY_DATE=:updateTime where TRM_ID=:trmId and DEPLOY_ID=:deployId";
		getSession().createNativeQuery(sql).setParameter(TRM_ID_COLUMN, deploy.getTrmId())
				.setParameter(DEPLOY_ID_COLUMN, deploy.getDeployId())
				.setParameter(DWNL_STATUS_COLUMN, downloadStatus.name())
				.setParameter(UPDATE_TIME_PARA, updateTime, TemporalType.TIMESTAMP).executeUpdate();
	}

	@Override
	public void updateDeploymentStatusAtActvFinish(Terminal terminal, Deployment deploy,
			DownOrActvStatus activationStatus, Date updateTime) {
		String sql = null;
		switch (activationStatus) {
		case SUCCESS:
		case NOUPDATE:
		case CANCELED:
		case NOACTIVITION:
			sql = "update TMSTTRM_DEPLOY set ACTV_TIME=:updateTime, ACTV_STATUS=:actvStatus where TRM_ID=:trmId and DEPLOY_ID=:deployId";
			break;
		case FAILED:
			sql = "update TMSTTRM_DEPLOY set ACTV_TIME=:updateTime, ACTV_STATUS=:actvStatus, ACTV_FAIL_COUNT=ACTV_FAIL_COUNT+1 where TRM_ID=:trmId and DEPLOY_ID=:deployId";
			break;
		default:
		}

		if (sql == null) {
			return;
		}

		getSession().createNativeQuery(sql).setParameter(TRM_ID_COLUMN, deploy.getTrmId())
				.setParameter(DEPLOY_ID_COLUMN, deploy.getDeployId())
				.setParameter(ACTV_STATUS_COLUMN, activationStatus.name())
				.setParameter(UPDATE_TIME_PARA, updateTime, TemporalType.TIMESTAMP).executeUpdate();
	}

	@Override
	public void deleteDeploymentAtActvFinish(Terminal terminal, Deployment deploy, DownOrActvStatus activationStatus,
			Date updateTime) {
		String sql1 = "delete from TMSTTRM_DEPLOY where TRM_ID=:trmId and DEPLOY_ID=:deployId";
		String sql2 = "delete from TMSTDEPLOY where DEPLOY_ID=:deployId";
		getSession().createNativeQuery(sql1).setParameter(TRM_ID_COLUMN, deploy.getTrmId())
				.setParameter(DEPLOY_ID_COLUMN, deploy.getDeployId()).executeUpdate();
		getSession().createNativeQuery(sql2).setParameter(DEPLOY_ID_COLUMN, deploy.getDeployId()).executeUpdate();
	}

	@Override
	public void updateDownloadReportAtActvFinish(Terminal terminal, Deployment deploy,
			DownOrActvStatus activationStatus, Date updateTime) {
		String sql = "update TMSTTRMDWNL set ACTV_TIME=:updateTime, ACTV_STATUS=:actvStatus, MODIFY_DATE=:updateTime where TRM_ID=:trmId and DEPLOY_ID=:deployId";
		getSession().createNativeQuery(sql).setParameter(TRM_ID_COLUMN, deploy.getTrmId())
				.setParameter(DEPLOY_ID_COLUMN, deploy.getDeployId())
				.setParameter(ACTV_STATUS_COLUMN, activationStatus.name())
				.setParameter(UPDATE_TIME_PARA, updateTime, TemporalType.TIMESTAMP).executeUpdate();
	}

	@Override
	public void addDownloadRetryCount(Terminal terminal, Deployment deployment) {
		String sql = "update TMSTTRM_DEPLOY set DWNL_FAIL_COUNT=DWNL_FAIL_COUNT+1, MODIFY_DATE=:updateTime where TRM_ID=:trmId and DEPLOY_ID=:deployId";
		getSession().createNativeQuery(sql).setParameter(TRM_ID_COLUMN, deployment.getTrmId())
				.setParameter(DEPLOY_ID_COLUMN, deployment.getDeployId())
				.setParameter(UPDATE_TIME_PARA, new Date(), TemporalType.TIMESTAMP).executeUpdate();
	}

	// batch update
	@Override
	public void updateTerminalOnlineStatus(Set<String> terminals, long maxLastAccessTime) {
		ArrayList<String> list = new ArrayList<>(terminals);
		Collections.sort(list);
		setTerminalOffline(list, maxLastAccessTime);
	}

	private void setTerminalOffline(final List<String> terminals, long maxLastAccessTime) {
		if (terminals == null || terminals.isEmpty()) {
			return;
		}

		Timestamp lastConnTime = new Timestamp(maxLastAccessTime);
		Timestamp now = new Timestamp(System.currentTimeMillis());
		Calendar caldendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

		String updateOnlineStatusSql = "update TMSTTRMSTATUS set IS_ONLINE=2, PRIVACY_SHIELD=3, STYLUS=3, TAMPER=null, SRED=null, RKI=null, OFFLINE_SINCE=? where TRM_SN=? and IS_ONLINE=1 and LAST_CONN_TIME<=?";

		String insertEventRecordSql = "insert into TMSTEVENT_TRM (EVENT_ID,EVENT_SOURCE,EVENT_TIME,EVENT_SEVERITY,EVENT_MSG) "
				+ "values (?,?,?," + Event.CRITICAL + ",'Terminal offline')";

		TerminalSpliterator ts = new TerminalSpliterator(terminals);

		doWork(conn -> {
			try (PreparedStatement eventSt = conn.prepareStatement(insertEventRecordSql);
					PreparedStatement updateSt = conn.prepareStatement(updateOnlineStatusSql)) {
				setTerminalOffline(ts, eventSt, updateSt, lastConnTime, now, caldendar);
			}
		});
	}

	private void setTerminalOffline(TerminalSpliterator ts, PreparedStatement eventSt, PreparedStatement updateSt,
			Timestamp lastConnTime, Timestamp now, Calendar caldendar) throws SQLException {
		while (ts.nextBatch()) {
			for (int i = 0; i < ts.batchSize(); i++) {
				updateSt.setTimestamp(1, now, caldendar);
				updateSt.setString(2, ts.get(i));
				updateSt.setTimestamp(3, lastConnTime, caldendar);
				updateSt.addBatch();
			}

			int[] result = updateSt.executeBatch();
			updateSt.clearBatch();

			boolean hasEvent = false;
			for (int i = 0; i < result.length; i++) {
				if (result[i] != 0) {
					hasEvent = true;
					eventSt.setLong(1, eventId.getId());
					eventSt.setString(2, ts.get(i));
					eventSt.setTimestamp(3, now, caldendar);
					eventSt.addBatch();
				}
			}

			if (hasEvent) {
				eventSt.executeBatch();
				eventSt.clearBatch();
			}
		}
	}

	@Override
	public void setTerminalOffline(long maxLastAccessTime) {
		Timestamp lastConnTime = new Timestamp(maxLastAccessTime);

		String sql = "select TRM_SN from TMSTTRMSTATUS where IS_ONLINE=1 and LAST_CONN_TIME<=:lastConnTime";
		List<String> terminals = createNativeQuery(sql, String.class)
				.setParameter(LAST_CONN_TIME_PARA, lastConnTime, TemporalType.TIMESTAMP).getResultList();
		if (terminals.isEmpty()) {
			return;
		}
		Collections.sort(terminals);
		setTerminalOffline(terminals, maxLastAccessTime);
	}

	@Override
	public void updateTerminalLastAccessTime(Map<String, String> map) {
		if (map == null || map.isEmpty()) {
			return;
		}

		final Map<String, String> sortedMap = map instanceof SortedMap ? map : new TreeMap<>(map);

		Calendar caldendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		String sql = "update TMSTTRMSTATUS set LAST_CONN_TIME=? where TRM_SN=? and LAST_CONN_TIME<?";
		getSession().doWork(conn -> {
			try (PreparedStatement st = conn.prepareStatement(sql)) {
				int count = 0;
				Entry<String, String> entry;
				Timestamp time;
				Iterator<Entry<String, String>> it = sortedMap.entrySet().iterator();
				while (it.hasNext()) {
					entry = it.next();
					time = new Timestamp(Long.parseLong(entry.getValue()));
					st.setTimestamp(1, time, caldendar);
					st.setString(2, entry.getKey());
					st.setTimestamp(3, time, caldendar);
					st.addBatch();
					count++;
					if (count % 200 == 0) {
						st.executeBatch();
						st.clearBatch();
					}
				}
				if (count % 200 != 0) {
					st.executeBatch();
					st.clearBatch();
				}
			}
		});
	}

	@Override
	public void updateTerminalInstallApps(String terminalSn, String terminalInstalledApps) {

		String sql = "UPDATE tmstterminal SET INSTALL_APPS=:terminalInstalledApps,REPORT_TIME=:reportTime WHERE TRM_ID=:terminalSn";
		super.createNativeQuery(sql).setParameter("terminalInstalledApps", terminalInstalledApps)
				.setParameter("terminalSn", terminalSn).setParameter("reportTime", new Date()).executeUpdate();

	}

	@Override
	public void updateTerminalInstallAppReportTime(String terminalSn) {
		String sql = "UPDATE tmstterminal SET REPORT_TIME=:reportTime WHERE TRM_ID=:terminalSn";
		super.createNativeQuery(sql).setParameter("terminalSn", terminalSn).setParameter("reportTime", new Date())
				.executeUpdate();

	}
	

	@Override
	public String getTerminalInstalledApps(String terminalSn) {
		String sql = "select t.INSTALL_APPS from tmstterminal t where t.TRM_ID=:terminalSn";
		return super.createNativeQuery(sql, String.class).setParameter("terminalSn", terminalSn).uniqueResult();

	}
	
	@Override
	public Integer getDownloadAndActivation(String itemName, String status,String terminalSN) {
		String sql = " SELECT COUNT(*) AS total FROM TerminalDownload "+
                      " WHERE tsn=:terminalSN ";
		Integer result = 0;
		if(itemName!=null&&AlertConstants.DOWNLOAD_HISTORY.equals(itemName))
		{
		    sql = sql + " AND DWNL_STATUS=:status ";
		}
		else if(itemName!=null&&AlertConstants.ACTIVATION_HISTORY.equals(itemName))
		{
			sql = sql + " AND ACTV_STATUS=:status ";
		}
		else
		{
			return result;
		}
		result =  super.createQuery(sql,Long.class).setParameter("terminalSN", terminalSN).setParameter("status", status).uniqueResult().intValue();
		return result;
	}
	
	   @Override
	    public TerminalUsageReport geTerminalUsageReport(String terminalSn,Long groupId,String startTime, String endTime){
	        String sql = "select RPT_ID as reportId,TRM_ID as terminalId, GROUP_ID as groupId, REPORT_TM as reportTime, MSR_ERRS as msrErrs, MSR_TOTS as msrTots, "
	                + "ICR_ERRS as icrErrs, ICR_TOTS as icrTots, PIN_FAILS as pinFails, PIN_TOTS as pinTots, SIGN_ERRS as signErr,"
	                +"SIGN_TOTS as signTots, DOWN_FAILS as downFails,DOWN_PENDING as downPending,DOWN_TOTS as downTots,"
	                +"ACTV_FAILS as actvErrs, ACTV_PENDING as actvPending, ACTV_TOTS as actvTots, "
	                +"CL_ICR_ERRS as clIcrErrs, CL_ICR_TOTS as clIcrTots,TXN_ERRS as txnErrs, TXN_TOTS as txnTots, POWER_NO as powers"
	                + " from TMSTTRM_REPORT_MSG where TRM_ID=:trmSn AND GROUP_ID=:groupId AND REPORT_TM >= :startTime AND REPORT_TM <= :endTime ";
	        NativeQuery<TerminalUsageReport> query = createNativeQuery(sql, TerminalUsageReport.class).setParameter(TRM_SN_COLUMN,terminalSn).setParameter("groupId", groupId)
	                .setParameter("startTime", startTime).setParameter("endTime", endTime);
	        query.addScalar("reportId", LongType.INSTANCE);
	        query.addScalar("terminalId", StringType.INSTANCE);
	        query.addScalar("groupId", LongType.INSTANCE);
	        query.addScalar("reportTime", StringType.INSTANCE);
	        query.addScalar("msrErrs", IntegerType.INSTANCE);
	        query.addScalar("msrTots", IntegerType.INSTANCE);
	        query.addScalar("icrErrs", IntegerType.INSTANCE);
	        query.addScalar("icrTots", IntegerType.INSTANCE);
	        query.addScalar("pinFails", IntegerType.INSTANCE);
	        query.addScalar("pinTots", IntegerType.INSTANCE);
	        query.addScalar("signErr", IntegerType.INSTANCE);
	        query.addScalar("signTots", IntegerType.INSTANCE);
	        query.addScalar("downFails", IntegerType.INSTANCE);
	        query.addScalar("downPending", IntegerType.INSTANCE);
	        query.addScalar("downTots", IntegerType.INSTANCE);
	        query.addScalar("actvErrs", IntegerType.INSTANCE);
	        query.addScalar("actvPending", IntegerType.INSTANCE);
	        query.addScalar("actvTots", IntegerType.INSTANCE);
	        query.addScalar("clIcrErrs", IntegerType.INSTANCE);
	        query.addScalar("clIcrTots", IntegerType.INSTANCE);
	        query.addScalar("txnErrs", IntegerType.INSTANCE);
	        query.addScalar("txnTots", IntegerType.INSTANCE);
	        query.addScalar("powers", IntegerType.INSTANCE);
	        
	        setResultTransformer(query, terminalUsageReportResultTransformer);
	        return uniqueResult(query);
	    }
	    
	    @Override
	    public List<TerminalUsageMessage> getTerminalUsageMessage(String terminalSn,Long groupId,String msgCycle){
	        String sql = "select ID as id, TRM_ID as terminalId, START_TIME as startTime, END_TIME as endTime, "
	                + "ITEM_NAME as itemName, ITEM_ERRS as itemErrors, ITEM_TOTS as itemTotals, MSG_CYCLE as msgCycle, CREATE_DATE as createDate"
	                + " from TMSTTRM_USAGE_MSG where TRM_ID = :trmId  AND GROUP_ID=:groupId AND MSG_CYCLE = :msgCycle ";
	        NativeQuery<TerminalUsageMessage> query = createNativeQuery(sql, TerminalUsageMessage.class).setParameter(TRM_ID_COLUMN,terminalSn).setParameter("groupId", groupId)
	                .setParameter("msgCycle", msgCycle);
	        query.addScalar("id", LongType.INSTANCE);
	        query.addScalar("terminalId", StringType.INSTANCE);
	        query.addScalar("startTime", StringType.INSTANCE);
	        query.addScalar("endTime", StringType.INSTANCE);
	        query.addScalar("itemName", StringType.INSTANCE);
	        query.addScalar("itemErrors", IntegerType.INSTANCE);
	        query.addScalar("itemTotals", IntegerType.INSTANCE);
	        query.addScalar("createDate", DateType.INSTANCE);
	        setResultTransformer(query, terminalUsageResultTransformer);
	        return query.getResultList();
	    }
	    
	    @Override
	    public void addTerminalUsageReport(TerminalUsageReport terminalUsageReport){
	        getSession().save(terminalUsageReport);
	        
	    }
	    
	    @Override
	    public void addTerminalUsageMessage(TerminalUsageMessage terminalUsageMessage){
	        getSession().save(terminalUsageMessage);
	    }

	    @Override
	    public void updateTerminalUsageReport(TerminalUsageReport terminalUsageReport){
	        String sql = "UPDATE TMSTTRM_REPORT_MSG SET MSR_ERRS = :msrErrs,MSR_TOTS = :msrTots,ICR_ERRS = :icrErrs,ICR_TOTS = :icrTots,PIN_FAILS = :pinFails,PIN_TOTS = :pinTots,"
	                +" SIGN_ERRS = :signErr, SIGN_TOTS = :signTots, CL_ICR_ERRS = :clIcrErrs,CL_ICR_TOTS = :clIcrTots,TXN_ERRS = :txnErrs, TXN_TOTS = :txnTots, POWER_NO = :powers,"
	                +" DOWN_FAILS = :downFails, DOWN_PENDING = :downPending, DOWN_TOTS = :downTots,ACTV_FAILS = :actvFails,ACTV_PENDING = :actvPending, ACTV_TOTS = :actvTots "
	                +" WHERE RPT_ID = :reportId AND TRM_ID=:terminalSn";
	        super.createNativeQuery(sql).setParameter("msrErrs", terminalUsageReport.getMsrErrs()).setParameter("msrTots", terminalUsageReport.getMsrTots())
	        .setParameter("icrErrs", terminalUsageReport.getIcrErrs()).setParameter("icrTots", terminalUsageReport.getIcrTots())
	        .setParameter("pinFails", terminalUsageReport.getPinFails()).setParameter("pinTots", terminalUsageReport.getPinTots())
	        .setParameter("signErr", terminalUsageReport.getSignErr()).setParameter("signTots", terminalUsageReport.getSignTots())
	        .setParameter("clIcrErrs", terminalUsageReport.getClIcrErrs()).setParameter("clIcrTots", terminalUsageReport.getClIcrTots())
	        .setParameter("txnErrs", terminalUsageReport.getTxnErrs()).setParameter("txnTots", terminalUsageReport.getTxnTots())
	        .setParameter("downFails", terminalUsageReport.getDownFails()).setParameter("downPending", terminalUsageReport.getDownPending())
	        .setParameter("downTots", terminalUsageReport.getDownTots()).setParameter("actvFails", terminalUsageReport.getActvErrs())
	        .setParameter("actvPending", terminalUsageReport.getActvPending()).setParameter("actvTots", terminalUsageReport.getActvTots())
	        .setParameter("powers", terminalUsageReport.getPowers())
	        .setParameter("reportId", terminalUsageReport.getReportId()).setParameter("terminalSn", terminalUsageReport.getTerminalId())
	        .executeUpdate();
	    }
	    
	    @Override
	    public void updateTerminalUsageMessage(TerminalUsageMessage terminalUsageMessage){
	        String sql = "UPDATE TMSTTRM_USAGE_MSG SET ITEM_ERRS = :itemErrors , ITEM_PENDING = :itemPending , ITEM_TOTS = :itemTotals , CREATE_DATE = :createDate  WHERE ID = :id ";
	        super.createNativeQuery(sql).setParameter("itemErrors", terminalUsageMessage.getItemErrors()).setParameter("itemPending", terminalUsageMessage.getItemPending())
	        .setParameter("itemTotals", terminalUsageMessage.getItemTotals()).setParameter("createDate", terminalUsageMessage.getCreateDate())
	        .setParameter("id", terminalUsageMessage.getId()).executeUpdate();
	    }
	    
	    @Override
	    public List<GroupMsg> getGroupMsgByTerminalId(String terminalId){
	        String sql = "select g.GROUP_ID as groupId,g.GROUP_NAME as groupName,g.TIME_ZONE as timeZone"
	                   + " from pubtgroup g LEFT JOIN tmsttrm_group tg on tg.GROUP_ID=g.GROUP_ID"
	                   +" where g.GROUP_ID!=1 and TRM_ID = :trmId";
            NativeQuery<GroupMsg> query = createNativeQuery(sql, GroupMsg.class).setParameter(TRM_ID_COLUMN,terminalId);
            query.addScalar("groupId", LongType.INSTANCE);
            query.addScalar("groupName", StringType.INSTANCE);
            query.addScalar("timeZone", StringType.INSTANCE);
            setResultTransformer(query, groupMsgResultTransformer);
            return query.getResultList();
	    }
	    
	    @Override
        public void addTerminalLog(TerminalLog terminalLog){
	        getSession().save(terminalLog);
	    }
	    
	    @Override
	    public void updateTerminalLocalTime(String terminalSn, String localTime) {
	    	String sql = "UPDATE tmstterminal SET LOCAL_TIME=:localTime WHERE TRM_SN=:terminalSn";
	    	createNativeQuery(sql).setParameter("localTime", localTime).setParameter("terminalSn", terminalSn).executeUpdate();
	    }
	    
	    @Override
	    public String getTerminalSysmetricKeys(String terminalSn) {
	        String sql = "select t.SYSMETRIC_KEYS from tmstterminal t where t.TRM_ID=:terminalSn";
	        return super.createNativeQuery(sql, String.class).setParameter("terminalSn", terminalSn).uniqueResult();

	    }
	    
	    @Override
	    public void updateTerminalSysmetricKeys(String terminalSn, String terminalSysmetricKeys) {

	        String sql = "UPDATE tmstterminal SET SYSMETRIC_KEYS=:terminalSysmetricKeys,KEY_REPORT_TIME=:reportTime WHERE TRM_ID=:terminalSn";
	        super.createNativeQuery(sql).setParameter("terminalSysmetricKeys", terminalSysmetricKeys)
	                .setParameter("terminalSn", terminalSn).setParameter("reportTime", new Date()).executeUpdate();
	    }
	    
	    @Override
	    public void updateTerminalSysmetricKeysReportTime(String terminalSn) {
	        String sql = "UPDATE tmstterminal SET KEY_REPORT_TIME=:reportTime WHERE TRM_ID=:terminalSn";
	        super.createNativeQuery(sql).setParameter("terminalSn", terminalSn).setParameter("reportTime", new Date())
	                .executeUpdate();

	    }
}
