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
package com.pax.http;

import java.io.File;
import java.io.Serializable;

public class UploadFileItem implements Serializable {
	private static final long serialVersionUID = -8123071077765643344L;

	private String filename;
	private String url;
	private long size;
	private File localFile;

	public UploadFileItem() {
	}

	public UploadFileItem(String filename, String url) {
		this.filename = filename;
		this.url = url;
	}

	public UploadFileItem(String filename, String url, long size) {
		this.filename = filename;
		this.url = url;
		this.size = size;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public File getLocalFile() {
		return localFile;
	}

	public void setLocalFile(File localFile) {
		this.localFile = localFile;
	}

}
