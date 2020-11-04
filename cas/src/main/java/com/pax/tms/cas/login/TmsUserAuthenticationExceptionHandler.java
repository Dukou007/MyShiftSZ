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

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.jasig.cas.authentication.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.stereotype.Component;

@Component("tmsUserAuthenticationExceptionHandler")
public class TmsUserAuthenticationExceptionHandler {
	/** State name when no matching exception is found. */
	private static final String UNKNOWN = "UNKNOWN";

	/** Default message bundle prefix. */
	private static final String DEFAULT_MESSAGE_BUNDLE_PREFIX = "authenticationFailure.";

	/** Default list of errors this class knows how to handle. */
	private static final List<Class<? extends Exception>> DEFAULT_ERROR_LIST = new ArrayList<>();

	static {
		DEFAULT_ERROR_LIST.add(javax.security.auth.login.AccountLockedException.class);
		DEFAULT_ERROR_LIST.add(javax.security.auth.login.FailedLoginException.class);
		DEFAULT_ERROR_LIST.add(javax.security.auth.login.CredentialExpiredException.class);
		DEFAULT_ERROR_LIST.add(javax.security.auth.login.AccountNotFoundException.class);
		DEFAULT_ERROR_LIST.add(org.jasig.cas.authentication.AccountDisabledException.class);
		DEFAULT_ERROR_LIST.add(org.jasig.cas.authentication.InvalidLoginLocationException.class);
		DEFAULT_ERROR_LIST.add(org.jasig.cas.authentication.AccountPasswordMustChangeException.class);
		DEFAULT_ERROR_LIST.add(org.jasig.cas.authentication.InvalidLoginTimeException.class);
		DEFAULT_ERROR_LIST.add(OneTimeToTryFailedLoginException.class);
		DEFAULT_ERROR_LIST.add(UserLockedFailedLoginException.class);
		DEFAULT_ERROR_LIST.add(TmsUserAccountPasswordMustChangeException.class);
		DEFAULT_ERROR_LIST.add(TmsUserCredentialExpiredException.class);
		DEFAULT_ERROR_LIST.add(TmsUserFailedLoginException.class);
		DEFAULT_ERROR_LIST.add(TmsUserAccountExpiredException.class);
		DEFAULT_ERROR_LIST.add(TmsUserAccountDisabledException.class);
		DEFAULT_ERROR_LIST.add(TmsUserAccountLockedException.class);
		DEFAULT_ERROR_LIST.add(TmsUserAccountNotFoundException.class);

		DEFAULT_ERROR_LIST.add(LdapAccountDisabledException.class);
		DEFAULT_ERROR_LIST.add(LdapAccountLockedException.class);
		DEFAULT_ERROR_LIST.add(LdapInvalidLoginTimeException.class);
		DEFAULT_ERROR_LIST.add(LdapInvalidLoginLocationException.class);
		DEFAULT_ERROR_LIST.add(LdapAccountPasswordMustChangeException.class);
		DEFAULT_ERROR_LIST.add(LdapCredentialExpiredException.class);
		DEFAULT_ERROR_LIST.add(LdapAccountExpiredException.class);
		DEFAULT_ERROR_LIST.add(javax.security.auth.login.AccountExpiredException.class);
	}

	private final transient Logger logger = LoggerFactory.getLogger(this.getClass());

	/** Ordered list of error classes that this class knows how to handle. */
	@NotNull
	private List<Class<? extends Exception>> errors = DEFAULT_ERROR_LIST;

	/**
	 * String appended to exception class name to create a message bundle key
	 * for that particular error.
	 */
	private String messageBundlePrefix = DEFAULT_MESSAGE_BUNDLE_PREFIX;

	public String handle(final AuthenticationException e, final MessageContext messageContext) {
		if (e != null) {
			final MessageBuilder builder = new MessageBuilder();
			for (final Class<? extends Exception> kind : this.errors) {
				for (final Class<? extends Exception> handlerError : e.getHandlerErrors().values()) {
					if (handlerError != null && handlerError.equals(kind)) {
						final String handlerErrorName = handlerError.getSimpleName();
						final String messageCode = this.messageBundlePrefix + handlerErrorName;
						messageContext.addMessage(builder.error().code(messageCode).build());
						return handlerErrorName;
					}
				}
			}
		}

		final String messageCode = this.messageBundlePrefix + UNKNOWN;
		logger.trace(
				"Unable to translate handler errors of the authentication exception {}. Returning {} by default...", e,
				messageCode);
		messageContext.addMessage(new MessageBuilder().error().code(messageCode).build());
		return UNKNOWN;
	}
}
