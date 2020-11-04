/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description: List/edit AlertCondition  List/delete AlertOff
 * Revision History:		
 * Date	                 Author	                Action
 * 20170111  	         Frank           	    Modify 
 * ============================================================================		
 */
package com.pax.tms.monitor.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.pax.common.security.CsrfProtect;
import com.pax.common.web.controller.BaseController;
import com.pax.common.web.form.BaseForm;
import com.pax.tms.group.model.Group;
import com.pax.tms.group.service.GroupService;
import com.pax.tms.monitor.model.AlertCondition;
import com.pax.tms.monitor.model.AlertOff;
import com.pax.tms.monitor.service.AlertConditionService;
import com.pax.tms.monitor.web.form.AddAlertOffForm;
import com.pax.tms.monitor.web.form.EditConditionForm;
import com.pax.tms.user.security.AclManager;
import com.pax.tms.web.annotation.AuditLog;

@Controller
@RequestMapping(value = "/alert")
public class AlertConditionController extends BaseController {
	@Autowired
	private AlertConditionService conditionService;

	@Autowired
	private GroupService groupService;

	@RequiresPermissions(value = { "tms:alert:alert off:view" })
	@RequestMapping(value = "/alertOff/{groupId}", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView alertOff(@PathVariable Long groupId, BaseForm command) {
		ModelAndView mv = new ModelAndView("alert/condition/addAlertOff");
		Group group = groupService.get(groupId);
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		mv.addObject("group", group);
		mv.addObject("activeUrl", AlertOff.LIST_URL);
		mv.addObject("timeZone", group.getTimeZone());
		mv.addObject("title", AlertOff.TITLE);
		return mv;
	}

	@RequiresPermissions(value = { "tms:alert:condition:view" })
	@RequestMapping(value = "/alertCondition/{groupId}", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView toEditConditin(@PathVariable Long groupId, BaseForm command) {
		ModelAndView mv = new ModelAndView("alert/condition/editAlertCondition");
		Group group = groupService.get(groupId);
		Group viewGroup = groupService.get(groupId);
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		mv.addObject("group", group);
		mv.addObject("viewGroup", viewGroup);
		mv.addObject("activeUrl", AlertCondition.LIST_URL);
		mv.addObject("title", AlertCondition.TITLE);
		return mv;
	}

	@RequiresPermissions(value = { "tms:alert:alert off:view" })
	@ResponseBody
	@RequestMapping(value = "/service/listAlertOff/{groupId}", method = { RequestMethod.GET, RequestMethod.POST })
	public Object listAlertOff(@PathVariable Long groupId, BaseForm command) {
		Long settingId = 0L;
		if (groupId != 1) {
			settingId = conditionService.findSettingByGroupId(groupId).getSettingId();
		}
		Group group = groupService.get(groupId);
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		Map<String, Object> mapJson = new HashMap<String, Object>();
		List<AlertOff> list = conditionService.getAlertOffList(settingId);
		mapJson.put("statusCode", SUCCESS_STATUS_CODE);
		mapJson.put("items", list);
		return mapJson;
	}

	@RequiresPermissions(value = { "tms:alert:alert off:add" })
	@ResponseBody
	@RequestMapping(value = "/service/doAddAlertOff/{groupId}", method = { RequestMethod.GET, RequestMethod.POST })
	@AuditLog(application = "Group", module = "Alert Setting", operation = "Add AlertOff")
	@CsrfProtect
	public Object addAlertOff(@RequestBody AddAlertOffForm form, @PathVariable Long groupId, BindingResult result) {
		form.setGroupId(groupId);
		Group group = groupService.get(groupId);
		AclManager.checkPermissionOfGroup(form.getLoginUserId(), group);
		conditionService.addAlertOff(form);
		return this.ajaxDoneSuccess();
	}

	@RequiresPermissions(value = { "tms:alert:alert off:delete" })
	@ResponseBody
	@RequestMapping(value = "/service/deleteAlertOff/{offId}/{groupId}", method = { RequestMethod.GET,
			RequestMethod.POST })
	@AuditLog(application = "Group", module = "Alert Setting", operation = "Delete AlertOff")
	@CsrfProtect
	public Object deleteAlertOff(@PathVariable Long offId, @PathVariable Long groupId, BaseForm command) {
		command.setGroupId(groupId);
		conditionService.deleteAlertOff(offId, command);
		return this.ajaxDoneSuccess();
	}

	@RequiresPermissions(value = { "tms:alert:condition:subscribe" })
	@ResponseBody
	@RequestMapping("/service/editCondition/{groupId}")
	@AuditLog(application = "Group", module = "Alert Setting", operation = "Add AlertCondition")
	@CsrfProtect
	public Object editCondition(@RequestBody EditConditionForm[] forms, @PathVariable Long groupId) {
		conditionService.editCondition(forms, groupId);
		return this.ajaxDoneSuccess();
	}

	@RequiresPermissions(value = { "tms:alert:condition:view" })
	@ResponseBody
	@RequestMapping("/service/listCondition/{groupId}")
	public Object toEditCondition(@PathVariable Long groupId, BaseForm command) {
		Map<String, Object> mapJson = new HashMap<String, Object>();
		List<AlertCondition> list = conditionService.getAlertConditionList(groupId, command.getLoginUserId());
		Group group = groupService.get(groupId);
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		mapJson.put("statusCode", SUCCESS_STATUS_CODE);
		mapJson.put("items", list);
		return mapJson;
	}

}
