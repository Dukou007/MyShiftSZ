/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description: get GroupUsageStatus
 * Revision History:		
 * Date	                 Author	                Action
 * 20170111  	         Frank           	    Modify 
 * ============================================================================		
 */	
package com.pax.tms.monitor.dao;

import java.util.List;

import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.springframework.stereotype.Repository;

import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.common.dao.hibernate.MyAliasToBeanResultTransformer;
import com.pax.tms.monitor.domain.GroupUsageCount;
import com.pax.tms.monitor.model.GroupUsageStatus;

@Repository("groupUsageDaoImpl")
public class GroupUsageDaoImpl extends BaseHibernateDao<GroupUsageStatus, Long> implements GroupUsageDao {
	private static final String GROUP_ID = "groupId";
	private static final String ITEM_NAME = "itemName";
	private ResultTransformer greoupUsageTransformer = new MyAliasToBeanResultTransformer(GroupUsageCount.class);

	@Override
	public GroupUsageStatus getUsageByGroupId(String itemName, Long groupId) {
		String hql = "from GroupUsageStatus where groupId=:groupId and itemName=:itemName order by createDate desc";

		Query<GroupUsageStatus> query = super.createQuery(hql, GroupUsageStatus.class);
		query.setParameter(GROUP_ID, groupId).setParameter(ITEM_NAME, itemName).setMaxResults(1);
		return super.uniqueResult(query);
	}

	@Override
	public List<GroupUsageStatus> getUsageDetail(String itemName, Long groupId) {
		String hql = "from GroupUsageStatus where groupId=:groupId and itemName=:itemName order by cycleDate desc";

		Query<GroupUsageStatus> query = super.createQuery(hql, GroupUsageStatus.class);
		query.setParameter(ITEM_NAME, itemName).setParameter(GROUP_ID, groupId).setMaxResults(5);
		return query.getResultList();
	}
	
	@Override
    public List<GroupUsageStatus> getUsageDetailByDate(Long groupId,String cycleDate) {
        String hql = "from GroupUsageStatus where groupId=:groupId and cycleDate=:cycleDate order by cycleDate desc";

        Query<GroupUsageStatus> query = super.createQuery(hql, GroupUsageStatus.class);
        query.setParameter(GROUP_ID, groupId).setParameter("cycleDate", cycleDate);
        return query.getResultList();
    }
	
	@Override
	public List<GroupUsageCount> getGroupUsage(String itemName, Long groupId,String startTime,String endTime){
	    String queryUsage = "SELECT SUM(tum.ITEM_TOTS) as total, SUM(tum.ITEM_ERRS) as errCount,tum.ITEM_NAME as itemName "
	            +" FROM TMSTTRM_GROUP tg INNER JOIN tmsttrm_usage_msg tum on tg.TRM_ID=tum.TRM_ID "
	            +" WHERE tum.ITEM_NAME = :itemName AND tg.GROUP_ID=:groupId "
                + " AND tum.MSG_CYCLE >=:startTime AND tum.MSG_CYCLE <= :endTime group by tum.MSG_CYCLE desc";
	            
	    NativeQuery<GroupUsageCount> query = super.createNativeQuery(queryUsage, GroupUsageCount.class);
        query.setParameter(GROUP_ID, groupId).setParameter(ITEM_NAME, itemName)
            .setParameter("startTime", startTime).setParameter("endTime", endTime);

        super.addScalar(query, "", StringType.INSTANCE);
        super.addScalar(query, "itemRate", IntegerType.INSTANCE);
        super.setResultTransformer(query, greoupUsageTransformer);
        return query.getResultList();
	}
	
	@Override
    public GroupUsageCount getGroupUsageCount(String itemName, Long groupId,String msgCycle){
        String queryUsage = "SELECT SUM(tum.ITEM_TOTS) as total, SUM(tum.ITEM_ERRS) as errCount,SUM(tum.ITEM_PENDING) as pendingCount,tum.ITEM_NAME as itemName "
                +" FROM tmsttrm_usage_msg tum "
                +" WHERE tum.ITEM_NAME = :itemName AND tum.GROUP_ID=:groupId AND tum.MSG_CYCLE = :msgCycle";
                
        NativeQuery<GroupUsageCount> query = super.createNativeQuery(queryUsage, GroupUsageCount.class);
        query.setParameter(GROUP_ID, groupId).setParameter(ITEM_NAME, itemName)
            .setParameter("msgCycle", msgCycle);
        super.addScalar(query, "itemName", StringType.INSTANCE);
        super.addScalar(query, "total", IntegerType.INSTANCE);
        super.addScalar(query, "errCount", IntegerType.INSTANCE);
        super.addScalar(query, "pendingCount", IntegerType.INSTANCE);
        super.setResultTransformer(query, greoupUsageTransformer);
        return query.getSingleResult();
    }
	
	@Override
    public void updateGroupUsageStatus(GroupUsageStatus groupUsageStatus) {
        String sql = "UPDATE TMSTGROUP_USAGE_STS SET TOTAL_TRMS = :totalTrms,ABNORMAL_TRMS = :abnormalTrms,NORMAL_TRMS = :normalTrms,UNKNOWN_TRMS = :unKnownTrms "
                + " WHERE ID = :id ";
        super.createNativeQuery(sql).setParameter("totalTrms", groupUsageStatus.getTotalTrms())
                .setParameter("abnormalTrms", groupUsageStatus.getAbnormalTrms())
                .setParameter("normalTrms", groupUsageStatus.getNormalTrms())
                .setParameter("unKnownTrms", groupUsageStatus.getUnKnownTrms())
                .setParameter("id", groupUsageStatus.getId()).executeUpdate();
    }
}
