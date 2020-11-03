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

import java.util.regex.Pattern;

public class RegexMatchUtils {

	private RegexMatchUtils() {
	}

	public static boolean contains(CharSequence property, String regex) {
		Pattern pattern = Pattern.compile(regex);
		return pattern.matcher(property).find() ? true : false;
	}

	public static boolean isMatcher(CharSequence property, String regex) {
		Pattern pattern = Pattern.compile(regex);
		return pattern.matcher(property).matches() ? true : false;
	}

}
