/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description: Create/Modify/Delete/List/Copy/Remove/Import/Activate/Deactivate terminal	
 * Revision History:		
 * Date	                 Author	                Action
 * 20170111  	         Aaron           	    Modify TerminalController
 * ============================================================================		
 */
package com.pax.tms.terminal.controller;


import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
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
import com.pax.common.security.CsrfProtect;
import com.pax.common.util.ExcelWritter;
import com.pax.common.util.HttpServletUtils;
import com.pax.common.web.controller.BaseController;
import com.pax.common.web.form.BaseForm;
import com.pax.tms.group.model.Group;
import com.pax.tms.group.service.GroupService;
import com.pax.tms.location.model.Country;
import com.pax.tms.location.model.Province;
import com.pax.tms.location.service.AddressService;
import com.pax.tms.location.service.CityService;
import com.pax.tms.location.service.CountryService;
import com.pax.tms.location.service.ProvinceService;
import com.pax.tms.monitor.model.GroupRealStatus;
import com.pax.tms.res.service.ModelService;
import com.pax.tms.terminal.model.Terminal;
import com.pax.tms.terminal.service.TerminalService;
import com.pax.tms.terminal.web.form.AddTerminalForm;
import com.pax.tms.terminal.web.form.AssignTerminalForm;
import com.pax.tms.terminal.web.form.CopyTerminalForm;
import com.pax.tms.terminal.web.form.EditTerminalForm;
import com.pax.tms.terminal.web.form.QueryTerminalForm;
import com.pax.tms.user.security.AclManager;

@Controller
@RequestMapping(value = "/terminal")
public class TerminalController extends BaseController {

	@Autowired
	private TerminalService terminalService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private CountryService countryService;

	@Autowired
	private ProvinceService provinceService;

	@Autowired
	private CityService cityService;

	@Autowired
	private ModelService modelService;

	@Autowired
	private AddressService addressService;
	
	protected Logger logger = LoggerFactory.getLogger(TerminalController.class);

	// When user in Group Module,get terminal list
	@RequiresPermissions(value = { "tms:terminal:view" })
	@RequestMapping(value = "/list/{groupId}")
	public ModelAndView listByGroup(@PathVariable Long groupId, BaseForm command) {
		ModelAndView mav = new ModelAndView("terminal/list_view_group");
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		Group group = groupService.get(groupId);
		if (group == null) {
			throw new BusinessException("msg.group.groupNotFound");
		}
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		mav.addObject("group", group);
		mav.addObject("activeUrl", Terminal.LIST_URL);
		mav.addObject("title", Terminal.TITLE);
		return mav;
	}

	// When user in Terminal Module,get terminal list
	@RequiresPermissions(value = { "tms:terminal:view" })
	@RequestMapping(value = "/list/{groupId}/{tsn}")
	public ModelAndView listByTsn(@PathVariable Long groupId, @PathVariable String tsn, BaseForm command) {
		ModelAndView mav = new ModelAndView("terminal/list_view_terminal");
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		Group group = groupService.get(groupId);
		if (group == null) {
			throw new BusinessException("msg.group.groupNotFound");
		}
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		if (StringUtils.isEmpty(tsn)) {
			throw new BusinessException("tsn.Required");
		}
		Terminal terminal = terminalService.get(tsn);
		if (terminal == null) {
			throw new BusinessException("tsn.notFound", new String[] { tsn });
		}
		mav.addObject("terminal", terminal);
		mav.addObject("group", group);
		mav.addObject("activeUrl", Terminal.LIST_URL);
		mav.addObject("title", Terminal.TITLE);
		return mav;
	}

	@RequestMapping(value = "/service/oneStatus/{itemName}/{itemStatus}")
	@ResponseBody
	public Page<Map<String, Object>> getOneStatusTerminalList(QueryTerminalForm command, @PathVariable String itemName,
			@PathVariable String itemStatus) {
		command.setItemName(itemName);
		command.setItemStatus(itemStatus.split(",")[0]);
		Page<Map<String, Object>> page = terminalService.page(command);
		return page;
	}

	@RequiresPermissions(value = { "tms:terminal:add" })
	@RequestMapping(value = "/toAdd/{groupId}")
	public ModelAndView toAdd(@PathVariable Long groupId, BaseForm command) {
		ModelAndView mav = new ModelAndView("terminal/add");
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		Group group = groupService.get(groupId);
		if (group == null) {
			throw new BusinessException("msg.group.groupNotFound");
		}
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		List<Map<String, String>> timeZoneList;
		List<Province> provinceList;
		Map<String, Object> parentTimeZone;
		if (StringUtils.isNotEmpty(group.getCountry())) {
			timeZoneList = addressService.getTimeZonesByCountry(group.getCountry());
			Country country = countryService.getCountryByName(group.getCountry());
			provinceList = provinceService.getProvinceList(country.getId());
			parentTimeZone = addressService.getParentTimeZone(group.getCountry(), group.getTimeZone());
		} else {
			timeZoneList = Collections.emptyList();
			provinceList = Collections.emptyList();
			parentTimeZone = Collections.emptyMap();
		}
		mav.addObject("timeZoneList", timeZoneList);
		mav.addObject("parentTimeZone", parentTimeZone);
		mav.addObject("group", group);
		mav.addObject("activeUrl", Terminal.ADD_URL);
		mav.addObject("title", Terminal.TITLE);
		mav.addObject("typeList", modelService.getList());
		mav.addObject("countryList", countryService.getCounryList());
		mav.addObject("provinceList", provinceList);
		return mav;
	}

	@RequiresPermissions(value = { "tms:terminal:add" })
	@RequestMapping(value = "/service/add")
	@ResponseBody
	@CsrfProtect
	public Map<String, Object> add(AddTerminalForm command, String start, String end) {
		if (command.getGroupId() == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		if (StringUtils.isEmpty(command.getDestModel())) {
			throw new BusinessException("model.Required");
		}
		Group group = groupService.get(command.getGroupId());
		if (group == null) {
			throw new BusinessException("msg.group.groupNotFound");
		}
		if (StringUtils.isEmpty(start)) {
			throw new BusinessException("tsn.Required");
		}
		String tsnRange = start;
		if (StringUtils.isNotEmpty(end)) {
			tsnRange = tsnRange + "-" + end;
		}
		String[] tsnRanges = new String[] { tsnRange };
		command.setTsnRanges(tsnRanges);
		return terminalService.save(command);
	}

	@RequiresPermissions(value = { "tms:terminal:view" })
	@RequestMapping(value = "/service/ajaxList")
	@ResponseBody
	public Page<Map<String, Object>> getTerminalList(QueryTerminalForm command) {
		if (command.getGroupId() == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		Page<Map<String, Object>> page = terminalService.page(command);
		return page;
	}

	@RequiresPermissions(value = "tms:terminal:view")
	@RequestMapping(value = "/service/export/{groupId}", method = { RequestMethod.GET, RequestMethod.POST })
	public void export(@PathVariable long groupId, QueryTerminalForm command, HttpServletResponse response)
			throws IOException {
		groupService.checkPermissionOfGroup(command, groupId);
		Group group = groupService.get(groupId);
		List<Map<String, Object>> list = terminalService.exportList(command);
		
		String caption = "[" + group.getName() + "]" +  "-TerminalList-" + ZonedDateTime.now();
		String filename = caption.replace("/", "_") + ".xlsx";
		HttpServletUtils.setDownloadExcel(filename, response);
		ExcelWritter ew = new ExcelWritter(response.getOutputStream());
		ew.open();
		ew.writeCaption(caption, 15);
		ew.writeTitle(new String[] {"Terminal SN", "Status", "Terminal Type", "Online/Offline", "Group", "Last Accessed", "Source IP",
				"Report Time", "Installed Packages", "Sysmetric Keys", "Tamper", "Privacy Shield", "Stylus", "SRED", "RKI"});
		ew.setWidths(new int[] { 14, 14, 15, 15, 55, 20, 16, 20, 100, 100, 14, 14, 14, 14, 14 });

		list.forEach(terminal -> {
			try {
				Object apps = terminal.get("installApps");
				if (apps != null) {
					apps = apps.toString().replace("pkgName", "Name").replace("pkgVersion",
							"Version").replace("pkgType", "Type").replace("},", "}," + (char)10);
				}
				ew.writeContent(terminal.get("tsn"), terminal.get("status"),
						terminal.get("model.name"), terminal.get("ts.isOnline"), terminal.get("groupNames"), terminal.get("lastAccessed"),
						terminal.get("ts.lastSourceIP"), terminal.get("reportTime"), apps, terminal.get("sysmetricKeys"), terminal.get("tamper"),
						terminal.get("privacyShield"), terminal.get("stylus"), terminal.get("sred"),terminal.get("rki") );
			} catch (IOException e) {
				logger.error("Terminal Download", e);
			}
		});
		ew.close();
	}

	@RequiresPermissions(value = { "tms:terminal:move" })
	@RequestMapping(value = "/service/move")
	@ResponseBody
	@CsrfProtect
	public Map<String, Object> move(QueryTerminalForm command, String[] tsns) {
		if (ArrayUtils.isEmpty(tsns)) {
			throw new BusinessException("tsn.Required");
		}
		if (command.getGroupId() == null) {
			throw new BusinessException("msg.group.sourceGroupRequired");
		}
		Group sourceGroup = groupService.get(command.getGroupId());
		if (sourceGroup == null) {
			throw new BusinessException("msg.group.sourceGroupNotFound");
		}
		if (command.getTargetGroupId() == null) {
			throw new BusinessException("msg.group.targetGroupRequired");
		}
		Group targetGroup = groupService.get(command.getTargetGroupId());
		if (targetGroup == null) {
			throw new BusinessException("msg.group.targetGroupNotFound");
		}
		terminalService.moveToGroup(tsns, command);
		return this.ajaxDoneSuccess();
	}

	@RequiresPermissions(value = { "tms:terminal:copy" })
	@RequestMapping(value = "/service/copy")
	@ResponseBody
	@CsrfProtect
	public Map<String, Object> copy(String[] tsns, AssignTerminalForm command) {
		if (ArrayUtils.isEmpty(tsns)) {
			throw new BusinessException("tsn.Required");
		}
		if (ArrayUtils.isEmpty(command.getGroupIds())) {
			throw new BusinessException("msg.group.groupRequired");
		}
		return terminalService.assign(tsns, command);
	}
	
	//对未注册的终端分配组
	@RequiresPermissions(value = { "tms:unrterminal:assign" })
	@RequestMapping(value = "/service/noreg/assign")
	@ResponseBody
	@CsrfProtect
	public Map<String, Object> assign(String[] tsns, AssignTerminalForm command) {
		if (ArrayUtils.isEmpty(tsns)) {
			throw new BusinessException("tsn.Required");
		}
		if (null == command.getGroupId()) {
			throw new BusinessException("msg.group.groupRequired");
		}
		terminalService.assignNotRegistered(tsns, command);
		return this.ajaxDoneSuccess();
	}
	
	@RequiresPermissions(value = { "tms:terminal:clone" })
	@RequestMapping(value = "/service/clone")
	@ResponseBody
	@CsrfProtect
	public Map<String, Object> clone(String tsn, CopyTerminalForm command) {
		if (StringUtils.isEmpty(tsn)) {
			throw new BusinessException("tsn.Required");
		}
		if (command.getGroupId() == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		return terminalService.copy(tsn, command);
	}

	@RequiresPermissions(value = { "tms:terminal:deactive" })
	@RequestMapping(value = "/service/deactivate")
	@ResponseBody
	@CsrfProtect
	public Map<String, Object> deactivate(String[] tsns, BaseForm command) {
		if (ArrayUtils.isEmpty(tsns)) {
			throw new BusinessException("tsn.Required");
		}
		terminalService.deactivate(tsns, command);
		return this.ajaxDoneSuccess();
	}

	@RequiresPermissions(value = { "tms:terminal:active" })
	@RequestMapping(value = "/service/activate")
	@ResponseBody
	@CsrfProtect
	public Map<String, Object> activate(String[] tsns, BaseForm command) {
		if (ArrayUtils.isEmpty(tsns)) {
			throw new BusinessException("tsn.Required");
		}
		terminalService.activate(tsns, command);
		return this.ajaxDoneSuccess();
	}

	
	@RequiresPermissions(value = { "tms:terminal:view" })
	@RequestMapping(value = "/profile/{groupId}/{tsn}")
	public ModelAndView profile(@PathVariable Long groupId, @PathVariable String tsn, BaseForm command) {
		ModelAndView mav = new ModelAndView("terminal/profile");
		if (StringUtils.isEmpty(tsn)) {
			throw new BusinessException("tsn.Required");
		}
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		Group group = groupService.get(groupId);
		if (group == null) {
			throw new BusinessException("msg.group.groupNotFound");
		}
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		Terminal terminal = terminalService.get(tsn);
		if (terminal == null) {
			throw new BusinessException("tsn.notFound", new String[] { tsn });
		}
		List<String> groupNames = terminalService.getGroupNameByTsn(tsn);
		List<Map<String, String>> timeZoneList;
		List<Province> provinceList;
		Map<String, Object> parentTimeZone;
		if (StringUtils.isEmpty(terminal.getCountry())) {
			timeZoneList = Collections.emptyList();
			provinceList = Collections.emptyList();
			parentTimeZone = Collections.emptyMap();
		} else {
			parentTimeZone = addressService.getParentTimeZone(terminal.getCountry(), terminal.getTimeZone());
			timeZoneList = addressService.getTimeZonesByCountry(terminal.getCountry());
			provinceList = provinceService.list();
		}
		mav.addObject("parentTimeZone", parentTimeZone);
		mav.addObject("timeZoneList", timeZoneList);
		mav.addObject("groupNames", groupNames);
		mav.addObject("group", group);
		mav.addObject("activeUrl", Terminal.LIST_URL);
		mav.addObject("title", Terminal.TITLE);
		mav.addObject("terminal", terminal);
		mav.addObject("countryList", countryService.getCounryList());
		mav.addObject("provinceList", provinceList);
		mav.addObject("cityList", cityService.list());
		return mav;
	}

	@RequiresPermissions(value = { "tms:terminal:edit" })
	@RequestMapping(value = "/service/edit")
	@ResponseBody
	@CsrfProtect
	public Map<String, Object> edit(String tsn, EditTerminalForm command) {
		if (StringUtils.isEmpty(tsn)) {
			throw new BusinessException("tsn.Required");
		}
		terminalService.edit(tsn, command);
		return this.ajaxDoneSuccess();
	}

	@RequiresPermissions(value = { "tms:terminal:import" })
	@RequestMapping(value = "toImport/{groupId}")
	public ModelAndView toImport(@PathVariable Long groupId, BaseForm command) {
		ModelAndView mav = new ModelAndView("terminal/import");
		if (groupId == null) {
			throw new BusinessException("msg.group.parentRequired");
		}
		Group group = groupService.get(groupId);
		if (group == null) {
			throw new BusinessException("msg.group.parentNotFound");
		}
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		mav.addObject("group", group);
		mav.addObject("activeUrl", Terminal.LIST_URL);
		mav.addObject("title", Terminal.TITLE);
		return mav;
	}

	@RequiresPermissions(value = { "tms:terminal:remove" })
	@ResponseBody
	@RequestMapping(value = "/service/dismiss")
	@CsrfProtect
	public Map<String, Object> dismiss(String[] tsns, BaseForm command) {
		if (ArrayUtils.isEmpty(tsns)) {
			throw new BusinessException("tsn.Required");
		}
		Long groupId = command.getGroupId();
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		if (groupService.get(groupId).isEnterPriceGroup()) {
			terminalService.delete(tsns, command);
		} else {
			terminalService.dismiss(tsns, command);
		}
		return this.ajaxDoneSuccess();
	}

	@RequiresPermissions(value = { "tms:terminal:delete" })
	@ResponseBody
	@RequestMapping(value = "/service/delete")
	@CsrfProtect
	public Map<String, Object> delete(String[] tsns, BaseForm command) {
		if (ArrayUtils.isEmpty(tsns)) {
			throw new BusinessException("tsn.Required");
		}
		Long groupId = command.getGroupId();
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		terminalService.delete(tsns, command);
		return this.ajaxDoneSuccess();
	}

	@ResponseBody
	@RequestMapping(value = "/getUsageStatus/{tsn}/{userId}", method = { RequestMethod.GET, RequestMethod.POST })
	public Object getUsageStatus(@PathVariable String tsn, @PathVariable Long userId) {
		Map<String, Object> mapJson = new HashMap<String, Object>();
		mapJson.put("statusCode", SUCCESS_STATUS_CODE);
		mapJson.put("result", terminalService.getUsageStatus(tsn, userId));
		return mapJson;
	}

	@ResponseBody
	@RequestMapping(value = "/getUsageStatusBar/{tsn}/{userId}", method = { RequestMethod.GET, RequestMethod.POST })
	public Object getUsageStatusBar(@PathVariable String tsn, @PathVariable Long userId) {
		Map<String, Object> mapJson = new HashMap<String, Object>();
		mapJson.put("statusCode", SUCCESS_STATUS_CODE);
		mapJson.put("result", terminalService.getUsageStatusBarList(tsn, userId));
		return mapJson;
	}

	@ResponseBody
	@RequestMapping(value = "/getRealStatus/{tsn}/{userId}", method = { RequestMethod.GET, RequestMethod.POST })
	public Object getRealStatus(@PathVariable String tsn, @PathVariable Long userId) {
		Map<String, Object> mapJson = new HashMap<String, Object>();
		mapJson.put("statusCode", SUCCESS_STATUS_CODE);
		mapJson.put("date", new Date());
		mapJson.put("result", terminalService.getRealStatus(tsn, userId));
		return mapJson;
	}

	@RequiresPermissions(value = { "tms:dashboard:view" })
	@RequestMapping(value = "/monitor/{groupId}/{tsn}")
	public ModelAndView monitor(@PathVariable Long groupId, @PathVariable String tsn) {
		ModelAndView mav = new ModelAndView("terminal/monitor");
		if (groupId == null) {
			throw new BusinessException("msg.group.parentRequired");
		}
		Group group = groupService.get(groupId);
		if (group == null) {
			throw new BusinessException("msg.group.parentNotFound");
		}
		Terminal terminal = terminalService.get(tsn);
		List<String> groupNames = terminalService.getGroupNameByTsn(tsn);
		mav.addObject("groupNames", groupNames);
		mav.addObject("group", group);
		mav.addObject("activeUrl", GroupRealStatus.LIST_URL);
		mav.addObject("title", GroupRealStatus.TITLE);
		mav.addObject("terminal", terminal);
		return mav;
	}

}
