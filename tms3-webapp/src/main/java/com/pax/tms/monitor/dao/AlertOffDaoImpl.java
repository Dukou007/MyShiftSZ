/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description: List/delete Alert Off
 * Revision History:		
 * Date	                 Author	                Action
 * 20170111  	         Frank           	    Modify 
 * ============================================================================		
 */	
package com.pax.tms.monitor.dao;

import java.util.List;

import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.tms.monitor.model.AlertOff;

@Repository("alertOffDaoImpl")
public class AlertOffDaoImpl extends BaseHibernateDao<AlertOff, Long> implements AlertOffDao {

	@Override
	public List<AlertOff> getListBySettingId(Long settingId) {
		String hql = " from AlertOff where settingId=:settingId order by offId desc";
		Query<AlertOff> query = this.getSession().createQuery(hql, AlertOff.class).setParameter("settingId", settingId);
		return query.getResultList();
	}

	@Override
	public int deleteBySettingId(Long settingId) {
		String hql="delete from AlertOff where settingId=:settingId";
		return this.createQuery(hql).setParameter("settingId", settingId).executeUpdate();
	}
}
