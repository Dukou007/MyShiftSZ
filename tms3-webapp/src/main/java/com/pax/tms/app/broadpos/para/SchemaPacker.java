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

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

public class SchemaPacker implements Closeable {

	private String charset = "ISO8859-1";

	private Document schema;

	private Map<String, ProgramParameter> programParameters;

	private String schemaPackDir;

	public SchemaPacker(Document schema) {
		this.schema = schema;
		this.programParameters = createProgramParameters();
	}

	@SuppressWarnings("unchecked")
	private Map<String, ProgramParameter> createProgramParameters() {
		Map<String, ProgramParameter> programParameters = new HashMap<>();
		List<Element> programs = schema.getRootElement().elements("Program");
		for (Element program : programs) {
			Element programSchema = program.element("Schema");
			if (programSchema == null || programSchema.isTextOnly() || programSchema.element("Files") == null) {
				continue;
			}

			String id = program.elementTextTrim("ID");
			String abbrName = program.elementTextTrim("AbbrName");
			ProgramParameter programParameter = programParameters.get(id);
			if (programParameter == null) {
				programParameter = new ProgramParameter(id, abbrName, programSchema);
				programParameters.put(id, programParameter);
			}
		}

		for (ProgramParameter programParameter : programParameters.values()) {
			programParameter.addParameters(programParameters);
		}
		return programParameters;
	}

	private synchronized void createSchemaPackDir() throws IOException {
		if (schemaPackDir == null) {
			schemaPackDir = Files.createTempDirectory("schema-pack-").toString();
		}
	}

	public Map<String, String> pack(String deployId, Map<String, String> parameterValus, String parameterVersion)
			throws IOException, DocumentException {
		if (schemaPackDir == null) {
			createSchemaPackDir();
		}

		String deployPackDir = new File(schemaPackDir, deployId).getPath();

		Map<String, String> parameterFiles = new HashMap<String, String>(programParameters.size());

		for (ProgramParameter programParameter : programParameters.values()) {
			String zipFilePath = programParameter.pack(deployPackDir, parameterValus, parameterVersion, charset);
			parameterFiles.put(programParameter.getId(), zipFilePath);
		}

		return parameterFiles;
	}

	@Override
	public void close() throws IOException {
		if (schemaPackDir != null) {
			FileUtils.deleteQuietly(new File(schemaPackDir));
			schemaPackDir = null;
		}
	}
}
