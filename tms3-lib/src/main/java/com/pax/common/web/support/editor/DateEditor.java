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

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.util.StringUtils;

public class DateEditor extends PropertyEditorSupport {
	@Override
	public void setAsText(String text) {
		if (!StringUtils.hasText(text)) {
			setValue(null);
		} else {
			try {
				setValue(DateUtils.parseDate(text,
						new String[] { "yyyy-MM-dd HH:mm:ss", "dd/MM/yyyy HH:mm:ss", "yyyy-MM-dd"}));
			} catch (ParseException e) {
				setValue(null);
			}
		}
	}

	@Override
	public String getAsText() {
		return getValue().toString();
	}
}
