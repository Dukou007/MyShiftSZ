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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.springframework.stereotype.Repository;

import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.common.dao.hibernate.MyAliasToBeanResultTransformer;
import com.pax.tms.deploy.dao.TerminalDeployDao;
import com.pax.tms.deploy.domain.DeployInfo;
import com.pax.tms.deploy.domain.TerminalDeployInfo;
import com.pax.tms.deploy.model.TerminalDeploy;
import com.pax.tms.deploy.web.form.QueryCurrentTsnDeployForm;
import com.pax.tms.res.model.PkgType;

@Repository("terminalDeployDaoImpl")
public class TerminalDeployDaoImpl extends BaseHibernateDao<TerminalDeploy, Long> implements TerminalDeployDao {

	private ResultTransformer terminalDeployRT = new MyAliasToBeanResultTransformer(TerminalDeployInfo.class);

	@Override
	public List<TerminalDeployInfo> getTerminalDeployInfos(Long groupId, String destModel) {
		String sql = null;
		if (StringUtils.isEmpty(destModel)) {
			sql = "SELECT A.TRM_ID as tsn , B.DEPLOY_TIME as deployTime, C.DEPLOY_ID as deployId, 	"
					+ " C.PARAM_SET as paramSet, D.PKG_NAME as pkgName,C.PARAM_VERSION as paramVersion		"
					+ " FROM (																				"
					+ "		SELECT MAX(TD.DEPLOY_TIME) DEPLOY_TIME, T.TRM_ID 								"
					+ "		FROM  TMSTTERMINAL T 															"
					+ " 	INNER JOIN TMSTTRM_GROUP TG ON T.TRM_ID=TG.TRM_ID  								"
					+ "		INNER JOIN PUBTGROUP G ON  G.GROUP_ID=TG.GROUP_ID								"
					+ "		AND G.GROUP_ID=:groupId														"
					+ "		LEFT JOIN TMSTTRM_DEPLOY TD	ON TD.TRM_ID=T.TRM_ID								"
					+ "		GROUP BY T.TRM_ID  ) A 															"
					+ " LEFT JOIN TMSTTRM_DEPLOY B ON A.TRM_ID=B.TRM_ID AND B.DEPLOY_TIME=A.DEPLOY_TIME		"
					+ " LEFT JOIN TMSTDEPLOY C ON B.DEPLOY_ID=C.DEPLOY_ID 									"
					+ " LEFT JOIN TMSTPACKAGE D ON C.PKG_ID=D.PKG_ID										";

		} else {
			sql = "SELECT A.TRM_ID as tsn , B.DEPLOY_TIME as deployTime, C.DEPLOY_ID as deployId, 	"
					+ " C.PARAM_SET as paramSet, D.PKG_NAME as pkgName,C.PARAM_VERSION as paramVersion		"
					+ " FROM (																				"
					+ "		SELECT MAX(TD.DEPLOY_TIME) DEPLOY_TIME, T.TRM_ID 								"
					+ "		FROM  TMSTTERMINAL T 															"
					+ " 	INNER JOIN TMSTTRM_GROUP TG ON T.TRM_ID=TG.TRM_ID  								"
					+ "		INNER JOIN PUBTGROUP G ON  G.GROUP_ID=TG.GROUP_ID								"
					+ "		AND G.GROUP_ID=:groupId														"
					+ "		LEFT JOIN TMSTTRM_DEPLOY TD	ON TD.TRM_ID=T.TRM_ID								"
					+ "		WHERE T.MODEL_ID=:destModel														"
					+ "		GROUP BY T.TRM_ID  ) A 															"
					+ " LEFT JOIN TMSTTRM_DEPLOY B ON A.TRM_ID=B.TRM_ID AND B.DEPLOY_TIME=A.DEPLOY_TIME		"
					+ " LEFT JOIN TMSTDEPLOY C ON B.DEPLOY_ID=C.DEPLOY_ID 									"
					+ " LEFT JOIN TMSTPACKAGE D ON C.PKG_ID=D.PKG_ID										";

		}

		Map<String, Type> scalarMap = new HashMap<>(5);
		scalarMap.put("tsn", StandardBasicTypes.STRING);
		scalarMap.put("deployId", StandardBasicTypes.LONG);
		scalarMap.put("paramSet", StandardBasicTypes.STRING);
		scalarMap.put("pkgName", StandardBasicTypes.STRING);
		scalarMap.put("paramVersion", StandardBasicTypes.STRING);
		NativeQuery<TerminalDeployInfo> query = super.createNativeQuery(sql, TerminalDeployInfo.class, scalarMap);
		if (StringUtils.isEmpty(destModel)) {
			return query.setParameter("groupId", groupId).getResultList();
		} else {
			return query.setParameter("groupId", groupId).setParameter("destModel", destModel).getResultList();
		}

	}

	@Override
	public TerminalDeployInfo getTerminalLastedDeploy(String terminalId) {
		String sql = "SELECT TD.TRM_ID AS tsn, TD.DEPLOY_TIME AS deployTime, D.DEPLOY_ID AS deployId,	"
				+ " D.PARAM_SET AS paramSet, P.PKG_NAME AS pkgName, D.PARAM_VERSION AS paramVersion 	"
				+ " FROM TMSTTRM_DEPLOY TD																"
				+ " INNER JOIN TMSTDEPLOY D ON TD.DEPLOY_ID=D.DEPLOY_ID									"
				+ " INNER JOIN TMSTPACKAGE P ON P.PKG_ID=D.PKG_ID										"
				+ " WHERE TD.TRM_ID=:terminalId															"
				+ " ORDER BY TD.DEPLOY_TIME DESC														";
		NativeQuery<TerminalDeployInfo> query = super.createNativeQuery(sql, TerminalDeployInfo.class);
		query.setParameter("terminalId", terminalId).setMaxResults(1);
		super.setResultTransformer(query, terminalDeployRT);
		return super.uniqueResult(query);
	}

	@Override
	public DeployInfo getDeployInfo(Long deployId) {

		String hql = "select td.terminal.tid as tsn,td.dwnlTime as dwnlTime,d.pkg.id as pkgId,"
				+ " td.actvTime as actvTime, td.downStatus as dwnStatus,"
				+ " td.actvStatus as actvStatus,d.deploySource as deploySource,d.pkgSchema.id as pkgSchemaId,"
				+ " d.modifier as operator,d.model.id as destModel,d.status as status,"
				+ " d.pkg.name as pkgName, d.pkg.version as pkgVersion,"
				+ " d.paramVersion as paramVersion, d.paramSet as paramSet,"
				+ " d.dwnlStartTime as dwnlStartTime, d.downReTryCount as downReTryCount,"
				+ " d.actvReTryCount as actvReTryCount, d.actvStartTime as actvStartTime"
				+ " from Deploy d inner join d.pkg p on p.id=d.pkg.id"
				+ " join TerminalDeploy td on d.id=td.deploy.id where d.id=:deployId";
		return uniqueResult(createQuery(hql, DeployInfo.class, false).setParameter("deployId", deployId));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T, S extends Serializable> List<T> page(S command, int start, int length) {
		QueryCurrentTsnDeployForm form = (QueryCurrentTsnDeployForm) command;
		StringBuffer hql = new StringBuffer(1000);
		hql.append("select td.terminal.tid as tsn,d.id as deployId,d.pkg.id as pkgId,"
				+ "  d.pkgSchema.id as pkgSchemaId,td.dwnlTime as dwnlTime,d.timeZone as timeZone, "
				+ "  td.actvTime as actvTime, td.downStatus as dwnStatus,d.daylightSaving as daylightSaving,"
				+ "  td.actvStatus as actvStatus,g.name as deploySource,g.namePath as deploySourceNamePath,"
				+ "  d.paramVersion as paramVersion,d.paramSet as paramSet,d.dwnlEndTime as dwnlEndTime,"
				+ "  d.modifier as operator,d.model.id as destModel, d.status as status,"
				+ "  d.pkg.name as pkgName, d.pkg.version as pkgVersion,"
				+ "	 d.pkg.pgmType as pgmType,d.dwnlStartTime as dwnlStartTime,"
				+ "  d.downReTryCount as downReTryCount, d.actvReTryCount as actvReTryCount,"
				+ "  d.actvStartTime as actvStartTime,d.modifyDate as modifyDate");
		hql.append(" from Deploy d inner join d.pkg p on p.id=d.pkg.id"
				+ "  left join Group g on d.deploySourceGroup.id=g.id"
				+ "  inner join TerminalDeploy td  on d.id=td.deploy.id where td.terminal.tid=:tsn"
				+ "  and (d.dwnlEndTime is null or d.dwnlEndTime >:dwnlEndTime)");
		if(0 == form.getQueryType()){
            hql.append(" and d.pkg.type = :pkgType ");
        }else {
            hql.append(" and d.pkg.type != :pkgType");
        }
		hql.append(" order by td.deployTime desc");

		return (List<T>) createQuery(hql.toString(), DeployInfo.class, false).setParameter("tsn", form.getTsn()).setParameter("pkgType", PkgType.OFFLINEKEY.getPkgName())
				.setParameter("dwnlEndTime", new Date()).setFirstResult(start).setMaxResults(length).getResultList();
	}

	@Override
	public <S extends Serializable> long count(S command) {
		QueryCurrentTsnDeployForm form = (QueryCurrentTsnDeployForm) command;
		String hql = " select count(d.id) as totals from Deploy d "
				+ "   inner join TerminalDeploy td  on d.id=td.deploy.id where td.terminal.tid=:tsn"
				+ "   and (d.dwnlEndTime is null or d.dwnlEndTime >:dwnlEndTime)";
		if(0 == form.getQueryType()){
            hql += " and d.pkg.type = :pkgType ";
        }else {
            hql += " and d.pkg.type != :pkgType";
        }
		return createQuery(hql, Long.class).setParameter("tsn", form.getTsn()).setParameter("dwnlEndTime", new Date())
		        .setParameter("pkgType", PkgType.OFFLINEKEY.getPkgName()).getSingleResult().intValue();

	}

	@Override
	public void deleteTerminalDeploy(String tsn, Long deployId) {
		String hql = "delete from TerminalDeploy td where td.terminal.tid=:tsn and td.deploy.id=:deployId";
		createQuery(hql).setParameter("tsn", tsn).setParameter("deployId", deployId).executeUpdate();

	}

	@Override
	public void deleteTerminalDeploy(List<String> tsnList) {
		String sql = "delete from TMSTTRM_DEPLOY where TRM_ID=?";
		doBatchExecute(sql, tsnList.iterator(), (st, tsn) -> st.setString(1, tsn));

	}

	@Override
	public void deleteTerminalDeploys(Map<String, List<Long>> tsnDeployIdsMap) {

		String hql = "delete from TerminalDeploy td where td.terminal.tid=:tsn and td.deploy.id "
				+ " in (:deployIds)	";
		Iterator<Entry<String, List<Long>>> it = tsnDeployIdsMap.entrySet().iterator();

		while (it.hasNext()) {

			Entry<String, List<Long>> entry = it.next();
			String tsn = entry.getKey();
			List<Long> deployIds = entry.getValue();
			if (CollectionUtils.isEmpty(deployIds)) {
				continue;
			}
			createQuery(hql).setParameter("tsn", tsn).setParameterList("deployIds", deployIds).executeUpdate();
		}

	}

	@Override
	public void updateDeployTime(Date requestTime, Long deployId) {
		String hql = "update TerminalDeploy td set td.deployTime=:deployTime where td.deploy.id=:deployId";
		createQuery(hql).setParameter("deployTime", requestTime.getTime()).setParameter("deployId", deployId)
				.executeUpdate();
	}

	@Override
	public List<Long> getDeployIdsByGroupId(Long deployId) {
		String hql = "select d.id as deployId from Deploy d where d.sourceDeploy.id=:sourceDeployId";
		return createQuery(hql, Long.class).setParameter("sourceDeployId", deployId).getResultList();
	}

	@Override
	public void deleteTerminalDeploys(List<Long> deployIds) {
		String sql = "delete from TMSTTRM_DEPLOY where DEPLOY_ID=?";
		doBatchExecute(sql, deployIds.iterator(), (st, deployId) -> st.setLong(1, deployId));
	}

	@Override
	public List<Long> getInheritGroupDeployIds(Long groupId) {
		String hql = "select d.id from Deploy d where d.deploySourceGroup.id=:groupId";
		return createQuery(hql, Long.class).setParameter("groupId", groupId).getResultList();
	}

	@Override
	public TerminalDeploy getTerminalDeploy(Long deployId, String terminalId) {
		String hql = "select td from TerminalDeploy td where td.deploy.id=:deployId and td.terminal.tid=:terminalId";
		Query<TerminalDeploy> query = createQuery(hql, TerminalDeploy.class).setParameter("deployId", deployId)
				.setParameter("terminalId", terminalId);
		return uniqueResult(query);
	}

	@Override
	public List<String> getTsnsByDeployGroupId(Long deployId) {

		String hql = "select td.terminal.id from TerminalDeploy td,Deploy d where td.deploy.id=d.id and "
				+ " d.sourceDeploy.id=:deployId ";
		List<String> tsnList = createQuery(hql, String.class).setParameter("deployId", deployId).getResultList();

		return tsnList;

	}

	@Override
	public List<Long> getLatedDeployId(String terminalId) {
		String hql = " select td.deploy.id from TerminalDeploy td where td.terminal.tid=:terminalId order by td.modifyDate desc";
		return createQuery(hql, Long.class).setParameter("terminalId", terminalId).getResultList();
	}

}
