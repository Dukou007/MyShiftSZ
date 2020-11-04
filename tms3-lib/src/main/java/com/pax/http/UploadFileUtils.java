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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.IOUtils;

import com.pax.common.fs.FileManagerUtils;

public class UploadFileUtils {

	private static final String ENCODING = "utf-8";

	private UploadFileUtils() {
	}

	public static UploadFileItem storeUploadFile(HttpServletRequest request, String user) throws FileUploadException {

		Iterator<FileItem> itr = getFileItems(request);
		while (itr.hasNext()) {
			FileItem item = itr.next();
			if (item.isFormField()) {
				continue;
			}
			String filename = new File(item.getName()).getName();
			InputStream inputStream = null;
			try {
				inputStream = new BufferedInputStream(item.getInputStream());
				return storeUploadFileTemp(filename, inputStream, user);
			} catch (IOException e) {
				throw new FileUploadException("Failed to store upload file", e);
			} finally {
				if (inputStream != null) {
					IOUtils.closeQuietly(inputStream);
				}
			}
		}
		return null;
	}

	private static Iterator<FileItem> getFileItems(HttpServletRequest request) throws FileUploadException {
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setHeaderEncoding(ENCODING);
		Iterator<FileItem> items = upload.parseRequest(request).iterator();
		return items;
	}

	private static UploadFileItem storeUploadFileTemp(String filename, InputStream inputStream, String user)
			throws IOException {
		File tempFile = null;
		String temp = new SimpleDateFormat("yyMMdd").format(new Date()) + "_"
				+ (StringUtils.isEmpty(user) ? "unknown" : user);
		String tempDir = Files.createTempDirectory(temp).toString();
		String tempFilePath = tempDir + File.separator + filename;
		long size = writeUploadFile(inputStream, new File(tempFilePath));
		UploadFileItem uploadFileItem = new UploadFileItem(filename, tempFilePath, size);
		uploadFileItem.setLocalFile(tempFile);
		return uploadFileItem;
	}

	private static long writeUploadFile(InputStream inputStream, File storeFile) throws IOException {
		OutputStream outputStream = null;
		try {
			outputStream = new BufferedOutputStream(new FileOutputStream(storeFile));
			byte[] buff = new byte[4096];
			int c = 0;
			long size = 0;
			while ((c = inputStream.read(buff)) != -1) {
				size += c;
				outputStream.write(buff, 0, c);
			}
			outputStream.flush();
			return size;
		} finally {
			if (outputStream != null) {
				outputStream.close();
			}
		}
	}

	public static UploadFileItem storeUploadFile(String filename, byte[] fileData, String user) throws IOException {
		ByteArrayInputStream inputStream = new ByteArrayInputStream(fileData);
		return storeUploadFileTemp(filename, inputStream, user);
	}

	public static UploadFileItem storeFileToFastDsf(HttpServletRequest request) throws FileUploadException {
		Iterator<FileItem> itr = getFileItems(request);
		while (itr.hasNext()) {
			FileItem item = itr.next();
			if (item.isFormField()) {
				continue;
			}
			String filename = new File(item.getName()).getName();
			InputStream inputStream = null;
			try {
				inputStream = new BufferedInputStream(item.getInputStream());
				long fileSize = item.getSize();
				String url=FileManagerUtils.getFileManager().uploadFile(inputStream, fileSize,
						FileManagerUtils.getFileExtension(filename));
				UploadFileItem uploadFileItem=new UploadFileItem();
				uploadFileItem.setFilename(filename);
				uploadFileItem.setSize(fileSize);
				uploadFileItem.setUrl(url);
				return uploadFileItem;
			} catch (IOException e) {
				throw new FileUploadException("Failed to store upload file", e);
			} finally {
				if (inputStream != null) {
					IOUtils.closeQuietly(inputStream);
				}
			}
		}
		return null;

	}

}
