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
package com.pax.tms.app.broadpos;

import com.pax.common.fs.IFile;

public class CertFile implements IFile {
	private long fileSize;
	private byte[] fileData;

	@Override
	public long getFileSize() {
		return fileSize;
	}

	@Override
	public byte[] getFileData(long offset, int length) {
		byte[] buff = new byte[length];
		System.arraycopy(fileData, (int) offset, buff, 0, length);
		return buff;
	}

	public byte[] getFileData() {
		return fileData;
	}

	public void setFileData(byte[] fileData) {
		this.fileData = fileData;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

}
