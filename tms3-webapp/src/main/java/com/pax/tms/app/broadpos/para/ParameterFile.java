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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.pax.common.fs.FileManagerUtils;
import com.pax.common.security.XEEProtectUtils;

public class ParameterFile {

	private String fileName;
	private List<ParaInfo> parameters = new ArrayList<ParaInfo>();
	HashMap<String, List<ParaInfo>> pidsMap = new HashMap<String, List<ParaInfo>>();

	private static class ParaInfo {
		Element element;
		String uniquePid;
		boolean uploadFile;

		public ParaInfo(Element element, String uniquePid, boolean uploadFile) {
			super();
			this.element = element;
			this.uniquePid = uniquePid;
			this.uploadFile = uploadFile;
		}
	}

	public ParameterFile(String fileName) {
		this.fileName = fileName;
	}

	public void add(Element e) {
		String uniquePid = ApplicationSchemaHelper.getParameterId(e);
		ParaInfo paraInfo = new ParaInfo(e, uniquePid, isUploadFile(e));
		parameters.add(paraInfo);

		String pid = e.elementText("PID");
		List<ParaInfo> elements = pidsMap.get(pid);
		if (elements == null) {
			elements = new ArrayList<>();
			pidsMap.put(pid, elements);
		}
		elements.add(paraInfo);
	}

	public List<String> copyUploadFile(Map<String, String> parameterValues, String programPackDir) throws IOException {
		List<String> unresolvedUploadFiles = new ArrayList<String>();
		for (ParaInfo parameter : parameters) {
			String uniquePid = parameter.uniquePid;
			if (parameter.uploadFile) {
				addUploadFile(uniquePid, parameter, parameterValues, programPackDir, unresolvedUploadFiles);
			}
		}
		return unresolvedUploadFiles;
	}

	private void addUploadFile(String uniquePid, ParaInfo parameter, Map<String, String> parameterValues,
			String programPackDir, List<String> unresolvedUploadFiles) throws IOException {
		String filename = parameter.element.elementTextTrim("Defaultvalue");
		/*
		 * Schema中附带的Data文件在部署时都已上传至FDFS
		 */
		String filePath = parameterValues.get(uniquePid).split("\\|")[2];
		addUploadFile(filename, filePath, programPackDir);
	}

	private boolean isUploadFile(Element parameter) {
		String dataType = parameter.elementTextTrim("DataType");
		String inputType = parameter.elementTextTrim("InputType");
		if ("upload".equalsIgnoreCase(inputType) && "File".equalsIgnoreCase(dataType)) {
			return true;
		} else {
			return false;
		}
	}

	private void addUploadFile(String fileName, String uploadFilePath, String programPackDir) throws IOException {
		FileManagerUtils.getFileManager().downloadFile(uploadFilePath, ins -> {
			FileUtils.copyInputStreamToFile(ins, new File(programPackDir, fileName));
			return null;
		});
	}

	public void writeParameterFile(Map<String, String> parameterValues, String programPackDir)
			throws DocumentException, IOException {
		HashMap<String, List<ParaInfo>> parametersMap = pidsMap;
		Document document = createParameterDocument(parametersMap, parameterValues);
		writeParameterFile(programPackDir, document);
	}

	private Document createParameterDocument(HashMap<String, List<ParaInfo>> parametersMap,
			Map<String, String> parameterValues) {
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("parameter");
		String value;
		for (Entry<String, List<ParaInfo>> entry : parametersMap.entrySet()) {
			String pid = entry.getKey();
			List<ParaInfo> elements = entry.getValue();
			if (elements.size() > 1) {
				value = getComboParameterValue(elements, parameterValues);
			} else {
				value = getValue(elements.get(0), parameterValues);
			}
			root.addElement(pid).setText(value);
		}
		return doc;
	}

	private String getValue(ParaInfo parameter, Map<String, String> parameterValues) {
		String uniquePid = parameter.uniquePid;
		if (parameterValues.containsKey(uniquePid)) {
			String val = parameterValues.get(uniquePid) == null ? "" : parameterValues.get(uniquePid);
			if (parameter.uploadFile) {
				return parameterValues.get(uniquePid).split("\\|")[0];
			} else {
				return val;
			}
		} else {
			return parameter.element.elementText("Defaultvalue");
		}
	}

	private String getComboParameterValue(List<ParaInfo> parameters, Map<String, String> parameterValues) {
		TreeMap<Integer, ParaInfo> indexToElements = new TreeMap<>();
		for (ParaInfo parameter : parameters) {
			String paramType = parameter.element.elementText("Type");
			if ("combo".equals(paramType)) {
				String index = parameter.element.elementText("Index");
				if (index != null) {
					indexToElements.put(Integer.valueOf(index), parameter);
				}
			} else {
				return getValue(parameter, parameterValues);
			}
		}

		StringBuilder value = new StringBuilder("");
		for (ParaInfo paramElement : indexToElements.values()) {
			String separator = paramElement.element.elementText("Separator");
			if (separator != null) {
				separator = separator.trim();
			}
			String defaultValue = getValue(paramElement, parameterValues);
			if (value.length() > 0) {
				value = value.append(separator);
			}
			value = value.append(defaultValue);
		}
		return value.toString();
	}

	private void writeParameterFile(String programPackDir, Document doc) throws DocumentException, IOException {
		OutputFormat fmt = OutputFormat.createPrettyPrint();
		fmt.setEncoding("UTF-8");
		fmt.setNewLineAfterDeclaration(false);
		XMLWriter writer;

		String filePath = getParameterFilePath(programPackDir);
		File file = new File(filePath);
		if (file.exists()) {
			mergeXmlFile(doc, filePath);
		} else {
			FileUtils.forceMkdirParent(new File(filePath));
		}

		try (OutputStream output = new FileOutputStream(filePath)) {
			writer = new XMLWriter(output, fmt);
			writer.write(doc);
			writer.flush();
			writer.close();
		}
	}

	private String getParameterFilePath(String destDir) {
		if (fileName.indexOf("_") > 0) {
			File parent = new File(destDir).getParentFile();
			File newDestDir = new File(parent, fileName.substring(0, fileName.indexOf("_")));
			return new File(newDestDir, fileName).getPath();
		} else {
			return new File(destDir, fileName).getPath();
		}
	}

	@SuppressWarnings("unchecked")
	private Document mergeXmlFile(Document document, String filePath) throws DocumentException, IOException {
		Document existing = XEEProtectUtils.createSAXReader().read(new File(filePath));
		List<Element> elements = existing.getRootElement().elements();
		Element root = (Element) document.getRootElement();
		for (Element element : elements) {
			root.add(element.detach());
		}
		return document;
	}

}
