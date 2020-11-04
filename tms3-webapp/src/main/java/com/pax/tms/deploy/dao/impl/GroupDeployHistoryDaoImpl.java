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
import java.sql.Timestamp;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.common.web.form.BaseForm;
import com.pax.tms.deploy.dao.GroupDeployHistoryDao;
import com.pax.tms.deploy.domain.DeployInfo;
import com.pax.tms.deploy.model.GroupDeployHistory;
import com.pax.tms.deploy.web.form.QueryHistoryGroupDeployForm;
import com.pax.tms.res.model.PkgType;
import com.pax.tms.user.security.UTCTime;

@Repository("groupDeployHistoryDao")
public class GroupDeployHistoryDaoImpl extends BaseHibernateDao<GroupDeployHistory, Long>
		implements GroupDeployHistoryDao {

	@Override
	public void insertDeployHistorys(List<DeployInfo> deployInfos, BaseForm form) {

		Timestamp timestamp = new Timestamp(form.getRequestTime().getTime());
		String username = form.getLoginUsername();

		String sql = "insert into TMSTGROUPDEPLOY_HISTORY (GROUP_DEPLOY_HIS_ID,MODEL_ID,GROUP_ID,"
				+ " DEPLOY_SOURCE,DEPLOY_STATUS,PKG_ID,PKG_NAME,PKG_VERSION, SCHEMA_ID,"
				+ " PARAM_VERSION,PARAM_SET,DWNL_START_TM,DOWN_RETRY_COUNT,ACTV_RETRY_COUNT,"
				+ " ACTV_START_TM,DWNL_ORDER,FORCE_UPDATE,ONLY_PARAM,"
				+ " CREATOR,CREATE_DATE,MODIFIER,MODIFY_DATE) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		doBatchInsert(sql, GroupDeployHistory.ID_SEQUENCE_NAME, deployInfos, (st, deployInfo, deployHistoryId) -> {
			st.setLong(1, deployHistoryId);
			st.setString(2, deployInfo.getDestModel());
			st.setLong(3, deployInfo.getGroupId());
			st.setString(4, deployInfo.getDeploySource());
			st.setInt(5, deployInfo.getStatus());
			st.setLong(6, deployInfo.getPkgId());
			st.setString(7, deployInfo.getPkgName());
			st.setString(8, deployInfo.getPkgVersion());
			st.setLong(9, deployInfo.getPkgSchemaId());
			st.setString(10, deployInfo.getParamVersion());
			st.setString(11, deployInfo.getParamSet());
			st.setTimestamp(12, new Timestamp(deployInfo.getDwnlStartTime().getTime()), UTCTime.UTC_CLENDAR);
			st.setInt(13, deployInfo.getDownReTryCount() == null ? 1 : deployInfo.getDownReTryCount());
			st.setInt(14, deployInfo.getActvReTryCount() == null ? 1 : deployInfo.getActvReTryCount());
			st.setTimestamp(15, new Timestamp(deployInfo.getActvStartTime().getTime()), UTCTime.UTC_CLENDAR);
			st.setBoolean(16, deployInfo.isDwnlOrder());
			st.setBoolean(17, deployInfo.isForceUpdate());
			st.setBoolean(18, deployInfo.isOnlyParam());
			st.setString(19, username);
			st.setTimestamp(20, timestamp, UTCTime.UTC_CLENDAR);
			st.setString(21, username);
			st.setTimestamp(22, timestamp, UTCTime.UTC_CLENDAR);

		});

	}

	@SuppressWarnings("unchecked")
	@Override
	public <T, S extends Serializable> List<T> page(S command, int start, int length) {
		QueryHistoryGroupDeployForm form = (QueryHistoryGroupDeployForm) command;

		String hql = "select gdh.pkgSchema.id as pkgSchemaId, gdh.deploySource as deploySource,gdh.pkg.id as pkgId,"
				+ " gdh.model.id as destModel,gdh.status as status,gdh.modifier as operator,"
				+ " gdh.pkg.name as pkgName,gdh.pkg.version as pkgVersion,gdh.paramVersion as paramVersion,"
				+ " gdh.paramSet as paramSet, gdh.dwnlStartTime as dwnlStartTime,gdh.pkg.id as pkgId,"
				+ " gdh.downReTryCount as downReTryCount, gdh.actvReTryCount as actvReTryCount,"
				+ " gdh.actvStartTime as actvStartTime,gdh.modifyDate as modifyDate from GroupDeployHistory gdh,"
				+ " GroupAncestor ga where gdh.group.id=ga.group.id and ga.ancestor.id=:groupId ";
		if(0 == form.getQueryType()){
            hql += " and gdh.pkg.type = :pkgType";
        }else {
            hql += " and gdh.pkg.type != :pkgType";
        }
		hql += " order by gdh.modifyDate desc";
		return (List<T>) createQuery(hql, DeployInfo.class, false).setParameter("groupId", form.getGroupId())
		        .setParameter("pkgType", PkgType.OFFLINEKEY.getPkgName()).setFirstResult(start).setMaxResults(length).getResultList();
	}

	@Override
	public <S extends Serializable> long count(S command) {

		QueryHistoryGroupDeployForm form = (QueryHistoryGroupDeployForm) command;
		String hql = "select count(gdh.id) as totals from GroupDeployHistory gdh,GroupAncestor ga "
				+ " where gdh.group.id=ga.group.id and ga.ancestor.id=:groupId";
		if(0 == form.getQueryType()){
            hql += " and gdh.pkg.type = :pkgType";
        }else {
            hql += " and gdh.pkg.type != :pkgType";
        }
		return createQuery(hql, Long.class).setParameter("groupId", form.getGroupId())
		        .setParameter("pkgType", PkgType.OFFLINEKEY.getPkgName()).getSingleResult().intValue();
	}

	@Override
	public boolean isDeploy(Long pkgSchemaId) {
		String hql = "select count(gdh.id) from GroupDeployHistory gdh  where gdh.pkgSchema.id=:pkgSchemaId ";
		return createQuery(hql, Long.class).setParameter("pkgSchemaId", pkgSchemaId).getSingleResult().intValue() > 0
				? true : false;
	}

}
