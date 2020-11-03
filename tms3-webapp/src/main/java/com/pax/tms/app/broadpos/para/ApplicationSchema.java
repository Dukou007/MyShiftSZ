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
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.shiro.util.CollectionUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.pax.common.fs.FileManagerUtils;
import com.pax.common.security.XEEProtectUtils;
import com.pax.fastdfs.proto.storage.DownloadByteArray;

public class ApplicationSchema {

	private static final int MAX_SCHEMA_SIZE = 4 * 1024 * 1024;

	private List<ApplicationModule> applicationModules;

	private Document lastSchema;

	private Map<String, String> specifiedTitleParamValues;

	public ApplicationSchema() {
	}

	public static Document loadSchemaFromLocal(String schemaFilePath) {
		try (InputStream ins = new BufferedInputStream(new FileInputStream(schemaFilePath))) {
			return XEEProtectUtils.createSAXReader().read(ins);
		} catch (DocumentException e) {
			throw new ParameterSchemaException("Failed to parse application parameter schema", e);
		} catch (IOException e) {
			throw new ParameterSchemaException("Failed to read application parameter schema", e);
		}
	}

	public static Document loadSchemaFromDfs(String schemaFilePath) {
		byte[] data = FileManagerUtils.getFileManager().downloadFile(schemaFilePath,
				new DownloadByteArray(MAX_SCHEMA_SIZE));
		try {
			return XEEProtectUtils.createSAXReader().read(new ByteArrayInputStream(data));
		} catch (DocumentException e) {
			throw new ParameterSchemaException("Failed to read application parameter schema", e);
		}
	}

	public ApplicationSchema(List<ApplicationModule> applicationModules) {
		this.applicationModules = applicationModules;
	}

	public ApplicationSchema(Document lastSchema) {
		this.lastSchema = lastSchema;
	}

	public ApplicationSchema(List<ApplicationModule> applicationModules, Document lastSchema) {
		this.applicationModules = applicationModules;
		this.lastSchema = lastSchema;
	}

	public void setApplicationModules(List<ApplicationModule> applicationModules) {
		this.applicationModules = applicationModules;
	}

	public void setLastApplicationSchema(Document lastSchema) {
		this.lastSchema = lastSchema;
	}

	public void setSpecifiedTitleParamValues(Map<String, String> specifiedTitleParamValues) {
		this.specifiedTitleParamValues = specifiedTitleParamValues;
	}

	public Document createSchemaDocument() {
		if (!CollectionUtils.isEmpty(applicationModules)) {
			return createNewApplicationSchema();
		} else if (lastSchema != null) {
			ApplicationSchemaHelper.setApplicationParams(lastSchema, null, specifiedTitleParamValues);
			return lastSchema;
		}
		return null;
	}

	public Document createNewApplicationSchema() {
		Document document = DocumentHelper.createDocument();
		Element rootElement = document.addElement("Root");

		for (ApplicationModule appModule : applicationModules) {
			addSchemaElement(rootElement, appModule);
		}

		Map<String, String> lastParamValues = null;
		if (lastSchema != null) {
			lastParamValues = ApplicationSchemaHelper.extractApplicationParams(lastSchema);
		}

		ApplicationSchemaHelper.setApplicationParams(document, lastParamValues, specifiedTitleParamValues);
		return document;
	}

	private void addSchemaElement(Element rootElement, ApplicationModule appModule) {
		Element programElement = rootElement.addElement("Program");
		programElement.addElement("ID").setText(appModule.getProgramId().toString());
		programElement.addElement("AbbrName").setText(appModule.getAbbrName());
		if (appModule.getPctFilePath() == null) {
			programElement.addElement("Schema");
		} else {
			Document schemaDocument = ApplicationModule.readSchemaFromDfs(appModule.getPctFilePath());
			if (schemaDocument == null) {
				programElement.addElement("Schema");
			} else {
				programElement.add(schemaDocument.getRootElement());
			}
		}
	}
}
