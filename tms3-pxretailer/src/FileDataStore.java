package com.pax.tms.pxretailer;

import com.github.tobato.fastdfs.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.proto.storage.DownloadCallback;
import com.pax.common.fs.FileManager;
import com.pax.common.fs.FileManagerUtils;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.shareddata.AsyncMap;

public class FileDataStore {

	public static final String MAP_STORE_NAME = "pos.file";

	private static final Logger LOGGER = LoggerFactory.getLogger(FileDataStore.class);

	private static final DownloadCallback<byte[]> downloadCallback = new DownloadByteArray(4 * 1024 * 1024);

	private static final byte[] emptyBytes = new byte[0];

	private Vertx vertx;
	private AsyncMap<String, byte[]> mapStore;
	private FileManager fileManager;

	public FileDataStore(Vertx vertx, AsyncMap<String, byte[]> mapStore) {
		this.vertx = vertx;
		this.mapStore = mapStore;
		this.fileManager = FileManagerUtils.getFileManager();
	}

	public void getFileData(String filePath, long offset, int length, Future<byte[]> future) {
		String cacheKey = getCacheKey(filePath, offset, length);
		getFileDataFromCache(cacheKey, future, v -> {
			getFileDataFromDfs(filePath, offset, length, future, cacheKey);
		});
	}

	private String getCacheKey(String filePath, long offset, int length) {
		return offset + "|" + length + "|" + filePath;
	}

	private void getFileDataFromDfs(String filePath, long offset, int length, Future<byte[]> future, String cacheKey) {
		vertx.<byte[]>executeBlocking(fut -> {
			fut.complete(fileManager.downloadFile(filePath, offset, length, downloadCallback));
		}, res -> {
			afterGetFileDataFromDfs(res, future, cacheKey);
		});
	}

	private void afterGetFileDataFromDfs(AsyncResult<byte[]> res, Future<byte[]> future, String cacheKey) {
		if (res.succeeded()) {
			byte[] fileData = res.result();
			future.complete(fileData);
			putFileDataToCache(cacheKey, fileData);
		} else {
			LOGGER.error("Failed to fetch file data from DFS", res.cause());
			future.fail("Failed to fetch file data from DFS");
		}
	}

	private void putFileDataToCache(String cacheKey, byte[] fileData) {
		if (mapStore != null) {
			mapStore.put(cacheKey, fileData == null ? emptyBytes : fileData, res -> {
				if (res.failed()) {
					LOGGER.error("Failed to put file data to cache", res.cause());
				}
			});
		}
	}

	private void getFileDataFromCache(String cacheKey, Future<byte[]> future, Handler<Void> failureHandle) {
		if (mapStore != null) {
			mapStore.get(cacheKey, res -> {
				if (res.succeeded()) {
					byte[] fileData = res.result();
					if (fileData == null) {
						failureHandle.handle(null);
					} else if (fileData.length == 0) {
						future.complete();
					} else {
						future.complete(fileData);
					}
				} else {
					LOGGER.error("Failed to fetch file data form cache", res.cause());
					failureHandle.handle(null);
				}
			});
		} else {
			failureHandle.handle(null);
		}
	}

}
