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

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

import org.springframework.beans.factory.annotation.Autowired;

import com.pax.common.fs.FileManager;
import com.pax.fastdfs.proto.storage.DownloadByteArray;
import com.pax.fastdfs.proto.storage.DownloadCallback;

public class FileDataStore {

	private static final DownloadCallback<byte[]> fdfsCallback = new DownloadByteArray(4 * 1024 * 1024);

	private Vertx vertx;

	private FileManager fileManager;

	public FileDataStore(Vertx vertx) {
		this.vertx = vertx;
	}

	@Autowired
	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	public void getFileData(String filePath, long offset, int length, Handler<AsyncResult<byte[]>> handler) {
		vertx.<byte[]>executeBlocking(fut -> {
			byte[] fileData  = fileManager.downloadFile(filePath, offset, length, fdfsCallback);
			fut.complete(fileData);
		}, false, res -> {
			if (res.succeeded()) {
				handler.handle(Future.succeededFuture(res.result()));
			} else {
				handler.handle(Future.failedFuture(res.cause()));
			}
		});
	}

}
