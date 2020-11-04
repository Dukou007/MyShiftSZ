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
package com.pax.common.web.message;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalConversionFileReader {
	private static final Logger LOGGER = LoggerFactory.getLogger(LocalConversionFileReader.class);
	private String directory = File.separator;
	private String basename = "convert";

	public LocalConversionFileReader(String directory) {
		this.directory = directory;
	}

	public LocalConversionFileReader(String directory, String basename) {
		this.directory = directory;
		this.basename = basename;
	}

	public Map<String, String> parse() {
		Map<String, String> conversions = new HashMap<>();

		if (StringUtils.isEmpty(this.directory)) {
			LOGGER.warn("the directory is not correct");
			return conversions;
		}

		URL url = this.getClass().getClassLoader().getResource(directory);
		if (url == null) {
			LOGGER.warn("load conversion file failed, could not found directory:{}", directory);
			return conversions;
		}
		File file = null;
		try {
			file = new File(url.toURI());
		} catch (URISyntaxException e) {
			LOGGER.error("load conversion file failed, illegal URL", e);
		}
		if (file != null && file.exists()) {
			LOGGER.debug("parse local conversion file:{}", file.getName());
			File[] files = file.listFiles(new ConversionFilenameFilter());
			for (File f : files) {
				String lang = f.getName().substring(0, f.getName().lastIndexOf('.')).split("_")[1];
				try {
					SAXReader reader = new SAXReader();
					Document doc = reader.read(f);
					for (Element e : this.getConvertNodes(doc)) {
						conversions.putAll(this.parseElements(e, lang));
					}
				} catch (DocumentException e) {
					LOGGER.error("parse conversion file failed", e);
				}
			}
		} else {
			LOGGER.error("directory not exist");
		}

		LOGGER.debug("conversion initialization complete");
		return conversions;
	}

	protected class ConversionFilenameFilter implements FilenameFilter {
		@Override
		public boolean accept(File dir, String name) {
			return name.contains(basename) && name.endsWith(".xml");
		}
	}

	@SuppressWarnings("unchecked")
	public List<Element> getConvertNodes(Document doc) {
		return (List<Element>) doc.getRootElement().elements("CONVERT");
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> parseElements(Element convertNode, String lang) {
		Map<String, String> m = new HashMap<>();
		String convertName = convertNode.attributeValue("name");
		for (Element argNode : (List<Element>) convertNode.elements("arg")) {
			m.put(convertName + "." + argNode.attributeValue("name") + "." + lang, argNode.attributeValue("value"));
		}
		return m;
	}
}
