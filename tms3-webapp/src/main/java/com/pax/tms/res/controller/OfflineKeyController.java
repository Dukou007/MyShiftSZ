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
 * 20200429  	         liming           	    Create OfflineKeyController
 * ============================================================================		
 */
package com.pax.tms.res.controller;

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
import com.pax.tms.res.service.OfflineKeyService;
import com.pax.tms.res.web.form.AddOfflineKeyForm;
import com.pax.tms.res.web.form.AssignPkgForm;
import com.pax.tms.res.web.form.EditPkgForm;
import com.pax.tms.res.web.form.QueryPkgForm;
import com.pax.tms.terminal.model.Terminal;
import com.pax.tms.terminal.service.TerminalService;
import com.pax.tms.user.security.AclManager;

@Controller
@RequestMapping(value = "/offlinekey")
public class OfflineKeyController extends BaseController {

	@Autowired
	private OfflineKeyService offlineKeyService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private TerminalService terminalService;

	// When user in Group Module, user only can view and assign package
	@RequiresPermissions(value = { "tms:offlinekey:view" })
	@RequestMapping(value = "/list/{groupId}")
	public ModelAndView listByGroup(@PathVariable Long groupId, BaseForm command) {
		ModelAndView mav = new ModelAndView("offlinekey/list_view");
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		Group group = groupService.get(groupId);
		if (group == null) {
			throw new BusinessException("msg.group.groupNotFound");
		}
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		mav.addObject("group", group);
		mav.addObject("activeUrl", Pkg.KEY_LIST_URL);
		mav.addObject("title", Pkg.KEY_TITLE);
		return mav;
	}

	// When user in Terminal Module, user only can view and assign package
	@RequiresPermissions(value = { "tms:offlinekey:view" })
	@RequestMapping(value = "/list/{groupId}/{tsn}")
	public ModelAndView listByTsn(@PathVariable Long groupId, @PathVariable String tsn, BaseForm command) {
		ModelAndView mav = new ModelAndView("offlinekey/list_view");
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
		mav.addObject("activeUrl", Pkg.KEY_LIST_URL);
		mav.addObject("title", Pkg.KEY_TITLE);
		return mav;
	}

	@RequiresPermissions(value = { "tms:offlinekey:view" })
	@ResponseBody
	@RequestMapping(value = "/service/ajaxList/{groupId}")
	public Page<Map<String, Object>> getOfflineKeyList(QueryPkgForm command, @PathVariable Long groupId) {
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		command.setGroupId(groupId);
		Page<Map<String, Object>> page = offlineKeyService.page(command);
		return page;
	}
	
	@RequiresPermissions(value = { "tms:offlinekey:view" })
	@ResponseBody
	@RequestMapping(value = "/service/ajaxListByName")
	public List<Map<String, Object>> getOfflineKeyListByName(String name, Long groupId) {
		return offlineKeyService.getOfflineKeyListByName(name, groupId);
	}

	@RequiresPermissions(value = { "tms:offlinekey:import" })
	@RequestMapping(value = "/toImport/{groupId}")
	public ModelAndView toImport(@PathVariable Long groupId, BaseForm command) {
		ModelAndView mav = new ModelAndView("offlinekey/import");
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		Group group = groupService.get(groupId);
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		if (group == null) {
			throw new BusinessException("msg.group.groupNotFound");
		}
		mav.addObject("group", group);
		mav.addObject("activeUrl", "/offlinekey/toImport/");
		return mav;
	}
	
	@RequiresPermissions(value = { "tms:offlinekey:import" })
    @RequestMapping(value = "/toImport/{groupId}/{tsn}")
    public ModelAndView toImport(@PathVariable Long groupId, @PathVariable String tsn, BaseForm command) {
        ModelAndView mav = new ModelAndView("offlinekey/import");
        if (groupId == null) {
            throw new BusinessException("msg.group.groupRequired");
        }
        Group group = groupService.get(groupId);
        AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
        if (group == null) {
            throw new BusinessException("msg.group.groupNotFound");
        }
        if (StringUtils.isEmpty(tsn)) {
            throw new BusinessException("tsn.Required");
        }
        Terminal terminal = terminalService.get(tsn);
        if (terminal == null) {
            throw new BusinessException("tsn.notFound", new String[] { tsn });
        }
        mav.addObject("terminal", terminal);
        mav.addObject("group", group);
        mav.addObject("activeUrl", "/offlinekey/toImport/");
        return mav;
    }

	@RequiresPermissions(value = { "tms:offlinekey:import" })
	@ResponseBody
	@RequestMapping(value = "/service/import")
	@CsrfProtect
	public Map<String, Object> importKey(AddOfflineKeyForm command) {
	    return offlineKeyService.save(command);
	}


	// When user in Group Module, user only can view package
	@RequiresPermissions(value = { "tms:offlinekey:view" })
	@RequestMapping(value = "/profileView/{groupId}/{offlineKeyId}")
	public ModelAndView profileByGroup(@PathVariable Long groupId, @PathVariable Long offlineKeyId, String type,
			BaseForm command) {
		ModelAndView mav = new ModelAndView("offlinekey/profile_view");
		if (offlineKeyId == null) {
			throw new BusinessException("msg.offlineKey.required");
		}
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		Group group = groupService.get(groupId);
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		if (group == null) {
			throw new BusinessException("msg.group.groupNotFound");
		}
		Pkg offlineKey = offlineKeyService.get(offlineKeyId);
		List<String> groupNames = offlineKeyService.getGroupNames(offlineKeyId);
		if (StringUtils.equals(type, "1")) {
			mav.addObject("activeUrl", Pkg.KEY_LIST_URL);
		} else {
			mav.addObject("activeUrl", GroupDeploy.KEY_LIST_URL);
		}
		mav.addObject("groupNames", groupNames);
		mav.addObject("group", group);
		mav.addObject("offlinekey", offlineKey);
		mav.addObject("title", Pkg.KEY_TITLE);
		return mav;
	}

	// When user in Terminal Module, user only can view package
	@RequiresPermissions(value = { "tms:offlinekey:view" })
	@RequestMapping(value = "/profileView/{groupId}/{tsn}/{offlineKeyId}")
	public ModelAndView profileByTsn(@PathVariable Long groupId, @PathVariable Long offlineKeyId, @PathVariable String tsn,
			String type, BaseForm command) {
		ModelAndView mav = new ModelAndView("offlinekey/profile_view");
		if (offlineKeyId == null) {
			throw new BusinessException("msg.offlineKey.required");
		}
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		Group group = groupService.get(groupId);
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		if (group == null) {
			throw new BusinessException("msg.group.groupNotFound");
		}
		Pkg offlineKey = offlineKeyService.get(offlineKeyId);
		List<String> groupNames = offlineKeyService.getGroupNames(offlineKeyId);
		if (StringUtils.isEmpty(tsn)) {
			throw new BusinessException("tsn.Required");
		}
		Terminal terminal = terminalService.get(tsn);
		if (terminal == null) {
			throw new BusinessException("tsn.notFound", new String[] { tsn });
		}
		if (StringUtils.equals(type, "1")) {
			mav.addObject("activeUrl", Pkg.KEY_LIST_URL);
		} else {
			mav.addObject("activeUrl", TerminalDeploy.KEY_LIST_URL);
		}
		mav.addObject("terminal", terminal);
		mav.addObject("groupNames", groupNames);
		mav.addObject("group", group);
		mav.addObject("offlinekey", offlineKey);
		mav.addObject("title", Pkg.KEY_TITLE);
		return mav;
	}

	@RequiresPermissions(value = { "tms:offlinekey:edit" })
	@ResponseBody
	@RequestMapping(value = "/service/edit/{offlineKeyId}")
	@CsrfProtect
	public Map<String, Object> edit(EditPkgForm command, @PathVariable Long offlineKeyId) {
		if (offlineKeyId == null) {
			throw new BusinessException("msg.offlineKey.required");
		}
		offlineKeyService.edit(offlineKeyId, command);
		return this.ajaxDoneSuccess();
	}

	@RequiresPermissions(value = { "tms:offlinekey:remove" })
	@ResponseBody
	@RequestMapping(value = "/service/dismiss")
	@CsrfProtect
	public Map<String, Object> dismiss(Long[] offlineKeyId, BaseForm command) {
		if (ArrayUtils.isEmpty(offlineKeyId)) {
			throw new BusinessException("msg.offlineKey.required");
		}
		List<Long> list = Arrays.asList(offlineKeyId);
		List<Long> offlineKeyList = new ArrayList<>();
		offlineKeyList.addAll(list);
		Long groupId = command.getGroupId();
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}

		if (groupService.get(groupId).isEnterPriceGroup()) {
			offlineKeyService.delete(offlineKeyList, command);
		} else {
			offlineKeyService.dismiss(offlineKeyList, command);
		}
		return this.ajaxDoneSuccess();
	}

	@RequiresPermissions(value = { "tms:offlinekey:delete" })
	@ResponseBody
	@RequestMapping(value = "/service/delete")
	@CsrfProtect
	public Map<String, Object> delete(Long[] offlineKeyId, BaseForm command) {
		if (ArrayUtils.isEmpty(offlineKeyId)) {
			throw new BusinessException("msg.offlineKey.required");
		}
		List<Long> list = Arrays.asList(offlineKeyId);
		List<Long> offlineKeyList = new ArrayList<>();
		offlineKeyList.addAll(list);
		Long groupId = command.getGroupId();
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		offlineKeyService.delete(offlineKeyList, command);
		return this.ajaxDoneSuccess();
	}


	@RequiresPermissions(value = { "tms:offlinekey:assign" })
	@ResponseBody
	@RequestMapping(value = "/service/assign")
	@CsrfProtect
	public Map<String, Object> assign(Long[] offlineKeyId, AssignPkgForm command) {
		if (ArrayUtils.isEmpty(offlineKeyId)) {
			throw new BusinessException("msg.offlineKey.required");
		}
		if (ArrayUtils.isEmpty(command.getGroupIds())) {
			throw new BusinessException("msg.group.groupRequired");
		}
		offlineKeyService.assign(offlineKeyId, command);
		return this.ajaxDoneSuccess();
	}

	@ResponseBody
	@RequestMapping(value = "/service/getVersions")
	public List<String> getVersionsByName(String name, Long groupId) {
		if (StringUtils.isEmpty(name)) {
			return Collections.emptyList();
		}
		List<String> versionList = offlineKeyService.getOfflineKeyVersionsByName(name, groupId);
		return versionList;
	}

	@ResponseBody
	@RequestMapping(value = "/service/getPkgId")
	public Object getPkgId(String offlineKeyName, String offlineKeyVersion) {
		if (StringUtils.isEmpty(offlineKeyName)) {
			throw new BusinessException("msg.offlineKey.nameRequired");
		}
		if (StringUtils.isEmpty(offlineKeyVersion)) {
			throw new BusinessException("msg.offlineKey.versionRequired");
		}
		Pkg offlineKey = offlineKeyService.getOfflineKeyByNameAndVersion(offlineKeyName, offlineKeyVersion);
		Long offlineKeyId = offlineKey.getId();
		return offlineKeyId;
	}
	
}
