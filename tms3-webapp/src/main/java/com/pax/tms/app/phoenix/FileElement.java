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
package com.pax.tms.app.phoenix;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

public class FileElement extends ChildElement {

	private static final String MD5_PATTERN = "^[0-9A-Fa-f]{32}$";
	private static final String SHA256_PATTERN = "^[0-9A-Fa-f]{64}$";
	private static final String FOLDER_PATTERN = "^[\\.\\-0-9A-Za-z_~!\\s/]+$";
	private static final String FILE_PATTERN = "^[0-9A-Za-z]+[\\.\\-0-9A-Za-z_~!\\s]*$";

	private String path;
	private String version;
	private String md5;
	private String sha256;

	private String fileName;

	@Override
	public void parse(String packageType, Element el) {
		setPackageType(packageType);

		path = el.attributeValue("path");
		version = el.attributeValue("version");
		md5 = el.attributeValue("md5");
		sha256 = el.attributeValue("sha256");

		validatePath(el);
		validateHash(el);

		fileName = (path.startsWith(".") ? path.replaceFirst("\\.", "") : path);
		fileName = fileName.startsWith("/") ? fileName.replaceFirst("/", "") : fileName;
	}

	private void validatePath(Element el) {
		if (StringUtils.isEmpty(path)) {
			throw new PackageException("msg.phonenixPackage.manifest.file.noPath");
		}

		if (!checkPathMatchPattern(path, getPackageType())) {
			throw new PackageException("msg.phonenixPackage.manifest.file.invalidPath");
		}
	}

	private boolean checkPathMatchPattern(String pathInput, String packageType) {
		path = pathInput.trim();
		if (StringUtils.isEmpty(path) || path.startsWith("./")) {
			return false;
		}

		if (path.startsWith("/") && !"firmware".equalsIgnoreCase(packageType)) {
			return false;
		}

		if (path.length() > 255) {
			return false;
		}

		if (path.contains("/")) {
			if (!validateInput(FOLDER_PATTERN, path.substring(0, path.lastIndexOf("/")))
					|| !validateInput(FILE_PATTERN, path.substring(path.lastIndexOf("/") + 1))) {
				return false;
			}
		} else if (!validateInput(FILE_PATTERN, path)) {
			return false;
		}

		return true;
	}

	private void validateHash(Element el) {
		if (StringUtils.isEmpty(sha256)) {
			throw new PackageException("msg.phonenixPackage.manifest.file.noSha256Hashes");
		}

		if (sha256 != null && sha256.isEmpty()) {
			throw new PackageException("msg.phonenixPackage.manifest.file.noSha256");
		}

		if (sha256 != null && !validateInput(SHA256_PATTERN, sha256)) {
			throw new PackageException("msg.phonenixPackage.manifest.file.invalidSha256");
		}

		if (md5 != null && md5.isEmpty()) {
			throw new PackageException("msg.phonenixPackage.manifest.file.noMd5");
		}

		if (md5 != null && !validateInput(MD5_PATTERN, md5)) {
			throw new PackageException("msg.phonenixPackage.manifest.file.invalidMd5");
		}
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getSha256() {
		return sha256;
	}

	public void setSha256(String sha256) {
		this.sha256 = sha256;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
