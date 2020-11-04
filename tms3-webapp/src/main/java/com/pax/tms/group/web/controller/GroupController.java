/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description: Create/Modify/Delete/List/Copy/Move/Import/Search group	
 * Revision History:		
 * Date	                 Author	                Action
 * 20170111  	         Aaron           	    Modify GroupController
 * ============================================================================		
 */
package com.pax.tms.group.web.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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
import com.pax.common.web.controller.BaseController;
import com.pax.common.web.form.BaseForm;
import com.pax.tms.group.GroupOrTerminal;
import com.pax.tms.group.model.Group;
import com.pax.tms.group.service.GroupService;
import com.pax.tms.group.service.GroupTreeService;
import com.pax.tms.group.web.form.AddGroupForm;
import com.pax.tms.group.web.form.CopyGroupForm;
import com.pax.tms.group.web.form.EditGroupForm;
import com.pax.tms.group.web.form.MoveGroupForm;
import com.pax.tms.group.web.form.QueryGroupForm;
import com.pax.tms.location.model.Country;
import com.pax.tms.location.model.Province;
import com.pax.tms.location.service.AddressService;
import com.pax.tms.location.service.CountryService;
import com.pax.tms.location.service.ProvinceService;
import com.pax.tms.user.security.AclManager;
import com.pax.tms.user.service.UserGroupService;

@Controller
@RequestMapping(value = "/group")
public class GroupController extends BaseController {

	@Autowired
	private GroupService groupService;

	@Autowired
	private GroupTreeService groupTreeService;

	@Autowired
	private CountryService countryService;

	@Autowired
	private ProvinceService provinceService;

	@Autowired
	private AddressService addressService;

	@Autowired
	private UserGroupService userGroupService;

	// get all groups
	@RequestMapping(value = "/service/tree/groupContext")
	@ResponseBody
	public Page<Map<String, Object>> getGroupContext(QueryGroupForm command) {
		return groupTreeService.getGroupContext(command);
	}

	// get subgroups of the group which is selected
	@RequestMapping(value = "/service/tree/subgroup")
	@ResponseBody
	public Page<Map<String, Object>> getSubgroup(QueryGroupForm command) {
		return groupTreeService.getSubgroup(command);
	}

	// get itself and subgroups of the group which is selected
	@RequestMapping(value = "/service/tree/descantGroup")
	@ResponseBody
	public Page<Map<String, Object>> getDescantGroup(QueryGroupForm command) {
		return groupTreeService.getDescantGroup(command);
	}

	@RequestMapping(value = "/service/tree/search")
	@ResponseBody
	public Page<Map<String, Object>> searchGroupTree(QueryGroupForm command) {
		return groupTreeService.searchGroupTree(command);
	}

	@RequiresPermissions(value = { "tms:group:view" })
	@RequestMapping(value = "/list/{groupId}")
	public ModelAndView list(@PathVariable Long groupId, BaseForm command) {
		ModelAndView mav = new ModelAndView("group/list");
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}

		Group group = groupService.get(groupId);
		if (group == null) {
			throw new BusinessException("msg.group.groupNotFound");
		}
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		List<Long> groupIds = userGroupService.getGroupIdsOld(command.getLoginUserId());
		mav.addObject("group", group);
		mav.addObject("activeUrl", Group.LIST_URL);
		mav.addObject("title", Group.TITLE);
		mav.addObject("groupIds", groupIds);
		return mav;
	}

	@RequiresPermissions(value = { "tms:group:view" })
	@RequestMapping(value = "/profile/{groupId}/{viewGroupId}")
	public ModelAndView profile(@PathVariable Long groupId, @PathVariable Long viewGroupId, BaseForm command) {
		ModelAndView mav = new ModelAndView("group/profile");
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		Group group = groupService.get(groupId);
		if (group == null) {
			throw new BusinessException("msg.group.groupNotFound");
		}
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		Group viewGroup = groupService.get(viewGroupId);
		if (viewGroup == null) {
            throw new BusinessException("msg.group.groupNotFound");
        }
		if(!viewGroup.getIdPath().startsWith(group.getIdPath())){
		    throw new BusinessException("msg.user.notGrantedGroup");
		}
		
		List<String> types = groupService.getTerminalTypesByGroupId(viewGroupId);
		String termianlTypes = "";
		if (CollectionUtils.isNotEmpty(types)) {
			termianlTypes = getTerminalTypes(types);
		}
		Long terminalNumber = groupService.getTerminalNumbersByGroupId(viewGroupId);
		List<Map<String, String>> timeZoneList = new ArrayList<>();
		List<Province> provinceList = new ArrayList<Province>();
		Map<String, Object> parentTimeZone = new HashMap<>();
		if (StringUtils.isEmpty(viewGroup.getCountry())) {
			timeZoneList = Collections.emptyList();
			provinceList = Collections.emptyList();
			parentTimeZone = Collections.emptyMap();
		} else {
			timeZoneList = addressService.getTimeZonesByCountry(viewGroup.getCountry());
			parentTimeZone = addressService.getParentTimeZone(viewGroup.getCountry(), viewGroup.getTimeZone());
			provinceList = provinceService.list();
		}
		List<Long> groupIds = userGroupService.getGroupIdsOld(command.getLoginUserId());
		mav.addObject("groupIds", groupIds);
		mav.addObject("parentTimeZone", parentTimeZone);
		mav.addObject("timeZoneList", timeZoneList);
		mav.addObject("termianlTypes", termianlTypes);
		mav.addObject("terminalNumber", terminalNumber);
		mav.addObject("group", group);
		mav.addObject("viewGroup", viewGroup);
		mav.addObject("activeUrl", Group.LIST_URL);
		mav.addObject("title", Group.TITLE);
		mav.addObject("countryList", countryService.getCounryList());
		mav.addObject("provinceList", provinceList);
		return mav;
	}

	public String getTerminalTypes(List<String> types) {
		int size = types.size();
		String string = "";
		for (int i = 0; i < size; i++) {
			if (i == size - 1) {
				string = string + types.get(i);
			} else {
				string = string + types.get(i) + "/";
			}
		}
		return string;
	}

	@RequiresPermissions(value = { "tms:group:delete" })
	@ResponseBody
	@RequestMapping(value = "/service/delete")
	@CsrfProtect
	public Map<String, Object> delete(Long id, BaseForm command) {
		if (id == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		Group group = groupService.get(id);
		if (group == null) {
			throw new BusinessException("msg.group.groupNotFound");
		}
		groupService.delete(id, command);
		return this.ajaxDoneSuccess();
	}
	
	//同步某个组的字段 Country、State/Province、City、ZIP/Postal Code、Time Zone、Daylight Saving
		//到该组下的所有终端对应的字段值
	@RequiresPermissions(value = { "tms:group:sync" })
	@ResponseBody
	@RequestMapping(value = "/service/syncProfileToTerminals", method = RequestMethod.POST)
	@CsrfProtect
	public Map<String, Object> syncProfileToTerminals(Long id, BaseForm command) {
		if (id == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		Group group = groupService.get(id);
		if (group == null) {
			throw new BusinessException("msg.group.groupNotFound");
		}
		groupService.syncProfileToTerminals(id, command);
		return this.ajaxDoneSuccess();
	}

	@RequiresPermissions(value = { "tms:group:move" })
	@RequestMapping(value = "/service/move", method = RequestMethod.POST)
	@ResponseBody
	@CsrfProtect
	public Map<String, Object> move(MoveGroupForm command) throws CloneNotSupportedException {
		if (command.getSourceGroupId() == null) {
			throw new BusinessException("msg.group.sourceGroupRequired");
		}
		if (command.getTargetGroupId() == null) {
			throw new BusinessException("msg.group.targetGroupRequired");
		}
		groupService.move(command);
		return this.ajaxDoneSuccess();
	}

	@RequiresPermissions(value = { "tms:group:copy" })
	@RequestMapping(value = "/service/copy", method = RequestMethod.POST)
	@ResponseBody
	@CsrfProtect
	public Map<String, Object> copy(CopyGroupForm command) {
		if (command.getSourceGroupId() == null) {
			throw new BusinessException("msg.group.sourceGroupRequired");
		}
		if (command.getTargetGroupId() == null) {
			throw new BusinessException("msg.group.targetGroupRequired");
		}
		groupService.copy(command);
		return this.ajaxDoneSuccess();
	}

	@RequiresPermissions(value = { "tms:group:add" })
	@RequestMapping(value = "/toAdd/{groupId}/{parentId}")
	public ModelAndView toAdd(@PathVariable Long groupId, @PathVariable Long parentId, BaseForm command) {
		ModelAndView mav = new ModelAndView("group/add");
		if (groupId == null) {
			throw new BusinessException("msg.group.parentRequired");
		}
		Group group = groupService.get(groupId);
		if (group == null) {
			throw new BusinessException("msg.group.parentNotFound");
		}
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		Group parentGroup = groupService.get(parentId);
		List<Map<String, String>> timeZoneList = new ArrayList<>();
		List<Province> provinceList = new ArrayList<Province>();
		Map<String, Object> parentTimeZone = new HashMap<>();
		if (StringUtils.isNotEmpty(parentGroup.getCountry())) {
			timeZoneList = addressService.getTimeZonesByCountry(parentGroup.getCountry());
			Country country = countryService.getCountryByName(parentGroup.getCountry());
			provinceList = provinceService.getProvinceList(country.getId());
			parentTimeZone = addressService.getParentTimeZone(parentGroup.getCountry(), parentGroup.getTimeZone());
		} else {
			timeZoneList = Collections.emptyList();
			provinceList = Collections.emptyList();
			parentTimeZone = Collections.emptyMap();
		}
		mav.addObject("timeZoneList", timeZoneList);
		mav.addObject("parentTimeZone", parentTimeZone);
		mav.addObject("parentGroup", parentGroup);
		mav.addObject("group", group);
		mav.addObject("countryList", countryService.getCounryList());
		mav.addObject("provinceList", provinceList);
		mav.addObject("activeUrl", Group.LIST_URL);
		mav.addObject("title", Group.TITLE);
		return mav;
	}

	@RequiresPermissions(value = { "tms:group:add" })
	@RequestMapping(value = "/service/add", method = RequestMethod.POST)
	@ResponseBody
	@CsrfProtect
	public Map<String, Object> add(AddGroupForm command) {
		if (StringUtils.isEmpty(command.getName())) {
			throw new BusinessException("msg.group.nameRequired");
		}
		if (command.getParentId() == null) {
			throw new BusinessException("msg.group.parentRequired");
		}
		groupService.save(command);

		return this.ajaxDoneSuccess();
	}

	@RequiresPermissions(value = { "tms:group:edit" })
	@RequestMapping(value = "/service/edit", method = RequestMethod.POST)
	@ResponseBody
	@CsrfProtect
	public Map<String, Object> edit(EditGroupForm command, Long id) {
		if (id == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		if (groupService.get(id) == null) {
			throw new BusinessException("msg.group.groupNotFound");
		}
		groupService.edit(id, command);
		return this.ajaxDoneSuccess();
	}

	@RequiresPermissions(value = { "tms:group:import" })
	@RequestMapping(value = "/toImport/{groupId}")
	public ModelAndView toImport(@PathVariable Long groupId, BaseForm command) {
		ModelAndView mav = new ModelAndView("group/import");
		if (groupId == null) {
			throw new BusinessException("msg.group.parentRequired");
		}
		Group group = groupService.get(groupId);
		if (group == null) {
			throw new BusinessException("msg.group.parentNotFound");
		}
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		mav.addObject("group", group);
		mav.addObject("activeUrl", Group.LIST_URL);
		mav.addObject("title", Group.TITLE);
		return mav;
	}

	@ResponseBody
	@RequestMapping(value = "/service/searchGroupOrTerminal")
	public List<GroupOrTerminal> searchGroupOrTerminal(QueryGroupForm command) {
		List<GroupOrTerminal> list = groupTreeService.searchGroupOrTerminal(command);
		return list;
	}

}
