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
package com.pax.common.web;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.springframework.util.StringUtils;

public class DateUtils extends PropertyEditorSupport {

	private static TimeZone UTC = TimeZone.getTimeZone("UTC");

	@Override
	public void setAsText(String text) {
		if (!StringUtils.hasText(text)) {
			setValue(null);
		} else {
			try {
				setValue(parseDate(text, new String[] { "yyyy-MM-dd HH:mm:ss", "dd/MM/yyyy HH:mm:ss", "yyyy-MM-dd" }));
			} catch (ParseException e) {
				setValue(null);
			}
		}
	}

	@Override
	public String getAsText() {
		return getValue().toString();
	}

	public static Date parseDate(final String str, final String... parsePatterns) throws ParseException {
		return parseDateWithLeniency(str, null, parsePatterns, true);
	}

	private static Date parseDateWithLeniency(final String str, final Locale locale, final String[] parsePatterns,
			final boolean lenient) throws ParseException {
		if (str == null || parsePatterns == null) {
			throw new IllegalArgumentException("Date and Patterns must not be null");
		}

		SimpleDateFormat parser;
		if (locale == null) {
			parser = new SimpleDateFormat();
		} else {
			parser = new SimpleDateFormat("", locale);
		}
		parser.setTimeZone(UTC);

		parser.setLenient(lenient);
		final ParsePosition pos = new ParsePosition(0);
		for (final String parsePattern : parsePatterns) {

			String pattern = parsePattern;

			// LANG-530 - need to make sure 'ZZ' output doesn't get passed to
			// SimpleDateFormat
			if (parsePattern.endsWith("ZZ")) {
				pattern = pattern.substring(0, pattern.length() - 1);
			}

			parser.applyPattern(pattern);
			pos.setIndex(0);

			String str2 = str;
			// LANG-530 - need to make sure 'ZZ' output doesn't hit
			// SimpleDateFormat as it will ParseException
			if (parsePattern.endsWith("ZZ")) {
				str2 = str.replaceAll("([-+][0-9][0-9]):([0-9][0-9])$", "$1$2");
			}

			final Date date = parser.parse(str2, pos);
			if (date != null && pos.getIndex() == str2.length()) {
				return date;
			}
		}
		throw new ParseException("Unable to parse the date: " + str, -1);
	}
}
