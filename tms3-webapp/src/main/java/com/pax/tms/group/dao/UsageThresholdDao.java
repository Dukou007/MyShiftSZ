/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description: list usageThreshold
 * Revision History:		
 * Date	                 Author	                Action
 * 20170111  	         Aaron           	    Modify UsageThresholdDao
 * ============================================================================		
 */	
package com.pax.tms.group.dao;

import java.util.List;

import com.pax.common.dao.IBaseDao;
import com.pax.tms.group.model.UsageThreshold;

public interface UsageThresholdDao extends IBaseDao<UsageThreshold, Long> {
	List<UsageThreshold> list(Long groupId);
}
