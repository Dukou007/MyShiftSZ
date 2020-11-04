/*
 * ============================================================================
 * = COPYRIGHT				
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *   	Copyright (C) 2009-2020 PAX Technology, Inc. All rights reserved.		
 * ============================================================================		
 */
package com.pax.tms.cas.login;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hjson.JsonArray;

public class TmsUserAttributes {

	private static final String IS_LDAP_KEY = "IS_LDAP";
	private static final String ACC_EXP_DT_KEY = "ACC_EXPIRY_DATE";
	private static final String IS_ENABLED_KEY = "IS_ENABLED";
	private static final String IS_LOCKED_KEY = "IS_LOCKED";
	private static final String LAST_LOGIN_DATE_KEY = "LAST_LOGIN_DATE";
	private static final String ENABLED_DATE_KEY = "ENABLED_DATE";
	private static final String CREATE_DATE_KEY = "CREATE_DATE";
	private static final String SUSPEND_USER_ACCOUNT_INACTIVE_FOR_DAYS_KEY = "DISABLED_AFTER_DAYS";
	private static final String DELETE_USER_ACCOUNT_INACTIVE_FOR_DAYS_KEY = "REMOVED_AFTER_DAYS";
	private static final String USER_ROLES_KEY = "USER_ROLES";
	private static final String USERNAME_KEY = "USERNAME";
	private static final String IS_SYS_KEY = "IS_SYS";

	private static final String FORCE_CHG_PASS_KEY = "FORCE_CHG_PWD";
	private static final String LAST_PASS_CHG_DATE_KEY = "LAST_PWD_CHG_DATE";
	private static final String MAX_PASS_AGE_KEY = "MAX_PWD_AGE";
	private static final String ENCRYPT_ALG_KEY = "ENCRYPT_ALG";
	private static final String ENCRYPT_SALT_KEY = "ENCRYPT_SALT";
	private static final String ENCRYPT_ITERATION_COUNT_KEY = "ENCRYPT_ITERATION_COUNT";
	private static final String USER_PASS_KEY = "USER_PWD";
	private static final String FAILED_LOGIN_COUNT_KEY = "FAILED_LOGIN_COUNT";

	private long userId;
	private Map<String, Object> attributes;
	private List<String> roles;

	public TmsUserAttributes(Map<String, Object> attributes) {
		super();
		this.attributes = attributes;
		Object userIdAttr = attributes.get("USER_ID");
		if (userIdAttr == null) {
			throw new IllegalArgumentException("USER_ID cannot be empty");
		}
		userId = Long.parseLong(userIdAttr.toString());
	}

	public long getUserId() {
		return userId;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public boolean isSystemUser() {
		Object value = attributes.get(IS_SYS_KEY);
		return value != null && "1".equals(value.toString());
	}

	public boolean isLdapUser() {
		Object ldapAttr = attributes.get(IS_LDAP_KEY);
		return ldapAttr != null && "1".equals(ldapAttr.toString());
	}

	public boolean isAccoutExpired() {
		Date accExpDt = (Date) attributes.get(ACC_EXP_DT_KEY);
		return accExpDt != null && new Date().after(accExpDt);
	}

	public boolean isAccountDisabled() {
		Object enabledAttr = attributes.get(IS_ENABLED_KEY);
		return enabledAttr != null && "0".equals(enabledAttr.toString());
	}

	public boolean isAccountLocked() {
		Object lockedAttr = attributes.get(IS_LOCKED_KEY);
		return lockedAttr != null && "1".equals(lockedAttr.toString());
	}

	public boolean isInactiveUser() {
		return isInactiveUser(getSuspendUserAccountInactiveForDays(), getRemoveUserAccountInactiveForDays());
	}

	private Date getLastActiveTime() {
		Date lastActiveTime = null;
		Date lastLoginDt = (Date) attributes.get(LAST_LOGIN_DATE_KEY);
		Date enabledDate = (Date) attributes.get(ENABLED_DATE_KEY);
		if (lastLoginDt != null) {
			if (enabledDate != null && enabledDate.after(lastLoginDt)) {
				lastActiveTime = enabledDate;
			} else {
				lastActiveTime = lastLoginDt;
			}
		} else {
			lastActiveTime = enabledDate;
		}

		if (lastActiveTime == null) {
			lastActiveTime = (Date) attributes.get(CREATE_DATE_KEY);
		}
		return lastActiveTime;
	}

	public boolean isInactiveUser(int suspendUserAccountInactiveForDays, int deleteUserAccountInactiveForDays) {
		int maxInactiveForDays = suspendUserAccountInactiveForDays != 0
				&& suspendUserAccountInactiveForDays < deleteUserAccountInactiveForDays
						? suspendUserAccountInactiveForDays : deleteUserAccountInactiveForDays;
		if (maxInactiveForDays <= 0) {
			return false;
		}

		Date lastActiveTime = getLastActiveTime();
		return lastActiveTime != null && new Date().after(DateUtils.addDays(lastActiveTime, maxInactiveForDays));
	}

	private int getSuspendUserAccountInactiveForDays() {
		Object suspendInactiveAttr = attributes.get(SUSPEND_USER_ACCOUNT_INACTIVE_FOR_DAYS_KEY);
		if (suspendInactiveAttr == null) {
			return 0;
		}
		return Integer.parseInt(suspendInactiveAttr.toString());
	}

	private int getRemoveUserAccountInactiveForDays() {
		Object deleteInactiveAttr = attributes.get(DELETE_USER_ACCOUNT_INACTIVE_FOR_DAYS_KEY);
		if (deleteInactiveAttr == null) {
			return 0;
		}
		return Integer.parseInt(deleteInactiveAttr.toString());
	}

	public boolean isPasswordMustChange() {
		Object forceChangePwdAttr = attributes.get(FORCE_CHG_PASS_KEY);
		return forceChangePwdAttr != null && "1".equals(forceChangePwdAttr.toString());
	}

	public boolean isPasswordExpired() {
		int pwdChgIv = getChangePasswordInterval();
		if (pwdChgIv <= 0) {
			return false;
		}

		Date pwdChgDt = (Date) attributes.get(LAST_PASS_CHG_DATE_KEY);

		if (pwdChgDt == null) {
			pwdChgDt = (Date) attributes.get(ENABLED_DATE_KEY);
		}

		if (pwdChgDt == null) {
			pwdChgDt = (Date) attributes.get(CREATE_DATE_KEY);
		}

		return new Date().after(DateUtils.addDays(pwdChgDt, pwdChgIv));
	}

	private int getChangePasswordInterval() {
		Object chgPwdIvAttr = attributes.get(MAX_PASS_AGE_KEY);
		if (chgPwdIvAttr == null) {
			return 0;
		}
		return Integer.parseInt(chgPwdIvAttr.toString());
	}

	public String getPasswordEncodeAlgorithm() {
		Object algorithmAttr = attributes.get(ENCRYPT_ALG_KEY);
		return algorithmAttr == null ? null : algorithmAttr.toString();
	}

	public String getPasswordEncodeSalt() {
		Object saltAttr = attributes.get(ENCRYPT_SALT_KEY);
		return saltAttr == null ? null : saltAttr.toString();
	}

	public int getPasswordEncodeIterations() {
		Object iterationsAttr = attributes.get(ENCRYPT_ITERATION_COUNT_KEY);
		return iterationsAttr == null ? 0 : Integer.parseInt(iterationsAttr.toString());
	}

	public int getFailedLoginCount() {
		Object pwdErrorCountAttr = attributes.get(FAILED_LOGIN_COUNT_KEY);
		return pwdErrorCountAttr == null ? 0 : Integer.parseInt(pwdErrorCountAttr.toString());
	}

	public String getPassword() {
		Object passwordAttr = attributes.get(USER_PASS_KEY);
		return passwordAttr == null ? null : passwordAttr.toString();
	}

	public void setAllRoles(List<String> roles) {
		this.roles = roles;
		attributes.put(USER_ROLES_KEY, StringUtils.join(roles, ","));
	}

	public String getRoles() {
		return (String) attributes.get(USER_ROLES_KEY);
	}

	public List<String> getUserRoles() {
		return roles;
	}

	public void setAppRoles(Map<String, List<String>> appRolesMap) {
		if (appRolesMap == null) {
			return;
		}

		for (Entry<String, List<String>> entry : appRolesMap.entrySet()) {
			String appName = entry.getKey();
			if (appName == null || appName.isEmpty()) {
				attributes.put("ROLES", entry.getValue());
			} else {
				attributes.put(appName.toUpperCase() + "_" + "ROLES", entry.getValue());
			}
		}
	}

	@SuppressWarnings("unchecked")
	public String getAppRoles(String app) {
		List<String> appRoles = (List<String>) attributes.get(app.toUpperCase() + "_" + "ROLES");
		if (appRoles == null || appRoles.isEmpty()) {
			return null;
		}
		return StringUtils.join(appRoles, ",");
	}

	public void setGroups(JsonArray groups) {
		attributes.put("USER_GROUPS", groups);
	}

	public void setDefaultGroup(Long defaultGroup) {
		attributes.put("DEFAULT_GROUP", defaultGroup);
	}

	public String getUserName() {
		Object userNameAttr = attributes.get(USERNAME_KEY);
		return userNameAttr == null ? null : userNameAttr.toString();
	}

}
