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
package com.pax.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jxls.transformer.XLSTransformer;

public class ExcelTemplate {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelTemplate.class);

	private ExcelTemplate() {
	}

	public static void generateFromTemplate(InputStream template, OutputStream outputStream, Map<String, Object> params)
			throws IOException, InvalidFormatException {
		try {
			XLSTransformer transformer = new XLSTransformer();
			XSSFWorkbook book = (XSSFWorkbook) transformer.transformXLS(template, params);
			book.write(outputStream);
			outputStream.flush();
		} finally {
			if (template != null) {
				try {
					template.close();
				} catch (IOException e) {
					LOGGER.error("IOException trying to close InputStream", e);
				}
			}
		}
	}
}
