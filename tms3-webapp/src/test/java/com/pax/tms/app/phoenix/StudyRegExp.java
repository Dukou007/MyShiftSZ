/*	
 * Copyright (C) 2018 PAX Computer Technology(Shenzhen) CO., LTD。 All rights reserved.			
 * ----------------------------------------------------------------------------------
 * PAX Computer Technology(Shenzhen) CO., LTD PROPRIETARY INFORMATION。
 * 
 * This software is supplied under the terms of a license agreement or nondisclosure 
 * agreement with PAX Computer Technology(Shenzhen) CO., LTD and may not be copied 
 * or disclosed except in accordance with the terms in that agreement.
 */
package com.pax.tms.app.phoenix;

import java.util.regex.Pattern;

/**
 * @author Elliott.Z
 *
 */
public class StudyRegExp {
	
	private static final String APPLICATION_DISPLAY_NAME = "^[\\x20-\\x7e]{1,128}$";
	private static final String BIN_PATTERN = "^[\\.\\-0-9A-Za-z_~!/\\\\]{1,255}$";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String regex = APPLICATION_DISPLAY_NAME;
		boolean result = Pattern.matches(regex, "com.pax.retailer.xxx1111111");
		System.out.println(result);
	}

}
