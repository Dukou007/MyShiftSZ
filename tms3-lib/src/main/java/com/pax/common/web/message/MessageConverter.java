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

import java.util.Collections;
import java.util.Map;

import org.springframework.util.StringUtils;

public class MessageConverter {
	private static final String CONVERT_DIRECTORY = "convert";

	private static class MessageCoverterInstanceHolder {
		private static MessageConverter instance = new MessageConverter();

		private MessageCoverterInstanceHolder() {
			// utility class
		}

		public static MessageConverter getInstance() {
			return instance;
		}
	}

	private Map<String, String> conversions = Collections.emptyMap();

	public MessageConverter() {
		this(CONVERT_DIRECTORY);
	}

	public MessageConverter(String convertDirectory) {
		init(convertDirectory);
	}

	private void init(String convertDirectory) {
		LocalConversionFileReader p = new LocalConversionFileReader(convertDirectory);
		Map<String, String> result = p.parse();
		this.conversions = result;
	}

	public static MessageConverter getInstance() {
		return MessageCoverterInstanceHolder.getInstance();
	}

	public static String getMessage(String convertName, String argName, String lang) {
		return getInstance().convert(convertName, argName, lang);
	}

	public String convert(String convertName, String argName, String lang) {
		String value = null;
		if (StringUtils.isEmpty(lang)) {
			value = conversions.get(convertName + "." + argName + ".en");
		} else {
			value = conversions.get(convertName + "." + argName + "." + lang);
		}

		return StringUtils.isEmpty(value) ? argName : value;
	}
}
