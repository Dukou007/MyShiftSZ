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
import java.util.List;

import org.springframework.stereotype.Repository;

import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.tms.deploy.dao.TerminalDeployHistoryDao;
import com.pax.tms.deploy.domain.DeployInfo;
import com.pax.tms.deploy.model.TerminalDeployHistory;
import com.pax.tms.deploy.web.form.QueryHistotyTsnDeployForm;
import com.pax.tms.res.model.PkgType;

@Repository("terminalDeployHistoryDaoImpl")
public class TerminalDeployHistoryDaoImpl extends BaseHibernateDao<TerminalDeployHistory, Long>
		implements TerminalDeployHistoryDao {

	@SuppressWarnings("unchecked")
	@Override
	public <T, S extends Serializable> List<T> page(S command, int start, int length) {
		QueryHistotyTsnDeployForm form = (QueryHistotyTsnDeployForm) command;
		String hql = "select tdh.dwnlTime as dwnlTime,tdh.actvTime as actvTime ,tdh.pkgSchema.id as pkgSchemaId,"
				+ " tdh.downStatus as dwnStatus, tdh.actvStatus as actvStatus,"
				+ " tdh.deploySource as deploySource,tdh.modifier as operator,tdh.model.id as destModel,"
				+ " tdh.paramVersion as paramVersion, tdh.paramSet as paramSet,"
				+ " tdh.status as status, tdh.pkg.name as pkgName,tdh.pkg.version as pkgVersion,"
				+ " tdh.dwnlStartTime as dwnlStartTime,tdh.pkg.id as pkgId,"
				+ " tdh.downReTryCount as downReTryCount, tdh.actvReTryCount as actvReTryCount,"
				+ " tdh.actvStartTime as actvStartTime,tdh.modifyDate as modifyDate "
				+ " from TerminalDeployHistory tdh where tdh.terminal.tid=:tsn";
		if(0 == form.getQueryType()){
            hql += " and tdh.pkg.type = :pkgType";
        }else {
            hql += " and tdh.pkg.type != :pkgType";
        }
		hql += " order by tdh.modifyDate desc ";

		return (List<T>) createQuery(hql, DeployInfo.class, false).setParameter("tsn", form.getTsn()).setParameter("pkgType", PkgType.OFFLINEKEY.getPkgName())
				.setFirstResult(start).setMaxResults(length).getResultList();
	}

	@Override
	public <S extends Serializable> long count(S command) {
		QueryHistotyTsnDeployForm form = (QueryHistotyTsnDeployForm) command;
		String hql = "select count(tdh.id) as totals from TerminalDeployHistory tdh where tdh.terminal.tid=:tsn";
		if(0 == form.getQueryType()){
            hql += " and tdh.pkg.type = :pkgType";
        }else {
            hql += " and tdh.pkg.type != :pkgType";
        }
		return createQuery(hql, Long.class).setParameter("tsn", form.getTsn()).setParameter("pkgType", PkgType.OFFLINEKEY.getPkgName()).getSingleResult().intValue();
	}

	@Override
	public boolean isDeploy(Long pkgSchemaId) {
		String hql = "select count(tdh.id) from TerminalDeployHistory tdh  where tdh.pkgSchema.id=:pkgSchemaId ";
		return createQuery(hql, Long.class).setParameter("pkgSchemaId", pkgSchemaId).getSingleResult().intValue() > 0
				? true : false;
	}

}
