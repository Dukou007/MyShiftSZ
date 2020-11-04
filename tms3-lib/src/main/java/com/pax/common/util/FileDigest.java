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
package com.pax.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.codec.digest.DigestUtils;

public class FileDigest {

	private FileDigest() {
	}

	public static String md5Hex(String filePath) throws IOException {
		return md5Hex(new File(filePath));
	}

	public static String md5Hex(File file) throws IOException {
		try (InputStream input = new FileInputStream(file)) {
			return DigestUtils.md5Hex(input);
		}
	}

	public static String sha256Hex(String filePath) throws IOException {
		return sha256Hex(new File(filePath));
	}

	public static String sha256Hex(File file) throws IOException {
		try (InputStream input = new FileInputStream(file)) {
			return DigestUtils.sha256Hex(input);
		}
	}

}
