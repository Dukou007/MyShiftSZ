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
import com.pax.tms.report.dao.UserMaintenanceDao;
import com.pax.tms.report.web.form.QueryUserMaintenanceForm;
import com.pax.tms.user.model.UserLog;

@Service("userMaintenanceServiceImpl")
public class UserMaintenanceServiceImpl extends BaseService<UserLog, Long> implements UserMaintenanceService {

	@Autowired
	private GroupService groupService;

	@Autowired
	private UserMaintenanceDao usermainDao;

	@Override
	public IBaseDao<UserLog, Long> getBaseDao() {
		return usermainDao;
	}

	@Override
	public <E, S extends IPageForm> Page<E> page(S command) {
		QueryUserMaintenanceForm form = (QueryUserMaintenanceForm) command;
		// check group permission
		groupService.checkPermissionOfGroup(form, form.getGroupId());
		return super.page(form);
	}

}
