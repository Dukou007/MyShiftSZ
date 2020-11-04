/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description:  query audit log list
 * Revision History:		
 * Date	        Author	            Action
 * 20170111     Carry         query audit log list 
 * ============================================================================		
 */
package com.pax.tms.user.web.controller;

import java.text.ParseException;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.pax.common.pagination.Page;
import com.pax.common.web.controller.BaseController;
import com.pax.common.web.form.BaseForm;
import com.pax.tms.group.service.GroupService;
import com.pax.tms.user.model.User;
import com.pax.tms.user.service.AuditLogService;
import com.pax.tms.user.web.form.QueryAuditLogForm;

@Controller
@RequestMapping("/auditLog")
public class AuditLogController extends BaseController {

	@Autowired
	private AuditLogService auditLogService;

	@Autowired
	private GroupService groupService;

	private static final String LOG_LIST_URL = "/auditLog/list/";

	@RequiresPermissions(value = "tms:audit log:view")
	@RequestMapping("/list/{groupId}")
	public ModelAndView getUserLogList(@PathVariable Long groupId, BaseForm command) {
		ModelAndView mv = new ModelAndView("auditLog/list");
		groupService.checkPermissionOfGroup(command, groupId);
		mv.addObject("group", groupService.get(groupId));
		mv.addObject("activeUrl", LOG_LIST_URL);
		mv.addObject("title", User.USER_TITLE);
		return mv;
	}

	/***
	 * service
	 * 
	 * @throws ParseException
	 ***/

	@ResponseBody
	@RequiresPermissions(value = "tms:audit log:view")
	@RequestMapping("/service/list/{groupId}")
	public Page<Map<String, Object>> getUserLogListJSON(@PathVariable Long groupId, QueryAuditLogForm command) {
		command.setGroupId(groupId);
		return auditLogService.page(command);
	}
}
