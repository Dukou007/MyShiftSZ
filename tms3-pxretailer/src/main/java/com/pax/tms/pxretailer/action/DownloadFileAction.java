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

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pax.tms.download.FileDataStore;
import com.pax.tms.download.model.Terminal;
import com.pax.tms.pxretailer.FileSender;
import com.pax.tms.pxretailer.PxRetailerConfig;
import com.pax.tms.pxretailer.PxRetailerException;
import com.pax.tms.pxretailer.PxRetailerProtocol;
import com.pax.tms.pxretailer.message.DownloadFileRequest;
import com.pax.tms.pxretailer.message.DownloadFileResponse;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

@Service
public class DownloadFileAction extends AbstractDeploymentAction<DownloadFileRequest> {

	private static final String ADS_DOWNLOAD_FILE_ROUTE = "/ads/downloadFile";

	private FileDataStore fileDataStore;

	private int fileDownloadChunkSize;

	@Autowired
	private PxRetailerConfig configs;

	@Override
	public void registerAction(Router router, JsonObject config) {
		fileDownloadChunkSize = config.getInteger("tms.fileDownloadChunkSize");

		router.get(ADS_DOWNLOAD_FILE_ROUTE).handler(createBodyHandler(config));
		router.get(ADS_DOWNLOAD_FILE_ROUTE).handler(this::handle).failureHandler(this::handleException);
	}

	public void setFileDataStore(FileDataStore fileDataStore) {
		this.fileDataStore = fileDataStore;
	}

	/**
	 * Handle download file requests
	 * 
	 * @param routingContext
	 */
	@Override
	public void handle(RoutingContext routingContext) {
		DownloadFileRequest request = parseRequestParameters(routingContext);
		auth(routingContext, request, TerminalActionType.DOWNLOAD);
	}

	@Override
	public void process(RoutingContext routingContext, DownloadFileRequest req, Terminal terminal) {
		downloadLimiter.updateDownloadProgress(terminal.getTerminalId());
		if (checkDeployment(routingContext, terminal, req.getDeploymentUUID())) {
			getFileInfo(routingContext, terminal, req.getFileUUID(), null, pkgFile -> {
				if (pkgFile.getFileSize() <= 0) {
					throw new PxRetailerException(PxRetailerProtocol.INVALID_FILE_SIZE_RESPONSE, "Fize size is 0");
				}
				new FileSender(terminalManager, downloadLimiter, fileDataStore, configs.getUpdateOnlineStatusInterval())
						.sendFile(routingContext, terminal, req.getFileUUID(), pkgFile.getFileName(),
								pkgFile.getFilePath(), pkgFile.getFileSize(), fileDownloadChunkSize);
			});
		}
	}

	private DownloadFileRequest parseRequestParameters(RoutingContext routingContext) {
		String deviceType = "";
		String deviceSerialNumber = "";
		String deploymentUUID = "";
		String fileUUID = "";

		String queryParams = routingContext.request().query();
		logger.debug("Received request from terminal {}", queryParams);
		String[] params = queryParams.split("\\?");
		for (String param : params) {
			if (param.contains("deviceType")) {
				deviceType = param.substring(param.indexOf('=') + 1);
			}

			if (param.contains("deviceSerialNumber")) {
				deviceSerialNumber = param.substring(param.indexOf('=') + 1);
			}

			if (param.contains("deploymentUUID")) {
				deploymentUUID = param.substring(param.indexOf('=') + 1);
			}

			if (param.contains("fileUUID")) {
				fileUUID = param.substring(param.indexOf('=') + 1);
			}
		}

		if (StringUtils.isEmpty(deploymentUUID)) {
			throw new PxRetailerException(PxRetailerProtocol.INVALID_REQUEST_RESPONSE,
					"No deploymentUUID specified in the request");
		}

		if (StringUtils.isEmpty(fileUUID)) {
			throw new PxRetailerException(PxRetailerProtocol.INVALID_REQUEST_RESPONSE,
					"No FileUUID specified in the request");
		}

		DownloadFileRequest downloadFileRequest = new DownloadFileRequest(deviceType, deviceSerialNumber);
		downloadFileRequest.setDeploymentUUID(deploymentUUID);
		downloadFileRequest.setFileUUID(fileUUID);
		downloadFileRequest.validateInput();

		return downloadFileRequest;
	}

	@Override
	public DownloadFileResponse createResponse(int statusCode, String statusMessage) {
		return new DownloadFileResponse(statusCode, statusMessage);
	}

}
