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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * zip util
 * 
 * @author spring
 */
public class ZipFileUtil {

	public static final String ENCODING_DEFAULT = "UTF-8";

	public static final int BUFFER_SIZE_DIFAULT = 4096;

	private ZipFileUtil() {
	}

	public static void zip(String path, String... zipFiles) throws IOException {
		zip(path, Charset.forName(ENCODING_DEFAULT), null, zipFiles);
	}

	public static void zip(String path, File... zipFiles) throws IOException {
		zip(path, Charset.forName(ENCODING_DEFAULT), null, zipFiles);
	}

	public static void zip(String path, String encoding, String... zipFiles) throws IOException {
		zip(path, Charset.forName(encoding), null, zipFiles);
	}

	public static void zip(String path, String encoding, String comment, String... zipFiles) throws IOException {
		zip(path, Charset.forName(encoding), comment, zipFiles);
	}

	public static void zip(String path, String encoding, File... zipFiles) throws IOException {
		zip(path, Charset.forName(encoding), null, zipFiles);
	}

	public static void zip(String path, String encoding, String comment, File... zipFiles) throws IOException {
		zip(path, Charset.forName(encoding), comment, zipFiles);
	}

	private static void zip(String path, Charset charset, String comment, String... zipFiles) throws IOException {
		try (ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(path)),
				charset)) {
			if (comment != null) {
				zipOut.setComment(comment);
			}
			zipOut.setMethod(Deflater.DEFLATED);
			for (int i = 0; i < zipFiles.length; i++) {
				File file = new File(zipFiles[i]);
				addZipEntry(zipOut, file, file.getName());
			}
			zipOut.flush();
		}
	}

	private static void zip(String path, Charset charset, String comment, File... zipFiles) throws IOException {
		try (ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(path)),
				charset)) {
			if (comment != null) {
				zipOut.setComment(comment);
			}
			zipOut.setMethod(Deflater.DEFLATED);
			for (int i = 0; i < zipFiles.length; i++) {
				File file = zipFiles[i];
				addZipEntry(zipOut, file, file.getName());
			}
			zipOut.flush();
		}
	}

	public static void addZipEntry(ZipOutputStream zipOutput, File file) throws IOException {
		addZipEntry(zipOutput, file, file.getPath());
	}

	public static void addZipEntry(ZipOutputStream zipOutput, File file, String entryName) throws IOException {
		String zipEntryName = entryName;
		if (file.isFile()) {
			try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
				while (zipEntryName.charAt(0) == '\\' || zipEntryName.charAt(0) == '/') {
					zipEntryName = zipEntryName.substring(1);
				}
				ZipEntry entry = new ZipEntry(zipEntryName);
				entry.setMethod(Deflater.DEFLATED);
				zipOutput.putNextEntry(entry);
				byte[] buff = new byte[BUFFER_SIZE_DIFAULT];
				int size;
				while ((size = bis.read(buff, 0, buff.length)) != -1) {
					zipOutput.write(buff, 0, size);
				}
				zipOutput.closeEntry();
			}
		} else {
			File[] files = file.listFiles();
			for (File f : files) {
				addZipEntry(zipOutput, f, zipEntryName + File.separator + f.getName());
			}
		}
	}

	public static void unzip(String path, String exdir) throws IOException {
		unzip(new File(path), exdir);
	}

	public static void unzip(String path, File exdir) throws IOException {
		unzip(new File(path), exdir);
	}

	public static Map<String, String> unzip(File file, String exdir) throws IOException {
		try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
			return unzip(bis, exdir);
		}
	}

	public static Map<String, String> unzip(File file, File exdir) throws IOException {
		try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
			return unzip(bis, exdir);
		}
	}

	public static Map<String, String> unzip(File file, String charset, File exdir) throws IOException {
		try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
			return unzip(bis, Charset.forName(charset), exdir);
		}
	}

	public static Map<String, String> unzip(String file, String charset, String exdir) throws IOException {
		try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
			return unzip(bis, Charset.forName(charset), exdir);
		}
	}

	public static Map<String, String> unzip(InputStream input, String exdir) throws IOException {
		return unzip(input, Charset.forName(ENCODING_DEFAULT), exdir);
	}

	public static Map<String, String> unzip(InputStream input, File exdir) throws IOException {
		return unzip(input, Charset.forName(ENCODING_DEFAULT), exdir);
	}

	public static Map<String, String> unzip(InputStream input, Charset charset, String exdir) throws IOException {
		return unzip(input, charset, new File(exdir));
	}

	public static Map<String, String> unzip(InputStream input, Charset charset, File exdir) throws IOException {
		ZipEntry zipEntry;
		byte[] buff = new byte[BUFFER_SIZE_DIFAULT];
		int size;
		Map<String, String> resultMap = new HashMap<>();

		if (!exdir.exists()) {
			exdir.mkdirs();
		}

		try (ZipInputStream zipInput = new ZipInputStream(input, charset)) {
			while ((zipEntry = zipInput.getNextEntry()) != null) {
				String zipEntryName = zipEntry.getName();
				if (zipEntry.isDirectory()) {
					File zipFile = new File(exdir, zipEntryName);
					if (!zipFile.exists()) {
						zipFile.mkdirs();
					}
					continue;
				}

				File zipFile = new File(exdir, zipEntryName);
				if (!zipFile.getParentFile().exists()) {
					zipFile.getParentFile().mkdirs();
				}

				try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(zipFile))) {
					while ((size = zipInput.read(buff)) > 0) {
						bos.write(buff, 0, size);
					}
					bos.flush();
				}

				resultMap.put(zipEntryName, zipFile.getAbsolutePath());
			}
			return resultMap;
		}
	}

}