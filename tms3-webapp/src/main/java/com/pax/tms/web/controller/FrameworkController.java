/*
 * ========================================================================
 * COPYRIGHT PAX TECHNOLOGY, INC. PROPRIETARY INFORMATION
 * This software is supplied under the terms of a license agreement or
 * nondisclosure agreement with PAX Technology, Inc. and may not be copied 
 * or disclosed except in accordance with the terms in that agreement.
 * Copyright (C) 2009-2015 PAX Technology, Inc. All rights reserved.
 * ========================================================================
 */

package com.pax.tms.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.ldaptive.LdapException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pax.common.exception.BusinessException;
import com.pax.common.web.controller.BaseController;
import com.pax.common.web.form.BaseForm;
import com.pax.http.UploadFileItem;
import com.pax.http.UploadFileUtils;
import com.pax.tms.group.model.Group;
import com.pax.tms.group.service.GroupService;
import com.pax.tms.monitor.model.GroupRealStatus;
import com.pax.tms.user.model.User;
import com.pax.tms.user.model.UserGroup;
import com.pax.tms.user.security.AclManager;
import com.pax.tms.user.service.UserGroupService;
import com.pax.tms.user.service.UserService;

@Controller
public class FrameworkController extends BaseController {

	@Autowired
	private UserGroupService userGroupService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private UserService userService;
	
	@RequestMapping(value = "/sessionExpired", method = { RequestMethod.GET, RequestMethod.POST })
	public String sessionExpired(Model model) {
		return "sessionExpired";
	}

	@RequestMapping(value = "/ajaxLoginDone", method = { RequestMethod.GET, RequestMethod.POST })
	public String ajaxLoginDone(Model model) {
		return "ajaxLoginDone";
	}

	@RequestMapping(value = "/ajaxLogoutDone", method = { RequestMethod.GET, RequestMethod.POST })
	public String ajaxLogoutDone(Model model) {
		return "ajaxLogoutDone";
	}

	@RequiresPermissions("tms:dashboard:view")
	@RequestMapping(value = "/index", method = { RequestMethod.GET })
	public String index(Model model, HttpServletRequest request, BaseForm command) throws LdapException {
		Group group = null;
		Long groupId = 0L;
		if (command.getGroupId() != null && command.getGroupId() > 0) {
			group = groupService.get(command.getGroupId());
		}
		if (group == null) {
			groupId = userGroupService.getFavoriteGroup(command.getLoginUserId());
			if (groupId == null) {
				User user = userService.get(command.getLoginUserId());
				if (user == null) {
					throw new BusinessException("msg.user.notFound");
				}
				List<UserGroup> userGroupList = user.getUserGroupList();
				if(!userGroupList.isEmpty() && 0 != userGroupList.size()){
					group = groupService.get(userGroupList.get(0).getGroupId());
				}else{
					group = null;
				}
				if (group == null) {
					throw new BusinessException("msg.group.groupNotFound");
				}
				for (UserGroup ug : userGroupList) {
					if (ug.getGroup().getTreeDepth() < group.getTreeDepth()) {
						group = ug.getGroup();
					}
				}
				// throw new BusinessException("msg.group.groupRequired");
			} else {
				group = groupService.get(groupId);
				if (group == null) {
					throw new BusinessException("msg.group.groupNotFound");
				}
			}

		}
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		model.addAttribute("group", group);
		model.addAttribute("activeUrl", GroupRealStatus.LIST_URL);
		model.addAttribute("title", GroupRealStatus.TITLE);
		userService.updateLoginLdapUserInfo();
		return "index";
		// return "redirect:dashboard/dashboard";
	}

	@RequiresPermissions("tms:dashboard:view")
	@RequestMapping(value = "/index/{groupId}", method = { RequestMethod.GET })
	public String indexActive(Model model, @PathVariable Long groupId, HttpServletRequest request, BaseForm command) {
		Group group = groupService.get(groupId);
		if (group == null) {
			throw new BusinessException("msg.group.groupNotFound");
		}
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		model.addAttribute("group", group);
		model.addAttribute("activeUrl", GroupRealStatus.LIST_URL);
		model.addAttribute("title", GroupRealStatus.TITLE);
		return "index";
		// return "redirect:dashboard/dashboard";
	}

	@RequestMapping(value = "/accessDenied")
	public String accessDenied(HttpServletRequest request) {
		throw new BusinessException("access.denied");
	}

	@RequestMapping("/management")
	public String mainPage(Model model, HttpServletRequest request, HttpServletResponse response) {
		return "/index";
	}

	@RequestMapping(value = "loadHome/{orgId}")
	public String loadHome(Model model, HttpServletRequest request, @PathVariable Long orgId) {
		// get the organization first, if the orgId is null get current
		// organization by default!

		return "index";
	}

	@ResponseBody
	@RequestMapping(value = "tools/service/upload", method = RequestMethod.PUT)
	public Map<String, Object> fileUpload(BaseForm command, HttpServletRequest request) throws FileUploadException {
		Map<String, Object> map = new HashMap<String, Object>();
		String user = command.getLoginUsername();
		String message = "success";
		UploadFileItem uploadFileItem = null;

		uploadFileItem = UploadFileUtils.storeUploadFile(request, user);

		map.put("message", message);
		map.put("filePath", uploadFileItem.getUrl());
		map.put("fileName", uploadFileItem.getFilename());
		return map;
	}

}
