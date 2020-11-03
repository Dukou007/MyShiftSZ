/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description: List/get Condition/find Setting
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
import com.pax.tms.monitor.model.AlertCondition;
import com.pax.tms.monitor.model.AlertSetting;

@Repository("alertConditionDaoImpl")
public class AlertConditionDaoImpl extends BaseHibernateDao<AlertCondition, Long> implements AlertConditionDao {
	private static final String GROUP_ID = "groupId";
	private static final String USER_ID = "userId";

	@Override
	public List<AlertCondition> getListByGroupId(Long groupId) {
		String hql = "select cn.condId as condId, cn.settingId as settingId, cn.alertItem as alertItem,            "
				+ " cn.alertSeverity as alertSeverity, cn.alertThreshold as alertThreshold, g.name as groupName,     "
				+ " cn.alertMessage as alertMessage" + " from AlertCondition cn "
				+ " inner join AlertSetting s on s.settingId=cn.settingId" + " inner join Group g on g.id=s.groupId"
				+ " where g.id=:groupId order by condId asc";

		return this.createQuery(hql, AlertCondition.class, false).setParameter(GROUP_ID, groupId).getResultList();
	}

	@Override
	public List<AlertCondition> getListByCondUser(Long groupId, Long userId) {
		String hql = "select cn.condId as condId, cn.settingId as settingId, cn.alertItem as alertItem, "
				+ " cn.alertSeverity as alertSeverity, cn.alertThreshold as alertThreshold, g.name as groupName, "
				+ " cn.alertMessage as alertMessage, sb.scbSms as scbSms, sb.scbEmail as scbEmail"
				+ " from AlertCondition cn left join AlertSubscribe sb on cn.condId=sb.condId and sb.userId=:userId "
				+ " inner join AlertSetting s on s.settingId=cn.settingId inner join Group g on g.id=s.groupId"
				+ " where g.id=:groupId order by condId asc";
		Query<AlertCondition> query = this.createQuery(hql, AlertCondition.class, false).setParameter(USER_ID, userId)
				.setParameter(GROUP_ID, groupId);
		return query.getResultList();
	}

	@Override
	public AlertSetting findSetting(Long groupId) {
		String hql = "from AlertSetting where groupId=:groupId";
		return createQuery(hql, AlertSetting.class).setParameter(GROUP_ID, groupId).uniqueResult();
	}

}
