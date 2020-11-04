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
package com.pax.common.fs;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.pax.fastdfs.exception.FdfsException;

public class FileManagerUtils {

	private static final DateFormat YYMMDD = new SimpleDateFormat("yyMMdd");

	private static FileManager fileManager;

	private FileManagerUtils() {
	}

	public static FileManager getFileManager() {
		return fileManager;
	}

	public static void setFileManager(FileManager fileManager) {
		FileManagerUtils.fileManager = fileManager;
	}

	public static String getFileExtension(String filePath) {
		String fileName = filePath;
		int index = fileName.lastIndexOf('/');
		if(index!=-1 && index != 0) {
			fileName = fileName.substring(index + 1);
		}
		
		index = fileName.lastIndexOf('\\');
		if(index!=-1 && index != 0) {
			fileName = fileName.substring(index + 1);
		}
		
		index = fileName.lastIndexOf(".");
		if (index != -1 && index != 0) {
			return fileName.substring(index + 1);
		}
		return null;
	}

	public static String getFileExtensionByFilePath(String filePath) {
		File file = new File(filePath);
		String fileName = file.getName();
		return getFileExtension(fileName);
	}

	public static String saveLocalFileToFdfs(String filePath) {
		return saveLocalFileToFdfs(filePath, getFileExtensionByFilePath(filePath));
	}

	public static String saveLocalFileToFdfs(String filePath, String fileExtName) {
		File file = new File(filePath);

		try (InputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
			return fileManager.uploadFile(inputStream, file.length(), fileExtName == null ? "dat" : fileExtName);
		} catch (IOException e) {
			throw new FdfsException("Failed to save file to FDFS", e);
		}

	}

	public static String getLocalTempFilePrefix() {
		return YYMMDD.format(new Date()) + "-";
	}

	public static String saveBytesToFdfs(byte[] data, String fileExtName) {
		return fileManager.uploadFile(new ByteArrayInputStream(data), data.length, fileExtName);
	}

}
