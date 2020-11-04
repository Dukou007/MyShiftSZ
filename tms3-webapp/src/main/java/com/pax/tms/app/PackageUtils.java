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
package com.pax.tms.app;

import java.io.File;

import com.pax.common.fs.FileManagerUtils;

public class PackageUtils {

	private PackageUtils() {
	}

	public static String getFileExtension(String fileName) {
		return FileManagerUtils.getFileExtension(fileName);
	}

	public static String getFilePrefix(String fileName) {
		int index = fileName.lastIndexOf(".");
		if (index != -1) {
			return fileName.substring(0, index);
		}
		return fileName;
	}

	public static String getParentFilePath(String filePath) {
		return new File(filePath).getParent();
	}
}
