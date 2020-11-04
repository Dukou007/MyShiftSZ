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

import com.pax.tms.download.PackageManager;
import com.pax.tms.download.model.PkgFile;
import com.pax.tms.download.model.Terminal;
import com.pax.tms.pxretailer.PxRetailerException;
import com.pax.tms.pxretailer.PxRetailerProtocol;
import com.pax.tms.pxretailer.message.BaseRequest;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public abstract class AbstractDeploymentAction<T extends BaseRequest> extends AbstractTerminalAction<T> {

	protected static final String FILE_NOTE_FOUND_ERR_MSG = "File not found for deployment";
	protected static final String DEPLOYMENT_NOT_FOUND_ERR_MSG = "Deployment UUID not found";

	protected PackageManager packageManager;

	public AbstractDeploymentAction() {
	}

	public void setPackageManager(PackageManager packageManager) {
		this.packageManager = packageManager;
	}

	protected long getFileId(String fileUUID) {
		try {
			return Long.parseLong(fileUUID);
		} catch (NumberFormatException e) {
			throw new PxRetailerException(PxRetailerProtocol.FILE_NOT_FOUND_RESPONSE, FILE_NOTE_FOUND_ERR_MSG);
		}
	}

	protected long parseDeploymentId(String deploymentUUID) {
		try {
			return Long.parseLong(deploymentUUID);
		} catch (NumberFormatException e) {
			throw new PxRetailerException(PxRetailerProtocol.DEPLOYMENT_NOT_FOUND_RESPONSE,
					DEPLOYMENT_NOT_FOUND_ERR_MSG);
		}
	}

	protected boolean checkDeployment(RoutingContext routingContext, Terminal terminal, String deploymentUUID) {
		long deploymentId = parseDeploymentId(deploymentUUID);
		return checkDeployment(routingContext, terminal, deploymentId);
	}

	protected boolean checkDeployment(RoutingContext routingContext, Terminal terminal, long deploymentId) {
		if (terminal.getDeployId() == null || terminal.getDeployId() != deploymentId) {
			logger.warn("deviceType={}, deviceSerialNumber={} - deployment {} is invalid or not active",
					terminal.getModelId(), terminal.getTerminalSn(), deploymentId);
			sendResponse(routingContext,
					createResponse(PxRetailerProtocol.DEPLOYMENT_NOT_FOUND_RESPONSE, DEPLOYMENT_NOT_FOUND_ERR_MSG));
			return false;
		}
		return true;
	}

	protected void getFileInfo(RoutingContext routingContext, Terminal terminal, String fileUUID, String fileName,
			Handler<PkgFile> handler) {
		if (StringUtils.isNotEmpty(fileUUID)) {
			getFileInfoByFileUUID(routingContext, terminal, fileUUID, handler);
		} else {
			getProgramFileByName(routingContext, terminal, fileName, handler);
		}
	}

	protected void getFileInfoByFileUUID(RoutingContext routingContext, Terminal terminal, String fileUUID,
			Handler<PkgFile> handler) {
		if (fileUUID.startsWith("PKG")) {
			getPackageFileInfo(routingContext, terminal, fileUUID, handler);
		} else {
			getProgramFileById(routingContext, terminal, fileUUID, handler);
		}
	}

	protected long parsePackageId(String fileUUID) {
		try {
			return Long.parseLong(fileUUID.substring(3));
		} catch (NumberFormatException e) {
			throw new PxRetailerException(PxRetailerProtocol.FILE_NOT_FOUND_RESPONSE, FILE_NOTE_FOUND_ERR_MSG);
		}
	}

	private void getPackageFileInfo(RoutingContext routingContext, Terminal terminal, String fileUUID,
			Handler<PkgFile> handler) {
		long pkgId = parsePackageId(fileUUID);
		if (terminal.getPkgId() == null || terminal.getPkgId() != pkgId) {
			logger.warn(
					"deviceType={}, deviceSerialNumber={} - package file {} is invalid or not active for deployment",
					terminal.getModelId(), terminal.getTerminalSn(), pkgId);
			sendResponse(routingContext,
					createResponse(PxRetailerProtocol.FILE_NOT_FOUND_RESPONSE, FILE_NOTE_FOUND_ERR_MSG));
		}

		packageManager.getPakcageFileInfo(pkgId, res -> {
			if (res.succeeded()) {
				PkgFile pkeFile = res.result();
				if (pkeFile == null) {
					logger.warn("deviceType={}, deviceSerialNumber={} - package file {} not found for deployment",
							terminal.getModelId(), terminal.getTerminalSn(), pkgId);
					sendResponse(routingContext,
							createResponse(PxRetailerProtocol.FILE_NOT_FOUND_RESPONSE, FILE_NOTE_FOUND_ERR_MSG));
				} else {
					handler.handle(pkeFile);
				}
			} else {
				logger.error("deviceType={}, deviceSerialNumber={} - failed to fetch package file", res.cause(),
						terminal.getModelId(), terminal.getTerminalSn());
				routingContext.fail(res.cause());
			}
		});
	}

	private void getProgramFileById(RoutingContext routingContext, Terminal terminal, String fileUUID,
			Handler<PkgFile> handler) {
		long fileId = getFileId(fileUUID);
		packageManager.getProgramFileById(terminal.getPkgId(), fileId, res -> {
			if (res.succeeded()) {
				PkgFile pkgFile = res.result();
				if (pkgFile == null) {
					logger.warn(
							"deviceType={}, deviceSerialNumber={} - program file not found for deployment. pkgId={}, fileId={}",
							terminal.getModelId(), terminal.getTerminalSn(), terminal.getPkgId(), fileId);
					sendResponse(routingContext,
							createResponse(PxRetailerProtocol.FILE_NOT_FOUND_RESPONSE, FILE_NOTE_FOUND_ERR_MSG));
				} else if (!pkgFile.getPkgId().equals(terminal.getPkgId())) {
					logger.warn(
							"deviceType={}, deviceSerialNumber={} - program file id {} is inaccessible or not active, current active package {}",
							terminal.getModelId(), terminal.getTerminalSn(), fileId, terminal.getPkgId());
					sendResponse(routingContext,
							createResponse(PxRetailerProtocol.FILE_NOT_FOUND_RESPONSE, FILE_NOTE_FOUND_ERR_MSG));
				} else {
					handler.handle(pkgFile);
				}
			} else {
				logger.error("deviceType={}, deviceSerialNumber={} - failed to fetch program file", res.cause(),
						terminal.getModelId(), terminal.getTerminalSn());
				routingContext.fail(res.cause());
			}
		});
	}

	protected void getProgramFileByName(RoutingContext routingContext, Terminal terminal, String fileName,
			Handler<PkgFile> handler) {
		packageManager.getProgramFileByName(terminal.getPkgId(), fileName, res -> {
			if (res.succeeded()) {
				PkgFile pkgFile = res.result();
				if (pkgFile == null) {
					if ("manifest.xml.sig".equals(fileName)) {
						logger.debug(
								"deviceType={}, deviceSerialNumber={} - program file not found for deployment. pkgId={}, fileName={}",
								terminal.getModelId(), terminal.getTerminalSn(), terminal.getPkgId(), fileName);
					} else {
						logger.warn(
								"deviceType={}, deviceSerialNumber={} - program file not found for deployment. pkgId={}, fileName={}",
								terminal.getModelId(), terminal.getTerminalSn(), terminal.getPkgId(), fileName);
					}
					sendResponse(routingContext,
							createResponse(PxRetailerProtocol.FILE_NOT_FOUND_RESPONSE, FILE_NOTE_FOUND_ERR_MSG));
				} else if (!pkgFile.getPkgId().equals(terminal.getPkgId())) {
					logger.warn(
							"deviceType={}, deviceSerialNumber={} - program file name {} is inaccessible or not active, current active package {}",
							terminal.getModelId(), terminal.getTerminalSn(), fileName, terminal.getPkgId());
					sendResponse(routingContext,
							createResponse(PxRetailerProtocol.FILE_NOT_FOUND_RESPONSE, FILE_NOTE_FOUND_ERR_MSG));
				} else {
					handler.handle(pkgFile);
				}
			} else {
				logger.error("deviceType={}, deviceSerialNumber={} - failed to fetch program file", res.cause(),
						terminal.getModelId(), terminal.getTerminalSn());
				routingContext.fail(res.cause());
			}
		});
	}
}
