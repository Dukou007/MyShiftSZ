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

import com.pax.common.dao.IBaseDao;
import com.pax.tms.monitor.model.AlertCondition;
import com.pax.tms.monitor.model.AlertSetting;

public interface AlertConditionDao extends IBaseDao<AlertCondition, Long> {
	List<AlertCondition> getListByCondUser(Long groupId, Long userId);

	AlertSetting findSetting(Long groupId);

	List<AlertCondition> getListByGroupId(Long groupId);


}
