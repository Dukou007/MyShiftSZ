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

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SignFile {

	private static final Logger LOGGER = LoggerFactory.getLogger(SignFile.class);

	private SignFile() {
	}

	public static void generateSignFile(String signStr, String signVersion, String signFilePath) throws IOException {
		Document document = DocumentHelper.createDocument();
		Element rootElement = document.addElement("sign");
		Element verElement = rootElement.addElement("version");
		verElement.setText(signVersion);
		Element valueElement = rootElement.addElement("value");
		valueElement.setText(signStr);
		OutputFormat fmt = OutputFormat.createPrettyPrint();
		fmt.setEncoding("UTF-8");

		XMLWriter writer = null;
		try {
			writer = new XMLWriter(new BufferedOutputStream(new FileOutputStream(signFilePath)), fmt);
			writer.write(document);
			writer.flush();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					LOGGER.error("IO exception", e);
				}
			}
		}
	}

	public static void generateSignFile(byte[] signature, String signVersion, String signFilePath) throws IOException {
		String signStr = bytesToHexString(signature);
		generateSignFile(signStr, signVersion, signFilePath);
	}

	private static String bytesToHexString(byte[] bArray) {
		if (bArray == null) {
			throw new NullPointerException();
		}
		StringBuilder sb = new StringBuilder(2 * bArray.length);

		for (int i = 0; i < bArray.length; ++i) {
			String sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}
}
