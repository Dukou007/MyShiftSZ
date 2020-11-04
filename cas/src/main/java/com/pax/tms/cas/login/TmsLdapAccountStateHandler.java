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

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.LoginException;

import org.jasig.cas.DefaultMessageDescriptor;
import org.jasig.cas.authentication.MessageDescriptor;
import org.jasig.cas.authentication.support.AccountStateHandler;
import org.jasig.cas.authentication.support.LdapPasswordPolicyConfiguration;
import org.jasig.cas.authentication.support.PasswordExpiringWarningMessageDescriptor;
import org.joda.time.Days;
import org.joda.time.Instant;
import org.ldaptive.auth.AccountState;
import org.ldaptive.auth.AuthenticationResponse;
import org.ldaptive.auth.ext.ActiveDirectoryAccountState;
import org.ldaptive.auth.ext.ActiveDirectoryAuthenticationResponseHandler;
import org.ldaptive.auth.ext.EDirectoryAccountState;
import org.ldaptive.auth.ext.PasswordExpirationAccountState;
import org.ldaptive.control.PasswordPolicyControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("tmsLdapAccountStateHandler")
public class TmsLdapAccountStateHandler implements AccountStateHandler {
	/** Map of account state error to CAS authentication exception. */
	protected final Map<AccountState.Error, LoginException> errorMap;

	/** Logger instance. */
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private ActiveDirectoryAuthenticationResponseHandler adAuthenticationResponseHandler = new ActiveDirectoryAuthenticationResponseHandler();

	/**
	 * Instantiates a new account state handler, that populates the error map
	 * with LDAP error codes and corresponding exceptions.
	 */
	public TmsLdapAccountStateHandler() {
		this.errorMap = new HashMap<>();
		this.errorMap.put(ActiveDirectoryAccountState.Error.ACCOUNT_DISABLED, new LdapAccountDisabledException());
		this.errorMap.put(ActiveDirectoryAccountState.Error.ACCOUNT_LOCKED_OUT, new LdapAccountLockedException());
		this.errorMap.put(ActiveDirectoryAccountState.Error.INVALID_LOGON_HOURS, new LdapInvalidLoginTimeException());
		this.errorMap.put(ActiveDirectoryAccountState.Error.INVALID_WORKSTATION,
				new LdapInvalidLoginLocationException());
		this.errorMap.put(ActiveDirectoryAccountState.Error.PASSWORD_MUST_CHANGE,
				new LdapAccountPasswordMustChangeException());
		this.errorMap.put(ActiveDirectoryAccountState.Error.PASSWORD_EXPIRED, new LdapCredentialExpiredException());
		this.errorMap.put(EDirectoryAccountState.Error.ACCOUNT_EXPIRED, new LdapAccountExpiredException());
		this.errorMap.put(EDirectoryAccountState.Error.LOGIN_LOCKOUT, new LdapAccountLockedException());
		this.errorMap.put(EDirectoryAccountState.Error.LOGIN_TIME_LIMITED, new LdapInvalidLoginTimeException());
		this.errorMap.put(EDirectoryAccountState.Error.PASSWORD_EXPIRED, new LdapCredentialExpiredException());
		this.errorMap.put(PasswordExpirationAccountState.Error.PASSWORD_EXPIRED, new LdapCredentialExpiredException());
		this.errorMap.put(PasswordPolicyControl.Error.ACCOUNT_LOCKED, new LdapAccountLockedException());
		this.errorMap.put(PasswordPolicyControl.Error.PASSWORD_EXPIRED, new LdapCredentialExpiredException());
		this.errorMap.put(PasswordPolicyControl.Error.CHANGE_AFTER_RESET, new LdapAccountPasswordMustChangeException());
	}

	@Override
	public List<MessageDescriptor> handle(final AuthenticationResponse response,
			final LdapPasswordPolicyConfiguration configuration) throws LoginException {
		adAuthenticationResponseHandler.handle(response);
		final AccountState state = response.getAccountState();
		if (state == null) {
			logger.debug("Account state not defined. Returning empty list of messages.");
			return Collections.emptyList();
		}
		final List<MessageDescriptor> messages = new ArrayList<>();
		handleError(state.getError());
		handleWarning(state.getWarning(), configuration, messages);

		return messages;
	}

	/**
	 * Handle an account state error produced by ldaptive account state
	 * machinery.
	 * <p>
	 * Override this method to provide custom error handling.
	 *
	 * @param error
	 *            Account state error.
	 * @throws LoginException
	 *             On errors that should be communicated as login exceptions.
	 */
	protected void handleError(final AccountState.Error error) throws LoginException {

		logger.debug("Handling error {}", error);
		final LoginException ex = this.errorMap.get(error);
		if (ex != null) {
			throw ex;
		}
		logger.debug("No LDAP error mapping defined for {}", error);
	}

	/**
	 * Handle an account state warning produced by ldaptive account state
	 * machinery.
	 * <p>
	 * Override this method to provide custom warning message handling.
	 *
	 * @param warning
	 *            the account state warning messages.
	 * @param configuration
	 *            Password policy configuration.
	 * @param messages
	 *            Container for messages produced by account state warning
	 *            handling.
	 */
	protected void handleWarning(final AccountState.Warning warning,
			final LdapPasswordPolicyConfiguration configuration, final List<MessageDescriptor> messages) {

		logger.debug("Handling warning {}", warning);
		if (warning == null) {
			logger.debug("Account state warning not defined");
			return;
		}

		final ZonedDateTime expDate = warning.getExpiration();
		final Days ttl = Days.daysBetween(Instant.now(), new Instant(expDate));
		logger.debug("Password expires in {} days. Expiration warning threshold is {} days.", ttl.getDays(),
				configuration.getPasswordWarningNumberOfDays());
		if (configuration.isAlwaysDisplayPasswordExpirationWarning()
				|| ttl.getDays() < configuration.getPasswordWarningNumberOfDays()) {
			messages.add(new PasswordExpiringWarningMessageDescriptor(
					"Password expires in {0} days. Please change your password at <href=\"{1}\">{1}</a>", ttl.getDays(),
					configuration.getPasswordPolicyUrl()));
		}
		if (warning.getLoginsRemaining() > 0) {
			messages.add(new DefaultMessageDescriptor("password.expiration.loginsRemaining",
					"You have {0} logins remaining before you MUST change your password.",
					warning.getLoginsRemaining()));

		}
	}
}
