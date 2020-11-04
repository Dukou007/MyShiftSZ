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
package com.pax.tms.app.broadpos;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.pax.common.security.XEEProtectUtils;
import com.pax.tms.app.phoenix.PackageException;

public class ConfigFile {

	private Map<String, String> app = Collections.emptyMap();

	private Map<String, String> firmware = Collections.emptyMap();

	private List<Map<String, String>> firmwarePrograms = Collections.emptyList();

	private List<Map<String, String>> modulePrograms = Collections.emptyList();

	private String rootCert;

	private String scriptCert;

	public static ConfigFile parse(String filePath) {
		Document doc = null;
		SAXReader reader = XEEProtectUtils.createSAXReader();
		try {
			doc = reader.read(new File(filePath));
			ConfigFile configFile = new ConfigFile();
			configFile.parse(doc);
			return configFile;
		} catch (DocumentException e) {
			throw new PackageException("msg.broadposPackage.configFile.readFailed", new String[] { e.getMessage() }, e);
		}
	}

	public void parse(Document doc) {
		Element root = doc.getRootElement();
		Element appElement = root.element("app");
		if (appElement == null) {
			throw new PackageException("msg.broadposPackage.configFile.noAppElement");
		}
		app = app(appElement);

		Element firmwareElement = appElement.element("firmware");
		if (firmwareElement != null) {
			firmware = firmware(firmwareElement);
			firmwarePrograms = firmwarePrograms(firmwareElement);
		}

		Element moduleElement = appElement.element("module");
		if (moduleElement != null) {
			modulePrograms = modulePrograms(moduleElement);
		}

		Element rootCertElement = appElement.element("rc");
		if (rootCertElement != null) {
			rootCert = StringUtils.trim(rootCertElement.getText());
		}

		Element scriptCertElement = appElement.element("sc");
		if (scriptCertElement != null) {
			scriptCert = StringUtils.trim(scriptCertElement.getText());
		}
	}

	/**
	 * <p>
	 * 
	 * POS Application Version:
	 * 
	 * <li>APP_NAME: Application Name. (Required)
	 * 
	 * <li>APP_VER: Application Version. (Required)
	 * 
	 * <li>RLS_NOTE: Application Release Notes.
	 * 
	 * <li>APPV_DESC: Application Version Description.
	 * 
	 * <li>CAP_TYPE: Application Capture Type.
	 * 
	 * Value Label 00 NULL 01 Host-based 02 Terminal-based 03 Hybrid
	 * 
	 * <li>MDL_ABRNAM: Model AbbrName.
	 * 
	 * <li>IND_ID: POS Application Version Industry ID list. e.g.,
	 * "00000001|00000002"
	 * 
	 * <li>EDC_ID: POS Application Version EDC ID List. e.g.,
	 * "00000001|00000006"
	 * 
	 * </p>
	 * 
	 * <p>
	 * APP_TYPE: Application Type, default 01
	 * 
	 * Value Label 01 User Application 02 Initial Application
	 * </p>
	 */
	private Map<String, String> app(Element appElement) {
		Map<String, String> attrs = attributesToMap(appElement);

		String appName = attrs.get("APP_NAME");
		if (StringUtils.isEmpty(appName)) {
			throw new PackageException("msg.broadposPackage.configFile.emptyAppName");
		}

		String appVersion = attrs.get("APP_VER");
		if (StringUtils.isEmpty(appVersion)) {
			throw new PackageException("msg.broadposPackage.configFile.emptyAppVersion");
		}

		return attrs;
	}

	/**
	 * <p>
	 * POS Firmware Version:
	 * 
	 * <li>PI_VER: PIV Version. (Required)
	 * 
	 * <li>PIV_NAME: PIV Name. (Required)
	 * 
	 * <li>PRS_METHOD: Process Method. (Required)
	 * 
	 * Value Label 01 PAXSZ Monitor Process 02 Verix Process
	 * 
	 * <li>PIV_DESC: PIV Description.
	 * 
	 * </p>
	 * 
	 * <p>
	 * PIS_METHOD:
	 * 
	 * Value Label 01 TermSync 02 DDL
	 * 
	 * </p>
	 * 
	 */
	private Map<String, String> firmware(Element firmwareElement) {
		return attributesToMap(firmwareElement);
	}

	private List<Map<String, String>> firmwarePrograms(Element node) {
		List<Map<String, String>> programs = programs(node);

		for (Map<String, String> program : programs) {
			validateFirmwareProgram(program);
		}

		return programs;
	}

	/**
	 * <p>
	 * POS Firmware Program
	 * 
	 * <li>PIP_VER: Version. (Required)
	 * 
	 * <li>PIP_TYPE: Program Type. (Required)
	 * 
	 * Value Label 01 OS 02 BASE_LIB 03 UAI_LIB 04 BROWER 05 APP_LIB 06 SYSTEM
	 * 07 Public File 08 US PUK 09 Base Driver
	 * 
	 * <li>PIP_NAME: Program Name. (Required)
	 * 
	 * <li>PIP_DESC: Description
	 * 
	 * <li>PGO_FLNM: Program Original File Name. (Required)
	 * 
	 * <li>PGS_FLNM: Program Signature File Name.
	 * 
	 * <li>SIGN_VER: Program Signature Version.
	 * 
	 * <li>FILE: file list. e.g., "browser.bin|br.v|br.d"
	 * 
	 * </p>
	 * 
	 * <p>
	 * Program Files:
	 * 
	 * <li>PGO: Program Original File.
	 * <li>PGD: Program Digest File.
	 * <li>PGS: Program Signature File.
	 * <li>PGT: Program Target File.
	 * <p>
	 * 
	 */
	private void validateFirmwareProgram(Map<String, String> program) {
		String firmwareVersion = program.get("PIP_VER");
		if (StringUtils.isEmpty(firmwareVersion)) {
			throw new PackageException("msg.broadposPackage.configFile.emptyPipVer");
		}

		String firmwareType = program.get("PIP_TYPE");
		if (StringUtils.isEmpty(firmwareType)) {
			throw new PackageException("msg.broadposPackage.configFile.emptyPipType");
		}

		String pipType = FirmwareTypes.convent(firmwareType);
		program.put("PIP_TYPE", pipType);
		program.put("APM_ABRNAM", pipType);

		String firmwareName = program.get("PIP_NAME");
		if (StringUtils.isEmpty(firmwareName)) {
			throw new PackageException("msg.broadposPackage.configFile.emptyPipName");
		}

		String programFileName = program.get("PGO_FLNM");
		if (StringUtils.isEmpty(programFileName)) {
			throw new PackageException("msg.broadposPackage.configFile.emptyPgoFlnm");
		}
	}

	private List<Map<String, String>> modulePrograms(Element node) {

		List<Map<String, String>> programs = programs(node);

		for (Map<String, String> program : programs) {
			validateModuleProgram(program);
		}

		return programs;
	}

	/**
	 * <p>
	 * POS Application Module Version:
	 * 
	 * <li>APM_VER: Application Module Version. (Required)
	 * 
	 * <li>APMV_DSPNAM: Application Module Display Name. (Required)
	 * 
	 * <li>APMV_DESC: Application Module Version Description.
	 * 
	 * <li>PGO_FLNM: Program Original File Name. (Required)
	 * 
	 * <li>PCT_FLNM: Program Configuration Template File Name. e.g.,
	 * "tpi_param.zip"
	 * 
	 * <li>PGS_FLNM: Program Signature File ID.
	 * 
	 * <li>SIGN_VER: Signature Version.
	 * 
	 * </p>
	 * 
	 * <p>
	 * POS Application Module Information:
	 * 
	 * <li>APM_NAME: Application Module Name. (Required)
	 * 
	 * <li>APM_ABRNAM: Application Module AbbrName. (Required)
	 * 
	 * <li>APM_DESC: Application Module Description.
	 * 
	 * </p>
	 * 
	 * <p>
	 * POS Application Module Category:
	 * 
	 * <li>AMC_NAME: Application Module Category Name.
	 * 
	 * <li>AMC_TYPE: Application Module Category Select Type.
	 * 
	 * Value Label 01 MultiSelect 02 SingleSelect
	 * 
	 * <li>AMC_MODE: Application Module Category Select Mode.
	 * 
	 * Value Label 01 Must 02 Option
	 * 
	 * <li>AMC_DESC: Application Module Category Description.
	 * </p>
	 *
	 */
	private void validateModuleProgram(Map<String, String> program) {
		String moduleVersion = program.get("APM_VER");
		if (StringUtils.isEmpty(moduleVersion)) {
			throw new PackageException("msg.broadposPackage.configFile.emptyApmVer");
		}

		String moduleDisplayName = program.get("APMV_DSPNAM");
		if (StringUtils.isEmpty(moduleDisplayName)) {
			throw new PackageException("msg.broadposPackage.configFile.apmvDspNam");
		}

		String programFileName = program.get("PGO_FLNM");
		if (StringUtils.isEmpty(programFileName)) {
			throw new PackageException("msg.broadposPackage.configFile.emptyPgoFlnm");
		}

		String moduleName = program.get("APM_NAME");
		if (StringUtils.isEmpty(moduleName)) {
			throw new PackageException("msg.broadposPackage.configFile.emptyApmName");
		}

		String moduleAbbrName = program.get("APM_ABRNAM");
		if (StringUtils.isEmpty(moduleAbbrName)) {
			throw new PackageException("msg.broadposPackage.configFile.emptyApmAbrName");
		}

		program.put("PIP_TYPE", FirmwareTypes.convent("APP_MODULE"));

	}

	@SuppressWarnings("unchecked")
	private Map<String, String> attributesToMap(Element el) {
		Map<String, String> attrMap = new HashMap<String, String>();
		List<Attribute> attrs = el.attributes();
		for (Attribute attr : attrs) {
			String name = attr.getName();
			String value = StringUtils.trim(attr.getStringValue());
			attrMap.put(name, value);
		}
		return attrMap;
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, String>> programs(Element node) {
		List<Map<String, String>> programs = new ArrayList<Map<String, String>>();
		List<Element> elements = node.elements("program");
		for (Element el : elements) {
			programs.add(attributesToMap(el));
		}
		return programs;
	}

	public Map<String, String> getApp() {
		return app;
	}

	public Map<String, String> getFirmware() {
		return firmware;
	}

	public List<Map<String, String>> getFirmwarePrograms() {
		return firmwarePrograms;
	}

	public List<Map<String, String>> getModulePrograms() {
		return modulePrograms;
	}

	public String getRootCert() {
		return rootCert;
	}

	public String getScriptCert() {
		return scriptCert;
	}

}
