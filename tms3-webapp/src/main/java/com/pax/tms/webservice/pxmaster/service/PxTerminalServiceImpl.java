/*
 * ============================================================================
 * = COPYRIGHT				
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *   	Copyright (C) 2009-2020 PAX Technology, Inc. All rights reserved.		
 * ============================================================================		
 */
package com.pax.tms.webservice.pxmaster.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pax.tms.terminal.dao.TerminalDao;
import com.pax.tms.terminal.model.Terminal;
import com.pax.tms.webservice.pxmaster.PxTerminalService;

@Service("pxmasterTmsTerminalService")
public class PxTerminalServiceImpl implements PxTerminalService {

	@Autowired
	TerminalDao terminalDao;

	@Override
	public Terminal getTerminalByTsn(String tsn) {
		return terminalDao.get(tsn);
	}

	public TerminalDao getTerminalDao() {
		return terminalDao;
	}

	public void setTerminalDao(TerminalDao terminalDao) {
		this.terminalDao = terminalDao;
	}

}
