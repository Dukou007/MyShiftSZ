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
package com.pax.tms.report.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pax.common.dao.IBaseDao;
import com.pax.common.pagination.IPageForm;
import com.pax.common.pagination.Page;
import com.pax.common.service.impl.BaseService;
import com.pax.tms.group.service.GroupService;
import com.pax.tms.monitor.model.UnregisteredTerminal;
import com.pax.tms.report.dao.TerminalNotRegisteredDao;
import com.pax.tms.report.web.form.QueryTerminalNotRegisteredForm;

@Service("terminalNotRegisteredServiceImpl")
public class TerminalNotRegisteredServiceImpl extends BaseService<UnregisteredTerminal, Long>
		implements TerminalNotRegisteredService {
	@Autowired
	private TerminalNotRegisteredDao notRegisteredDao;

	@Override
	public IBaseDao<UnregisteredTerminal, Long> getBaseDao() {
		return notRegisteredDao;
	}

	@Autowired
	private GroupService groupService;

	@Override
	public <E, S extends IPageForm> Page<E> page(S command) {
		QueryTerminalNotRegisteredForm form = (QueryTerminalNotRegisteredForm) command;
		// check group permission
		groupService.checkPermissionOfGroup(form, form.getGroupId());
		return super.page(form);
	}
}
