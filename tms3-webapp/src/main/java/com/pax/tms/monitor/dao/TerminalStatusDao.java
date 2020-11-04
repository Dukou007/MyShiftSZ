/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description: get UsageMessageInfo/TerminalRealStatusInfo/UsageStatusBar/TerminalRealStatus
 * Revision History:		
 * Date	                 Author	                Action
 * 20170111  	         Frank           	    Modify 
 * ============================================================================		
 */	
package com.pax.tms.monitor.dao;

import java.util.Date;
import java.util.List;

import com.pax.common.dao.IBaseDao;
import com.pax.tms.monitor.domain.TerminalRealStatusInfo;
import com.pax.tms.monitor.domain.UsageMessageInfo;
import com.pax.tms.monitor.model.TerminalReportMessage;

public interface TerminalStatusDao extends IBaseDao<TerminalReportMessage, String> {

	UsageMessageInfo getUsageStatus(String terminalId, String itemName);

	List<UsageMessageInfo> getUsageStatusBar(String terminalId, String itemName);

	TerminalRealStatusInfo getRealStatus(String terminalId);

	List<Object[]> getTerminalRealStatus(String terminalId, String itemName,Date dayStart);
}
