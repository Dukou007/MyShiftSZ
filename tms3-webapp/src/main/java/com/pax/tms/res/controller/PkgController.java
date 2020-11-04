/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description: Create/Modify/Delete/List/Assign/Remove package	
 * Revision History:		
 * Date	                 Author	                Action
 * 20170111  	         Aaron           	    Modify PkgController
 * 20170112				 Aaron					Modify 
 * ============================================================================		
 */
package com.pax.tms.res.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.pax.common.exception.BusinessException;
import com.pax.common.pagination.Page;
import com.pax.common.security.CsrfProtect;
import com.pax.common.web.controller.BaseController;
import com.pax.common.web.form.BaseForm;
import com.pax.tms.deploy.model.GroupDeploy;
import com.pax.tms.deploy.model.TerminalDeploy;
import com.pax.tms.group.model.Group;
import com.pax.tms.group.service.GroupService;
import com.pax.tms.res.model.Pkg;
import com.pax.tms.res.model.PkgProgram;
import com.pax.tms.res.service.PkgService;
import com.pax.tms.res.web.form.AddPkgForm;
import com.pax.tms.res.web.form.AssignPkgForm;
import com.pax.tms.res.web.form.EditPkgForm;
import com.pax.tms.res.web.form.QueryPkgForm;
import com.pax.tms.terminal.model.Terminal;
import com.pax.tms.terminal.service.TerminalService;
import com.pax.tms.user.security.AclManager;

@Controller
@RequestMapping(value = "/pkg")
public class PkgController extends BaseController {

	@Autowired
	private PkgService pkgService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private TerminalService terminalService;

	// user can do all operations about package in this page
	@RequiresPermissions(value = { "tms:package:view" })
	@RequestMapping(value = "/manageList/{groupId}")
	public ModelAndView manageList(@PathVariable Long groupId, BaseForm command) {
		ModelAndView mav = new ModelAndView("pkg/list_manage");
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		Group group = groupService.get(groupId);
		if (group == null) {
			throw new BusinessException("msg.group.groupNotFound");
		}
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		mav.addObject("group", group);
		mav.addObject("activeUrl", Pkg.LIST_MANAGE_URL);
		return mav;
	}

	// When user in Group Module, user only can view and assign package
	@RequiresPermissions(value = { "tms:package:view" })
	@RequestMapping(value = "/list/{groupId}")
	public ModelAndView listByGroup(@PathVariable Long groupId, BaseForm command) {
		ModelAndView mav = new ModelAndView("pkg/list_view");
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		Group group = groupService.get(groupId);
		if (group == null) {
			throw new BusinessException("msg.group.groupNotFound");
		}
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		mav.addObject("group", group);
		mav.addObject("activeUrl", Pkg.LIST_URL);
		mav.addObject("title", Pkg.TITLE);
		return mav;
	}

	// When user in Terminal Module, user only can view and assign package
	@RequiresPermissions(value = { "tms:package:view" })
	@RequestMapping(value = "/list/{groupId}/{tsn}")
	public ModelAndView listByTsn(@PathVariable Long groupId, @PathVariable String tsn, BaseForm command) {
		ModelAndView mav = new ModelAndView("pkg/list_view");
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
		mav.addObject("activeUrl", Pkg.LIST_URL);
		mav.addObject("title", Pkg.TITLE);
		return mav;
	}

	@RequiresPermissions(value = { "tms:package:view" })
	@ResponseBody
	@RequestMapping(value = "/service/ajaxList/{groupId}")
	public Page<Map<String, Object>> getPkgList(QueryPkgForm command, @PathVariable Long groupId) {
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		command.setGroupId(groupId);
		Page<Map<String, Object>> page = pkgService.page(command);
		return page;
	}
	
	@RequiresPermissions(value = { "tms:package:view" })
	@ResponseBody
	@RequestMapping(value = "/service/ajaxListByName")
	public List<Map<String, Object>> getPkgListByName(String name, Long groupId) {
	    List<Map<String, Object>> result = pkgService.getPkgListByName(name, groupId);
	    return result;
	}

	@RequiresPermissions(value = { "tms:package:add" })
	@RequestMapping(value = "/toAdd/{groupId}")
	public ModelAndView toAdd(@PathVariable Long groupId, BaseForm command) {
		ModelAndView mav = new ModelAndView("pkg/add");
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		Group group = groupService.get(groupId);
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		if (group == null) {
			throw new BusinessException("msg.group.groupNotFound");
		}
		mav.addObject("group", group);
		mav.addObject("activeUrl", Pkg.LIST_URL);
		return mav;
	}

	@RequiresPermissions(value = { "tms:package:add" })
	@ResponseBody
	@RequestMapping(value = "/service/add")
	@CsrfProtect
	public Map<String, Object> add(AddPkgForm command) throws IOException {
		if (ArrayUtils.isEmpty(command.getGroupIds())) {
			throw new BusinessException("msg.group.groupRequired");
		}
		if (StringUtils.isEmpty(command.getFilePath())) {
			throw new BusinessException("msg.packageFilePath.required");
		}
		if (StringUtils.isEmpty(command.getFileName())) {
			throw new BusinessException("pkgName.Required");
		}
		pkgService.save(command);
		return this.ajaxDoneSuccess();
	}

	// user can view , modify and remove the package
	@RequiresPermissions(value = { "tms:package:view" })
	@RequestMapping(value = "/profileManage/{groupId}/{pkgId}")
	public ModelAndView profileManage(@PathVariable Long groupId, @PathVariable Long pkgId, BaseForm command) {
		ModelAndView mav = new ModelAndView("pkg/profile_manage");
		if (pkgId == null) {
			throw new BusinessException("msg.pkg.required");
		}
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		Group group = groupService.get(groupId);
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		if (group == null) {
			throw new BusinessException("msg.group.groupNotFound");
		}
		Pkg pkg = pkgService.get(pkgId);
		List<String> groupNames = pkgService.getGroupNames(pkgId);
		List<PkgProgram> pkgProgramList = pkgService.getPkgProgramInfo(pkgId);
		mav.addObject("pkgProgramList", pkgProgramList);
		mav.addObject("groupNames", groupNames);
		mav.addObject("group", group);
		mav.addObject("pkg", pkg);
		mav.addObject("activeUrl", Pkg.LIST_MANAGE_URL);
		return mav;
	}

	// When user in Group Module, user only can view package
	@RequiresPermissions(value = { "tms:package:view" })
	@RequestMapping(value = "/profileView/{groupId}/{pkgId}")
	public ModelAndView profileByGroup(@PathVariable Long groupId, @PathVariable Long pkgId, String type,
			BaseForm command) {
		ModelAndView mav = new ModelAndView("pkg/profile_view");
		if (pkgId == null) {
			throw new BusinessException("msg.pkg.required");
		}
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		Group group = groupService.get(groupId);
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		if (group == null) {
			throw new BusinessException("msg.group.groupNotFound");
		}
		Pkg pkg = pkgService.get(pkgId);
		List<String> groupNames = pkgService.getGroupNames(pkgId);
		List<PkgProgram> pkgProgramList = pkgService.getPkgProgramInfo(pkgId);
		if (StringUtils.equals(type, "1")) {
			mav.addObject("activeUrl", Pkg.LIST_URL);
		} else {
			mav.addObject("activeUrl", GroupDeploy.LIST_URL);
		}
		mav.addObject("pkgProgramList", pkgProgramList);
		mav.addObject("groupNames", groupNames);
		mav.addObject("group", group);
		mav.addObject("pkg", pkg);
		mav.addObject("title", Pkg.TITLE);
		return mav;
	}

	// When user in Terminal Module, user only can view package
	@RequiresPermissions(value = { "tms:package:view" })
	@RequestMapping(value = "/profileView/{groupId}/{tsn}/{pkgId}")
	public ModelAndView profileByTsn(@PathVariable Long groupId, @PathVariable Long pkgId, @PathVariable String tsn,
			String type, BaseForm command) {
		ModelAndView mav = new ModelAndView("pkg/profile_view");
		if (pkgId == null) {
			throw new BusinessException("msg.pkg.required");
		}
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		Group group = groupService.get(groupId);
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		if (group == null) {
			throw new BusinessException("msg.group.groupNotFound");
		}
		Pkg pkg = pkgService.get(pkgId);
		List<String> groupNames = pkgService.getGroupNames(pkgId);
		if (StringUtils.isEmpty(tsn)) {
			throw new BusinessException("tsn.Required");
		}
		Terminal terminal = terminalService.get(tsn);
		if (terminal == null) {
			throw new BusinessException("tsn.notFound", new String[] { tsn });
		}
		List<PkgProgram> pkgProgramList = pkgService.getPkgProgramInfo(pkgId);
		if (StringUtils.equals(type, "1")) {
			mav.addObject("activeUrl", Pkg.LIST_URL);
		} else {
			mav.addObject("activeUrl", TerminalDeploy.LIST_URL);
		}
		mav.addObject("pkgProgramList", pkgProgramList);
		mav.addObject("terminal", terminal);
		mav.addObject("groupNames", groupNames);
		mav.addObject("group", group);
		mav.addObject("pkg", pkg);
		mav.addObject("title", Pkg.TITLE);
		return mav;
	}

	@RequiresPermissions(value = { "tms:package:edit" })
	@ResponseBody
	@RequestMapping(value = "/service/edit/{pkgId}")
	@CsrfProtect
	public Map<String, Object> edit(EditPkgForm command, @PathVariable Long pkgId) {
		if (pkgId == null) {
			throw new BusinessException("msg.pkg.required");
		}
		pkgService.edit(pkgId, command);
		return this.ajaxDoneSuccess();
	}

	@RequiresPermissions(value = { "tms:package:remove" })
	@ResponseBody
	@RequestMapping(value = "/service/dismiss")
	@CsrfProtect
	public Map<String, Object> dismiss(Long[] pkgId, BaseForm command) {
		if (ArrayUtils.isEmpty(pkgId)) {
			throw new BusinessException("msg.pkg.required");
		}
		List<Long> list = Arrays.asList(pkgId);
		List<Long> pkgList = new ArrayList<>();
		pkgList.addAll(list);
		Long groupId = command.getGroupId();
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}

		if (groupService.get(groupId).isEnterPriceGroup()) {
			pkgService.delete(pkgList, command);
		} else {
			pkgService.dismiss(pkgList, command);
		}
		return this.ajaxDoneSuccess();
	}

	@RequiresPermissions(value = { "tms:package:delete" })
	@ResponseBody
	@RequestMapping(value = "/service/delete")
	@CsrfProtect
	public Map<String, Object> delete(Long[] pkgId, BaseForm command) {
		if (ArrayUtils.isEmpty(pkgId)) {
			throw new BusinessException("msg.pkg.required");
		}
		List<Long> list = Arrays.asList(pkgId);
		List<Long> pkgList = new ArrayList<>();
		pkgList.addAll(list);
		Long groupId = command.getGroupId();
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		pkgService.delete(pkgList, command);
		return this.ajaxDoneSuccess();
	}

	@RequiresPermissions(value = { "tms:package:active" })
	@ResponseBody
	@RequestMapping(value = "/service/active")
	@CsrfProtect
	public Map<String, Object> active(Long[] pkgId, BaseForm command) {
		if (ArrayUtils.isEmpty(pkgId)) {
			throw new BusinessException("msg.pkg.required");
		}
		pkgService.activate(pkgId, command);
		return this.ajaxDoneSuccess();
	}

	@RequiresPermissions(value = { "tms:package:deactive" })
	@ResponseBody
	@RequestMapping(value = "/service/deactive")
	@CsrfProtect
	public Map<String, Object> deactive(Long[] pkgId, BaseForm command) {
		if (ArrayUtils.isEmpty(pkgId)) {
			throw new BusinessException("msg.pkg.required");
		}
		pkgService.deactivate(pkgId, command);
		return this.ajaxDoneSuccess();
	}

	@RequiresPermissions(value = { "tms:package:assign" })
	@ResponseBody
	@RequestMapping(value = "/service/assign")
	@CsrfProtect
	public Map<String, Object> assign(Long[] pkgId, AssignPkgForm command) {
		if (ArrayUtils.isEmpty(pkgId)) {
			throw new BusinessException("msg.pkg.required");
		}
		if (ArrayUtils.isEmpty(command.getGroupIds())) {
			throw new BusinessException("msg.group.groupRequired");
		}
		pkgService.assign(pkgId, command);
		return this.ajaxDoneSuccess();
	}

	@ResponseBody
	@RequestMapping(value = "/service/getVersions")
	public List<String> getVersionsByName(String name, Long groupId) {
		if (StringUtils.isEmpty(name)) {
			return Collections.emptyList();
		}
		List<String> versionList = pkgService.getPkgVersionsByName(name, groupId);
		return versionList;
	}

	@ResponseBody
	@RequestMapping(value = "/service/getPkgId")
	public Object getPkgId(String pkgName, String pkgVersion) {
		if (StringUtils.isEmpty(pkgName)) {
			throw new BusinessException("msg.pkg.nameRequired");
		}
		if (StringUtils.isEmpty(pkgVersion)) {
			throw new BusinessException("msg.pkg.versionRequired");
		}
		Pkg pkg = pkgService.getPkgByNameAndVersion(pkgName, pkgVersion);
		Long pkgId = pkg.getId();
		return pkgId;
	}
	
	@ResponseBody
    @RequestMapping(value = "/service/check/history/packege")
	public Object checkHistoryPackage(){
	    return pkgService.checkHistoryPackage();
	}

}
