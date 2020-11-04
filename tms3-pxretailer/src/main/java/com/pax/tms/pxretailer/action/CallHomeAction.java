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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pax.tms.download.HmsLimiter;
import com.pax.tms.download.model.Terminal;
import com.pax.tms.pxretailer.PxRetailerConfig;
import com.pax.tms.pxretailer.message.CallHomeRequest;
import com.pax.tms.pxretailer.message.CallHomeResponse;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class CallHomeAction extends AbstractTerminalAction<CallHomeRequest> {

	private static final String CHS_ROUTE = "/chs/request";

	private String chsAddress;
	private String hmsAddress;
	private String adsAddress;

	private int chsDelay;
	private int hmsDelay;
	private int adsDelay;
	private long updateTerminalTimeInterval;
//	private boolean syncToServerTime; 

	protected HmsLimiter hmsLimiter;

	@Override
	public void registerAction(Router router, JsonObject config) {
		chsAddress = config.getString("tms.chsAddress");
		hmsAddress = config.getString("tms.hmsAddress");
		adsAddress = config.getString("tms.adsAddress");

		router.post(CHS_ROUTE).handler(createBodyHandler(config));
		router.post(CHS_ROUTE).handler(this::handle).failureHandler(this::handleException);
	}

	@Autowired
	public void setConfig(PxRetailerConfig globalConfig) {
		chsDelay = globalConfig.getChsDelay();
		hmsDelay = globalConfig.getHmsDelay();
		adsDelay = globalConfig.getAdsDelay();
		updateTerminalTimeInterval = globalConfig.getUpdateTerminalTimeInterval();
		//syncToServerTime = globalConfig.isSyncToServerTime(); 根据终端属性来处理
	}

	@Autowired
	public void setHmsLimiter(HmsLimiter hmsLimiter) {
		this.hmsLimiter = hmsLimiter;
	}

	/**
	 * Handle CALLHOME Requests
	 * 
	 * @param routingContext
	 */
	@Override
	public void handle(RoutingContext routingContext) {
		CallHomeRequest req = decodeJson(routingContext.getBodyAsString(), CallHomeRequest.class);
		auth(routingContext, req, TerminalActionType.CALLHOME);
	}

	@Override
	public void process(RoutingContext routingContext, CallHomeRequest req, Terminal terminal) {
		CallHomeResponse response = new CallHomeResponse();
		if (req.isStateChanged() || terminal.isNeedHms()) {
			if (hmsLimiter.startHms(terminal.getTerminalId())) {
				response.addService("hms", hmsAddress, hmsDelay);
				sendResponse(routingContext, terminal, response);
			} else {
				terminalManager.setNeedHmsFlag(terminal, res -> {
					if (res.failed()) {
						logger.error("deviceType={}, deviceSerialNumber={} - failed to save needHms flag", res.cause(),
								req.getDeviceType(), req.getDeviceSerialNumber());
						routingContext.fail(res.cause());
					} else {
						response.addService("chs", chsAddress, chsDelay);
						sendResponse(routingContext, terminal, response);
					}
				});
			}
		} else if (terminal.hasScheduledPackage() && downloadLimiter.startDownload(terminal.getTerminalId())) {
			response.addService("ads", adsAddress, adsDelay);
			sendResponse(routingContext, terminal, response);
		} else {
			response.addService("chs", chsAddress, chsDelay);
			sendResponse(routingContext, terminal, response);
		}
	}

	private void sendResponse(RoutingContext routingContext, Terminal terminal, CallHomeResponse response) {
		if (terminal.isSyncToServerTime()) {
			updateTerminalTime(terminal, response);
		}

		sendResponse(routingContext, response);
	}

	private void updateTerminalTime(Terminal terminal, CallHomeResponse response) {
		if (!StringUtils.isEmpty(terminal.getTimeZone())) {
			if ((terminal.getLastSyncTime() == null) || (Math.abs(
					System.currentTimeMillis() - terminal.getLastSyncTime().getTime()) >= updateTerminalTimeInterval)) {
				TimeZone timeZone = TimeZone.getTimeZone(terminal.getTimeZone());
				setTerminalTime(terminal, response, timeZone, terminal.isUseDaylightTime());
			} else if (terminal.isUseDaylightTime()) {
				TimeZone timeZone = TimeZone.getTimeZone(terminal.getTimeZone());
				if (timeZone.inDaylightTime(terminal.getLastSyncTime()) ^ timeZone.inDaylightTime(new Date())) {
					setTerminalTime(terminal, response, timeZone, terminal.isUseDaylightTime());
				}
			}
		}
	}

	private void setTerminalTime(Terminal terminal, CallHomeResponse response, TimeZone timeZone,
			boolean useDaylightTime) {
		logger.debug("deviceType={}, deviceSerialNumber={} - sync terminal time", terminal.getModelId(),
				terminal.getTerminalSn());
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		long cur = System.currentTimeMillis();
		Date date = null;
		if (!useDaylightTime && timeZone.inDaylightTime(new Date())) {
			date = new Date(cur + timeZone.getRawOffset());
			format.setTimeZone(TimeZone.getTimeZone("UTC"));
		} else {
			date = new Date(cur);
			format.setTimeZone(timeZone);
		}
		response.setTerminalTime(format.format(date));
		terminalManager.updateLasSyncTime(terminal, cur);
	}

	@Override
	public CallHomeResponse createResponse(int statusCode, String statusMessage) {
		return new CallHomeResponse(statusCode, statusMessage);
	}

}
