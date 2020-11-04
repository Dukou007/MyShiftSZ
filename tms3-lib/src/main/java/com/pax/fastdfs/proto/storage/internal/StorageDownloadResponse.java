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
package com.pax.fastdfs.proto.storage.internal;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import com.pax.fastdfs.proto.FdfsResponse;
import com.pax.fastdfs.proto.storage.DownloadCallback;
import com.pax.fastdfs.proto.storage.FdfsInputStream;

/**
 * 文件下载结果
 * 
 * @author tobato
 * @param <T>
 *
 */
public class StorageDownloadResponse<T> extends FdfsResponse<T> {

	private DownloadCallback<T> callback;

	public StorageDownloadResponse(DownloadCallback<T> callback) {
		super();
		this.callback = callback;
	}

	/**
	 * 解析反馈内容
	 */
	@Override
	public T decodeContent(InputStream in, Charset charset) throws IOException {
		// 解析报文内容
		FdfsInputStream input = new FdfsInputStream(in, getContentLength());
		try {
			return callback.recv(input);
		} finally {
			input.readComplete();
		}
	}

}
