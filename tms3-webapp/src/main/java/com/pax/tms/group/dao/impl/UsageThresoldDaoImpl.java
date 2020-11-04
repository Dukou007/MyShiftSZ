/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description: List usageThreshold
 * Revision History:		
 * Date	                 Author	                Action
 * 20170111  	         Aaron           	    Modify UsageThresholdDaoImpl
 * ============================================================================		
 */	
package com.pax.tms.group.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.tms.group.dao.UsageThresholdDao;
import com.pax.tms.group.model.UsageThreshold;

@Repository("usageThresoldDaoImpl")
public class UsageThresoldDaoImpl extends BaseHibernateDao<UsageThreshold, Long>
	implements UsageThresholdDao{

	@Override
	public List<UsageThreshold> list(Long groupId) {
		String hql = "select usageThreshold from UsageThreshold usageThreshold where usageThreshold.group.id=:groupId order by id";
		return createQuery(hql, UsageThreshold.class).setParameter("groupId", groupId).getResultList();
	}
	
}
