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

@SuppressWarnings({ "rawtypes", "unchecked" })
public class EnumEditor extends PropertyEditorSupport {

	private Class enumClass;

	public EnumEditor(Class cls) {
		this.enumClass = cls;
	}

	@Override
	public void setAsText(String text) {
		if (text != null && text.trim().length() > 0) {
			setValue(Enum.valueOf(enumClass, text));
		}
	}

	@Override
	public String getAsText() {
		return (getValue() == null) ? "" : getValue().toString();
	}
}
