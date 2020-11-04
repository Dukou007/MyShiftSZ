/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description: List UsagePie
 * Revision History:		
 * Date	                 Author	                Action
 * 20170111  	         Frank           	    Modify 
 * ============================================================================		
 */
package com.pax.tms.monitor.service;

import java.util.List;

import com.pax.common.service.IBaseService;
import com.pax.tms.monitor.model.GroupUsageStatus;
import com.pax.tms.monitor.web.form.UsagePie;

public interface DashboardService extends IBaseService<GroupUsageStatus, Long> {

	List<UsagePie> getUsagePielist(Long groupId, Long userId);

	List<List<UsagePie>> getUsageBarList(Long groupId, Long userId);
}
