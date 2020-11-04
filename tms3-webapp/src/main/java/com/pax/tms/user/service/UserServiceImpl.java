/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) YYYY-? PAX Technology, Inc. All rights reserved.		
 * Description: add/edit/delete/active/inactive/reset password/forget password user 		
 * Revision History:		
 * Date	        Author	                          Action
 * 20170111     Carry     add/edit/delete/active/inactive/reset password/forget password user 
 * ============================================================================		
 */
package com.pax.tms.user.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.jasig.cas.client.util.CommonUtils;
import org.ldaptive.LdapException;
import org.pac4j.core.profile.CommonProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pax.common.dao.IBaseDao;
import com.pax.common.exception.BusinessException;
import com.pax.common.pagination.IPageForm;
import com.pax.common.pagination.Page;
import com.pax.common.redis.RedisClient;
import com.pax.common.security.HashedCredentials;
import com.pax.common.security.HashedCredentials.PasswordInfo;
import com.pax.common.service.impl.BaseService;
import com.pax.common.util.DateTimeUtils;
import com.pax.common.util.RegexMatchUtils;
import com.pax.common.util.StringHelper;
import com.pax.common.web.form.BaseForm;
import com.pax.tms.group.model.Group;
import com.pax.tms.group.service.GroupService;
import com.pax.tms.location.service.AddressInfo;
import com.pax.tms.location.service.AddressService;
import com.pax.tms.monitor.service.AlertConditionService;
import com.pax.tms.monitor.web.AlertConstants;
import com.pax.tms.user.CreateUserMailSender;
import com.pax.tms.user.ForgetPasswordMailSender;
import com.pax.tms.user.ResetPasswordMailSender;
import com.pax.tms.user.dao.PasswordHistoryDao;
import com.pax.tms.user.dao.PasswordResetTokenDao;
import com.pax.tms.user.dao.UserDao;
import com.pax.tms.user.dao.UserRoleDao;
import com.pax.tms.user.model.PasswordHistory;
import com.pax.tms.user.model.PasswordResetToken;
import com.pax.tms.user.model.Role;
import com.pax.tms.user.model.User;
import com.pax.tms.user.model.UserRole;
import com.pax.tms.user.security.AclManager;
import com.pax.tms.user.security.LdapSearchExecutor;
import com.pax.tms.user.web.form.AddUserForm;
import com.pax.tms.user.web.form.ChangePasswordForm;
import com.pax.tms.user.web.form.EditUserForm;
import com.pax.tms.user.web.form.OperatorLogForm;
import com.pax.tms.user.web.form.QueryUserForm;
import com.pax.tms.user.web.form.ResetPasswordForm;
import com.pax.tms.user.web.form.UserForm;

@Transactional
@Service("userServiceImpl")
public class UserServiceImpl extends BaseService<User, Long> implements UserService {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	private UserDao userDao;

	@Autowired
	private PasswordResetTokenDao pwdTokenDao;

	@Autowired
	private PasswordHistoryDao passwordHistoryDao;
	
	@Autowired
	private UserRoleDao userRoleDao;

	@Autowired
	private UserLogService userlogService;

	@Autowired
	private UserGroupService userGroupService;

	@Autowired
	private UserRoleService userRoleService;

	@Autowired
	private AddressService addressService;

	@Autowired(required = false)
	private ResetPasswordMailSender resetPasswordMailSender;

	@Autowired(required = false)
	private ForgetPasswordMailSender forgetPasswordMailSender;

	@Autowired(required = false)
	private CreateUserMailSender createUserMailSender;

	@Autowired(required = false)
	private LdapSearchExecutor ldapSearchExecutor;

	@Autowired(required = false)
	private PasswordPolicy passwordPolicy;

	@Autowired
	private AuditLogService auditLogService;

	@Autowired
	private AlertConditionService alertService;

	@Autowired
	private ACLTUserGroupService aclTUserGroupService;

	@Autowired
	private GroupService groupService;

	private int resetTokenValidHours = 24 * 7;
	
	private Long entitiesID = 1l;
	
	private Long siteAdminID = 1l;

	@Autowired(required = false)
	private RedisClient redisClient;
	
	private String emailSendLimit = "emailSendLimit_";
    private String allEmailSendLimit = "allEmailSendLimit_";

	@Autowired
	private AppKeyService appKeyService;

	@Override
	public IBaseDao<User, Long> getBaseDao() {
		return userDao;
	}

	@Override
	public User findByUsername(String username) {
		return userDao.findByUsername(username);
	}

	@Override
	public <E, S extends IPageForm> Page<E> page(S command) {
		QueryUserForm form = (QueryUserForm) command;
		// check group permission
		groupService.checkPermissionOfGroup(form, form.getGroupId());
		return super.page(form);
	}

	@Override
	public void add(AddUserForm command) {
		// check group permission
		groupService.checkPermissionOfGroup(command, command.getGroupId());

		/*** input validation ***/
		if (StringUtils.isEmpty(command.getUsername())) {
			throw new BusinessException("msg.user.usernameRequired");
		}

		// check user name unique
		if (userDao.findByUsername(command.getUsername()) != null) {
			throw new BusinessException("msg.user.usernameExisted");
		}

		List<Role> roleList = validateRoles(command.getRoleIds());

		List<Group> groupList = userGroupService.validateUserGroups(command.getGroupIds());

		// check group and role validation
		checkGroupAndRole(command, groupList);

		AclManager.checkPermissionOfGroup(command.getLoginUserId(), groupList);
		User user = new User();

		// add user info
		if (command.isLdap()) {
			setLdapUserInfo(user, command.getUsername());
		} else {
			checkInputLegitimacy(command);
			user.setDirectory(User.DIRECTORY_TMS);
			user.setForceChgPwd(true);
			setUserInfo(command, user);
		}
		user.setEnableDate(command.getRequestTime());
		user.setAccExpDate(command.getExpirationDate());
		user.setCreator(command.getLoginUsername());
		user.setCreateDate(command.getRequestTime());
		user.setModifier(command.getLoginUsername());
		user.setModifyDate(command.getRequestTime());

		addRealTimeAndUsageInfo(user);
		userDao.save(user);

		// add role and group info to user
		userRoleService.addUserRoles(user, roleList);
		userGroupService.addUserGroups(user, groupList);
		userDao.flush();
		for (Long groupId : command.getGroupIds()) {
			aclTUserGroupService.saveACLUserGroups(user.getId(), groupId);
		}

		userDao.flush();
		// send mail
		if (!user.isLdap()) {
			PasswordResetToken pwdToken = savePasswordResetToken(user.getId());
			if (createUserMailSender != null && !StringUtils.isEmpty(user.getEmail())) {
				try {
					createUserMailSender.sendWelcomeEmail(user, pwdToken);
				} catch (Exception e) {
					LOGGER.warn("Send welcome mail to " + user.getUsername() + " failed", e);
				}
			}
		}

		String groupNamePath = userGroupService.getLogGroup(user.getId());

		// add audit log and user log
		if (command.isLdap()) {
			userlogService.addUserLog(command, user,
					"Add LDAP User " + user.getUsername() + ", Assign Group " + groupNamePath);
			auditLogService.addAuditLog(Arrays.asList(user.getUsername()), command, OperatorLogForm.ADD_LDAP_USER,
					", Assign Group " + groupNamePath);
		} else {
			userlogService.addUserLog(command, user,
					"Add Local User " + user.getUsername() + ", Assign Group " + groupNamePath);
			auditLogService.addAuditLog(Arrays.asList(user.getUsername()), command, OperatorLogForm.ADD_LOCAL_USER,
					", Assign Group " + groupNamePath);
		}

	}

	private void addRealTimeAndUsageInfo(User user) {
		String realTime = "";
		int realCount = AlertConstants.getRealItemsCount();
		int noShowRealCount = AlertConstants.getNoShowRealItemsCount();
		for (int i = 0; i < realCount; i++) {
			if (i < realCount - 1) {
				realTime += AlertConstants.getRealItems()[i] + ":1,";
			} else {
				realTime += AlertConstants.getRealItems()[i] + ":1";
			}
		}
		if (noShowRealCount != 0) {
			for (int i = 0; i < noShowRealCount; i++) {
				if (realTime.contains(AlertConstants.getNoShowRealItems()[i])) {
					realTime = realTime.replace(AlertConstants.getNoShowRealItems()[i]+":1", AlertConstants.getNoShowRealItems()[i]+":0");
				}
			}
		}
		user.setRealTime(realTime);
		String usage = "";
		int usageCount = AlertConstants.getUsageItemsCount();
		for (int i = 0; i < usageCount; i++) {
			if (i < usageCount - 1) {
				usage += AlertConstants.getUsageItems()[i] + ":1,";
			} else {
				usage += AlertConstants.getUsageItems()[i] + ":1";
			}
		}
		user.setUsage(usage);
	}

	private PasswordResetToken savePasswordResetToken(Long userId) {
		PasswordResetToken pwdToken = new PasswordResetToken();
		String resetToken = UUID.randomUUID().toString();
		pwdToken.setUserId(userId);
		pwdToken.setResetPwdToken(resetToken);
		pwdToken.setTokenIssueDate(new Date());
		pwdToken.save();
		return pwdToken;
	}

	private void checkInputLegitimacy(UserForm command) {
		if (RegexMatchUtils.contains(command.getUsername(), "[/\"<>{}()=]|\\$$")
				|| command.getUsername().length() > 36) {
			throw new BusinessException("msg.user.illegalUsername");
		}
		/*** input validation ***/
		if (StringUtils.isEmpty(command.getFullname())) {
			throw new BusinessException("msg.user.fullUserNameRequired");
		}
		if (StringUtils.isEmpty(command.getPhone())) {
			throw new BusinessException("msg.user.phoneRequired");
		}
		if (StringUtils.isEmpty(command.getEmail())) {
			throw new BusinessException("msg.user.emailRequired");
		}
		if (RegexMatchUtils.contains(command.getFullname(), "[/\"<>{}()=]|\\$$")
				|| command.getFullname().length() > 128) {
			throw new BusinessException("msg.user.illegalFullname");
		}
		if (!RegexMatchUtils.contains(command.getPhone(), "^(\\+)?(\\d|-|\\s)*$") || command.getPhone().length() > 60) {
			throw new BusinessException("msg.user.illegalPhone");
		}
		if (!RegexMatchUtils.contains(command.getEmail(), "(\\w)+(\\.\\w+)*@(\\w)+((\\.\\w+)+)")
				|| command.getEmail().length() > 128) {
			throw new BusinessException("msg.user.illegalEmail");
		}

	}

	public List<Role> validateRoles(Long[] roleIds) {
		if (roleIds == null || roleIds.length == 0) {
			throw new BusinessException("Role can not be empty");
		}

		Set<Long> roleIdSet = new HashSet<Long>(Arrays.asList(roleIds));
		List<Role> roleList = new ArrayList<Role>(roleIdSet.size());

		for (Long roleId : roleIdSet) {
			roleList.add(validateRole(roleId));
		}
		return roleList;
	}

	public Role validateRole(long roleId) {
		Role role = userDao.getRole(roleId);
		if (role == null) {
			throw new BusinessException("msg.role.notFound", new String[] { String.valueOf(roleId) });
		}
		return role;
	}

	private void setLdapUserInfo(User user, String username) {
		user.setDirectory(User.DIRECTORY_LDAP);
		user.setLdap(true);

		if (ldapSearchExecutor == null) {
			throw new BusinessException("msg.user.unsupportLdapUser");
		}

		UserForm userForm = null;
		try {
			userForm = ldapSearchExecutor.getUserInfo(username);
		} catch (LdapException e) {
			throw new BusinessException("msg.user.authLdapUserFailed", e);
		}

		if (userForm == null) {
			throw new BusinessException("msg.user.authLdapUserFailed");
		}

		setUserInfo(userForm, user);
	}

	private void setTmsUserPassword(String password, User user) {
		PasswordInfo pwdInfo = HashedCredentials.digestEncodedPassword(password);
		user.setPassword(pwdInfo.getEncodedPassword());
		user.setPwdEncAlg(pwdInfo.getAlgorithmName());
		user.setPwdEncSalt(pwdInfo.getDynaSalt());
		user.setPwdEncIt(pwdInfo.getNumberOfIterations());
		user.setPwdChgDate(new Date());
	}

	private void setUserInfo(UserForm command, User user) {

		if (StringUtils.isEmpty(command.getUsername())) {
			throw new BusinessException("msg.user.usernameRequired");
		}
		// check user name unique
		if (userDao.findByUsername(command.getUsername()) != null
				&& !user.getUsername().equals(command.getUsername())) {
			throw new BusinessException("msg.user.usernameExisted");
		}
		if (user.isLdap()) {
			user.setCountry(command.getCountryName());
			user.setProvince(command.getProvinceName());
			user.setCity(command.getCityName());
			user.setAddress(command.getAddress());
			user.setZipCode(command.getZipCode());

		} else if(!user.isLdap() && (null != command.getCountryId() && 0l != command.getCountryId())) {
			AddressInfo addressInfo = addressService.checkAddress(command);
			user.setAddressInfo(addressInfo);
		} else {
			user.setCountry(command.getCountryName());
			user.setProvince(command.getProvinceName());
			user.setCity(command.getCityName());
			user.setAddress(command.getAddress());
			user.setZipCode(command.getZipCode());
		}
		
		user.setUsername(command.getUsername());
		user.setFullname(command.getFullname());
		user.setEmail(command.getEmail());
		user.setPhone(command.getPhone());

		user.setDescription(command.getDescription());
	}

	@Override
	public void edit(Long userId, EditUserForm command) {
		// check site administrator
		UserRole siteAdmin = userRoleDao.getUserRole(userId, null, siteAdminID);
		checkSiteAdmin(siteAdmin, command);
		// check group permission
		groupService.checkPermissionOfGroup(command, command.getGroupId());
		User user = validateUser(userId);
		AclManager.checkPermissionOfUser(userId, user);
//		if (user.isSys()) {
//			throw new BusinessException("msg.user.cannotEditSystemUser");
//		}
		List<Long> groupIdsOld = userGroupService.getGroupIdsOld(userId);

		List<Role> roleList = validateRoles(command.getRoleIds());
		List<Group> groupList = userGroupService.validateUserGroups(command.getGroupIds());
		if( (null != siteAdmin) && (!siteAdminID.equals(siteAdmin.getRoleId())) ){
			checkGroupAndRole(command, groupList);
		}
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), groupList);

		editUserInfo(user, command);
		user.setAccExpDate(command.getExpirationDate());
		user.setModifier(command.getLoginUsername());
		user.setModifyDate(new Date());
		if (!user.isSys()){
		    //非系统用户才能修改角色、组
		    userRoleService.editUserRoles(user, roleList);
	        userGroupService.editUserGroups(user, groupList);
	        aclTUserGroupService.deleteACLUserGroups(user.getId());
	        for (Long groupId : command.getGroupIds()) {
	            aclTUserGroupService.saveACLUserGroups(user.getId(), groupId);
	        }
		}

		List<Long> groupIdsNew = Arrays.asList(command.getGroupIds());
		List<Long> addNameId = new ArrayList<Long>(groupIdsNew);
		List<Long> removeNameId = new ArrayList<Long>(groupIdsOld);
		addNameId.removeAll(groupIdsOld);
		removeNameId.removeAll(groupIdsNew);
		String addGroupName = null;
		String removeGroupName = null;

		// edit user: remove exists group and add new group
		if (!addNameId.isEmpty() && !removeNameId.isEmpty()) {
			addGroupName = userGroupService.getGroupNames(addNameId);
			removeGroupName = userGroupService.getGroupNames(removeNameId);
			// add user log and audit log
			userlogService.addUserLog(command, user, "Edit User " + user.getUsername() + ", Remove Group "
					+ removeGroupName + ",  Assign Group " + addGroupName);
			auditLogService.addAuditLog(Arrays.asList(user.getUsername()), command, OperatorLogForm.EDIT_USER,
					", Remove Group " + removeGroupName + ", Assign Group " + addGroupName);
			for (Long groupId : removeNameId) {
				alertService.deleteAlertOffInfo(userId, groupId);
			}
			// edit user:remove exists group
		} else if (addNameId.isEmpty() && !removeNameId.isEmpty()) {
			removeGroupName = userGroupService.getGroupNames(removeNameId);
			// add user log and audit log
			userlogService.addUserLog(command, user,
					"Edit User " + user.getUsername() + ", Remove Group " + removeGroupName);
			auditLogService.addAuditLog(Arrays.asList(user.getUsername()), command, OperatorLogForm.EDIT_USER,
					", Remove Group " + removeGroupName);
			for (Long groupId : removeNameId) {
				alertService.deleteAlertOffInfo(userId, groupId);
			}
			// edit user:add new group
		} else if (!addNameId.isEmpty() && removeNameId.isEmpty()) {
			addGroupName = userGroupService.getGroupNames(addNameId);
			// add user log and audit log
			userlogService.addUserLog(command, user,
					"Edit User " + user.getUsername() + ", Assign Group " + addGroupName);
			auditLogService.addAuditLog(Arrays.asList(user.getUsername()), command, OperatorLogForm.EDIT_USER,
					", Assign Group:" + addGroupName);

			// edit user:no change with group
		} else if (addNameId.isEmpty() && removeNameId.isEmpty()) {
			// add user log and audit log
			userlogService.addUserLog(command, user, "Edit User " + user.getUsername());
			auditLogService.addAuditLog(Arrays.asList(user.getUsername()), command, OperatorLogForm.EDIT_USER, null);
		}

		this.sendUserStatusChangedMessage(userId, user.getUsername(), "edit");
	}

	private void editUserInfo(User user, UserForm command) {
		if (user.isLdap()) {
			try {
				setLdapUserInfo(user, command.getUsername());
			} catch (Exception e) {
				LOGGER.warn("LDAP search failed", e);
			}
		} else {
			checkInputLegitimacy(command);
			setUserInfo(command, user);
		}
	}
	
	private void checkSiteAdmin(UserRole userRole,EditUserForm command){
		if(null == userRole){
			return;
		}
		if(siteAdminID.intValue() == userRole.getRoleId().intValue()){
			//如果为空则初始化
			if(null == command.getGroupIds() || 0 == command.getGroupIds().length)
			{
				Long[] groupIds = {1l};
				command.setGroupIds(groupIds);
			}
			if(null == command.getRoleIds() || 0 == command.getRoleIds().length)
			{
				Long[] roleIds = {1l};
				command.setRoleIds(roleIds);
			}
			//分组不是Entities
			for(Long g:command.getGroupIds())
			{
				if(!entitiesID.equals(g)){
					throw new BusinessException("msg.user.siteAdminOnlyBelongEnterprise");
				}
			}
			//如果role不是1
			for(Long roleId:command.getRoleIds())
			{
				if(!siteAdminID.equals(roleId)){
					throw new BusinessException("msg.user.siteAdminRoleCannotModify");
				}
			}
		}
	}

	private void checkGroupAndRole(UserForm command, List<Group> groupList) {
		for (Group group : groupList) {
			if (group.getTreeDepth() == 0) {
				throw new BusinessException("msg.user.cannotAssignRootGroup");
			}
		}
		if (ArrayUtils.contains(command.getRoleIds(), 2L)) {
			for (Group group : groupList) {
				if (group.getTreeDepth() != 1) {
					throw new BusinessException("msg.user.adminOnlyBelongToEnterprise");
				}
			}
			int enterPriceCount = 0;
			for (Group group : groupList) {
				if (group.isEnterPriceGroup()) {
					enterPriceCount++;
				}
				if (enterPriceCount > 1) {
					throw new BusinessException("msg.user.UniqueEnterprise");
				}
			}
		}
	}

	@Override
	public void editMyProfile(EditUserForm command) {
		// check group permission
		groupService.checkPermissionOfGroup(command, command.getGroupId());
		Long userId = command.getLoginUserId();
		if (userId == null) {
			return;
		}

		User user = validateUser(userId);
		if (command.getFavoriteGroupId() != null) {
			userGroupService.setFavoriteGroup(userId, command.getFavoriteGroupId());
		}

		editUserInfo(user, command);
		user.setModifier(command.getLoginUsername());
		user.setModifyDate(new Date());

		// add user log and audit log
		userlogService.addUserLog(command, user, "Edit my profile");
		auditLogService.addAuditLog(Arrays.asList(""), command, OperatorLogForm.EDIT_USER_PROFILE, " profile");

		//刷新fullname
		Subject subject = SecurityUtils.getSubject();
		PrincipalCollection principalCollection = subject.getPrincipals();
	    Object principal = subject.getPrincipal();
	    if (principal instanceof io.buji.pac4j.subject.Pac4jPrincipal) {
	          CommonProfile profile = ((io.buji.pac4j.subject.Pac4jPrincipal) principal).getProfile();
	          profile.addAttribute("displayName", user.getFullname()+" ("+user.getUsername()+")");
	          profile.addAttribute("loginUsername", user.getUsername());
	     } else if (principal instanceof com.pax.login.TmsPac4jPrincipal) {
	          CommonProfile profile = ((com.pax.login.TmsPac4jPrincipal) principal).getProfile();
	          profile.addAttribute("displayName", user.getFullname()+" ("+user.getUsername()+")");
	          profile.addAttribute("loginUsername", user.getUsername());
	    }
	    String realmName = principalCollection.getRealmNames().iterator().next();
	    PrincipalCollection newPrincipalCollection = new SimplePrincipalCollection(principal, realmName);
	    subject.runAs(newPrincipalCollection);
	}

	@Override
	public void active(Long id, Long groupId, BaseForm command) {
		// check group permission
		groupService.checkPermissionOfGroup(command, groupId);
		User user = validateUser(id);
		AclManager.checkPermissionOfUser(id, user);
		user.setEnabled(true);
		user.setEnableDate(new Date());
		user.setLocked(false);
		user.setPasswordErrorCount(0);
		user.setModifier(command.getLoginUsername());
		user.setModifyDate(new Date());
		// add user log and audit log
		addUserLogInfo1(command, user, "Activate User ");
		auditLogService.addAuditLog(Arrays.asList(user.getUsername()), command, OperatorLogForm.ACTIVE_USER, null);
	}

	private void addUserLogInfo1(BaseForm command, User user, String appName) {
		userlogService.addUserLog(command, user, appName + user.getUsername());
	}

	@Override
	public void deactive(Long id, Long groupId, BaseForm command) {
		// check group permission
		groupService.checkPermissionOfGroup(command, groupId);
		User user = validateUser(id);
		AclManager.checkPermissionOfUser(id, user);

		user.setEnabled(false);
		user.setModifier(command.getLoginUsername());
		user.setModifyDate(new Date());
		// add user log and audit log
		addUserLogInfo1(command, user, "Deactivate User ");
		auditLogService.addAuditLog(Arrays.asList(user.getUsername()), command, OperatorLogForm.DEACTIVE_USER, null);
		sendUserStatusChangedMessage(user.getId(), user.getUsername(), "deactive");
	}

	@Override
	public void delete(Long[] idList, Long groupId, BaseForm command) {
		// check group permission
		groupService.checkPermissionOfGroup(command, groupId);
		if (idList != null && idList.length > 0) {
			for (Long id : idList) {
				delete(id, command);
			}
		}
	}

	public void delete(Long id, BaseForm command) {
		User user = validateUser(id);
		AclManager.checkPermissionOfUser(command.getLoginUserId(), user);

		if (user.isSys()) {
			throw new BusinessException("msg.user.cannotDeleteSystemUser");
		}
		if (user.getId().equals(command.getLoginUserId())) {
			throw new BusinessException("msg.user.cannotDeleteSelf");
		}

		delete(user);

		// add user log and audit log
		addUserLogInfo1(command, user, "Delete User ");
		auditLogService.addAuditLog(Arrays.asList(user.getUsername()), command, OperatorLogForm.DELETE_USER, null);

		sendUserStatusChangedMessage(user.getId(), user.getUsername(), "delete");
	}

	private void sendUserStatusChangedMessage(long userId, String username, String eventType) {
		if (redisClient != null) {
			try {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", userId);
				map.put("username", username);
				map.put("action", eventType);
				redisClient.sendMessage("tms.user.status.changed", map);
			} catch (Exception e) {
				LOGGER.error("Failed to publish user status changed message", e);
			}

		}
	}

	private void delete(User user) {
		userRoleService.deleteUserRoles(user);
		userGroupService.deleteUserGroups(user);
		passwordHistoryDao.deletePasswordHistory(user.getId());
		pwdTokenDao.deletePasswordResetToken(user.getId());
		aclTUserGroupService.deleteACLUserGroups(user.getId());
		appKeyService.deleteAppClient(user.getId());
		userDao.delete(user.getId());
	}
	/**
	 * @Description: 校验前端传来的密码，需先解密，再校验
	 * @param password
	 * @return
	 * @return: boolean
	 */
	private boolean validatePassword(String password) {
		String regex = "(?:(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[^A-Za-z0-9])).{8,32}";
		if (!Pattern.matches(regex, HashedCredentials.decryptPassword(password))) {
			return false;
		}
		return true;
	}

	@Override
	public void resetPassword(Long userId, ResetPasswordForm command) {
		// check group permission
		groupService.checkPermissionOfGroup(command, command.getGroupId());
		User user = validateUser(userId);

		AclManager.checkPermissionOfUser(command.getLoginUserId(), user);
		PasswordResetToken pwdToken = validatePwdToken(userId);
		if (user.isLdap()) {
			throw new BusinessException("msg.user.unsupportedResetPasswordOfLdapUser");
		}

		user.setModifier(command.getLoginUsername());
		user.setModifyDate(new Date());

		// send mail
		String resetToken = UUID.randomUUID().toString();
		pwdToken.setResetPwdToken(resetToken);
		pwdToken.setTokenIssueDate(new Date());
		if (resetPasswordMailSender != null && !StringUtils.isEmpty(user.getEmail())) {
			try {
				resetPasswordMailSender.sendResetPasswordEmail(user, pwdToken);
				// add user log and audit log
				userlogService.addUserLog(command, user, "Request to reset User " + user.getUsername() + " password");
				auditLogService.addAuditLog(Arrays.asList(user.getUsername()), command,
						OperatorLogForm.Send_RESET_EMAIL, " password");
			} catch (Exception e) {
				LOGGER.warn("Reset User " + user.getUsername() + " password failed", e);
			}
		}

	}

	private PasswordResetToken validatePwdToken(Long userId) {
		PasswordResetToken pwdToke = pwdTokenDao.get(userId);
		if (pwdToke == null) {
			throw new BusinessException("msg.passwordResetToken.notFound");
		}
		return pwdToke;
	}

	private void savePasswordHistory(User user) {
		PasswordHistory ph = new PasswordHistory();
		ph.setUserId(user.getId());
		ph.setPassword(user.getPassword());
		ph.setPwdEncAlg(user.getPwdEncAlg());
		ph.setPwdEncSalt(user.getPwdEncSalt());
		ph.setPwdEncIt(user.getPwdEncIt());
		ph.setChangeDate(new Date());
		ph.save();
	}

	@Override
	public void changePassword(String username, String token, ChangePasswordForm command) {
		User user = userDao.findByUsername(username);
		if (user == null) {
			throw new BusinessException("msg.user.notFound");
		}

		PasswordResetToken pwdToken = validatePwdToken(user.getId());
		if (StringUtils.isEmpty(pwdToken.getResetPwdToken()) || !pwdToken.getResetPwdToken().equals(token)) {
			throw new BusinessException("msg.user.findPassword.invalidToken");
		}
		commandChangePassword(user, pwdToken, command);
	}

	@Override
	public void changeMyPassword(ChangePasswordForm command) {
		Long userId = command.getLoginUserId();
		if (userId == null) {
			return;
		}
		User user = validateUser(userId);
		PasswordResetToken pwdToken = validatePwdToken(userId);
		commandChangePassword(user, pwdToken, command);

	}
	
	@Override
    public String generatePassword(String username, BaseForm command) {
        User user = userDao.findByUsername(username);
        if (user == null) {
            throw new BusinessException("msg.user.notFound");
        }
        String newPassword = buildPassword();
        if(StringUtils.isNotBlank(user.getPassword())){
            savePasswordHistory(user);
        }
        
        user.setForceChgPwd(false);
        user.setPasswordErrorCount(0);
        user.setPwdChgDate(new Date());
        setTmsUserPassword(newPassword, user);

        if (command.getLoginUserId() == null) {
            user.setModifier(user.getUsername());
        } else {
            user.setModifier(command.getLoginUsername());
        }
        user.setModifyDate(new Date());
        
        userDao.update(user);
        // add user log and audit log
        userlogService.addUserLog(command, user, "Generate  password");
        auditLogService.addAuditLog(Arrays.asList(""), command, OperatorLogForm.GENERATE_USER_PWD, " password");
        return newPassword;
    }
	
	/**
	 * @Description: 生成满足密码规则的10位随机密码
	 * @return
	 * @return: String
	 */
	private String buildPassword(){
	    String password = StringHelper.getRandomPwdByLength(10);
	    String encryptPassword = HashedCredentials.encryptPassword(password);
	    if(validatePassword(encryptPassword)){
	        return encryptPassword;
	    }else {
            return buildPassword();
        }
	}

	private void commandChangePassword(User user, PasswordResetToken pwdToken, ChangePasswordForm command) {
		if (user.isLdap()) {
			throw new BusinessException("msg.user.unsupportedChangePasswordOfLdapUser");
		}
		String digestedOldPwd = HashedCredentials.encodedPassword(command.getOldPassword(), user.getPwdEncAlg(),
				HashedCredentials.getStatcSalt(), user.getPwdEncSalt(), user.getPwdEncIt());
		if (!StringUtils.equals(digestedOldPwd, user.getPassword())) {
			throw new BusinessException("msg.user.invalidOldPassword");
		}

		changePassword(user, pwdToken, command);

		// add user log and audit log
		userlogService.addUserLog(command, user, "Change password");
		auditLogService.addAuditLog(Arrays.asList(""), command, OperatorLogForm.CHG_USER_PWD, " password");
	}

	private void changePassword(User user, PasswordResetToken pwdToken, ResetPasswordForm command) {
		String pwd = command.getNewPassword();
		if (!validatePassword(pwd)) {
			throw new BusinessException("msg.user.illegalPassword");
		}

		// validate password:can not same as the before 13 times password
		if (passwordPolicy != null) {
			passwordPolicy.validateNewPassword(pwd, user, oldPwdPreserveNumber);
		}

		savePasswordHistory(user);

		user.setForceChgPwd(false);
		String resetToken = UUID.randomUUID().toString();
		pwdToken.setResetPwdToken(resetToken);
		pwdToken.setTokenIssueDate(new Date());
		user.setPasswordErrorCount(0);
		user.setPwdChgDate(new Date());
		setTmsUserPassword(pwd, user);

		user.setModifier(user.getUsername());
		user.setModifyDate(new Date());

	}

	@Override
	public void forgetPassword(String username, String email, BaseForm command) {
		User user = this.userDao.findByUsernameAndEmail(username, email);
		if (user == null) {
			throw new BusinessException("msg.user.nameOrEmailError");
		}
		if (user.isLdap()) {
			throw new BusinessException("msg.user.unsupportedFindPasswordOfLdapUser");
		}

		if (resetPasswordMailSender == null) {
			throw new BusinessException("msg.user.unsupportedFindPassword");
		}

		if (StringUtils.isEmpty(user.getEmail())) {
			throw new BusinessException("msg.user.emptyEmail");
		}
        if (checkSendLimit(email)) {
            // 用于限制一个邮箱地址一分钟内只能发送一封邮件
            String redisKey = emailSendLimit + email;
            redisClient.getRedisTemplate().opsForValue().set(redisKey, StringHelper.getDatetime());
            redisClient.getRedisTemplate().expire(redisKey, 60, TimeUnit.SECONDS);
            // 用于限制整个系统一分钟内最多只能发送五封邮件
            String allEmailSendKey = allEmailSendLimit + StringHelper.getDatetime() + StringHelper.random(5, 0);
            redisClient.getRedisTemplate().opsForValue().set(allEmailSendKey, StringHelper.getDatetime());
            redisClient.getRedisTemplate().expire(allEmailSendKey, 60, TimeUnit.SECONDS);
            String resetToken = UUID.randomUUID().toString();
            PasswordResetToken pwdToken = validatePwdToken(user.getId());
            pwdToken.setResetPwdToken(resetToken);
            pwdToken.setTokenIssueDate(new Date());
            forgetPasswordMailSender.sendForgetPasswordEmail(user, pwdToken);
            // / add user log and audit log
            userlogService.addUserLog(command, user, "Forgot password");
            auditLogService.addAuditLog(Arrays.asList(user.getUsername()), command, OperatorLogForm.FORGOT_USER_PWD, null);
        } else {
            throw new BusinessException("msg.forgetPassword.tooMany");
        }
	}

	@Override
	public void setPasswordByToken(String username, String token, ResetPasswordForm command) {
		User user = userDao.findByUsername(username);
		if (user == null) {
			throw new BusinessException("msg.user.notFound");
		}
		PasswordResetToken pwdToken = validatePwdToken(user.getId());
		if (StringUtils.isEmpty(pwdToken.getResetPwdToken()) || !pwdToken.getResetPwdToken().equals(token)) {
			throw new BusinessException("msg.user.findPassword.invalidToken");
		}

		if (pwdToken.getTokenIssueDate() != null) {
			Date expirationDate = DateTimeUtils.addDate(pwdToken.getTokenIssueDate(), "hour", resetTokenValidHours);
			if (expirationDate.before(new Date())) {
				throw new BusinessException("msg.user.findPassword.expiredToken");
			}
		}
		changePassword(user, pwdToken, command);

		/// add user log and audit log
		userlogService.addUserLog(command, user, "Reset User " + user.getUsername() + " password");
		auditLogService.addAuditLog(Arrays.asList(user.getUsername()), command, OperatorLogForm.RESET_USER_PWD,
				" password");
	}

	@Override
	public void loginSuccess(String username) {
		if (username == null || "".equals(username)) {
			return;
		}
		User user = findByUsername(username);
		if (user == null) {
			return;
		}

		user.setLastLoginDate(new Date());
		user.setPasswordErrorCount(0);
	}

	@Override
	public int loginFailureWithBadCredentials(String username) {
		if (username == null || "".equals(username)) {
			return 0;
		}
		User user = findByUsername(username);
		if (user == null) {
			return 0;
		}

		int errorCount = user.getPasswordErrorCount() + 1;
		user.setPasswordErrorCount(errorCount);
		user.setLastLoginDate(new Date());
		return errorCount;
	}

	@Override
	public List<User> getInactiveUser(Date startDate, Date endDate) {
		return userDao.getInactiveUsers(startDate, endDate);
	}

	@Override
	public void lockInactiveUsers(Date lastLoginDate) {
		// system get inactive user and lock it
		List<User> users = userDao.getInactiveUsers(lastLoginDate);
		for (User user : users) {
			userDao.disableInactiveUsers(user.getId());
			userlogService.addUserLogInfo(
					"User " + user.getUsername() + " bas been deactivated due to not logged in exceeds the limit days");
			auditLogService.addAuditLogs(
					"User " + user.getUsername() + " bas been deactivated due to not logged in exceeds the limit days");
		}
	}

	@Override
	public void deleteDisabledInactiveUser(Date lastLoginDate) {
		// system delete disabled user
		List<User> users = userDao.getDisabledInactiveUser(lastLoginDate);
		for (User user : users) {
			userlogService.addUserLogInfo(
					"User " + user.getUsername() + " has been deleted due to deactivated days that exceeds the limit");
			auditLogService.addAuditLogs(
					"User " + user.getUsername() + " has been deleted due to deactivated days that exceeds the limit ");
			delete(user);
		}
	}

	@Override
	public List<User> getPasswordExpiredUsers(Date endLastChangeDate, Date startLastChangeDate) {
		return userDao.getPasswordExpiredActiveUsers(endLastChangeDate, startLastChangeDate);
	}

	@Override
	public void setPasswordExpired(Date lastChangeDate) {
		List<User> users = userDao.getPasswordExpiredUser(lastChangeDate);
		for (User user : users) {
			userDao.setPasswordExpired(user.getId());
		}

	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public void dismissGroup(long groupId, BaseForm command) {
		List<Long> groupDecantUserIds = userGroupService.getGroupDecsantUserIds(groupId);
		userGroupService.deleteUsersFromGroupCascading(groupId);
		Object[] object = getNeedDeleteAndUnBindUserIds(groupDecantUserIds);
		List<Long> needUnBindUserIds = (List<Long>) object[0];
		List<Long> needDeleteUserIds = (List<Long>) object[1];
		deleteAclUserGroups(needUnBindUserIds, groupId);
		if (CollectionUtils.isEmpty(needDeleteUserIds)) {
			return;
		}

		deleteAclUserGroups(needDeleteUserIds);
		userDao.deleteUserCascading(needDeleteUserIds);
	}

	private void deleteAclUserGroups(List<Long> needDeleteUserIds) {

		if (CollectionUtils.isEmpty(needDeleteUserIds)) {
			return;
		}
		for (Long userId : needDeleteUserIds) {
			aclTUserGroupService.deleteACLUserGroups(userId);
		}

	}

	private void deleteAclUserGroups(List<Long> needunBindUserIds, Long groupId) {
		if (CollectionUtils.isEmpty(needunBindUserIds)) {
			return;
		}
		for (Long userId : needunBindUserIds) {

			aclTUserGroupService.deleteACLUserGroups(userId, groupId);

		}
	}

	private Object[] getNeedDeleteAndUnBindUserIds(List<Long> groupDecantUserIds) {

		Object[] object = new Object[2];
		if (CollectionUtils.isEmpty(groupDecantUserIds)) {

			return object;
		}

		List<Long> needDeleteUserIds = new ArrayList<Long>();
		List<Long> needUnBinUserIds = new ArrayList<Long>();
		// check user is bind by other group
		for (Long userId : groupDecantUserIds) {
			if (userGroupService.isBindByOtherGroup(userId)) {
				needUnBinUserIds.add(userId);
				continue;
			}
			needDeleteUserIds.add(userId);
		}
		object[0] = needUnBinUserIds;
		object[1] = needDeleteUserIds;
		return object;
	}

	@Override
	public User validateUser(long userId) {
		User user = userDao.get(userId);
		if (user == null) {
			throw new BusinessException("msg.user.notFound");
		}
		return user;
	}

	@Override
	public int getUserCount(String appName, Long groupId, BaseForm command) {
		return userDao.getUserCount(appName, groupId, command);
	}

	@Override
	public void updateLoginLdapUserInfo() {
		BaseForm command = new BaseForm();
		User user = validateUser(command.getLoginUserId());
		if (user.isLdap()) {
			if (ldapSearchExecutor == null) {
				throw new BusinessException("msg.user.unsupportLdapUser");
			}
			UserForm userForm = null;
			try {
				userForm = ldapSearchExecutor.getUserInfo(user.getUsername());
			} catch (LdapException e) {
				LOGGER.warn("Connect to the LDAP server failed", e);
			}
			if (userForm != null) {
				user.setUsername(userForm.getUsername());
				user.setFullname(userForm.getFullname());
				user.setEmail(userForm.getEmail());
				user.setPhone(userForm.getPhone());
				user.setCountry(userForm.getCountryName());
				user.setProvince(userForm.getProvinceName());
				user.setCity(userForm.getCityName());
				user.setZipCode(userForm.getZipCode());
				user.setAddress(userForm.getAddress());
			}
		}
	}

	@Override
	public void updateAllLdapUsersInfo() {
		List<User> ldapUsers = userDao.getLdapUser();
		if (ldapSearchExecutor == null) {
			throw new BusinessException("msg.user.unsupportLdapUser");
		}
		for (User user : ldapUsers) {
			UserForm userForm;
			try {
				userForm = ldapSearchExecutor.getUserInfo(user.getUsername());
			} catch (LdapException e) {
				LOGGER.warn("Connect to the LDAP server failed", e);
				break;
			}
			if (userForm != null) {
				user.setUsername(userForm.getUsername());
				user.setFullname(userForm.getFullname());
				user.setEmail(userForm.getEmail());
				user.setPhone(userForm.getPhone());
				user.setCountry(userForm.getCountryName());
				user.setProvince(userForm.getProvinceName());
				user.setCity(userForm.getCityName());
				user.setZipCode(userForm.getZipCode());
				user.setAddress(userForm.getAddress());
			}
		}

	}

	@Override
	public List<String> getUserRolesByUserName(String username) {
		List<String> userRoles = userDao.getUserRolesByUserName(username);
		return userRoles;
	}

	@Override
	public List<Map<String, Object>> getRoleAndCount(String appName, Long groupId, BaseForm command) {
		return userDao.getRoleAndCount(appName, groupId, command);
	}

	@Override
	public List<Role> findRole(String appName) {
		return userDao.findRole(appName);
	}

	private String casLoginUrl;
	private String tmsCallbackUrl;
	private String pxdesignerCallbackUrl;
	private String oldPwdPreserveNumber;

	@Autowired
	public void setCasLoginUrl(@Value("${tms.login.casLoginUrl}") String casLoginUrl) {
		this.casLoginUrl = casLoginUrl;
	}

	@Autowired
	public void setTmsCallbackUrl(@Value("${tms.login.callbackUrl}") String tmsCallbackUrl) {
		this.tmsCallbackUrl = tmsCallbackUrl;
	}

	@Autowired(required = true)
	public void setPxdesignerCallbackUrl(@Value("${pxdesigner.login.callbackUrl}") String pxdesignerCallbackUrl) {
		this.pxdesignerCallbackUrl = pxdesignerCallbackUrl;
	}

	@Autowired
	public void setForgetPasswordUrl(@Value("${tms.user.oldPassword.preserveNumber}") String oldPwdPreserveNumber) {
		this.oldPwdPreserveNumber = oldPwdPreserveNumber;
	}

	public String getForgetPasswordUrl() {
		return oldPwdPreserveNumber;
	}

	@Override
	public String getPxdesignerAddress() {
		if (StringUtils.isNotEmpty(pxdesignerCallbackUrl)) {
			return CommonUtils.constructRedirectUrl(casLoginUrl, "service", pxdesignerCallbackUrl, false, false);
		}
		return null;
	}

	@Override
	public String getTMSCallbackUrl() {
		String serviceUrl = tmsCallbackUrl;
		if (tmsCallbackUrl != null && tmsCallbackUrl.lastIndexOf('?') == -1) {
			serviceUrl += "?client_name=casclient";
		}
		return CommonUtils.constructRedirectUrl(casLoginUrl, "service", serviceUrl, false, false);
	}

	@Override
	public void updateLastLoginDate(String userName) {
		userDao.updateLastLoginDate(userName);
	}
	
	@Override
	public void removeLoginUser(Page<Map<String, Object>> items,QueryUserForm command) {
		List<Map<String, Object>> users = items.getItems();
		List<Map<String, Object>> result = new LinkedList<Map<String,Object>>();
		for(Map<String, Object> map:users)
		{
			Long id = (Long)map.get("id");
			if(!command.getLoginUserId().equals(id))
			{
				result.add(map);
			}
		}
		items.setItems(result);
	}
	
	/**
     * @Description:限制每分钟最多只能发送5封邮件，同一个地址，每分钟只能发一次
     * @return
     * @return: boolean
     */
    private boolean checkSendLimit(String toAddress) {
        Set<String> sendEmailKeys = redisClient.getRedisTemplate().keys(allEmailSendLimit + "*");
        String addressKey = emailSendLimit + toAddress;
        if ((null != sendEmailKeys && sendEmailKeys.size() > 5) || redisClient.getRedisTemplate().hasKey(addressKey)) {// 整个系统只能发5封邮件，同一个地址，每分钟只能发一次
            return false;
        } else {
            return true;
        }
    }

}
