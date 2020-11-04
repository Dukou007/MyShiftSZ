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

import java.util.regex.Pattern;

import org.dom4j.Element;

public abstract class ChildElement {

	private static final String APPLICATION_NAME_PATTERN = "^[\\x20-\\x7e]{1,128}$";

	private String packageType;

	public ChildElement() {
	}

	public String getPackageType() {
		return packageType;
	}

	public void setPackageType(String packageType) {
		this.packageType = packageType;
	}

	protected boolean validateInput(String regex, String input) {
		return Pattern.matches(regex, input);
	}

	protected void validateApplicationName(Element el, String applicationNameInput) {
		if (applicationNameInput == null) {
			throw new PackageException("msg.phonenixPackage.manifest.el.noApplicationName",
					new String[] { el.getName() });
		}

		if (applicationNameInput.length() == 0) {
			throw new PackageException("msg.phonenixPackage.manifest.el.emptyApplicationName",
					new String[] { el.getName() });
		}

		if (!validateInput(APPLICATION_NAME_PATTERN, applicationNameInput)) {
			throw new PackageException("msg.phonenixPackage.manifest.el.invaliadApplicationName",
					new String[] { el.getName() });
		}
	}

	public abstract void parse(String packageType, Element el);

}
