/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description: get Alert Setting
 * Revision History:		
 * Date	                 Author	                Action
 * 20170111  	         Frank           	    Modify 
 * ============================================================================		
 */	
package com.pax.tms.monitor.dao;

import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.tms.monitor.model.AlertSetting;

@Repository("alertSettingDaoImpl")
public class AlertSettingDaoImpl extends BaseHibernateDao<AlertSetting, Long> implements AlertSettingDao {

	@Override
	public AlertSetting findByGroupId(Long groupId) {
		String hql = "from AlertSetting where groupId=:groupId";
		Query<AlertSetting> query = this.createQuery(hql, AlertSetting.class).setParameter("groupId", groupId);
		return super.uniqueResult(query);
	}
}
