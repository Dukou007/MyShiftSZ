package com.pax.tms.cas.login;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.logout.LogoutManager;
import org.jasig.cas.ticket.Ticket;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.jasig.cas.ticket.registry.TicketRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class TmsUserStatusChangedMessageHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(TmsUserStatusChangedMessageHandler.class);

	@Autowired
	@Qualifier("ticketRegistry")
	private TicketRegistry ticketRegistry;

	@Autowired
	@Qualifier("logoutManager")
	private LogoutManager logoutManager;

	@Autowired
	private TmsAuthenticationHandler authenticationHandler;

	@SuppressWarnings("rawtypes")
	public void handleMessage(Object message) {
		LOGGER.debug("TMS user status changed: {}", message);
		if (message instanceof Map) {
			String username = (String) ((Map) message).get("username");
			if (username != null && !username.isEmpty()) {
				handleUserStatusChanged(username, (String) ((Map) message).get("action"));
			}
		}
	}

	private void handleUserStatusChanged(String username, String action) {
		Collection<Ticket> tickets = ticketRegistry.getTickets();
		for (final Ticket ticket : tickets) {
			if (ticket instanceof TicketGrantingTicket) {
				Authentication auth = ((TicketGrantingTicket) ticket).getAuthentication();
				Principal principal = auth.getPrincipal();
				if (principal.getId().equalsIgnoreCase(username)) {
					handleUserStatusChanged(username, action, (TicketGrantingTicket) ticket, principal);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void handleUserStatusChanged(String username, String action, TicketGrantingTicket ticket,
			Principal principal) {
		if ("deactive".equalsIgnoreCase(action) || "delete".equalsIgnoreCase(action)) {
			logoutManager.performLogout((TicketGrantingTicket) ticket);
			ticketRegistry.deleteTicket(ticket.getId());
		} else if ("edit".equalsIgnoreCase(action)) {
			Map<String, Object> newAttributes = authenticationHandler.getPersonAttributes(username);
			Map<String, Object> cachedAttributes = principal.getAttributes();
			if (isRoleChanged((List<String>) newAttributes.get("TMS_ROLES"),
					(List<String>) cachedAttributes.get("TMS_ROLES"))
					|| isRoleChanged((List<String>) newAttributes.get("PXDESIGNER_ROLES"),
							(List<String>) cachedAttributes.get("PXDESIGNER_ROLES"))) {
				logoutManager.performLogout((TicketGrantingTicket) ticket);
				ticketRegistry.deleteTicket(ticket.getId());
			}
		}
	}

	private boolean isRoleChanged(List<String> roles, List<String> cachedRoles) {
		boolean roleChanged = false;
		if ((roles == null || roles.isEmpty())) {
			roleChanged = !(cachedRoles == null || cachedRoles.isEmpty());
		} else {
			if (cachedRoles == null || cachedRoles.isEmpty()) {
				roleChanged = true;
			} else {
				Set<String> newRoleSet = new HashSet<>(roles);
				Set<String> cachedRoleSet = new HashSet<>(cachedRoles);
				roleChanged = !newRoleSet.equals(cachedRoleSet);
			}
		}
		return roleChanged;
	}
}
