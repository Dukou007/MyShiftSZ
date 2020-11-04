/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description: List Staitics
 * Revision History:		
 * Date	                 Author	                Action
 * 20170111  	         Frank           	    Modify 
 * ============================================================================		
 */
package com.pax.tms.monitor.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pax.common.dao.IBaseDao;
import com.pax.common.service.impl.BaseService;
import com.pax.tms.monitor.dao.TerminalUsageStatisticsDao;
import com.pax.tms.monitor.model.TerminalUsageMessage;
@Service("terminalUsageStatisticsServiceImpl")
public class TerminalUsageStatisticsServiceImpl extends BaseService<TerminalUsageMessage, Long> implements TerminalUsageStatisticsService{
	@Autowired
	@Qualifier("terminalUsageStatisticsDaoImpl")
	private TerminalUsageStatisticsDao terminalUsageStatisticsDao;

	@Override
	public IBaseDao<TerminalUsageMessage, Long> getBaseDao() {
		return terminalUsageStatisticsDao;
	}

}
