/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description: get GroupRealStatus
 * Revision History:		
 * Date	                 Author	                Action
 * 20170111  	         Frank           	    Modify 
 * ============================================================================		
 */
package com.pax.tms.monitor.dao;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.query.NativeQuery;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.springframework.stereotype.Repository;

import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.tms.monitor.model.GroupRealStatus;

@Repository("groupRealDaoImpl")
public class GroupRealDaoImpl extends BaseHibernateDao<GroupRealStatus, Long> implements GroupRealDao {
	private static final String GROUP_ID = "groupId";
	private static final String GROUP_NAME = "groupName";
	private static final String ITEM_NAME = "itemName";
	private static final String TOTAL_TRMS = "totalTrms";
	private static final String ABNORMAL_TRMS = "abnormalTrms";
	private static final String NORMAL_TRMS = "normalTrms";
	private static final String UNKNOWN_TRMS = "unKnownTrms";
	private static final String CREATE_DATE = "createDate";

	@Override
	public GroupRealStatus getRealStatus(String itemName, Long groupId) {
		String sql = "select T.ITEM_NAME as ITEMNAME,T.GROUP_ID as GROUPID,T.GROUP_NAME as GROUPNAME,T.TOTAL_TRMS as TOTALTRMS,"
				+ "T.ABNORMAL_TRMS as ABNORMALTRMS,T.NORMAL_TRMS as NORMALTRMS,T.UNKNOWN_TRMS as UNKNOWNTRMS,T.CREATE_DATE as CREATEDATE "
				+ "from TMSTGROUP_REAL_STS as T where T.GROUP_ID=:groupId and T.ITEM_NAME=:itemName order by T.CREATE_DATE desc";
		Map<String, Type> scalarMap = new HashMap<String, Type>();
		scalarMap.put(GROUP_ID, StandardBasicTypes.LONG);
		scalarMap.put(ITEM_NAME, StandardBasicTypes.STRING);
		scalarMap.put(GROUP_NAME, StandardBasicTypes.STRING);
		scalarMap.put(TOTAL_TRMS, StandardBasicTypes.INTEGER);
		scalarMap.put(ABNORMAL_TRMS, StandardBasicTypes.INTEGER);
		scalarMap.put(NORMAL_TRMS, StandardBasicTypes.INTEGER);
		scalarMap.put(UNKNOWN_TRMS, StandardBasicTypes.INTEGER);
		scalarMap.put(CREATE_DATE, StandardBasicTypes.TIMESTAMP);
		NativeQuery<GroupRealStatus> query = super.createNativeQuery(sql, GroupRealStatus.class, scalarMap);
		query.setParameter(GROUP_ID, groupId).setParameter(ITEM_NAME, itemName).setMaxResults(1);
		return query.uniqueResult();
	}
}
