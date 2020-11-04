/*		
 * ============================================================================		
 *  COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description: view and export user maintenance report
 * Revision History:		
 * Date	                 Author	                Action
 * 20170111  	         Alan Jiang           	Modify
 * ============================================================================		
 */
package com.pax.tms.report.web.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.pax.common.exception.BusinessException;
import com.pax.common.pagination.Page;
import com.pax.common.util.ExcelWritter;
import com.pax.common.util.HttpServletUtils;
import com.pax.common.web.controller.BaseController;
import com.pax.common.web.form.BaseForm;
import com.pax.tms.group.service.GroupService;
import com.pax.tms.report.service.UserMaintenanceService;
import com.pax.tms.report.web.form.QueryUserMaintenanceForm;
import com.pax.tms.terminal.model.Terminal;
import com.pax.tms.terminal.service.TerminalService;
import com.pax.tms.user.service.AuditLogService;
import com.pax.tms.user.web.form.OperatorLogForm;

@Controller
@RequestMapping("/report")
public class UserMaintenanceController extends BaseController {

	@Autowired
	private UserMaintenanceService userMainService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private AuditLogService auditLogService;

	@Autowired
	private TerminalService terminalService;

	private static final String REPORT_LIST_URL = "/report/userMaintenance/";
	private static final String REPORT_TITLE = "REPORTS";
	protected Logger logger = LoggerFactory.getLogger(UserMaintenanceController.class);

	@RequestMapping("/userMaintenance/{groupId}")
	@RequiresPermissions(value = "tms:report:user maintenance")
	public ModelAndView getList(@PathVariable Long groupId, BaseForm command) {
		ModelAndView mv = new ModelAndView("report/userMaintenance");
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		groupService.checkPermissionOfGroup(command, groupId);
		mv.addObject("group", groupService.get(groupId));
		mv.addObject("activeUrl", REPORT_LIST_URL);
		mv.addObject("title", REPORT_TITLE);
		return mv;
	}

	@RequestMapping("/userMaintenance/{groupId}/{tsn}")
	@RequiresPermissions(value = "tms:report:user maintenance")
	public ModelAndView getList(@PathVariable Long groupId, @PathVariable String tsn, BaseForm command) {
		ModelAndView mv = new ModelAndView("report/userMaintenance");
		groupService.checkPermissionOfGroup(command, groupId);
		if (StringUtils.isEmpty(tsn)) {
			throw new BusinessException("tsn.Required");
		}
		Terminal terminal = terminalService.get(tsn);
		mv.addObject("terminal", terminal);
		mv.addObject("group", groupService.get(groupId));
		mv.addObject("activeUrl", REPORT_LIST_URL);
		mv.addObject("title", REPORT_TITLE);
		return mv;
	}

	/*** service ***/
	@ResponseBody
	@RequestMapping("/service/userMaintList/{groupId}")
	@RequiresPermissions(value = "tms:report:user maintenance")
	public Page<Map<String, Object>> getListJSON(@PathVariable Long groupId, QueryUserMaintenanceForm command) {
		command.setGroupId(groupId);
		Page<Map<String, Object>> page = userMainService.page(command);
		return page;
	}

	@RequiresPermissions(value = "tms:report:user maintenance")
	@RequestMapping(value = "/userMaintenance/export/{grpId}", method = { RequestMethod.GET, RequestMethod.POST })
	public void export(@PathVariable Long grpId, QueryUserMaintenanceForm command, HttpServletResponse response)
			throws IOException {
		groupService.checkPermissionOfGroup(command, grpId);
		if(null != command.getGroupId()){
		    groupService.checkPermissionOfGroup(command, command.getGroupId());
		}
		String caption = this.getMessage("title.user.maintenance") + "_"
				+ new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		String filename = caption + ".xlsx";
		HttpServletUtils.setDownloadExcel(filename, response);
		ExcelWritter ew = new ExcelWritter(response.getOutputStream());
		ew.open();
		ew.writeCaption(caption, 5);
		ew.writeTitle(new String[] { this.getMessage("form.user.name"), this.getMessage("form.role"),
				this.getMessage("form.action.name"), this.getMessage("form.ip.address"),
				this.getMessage("form.dateTime") });
		ew.setWidths(new int[] { 40, 40, 40, 40, 40 });
		List<Map<String, Object>> list = userMainService.list(command);
		list.forEach(userLog -> {
			try {
				ew.writeContent(userLog.get("username"), userLog.get("role"), userLog.get("eventAction"),
						userLog.get("clientIp"), userLog.get("eventTime"));
			} catch (IOException e) {
				logger.error("User Maintenance", e);
			}
		});
		ew.close();
		// add audit log
		auditLogService.addAuditLog(Arrays.asList(""), command, OperatorLogForm.USER_MAINTENANCE_REPORT, null);
	}

}
