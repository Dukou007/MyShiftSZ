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
import org.apache.commons.lang3.StringUtils;

import com.pax.tms.download.model.PkgFile;
import com.pax.tms.download.model.Terminal;
import com.pax.tms.pxretailer.PxRetailerException;
import com.pax.tms.pxretailer.PxRetailerProtocol;
import com.pax.tms.pxretailer.message.BaseResponse;
import com.pax.tms.pxretailer.message.FileInfo;
import com.pax.tms.pxretailer.message.GetFileInfoResponse;
import com.pax.tms.pxretailer.message.GetFilesInfoRequest;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class GetFileInfoAction extends AbstractDeploymentAction<GetFilesInfoRequest> {

	private static final String ADS_GET_FILES_INFO_ROUTE = "/ads/getFilesInformation";

	@Override
	public void registerAction(Router router, JsonObject config) {
		router.post(ADS_GET_FILES_INFO_ROUTE).handler(createBodyHandler(config));
		router.post(ADS_GET_FILES_INFO_ROUTE).handler(this::handle).failureHandler(this::handleException);
	}

	/**
	 * Handle GETFILESINFORMATION requests
	 * 
	 * @param routingContext
	 */
	@Override
	public void handle(RoutingContext routingContext) {
		GetFilesInfoRequest request = decodeJson(routingContext.getBodyAsString(), GetFilesInfoRequest.class);
		auth(routingContext, request, TerminalActionType.DOWNLOAD);
	}

	@Override
	public void process(RoutingContext routingContext, GetFilesInfoRequest req, Terminal terminal) {
		downloadLimiter.updateDownloadProgress(terminal.getTerminalId());
		FileInfo fileInfo = validateFileInfo(req);
		if (checkDeployment(routingContext, terminal, fileInfo.getDeploymentUUID())) {
			getFileInfo(routingContext, terminal, fileInfo.getFileUUID(), fileInfo.getFileName(),
					pkgFile -> sendFileInfo(routingContext, fileInfo, pkgFile, terminal));
		}
	}

	private void sendFileInfo(RoutingContext routingContext, FileInfo fileInfo, PkgFile pkgFile, Terminal terminal) {
		if (pkgFile == null) {
			logger.debug("deviceType={}, deviceSerialNumber={} - file not found for deployment", terminal.getModelId(),
					terminal.getTerminalSn());
			sendResponse(routingContext,
					createResponse(PxRetailerProtocol.FILE_NOT_FOUND_RESPONSE, "File not found for deployment"));
		} else {
			GetFileInfoResponse response = new GetFileInfoResponse();
			response.addFileInformation(fileInfo.getDeploymentUUID(), getFileUUID(fileInfo, pkgFile),
					pkgFile.getFileName(), (int) pkgFile.getFileSize(), pkgFile.getFileVersion(), pkgFile.getMd5(),
					pkgFile.getSha256());
			sendResponse(routingContext, response);
		}
	}

	private String getFileUUID(FileInfo fileInfo, PkgFile pkgFile) {
		if (StringUtils.isNotEmpty(fileInfo.getFileUUID())) {
			return fileInfo.getFileUUID();
		}
		return pkgFile.getFileId() + "";
	}

	private FileInfo validateFileInfo(GetFilesInfoRequest req) {
		if (CollectionUtils.isEmpty(req.getFilesInformation())) {
			throw new PxRetailerException(PxRetailerProtocol.INVALID_REQUEST_RESPONSE,
					"File information list is empty");
		}

		FileInfo fileInfo = req.getFilesInformation().get(0);

		// Request must contain a deploymentUUID
		if (StringUtils.isEmpty(fileInfo.getDeploymentUUID())) {
			throw new PxRetailerException(PxRetailerProtocol.INVALID_REQUEST_RESPONSE,
					"No deploymentUUID specified in the request");
		}

		String fileUUID = fileInfo.getFileUUID();
		String fileName = fileInfo.getFileName();

		// A File Info Request must specify either a File UUID or a File
		// Name, but not both. Reject a request if it specifies both or
		// neither.
		if (!StringUtils.isEmpty(fileUUID) && !StringUtils.isEmpty(fileName)) {
			throw new PxRetailerException(PxRetailerProtocol.INVALID_REQUEST_RESPONSE,
					"Both fileUUID and FileName specified");
		}

		if (StringUtils.isEmpty(fileUUID) && StringUtils.isEmpty(fileName)) {
			throw new PxRetailerException(PxRetailerProtocol.INVALID_REQUEST_RESPONSE,
					"fileUUID and FileName not specified");
		}

		return fileInfo;
	}

	@Override
	public BaseResponse createResponse(int statusCode, String statusMessage) {
		return new GetFileInfoResponse(statusCode, statusMessage);
	}

}
