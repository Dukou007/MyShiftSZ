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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;

import com.pax.common.util.ZipFileUtil;

public class ProgramParameter {
	private String id;
	private String abbrName;
	private String versionFileName;
	private String zipFileName;
	private Element programSchemaElement;
	private Map<String, ParameterFile> parameterFiles;

	public ProgramParameter(String id, String abbrName, Element programSchemaElement) {
		this.id = id;
		this.abbrName = abbrName;
		this.versionFileName = abbrName + ".pv";
		this.zipFileName = abbrName + ".zip";
		this.programSchemaElement = programSchemaElement;
		this.parameterFiles = getParameterFiles(programSchemaElement);
	}

	@SuppressWarnings("unchecked")
	private static Map<String, ParameterFile> getParameterFiles(Element schemaElement) {
		Map<String, ParameterFile> parameterFiles = new HashMap<String, ParameterFile>();
		if (schemaElement == null || schemaElement.isTextOnly() || schemaElement.element("Files") == null) {
			return parameterFiles;
		}

		Element filesElement = schemaElement.element("Files");
		List<Element> files = filesElement.elements("File");
		for (Element file : files) {
			String fileId = file.elementTextTrim("ID");
			String fileName = file.elementTextTrim("FileName");
			if (!parameterFiles.containsKey(fileId)) {
				parameterFiles.put(fileId, new ParameterFile(fileName));
			}
		}
		return parameterFiles;
	}

	@SuppressWarnings("unchecked")
	public void addParameters(Map<String, ProgramParameter> programParemeters) {
		List<Element> parametersElements = programSchemaElement.elements("Parameters");
		for (Element parametersElement : parametersElements) {
			addParametersElement(parametersElement, programParemeters);
		}
	}

	@SuppressWarnings("unchecked")
	private void addParametersElement(Element parametersElement, Map<String, ProgramParameter> programParemeters) {
		List<Element> parameterElements = parametersElement.elements("Parameter");
		for (Element parameter : parameterElements) {
			addParameterElement(parameter, programParemeters);
		}

		List<Element> headerElements = parametersElement.elements("Header");
		for (Element header : headerElements) {
			addParametersElement(header, programParemeters);
		}
	}

	private void addParameterElement(Element parameter, Map<String, ProgramParameter> programParemeters) {
		if ("label".equalsIgnoreCase(parameter.elementTextTrim("InputType"))) {
			return;
		}

		String fileId = parameter.elementTextTrim("FileID");
		ParameterFile parameterFile = getParameterFile(fileId);
		if (parameterFile != null) {
			parameterFile.add(parameter);
			return;
		}

		for (ProgramParameter otherApplicationModule : programParemeters.values()) {
			parameterFile = otherApplicationModule.getParameterFile(fileId);
			if (parameterFile != null) {
				parameterFile.add(parameter);
			}
		}
	}

	public String pack(String deployPackDir, Map<String, String> parameterValus, String parameterVersion,
			String charset) throws IOException, DocumentException {
		String zipFilePath = new File(deployPackDir, zipFileName).getPath();
		File file = null;

		try {
			file = new File(deployPackDir, abbrName);
			if (!(file.exists())) {
				file.mkdir();
			}

			String programPackDir = file.getPath();

			copyUploadFile(parameterValus, programPackDir);
			outputParameterFiles(parameterValus, programPackDir);
			createVersionFile(parameterVersion, programPackDir);
			compress(zipFilePath, programPackDir, charset);
		} finally {
			if (file != null) {
				destory(file);
			}
		}

		return zipFilePath;
	}

	private void copyUploadFile(Map<String, String> parameterValus, String programPackDir) throws IOException {
		for (ParameterFile parmfile : parameterFiles.values()) {
			parmfile.copyUploadFile(parameterValus, programPackDir);
		}
	}

	private void outputParameterFiles(Map<String, String> parameterValues, String programPackDir)
			throws IOException, DocumentException {
		for (ParameterFile parmfile : parameterFiles.values()) {
			parmfile.writeParameterFile(parameterValues, programPackDir);
		}
	}

	private void createVersionFile(String version, String programPackDir) throws IOException {
		Document doc = DocumentHelper.createDocument();
		doc.addElement("paraminfo").addElement("version").setText(version);
		XMLWriter writer;
		try (OutputStream output = new FileOutputStream(new File(programPackDir, versionFileName))) {
			writer = new XMLWriter(output);
			writer.write(doc);
			writer.flush();
			writer.close();
		}
	}

	private void compress(String zipFilePath, String programPackDir, String charset) throws IOException {
		ZipFileUtil.zip(zipFilePath, charset, new File(programPackDir).listFiles());
	}

	private void destory(File file) {
		FileUtils.deleteQuietly(file);
	}

	public String getAbbrName() {
		return abbrName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setAbbrName(String abbrName) {
		this.abbrName = abbrName;
	}

	public boolean containParameterFile(String fileId) {
		if (parameterFiles.containsKey(fileId)) {
			return true;
		}
		return false;
	}

	public ParameterFile getParameterFile(String fileId) {
		return parameterFiles.get(fileId);
	}

}
