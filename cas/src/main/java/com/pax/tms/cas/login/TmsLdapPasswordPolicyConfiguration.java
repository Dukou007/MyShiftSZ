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

import org.jasig.cas.authentication.support.AccountStateHandler;
import org.jasig.cas.authentication.support.LdapPasswordPolicyConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("tmsLdapPasswordPolicyConfiguration")
public class TmsLdapPasswordPolicyConfiguration extends LdapPasswordPolicyConfiguration {

	/**
	 * Sets the directory-specific account state handler. If none is defined,
	 * account state handling is disabled, which is the default behavior.
	 *
	 * @param accountStateHandler
	 *            Account state handler.
	 */
	@Autowired
	@Override
	public void setAccountStateHandler(
			@Qualifier("tmsLdapAccountStateHandler") final AccountStateHandler accountStateHandler) {
		super.setAccountStateHandler(accountStateHandler);
	}

	@Autowired
	@Override
	public void setAlwaysDisplayPasswordExpirationWarning(
			@Value("${tms.ldap.password.policy.warnAll:false}") final boolean alwaysDisplayPasswordExpirationWarning) {
		super.setAlwaysDisplayPasswordExpirationWarning(alwaysDisplayPasswordExpirationWarning);
	}

	@Autowired
	@Override
	public void setPasswordPolicyUrl(@Value("${tms.ldap.password.policy.url:#}") final String passwordPolicyUrl) {
		super.setPasswordPolicyUrl(passwordPolicyUrl);
	}

	@Autowired
	@Override
	public void setPasswordWarningNumberOfDays(@Value("${tms.ldap.password.policy.warningDays:30}") final int days) {
		super.setPasswordWarningNumberOfDays(days);
	}
}
