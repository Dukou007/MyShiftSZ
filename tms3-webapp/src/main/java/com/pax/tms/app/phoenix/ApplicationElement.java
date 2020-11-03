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

public class ApplicationElement extends ChildElement {

	private static final String APPLICATION_DISPLAY_NAME = "^[\\x20-\\x7e]{1,128}$";
	private static final String BIN_PATTERN = "^[\\.\\-0-9A-Za-z_~!/\\\\]{1,255}$";

	private static final String EXECUTABLE_FILE = "exe";

	private String displayName;
	private String bin;
	private String autorun = "no";
	private String fileType = EXECUTABLE_FILE;

	@Override
	public void parse(String packageType, Element el) {
		setPackageType(packageType);

		displayName = el.attributeValue("display_name");
		validateDisplayName(el, displayName);

		bin = el.attributeValue("bin");
		validateBin(el, bin);

		if (StringUtils.isNotEmpty(el.attributeValue("autorun"))) {
			autorun = el.attributeValue("autorun").toLowerCase();
			validateAutorun(el, autorun);
		}

		if (StringUtils.isNotEmpty(el.attributeValue("file_type"))) {
			fileType = el.attributeValue("file_type").toLowerCase();
		}
	}

	private void validateDisplayName(Element el, String displayName) {
		if (displayName == null) {
			throw new PackageException("msg.phonenixPackage.manifest.application.noDisplayName",
					new String[] { el.getName() });
		}
		if (displayName.isEmpty()) {
			throw new PackageException("msg.phonenixPackage.manifest.application.emptyDisplayName",
					new String[] { el.getName() });
		}
		if (!validateInput(APPLICATION_DISPLAY_NAME, displayName)) {
			throw new PackageException("msg.phonenixPackage.manifest.application.invalidDisplayName",
					new String[] { el.getName() });
		}
	}

	private void validateBin(Element el, String bin) {
		if (bin == null) {
			throw new PackageException("msg.phonenixPackage.manifest.application.noBin", new String[] { el.getName() });
		}
		if (StringUtils.isEmpty(bin)) {
			throw new PackageException("msg.phonenixPackage.manifest.application.emptyBin",
					new String[] { el.getName() });
		}
		if (!validateInput(BIN_PATTERN, bin)) {
			throw new PackageException("msg.phonenixPackage.manifest.application.invalidBin",
					new String[] { el.getName() });
		}
	}

	private void validateAutorun(Element el, String autorun) {
		if (!StringUtils.equalsIgnoreCase(autorun, "no") && !StringUtils.equalsIgnoreCase(autorun, "yes")) {
			throw new PackageException("msg.phonenixPackage.manifest.application.invalidAutorun",
					new String[] { el.getName() });
		}
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getBin() {
		return bin;
	}

	public void setBin(String bin) {
		this.bin = bin;
	}

	public String getAutorun() {
		return autorun;
	}

	public void setAutorun(String autorun) {
		this.autorun = autorun;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

}
