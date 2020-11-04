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

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter {

	private static final DateFormatter DATETIMEFORMATTER = new DateFormatter();

	private SimpleDateFormat timeFormat;

	private SimpleDateFormat dateFormat;

	private DateFormatter() {
		timeFormat = new SimpleDateFormat("HH:mm:ss");
		dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}

	public String innerFormatDate(Date date) {
		if (date == null) {
			return "";
		}

		return dateFormat.format(date);
	}

	public String innerFormatTime(Date date) {
		if (date == null) {
			return "";
		}
		return timeFormat.format(date);
	}

	public static String formatDate(Date date) {
		return DATETIMEFORMATTER.innerFormatDate(date);
	}

	public static String formatTime(Date date) {
		return DATETIMEFORMATTER.innerFormatTime(date);
	}

	public static String formatTime(long remTime) {
		long ts = remTime / 1000;
		String s = String.valueOf(ts % 60);
		String m = String.valueOf((ts % 3600) / 60);
		String h = String.valueOf(ts / 3600);
		return (h.length() == 1 ? "0" + h : h) + ":" + (m.length() == 1 ? "0" + m : m) + ":"
				+ (s.length() == 1 ? "0" + s : s);
	}
}
