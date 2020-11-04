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

import com.pax.tms.download.model.Deployment;
import com.pax.tms.download.model.Terminal;
import com.pax.tms.pxretailer.PxRetailerProtocol;
import com.pax.tms.pxretailer.message.GetScheduledPackagesRequest;
import com.pax.tms.pxretailer.message.GetScheduledPackagesResponse;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class GetScheduledPackageAction extends AbstractTerminalAction<GetScheduledPackagesRequest> {

	private static final String ADS_GET_SCHEDULED_PACKAGES_ROUTE = "/ads/getScheduledPackages";

	private final SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyyMMddHHmmss");

	@Override
	public void registerAction(Router router, JsonObject config) {
		router.post(ADS_GET_SCHEDULED_PACKAGES_ROUTE).handler(createBodyHandler(config));
		router.post(ADS_GET_SCHEDULED_PACKAGES_ROUTE).handler(this::handle).failureHandler(this::handleException);
	}

	@Override
	public void handle(RoutingContext routingContext) {
		GetScheduledPackagesRequest request = decodeJson(routingContext.getBodyAsString(),
				GetScheduledPackagesRequest.class);
		auth(routingContext, request, TerminalActionType.DOWNLOAD);
	}

	@Override
	public void process(RoutingContext routingContext, GetScheduledPackagesRequest req, Terminal terminal) {
		downloadLimiter.updateDownloadProgress(terminal.getTerminalId());
		getScheduledPackage(routingContext, terminal);
	}

	private void getScheduledPackage(RoutingContext routingContext, Terminal terminal) {
		terminalManager.getScheduledPackage(terminal, res -> {
			if (res.failed()) {
				logger.error("deviceType={}, deviceSerialNumber={} - failed to get scheduled package", res.cause(),
						terminal.getModelId(), terminal.getTerminalSn());
				sendResponse(routingContext,
						createResponse(PxRetailerProtocol.INVALID_REQUEST_RESPONSE, "Failed to get scheduled package"));
			} else {
				Deployment deployment = res.result();
				if (deployment == null || !deployment.isInEffect()) {
					sendResponse(routingContext,
							createResponse(PxRetailerProtocol.DEPLOYMENT_NOT_FOUND_RESPONSE, "No scheduled packages"));
				} else {
					startDownload(routingContext, terminal, deployment);
				}
			}
		});
	}

	private void startDownload(RoutingContext routingContext, Terminal terminal, Deployment deployment) {
		routingContext.vertx().executeBlocking(fut -> {
			logger.debug(
					"deviceType={}, deviceSerialNumber={} - start downloading and update download status in database",
					terminal.getModelId(), terminal.getTerminalSn());
			terminalStorageService.startDownload(terminal, deployment, new Date());
			fut.complete();
		}, false, res -> {
			if (res.failed()) {
				logger.error(
						"deviceType={}, deviceSerialNumber={} - failed to start downloading and update download status in database",
						res.cause(), terminal.getModelId(), terminal.getTerminalSn());
				sendResponse(routingContext,
						createResponse(PxRetailerProtocol.INVALID_REQUEST_RESPONSE, "Failed to start downloading"));
			} else {
				GetScheduledPackagesResponse response = new GetScheduledPackagesResponse();
				// Set the deployment info in the response message.
				response.addPackageInformation(deployment.getDeployId().toString(), deployment.getPkgName(),
						deployment.getPkgVersion(), "PKG" + deployment.getPkgId(),
						formatActivationTime(deployment.getActvStartTm(), terminal));
				sendResponse(routingContext, response);
			}
		});
	}

	private String formatActivationTime(Date activationTimeInput, Terminal terminal) {
		Date activationTime = activationTimeInput;
		if (activationTime == null) {
			return "NOW";
		} else {
			return processTimeZone(activationTime, terminal);

		}
	}

	private String processTimeZone(Date activationTime, Terminal terminal) {
		if(StringUtils.isEmpty(terminal.getTimeZone())){
			return null;
		}
		TimeZone timeZone = TimeZone.getTimeZone(terminal.getTimeZone());
		if (!terminal.isUseDaylightTime() && timeZone.inDaylightTime(new Date())) {
			activationTime = new Date(activationTime.getTime() + timeZone.getRawOffset());
			datetimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		} else {
		
			datetimeFormat.setTimeZone(timeZone);
		}

		return datetimeFormat.format(activationTime);
	}

	@Override
	public GetScheduledPackagesResponse createResponse(int statusCode, String statusMessage) {
		return new GetScheduledPackagesResponse(statusCode, statusMessage);
	}

}
