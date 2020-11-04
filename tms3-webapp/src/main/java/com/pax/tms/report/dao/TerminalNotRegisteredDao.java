/*		
 * ============================================================================		
 *  COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.	
 * Revision History:		
 * Date	                 Author	                Action
 * 20170111  	         Alan Jiang           	Modify
 * ============================================================================		
 */
package com.pax.tms.report.dao;

import java.util.Collection;

import com.pax.common.dao.IBaseDao;
import com.pax.tms.monitor.model.UnregisteredTerminal;

public interface TerminalNotRegisteredDao extends IBaseDao<UnregisteredTerminal, Long>{

	void deleteTerminalNotRegisters(Collection<String> newTsns);
	
	UnregisteredTerminal getUnregisteredTerminalBySN(String tsn);

}
