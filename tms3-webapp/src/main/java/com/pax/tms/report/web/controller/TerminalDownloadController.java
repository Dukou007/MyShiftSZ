/*		
 * ============================================================================		
 *  COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description: view and export terminal download report
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
import com.pax.tms.report.service.TerminalDownloadService;
import com.pax.tms.report.web.form.QueryTerminalDownloadForm;
import com.pax.tms.res.service.ModelService;
import com.pax.tms.terminal.model.Terminal;
import com.pax.tms.terminal.service.TerminalService;
import com.pax.tms.user.service.AuditLogService;
import com.pax.tms.user.web.form.OperatorLogForm;

@Controller
@RequestMapping("/report")
public class TerminalDownloadController extends BaseController {

	@Autowired
	private GroupService groupService;

	@Autowired
	private ModelService modelService;

	@Autowired
	private TerminalDownloadService tDownloadService;

	@Autowired
	private AuditLogService auditLogService;

	@Autowired
	private TerminalService terminalService;

	private static final String REPORT_LIST_URL = "/report/terminalDownload/";
	private static final String REPORT_TITLE = "REPORTS";
	protected Logger logger = LoggerFactory.getLogger(TerminalDownloadController.class);

	@RequestMapping("/terminalDownload/{groupId}")
	@RequiresPermissions(value = "tms:report:terminal download")
	public ModelAndView getList(@PathVariable Long groupId, BaseForm command) {
		ModelAndView mv = new ModelAndView("report/terminalDownload");
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		groupService.checkPermissionOfGroup(command, groupId);
		mv.addObject("group", groupService.get(groupId));
		mv.addObject("activeUrl", REPORT_LIST_URL);
		mv.addObject("title", REPORT_TITLE);
		mv.addObject("typeList", modelService.getList());
		return mv;
	}

	@RequestMapping("/terminalDownload/{groupId}/{tsn}")
	@RequiresPermissions(value = "tms:report:terminal download")
	public ModelAndView getList(@PathVariable Long groupId, @PathVariable String tsn, BaseForm command) {
		ModelAndView mv = new ModelAndView("report/terminalDownload");
		groupService.checkPermissionOfGroup(command, groupId);
		if (StringUtils.isEmpty(tsn)) {
			throw new BusinessException("tsn.Required");
		}
		Terminal terminal = terminalService.get(tsn);
		mv.addObject("terminal", terminal);
		mv.addObject("group", groupService.get(groupId));
		mv.addObject("activeUrl", REPORT_LIST_URL);
		mv.addObject("title", REPORT_TITLE);
		mv.addObject("typeList", modelService.list());
		return mv;
	}

	/*** service ***/
	@ResponseBody
	@RequestMapping("/service/terminalDownload/list/{groupId}")
	@RequiresPermissions(value = "tms:report:terminal download")
	public Page<Map<String, Object>> getGroupListJSON(@PathVariable Long groupId, QueryTerminalDownloadForm command) {
		if (groupId != null) {
			command.setGroupId(groupId);
		}
		Page<Map<String, Object>> page = tDownloadService.page(command);
		return page;
	}

	@ResponseBody
	@RequestMapping("/service/terminalDownload/list/{groupId}/{terminalId}")
	@RequiresPermissions(value = "tms:report:terminal download")
	public Page<Map<String, Object>> getTerminalListJSON(@PathVariable Long groupId, @PathVariable String terminalId,
			QueryTerminalDownloadForm command) {
		if (groupId != null) {
			command.setGroupId(groupId);
		}
		if (StringUtils.isNotEmpty(terminalId)) {
			command.setTsn(terminalId);
		}
		Page<Map<String, Object>> page = tDownloadService.page(command);
		return page;
	}

	@RequiresPermissions(value = "tms:report:terminal download")
	@RequestMapping(value = "/terminalDownload/export/{grpId}", method = { RequestMethod.GET, RequestMethod.POST })
	public void export(@PathVariable Long grpId, QueryTerminalDownloadForm command, HttpServletResponse response)
			throws IOException {
		groupService.checkPermissionOfGroup(command, grpId);
		if(null != command.getGroupId()){
            groupService.checkPermissionOfGroup(command, command.getGroupId());
        }
		String caption = this.getMessage("title.export.terminalDownload") + "_"
				+ new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		String filename = caption + ".xlsx";
		HttpServletUtils.setDownloadExcel(filename, response);
		ExcelWritter ew = new ExcelWritter(response.getOutputStream());
		ew.open();
		ew.writeCaption(caption, 11);
		ew.writeTitle(new String[] { this.getMessage("form.terminal.sn"), this.getMessage("form.terminal.type"),
				this.getMessage("form.package.name"), this.getMessage("form.package.version"),
				this.getMessage("form.package.type"), this.getMessage("form.download.schedule"),
				this.getMessage("form.download.status"), this.getMessage("form.download.time"),
				this.getMessage("form.activation.schedule"), this.getMessage("form.activation.status"),
				this.getMessage("form.activation.time") });
		ew.setWidths(new int[] { 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40 });
		List<Map<String, Object>> list = tDownloadService.getExportList(command);
		list.forEach(tDownload -> {
			try {
				ew.writeContent(tDownload.get("tid"), tDownload.get("terminalType"), tDownload.get("pkgName"),
						tDownload.get("pkgVersion"), tDownload.get("pkgType"), tDownload.get("downSchedule"),
						tDownload.get("downStatus"), tDownload.get("downEndTime"), tDownload.get("actvSchedule"),
						tDownload.get("actvStatus"), tDownload.get("actvTime"));
			} catch (IOException e) {
				logger.error("Terminal Download", e);
			}
		});
		ew.close();
		// add audit log
		auditLogService.addAuditLog(Arrays.asList(""), command, OperatorLogForm.TERMINA_DOWNLOAD_REPORT, null);

	}

}
