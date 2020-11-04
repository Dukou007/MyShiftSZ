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

import com.pax.common.dao.IBaseDao;
import com.pax.tms.monitor.domain.GroupUsageCount;
import com.pax.tms.monitor.model.GroupUsageStatus;

public interface GroupUsageDao extends IBaseDao<GroupUsageStatus, Long> {
	GroupUsageStatus getUsageByGroupId(String itemName,Long groupId);

	List<GroupUsageStatus> getUsageDetail(String itemName, Long groupId);
	
	List<GroupUsageStatus> getUsageDetailByDate(Long groupId,String cycleDate);
	
	List<GroupUsageCount> getGroupUsage(String itemName, Long groupId,String startTime,String endTime);
	
	GroupUsageCount getGroupUsageCount(String itemName, Long groupId,String msgCycle);
	
	void updateGroupUsageStatus(GroupUsageStatus groupUsageStatus);

}
