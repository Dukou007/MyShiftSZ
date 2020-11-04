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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.TemporalType;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;

import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.common.web.form.BaseForm;
import com.pax.tms.deploy.dao.DeployDao;
import com.pax.tms.deploy.domain.DeployInfo;
import com.pax.tms.deploy.model.Deploy;
import com.pax.tms.deploy.web.form.GroupChangeParaForm;
import com.pax.tms.user.security.UTCTime;

@Repository("deployDaoImpl")
public class DeployDaoImpl extends BaseHibernateDao<Deploy, Long> implements DeployDao {

    private final String GROUP_ID = "groupId";
    private final String BATCH_ID = "batchId";
	@Override
	public List<DeployInfo> getDeployInfos(Long groupId, String destModel, Long pkgId) {

		String hql = "select g.name as deploySource,gd.group.id as groupId,d.modifier as operator,"
				+ " d.model.id as destModel,d.pkg.id as pkgId,d.status as status,d.pkgSchema.id as pkgSchemaId,"
				+ " d.paramVersion as paramVersion,d.paramSet as paramSet,"
				+ " d.pkg.name as pkgName,d.pkg.version as pkgVersion,"
				+ " d.pkgSchema.id as pkgSchemaId, d.dwnlStartTime as dwnlStartTime, d.actvStartTime as actvStartTime,"
				+ " d.downReTryCount as downReTryCount, d.actvReTryCount as actvReTryCount"
				+ " from Deploy d inner join d.pkg p on p.id=d.pkg.id" + " join GroupDeploy gd  on d.id=gd.deploy.id "
				+ " join Group g on gd.group.id=g.id where g.id=:groupId and "
				+ " d.model.id=:destModel and d.pkg.id=:pkgId";

		return createQuery(hql, DeployInfo.class, false).setParameter(GROUP_ID, groupId)
				.setParameter("destModel", destModel).setParameter("pkgId", pkgId).getResultList();

	}

	@Override
	public List<Long> getDeployIds(Long groupId, String destModel, Long pkgId) {
		String hql = " select d.id from Deploy d, GroupAncestor ga ,GroupDeploy gd "
				+ " where gd.deploy.id=d.id and gd.group.id=ga.group.id"
				+ " and  ga.ancestor.id=:groupId and d.model.id=:destModel and d.pkg.id=:pkgId";
		return createQuery(hql, Long.class).setParameter(GROUP_ID, groupId).setParameter("destModel", destModel)
				.setParameter("pkgId", pkgId).getResultList();
	}

	@Override
	public void updateDeploys(List<Long> deployIds, Deploy deploy, GroupChangeParaForm command) {

		Timestamp timestamp = new Timestamp(command.getRequestTime().getTime());
		String username = command.getLoginUsername();
		String sql = "update TMSTDEPLOY set SCHEMA_ID=?,PARAM_VERSION=?,PARAM_SET=?,PARAM_STATUS=?,DWNL_START_TM=?,"
				+ " DWNL_RETRY_COUNT=?,ACTV_RETRY_COUNT=?,ACTV_START_TM=?,MODIFIER=?,MODIFY_DATE=? where DEPLOY_ID=?";
		doBatchExecute(sql, deployIds.iterator(), (st, deployId) -> {

			st.setLong(1, command.getPkgSchemaId());
			st.setString(2, deploy.getParamVersion());
			st.setString(3, deploy.getParamSet());
			st.setInt(4, 0);
			st.setTimestamp(5, new Timestamp(command.getDwnlStartTime().getTime()), UTCTime.UTC_CLENDAR);
			st.setInt(6, command.getDownReTryCount() == null ? 1 : command.getDownReTryCount());
			st.setInt(7, command.getActvReTryCount() == null ? 1 : command.getActvReTryCount());
			st.setTimestamp(8, new Timestamp(command.getActvStartTime().getTime()), UTCTime.UTC_CLENDAR);
			st.setString(9, username);
			st.setTimestamp(10, timestamp, UTCTime.UTC_CLENDAR);
			st.setLong(11, deployId);

		});

	}

	@Override
	public boolean isDeploy(Long pkgSchemaId) {
		String hql = "select count(d.id) from Deploy d where d.pkgSchema.id=:pkgSchemaId ";
		return createQuery(hql, Long.class).setParameter("pkgSchemaId", pkgSchemaId).getSingleResult().intValue() > 0
				? true : false;
	}

	@Override
	public List<Deploy> getUnDealDeployList() {
		String hql = " select td.deploy from TerminalDeploy td "
				+ " where td.deploy.paramStatus < 1 and td.deploy.paramVersion is not null ";
		return super.createQuery(hql, Deploy.class).getResultList();
	}

	@Override
	public void deleteDeployByTsns(Long batchId) {

		String hql = "select d.id from TMSPTSNS p,Deploy d join TerminalDeploy td on d.id=td.deploy.id"
				+ "	join Terminal t on td.terminal.id=t.tid where t.tid=p.tsn and p.batchId=:batchId ";
		List<Long> deployIds = createQuery(hql, Long.class).setParameter(BATCH_ID, batchId).getResultList();
		if (CollectionUtils.isEmpty(deployIds)) {
			return;
		}
		String sql = "delete from TMSTDEPLOY where DEPLOY_ID=?";
		doBatchExecute(sql, deployIds.iterator(), (st, deployId) -> {
			st.setLong(1, deployId);
		});
	}

	@Override
	public Map<String, List<Long>> getTsnInheritSourceGroupId(Long batchId, Long groupId) {

		String hql = "select t.tid,d.deploySourceGroup.id as groupId from TMSPTSNS p,Terminal t left join "
				+ " TerminalDeploy td on t.id=td.terminal.id left join Deploy d on td.deploy.id=d.id "
				+ " where d.deploySourceGroup.id in (select distinct ga.ancestor.id as groupId from "
				+ " GroupAncestor ga join Group g on g.id=ga.group.id "
				+ " where g.id=:groupId) and t.tid=p.tsn and p.batchId=:batchId";

		List<Object[]> result = createQuery(hql, Object[].class).setParameter(BATCH_ID, batchId)
				.setParameter(GROUP_ID, groupId).getResultList();

		Map<String, List<Long>> map = new HashMap<>();
		String tsn = null;
		Long sourceGroupId = null;
		List<Long> values = null;
		for (Object[] objects : result) {
			tsn = (String) objects[0];
			sourceGroupId = (Long) objects[1];
			values = map.get(tsn);
			if (values == null) {
				values = new ArrayList<>();
				map.put(tsn, values);
			}
			if (sourceGroupId != null) {
				values.add(sourceGroupId);
			}

		}

		return map;

	}

	@Override
	public Map<String, List<Long>> getTsnAllInheritDeployIds(Long batchId) {
		String hql = "select t.tid,d.id as deployId from TMSPTSNS p,Terminal t left join "
				+ "	TerminalDeploy td on t.id=td.terminal.id left join Deploy d"
				+ " on td.deploy.id=d.id where d.deploySourceGroup.id is not null "
				+ "	and t.tid=p.tsn and p.batchId=:batchId ";

		List<Object[]> result = createQuery(hql, Object[].class).setParameter(BATCH_ID, batchId).getResultList();
		Map<String, List<Long>> map = new HashMap<>();
		String tsn = null;
		Long deployId = null;
		List<Long> values = null;
		for (Object[] objects : result) {
			tsn = (String) objects[0];
			deployId = (Long) objects[1];
			values = map.get(tsn);
			if (values == null) {
				values = new ArrayList<Long>();
				map.put(tsn, values);
			}
			if (deployId != null) {
				values.add(deployId);
			}

		}

		return map;

	}

	@Override
	public Map<String, List<Long>> getTsnInheritDeployIds(Long batchId, Long groupId) {

		String hql = "select t.tid,d.id as deployId from TMSPTSNS p,Terminal t left join "
				+ "	TerminalDeploy td on t.id=td.terminal.id left join Deploy d"
				+ " on td.deploy.id=d.id join GroupAncestor ga on d.deploySourceGroup.id=ga.group.id"
				+ "	where ga.ancestor.id=:groupId and t.tid=p.tsn and p.batchId=:batchId ";

		List<Object[]> result = createQuery(hql, Object[].class).setParameter(BATCH_ID, batchId)
				.setParameter(GROUP_ID, groupId).getResultList();
		Map<String, List<Long>> map = new HashMap<>();
		String tsn = null;
		Long deployId = null;
		List<Long> values = null;
		for (Object[] objects : result) {
			tsn = (String) objects[0];
			deployId = (Long) objects[1];
			values = map.get(tsn);
			if (values == null) {
				values = new ArrayList<Long>();
				map.put(tsn, values);
			}
			if (deployId != null) {
				values.add(deployId);
			}

		}

		return map;

	}

	@Override
	public void deleteTaskFromGroupCascading(Collection<Long> deleteDeployIds) {
		String sql = "delete from TMSTDEPLOY where DEPLOY_ID=?";
		doBatchExecute(sql, deleteDeployIds.iterator(), (st, deployId) -> st.setLong(1, deployId));

	}

	@Override
	public List<Long> getDeployIds(Long batchId) {
		String hql = "select td.deploy.id as deployId from TMSPTSNS p,TerminalDeploy td "
				+ "where td.terminal.tid=p.tsn and p.batchId=:batchId ";
		return createQuery(hql, Long.class).setParameter(BATCH_ID, batchId).getResultList();
	}

	@Override
	public List<Object[]> getTerminalSelfTask(String tsn) {
		String hql = " select d.id as deployId,td.deployTime as deployTime,d.daylightSaving"
				+ "  as daylightSaving from Deploy d,TerminalDeploy td where td.deploy.id=d.id"
				+ " and td.terminal.tid=:tsn and (d.dwnlEndTime is null or d.dwnlEndTime >:dwnlEndTime) "
				+ " and d.deploySourceGroup is null ";
		return createQuery(hql, Object[].class).setParameter("dwnlEndTime", new Date()).setParameter("tsn", tsn)
				.getResultList();
	}

	@Override
	public Map<String, List<Long>> getDeployTimeTsns(Collection<String> tsnNeedToCopyTask, Long batchId) {
		if (CollectionUtils.isEmpty(tsnNeedToCopyTask)) {
			return Collections.emptyMap();
		}
		String hql = "select td.terminal.tid,td.deployTime from TerminalDeploy td,TMSPTSNS p where "
				+ "td.terminal.tid=p.tsn and p.batchId=:batchId ";
		List<Object[]> result = createQuery(hql, Object[].class).setParameter(BATCH_ID, batchId).getResultList();
		Map<String, List<Long>> map = new HashMap<>();
		String tsn = null;
		List<Long> values = null;
		for (Object[] objects : result) {
			tsn = (String) objects[0];
			Long deployTime = (Long) objects[1];
			values = map.get(tsn);
			if (values == null) {
				values = new ArrayList<>();
				map.put(tsn, values);
			}
			values.add(deployTime);
		}
		return map;

	}

	@Override
	public List<Long> getTerminalInheritDeployIds(Long groupId) {
		String hql = "select d.id as deployId from Deploy d where d.deploySourceGroup.id=:groupId";
		return createQuery(hql, Long.class).setParameter(GROUP_ID, groupId).getResultList();
	}

	@Override
	public void updateDeployStatus(List<Long> deployIds, int status, BaseForm form) {
		String sql = "update TMSTDEPLOY set DEPLOY_STATUS=?, MODIFIER=?, MODIFY_DATE=? where DEPLOY_ID=?";

		Timestamp timestamp = new Timestamp(form.getRequestTime().getTime());
		String username = form.getLoginUsername();

		doBatchExecute(sql, deployIds.iterator(), (st, deployId) -> {
			st.setInt(1, status);
			st.setString(2, username);
			st.setTimestamp(3, timestamp, UTCTime.UTC_CLENDAR);
			st.setLong(4, deployId);
		});

	}

	@Override
	public void replaceDeployPkgIds(Collection<Long> oldPkgIds, Long pkgId) {
		String sql = "update TMSTDEPLOY set PKG_ID=? where PKG_ID=? ";
		doBatchExecute(sql, oldPkgIds.iterator(), (st, oldPkgId) -> {
			st.setLong(1, pkgId);
			st.setLong(2, oldPkgId);

		});

	}

	@Override
	public List<Long> getDeployPkgIds() {
		String hql = "select distinct d.pkg.id from Deploy d ";
		return createQuery(hql, Long.class).getResultList();
	}

	@Override
	public List<Long> getOverdueDeployIds(Date when) {
		String hql = "select d.id from Deploy d where d.dwnlEndTime<=:when";
		return createQuery(hql, Long.class).setParameter("when", when, TemporalType.TIMESTAMP).getResultList();
	}
	

}
