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
package com.pax.tms.pxretailer.action;

import java.io.File;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pax.common.util.DateTimeUtils;
import com.pax.tms.download.model.Terminal;
import com.pax.tms.download.model.TerminalStatus;
import com.pax.tms.download.service.TerminalDownloadService;
import com.pax.tms.pxretailer.PxRetailerConfig;
import com.pax.tms.pxretailer.PxRetailerDownloadLimiter;
import com.pax.tms.pxretailer.PxRetailerException;
import com.pax.tms.pxretailer.PxRetailerProtocol;
import com.pax.tms.pxretailer.PxRetailerTerminalManager;
import com.pax.tms.pxretailer.message.BaseRequest;
import com.pax.tms.pxretailer.message.BaseResponse;

import io.vertx.core.Future;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public abstract class AbstractTerminalAction<T extends BaseRequest> implements ITerminalAction {

	private static final String UNKNOWN = "unknown";

	protected PxRetailerTerminalManager terminalManager;

	protected PxRetailerDownloadLimiter downloadLimiter;

	protected TerminalDownloadService terminalStorageService;

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	protected PxRetailerConfig config;

	@Autowired
	public void setDownloadLimiter(PxRetailerDownloadLimiter downloadLimiter) {
		this.downloadLimiter = downloadLimiter;
	}

	@Autowired
	public void setTerminalStorageService(TerminalDownloadService terminalStorageService) {
		this.terminalStorageService = terminalStorageService;
	}

	public void setTerminalManager(PxRetailerTerminalManager terminalManager) {
		this.terminalManager = terminalManager;
	}

	public void setConfig(PxRetailerConfig config) {
		this.config = config;
	}

	protected BodyHandler createBodyHandler(JsonObject config) {
		String uploadsDirectory = null;
		String tmsHome = System.getProperty("tms.home");
		if (StringUtils.isNotEmpty(tmsHome)) {
			uploadsDirectory = new File(tmsHome, BodyHandler.DEFAULT_UPLOADS_DIRECTORY).getPath();
		} else {
			uploadsDirectory = BodyHandler.DEFAULT_UPLOADS_DIRECTORY;
		}

		BodyHandler bodyHandler = BodyHandler.create(uploadsDirectory);
		bodyHandler.setBodyLimit(config.getLong("bodyLimit", 1 * 1024 * 1024L));
		bodyHandler.setDeleteUploadedFilesOnEnd(true);
		return bodyHandler;
	}

	protected T decodeJson(String json, Class<T> clazz) {
		T obj = Json.decodeValue(json, clazz);
		obj.validateInput();
		return obj;
	}

	protected <R extends BaseResponse> void sendResponse(RoutingContext routingContext, R t) {
		if (t == null) {
			routingContext.response().close();
			return;
		}

		// close the underlying connection when response is written
		routingContext.response().bodyEndHandler(v -> routingContext.response().close());
		String json = Json.encode(t);
		routingContext.response().putHeader("content-type", "application/json").end(json);
		Long requestTime = routingContext.get("REQUEST_TIME");
		T req = routingContext.get("REQUEST_ENTITY");
		if (requestTime != null && req != null) {
			if ((System.currentTimeMillis() - requestTime) >= config.getMaxHeartBeatInterval()) {
				logger.warn(
						"deviceType={}, deviceSerialNumber={} - {}, Request Time {}, Response Time {}, Spend Time: {}",
						req.getDeviceType(), req.getDeviceSerialNumber(), this.getClass().getSimpleName(),
						new Date(requestTime), new Date(), System.currentTimeMillis() - requestTime);
			} else {
				logger.debug(
						"deviceType={}, deviceSerialNumber={} - {}, Request Time:{}, Response Time:{}, Spend Time: {}",
						req.getDeviceType(), req.getDeviceSerialNumber(), this.getClass().getSimpleName(),
						new Date(requestTime), new Date(), System.currentTimeMillis() - requestTime);
			}
		}
	}

	protected void handleException(RoutingContext routingContext) {
		T req = routingContext.get("REQUEST_ENTITY");
		Throwable e = routingContext.failure();
		if (e == null) {
			if (req == null) {
				logger.error("Failed to process request {}, Server Error", this.getClass().getSimpleName());
			} else {
				logger.error("deviceType={}, deviceSerialNumber={} - failed to process request {}", req.getDeviceType(),
						req.getDeviceSerialNumber(), this.getClass().getSimpleName());
			}

			sendResponse(routingContext, createResponse(PxRetailerProtocol.SERVER_ERROR_RESPONSE, "Server Error"));
		} else {
			if (req == null) {
				logger.error("Failed to process request {}, {}", this.getClass().getSimpleName(),e.getMessage());
			} else {
				logger.error("deviceType={}, deviceSerialNumber={} - failed to process request {}", 
						req.getDeviceType(), req.getDeviceSerialNumber(), this.getClass().getSimpleName());
			}

			if (e instanceof DecodeException) {
				sendResponse(routingContext,
						createResponse(PxRetailerProtocol.INVALID_REQUEST_RESPONSE, e.getMessage()));
			} else if (e instanceof PxRetailerException) {
				sendResponse(routingContext, createResponse(((PxRetailerException) e).getStatusCode(), e.getMessage()));
			} else {
				sendResponse(routingContext, createResponse(PxRetailerProtocol.SERVER_ERROR_RESPONSE, e.getMessage()));
			}
		}
	}

	private String remoteAddress(RoutingContext routingContext) {
		HttpServerRequest request = routingContext.request();

		String ip = request.getHeader("X-Forwarded-For");

		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-real-ip");
		}

		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}

		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}

		if (StringUtils.isNoneBlank(ip)) {
			return ip.split(",")[0];
		}

		return request.remoteAddress().host();
	}

	protected void auth(RoutingContext routingContext, T req, TerminalActionType actionType) {
		routingContext.put("REQUEST_TIME", System.currentTimeMillis());
		routingContext.put("REQUEST_ENTITY", req);
		logger.debug("deviceType={}, deviceSerialNumber={} - receive request {}", req.getDeviceType(),
				req.getDeviceSerialNumber(), this.getClass().getSimpleName());
		terminalManager.getTerminal(req.getDeviceType(), req.getDeviceSerialNumber(), actionType, res -> {
			if (res.failed()) {
				logger.error("deviceType={}, deviceSerialNumber={} - failed to get terminal info", res.cause(),
						req.getDeviceType(), req.getDeviceSerialNumber());
				routingContext.fail(res.cause());
			} else {
				Terminal terminal = res.result();
				if (terminal == null) {
					logger.debug("deviceType={}, deviceSerialNumber={} - terminal is not registerd",
							req.getDeviceType(), req.getDeviceSerialNumber());
					terminalManager.addUnregisteredTerminal(req.getDeviceType(), req.getDeviceSerialNumber(),
							remoteAddress(routingContext), new Date(), Future.<Void>future()
									.setHandler(addUnregTrmRes -> sendResponseToUnregisteredTerminal(routingContext)));
				} else if (terminal.isUnregistered()) {
					logger.debug("deviceType={}, deviceSerialNumber={} - terminal is not registerd",
							req.getDeviceType(), req.getDeviceSerialNumber());
					terminalManager.updateUnregisteredTerminal(req.getDeviceType(), req.getDeviceSerialNumber(),
							remoteAddress(routingContext), new Date(), Future.<Void>future().setHandler(
									updateUnregTrmRes -> sendResponseToUnregisteredTerminal(routingContext)));
				} else if (terminal.isDisabled()) {
					logger.debug("deviceType={}, deviceSerialNumber={} - terminal has been disabled",
							req.getDeviceType(), req.getDeviceSerialNumber());
					sendResponseToDisabledTerminal(routingContext);
				} else {
					updateLastAccessInfo(routingContext, req, terminal);
				}
			}
		});
	}

	private void updateLastAccessInfo(RoutingContext routingContext, T req, Terminal terminal) {
		Date accessTime = req.getRequestTime();
		//根据终端时区获取本次accesss时间和上次accesss时间
		String timeZone = terminal.getTimeZone();
		logger.debug("terminalLastAccessTime",terminal.getLastAccessTime());
        Date terminalLastAccessTime = DateTimeUtils.getTimeZoneDate(terminal.getLastAccessTime(), timeZone);
        String terminalLastAccessDate = DateTimeUtils.format(terminalLastAccessTime, "yyyy-mm-dd");
        Date newAccessTime = DateTimeUtils.getTimeZoneDate(accessTime, timeZone);
        String newAccessDate = DateTimeUtils.format(newAccessTime, "yyyy-mm-dd");
        
		terminalManager.updateLastAccessTime(terminal, accessTime);

		String sourceIp = remoteAddress(routingContext);
		if (terminal.isOffline()) {
			logger.debug("deviceType={}, deviceSerialNumber={} - terminal online", req.getDeviceType(),
					req.getDeviceSerialNumber());
			terminal.setNeedHms(true);
			terminalManager.setTerminalOnline(terminal, sourceIp, accessTime, res -> {
				if (res.failed()) {
					logger.error("deviceType={}, deviceSerialNumber={} - failed to set terminal online", res.cause(),
							req.getDeviceType(), req.getDeviceSerialNumber());
					routingContext.fail(res.cause());
				} else {
					terminal.setLastAccessTime(accessTime);
					terminal.setLastSourceIp(sourceIp);
					terminal.setOnline(TerminalStatus.ONLINE_STATUS);
					process(routingContext, req, terminal);
				}
			});
		} else {
		    //如果终端是当天第一次上送chs消息，则需要发送hms消息
		    if(StringUtils.equals(terminalLastAccessDate, newAccessDate)){
		        terminal.setNeedHms(true);
		    }
		    
			terminal.setLastAccessTime(accessTime);
			terminal.setLastSourceIp(sourceIp);
			process(routingContext, req, terminal);
		}
	}

	private void sendResponseToUnregisteredTerminal(RoutingContext routingContext) {
		this.sendResponse(routingContext, this.createResponse(PxRetailerProtocol.INVALID_REQUEST_RESPONSE,
				"Terminal is not registerd or has been disabled"));
	}

	private void sendResponseToDisabledTerminal(RoutingContext routingContext) {
		this.sendResponse(routingContext, this.createResponse(PxRetailerProtocol.INVALID_REQUEST_RESPONSE,
				"Terminal is not registerd or has been disabled"));
	}

	public abstract void handle(RoutingContext routingContext);

	public abstract void process(RoutingContext routingContext, T req, Terminal terminal);

	public abstract BaseResponse createResponse(int statusCode, String statusMessage);

}
