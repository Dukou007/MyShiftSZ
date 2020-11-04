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
package com.pax.tms.download;

import javax.cache.Cache;
import javax.cache.CacheManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.pax.tms.download.model.PkgFile;
import com.pax.tms.download.service.TerminalDownloadService;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * @author Elliott.Z
 *
 */
public class PackageManager {

	private TerminalDownloadService terminalDownloadService;

	private Vertx vertx;

	private Cache<Long, PkgFile> fileIdCache;

	private Cache<Long, PkgFile> pkgCache;

	private Cache<String, PkgFile> fileNameCache;

	private Logger logger = LoggerFactory.getLogger(PackageManager.class);

	public PackageManager(Vertx vertx) {
		this.vertx = vertx;
	}

	@Autowired
	@Qualifier("jCacheManager")
	public void setCacheManager(CacheManager cacheManager) {
		fileIdCache = cacheManager.getCache("fileIdCache", Long.class, PkgFile.class);
		pkgCache = cacheManager.getCache("pkgCache", Long.class, PkgFile.class);
		fileNameCache = cacheManager.getCache("fileNameCache", String.class, PkgFile.class);
	}

	@Autowired
	public void setTerminalDownloadService(TerminalDownloadService terminalDownloadService) {
		this.terminalDownloadService = terminalDownloadService;
	}

	public void getPakcageFileInfo(Long pkgId, Handler<AsyncResult<PkgFile>> handler) {
		if (pkgId == null) {
			handler.handle(null);
		}
		vertx.<PkgFile>executeBlocking(fut -> {
			PkgFile pkgFile = pkgCache.get(pkgId);
			if (pkgFile != null) {
				fut.complete(pkgFile);
			} else {
				logger.debug("package id {} - load package file info from databse", pkgId);
				pkgFile = terminalDownloadService.getPackageFile(pkgId);
				if (pkgFile != null) {
					pkgCache.put(pkgId, pkgFile);
				} else {
					logger.debug("package id {} - package file does not exist in database", pkgId);
				}
				fut.complete(pkgFile);
			}
		}, false, res -> {
			if (res.failed()) {
				handler.handle(Future.failedFuture(res.cause()));
			} else {
				handler.handle(Future.succeededFuture(res.result()));
			}
		});
	}

	public void getProgramFileById(Long pkgId, long fileId, Handler<AsyncResult<PkgFile>> handler) {
		if (pkgId == null) {
			handler.handle(null);
		}
		vertx.<PkgFile>executeBlocking(fut -> {
			PkgFile pkgFile = fileIdCache.get(fileId);
			if (pkgFile != null) {
				fut.complete(pkgFile);
			} else {
				logger.debug("file id {}, package id {} - load program file from databse", fileId, pkgId);
				pkgFile = terminalDownloadService.getProgramFile(pkgId, fileId);
				if (pkgFile != null) {
					fileIdCache.put(fileId, pkgFile);
				} else {
					logger.debug("file id {}, package id {} - program file does not exist in database", fileId, pkgId);
				}
				fut.complete(pkgFile);
			}
		}, false, res -> {
			if (res.failed()) {
				handler.handle(Future.failedFuture(res.cause()));
			} else {
				handler.handle(Future.succeededFuture(res.result()));
			}
		});
	}

	public void getProgramFileByName(Long pkgId, String fileName, Handler<AsyncResult<PkgFile>> handler) {
		if (pkgId == null) {
			handler.handle(null);
		}
		String cacheKey = pkgId + "/" + fileName;
		vertx.<PkgFile>executeBlocking(fut -> {
			PkgFile pkgFile = fileNameCache.get(cacheKey);
			if (pkgFile != null) {
				fut.complete(pkgFile);
			} else {
				logger.debug("file name {}, package id {} - load program file from databse", fileName, pkgId);
				pkgFile = terminalDownloadService.getProgramFile(pkgId, fileName);
				if (pkgFile != null) {
					fileNameCache.put(cacheKey, pkgFile);
				} else {
					logger.debug("file name {}, package id {} - program file does not exist in database", fileName,
							pkgId);
				}
				fut.complete(pkgFile);
			}
		}, false, res -> {
			if (res.failed()) {
				handler.handle(Future.failedFuture(res.cause()));
			} else {
				handler.handle(Future.succeededFuture(res.result()));
			}
		});
	}
}
