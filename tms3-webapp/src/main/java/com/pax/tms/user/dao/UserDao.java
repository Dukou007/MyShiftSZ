/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) YYYY-? PAX Technology, Inc. All rights reserved.		
 * Description: user dao interfaces		
 * Revision History:		
 * Date	        Author	          Action
 * 20170111     Carry       user dao interfaces
 * ============================================================================		
 */
package com.pax.tms.user.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.pax.common.dao.IBaseDao;
import com.pax.common.web.form.BaseForm;
import com.pax.tms.user.model.Role;
import com.pax.tms.user.model.User;

public interface UserDao extends IBaseDao<User, Long> {

	User findByUsername(String username);

	User findByUsernameAndEmail(String username, String email);

	List<User> getInactiveUsers(Date lastLoginTime);

	List<User> getInactiveUsers(Date startLastLoginDate, Date endLastLoginDate);

	void disableInactiveUsers(Long userId);

	List<User> getDisabledInactiveUser(Date lastLoginTime);

	List<User> getPasswordExpiredActiveUsers(Date endLastChangeDate, Date startLastChangeDate);

	void setPasswordExpired(Long userId);

	int getUserCount(String appName, Long groupId, BaseForm command);

	List<User> getLdapUser();

	void deleteUserCascading(List<Long> needDeleteUserIds);

	boolean hasUsers(List<Long> groupIds);

	List<String> getUserRolesByUserName(String username);

	void updateRealAndUsageDashboardPie(String data, Long userId, String type);

	List<Map<String, Object>> getRoleAndCount(String appName, Long groupId, BaseForm command);

	List<Role> findRole(String appName);

	Role getRole(long roleId);

	List<User> getPasswordExpiredUser(Date lastChangeDate);

	void updateLastLoginDate(String userName);
}
