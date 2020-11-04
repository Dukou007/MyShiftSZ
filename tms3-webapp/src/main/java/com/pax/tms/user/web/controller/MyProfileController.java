/*		
 * ============================================================================		
 *  COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description: view、edit my profile,change my password
 * Revision History:		
 * Date	                 Author	                Action
 * 20170111  	         Alan Jiang           	Modify
 * ============================================================================		
 */
package com.pax.tms.user.web.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.pax.common.exception.BusinessException;
import com.pax.common.security.CsrfProtect;
import com.pax.common.web.controller.BaseController;
import com.pax.common.web.form.BaseForm;
import com.pax.tms.group.model.Group;
import com.pax.tms.group.service.GroupService;
import com.pax.tms.location.service.CityService;
import com.pax.tms.location.service.CountryService;
import com.pax.tms.location.service.ProvinceService;
import com.pax.tms.terminal.model.Terminal;
import com.pax.tms.terminal.service.TerminalService;
import com.pax.tms.user.model.User;
import com.pax.tms.user.security.AclManager;
import com.pax.tms.user.service.AuditLogService;
import com.pax.tms.user.service.UserLogService;
import com.pax.tms.user.service.UserRoleService;
import com.pax.tms.user.service.UserService;
import com.pax.tms.user.web.form.ChangePasswordForm;
import com.pax.tms.user.web.form.EditUserForm;
import com.pax.tms.web.component.PPMConfiguration;

@Controller
@RequestMapping("/myProfile")
public class MyProfileController extends BaseController {

	@Autowired
	private UserService userService;

	@Autowired
	private CountryService countryService;

	@Autowired
	private ProvinceService provinceService;

	@Autowired
	private UserLogService userlogService;

	@Autowired
	private AuditLogService auditLogService;

	@Autowired
	private CityService cityService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private UserRoleService userRoleService;

	@Autowired
	private TerminalService terminalService;

	@Autowired
	private PPMConfiguration config;

	private Long getLoggedInUserId(BaseForm form) {
		Long userId = form.getLoginUserId();
		if (userId == null) {
			throw new BusinessException("msg.user.notHasLoggedIn");
		}
		return userId;
	}

	@RequestMapping("/view/{groupId}")
	public ModelAndView viewMyProfile(BaseForm command, @PathVariable Long groupId) {
		Long userId = getLoggedInUserId(command);
		ModelAndView mv = new ModelAndView("user/viewMyProfile");
		User user = userService.get(userId);
		if (user == null) {
			throw new BusinessException("msg.user.notFound");
		}
		Group group = checkPermissionOfGroup(command, groupId);
		mv.addObject("group", group);
		mv.addObject("user", user);
		mv.addObject("countryList", countryService.getCounryList());
		mv.addObject("provinceList", provinceService.list());
		mv.addObject("selectedTMSRoleList", userRoleService.getAssignedRoleIds(userId, "TMS"));
		mv.addObject("selectedPXRoleList", userRoleService.getAssignedRoleIds(userId, "PxDesigner"));
		mv.addObject("cityList", cityService.list());
		mv.addObject("title", User.MY_PROFILE_TITLE);
		mv.addObject("activeUrl", User.MY_PROFILE_URL);
		return mv;
	}

	@RequestMapping("/view/{groupId}/{tsn}")
	public ModelAndView viewMyProfile(BaseForm command, @PathVariable Long groupId, @PathVariable String tsn) {
		Long userId = getLoggedInUserId(command);
		ModelAndView mv = new ModelAndView("user/viewMyProfile");
		User user = userService.get(userId);
		if (user == null) {
			throw new BusinessException("msg.user.notFound");
		}
		Group group = checkPermissionOfGroup(command, groupId);
		if (StringUtils.isEmpty(tsn)) {
			throw new BusinessException("tsn.Required");
		}
		Terminal terminal = terminalService.get(tsn);
		mv.addObject("terminal", terminal);
		mv.addObject("group", group);
		mv.addObject("user", user);
		mv.addObject("countryList", countryService.getCounryList());
		mv.addObject("provinceList", provinceService.list());
		mv.addObject("selectedTMSRoleList", userRoleService.getAssignedRoleIds(userId, "TMS"));
		mv.addObject("selectedPXRoleList", userRoleService.getAssignedRoleIds(userId, "PxDesigner"));
		mv.addObject("cityList", cityService.list());
		mv.addObject("title", User.MY_PROFILE_TITLE);
		mv.addObject("activeUrl", User.MY_PROFILE_URL);
		return mv;
	}

	@RequestMapping("/toChangePassword/{groupId}")
	public ModelAndView toChangeMyPassword(BaseForm command, @PathVariable Long groupId) {
		Long userId = command.getLoginUserId();
		User user = userService.get(userId);
		ModelAndView mv = new ModelAndView("user/changeMyPassword");
		Group group = checkPermissionOfGroup(command, groupId);
		mv.addObject("group", group);
		mv.addObject("user", user);
		mv.addObject("title", User.MY_PROFILE_TITLE);
		mv.addObject("activeUrl", User.CHG_PWD_URL);
		return mv;
	}

	@RequestMapping("/toChangePassword/{groupId}/{tsn}")
	public ModelAndView toChangeMyPassword(BaseForm form, @PathVariable Long groupId, @PathVariable String tsn) {
		Long userId = form.getLoginUserId();
		User user = userService.get(userId);
		ModelAndView mv = new ModelAndView("user/changeMyPassword");
		Group group = checkPermissionOfGroup(form, groupId);
		if (StringUtils.isEmpty(tsn)) {
			throw new BusinessException("tsn.Required");
		}
		Terminal terminal = terminalService.get(tsn);
		mv.addObject("terminal", terminal);
		mv.addObject("group", group);
		mv.addObject("user", user);
		mv.addObject("title", User.MY_PROFILE_TITLE);
		mv.addObject("activeUrl", User.CHG_PWD_URL);
		return mv;
	}

	/*** service ***/
	@ResponseBody
	@RequestMapping("/service/edit")
	@CsrfProtect
	public Map<String, Object> editMyProfile(@Valid EditUserForm command, BindingResult result) {
		getLoggedInUserId(command);
		userService.editMyProfile(command);
		return this.ajaxDoneSuccess();
	}

	@ResponseBody
	@RequestMapping("/service/changePassword")
	@CsrfProtect
	public Map<String, Object> changeMyPassword(ChangePasswordForm command) {
		getLoggedInUserId(command);
		if (StringUtils.isEmpty(command.getOldPassword()) || StringUtils.isEmpty(command.getNewPassword())
				|| StringUtils.isEmpty(command.getConfirmNewPassword())) {
			throw new BusinessException("msg.user.passwordCannotEmpty");
		}
		userService.changeMyPassword(command);
		return this.ajaxDoneSuccess();
	}

	@ResponseBody
	@RequestMapping("/service/logout")
	@CsrfProtect
	public void loginOut(HttpServletRequest request, HttpServletResponse response,BaseForm command) {
		Long userId = getLoggedInUserId(command);
		User user = userService.get(userId);
		userlogService.addUserLog(command, user, "Logout Successful");
		auditLogService.addAuditLog(Arrays.asList(""), command, "Logout Successful", null);
        try {
            // 这里并不是设置跳转页面，而是将重定向的地址发给前端，让前端执行重定向
            // 设置跳转地址
            response.setHeader("redirectUrl", getRedirctUrl());
            // 设置跳转使能
            response.setHeader("enableRedirect", "true");
            response.flushBuffer();
        } catch (IOException ex) {
           ex.printStackTrace();
        }
	}

	private String getRedirctUrl() {
		String tmsServerPrefix = config.getTmsServerPrefix();
		if (StringUtils.isEmpty(tmsServerPrefix)) {
			return "/logout";
		} else if (tmsServerPrefix.endsWith("/")) {
			return tmsServerPrefix + "logout";
		} else {
			return tmsServerPrefix + "/logout";
		}
	}

	public Group checkPermissionOfGroup(BaseForm command, Long groupId) {
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		Group group = groupService.get(groupId);
		if (group == null) {
			throw new BusinessException("msg.group.groupNotFound");
		}
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		return group;
	}


}
