/*		
 * ============================================================================		
 *  COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description: CRUD users	
 * Revision History:		
 * Date	                 Author	                Action
 * 20170111  	         Alan Jiang           	Modify
 * ============================================================================		
 */
package com.pax.tms.user.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.ldaptive.LdapException;
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
import com.pax.tms.group.service.GroupService;
import com.pax.tms.location.service.CityService;
import com.pax.tms.location.service.CountryService;
import com.pax.tms.location.service.ProvinceService;
import com.pax.tms.user.model.User;
import com.pax.tms.user.security.LdapSearchExecutor;
import com.pax.tms.user.service.AppKeyService;
import com.pax.tms.user.service.UserRoleService;
import com.pax.tms.user.service.UserService;
import com.pax.tms.user.web.form.AddUserForm;
import com.pax.tms.user.web.form.ChangePasswordForm;
import com.pax.tms.user.web.form.EditUserForm;
import com.pax.tms.user.web.form.QueryUserForm;
import com.pax.tms.user.web.form.ResetPasswordForm;
import com.pax.tms.user.web.form.UserForm;

@Controller
@RequestMapping("/user")
public class UserController extends BaseController {

	private static final String MSG_USER_NOTFOUND = "msg.user.notFound";

	@Autowired
	private UserService userService;

	@Autowired
	private UserRoleService userRoleService;

	@Autowired
	private CountryService countryService;

	@Autowired
	private ProvinceService provinceService;

	@Autowired
	private CityService cityService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private AppKeyService appKeyService;

	@Autowired(required = false)
	LdapSearchExecutor ldapSearchExecutor;

	@RequiresPermissions(value = { "tms:user:view" })
	@RequestMapping("/list/{groupId}")
	public ModelAndView getUserList(@PathVariable Long groupId, BaseForm command) {
		ModelAndView mv = new ModelAndView("user/userList");
		groupService.checkPermissionOfGroup(command, groupId);
		mv.addObject("group", groupService.get(groupId));
		mv.addObject("activeUrl", User.USER_LIST_URL);
		mv.addObject("title", User.USER_TITLE);
		return mv;
	}

	@ResponseBody
	@RequestMapping(value = "/service/getRoleAndCount/{appName}/{groupId}")
	public Object getRoleAndCount(@PathVariable("appName") String appName, @PathVariable("groupId") Long groupId,
			BaseForm command) {
		Map<String, Object> mapJson = new HashMap<String, Object>();
		List<Map<String, Object>> roleInfo = userService.getRoleAndCount(appName, groupId, command);
		mapJson.put("statusCode", SUCCESS_STATUS_CODE);
		mapJson.put("items", roleInfo);
		mapJson.put("sum", userService.getUserCount(appName, groupId, command));
		return mapJson;
	}

	@RequiresPermissions(value = { "tms:user:add" })
	@RequestMapping("/toAddUser/{groupId}")
	public ModelAndView toAddUser(BaseForm command, @PathVariable Long groupId) {
		ModelAndView mv = new ModelAndView("user/addUser");
		groupService.checkPermissionOfGroup(command, groupId);
		mv.addObject("group", groupService.get(groupId));
		mv.addObject("countryList", countryService.getCounryList());
		mv.addObject("tmsRole", userService.findRole("TMS"));
		mv.addObject("PXRole", userService.findRole("PxDesigner"));
		mv.addObject("activeUrl", User.USER_LIST_URL);
		mv.addObject("title", User.USER_TITLE);
		return mv;
	}

	@RequiresPermissions(value = { "tms:user:add" })
	@RequestMapping("/toAddLdapUser/{groupId}")
	public ModelAndView toAddLdapUser(BaseForm command, @PathVariable Long groupId) {
		ModelAndView mv = new ModelAndView("user/addLdapUser");
		groupService.checkPermissionOfGroup(command, groupId);
		mv.addObject("group", groupService.get(groupId));
		mv.addObject("countryList", countryService.getCounryList());
		mv.addObject("tmsRole", userService.findRole("TMS"));
		mv.addObject("PXRole", userService.findRole("PxDesigner"));
		mv.addObject("activeUrl", User.USER_LIST_URL);
		mv.addObject("title", User.USER_TITLE);
		return mv;
	}

	@RequiresPermissions(value = { "tms:user:view" })
	@RequestMapping("/view/{userId}/{groupId}")
	public ModelAndView view(@PathVariable Long userId, @PathVariable Long groupId, BaseForm command) {
		ModelAndView mv = new ModelAndView("user/viewUserProfile");
		groupService.checkPermissionOfGroup(command, groupId);
		User user = userService.get(userId);
		if (user == null) {
			throw new BusinessException(MSG_USER_NOTFOUND);
		}
		if (user.isSiteAdmin(command.getLoginUserId())) {
			user.setAppKey(appKeyService.getUserAppKey(user.getUsername()));
		}

		mv.addObject("group", groupService.get(groupId));
		mv.addObject("user", user);
		mv.addObject("countryList", countryService.getCounryList());
		mv.addObject("provinceList", provinceService.list());
		mv.addObject("cityList", cityService.list());
		mv.addObject("tmsRoleList", userService.findRole("TMS"));
		mv.addObject("pxRoleList", userService.findRole("PxDesigner"));
		mv.addObject("selectedTMSRoleList", userRoleService.getAssignedRoleIds(userId, "TMS"));
		mv.addObject("selectedPXRoleList", userRoleService.getAssignedRoleIds(userId, "PxDesigner"));
		mv.addObject("activeUrl", User.USER_LIST_URL);
		mv.addObject("title", User.USER_TITLE);
		return mv;
	}

	@RequestMapping(value = "/forgetMyPassword", method = { RequestMethod.GET })
	public ModelAndView toForgetMyPassword() {
		ModelAndView mv = new ModelAndView("user/forgetMyPassword");
		return mv;
	}

	@RequestMapping(value = "/resetMyPassword", method = { RequestMethod.GET })
	public ModelAndView toResetMyPassword(BaseForm form) {
		ModelAndView mv = new ModelAndView("user/resetMyPassword");
		mv.addObject("tmsAddress", userService.getTMSCallbackUrl());
		mv.addObject("pxAddress", userService.getPxdesignerAddress());
		return mv;
	}

	@RequestMapping(value = "/changePassword", method = { RequestMethod.GET })
	public ModelAndView toChangeMyPassword() {
		ModelAndView mv = new ModelAndView("user/changePassword");
		mv.addObject("tmsAddress", userService.getTMSCallbackUrl());
		return mv;
	}

	/*** service ***/

	@RequiresPermissions(value = { "tms:user:view" })
	@RequestMapping("/service/list/{appName}/{groupId}")
	@ResponseBody
	public Page<Map<String, Object>> getUserListJSON(@PathVariable String appName, @PathVariable Long groupId,
			QueryUserForm command, HttpServletRequest request) {
		Long activeRoleId = null;
		if (!StringUtils.isEmpty(request.getParameter("activeRole"))) {
			activeRoleId = Long.parseLong(request.getParameter("activeRole"));
		}
		command.setActiveRoleId(activeRoleId);
		command.setAppName(appName);
		command.setGroupId(groupId);
		Page<Map<String, Object>> result = userService.page(command);
		userService.removeLoginUser(result,command);
		return result;
	}

	@RequiresPermissions(value = { "tms:user:add" })
	@ResponseBody
	@RequestMapping(value = "/service/addUser", method = { RequestMethod.POST })
	@CsrfProtect
	public Map<String, Object> addUser(@Valid AddUserForm command) {
		userService.add(command);
		return this.ajaxDoneSuccess();
	}

	@RequiresPermissions(value = { "tms:user:edit" })
	@ResponseBody
	@RequestMapping("/service/edit")
	@CsrfProtect
	public Map<String, Object> editUser(EditUserForm command) {
		userService.edit(command.getId(), command);
		return this.ajaxDoneSuccess();
	}

	@RequiresPermissions(value = { "tms:user:delete" })
	@ResponseBody
	@RequestMapping("/service/delete/{userId}/{groupId}")
	@CsrfProtect
	public Map<String, Object> deleteUser(@PathVariable Long[] userId, @PathVariable Long groupId, BaseForm command) {
		userService.delete(userId, groupId, command);
		return this.ajaxDoneSuccess();
	}

	@RequiresPermissions(value = { "tms:user:password:reset" })
	@ResponseBody
	@RequestMapping(value = "/service/resetPassword/{userId}/{groupId}")
	@CsrfProtect
	public Map<String, Object> resetPassword(@PathVariable long userId, @PathVariable Long groupId,
			@Valid ResetPasswordForm command) {
		groupService.checkPermissionOfGroup(command, groupId);
		User user = userService.get(userId);
		if (user == null) {
			throw new BusinessException(MSG_USER_NOTFOUND);
		}
		userService.resetPassword(userId, command);
		return this.ajaxDoneSuccess();
	}

	@RequiresPermissions(value = { "tms:user:deactive" })
	@ResponseBody
	@RequestMapping("/service/deactive/{userId}/{groupId}")
	@CsrfProtect
	public Map<String, Object> deactiveUser(@PathVariable Long userId, @PathVariable Long groupId, BaseForm command) {
		userService.deactive(userId, groupId, command);
		return this.ajaxDoneSuccess();
	}

	@RequiresPermissions(value = { "tms:user:active" })
	@ResponseBody
	@RequestMapping("/service/active/{userId}/{groupId}")
	@CsrfProtect
	public Map<String, Object> activeUser(@PathVariable Long userId, @PathVariable Long groupId, BaseForm command) {
		userService.active(userId, groupId, command);
		return this.ajaxDoneSuccess();
	}

	@ResponseBody
	@RequestMapping("/service/ldapAuth")
	@CsrfProtect
	public UserForm ldapAuthenticate(String ldapName) {
		if (ldapSearchExecutor == null) {
			throw new BusinessException("msg.user.unsupportLdapUser");
		}
		UserForm userForm;
		try {
			userForm = ldapSearchExecutor.getUserInfo(ldapName);
		} catch (LdapException e) {
			throw new BusinessException("msg.user.connectLdapFailed");
		}
		if (userForm == null) {
			throw new BusinessException("msg.user.authLdapUserFailed");
		}
		return userForm;
	}

	@ResponseBody
	@RequestMapping(value = "service/forgetMyPassword", method = { RequestMethod.POST })
	@CsrfProtect
	public Map<String, Object> forgetMyPassword(String username, String email, BaseForm command) {
		userService.forgetPassword(username, email, command);
		return this.ajaxDoneSuccess();
	}

	@ResponseBody
	@RequestMapping(value = "service/resetMyPassword", method = { RequestMethod.POST })
	@CsrfProtect
	public List<String> resetMyPassword(String username, String token, ResetPasswordForm command) {
		userService.setPasswordByToken(username, token, command);
		List<String> userRoles = userService.getUserRolesByUserName(username);
		return userRoles;
	}

	@ResponseBody
	@RequestMapping(value = "service/changePassword", method = { RequestMethod.POST })
	@CsrfProtect
	public Map<String, Object> changePassword(String username, String token, ChangePasswordForm command) {
		if (StringUtils.isEmpty(command.getOldPassword()) || StringUtils.isEmpty(command.getNewPassword())
				|| StringUtils.isEmpty(command.getConfirmNewPassword())) {
			throw new BusinessException("msg.user.passwordCannotEmpty");
		}
		userService.changePassword(username, token, command);
		return this.ajaxDoneSuccess();
	}
	
	@ResponseBody
    @RequestMapping(value = "service/generatePassword", method = { RequestMethod.POST })
    @CsrfProtect
    public Map<String, Object> generatePassword(String username, BaseForm command) {
        String password = userService.generatePassword(username, command);
        return this.ajaxDoneSuccess(password);
    }

}
