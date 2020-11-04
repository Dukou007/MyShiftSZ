/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description: real/usage/usage Bar dashboard    statistics
 * Revision History:		
 * Date	                 Author	                Action
 * 20170111  	         Frank           	    Modify 
 * ============================================================================		
 */
package com.pax.tms.monitor.web.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.pax.common.exception.BusinessException;
import com.pax.common.pagination.Page;
import com.pax.common.security.CsrfProtect;
import com.pax.common.web.controller.BaseController;
import com.pax.common.web.form.BaseForm;
import com.pax.tms.group.model.Group;
import com.pax.tms.group.service.GroupService;
import com.pax.tms.monitor.model.GroupRealStatus;
import com.pax.tms.monitor.model.GroupUsageStatus;
import com.pax.tms.monitor.service.AlertProcessService;
import com.pax.tms.monitor.service.DashboardService;
import com.pax.tms.monitor.service.TerminalUsageStatisticsService;
import com.pax.tms.monitor.web.AlertConstants;
import com.pax.tms.monitor.web.form.Pie;
import com.pax.tms.monitor.web.form.QueryStatisticsForm;
import com.pax.tms.report.service.TerminalDownloadService;
import com.pax.tms.report.web.form.QueryTerminalDownloadForm;
import com.pax.tms.res.service.ModelService;
import com.pax.tms.terminal.model.Terminal;
import com.pax.tms.terminal.service.TerminalService;
import com.pax.tms.terminal.web.form.QueryTerminalForm;
import com.pax.tms.user.security.AclManager;
import com.pax.tms.user.security.UTCTime;
/**
 * pxretailer 存储数据到数据库,ppm直接拿数据库的数据 通过 terminalSN 找到分组
 * @author zengpeng
 *
 */
@Controller
@RequestMapping(value = "/dashboard")
public class DashboardController extends BaseController {
	private static final String GROUP = "group";
	private static final String ACTIVE_URL = "activeUrl";
	private static final String TITLE = "title";
	private static final String STATUS_CODE = "statusCode";
	private static final String MESSAGE = "message";
	private static final String RESULT = "result";
	private static final String TIMEZONE_NAME = "tzName";
	private static final String REPORT_LIST_URL = "/dashboard/terminalDownload/";
	@Autowired
	@Qualifier("dashboardServiceImpl")
	private DashboardService dashboardService;
	@Autowired
	@Qualifier("terminalUsageStatisticsServiceImpl")
	private TerminalUsageStatisticsService statisticsService;
	@Autowired
	@Qualifier("groupServiceImpl")
	private GroupService groupService;
	@Autowired
	private ModelService modelService;
	@Autowired
	@Qualifier("terminalServiceImpl")
	private TerminalService terminalService;
	@Autowired
	private TerminalDownloadService tDownloadService;
	@Autowired
	@Qualifier("alertProcessServiceImpl")
	private AlertProcessService alertProcessService;

	@RequestMapping(value = "/usage/{groupId}", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView dashboardUsage(@PathVariable Long groupId, BaseForm command) {
		ModelAndView mv = new ModelAndView("dashboard/dashboard_usage");
		Group group = groupService.get(groupId);
		if (group == null) {
			throw new BusinessException("msg.group.groupNotFound");
		}
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		mv.addObject(GROUP, group);
		mv.addObject(ACTIVE_URL, GroupUsageStatus.LIST_URL);
		mv.addObject(TITLE, GroupUsageStatus.TITLE);
		return mv;
	}

	@RequestMapping(value = "/oneStatusList/{itemName}/{itemStatus}/{groupId}")
	public ModelAndView listByStatus(@PathVariable Long groupId, @PathVariable String itemName,
			@PathVariable String itemStatus, BaseForm command) {
		ModelAndView mav = new ModelAndView("terminal/list_oneStatus_terminal");
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		Group group = groupService.get(groupId);
		if (group == null) {
			throw new BusinessException("msg.group.groupNotFound");
		}
		if (!this.statusInvalidURL(itemName, itemStatus)) {
			throw new BusinessException("msg.error.InvalidURL");
		}
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		mav.addObject("itemName", itemName);
		mav.addObject("itemStatus", itemStatus);
		mav.addObject(GROUP, group);
		mav.addObject(ACTIVE_URL, GroupRealStatus.TERMINAL_LIST_URL + "/" + itemName + "/" + itemStatus + "/");
		mav.addObject(TITLE, GroupRealStatus.TITLE);
		return mav;
	}

	@RequiresPermissions(value = { "tms:statistic:view" })
	@RequestMapping(value = "/statistics/{groupId}", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView dashboardStatistics(@PathVariable Long groupId, BaseForm command) {
		ModelAndView mv = new ModelAndView("dashboard/dashboard_statistics");
		Group group = groupService.get(groupId);
		if (group == null) {
			throw new BusinessException("msg.group.groupNotFound");
		}
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		mv.addObject(GROUP, group);
		mv.addObject(ACTIVE_URL, QueryStatisticsForm.LIST_URL);
		mv.addObject(TITLE, QueryStatisticsForm.TITLE);
		return mv;
	}

	@RequiresPermissions(value = { "tms:statistic:view" })
	@RequestMapping(value = "/terminalStatistics/{groupId}/{tsn}", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView terminalStatistics(@PathVariable Long groupId, @PathVariable String tsn, BaseForm command) {
		ModelAndView mv = new ModelAndView("dashboard/terminal_statistics");
		Group group = groupService.get(groupId);
		Terminal terminal = terminalService.get(tsn);
		if (group == null) {
			throw new BusinessException("msg.group.groupNotFound");
		}
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		mv.addObject(GROUP, group);
		mv.addObject("terminal", terminal);
		mv.addObject(ACTIVE_URL, QueryStatisticsForm.LIST_URL);
		mv.addObject(TITLE, QueryStatisticsForm.TITLE);
		return mv;
	}

	@RequestMapping("/terminalDownload/{itemName}/{itemStatus}/{groupId}")
	public ModelAndView toDownloadOrActivationPage(@PathVariable Long groupId, @PathVariable String itemName,
			@PathVariable String itemStatus, BaseForm command) {
		ModelAndView mv = new ModelAndView("dashboard/dashboard_terminalDownload");
		if (!this.reportInvalidURL(itemName, itemStatus)) {
			throw new BusinessException("msg.error.InvalidURL");
		}
		mv.addObject("itemName", itemName);
		mv.addObject("itemStatus", itemStatus);
		mv.addObject(GROUP, groupService.get(groupId));
		mv.addObject(ACTIVE_URL, REPORT_LIST_URL + itemName + "/" + itemStatus + "/");
		mv.addObject(TITLE, GroupRealStatus.TITLE);
		mv.addObject("typeList", modelService.list());
		return mv;
	}

	@RequestMapping(value = "/service/oneStatus/{itemName}/{itemStatus}")
	@ResponseBody
	public Page<Map<String, Object>> getOneStatusTerminalList(QueryTerminalForm command, @PathVariable String itemName,
			@PathVariable String itemStatus) {
		command.setItemName(itemName);
		command.setItemStatus(itemStatus.split(",")[0]);
		return terminalService.page(command);
	}

	@RequiresPermissions(value = { "tms:statistic:view" })
	@RequestMapping(value = "/service/statistics/{groupId}", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public Page<Map<String, Object>> listStatistics(QueryStatisticsForm command, @PathVariable Long groupId) {
		command.setGroupId(groupId);
		Page<Map<String, Object>> page = statisticsService.page(command);
		Group group = groupService.get(groupId);
		if (group == null) {
			throw new BusinessException("msg.group.groupNotFound");
		}
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		return page;
	}

	@ResponseBody
	@RequestMapping("/service/terminalDownload/{itemName}/{itemStatus}/{groupId}")
	public Page<Map<String, Object>> getDownloadOrActivationPageList(@PathVariable String itemName,
			@PathVariable String itemStatus, @PathVariable Long groupId, QueryTerminalDownloadForm command) {
		Date dayStart = UTCTime.getLastNHours(alertProcessService.getLastNhours());
		command.setDayStart(dayStart);
		if ("Downloads".equals(itemName)) {
			command.setDownStatusType(itemStatus);
		} else if ("Activations".equals(itemName)) {
			command.setActiStatusType(itemStatus);
		}
		if (groupId != null) {
			command.setGroupId(groupId);
		}
		Page<Map<String, Object>> page = tDownloadService.getPendingPage(command);
		return page;
	}

	@RequestMapping(value = "/service/terminalStatistics/{groupId}/{tsn}", method = { RequestMethod.GET,
			RequestMethod.POST })
	@ResponseBody
	public Object terminalStatistics(QueryStatisticsForm command, @PathVariable Long groupId,
			@PathVariable String tsn) {
		Group group = groupService.get(groupId);
		if (group == null) {
			throw new BusinessException("msg.group.groupNotFound");
		}
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		command.setTerminalId(tsn);
		return statisticsService.page(command);
	}

	@RequestMapping(value = "/real/getData/{groupId}/{userId}", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public Object getRealDashboardPie(@PathVariable Long groupId, @PathVariable Long userId, BaseForm command)
			throws JSONException {
		Group group = groupService.get(groupId);
		if (group == null) {
			throw new BusinessException("msg.group.groupNotFound");
		}
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		Map<String, Object> mapJson = new HashMap<>();
		List<Pie> pieList;
		synchronized (group) {
			pieList = alertProcessService.getRealDashboard(group, userId);
		}
		mapJson.put(STATUS_CODE, SUCCESS_STATUS_CODE);
		mapJson.put(MESSAGE, "");
		mapJson.put(RESULT, pieList);
		mapJson.put("date", new Date());
		mapJson.put("terminalNumber", groupService.getTerminalNumbersByGroupId(groupId));
		return mapJson;
	}

	@RequestMapping(value = "/realAndUsage/updateData/{userId}", method = { RequestMethod.POST })
	@ResponseBody
	@CsrfProtect
	public Map<String, Object> updateRealAndUsageDashboardPie(@PathVariable Long userId, BaseForm command, String data,
			String type) {
		alertProcessService.updateRealAndUsageDashboardPie(data, userId, type);
		return this.ajaxDoneSuccess();
	}

	@RequestMapping(value = "/usage/getData/{groupId}/{userId}", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public Object getUsageDashboardPie(@PathVariable Long groupId, @PathVariable Long userId, BaseForm command) {
	    
		Map<String, Object> mapJson = new HashMap<>();
		Group group = groupService.get(groupId);
		if (group == null) {
			throw new BusinessException("msg.group.groupNotFound");
		}
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		mapJson.put(STATUS_CODE, SUCCESS_STATUS_CODE);
		mapJson.put(MESSAGE, "");
		mapJson.put(RESULT, dashboardService.getUsagePielist(groupId, userId));
		mapJson.put(TIMEZONE_NAME, TimeZone.getDefault().getID());
		return mapJson;
	}

	@RequestMapping(value = "/usage/ajaxDetail/{groupId}/{userId}", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public Object getUsageDashboardBar(@PathVariable Long groupId, @PathVariable Long userId, BaseForm command) {
		Map<String, Object> mapJson = new HashMap<>();
		Group group = groupService.get(groupId);
		if (group == null) {
			throw new BusinessException("msg.group.groupNotFound");
		}
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		mapJson.put(STATUS_CODE, SUCCESS_STATUS_CODE);
		mapJson.put(MESSAGE, "");
		mapJson.put(RESULT, dashboardService.getUsageBarList(groupId, userId));
		mapJson.put(TIMEZONE_NAME, TimeZone.getDefault().getID());
		return mapJson;
	}

	private boolean statusInvalidURL(String itemName, String itemStatus) {
		if (!Arrays.asList(AlertConstants.getRealItems()).contains(itemName)) {
			return false;
		} else if (!Arrays.asList(new String[] { "1", "2", "3" }).contains(itemStatus.split(",")[0])) {
			return false;
		} else if ("3".equals(itemStatus.split(",")[0]) && "Offline".equals(itemName)) {
			return false;
		}
		return true;
	}

	private boolean reportInvalidURL(String itemName, String itemStatus) {
		if (!Arrays.asList(new String[] { "Activations", "Downloads" }).contains(itemName)) {
			return false;
		} else if (!Arrays.asList(new String[] { "Success", "Failed", "Pending" }).contains(itemStatus)) {
			return false;
		}
		return true;
	}

}
