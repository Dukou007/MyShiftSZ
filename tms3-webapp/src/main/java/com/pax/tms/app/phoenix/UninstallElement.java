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

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

import com.pax.common.exception.BusinessException;
import com.pax.tms.location.service.AddressServiceImpl;

import io.vertx.core.json.Json;

public class UninstallElement extends ChildElement {
	
	private static final String UNINSTALLED_NAME_PATTERN = "^(\\*|[\\x20-\\x7e]{1,128})$";

	private String type;
	private String name;
	private String applicationName;

	@Override
	public void parse(String packageType, Element el) {
		setPackageType(packageType);

		type = el.attributeValue("type");
		validateType(el, type);

		name = el.attributeValue("name");
		validateName(el, name);

		applicationName = el.attributeValue("application_name");
		if (!StringUtils.equals(applicationName, "*")) {
			validateApplicationName(el, applicationName);
		}
	}

	private void validateType(Element el, String type) {
		List<String> uninstalledTypes = getUninstalledTypes();
		if (null == uninstalledTypes || uninstalledTypes.isEmpty()) {
			uninstalledTypes.add("form");
			uninstalledTypes.add("application");
		}
		
		if (type == null) {
			throw new PackageException("msg.phonenixPackage.manifest.uninstall.noType", new String[] { el.getName() });
		}

		if (type.length() == 0) {
			throw new PackageException("msg.phonenixPackage.manifest.uninstall.emptyType",
					new String[] { el.getName() });
		}

		if (!uninstalledTypes.contains(type)) {
			throw new PackageException("msg.phonenixPackage.manifest.uninstall.invalidType",
					new String[] { el.getName() });
		}
	}

	private void validateName(Element el, String name) {
		if (name == null) {
			throw new PackageException("msg.phonenixPackage.manifest.uninstall.noName", new String[] { el.getName() });
		} else if (name.length() == 0) {
			throw new PackageException("msg.phonenixPackage.manifest.uninstall.emptyName",
					new String[] { el.getName() });
		} else if (!validateInput(UNINSTALLED_NAME_PATTERN, name)) {
			throw new PackageException("msg.phonenixPackage.manifest.uninstall.invalidName",
					new String[] { el.getName() });
		}
	}
	
	private static List<String> getUninstalledTypes() {
		String uninstalledTypes = "";
		List<String> result = new LinkedList<String>();
		try {
			InputStream in = getResourceAsStream("package.json");
			uninstalledTypes = IOUtils.toString(in, Charsets.toCharset("UTF-8"));
		} catch (IOException e) {
			throw new BusinessException("msg.initTimeZone.fileNotFound", e);
		}
		if(null == uninstalledTypes || "".equals(uninstalledTypes.trim()))
		{
			return result;
		}
		@SuppressWarnings("unchecked")
		Map<String, List<String>> map = Json.decodeValue(uninstalledTypes, Map.class);
		result = map.get("UninstalledTypes");
		return result;
	}
	
	private static InputStream getResourceAsStream(String resource) {
		String stripped = resource.startsWith("/") ? resource.substring(1) : resource;
		InputStream stream = null;
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader != null) {
			stream = classLoader.getResourceAsStream(stripped);
		}
		if (stream == null) {
			stream = AddressServiceImpl.class.getResourceAsStream(resource);
		}
		if (stream == null) {
			stream = AddressServiceImpl.class.getClassLoader().getResourceAsStream(stripped);
		}
		return stream;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}
}
