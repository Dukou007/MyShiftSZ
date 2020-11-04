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

import com.pax.common.dao.IBaseDao;
import com.pax.tms.monitor.model.AlertOff;

public interface AlertOffDao extends IBaseDao<AlertOff, Long> {

	List<AlertOff> getListBySettingId(Long settingId);

	int deleteBySettingId(Long settingId);
}
