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

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pax.common.security.XEEProtectUtils;

public class ManifestFile {

	private static final Logger LOGGER = LoggerFactory.getLogger(ManifestFile.class);

	private static final String ROOT_ELEMENT_NAME = "package";
	private static final String PACKAGE_NAME_PATTERN = "^[\\x20-\\x7e]{1,128}$";
	private static final String PACKAGE_VERSION_PATTERN = "^[\\.\\-0-9A-Za-z_]{1,64}$";
	private static final String PACKAGE_DESCRIPTION_PATTERN = "^[\\x20-\\x7e]{1,150}$";

	private static final String FIRMWARE = "firmware";
	private static final String APPLICATION = "application";

	private static final String WHITELIST = "whitelist";
	private static final String UNINSTALL = "uninstall";
	private static final String CONFIGURATION = "configuration";
	private static final String BOOT = "boot";
	private static final String AVPUK = "avpuk";
	//新增boot、avpuk类型
	private static final Set<String> PACKAGE_TYPES = new HashSet<String>(
			Arrays.asList(FIRMWARE, APPLICATION, "form", "emv", WHITELIST, UNINSTALL, CONFIGURATION, BOOT, AVPUK, "combo"));

	private static final Map<String, List<ChildElementSpec>> childElementSpecsMap = new HashMap<String, List<ChildElementSpec>>();
	static {

		ChildElementSpec filesElementSpec = new ChildElementSpec("files", false, true, FilesElement.class);

		List<ChildElementSpec> firmwarePackageElements = new ArrayList<ChildElementSpec>();
		firmwarePackageElements.add(new ChildElementSpec(FIRMWARE, false, true, FirmwareElement.class));
		firmwarePackageElements.add(filesElementSpec);
		childElementSpecsMap.put(FIRMWARE, firmwarePackageElements);

		List<ChildElementSpec> applicationPackageElements = new ArrayList<ChildElementSpec>();
		applicationPackageElements.add(new ChildElementSpec(APPLICATION, false, true, ApplicationElement.class));
		applicationPackageElements.add(filesElementSpec);
		childElementSpecsMap.put(APPLICATION, applicationPackageElements);

		List<ChildElementSpec> formPackageElements = new ArrayList<ChildElementSpec>();
		formPackageElements.add(new ChildElementSpec("form", false, true, FormElement.class));
		formPackageElements.add(filesElementSpec);
		childElementSpecsMap.put("form", formPackageElements);

		List<ChildElementSpec> emvPackageElements = new ArrayList<ChildElementSpec>();
		emvPackageElements.add(new ChildElementSpec("emv", false, true, EmvElement.class));
		emvPackageElements.add(filesElementSpec);
		childElementSpecsMap.put("emv", emvPackageElements);

		List<ChildElementSpec> whitelistPackageElements = new ArrayList<ChildElementSpec>();
		whitelistPackageElements.add(new ChildElementSpec(WHITELIST, false, true, WhitelistElement.class));
		whitelistPackageElements.add(filesElementSpec);
		childElementSpecsMap.put(WHITELIST, whitelistPackageElements);

		List<ChildElementSpec> uninstallPackageElements = new ArrayList<ChildElementSpec>();
		uninstallPackageElements.add(new ChildElementSpec(UNINSTALL, false, true, UninstallElement.class));
		uninstallPackageElements.add(filesElementSpec);
		childElementSpecsMap.put(UNINSTALL, uninstallPackageElements);

		List<ChildElementSpec> configurationPackageElements = new ArrayList<ChildElementSpec>();
		configurationPackageElements.add(new ChildElementSpec(CONFIGURATION, false, true, ConfigurationElement.class));
		configurationPackageElements.add(filesElementSpec);
		childElementSpecsMap.put(CONFIGURATION, configurationPackageElements);
		
		List<ChildElementSpec> bootPackageElements = new ArrayList<ChildElementSpec>();
		bootPackageElements.add(new ChildElementSpec(BOOT, false, true, BootElement.class));
		bootPackageElements.add(filesElementSpec);
		childElementSpecsMap.put(BOOT, bootPackageElements);
		
		List<ChildElementSpec> avpukPackageElements = new ArrayList<>();
		avpukPackageElements.add(new ChildElementSpec(AVPUK, false, true, AvpukElement.class));
		avpukPackageElements.add(filesElementSpec);
		childElementSpecsMap.put(AVPUK, avpukPackageElements);
		
		List<ChildElementSpec> comboPackageElements = new ArrayList<ChildElementSpec>();
		comboPackageElements.add(filesElementSpec);
		childElementSpecsMap.put("combo", comboPackageElements);
	}

	private String packageType;
	private String packageName;
	private String packageVersion;
	private String packageDescription;
	private List<ChildElement> childElements = Collections.emptyList();

	public static ManifestFile parse(String filePath) {
		Document doc = null;
		SAXReader reader = XEEProtectUtils.createSAXReader();
		try {
			doc = reader.read(new File(filePath));
			ManifestFile manifestFile = new ManifestFile();
			manifestFile.parse(doc);
			return manifestFile;
		} catch (DocumentException e) {
			throw new PackageException("msg.phonenixPackage.manifest.readFailed", new String[] { e.getMessage() }, e);
		}
	}

	public static ManifestFile parse(InputStream inputStream) {
		Document doc = null;
		SAXReader reader = XEEProtectUtils.createSAXReader();
		try {
			doc = reader.read(inputStream);
			ManifestFile manifestFile = new ManifestFile();
			manifestFile.parse(doc);
			return manifestFile;
		} catch (DocumentException e) {
			throw new PackageException("msg.phonenixPackage.manifest.readFailed", new String[] { e.getMessage() }, e);
		}
	}

	public void parse(Document doc) {
		// parse root element
		Element root = doc.getRootElement();
		validateRootElement(root);

		// parse package type attribute
		packageType = root.attributeValue("type");
		validatePackageType(packageType);

		// parse package name attribute
		packageName = root.attributeValue("name");
		validatePackageName(packageName);

		// parse package version attribute
		packageVersion = root.attributeValue("version");
		validatePackageVersion(packageVersion);

		// parse description name attribute
		packageDescription = root.attributeValue("description");
		validatePackageDesc(packageDescription);

		// parse child elements
		parsetChildElements(root);

	}

	private void validateRootElement(Element root) {
		if (root == null || !StringUtils.equalsIgnoreCase(ROOT_ELEMENT_NAME, root.getName())) {
			throw new PackageException("msg.phonenixPackage.manifest.invalidRoot");
		}
	}

	private void validatePackageType(String packageTypeInput) {
		if (StringUtils.isEmpty(packageTypeInput)) {
			throw new PackageException("msg.phonenixPackage.manifest.noPackageType");
		}
		if (!PACKAGE_TYPES.contains(packageTypeInput.toLowerCase())) {
			throw new PackageException("msg.phonenixPackage.manifest.invalidPackageType",
					new String[] { packageTypeInput });
		}
	}

	private void validatePackageName(String packageNameInput) {
		if (StringUtils.isEmpty(packageNameInput)) {
			throw new PackageException("msg.phonenixPackage.manifest.emptyPackageName");
		}
		if (!validateInput(PACKAGE_NAME_PATTERN, packageNameInput)) {
			throw new PackageException("msg.phonenixPackage.manifest.invliadPackageName");
		}
	}

	private void validatePackageVersion(String packageVersionInput) {
		if (StringUtils.isEmpty(packageVersionInput)) {
			throw new PackageException("msg.phonenixPackage.manifest.emptyPackageVersion");
		}
		if (!validateInput(PACKAGE_VERSION_PATTERN, packageVersionInput)) {
			throw new PackageException("msg.phonenixPackage.manifest.invliadPackageVersion");
		}
	}

	private void validatePackageDesc(String packageDescriptionInput) {
		if (StringUtils.isEmpty(packageDescriptionInput)) {
			throw new PackageException("msg.phonenixPackage.manifest.emptyPackageDesc");
		}
		if (!validateInput(PACKAGE_DESCRIPTION_PATTERN, packageDescriptionInput)) {
			throw new PackageException("msg.phonenixPackage.manifest.invliadPackageDesc");
		}
	}

	@SuppressWarnings("unchecked")
	private void parsetChildElements(Element root) {
		List<ChildElementSpec> childElementSpecs = childElementSpecsMap.get(packageType.toLowerCase());
		if (childElementSpecs == null) {
			return;
		}

		childElements = new ArrayList<ChildElement>();
		for (ChildElementSpec childElementSpec : childElementSpecs) {
			List<Element> elements = root.elements(childElementSpec.getElementName());
			if (childElementSpec.isElementRequired() && CollectionUtils.isEmpty(elements)) {
				throw new PackageException("msg.phonenixPackage.manifest.noChildElement", new String[] { packageType });
			}

			if (!childElementSpec.isElementRepeatable() && elements != null && elements.size() > 1) {
				throw new PackageException("msg.phonenixPackage.manifest.duplicatedChildElement",
						new String[] { packageType });
			}

			for (Element element : elements) {
				ChildElement childElement;
				try {
					childElement = childElementSpec.getInstanceClass().newInstance();
					childElement.parse(packageType, element);
					childElements.add(childElement);
				} catch (InstantiationException e) {
					LOGGER.error("parse manifest file failed", e);
				} catch (IllegalAccessException e) {
					LOGGER.error("parse manifest file failed", e);
				}
			}
		}
	}

	private boolean validateInput(String regex, String input) {
		return Pattern.matches(regex, input);
	}

	public String getPackageType() {
		return packageType;
	}

	public void setPackageType(String packageType) {
		this.packageType = packageType;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getPackageVersion() {
		return packageVersion;
	}

	public void setPackageVersion(String packageVersion) {
		this.packageVersion = packageVersion;
	}

	public String getPackageDescription() {
		return packageDescription;
	}

	public void setPackageDescription(String packageDescription) {
		this.packageDescription = packageDescription;
	}

	public List<ChildElement> getChildElements() {
		return childElements;
	}

	public void setChildElements(List<ChildElement> childElements) {
		this.childElements = childElements;
	}

	public List<FileElement> getFileElements() {
		for (ChildElement childElement : getChildElements()) {
			if (childElement instanceof FilesElement) {
				return ((FilesElement) childElement).getFileElements();
			}
		}
		return Collections.emptyList();
	}
}
