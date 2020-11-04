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

import org.apache.commons.collections.CollectionUtils;

import com.pax.tms.deploy.model.DownOrActvStatus;
import com.pax.tms.download.model.Terminal;
import com.pax.tms.pxretailer.PxRetailerException;
import com.pax.tms.pxretailer.PxRetailerProtocol;
import com.pax.tms.pxretailer.message.DeploymentStatus;
import com.pax.tms.pxretailer.message.UpdateDeploymentStatusRequest;
import com.pax.tms.pxretailer.message.UpdateDeploymentStatusResponse;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class UpdateDeploymentStatusAction extends AbstractDeploymentAction<UpdateDeploymentStatusRequest> {

	private static final String ADS_UPDATE_PACKAGES_STATUS_ROUTE = "/ads/updatePackagesDeploymentStatus";

	@Override
	public void registerAction(Router router, JsonObject config) {
		router.post(ADS_UPDATE_PACKAGES_STATUS_ROUTE).handler(createBodyHandler(config));
		router.post(ADS_UPDATE_PACKAGES_STATUS_ROUTE).handler(this::handle).failureHandler(this::handleException);
	}

	/**
	 * Handle UPDATEPACKAGESDEPLOYMENTSTATUS requests
	 * 
	 * @param routingContext
	 * @return
	 */
	@Override
	public void handle(RoutingContext routingContext) {
		UpdateDeploymentStatusRequest req = decodeJson(routingContext.getBodyAsString(),
				UpdateDeploymentStatusRequest.class);

		if (CollectionUtils.isEmpty(req.getDeploymentStatus())) {
			throw new PxRetailerException(PxRetailerProtocol.INVALID_REQUEST_RESPONSE,
					"Deployment status list is empty");
		}

		auth(routingContext, req, TerminalActionType.DOWNLOAD);
	}

	@Override
	public void process(RoutingContext routingContext, UpdateDeploymentStatusRequest req, Terminal terminal) {
		downloadLimiter.completeDownload(terminal.getTerminalId());

		DeploymentStatus deploymentStatus = req.getDeploymentStatus().get(0);
		long deployId = parseDeploymentId(deploymentStatus.getDeploymentUUID());
		DownOrActvStatus downloadStatus = PxRetailerProtocol.downOrActvStatus(deploymentStatus.getDownloadStatus());
		DownOrActvStatus activationStatus = PxRetailerProtocol.downOrActvStatus(deploymentStatus.getActivationStatus());
		String activationCode = deploymentStatus.getActivationCode();
		updateDeploymentStatus(routingContext, terminal, deployId, downloadStatus, activationStatus,activationCode);
	}

	private void updateDeploymentStatus(RoutingContext routingContext, Terminal terminal, long deployId,
			DownOrActvStatus downloadStatus, DownOrActvStatus activationStatus,String activationCode) {
		terminalManager.updateDeploymentStatus(terminal, deployId, downloadStatus, activationStatus, activationCode, res -> {
			if (res.failed()) {
				logger.error("deviceType={}, deviceSerialNumber={} - failed to update deployment status", res.cause(),
						terminal.getModelId(), terminal.getTerminalSn());
				routingContext.fail(res.cause());
			} else {
				sendResponse(routingContext, new UpdateDeploymentStatusResponse());
			}
		});
	}

	@Override
	public UpdateDeploymentStatusResponse createResponse(int statusCode, String statusMessage) {
		return new UpdateDeploymentStatusResponse(statusCode, statusMessage);
	}
}
