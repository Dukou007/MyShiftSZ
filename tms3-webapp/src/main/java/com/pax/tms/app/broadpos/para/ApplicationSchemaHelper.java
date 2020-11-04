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
package com.pax.tms.app.broadpos.para;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.util.CollectionUtils;
import org.dom4j.Document;
import org.dom4j.Element;

import com.pax.common.fs.FileManagerUtils;

public class ApplicationSchemaHelper {

	public static String getParameterId(Element parameter) {
		String type = parameter.elementTextTrim("Type");
		String pid = parameter.elementTextTrim("PID");
		if ("combo".equalsIgnoreCase(type)) {
			String index = parameter.elementTextTrim("Index");
			pid = StringUtils.isNotEmpty(index) ? (pid + "_" + index) : (pid);
		}
		return pid;
	}

	@SuppressWarnings("unchecked")
	public static void setApplicationParams(Document schema, Map<String, String> paramValues,
			Map<String, String> specifiedTitleParamValues) {
		if (schema == null || schema.getRootElement() == null
				|| (CollectionUtils.isEmpty(paramValues) && CollectionUtils.isEmpty(specifiedTitleParamValues))) {
			return;
		}

		List<Element> programElements = schema.getRootElement().elements("Program");
		for (Element programElement : programElements) {
			List<Element> schemaElements = programElement.elements("Schema");
			for (Element schemaElement : schemaElements) {
				setModuleParams(schemaElement, paramValues, specifiedTitleParamValues);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static void setModuleParams(Element module, Map<String, String> paramValues,
			Map<String, String> specifiedTitleParamValues) {
		List<Element> parametersElements = module.elements("Parameters");
		for (Element parametersElement : parametersElements) {
			setParametersElement(parametersElement, paramValues, specifiedTitleParamValues);
		}
	}

	@SuppressWarnings("unchecked")
	private static void setParametersElement(Element parametersElement, Map<String, String> paramValues,
			Map<String, String> specifiedTitleParamValues) {
		List<Element> parameterElements = parametersElement.elements("Parameter");
		for (Element parameter : parameterElements) {
			setParameterElement(parameter, paramValues, specifiedTitleParamValues);
		}
		List<Element> headerElements = parametersElement.elements("Header");
		for (Element header : headerElements) {
			if ("true".equalsIgnoreCase(header.elementTextTrim("Display"))) {
				setParametersElement(header, paramValues, specifiedTitleParamValues);
			}
		}
	}

	private static void setParameterElement(Element parameter, Map<String, String> paramValues,
			Map<String, String> specifiedTitleParamValues) {
		if ("true".equalsIgnoreCase(parameter.elementTextTrim("Readonly"))
				|| !"true".equalsIgnoreCase(parameter.elementTextTrim("Display"))
				|| parameter.element("Defaultvalue") == null) {
			return;
		}

		if (paramValues != null) {
			String finalPid = getParameterId(parameter);
			if (paramValues.containsKey(finalPid)) {
				parameter.element("Defaultvalue").setText(paramValues.get(finalPid));
			}
		}

		if (specifiedTitleParamValues != null) {
			String title = "";
			if (parameter.element("Title") != null) {
				title = parameter.elementTextTrim("Title");
			}

			if (specifiedTitleParamValues.containsKey(title)) {
				String value = specifiedTitleParamValues.get(title);
				if (value != null) {
					parameter.element("Defaultvalue").setText(value);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static Map<String, String> extractApplicationParams(Document document) {
		Map<String, String> params = new HashMap<String, String>();
		List<Element> programElements = document.getRootElement().elements("Program");
		for (Element programElement : programElements) {
			List<Element> schemaElements = programElement.elements("Schema");
			for (Element schemaElement : schemaElements) {
				extractModuleParams(schemaElement, params);
			}
		}
		return params;
	}

	@SuppressWarnings("unchecked")
	public static void extractModuleParams(Element schemaElement, Map<String, String> params) {
		List<Element> parametersElements = schemaElement.elements("Parameters");
		for (Element parametersElement : parametersElements) {
			extractParametersElement(parametersElement, params);
		}
	}

	@SuppressWarnings("unchecked")
	private static void extractParametersElement(Element parametersElement, Map<String, String> params) {
		List<Element> parameterElements = parametersElement.elements("Parameter");
		for (Element parameter : parameterElements) {
			String finalPid = getParameterId(parameter);
			String defaultValue = "";
			if (parameter.element("Defaultvalue") != null) {
				defaultValue = parameter.elementTextTrim("Defaultvalue");
			}
			params.put(finalPid, defaultValue);
		}

		List<Element> headerElements = parametersElement.elements("Header");
		for (Element headerElement : headerElements) {
			extractParametersElement(headerElement, params);
		}
	}

	public static String createSchemaTempFile() throws IOException {
		return File.createTempFile("param-schema-", ".xml").getPath();
	}

	public static String saveSchemaToDfs(String schemaFilePath) throws IOException {
		File file = new File(schemaFilePath);
		try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(schemaFilePath))) {
			return FileManagerUtils.getFileManager().uploadFile(inputStream, file.length(), "xml");
		}
	}

	public static void writeSchemaToLocalFile(Document document, File filePath) throws IOException {
		document.setXMLEncoding("UTF-8");
		try (Writer writer = new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8")) {
			document.write(writer);
			writer.flush();
		}
	}

	public static Map<String, String> unpackParaFiles(String parameterPackageFilePath, List<String> fileNames,
			File tempDir) throws IOException {
		return FileManagerUtils.getFileManager().downloadFile(parameterPackageFilePath, ins -> {
			return getZipEntryFile(fileNames, ins, tempDir);
		});
	}

	private static Map<String, String> getZipEntryFile(List<String> fileNames, InputStream ins, File tempDir)
			throws IOException {
		ZipEntry zipEntry = null;
		Map<String, String> result = new HashMap<String, String>();
		ZipInputStream zipInput = new ZipInputStream(ins);
		while ((zipEntry = zipInput.getNextEntry()) != null) {
			if (zipEntry.isDirectory()) {
				continue;
			}
			String entryName = zipEntry.getName();
			for (String filename : fileNames) {
				if (entryName.endsWith(filename)) {
					File destFile = new File(tempDir, filename);
					FileUtils.copyInputStreamToFile(zipInput, destFile);
					result.put(filename, destFile.getPath());
					break;
				}
			}
		}
		return result;
	}

	public static String getParameterFileName(String filePath) {
		int index = filePath.lastIndexOf("/");
		return filePath.substring(index + 1);
	}

}
