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
package com.pax.tms.pxretailer;

import java.util.Date;

import com.pax.tms.download.DownloadLimiter;
import com.pax.tms.download.FileDataStore;
import com.pax.tms.download.model.Terminal;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;

/**
 * This class provides more control to detect if terminal is disconnected during
 * the download and also update the terminal status to keep the terminal online
 * if file download takes longer than expected.
 */

public class FileSender {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileSender.class);

	private DownloadLimiter downloadLimiter;

	private PxRetailerTerminalManager terminalManager;

	private FileDataStore fileDataStore;

	private long updateOnlineStatusInterval;

	private long lastUpdateTime = System.currentTimeMillis();

	public FileSender(PxRetailerTerminalManager terminalManager, DownloadLimiter downloadLimiter,
			FileDataStore fileDataStore, long updateOnlineStatusInterval) {
		this.terminalManager = terminalManager;
		this.downloadLimiter = downloadLimiter;
		this.fileDataStore = fileDataStore;
		this.updateOnlineStatusInterval = updateOnlineStatusInterval;
	}

	public void sendFile(RoutingContext routingContext, Terminal terminal, String fileId, String fileName,
			String filePath, long fileSize, int chunkSize) {
		new StreamHandler(terminal, routingContext.response(), fileId, fileName, filePath, fileSize, chunkSize).start();
	}

	private void updateStatus(Terminal terminal) {
		if (updateOnlineStatusInterval > 0
				&& System.currentTimeMillis() - lastUpdateTime > updateOnlineStatusInterval) {
			LOGGER.debug(
					"deviceType={}, deviceSerialNumber={} - update online status in the progress of downloading",
					terminal.getModelId(), terminal.getTerminalSn());
			lastUpdateTime = System.currentTimeMillis();
			terminalManager.updateLastAccessTime(terminal, new Date());
			downloadLimiter.updateDownloadProgress(terminal.getTerminalId());
		}
	}

	private class StreamHandler implements Handler<AsyncResult<byte[]>> {
		private Terminal terminal;
		private HttpServerResponse response;
		private String fileId;
		private String fileName;
		private String filePath;
		private long fileSize;
		private int chunkSize;
		private long offset;
		private boolean closed;
		private boolean drainHandlerInited;

		public StreamHandler(Terminal terminal, HttpServerResponse response, String fileId, String fileName,
				String filePath, long fileSize, int chunkSize) {
			super();
			this.terminal = terminal;
			this.response = response;
			this.fileId = fileId;
			this.fileName = fileName;
			this.filePath = filePath;
			this.fileSize = fileSize;
			this.chunkSize = chunkSize;
		}

		public void start() {
			response.exceptionHandler(thr -> {
				LOGGER.error("deviceType={}, deviceSerialNumber={} - error while send file data to terminal", thr,
						terminal.getModelId(), terminal.getTerminalSn());
				response.end();
				closed = true;
			});

			response.closeHandler(closeHandler -> {
				closed = true;
				LOGGER.info("## response clossed");
				if (offset != fileSize) {
					LOGGER.warn("deviceType={}, deviceSerialNumber={} - download failed, file: {}",
							terminal.getModelId(), terminal.getTerminalSn(), fileName);
				} else {
					LOGGER.debug("deviceType={}, deviceSerialNumber={} - download completed, file: {}",
							terminal.getModelId(), terminal.getTerminalSn(), fileName);
				}
			});

			response.bodyEndHandler(v -> {
			    LOGGER.info("## response closs  ");
				response.close();
			});

			response.setStatusCode(200);
			response.setChunked(true);
			response.putHeader("content-type", "application/octet-stream");
			response.putHeader("content-disposition", "attachment; fileUUID=\"" + fileId + "" + "\"");
			stream();
		}

		private void stream() {
		    LOGGER.info("##### stream begin ##########");
			fileDataStore.getFileData(filePath, offset, getReadSize(fileSize, offset, chunkSize), this);
		}

		@Override
		public void handle(AsyncResult<byte[]> res) {
			if (res.failed()) {
				LOGGER.error("deviceType={}, deviceSerialNumber={} - failed to read file data", res.cause(),
						terminal.getModelId(), terminal.getTerminalSn());
				response.end();
				return;
			}

			if (closed) {
				return;
			}

			byte[] fileData = res.result();
			if (fileData == null || fileData.length == 0) {
				LOGGER.error("deviceType={}, deviceSerialNumber={} - failed to read file data, fileData=null",
						terminal.getModelId(), terminal.getTerminalSn());
				response.end();
				return;
			}

			sendFileData(fileData);

		}

		private void sendFileData(byte[] fileData) {
			offset += fileData.length;
			response.write(Buffer.buffer(fileData));
			updateStatus(terminal);
			if (response.writeQueueFull()) {
				LOGGER.debug("deviceType={}, deviceSerialNumber={} - writeQueueFull and pausing download",
						terminal.getModelId(), terminal.getTerminalSn());
				initDrainHandler();
			} else {
				readNextChunk();
			}
		}

		private void readNextChunk() {
			int length = getReadSize(fileSize, offset, chunkSize);
			if (length == 0) {
				response.end();
				closed = true;
				LOGGER.info("## readNextChunk response end");
			} else {
				fileDataStore.getFileData(filePath, offset, length, this);
			}
		}

		private void initDrainHandler() {
			if (!drainHandlerInited) {
				drainHandlerInited = true;
				response.drainHandler(result -> {
					if (!closed) {
						int length = getReadSize(fileSize, offset, chunkSize);
						if (length == 0) {
							response.end();
							closed = true;
							updateStatus(terminal);
						} else {
							fileDataStore.getFileData(filePath, offset, length, this);
						}
					}
				});
			}
		}

		private int getReadSize(long fileSize, long offset, int chunkSize) {
			if (offset + chunkSize > fileSize) {
				return (int) (fileSize - offset);
			} else {
				return chunkSize;
			}
		}
	}
}
