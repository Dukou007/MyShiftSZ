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
package com.pax.tms.monitor.web.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.pax.common.pagination.Page;
import com.pax.common.web.controller.BaseController;
import com.pax.common.web.form.BaseForm;
import com.pax.tms.group.model.Group;
import com.pax.tms.group.service.GroupService;
import com.pax.tms.monitor.model.AlertEvent;
import com.pax.tms.monitor.model.EventTrm;
import com.pax.tms.monitor.service.AlertEventService;
import com.pax.tms.monitor.service.EventGrpService;
import com.pax.tms.monitor.web.form.QueryAlertEventForm;
import com.pax.tms.monitor.web.form.QueryTerminalEventForm;
import com.pax.tms.terminal.model.Terminal;
import com.pax.tms.terminal.service.TerminalService;
import com.pax.tms.user.security.AclManager;
import com.pax.tms.user.security.UTCTime;
import com.pax.tms.user.security.UTCTime.TimeRange;

@Controller
@RequestMapping(value = "/events")
public class EventsController extends BaseController {
	private static final String GROUP = "group";
	private static final String ACTIVE_URL = "activeUrl";
	private static final String TITLE = "title";
	@Autowired
	private AlertEventService alertEventService;
	@Autowired
	private EventGrpService eventService;
	@Autowired
	private GroupService groupService;
	@Autowired
	private TerminalService terminalService;

	@RequiresPermissions(value = { "tms:alert:list:view" })
	@RequestMapping(value = "/alertEvents/{groupId}", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView listEvents(@PathVariable Long groupId, BaseForm command) {
		ModelAndView mv = new ModelAndView("alert/listAlert");
		Group group = groupService.get(groupId);
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		mv.addObject(GROUP, group);
		mv.addObject(ACTIVE_URL, AlertEvent.LIST_URL);
		mv.addObject(TITLE, "ALERT");
		return mv;
	}

	@RequiresPermissions(value = { "tms:event:view" })
	@RequestMapping(value = "/terminalEvents/{groupId}/{terminalId}", method = { RequestMethod.GET,
			RequestMethod.POST })
	public ModelAndView terminalEvents(@PathVariable Long groupId, @PathVariable String terminalId, BaseForm command) {
		ModelAndView mv = new ModelAndView("events/terminalEvents");
		Group group = groupService.get(groupId);
		Terminal terminal = terminalService.get(terminalId);
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		mv.addObject(GROUP, group);
		mv.addObject("terminal", terminal);
		mv.addObject(ACTIVE_URL, "/events/allEvents/");
		mv.addObject(TITLE, EventTrm.TITLE);
		return mv;
	}

	@RequiresPermissions(value = { "tms:event:view" })
	@RequestMapping(value = "/allEvents/{groupId}", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView allEvents(@PathVariable Long groupId, BaseForm command) {
		ModelAndView mv = new ModelAndView("events/listEvents");
		Group group = groupService.get(groupId);
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		mv.addObject(GROUP, group);
		mv.addObject(ACTIVE_URL, EventTrm.LIST_URL);
		mv.addObject(TITLE, EventTrm.TITLE);
		return mv;
	}

	@RequiresPermissions(value = { "tms:alert:list:view" })
	@RequestMapping(value = "/service/alertEvents/{groupId}", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public Page<Map<String, Object>> listAlertEvents(QueryAlertEventForm command, @PathVariable Long groupId) {
		command.setGroupId(groupId);
		Group group = groupService.get(groupId);
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		return alertEventService.page(command);
	}

	@RequiresPermissions(value = { "tms:event:view" })
	@RequestMapping(value = "/service/terminalEvents/{terminalId}/{days}", method = { RequestMethod.GET,
			RequestMethod.POST })
	@ResponseBody
	public Page<Map<String, Object>> listTerminalEvents(QueryTerminalEventForm command, @PathVariable String terminalId,
			@PathVariable int days) {
		command.setTerminalId(terminalId);
		//修复某个终端没有event list bug
		TimeRange dtr = UTCTime.getLastNDayForUTC(days);
		command.setFromDate(dtr.getFrom());
		command.setToDate(dtr.getTo());
		return eventService.page(command);
	}

	@RequiresPermissions(value = { "tms:event:view" })
	@RequestMapping(value = "/service/allEvents/{groupId}/{searchType}/{days}", method = { RequestMethod.GET,
			RequestMethod.POST })
	@ResponseBody
	public Page<Map<String, Object>> listAllEvents(QueryTerminalEventForm command, @PathVariable Long groupId,
			@PathVariable String searchType, @PathVariable int days) {
		Group group = groupService.get(groupId);
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		TimeRange dtr = UTCTime.getLastNDayForUTC(days);
		command.setFromDate(dtr.getFrom());
		command.setToDate(dtr.getTo());
		command.setGroupId(groupId);
		command.setSearchType(searchType);
		if ("All".equals(command.getSearchType())) {
			String pageStatus = command.getPaginationStatus();
			int index = command.getPageIndex();
			int size = command.getPageSize();
			long totalCount = eventService.countDao(command);
			if ("lastPage".equals(pageStatus)) {
				index = (int) ((totalCount % size > 0) ? (totalCount / size + 1) : (totalCount / size));
			}
			List<Map<String, Object>> list = new ArrayList<>();
			List<Map<String, Object>> listG = eventService.callGrp(command, 0, command.getPageSize());
			List<Map<String, Object>> listT = eventService.callTrm(command, 0, command.getPageSize());
			list.addAll(listG);
			list.addAll(listT);
			if ("lastPage".equals(pageStatus) || "previousPage".equals(pageStatus)) {
				this.sortListAsc(list);
			} else {
				this.sortListDesc(list);
			}
			if (list.size() > 10) {
				if ("lastPage".equals(pageStatus)) {
					list = list.subList(0, (int) ((totalCount % size) > 0 ? (totalCount % size) : size));
				} else {
					list = list.subList(0, size);
				}
			}
			this.sortListDesc(list);
			return Page.getPage(index, size, totalCount, list);
		} else {
			String pageStatus = command.getPaginationStatus();
			int index = command.getPageIndex();
			int size = command.getPageSize();
			long totalCount = eventService.count(command);
			if ("lastPage".equals(pageStatus)) {
				index = (int) ((totalCount % size > 0) ? (totalCount / size + 1) : (totalCount / size));
			}
			command.setPageIndex(index);
			return eventService.page(command);
		}
	}

	private List<Map<String, Object>> sortListAsc(List<Map<String, Object>> list) {
		list.sort((map1, map2) -> {
			BigDecimal eventId1 = (BigDecimal) map1.get("EVENTID");
			BigDecimal eventId2 = (BigDecimal) map2.get("EVENTID");
			Date eventTime1 = (Date) map1.get("EVENTTIME");
			Date eventTime2 = (Date) map2.get("EVENTTIME");

			int timeRs = eventTime1.compareTo(eventTime2);

			if (timeRs == 0) {
				return eventId1.compareTo(eventId2);
			} else {
				return timeRs;
			}
		});
		return list;
	}

	/**
	 * Sort by eventTime, eventId desc
	 * 
	 * @param list
	 * @return
	 */
	private List<Map<String, Object>> sortListDesc(List<Map<String, Object>> list) {
		list.sort((map1, map2) -> {
			BigDecimal eventId1 = (BigDecimal) map1.get("EVENTID");
			BigDecimal eventId2 = (BigDecimal) map2.get("EVENTID");
			Date eventTime1 = (Date) map1.get("EVENTTIME");
			Date eventTime2 = (Date) map2.get("EVENTTIME");

			int timeRs = eventTime2.compareTo(eventTime1);

			if (timeRs == 0) {
				return eventId2.compareTo(eventId1);
			} else {
				return timeRs;
			}
		});
		return list;
	}
}
