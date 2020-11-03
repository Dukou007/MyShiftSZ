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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import com.pax.common.fs.FileManagerUtils;
import com.pax.common.security.XEEProtectUtils;

public class ApplicationModule {

	private static final int MAX_SCHEMA_SIZE = 4 * 1024 * 1024;

	private Long programId;
	private String abbrName;
	private String pctFilePath;

	public static Document readSchemaFromDfs(String schemaFilePath) {
		byte[] data = FileManagerUtils.getFileManager().downloadFile(schemaFilePath, ins -> {
			return readSchemaFileData(ins);
		});

		SAXReader sr = XEEProtectUtils.createSAXReader();
		try {
			return sr.read(new ByteArrayInputStream(data));
		} catch (DocumentException e) {
			throw new ParameterSchemaException("Failed to parse paremeter schema", e);
		}
	}

	private static byte[] readSchemaFileData(InputStream ins) {
		ZipEntry zipEntry = null;
		try (ZipInputStream zipInput = new ZipInputStream(ins)) {
			while ((zipEntry = zipInput.getNextEntry()) != null) {
				if ((zipEntry.getName().endsWith(".ps"))) {
					if (zipEntry.getSize() > MAX_SCHEMA_SIZE) {
						throw new ParameterSchemaException(
								"Schema content length exceed the limit size " + MAX_SCHEMA_SIZE);
					}
					byte[] data = new byte[(int) zipEntry.getSize()];
					IOUtils.read(zipInput, data);
					return data;
				}
			}
		} catch (IOException e) {
			throw new ParameterSchemaException("Failed to read paremeter package", e);
		}
		return new byte[0];
	}

	public static Document readSchema(InputStream ins) throws IOException {
		ZipEntry zipEntry = null;
		SAXReader sr = XEEProtectUtils.createSAXReader();
		try (ZipInputStream zipInput = new ZipInputStream(ins)) {
			while ((zipEntry = zipInput.getNextEntry()) != null) {
				if ((zipEntry.getName().endsWith(".ps"))) {
					if (zipEntry.getSize() > MAX_SCHEMA_SIZE) {
						throw new ParameterSchemaException(
								"Schema content length exceed the limit size " + MAX_SCHEMA_SIZE);
					}
					return sr.read(ins);
				}
			}
		} catch (DocumentException e) {
			throw new ParameterSchemaException("Failed to parse paremeter schema", e);
		} catch (IOException e) {
			throw new ParameterSchemaException("Failed to read paremeter package", e);
		}
		return null;
	}

	public static Map<String, byte[]> readModuleDataFileFromDfs(String moduleConfPath) {
		return FileManagerUtils.getFileManager().downloadFile(moduleConfPath, ins -> {
			return readModuleDataFile(ins);
		});
	}

	public static Map<String, byte[]> readModuleDataFile(InputStream ins) {
		ZipEntry zipEntry = null;
		Map<String, byte[]> resultMap = new HashMap<String, byte[]>();
		try (ZipInputStream zipInput = new ZipInputStream(ins)) {
			while ((zipEntry = zipInput.getNextEntry()) != null) {
				//xxx_param/xxxxx
				if (!(zipEntry.getName().endsWith(".ps")) && !zipEntry.isDirectory()) {
					if (zipEntry.getSize() > MAX_SCHEMA_SIZE) {
						throw new ParameterSchemaException(
								"Schema content length exceed the limit size " + MAX_SCHEMA_SIZE);
					}
					byte[] data = new byte[(int) zipEntry.getSize()];
					IOUtils.read(zipInput, data);
					resultMap.put(new File(zipEntry.getName()).getName(), data);
				}
			}
			return resultMap;
		} catch (IOException e) {
			throw new ParameterSchemaException("Failed to read paremeter package", e);
		}
	}

	public Long getProgramId() {
		return programId;
	}

	public void setProgramId(Long programId) {
		this.programId = programId;
	}

	public String getAbbrName() {
		return abbrName;
	}

	public void setAbbrName(String abbrName) {
		this.abbrName = abbrName;
	}

	public String getPctFilePath() {
		return pctFilePath;
	}

	public void setPctFilePath(String pctFilePath) {
		this.pctFilePath = pctFilePath;
	}

}
