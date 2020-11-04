/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description: // Detail description about the function of this module,		
 *             // interfaces with the other modules, and dependencies. 		
 * Revision History:		
 * Date	                 Author	                Action
 * 20170112	             Jaden           	    Modify
 * ============================================================================		
 */
package com.pax.tms.deploy.dao.impl;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.pax.common.dao.DbHelper;
import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.common.web.form.BaseForm;
import com.pax.tms.deploy.dao.GroupDeployDao;
import com.pax.tms.deploy.domain.DeployInfo;
import com.pax.tms.deploy.model.Deploy;
import com.pax.tms.deploy.model.DownOrActvStatus;
import com.pax.tms.deploy.model.GroupDeploy;
import com.pax.tms.deploy.model.TerminalDeploy;
import com.pax.tms.deploy.web.form.QueryCurrentGroupDeployForm;
import com.pax.tms.terminal.model.TerminalDownload;
import com.pax.tms.user.security.UTCTime;

@Repository("groupDeployDaoImpl")
public class GroupDeployDaoImpl extends BaseHibernateDao<GroupDeploy, Long> implements GroupDeployDao {

	@Override
	public void copyGroupTaskToTerminals(Map<String, Deploy> tsnDeploys, BaseForm command) {

		if (tsnDeploys.isEmpty()) {
			return;
		}
		Timestamp timestamp = new Timestamp(command.getRequestTime().getTime());
		String username = command.getLoginUsername();
		long deployRelId = DbHelper.generateId(Deploy.ID_SEQUENCE_NAME, tsnDeploys.size());
		long terminalDeployRelId = DbHelper.generateId(TerminalDeploy.ID_SEQUENCE_NAME, tsnDeploys.size());
		long terminalDownloadRelId = DbHelper.generateId(TerminalDownload.ID_SEQUENCE_NAME, tsnDeploys.size());
		String deploySql = "INSERT INTO TMSTDEPLOY (DEPLOY_ID,MODEL_ID,DEPLOY_SOURCE_GROUP_ID,PKG_ID,SCHEMA_ID,"
				+ " DEPLOY_STATUS,DEPLOY_SOURCE,DWNL_START_TM,DWNL_END_TM,DWNL_RETRY_COUNT,ACTV_RETRY_COUNT,"
				+ " DEPLOY_SOURCE_ID,ACTV_START_TM,DWNL_ORDER,FORCE_UPDATE,ONLY_PARAM,DELETED_WHEN_DONE,"
				+ " PARAM_VERSION,PARAM_SET,TIME_ZONE,DAYLIGHT_SAVING,CREATOR,CREATE_DATE,MODIFIER,MODIFY_DATE) "
				+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
		String terminalDeploySql = "INSERT INTO TMSTTRM_DEPLOY (REL_ID,TRM_ID,DEPLOY_ID,"
				+ " DWNL_STATUS,ACTV_STATUS,CREATOR,CREATE_DATE,MODIFIER,MODIFY_DATE,DEPLOY_TIME)"
				+ " values(?,?,?,?,?,?,?,?,?,?)";
		String terminalDownloadSql = "insert into TMSTTRMDWNL(LOG_ID,DEPLOY_ID,TRM_ID,TRM_SN,MODEL_ID,PKG_NAME,"
				+ "	PKG_TYPE,PKG_VERSION,DWNL_STATUS,ACTV_STATUS,ACTV_SCHEDULE,DWNL_SCHEDULE,"
				+ "CREATE_DATE,MODIFY_DATE,EXPIRE_DATE) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		doWork(conn -> {

			long deployId = deployRelId;
			long terminalDeployId = terminalDeployRelId;
			long terminalDownloadId = terminalDownloadRelId;
			try (PreparedStatement deploySt = conn.prepareStatement(deploySql);
					PreparedStatement terminalDeploySt = conn.prepareStatement(terminalDeploySql);
					PreparedStatement terminalDownloadSt = conn.prepareStatement(terminalDownloadSql)) {
				int count = 0;

				Iterator<Entry<String, Deploy>> it = tsnDeploys.entrySet().iterator();
				while (it.hasNext()) {
					Entry<String, Deploy> entry = it.next();
					String tsn = entry.getKey();
					Deploy groupDeploy = entry.getValue();

					processDeploy(deploySt, groupDeploy, deployId, username, timestamp);
					deploySt.addBatch();

					processTerminalDeploy(terminalDeploySt, terminalDeployId, tsn, deployId, username, timestamp,
							groupDeploy.getDeployTime());
					terminalDeploySt.addBatch();

					processTerminalDownload(terminalDownloadSt, terminalDownloadId, tsn, groupDeploy, deployId,
							timestamp);
					terminalDownloadSt.addBatch();

					deployId++;
					terminalDeployId++;
					terminalDownloadId++;

					count++;
					if (count % 100 == 0) {
						deploySt.executeBatch();
						deploySt.clearBatch();
						terminalDeploySt.executeBatch();
						terminalDeploySt.clearBatch();
						terminalDownloadSt.executeBatch();
						terminalDownloadSt.clearBatch();

					}

				}
				if (count % 100 != 0) {
					deploySt.executeBatch();
					deploySt.clearBatch();
					terminalDeploySt.executeBatch();
					terminalDeploySt.clearBatch();
					terminalDownloadSt.executeBatch();
					terminalDownloadSt.clearBatch();
				}

			}
		});
	}

	private void processTerminalDownload(PreparedStatement terminalDownloadSt, long terminalDownloadId, String tsn,
			Deploy deploy, long deployId, Timestamp timestamp) throws SQLException {

		terminalDownloadSt.setLong(1, terminalDownloadId);
		terminalDownloadSt.setLong(2, deployId);
		terminalDownloadSt.setString(3, tsn);
		terminalDownloadSt.setString(4, tsn);
		if (deploy.getModel() != null) {
			terminalDownloadSt.setString(5, deploy.getModel().getId());
		} else {
			terminalDownloadSt.setString(5, null);
		}
		terminalDownloadSt.setString(6, deploy.getPkg().getName());
		if (StringUtils.isEmpty(deploy.getPkg().getPgmType())) {
			terminalDownloadSt.setString(7, deploy.getPkg().getType());
		} else {
			terminalDownloadSt.setString(7, deploy.getPkg().getPgmType());
		}

		terminalDownloadSt.setString(8, deploy.getPkg().getVersion());
		terminalDownloadSt.setString(9, DownOrActvStatus.PENDING.name());
		terminalDownloadSt.setString(10, DownOrActvStatus.PENDING.name());
		if (null != deploy.getActvStartTime()) {
			terminalDownloadSt.setTimestamp(11, new Timestamp(deploy.getActvStartTime().getTime()), UTCTime.UTC_CLENDAR);
		} else {
			terminalDownloadSt.setNull(11, java.sql.Types.TIMESTAMP);
		}
		terminalDownloadSt.setTimestamp(12, new Timestamp(deploy.getDwnlStartTime().getTime()), UTCTime.UTC_CLENDAR);
		terminalDownloadSt.setTimestamp(13, timestamp, UTCTime.UTC_CLENDAR);
		terminalDownloadSt.setTimestamp(14, timestamp, UTCTime.UTC_CLENDAR);
		if (deploy.getDwnlEndTime() != null) {
			terminalDownloadSt.setTimestamp(15, new Timestamp(deploy.getDwnlEndTime().getTime()), UTCTime.UTC_CLENDAR);
		} else {
			terminalDownloadSt.setNull(15, java.sql.Types.TIMESTAMP);
		}

	}

	private void processTerminalDeploy(PreparedStatement terminalDeploySt, long terminalDeployId, String tsn,
			long deployId, String username, Timestamp timestamp, long deployTime) throws SQLException {
		terminalDeploySt.setLong(1, terminalDeployId);
		terminalDeploySt.setString(2, tsn);
		terminalDeploySt.setLong(3, deployId);
		terminalDeploySt.setString(4, DownOrActvStatus.PENDING.name());
		terminalDeploySt.setString(5, DownOrActvStatus.PENDING.name());
		terminalDeploySt.setString(6, username);
		terminalDeploySt.setTimestamp(7, timestamp, UTCTime.UTC_CLENDAR);
		terminalDeploySt.setString(8, username);
		terminalDeploySt.setTimestamp(9, timestamp, UTCTime.UTC_CLENDAR);
		terminalDeploySt.setLong(10, deployTime);

	}

	private void processDeploy(PreparedStatement deploySt, Deploy deploy, long deployId, String username,
			Timestamp timestamp) throws SQLException {
		deploySt.setLong(1, deployId);
		if (deploy.getModel() != null) {
			deploySt.setString(2, deploy.getModel().getId());
		} else {
			deploySt.setString(2, null);
		}

		if (deploy.getTrmInheritGroup() != null) {
			deploySt.setLong(3, deploy.getTrmInheritGroup().getId());
		} else {
			deploySt.setNull(3, java.sql.Types.INTEGER);
		}
		
		deploySt.setLong(4, deploy.getPkg().getId());
		if (deploy.getPkgSchema() != null) {
			deploySt.setLong(5, deploy.getPkgSchema().getId());
		} else {
			deploySt.setNull(5, java.sql.Types.INTEGER);
		}

		deploySt.setInt(6, deploy.getStatus());
		deploySt.setString(7, deploy.getDeploySource());

		deploySt.setTimestamp(8, new Timestamp(deploy.getDwnlStartTime().getTime()), UTCTime.UTC_CLENDAR);
		if (deploy.getDwnlEndTime() != null) {
			deploySt.setTimestamp(9, new Timestamp(deploy.getDwnlEndTime().getTime()), UTCTime.UTC_CLENDAR);
		} else {
			deploySt.setNull(9, java.sql.Types.TIMESTAMP);
		}
		deploySt.setInt(10, deploy.getDownReTryCount());
		deploySt.setInt(11, deploy.getActvReTryCount());

		if (deploy.getSourceDeploy() != null) {
			deploySt.setLong(12, deploy.getSourceDeploy().getId());
		} else {
			deploySt.setNull(12, java.sql.Types.INTEGER);
		}
		if(null != deploy.getActvStartTime())
		{
			deploySt.setTimestamp(13, new Timestamp(deploy.getActvStartTime().getTime()), UTCTime.UTC_CLENDAR);
		}
		else{
			deploySt.setTimestamp(13,null);
		}
		deploySt.setBoolean(14, false);
		deploySt.setBoolean(15, false);
		deploySt.setBoolean(16, false);
		deploySt.setBoolean(17, deploy.isDeletedWhenDone());
		deploySt.setString(18, deploy.getParamVersion());
		deploySt.setString(19, deploy.getParamSet());
		if (deploy.getTimeZone() != null) {
			deploySt.setString(20, deploy.getTimeZone());
		} else {
			deploySt.setString(20, null);
		}
		if (deploy.isDaylightSaving()) {
			deploySt.setBoolean(21, deploy.isDaylightSaving());
		} else {
			deploySt.setBoolean(21, false);
		}
		deploySt.setString(22, username);
		deploySt.setTimestamp(23, timestamp, UTCTime.UTC_CLENDAR);
		deploySt.setString(24, username);
		deploySt.setTimestamp(25, timestamp, UTCTime.UTC_CLENDAR);

	}

	@Override
	public DeployInfo getDeployInfo(Long deployId) {
		String hql = "select d.deploySource as deploySource, d.model.id as destModel,"
				+ " d.status as status, d.pkg.name as pkgName,d.pkg.version as pkgVersion,"
				+ " d.paramVersion as paramVersion,d.paramSet as paramSet, d.pkgSchema.id as pkgSchemaId,"
				+ " d.dwnlStartTime as dwnlStartTime,d.pkg.id as pkgId,"
				+ " d.downReTryCount as downReTryCount,d.actvReTryCount as actvReTryCount,"
				+ " d.actvStartTime as actvStartTime" + "  from Deploy d inner join d.pkg p on p.id=d.pkg.id"
				+ " where d.id=:deployId";
		return uniqueResult(createQuery(hql, DeployInfo.class, false).setParameter("deployId", deployId));

	}

	@SuppressWarnings("unchecked")
	@Override
	public <T, S extends Serializable> List<T> page(S command, int start, int length) {

		QueryCurrentGroupDeployForm form = (QueryCurrentGroupDeployForm) command;
		StringBuffer hql = new StringBuffer(1024);
		if(0 == form.getQueryType()){
		    hql.append("select g.name as deploySource,g.namePath as deploySourceNamePath,ga.ancestor.id as groupId,"
	                + " d.id as deployId, d.model.id as destModel, d.status as status,"
	                + " d.timeZone as timeZone,d.dwnlEndTime as dwnlEndTime,d.daylightSaving as daylightSaving,"
	                + " d.paramVersion as paramVersion,d.paramSet as paramSet,d.dwnlStartTime as dwnlStartTime,"
	                + " d.downReTryCount as downReTryCount,d.modifier as operator,"
	                + " d.actvReTryCount as actvReTryCount, d.actvStartTime as actvStartTime,"
	                + " d.modifyDate as modifyDate, d.latestType as latestType ");
		    hql.append(" from Deploy d ");
        }else {
            hql.append("select g.name as deploySource,g.namePath as deploySourceNamePath,ga.ancestor.id as groupId,"
                + " d.id as deployId, d.model.id as destModel, d.status as status,"
                + " d.pkg.id as pkgId, d.pkg.name as pkgName,d.pkg.version as pkgVersion,d.pkg.pgmType as pgmType,d.pkgSchema.id as pkgSchemaId,"
                + " d.timeZone as timeZone,d.dwnlEndTime as dwnlEndTime,d.daylightSaving as daylightSaving,"
                + " d.paramVersion as paramVersion,d.paramSet as paramSet,d.dwnlStartTime as dwnlStartTime,"
                + " d.downReTryCount as downReTryCount,d.modifier as operator,"
                + " d.actvReTryCount as actvReTryCount, d.actvStartTime as actvStartTime,"
                + " d.modifyDate as modifyDate, d.latestType as latestType ");
            hql.append(" from Deploy d inner join d.pkg p on p.id=d.pkg.id ");
        }
		
		hql.append(" join GroupDeploy gd on d.id=gd.deploy.id  join Group g on gd.group.id=g.id join GroupAncestor ga "
				+ " on (gd.group.id=ga.ancestor.id and ga.group.id=:groupId and ga.ancestor.id <>:groupId) or  "
				+ "(gd.group.id=ga.group.id and ga.ancestor.id=:groupId) ");
		hql.append(" where (d.dwnlEndTime is null or d.dwnlEndTime >:dwnlEndTime)");
		if(0 == form.getQueryType()){
		    hql.append(" and d.deployType = 0 ");
		}else {
		    hql.append(" and (d.deployType is null or d.deployType = 1 )");
        }
		hql.append( " order by g.namePath asc, gd.deployTime desc");
		return (List<T>) createQuery(hql.toString(), DeployInfo.class, false).setParameter("groupId", form.getGroupId())
				.setParameter("dwnlEndTime", new Date()).setFirstResult(start).setMaxResults(length).getResultList();
	}

	@Override
	public <S extends Serializable> long count(S command) {
		QueryCurrentGroupDeployForm form = (QueryCurrentGroupDeployForm) command;
		String hql = "select count(d.id) as totals from Deploy d "
				+ " join GroupDeploy gd on d.id=gd.deploy.id join GroupAncestor ga"
				+ " on (gd.group.id=ga.ancestor.id and ga.group.id=:groupId and ga.ancestor.id <>:groupId ) or"
				+ " (gd.group.id=ga.group.id and ga.ancestor.id=:groupId )  "
				+ " where (d.dwnlEndTime is null or d.dwnlEndTime >:dwnlEndTime) ";
		if(0 == form.getQueryType()){
            hql += " and d.deployType = 0 ";
        }else {
            hql += " and (d.deployType is null or d.deployType = 1 )";
        }
		return createQuery(hql, Long.class).setParameter("groupId", form.getGroupId())
				.setParameter("dwnlEndTime", new Date()).getSingleResult().intValue();
	}

	@Override
	public void deleteGroupDeploy(Long deployId) {
		String hql = "delete from GroupDeploy gp where gp.deploy.id=:deployId";
		createQuery(hql).setParameter("deployId", deployId).executeUpdate();
	}

	@Override
	public List<Object[]> getGroupDeploys(Long groupId) {

		String hql = "select d.id as deployId,gd.group.id as groupId,"
				+ " gd.deployTime as deployTime,d.model.id as destModel,"
				+ " d.daylightSaving as daylightSaving from Deploy d join"
				+ " GroupDeploy gd on gd.deploy.id=d.id  join GroupAncestor ga on "
				+ " ga.ancestor.id=gd.group.id join Group g on g.id=ga.group.id"
				+ " where g.id=:groupId and (d.dwnlEndTime is null or d.dwnlEndTime >:dwnlEndTime)";
		return createQuery(hql, Object[].class).setParameter("groupId", groupId).setParameter("dwnlEndTime", new Date())
				.getResultList();

	}

	@Override
	public List<Object[]> getSelfGroupDeploys(Collection<Long> needInheritGroupIds) {
		if (CollectionUtils.isEmpty(needInheritGroupIds)) {
			return Collections.emptyList();
		}
		String hql = "select d.id as deployId,gd.group.id as groupId,"
				+ " gd.deployTime as deployTime,d.model.id as destModel,"
				+ " d.daylightSaving as daylightSaving from Deploy d join"
				+ " GroupDeploy gd on gd.deploy.id=d.id where gd.group.id in (:needInheritGroupIds)"
				+ " and (d.dwnlEndTime is null or d.dwnlEndTime >:dwnlEndTime)";
		return createQuery(hql, Object[].class).setParameterList("needInheritGroupIds", needInheritGroupIds)
				.setParameter("dwnlEndTime", new Date()).getResultList();
	}

	@Override
	public Map<String, List<Object[]>> getAllGroupDeploys(Long batchId) {
		String hql = "select t.tid ,d.id as deployId,gd.group.id as groupId,"
				+ " gd.deployTime as deployTime,d.model.id as destModel,d.daylightSaving as "
				+ " daylightSaving from TMSPTSNS p,TerminalGroup tg,Terminal t,GroupDeploy gd,"
				+ "	Deploy d where tg.terminal.tid=t.tid and tg.group.id=gd.group.id "
				+ "	and gd.deploy.id =d.id  and t.tid=p.tsn and p.batchId=:batchId and "
				+ " (d.dwnlEndTime is null or d.dwnlEndTime>:dwnlEndTime)";

		List<Object[]> result = createQuery(hql, Object[].class).setParameter("batchId", batchId)
				.setParameter("dwnlEndTime", new Date()).getResultList();
		Map<String, List<Object[]>> map = new HashMap<String, List<Object[]>>();

		String tsn = null;

		List<Object[]> values = null;
		for (Object[] objects : result) {
			Object[] groupDeploys = new Object[5];
			tsn = (String) objects[0];
			groupDeploys[0] = objects[1];
			groupDeploys[1] = objects[2];
			groupDeploys[2] = objects[3];
			groupDeploys[3] = objects[4];
			groupDeploys[4] = objects[5];
			values = map.get(tsn);
			if (values == null) {
				values = new ArrayList<Object[]>();
				map.put(tsn, values);
			}
			values.add(groupDeploys);

		}
		return map;

	}

	@Override
	public void insertInheritDeploys(Map<String, List<Deploy>> tsnNeedToCopyGroupTask, BaseForm command) {
		Timestamp timestamp = new Timestamp(command.getRequestTime().getTime());
		String username = command.getLoginUsername();
		int length = getLength(tsnNeedToCopyGroupTask);
		if (length == 0) {
			return;
		}
		long deployRelId = DbHelper.generateId(Deploy.ID_SEQUENCE_NAME, length);
		long terminalDeployRelId = DbHelper.generateId(TerminalDeploy.ID_SEQUENCE_NAME, length);
		long terminalDownloadRelId = DbHelper.generateId(TerminalDownload.ID_SEQUENCE_NAME, length);
		String deploySql = "INSERT INTO TMSTDEPLOY (DEPLOY_ID,MODEL_ID,DEPLOY_SOURCE_GROUP_ID,PKG_ID,SCHEMA_ID,DEPLOY_STATUS,DEPLOY_SOURCE,"
				+ "DWNL_START_TM,DWNL_END_TM,DWNL_RETRY_COUNT,ACTV_RETRY_COUNT,DEPLOY_SOURCE_ID,ACTV_START_TM,DWNL_ORDER,FORCE_UPDATE,ONLY_PARAM,"
				+ "DELETED_WHEN_DONE,PARAM_VERSION,PARAM_SET,TIME_ZONE,DAYLIGHT_SAVING,CREATOR,CREATE_DATE,MODIFIER,MODIFY_DATE) "
				+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";

		String terminalDeploySql = "INSERT INTO TMSTTRM_DEPLOY (REL_ID,TRM_ID,DEPLOY_ID,"
				+ " DWNL_STATUS,ACTV_STATUS,CREATOR,CREATE_DATE,MODIFIER,MODIFY_DATE,DEPLOY_TIME)"
				+ " values(?,?,?,?,?,?,?,?,?,?)";

		String terminalDownloadSql = "insert into TMSTTRMDWNL(LOG_ID,DEPLOY_ID,TRM_ID,TRM_SN,MODEL_ID,PKG_NAME,"
				+ "	PKG_TYPE,PKG_VERSION,DWNL_STATUS,ACTV_STATUS,ACTV_SCHEDULE,DWNL_SCHEDULE,"
				+ "CREATE_DATE,MODIFY_DATE,EXPIRE_DATE) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Iterator<Entry<String, List<Deploy>>> it = tsnNeedToCopyGroupTask.entrySet().iterator();

		doWork(conn -> {
			long deployId = deployRelId;
			long terminalDeployId = terminalDeployRelId;
			long terminalDownloadId = terminalDownloadRelId;
			int count = 0;
			try (PreparedStatement deploySt = conn.prepareStatement(deploySql);
					PreparedStatement terminalDeploySt = conn.prepareStatement(terminalDeploySql);
					PreparedStatement terminalDownloadSt = conn.prepareStatement(terminalDownloadSql)) {
				while (it.hasNext()) {
					Entry<String, List<Deploy>> entry = it.next();
					String tsn = entry.getKey();
					List<Deploy> deploys = entry.getValue();

					for (Deploy deploy : deploys) {
					    if(deploy.getDeployType() == Deploy.TYPE_KEY){
					        continue;
					    }
						processDeploy(deploySt, deploy, deployId, username, timestamp);
						deploySt.addBatch();
						processTerminalDeploy(terminalDeploySt, terminalDeployId, tsn, deployId, username, timestamp,
								deploy.getDeployTime());
						terminalDeploySt.addBatch();
						processTerminalDownload(terminalDownloadSt, terminalDownloadId, tsn, deploy, deployId,
								timestamp);
						terminalDownloadSt.addBatch();
						deployId++;
						terminalDeployId++;
						terminalDownloadId++;
						count++;
						if (count % 100 == 0) {
							deploySt.executeBatch();
							deploySt.clearBatch();
							terminalDeploySt.executeBatch();
							terminalDeploySt.clearBatch();
							terminalDownloadSt.executeBatch();
							terminalDownloadSt.clearBatch();

						}
					}

				}
				if (count % 100 != 0) {
					deploySt.executeBatch();
					deploySt.clearBatch();
					terminalDeploySt.executeBatch();
					terminalDeploySt.clearBatch();
					terminalDownloadSt.executeBatch();
					terminalDownloadSt.clearBatch();

				}

			}

		});

	}

	private int getLength(Map<String, List<Deploy>> tsnNeedToCopyGroupTask) {

		Iterator<Entry<String, List<Deploy>>> it = tsnNeedToCopyGroupTask.entrySet().iterator();
		int count = 0;
		while (it.hasNext()) {
			count += it.next().getValue().size();
		}
		return count;
	}

	@Override
	public void deleteGroupDeployCascading(Long groupId) {
		String hql = "select gd.deploy.id as deployId from GroupDeploy gd ,GroupAncestor ga"
				+ " where gd.group.id= ga.group.id and ga.ancestor.id=:groupId";
		List<Long> deployIds = createQuery(hql, Long.class).setParameter("groupId", groupId).getResultList();

		String groupDeploySql = "delete from TMSTGROUP_DEPLOY where DEPLOY_ID=? ";
		doBatchExecute(groupDeploySql, deployIds.iterator(), (st, deployId) -> st.setLong(1, deployId));
		String deploySql = "delete from TMSTDEPLOY where DEPLOY_ID=?";
		doBatchExecute(deploySql, deployIds.iterator(), (st, deployId) -> st.setLong(1, deployId));

		String deployParaSql = "delete from TMSTDEPLOY_PARA where DEPLOY_ID=? ";
		doBatchExecute(deployParaSql, deployIds.iterator(), (st, deployId) -> st.setLong(1, deployId));

	}

	@Override
	public void updateDeployTime(Date requestTime, Long deployId) {
		String hql = "update GroupDeploy gd set gd.deployTime=:deployTime where gd.deploy.id=:deployId";
		createQuery(hql).setParameter("deployTime", requestTime.getTime()).setParameter("deployId", deployId)
				.executeUpdate();
	}

	@Override
	public void deleteGroupDeploys(List<Long> deployIds) {
		String sql = "delete from TMSTGROUP_DEPLOY where DEPLOY_ID=?";
		doBatchExecute(sql, deployIds.iterator(), (st, deployId) -> st.setLong(1, deployId));
	}

	@Override
	public List<Long> getGroupDownloadedTask() {
		String hql = "select gd.deploy.id from GroupDeploy gd where not exists(select d.id from Deploy d where d.sourceDeploy.id=gd.deploy.id and d.deploySourceGroup.id is not null)";
		return createQuery(hql, Long.class).getResultList();
	}

}
