/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description:  user service interfaces
 * Revision History:		
 * Date	        Author	            Action
 * 20170111     Carry        user service interfaces
 * ============================================================================		
 */
package com.pax.tms.user.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.pax.common.pagination.Page;
import com.pax.common.service.IBaseService;
import com.pax.common.web.form.BaseForm;
import com.pax.tms.user.model.Role;
import com.pax.tms.user.model.User;
import com.pax.tms.user.web.form.AddUserForm;
import com.pax.tms.user.web.form.ChangePasswordForm;
import com.pax.tms.user.web.form.EditUserForm;
import com.pax.tms.user.web.form.QueryUserForm;
import com.pax.tms.user.web.form.ResetPasswordForm;

public interface UserService extends IBaseService<User, Long> {

	User findByUsername(String username);

	void add(AddUserForm command);

	void edit(Long userId, EditUserForm command);

	void active(Long id, Long groupId, BaseForm command);

	void deactive(Long id, Long groupId, BaseForm command);

	void delete(Long[] idList, Long groupId, BaseForm command);

	void resetPassword(Long userId, ResetPasswordForm command);

	void changeMyPassword(ChangePasswordForm command);

	void editMyProfile(EditUserForm command);

	void forgetPassword(String username, String email, BaseForm command);

	void setPasswordByToken(String username, String token, ResetPasswordForm command);

	void loginSuccess(String username);

	int loginFailureWithBadCredentials(String username);

	void dismissGroup(long groupId, BaseForm command);

	User validateUser(long userId);

	/* check and update user states */
	List<User> getInactiveUser(Date startLastLoginDate, Date endLastLoginDate);

	void lockInactiveUsers(Date lastLoginDate);

	void deleteDisabledInactiveUser(Date lastLoginDate);

	List<User> getPasswordExpiredUsers(Date endLastChangeDate, Date startLastChangeDate);

	void setPasswordExpired(Date lastChangeDate);

	int getUserCount(String appName, Long groupId, BaseForm command);

	void updateLoginLdapUserInfo();
	
	void updateAllLdapUsersInfo();

	List<String> getUserRolesByUserName(String username);

	List<Map<String, Object>> getRoleAndCount(String appName, Long groupId, BaseForm command);

	List<Role> findRole(String appName);

	void changePassword(String username, String token, ChangePasswordForm command);

	String getPxdesignerAddress();

	String getTMSCallbackUrl();

	void updateLastLoginDate(String userName);
	
	//排除已登录用户
	void removeLoginUser(Page<Map<String, Object>> items,QueryUserForm command);
	
    String generatePassword(String username, BaseForm command);
}
