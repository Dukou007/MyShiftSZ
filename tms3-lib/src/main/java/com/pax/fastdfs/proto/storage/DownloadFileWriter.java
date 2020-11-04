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

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

/**
 * 文件下载回调方法
 * 
 * @author tobato
 *
 */
public class DownloadFileWriter implements DownloadCallback<String> {

	/**
	 * 文件名称
	 */
	private String fileName;

	public DownloadFileWriter(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * 文件接收处理
	 */
	@Override
	public String recv(InputStream ins) throws IOException {
		FileOutputStream out = null;
		InputStream in = null;
		try {
			out = new FileOutputStream(fileName);
			in = new BufferedInputStream(ins);
			// 通过ioutil 对接输入输出流，实现文件下载
			IOUtils.copy(in, out);
			out.flush();
		} finally {
			// 关闭流
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}
		return fileName;
	}

}
