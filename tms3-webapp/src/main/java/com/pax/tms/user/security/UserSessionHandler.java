package com.pax.tms.user.security;

import javax.servlet.http.HttpSession;

import org.jasig.cas.client.session.HashMapBackedSessionMappingStorage;
import org.jasig.cas.client.session.SessionMappingStorage;
import org.jasig.cas.client.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserSessionHandler {

	private static UserSessionHandler INSTANCE = new UserSessionHandler();

	private final Logger logger = LoggerFactory.getLogger(getClass());

	/** Mapping of user IDs and session IDs to HTTP sessions */
	private SessionMappingStorage sessionMappingStorage = new HashMapBackedSessionMappingStorage();

	public void login(final String userId, final HttpSession session) {
		if (session == null) {
			return;
		}

		sessionMappingStorage.removeBySessionById(session.getId());
		sessionMappingStorage.addSessionById(userId, session);
	}

	public void logout(String userId) {
		if (CommonUtils.isNotBlank(userId)) {
			final HttpSession session = this.sessionMappingStorage.removeSessionByMappingId(userId);
			if (session != null) {
				try {
					session.invalidate();
				} catch (final IllegalStateException e) {
					logger.debug("Error invalidating session.", e);
				}
			}
		}
	}

	public SessionMappingStorage getSessionMappingStorage() {
		return sessionMappingStorage;
	}

	public void setSessionMappingStorage(SessionMappingStorage sessionMappingStorage) {
		this.sessionMappingStorage = sessionMappingStorage;
	}

	public static UserSessionHandler getInstance() {
		return INSTANCE;
	}
}
