/*		
 * ============================================================================		
 *  COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description: view and export terminal not registered report
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
import com.pax.tms.report.service.TerminalNotRegisteredService;
import com.pax.tms.report.web.form.QueryTerminalNotRegisteredForm;
import com.pax.tms.res.service.ModelService;
import com.pax.tms.user.service.AuditLogService;
import com.pax.tms.user.web.form.OperatorLogForm;

@Controller
@RequestMapping("/report")
public class TerminalNotRegisteredController extends BaseController {

	@Autowired
	private GroupService groupService;

	@Autowired
	private ModelService modelService;

	@Autowired
	private TerminalNotRegisteredService tnrService;

	@Autowired
	private AuditLogService auditLogService;
	private static final String REPORT_LIST_URL = "/report/terminalNotRegistered/";
	protected Logger logger = LoggerFactory.getLogger(TerminalNotRegisteredController.class);

	@RequestMapping("/terminalNotRegistered/{groupId}")
	@RequiresPermissions(value = "tms:report:terminal not registered")
	public ModelAndView getList(@PathVariable Long groupId, BaseForm command) {
		ModelAndView mv = new ModelAndView("report/terminalNotRegistered");
		groupService.checkPermissionOfGroup(command, groupId);
		mv.addObject("typeList", modelService.getList());
		mv.addObject("activeUrl", REPORT_LIST_URL);
		mv.addObject("group", groupService.get(groupId));
		return mv;
	}

	/*** service ***/

	@ResponseBody
	@RequiresPermissions(value = "tms:report:terminal not registered")
	@RequestMapping("/service/terminalNotRegisteredList/{groupId}")
	public Page<Map<String, Object>> getListJSON(@PathVariable Long groupId, QueryTerminalNotRegisteredForm command) {
		Page<Map<String, Object>> page = tnrService.page(command);
		return page;
	}

	@SuppressWarnings("resource")
	@RequiresPermissions(value = "tms:report:terminal not registered")
	@RequestMapping(value = "/export/terminalNotRegisteredList/{grpId}", method = { RequestMethod.GET,
			RequestMethod.POST })
	public void export(@PathVariable Long grpId, QueryTerminalNotRegisteredForm command, HttpServletResponse response)
			throws IOException {
		groupService.checkPermissionOfGroup(command, grpId);
		String caption = this.getMessage("title.terminal.notRegister") + "_"
				+ new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		String filename = caption + ".xlsx";
		HttpServletUtils.setDownloadExcel(filename, response);
		ExcelWritter ew = new ExcelWritter(response.getOutputStream());
		ew.open();
		ew.writeCaption(caption, 4);
		ew.writeTitle(new String[] { this.getMessage("form.terminal.sn"), this.getMessage("form.terminal.type"),
				this.getMessage("form.last.access.connection"), this.getMessage("form.source.ip") });
		ew.setWidths(new int[] { 40, 40, 40, 40 });
		List<Map<String, Object>> list = tnrService.list(command);
		list.forEach(terminalNotRegist -> {
			try {
				ew.writeContent(terminalNotRegist.get("trmSn"), terminalNotRegist.get("terminalType"),
						terminalNotRegist.get("lastDate"), terminalNotRegist.get("sourceIp"));
			} catch (IOException e) {
				throw new BusinessException(e);
			}
		});
		ew.close();
		// add audit log
		auditLogService.addAuditLog(Arrays.asList(""), command, OperatorLogForm.TERMINAL_NOT_REGISTERED_REPORT, null);
	}
}
