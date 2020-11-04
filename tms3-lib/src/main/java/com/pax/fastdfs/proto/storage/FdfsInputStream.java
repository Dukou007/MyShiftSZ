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

/**
 * FdfsInputStream包装类
 * 
 * @author tobato
 *
 */
public class FdfsInputStream extends InputStream {

	private final InputStream ins;
	private final long size;
	private long remainByteSize;

	public FdfsInputStream(InputStream ins, long size) {
		this.ins = ins;
		this.size = size;
		remainByteSize = size;
	}

	@Override
	public int read() throws IOException {
		return ins.read();
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if (remainByteSize == 0) {
			return -1;
		}
		int byteSize = ins.read(b, off, len);
		if (remainByteSize < byteSize) {
			throw new IOException("协议长度" + size + "与实际长度不符");
		}

		remainByteSize -= byteSize;
		return byteSize;
	}

	@Override
	public void close() throws IOException {
		// do nothing
	}

	/**
	 * 是否已完成读取
	 * 
	 * @return
	 */
	public boolean isReadCompleted() {
		return remainByteSize == 0;
	}

	public void readComplete() throws IOException {
		if (remainByteSize > 0) {
			long n = ins.skip(remainByteSize);
			remainByteSize -= n;
		}
		if (remainByteSize > 0) {
			ins.close();
		}
	}

	public long getSize() {
		return size;
	}

	public long getRemainByteSize() {
		return remainByteSize;
	}

}
