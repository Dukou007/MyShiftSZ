/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description: Modify/List usageThreshold
 * Revision History:		
 * Date	                 Author	                Action
 * 20170111  	         Aaron           	    Modify UsageThresholdController
 * ============================================================================		
 */	

package com.pax.tms.group.web.controller;

import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.pax.common.exception.BusinessException;
import com.pax.common.security.CsrfProtect;
import com.pax.common.web.controller.BaseController;
import com.pax.tms.group.model.Group;
import com.pax.tms.group.model.UsageThreshold;
import com.pax.tms.group.service.GroupService;
import com.pax.tms.group.service.UsageThresholdService;
import com.pax.tms.group.web.form.EditUsageThresholdForm;
import com.pax.tms.web.annotation.AuditLog;


@Controller
@RequestMapping(value = "/usage")
public class UsageThresholdController extends BaseController {

	@Autowired
	private UsageThresholdService usageThresholdService;
	
	@Autowired
	private GroupService groupService;

	@RequiresPermissions(value = { "tms:group:global Setting" })
	@RequestMapping(value = "/list/{groupId}/{viewGroupId}")
	public ModelAndView list(@PathVariable Long groupId,@PathVariable Long viewGroupId) {
		ModelAndView mav = new ModelAndView("usage/list");
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		Group group = groupService.get(groupId);
		if (group == null) {
			throw new BusinessException("msg.group.groupNotFound");
		}
		Group viewGroup = groupService.get(viewGroupId);
		List<UsageThreshold> usageList = usageThresholdService.list(viewGroupId);
		mav.addObject("viewGroup", viewGroup);
		mav.addObject("group", group);
		mav.addObject("activeUrl", Group.LIST_URL);
		mav.addObject("title", UsageThreshold.TITLE);
		mav.addObject("usageList", usageList);
		return mav;
	}

	@RequiresPermissions(value={"tms:group:global Setting"})
	@ResponseBody
	@AuditLog(application = "Group", module = "Global Setting", operation = "edit usage")
	@RequestMapping(value = "/service/edit")
	@CsrfProtect
	public Map<String, Object> edit(EditUsageThresholdForm command) {
		if (command.getGroupId() == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		usageThresholdService.editUsageThreshold(command);
		return this.ajaxDoneSuccess();
	}
}
