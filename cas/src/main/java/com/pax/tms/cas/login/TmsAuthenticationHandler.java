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

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;

import org.jasig.cas.adaptors.jdbc.AbstractJdbcUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.HandlerResult;
import org.jasig.cas.authentication.LdapAuthenticationHandler;
import org.jasig.cas.authentication.MessageDescriptor;
import org.jasig.cas.authentication.PreventedException;
import org.jasig.cas.authentication.UsernamePasswordCredential;
import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.authentication.support.PasswordExpiringWarningMessageDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.google.common.collect.ImmutableSet;

public class TmsAuthenticationHandler extends AbstractJdbcUsernamePasswordAuthenticationHandler {

	private LdapAuthenticationHandler ldapHandler;

	private Map<String, Set<String>> resultAttributeMapping;

	private TmsUserManager tmsUserManager;

	private int lockUserAfterFailedLoginTimes = 6;
	private int suspendUserAccountInactiveForDays = 0;
	private int deleteUserAccountInactiveForDays = 0;
	private boolean useGlobalSetting = false;
	private boolean ldapEnabled = false;

	@Override
	protected HandlerResult authenticateUsernamePasswordInternal(UsernamePasswordCredential transformedCredential)
			throws GeneralSecurityException, PreventedException {

		if (tmsUserManager == null) {
			throw new GeneralSecurityException("Authentication handler is not configured correctly");
		}

		String username = getPrincipalNameTransformer().transform(transformedCredential.getUsername()).toLowerCase();
		String encodedPsw = this.getPasswordEncoder().encode(transformedCredential.getPassword());

		TmsUserAttributes userAttributes = getUserAttributes(username);
		if (userAttributes == null) {
			throw new TmsUserFailedLoginException("Invalid username or password");
		}

		boolean success = false;
		try {
			HandlerResult handlerResult = doAuthenticate(transformedCredential, username, encodedPsw, userAttributes);
			success = true;
			return handlerResult;
		} finally {
			doAfter(transformedCredential, userAttributes, success);
		}
	}

	private TmsUserAttributes getUserAttributes(String username) throws LoginException, PreventedException {
		try {
			return tmsUserManager.getUserAttributes(username);
		} catch (IncorrectResultSizeDataAccessException e) {
			if (e.getActualSize() == 0) {
				String msg = username + " not found with SQL query";
				logger.error(msg, e);
				throw new TmsUserAccountNotFoundException(msg);
			} else {
				String msg = "Multiple records found for " + username;
				logger.error(msg, e);
				throw new TmsUserFailedLoginException(msg);
			}
		} catch (DataAccessException e) {
			String msg = "SQL exception while executing query for " + username;
			logger.error(msg, e);
			throw new PreventedException(msg, e);
		} catch (Exception e) {
			String msg = "Unknow exception while executing query for " + username;
			logger.error(msg, e);
			throw new PreventedException(msg, e);
		}
	}

	/**
	 * get person attributes
	 * 
	 * @param username
	 * @return
	 */
	public Map<String, Object> getPersonAttributes(String username) {
		if (username != null) {
			String lowerCaseUsername = username.toLowerCase();
			try {
				TmsUserAttributes userAttributes = getUserAttributes(lowerCaseUsername);
				if (userAttributes != null) {
					return getPersonAttributes(userAttributes);
				}
			} catch (LoginException | PreventedException e) {
				logger.error("Failed to get tms user pricipal attributes", e);
			}
		}
		return null;
	}

	private Map<String, Object> getPersonAttributes(TmsUserAttributes userAttributes) {
		Map<String, Object> personAttributes = userAttributes.getAttributes();
		// If no mapping just use the attributes as-is
		if (this.resultAttributeMapping == null) {
			return personAttributes;
		}

		// Map the attribute names via the resultAttributeMapping
		Map<String, Object> mappedAttributes = new LinkedHashMap<>();
		for (Map.Entry<String, Set<String>> resultAttrEntry : this.resultAttributeMapping.entrySet()) {
			String dataKey = resultAttrEntry.getKey();

			// Only map found data attributes.
			if (personAttributes.containsKey(dataKey)) {
				Set<String> resultKeys = resultAttrEntry.getValue();

				// If dataKey has no mapped resultKeys just use the dataKey
				if (resultKeys == null) {
					resultKeys = ImmutableSet.of(dataKey);
				}

				// Add the value to the mapped attributes for each mapped key
				Object value = personAttributes.get(dataKey);
				for (final String resultKey : resultKeys) {
					if (resultKey == null) {
						mappedAttributes.put(dataKey, value);
					} else {
						mappedAttributes.put(resultKey, value);
					}
				}
			}
		}
		return mappedAttributes;
	}

	private HandlerResult doAuthenticate(UsernamePasswordCredential transformedCredential, String username,
			String encodedPsw, TmsUserAttributes userAttributes) throws GeneralSecurityException, PreventedException {

		List<MessageDescriptor> warnings = null;
		if (userAttributes.isLdapUser()) {
			if (!ldapEnabled || ldapHandler == null) {
				throw new FailedLoginException("LDAP users is not supported");
			}
			HandlerResult ldapHandlerResult = ldapHandler.authenticate(transformedCredential);
			warnings = ldapHandlerResult.getWarnings();
			checkUserAccountStatus(username, userAttributes);

			Principal principal = ldapHandlerResult.getPrincipal();
			Map<String, Object> ldapAttributes = principal == null ? null : principal.getAttributes();
			tmsUserManager.updateLdapUserProfile(userAttributes, ldapAttributes);
		} else {
			verifyPassword(transformedCredential, userAttributes, encodedPsw);
			checkUserAccountStatus(username, userAttributes);
		}

		Map<String, Object> values = getPersonAttributes(userAttributes);
		return createHandlerResult(transformedCredential, this.principalFactory.createPrincipal(username, values),
				warnings);
	}

	private void verifyPassword(UsernamePasswordCredential transformedCredential, TmsUserAttributes userAttributes,
			String encodedPassword) throws FailedLoginException {
		if (userAttributes.getPassword() == null) {
			// skip verify password if password has not been set
			return;
		}

		if (!tmsUserManager.verifyPassowrd(userAttributes, encodedPassword)) {
			if (lockUserAfterFailedLoginTimes > 0 && !userAttributes.isAccountDisabled()
					&& !userAttributes.isAccountLocked()) {
				int failedLoginCount = tmsUserManager.addFailedLoginCount(userAttributes);
				if (failedLoginCount == lockUserAfterFailedLoginTimes - 1) {
					throw new OneTimeToTryFailedLoginException(
							"You just have one time to try. If the password is incorrect again, your account will be locked.");
				} else if (failedLoginCount == lockUserAfterFailedLoginTimes) {
					tmsUserManager.lockUser(userAttributes);
					tmsUserManager.addLockUserLog(transformedCredential, userAttributes);
					throw new UserLockedFailedLoginException("The user has been locked.");
				}
			}
			throw new TmsUserFailedLoginException("Password does not match value on record.");
		} else {
			tmsUserManager.resetFailedLoginCount(userAttributes);
		}
	}

	private void checkUserAccountStatus(String username, TmsUserAttributes userAttributes) throws LoginException {
		if (userAttributes.isAccoutExpired()) {
			throw new TmsUserAccountExpiredException(username + " has expired");
		}

		if (userAttributes.isAccountDisabled()) {
			throw new TmsUserAccountDisabledException(username + " has been disabled");
		}

		if (userAttributes.isAccountLocked()) {
			throw new TmsUserAccountLockedException(username + " was locked");
		}

		if (useGlobalSetting) {
			if (userAttributes.isInactiveUser(suspendUserAccountInactiveForDays, deleteUserAccountInactiveForDays)) {
				throw new TmsUserAccountLockedException(username + " has been suspended");
			}
		} else if (userAttributes.isInactiveUser()) {
			throw new TmsUserAccountLockedException(username + " has been suspended");
		}

		if (userAttributes.isPasswordMustChange()) {
			throw new TmsUserAccountPasswordMustChangeException("Password must change before login.");
		}

		if (userAttributes.isPasswordExpired()) {
			throw new TmsUserCredentialExpiredException("Password has expired.");
		}
	}

	protected List<MessageDescriptor> addPasswordExpiringWarningMessage(int ttlDays, String passwordPolicyUrl,
			List<MessageDescriptor> warningsInput) {
		List<MessageDescriptor> warnings = warningsInput;
		if (warnings == null) {
			warnings = new ArrayList<>();
		}
		warnings.add(new PasswordExpiringWarningMessageDescriptor(
				"Password expires in {0} days. Please change your password at <href=\"{1}\">{1}</a>", ttlDays,
				passwordPolicyUrl));
		return warnings;
	}

	private void doAfter(UsernamePasswordCredential transformedCredential, TmsUserAttributes userAttributes,
			boolean success) {
		tmsUserManager.updateLastLoginDate(userAttributes);
		tmsUserManager.addLoginLog(transformedCredential, userAttributes, success);
	}

	public void setResultAttributeMapping(Map<String, Set<String>> resultAttributeMapping) {
		this.resultAttributeMapping = resultAttributeMapping;
	}

	@Autowired
	public void setTmsUserManager(@Qualifier("tmsUserManager") TmsUserManager tmsUserManager) {
		this.tmsUserManager = tmsUserManager;
	}

	@Autowired(required = false)
	public void setLdapHandler(@Qualifier("ldapAuthenticationHandler") LdapAuthenticationHandler ldapHandler) {
		this.ldapHandler = ldapHandler;
	}

	@Autowired
	public void setLdapEnabled(@Value("${tms.ldap.enabled:false}") boolean ldapEnabled) {
		this.ldapEnabled = ldapEnabled;
	}

	@Autowired
	public void setLockUserAfterFailedLoginTimes(
			@Value("${tms.user.lockUserAccountAfterFailedLoginTimes:6}") int lockUserAfterFailedLoginTimes) {
		this.lockUserAfterFailedLoginTimes = lockUserAfterFailedLoginTimes;
	}

	@Autowired
	public void setSuspendUserAccountInactiveForDays(
			@Value("${tms.user.inactiveUser.lock.days:0}") int suspendUserAccountInactiveForDays) {
		this.suspendUserAccountInactiveForDays = suspendUserAccountInactiveForDays;
	}

	@Autowired
	public void setUseGlobalSetting(@Value("${tms.user.useGlobalSetting:false}") boolean useGlobalSetting) {
		this.useGlobalSetting = useGlobalSetting;
	}

	@Autowired
	public void setDeleteUserAccountInactiveForDays(
			@Value("${tms.user.inactiveUser.delete.days:0}") int deleteUserAccountInactiveForDays) {
		this.deleteUserAccountInactiveForDays = deleteUserAccountInactiveForDays;
	}

}
