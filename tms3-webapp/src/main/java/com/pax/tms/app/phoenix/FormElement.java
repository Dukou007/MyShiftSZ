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

public class FormElement extends ChildElement {

	private String applicationName;
	private String destination = "local";

	@Override
	public void parse(String packageType, Element el) {
		setPackageType(packageType);

		applicationName = el.attributeValue("application_name");
		validateApplicationName(el, applicationName);

		if (StringUtils.isNotEmpty(el.attributeValue("destination"))) {
			destination = el.attributeValue("destination").toLowerCase();
			validateDestination(el, destination);
		}
	}

	private void validateDestination(Element el, String destination) {
		if (!StringUtils.equalsIgnoreCase(destination, "local") && !StringUtils.equalsIgnoreCase(destination, "sdcard")
				&& !StringUtils.equalsIgnoreCase(destination, "usbstorage")) {
			throw new PackageException("msg.phonenixPackage.manifest.form.invalidLocal", new String[] { el.getName() });
		}
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

}
