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
 * 20170209              Aaron                  Modify
 * ============================================================================		
 */
package com.pax.tms.res.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.TemporalType;

import org.springframework.stereotype.Repository;

import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.tms.res.dao.PkgUsageInfoDao;
import com.pax.tms.res.model.PkgUsageInfo;

@Repository("pkgUsageInfoDaoImpl")
public class PkgUsageInfoDaoImpl extends BaseHibernateDao<PkgUsageInfo, Long> implements PkgUsageInfoDao {

	@Override
	public List<Long> getPkgIds() {
		String hql = "select p.pkg.id from PkgUsageInfo p";
		return createQuery(hql, Long.class).getResultList();
	}

	@Override
	public PkgUsageInfo getPkgUsageInfoByPkgId(Long pkgId) {
		String hql = "select p from PkgUsageInfo p where p.pkg.id=:pkgId";
		return uniqueResult(createQuery(hql, PkgUsageInfo.class).setParameter("pkgId", pkgId));
	}

	@Override
	public List<Long> getNotUsedPkgIds(Date when) {
		String hql = "select p.pkg.id from PkgUsageInfo p where p.lastOptTime<=:when";
		return createQuery(hql, Long.class).setParameter("when", when, TemporalType.TIMESTAMP).getResultList();
	}

	@Override
	public void clearPkgAndUsageInfo(Date when) {
		String hql = "delete from PkgUsageInfo p where p.lastOptTime<=:when ";
		this.getSession().createQuery(hql).setParameter("when", when, TemporalType.TIMESTAMP).executeUpdate();
	}

}
