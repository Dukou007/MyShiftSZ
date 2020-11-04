package com.pax.tms.pxretailer;

import com.pax.tms.protocol.service.PkgFile;
import com.pax.tms.protocol.service.TerminalService;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.shareddata.AsyncMap;

public class PackageStore {

	public static final String MAP_STORE_NAME = "pos.package";

	private static final Logger LOGGER = LoggerFactory.getLogger(PackageStore.class);

	private static class NullFileInfo extends PkgFile {
		private static final long serialVersionUID = -439603004035725853L;
	}

	private static final PkgFile nullFileInfo = new NullFileInfo();

	private TerminalService terminalService;

	private Vertx vertx;
	private AsyncMap<String, PkgFile> mapStore;

	public PackageStore(Vertx vertx, AsyncMap<String, PkgFile> mapStore) {
		this.vertx = vertx;
		this.mapStore = mapStore;
	}

	public void getPakcageFileInfo(long pkgId, Future<PkgFile> future) {
		getFileInfoFromCache("PKG_" + pkgId, future, v -> {
			getPackageFileFromDb(pkgId, future);
		});
	}

	private void getPackageFileFromDb(long pkgId, Future<PkgFile> future) {
		vertx.<PkgFile>executeBlocking(fut -> {
			fut.complete(terminalService.getPackageFile(pkgId));
		}, res -> {
			afterGetFileInfoFromDb(res, future, "PKG_" + pkgId);
		});
	}

	public void getFileInfoById(long fileId, Future<PkgFile> future) {
		getFileInfoFromCache("" + fileId, future, v -> {
			getProgramFileFromDb(fileId, future);
		});
	}

	private void getProgramFileFromDb(long fileId, Future<PkgFile> future) {
		vertx.<PkgFile>executeBlocking(fut -> {
			fut.complete(terminalService.getProgramFile(fileId));
		}, res -> {
			afterGetFileInfoFromDb(res, future, "" + fileId);
		});
	}

	public void getProgramFile(long pkgId, String fileName, Future<PkgFile> future) {
		getFileInfoFromCache("PKG_" + pkgId + "_" + fileName, future, v -> {
			getProgramFileFromDb(pkgId, fileName, future);
		});
	}

	private void getProgramFileFromDb(long pkgId, String fileName, Future<PkgFile> future) {
		vertx.<PkgFile>executeBlocking(fut -> {
			fut.complete(terminalService.getProgramFile(pkgId, fileName));
		}, res -> {
			afterGetFileInfoFromDb(res, future, "PKG_" + pkgId + "_" + fileName);
		});
	}

	private void afterGetFileInfoFromDb(AsyncResult<PkgFile> res, Future<PkgFile> future, String cacheKey) {
		if (res.succeeded()) {
			PkgFile fileInfo = res.result();
			future.complete(fileInfo);
			putFileInfoToCache(cacheKey, fileInfo);
		} else {
			LOGGER.error("Failed to fetch package file from DB", res.cause());
			future.fail("Failed to fetch package file from DB");
		}
	}

	private void putFileInfoToCache(String key, PkgFile fileInfo) {
		if (mapStore != null) {
			mapStore.put(key, fileInfo == null ? nullFileInfo : fileInfo, res -> {
				if (res.failed()) {
					LOGGER.error("Failed to put package file to cache", res.cause());
				}
			});
		}
	}

	private void getFileInfoFromCache(String cacheKey, Future<PkgFile> future, Handler<Void> failureHandle) {
		if (mapStore != null) {
			mapStore.get(cacheKey, res -> {
				if (res.succeeded()) {
					PkgFile fileInfo = res.result();
					if (fileInfo == null) {
						failureHandle.handle(null);
					} else if (fileInfo instanceof NullFileInfo) {
						future.complete();
					} else {
						future.complete(fileInfo);
					}
				} else {
					LOGGER.error("Failed to fetch package file form cache", res.cause());
					failureHandle.handle(null);
				}
			});
		} else {
			failureHandle.handle(null);
		}
	}

	public TerminalService getTerminalService() {
		return terminalService;
	}

	public void setTerminalService(TerminalService terminalService) {
		this.terminalService = terminalService;
	}

}
