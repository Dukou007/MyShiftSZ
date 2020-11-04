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
package com.pax.tms.schedule;

import static org.pac4j.core.util.CommonHelper.assertNotBlank;
import static org.pac4j.core.util.CommonHelper.assertNotNull;
import static org.pac4j.core.util.CommonHelper.assertTrue;

import org.pac4j.cas.credentials.CasCredentials;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.engine.DefaultCallbackLogic;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.http.HttpActionAdapter;
import org.pac4j.core.profile.CommonProfile;

import io.buji.pac4j.context.ShiroContext;

public class CasCallbackLogic extends DefaultCallbackLogic<Object, ShiroContext> {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object perform(final ShiroContext context, final Config config,
			final HttpActionAdapter<Object, ShiroContext> httpActionAdapter, final String inputDefaultUrl,
			final Boolean inputMultiProfile, final Boolean inputRenewSession) {

		logger.debug("=== CALLBACK ===");

		// default values
		final String defaultUrl;
		if (inputDefaultUrl == null) {
			defaultUrl = Pac4jConstants.DEFAULT_URL_VALUE;
		} else {
			defaultUrl = inputDefaultUrl;
		}
		final boolean multiProfile;
		if (inputMultiProfile == null) {
			multiProfile = false;
		} else {
			multiProfile = inputMultiProfile;
		}
		final boolean renewSession;
		if (inputRenewSession == null) {
			renewSession = true;
		} else {
			renewSession = inputRenewSession;
		}

		// checks
		assertNotNull("context", context);
		assertNotNull("config", config);
		assertNotNull("httpActionAdapter", httpActionAdapter);
		assertNotBlank(Pac4jConstants.DEFAULT_URL, defaultUrl);
		final Clients clients = config.getClients();
		assertNotNull("clients", clients);

		// logic
		final Client client = clients.findClient(context);
		logger.debug("client: {}", client);
		assertNotNull("client", client);
		assertTrue(client instanceof IndirectClient, "only indirect clients are allowed on the callback url");

		HttpAction action;
		try {
			final Credentials credentials = client.getCredentials(context);
			logger.debug("credentials: {}", credentials);

			final CommonProfile profile = client.getUserProfile(credentials, context);

			profile.addAttribute("SERVICE_TICKET", ((CasCredentials) credentials).getServiceTicket());

			logger.debug("profile: {}", profile);
			saveUserProfile(context, profile, multiProfile, renewSession);
			action = redirectToOriginallyRequestedUrl(context, defaultUrl);

		} catch (final HttpAction e) {
			logger.debug("extra HTTP action required in callback: {}", e.getCode());
			action = e;
		}

		return httpActionAdapter.adapt(action.getCode(), context);
	}
}
