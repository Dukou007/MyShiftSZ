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
package com.pax.fastdfs.proto.storage;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.pax.fastdfs.exception.FdfsException;

/**
 * 下载为byte流
 * 
 * @author tobato
 *
 */
public class DownloadByteArray implements DownloadCallback<byte[]> {
	private int limitSize = Integer.MAX_VALUE;

	public DownloadByteArray(int limitSize) {
		this.limitSize = limitSize;
	}

	@Override
	public byte[] recv(InputStream ins) throws IOException {
		FdfsInputStream fins = (FdfsInputStream) ins;
		if (fins.getRemainByteSize() > limitSize) {
			throw new FdfsException("Content length exceed the limit size");
		}
		byte[] data = new byte[(int) fins.getRemainByteSize()];
		IOUtils.read(fins, data);
		return data;
	}
}
