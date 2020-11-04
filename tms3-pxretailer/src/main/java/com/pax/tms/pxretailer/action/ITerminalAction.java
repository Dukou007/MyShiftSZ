package com.pax.tms.pxretailer.action;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public interface ITerminalAction {

	/**
	 * register a terminal action to the router
	 * 
	 * @param router
	 *            receive HTTP request and route it to terminal action
	 * 
	 * @param config
	 *            the configuration of the HTTP server
	 */
	void registerAction(Router router, JsonObject config);
}
