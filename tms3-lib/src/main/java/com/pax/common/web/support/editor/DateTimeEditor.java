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
package com.pax.common.web.support.editor;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.springframework.util.StringUtils;

public class DateTimeEditor extends PropertyEditorSupport {

	private SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd HH:mm:ss");

	@Override
	public void setAsText(String text) {

		if (!StringUtils.hasText(text)) {
			setValue(null);
		} else {
			try {
				setValue(formater.parse(text));
			} catch (ParseException ex) {
				setValue(null);
			}
		}
	}

	@Override
	public String getAsText() {
		return (getValue() == null) ? "" : formater.format(getValue());
	}
}
