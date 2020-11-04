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
package com.pax.common.web.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang3.StringUtils;

import com.pax.common.web.message.MessageConverter;

public class ConvertTag extends TagSupport {

	private static final long serialVersionUID = -3108441927844858331L;
	private String dicName;
	private String dicValue;

	public String getDicName() {
		return dicName;
	}

	public void setDicName(String dicName) {
		this.dicName = dicName;
	}

	public String getDicValue() {
		return dicValue;
	}

	public void setDicValue(String dicValue) {
		this.dicValue = dicValue;
	}

	@Override
	public int doStartTag() throws JspException {
		JspWriter out = this.pageContext.getOut();
		String lang = (String) this.pageContext.getSession().getAttribute("_LANG_TAG");
		try {
			if (StringUtils.isEmpty(lang)) {
				lang = "en";
			}
			String value = MessageConverter.getMessage(this.dicName, this.dicValue, lang);
			out.print(StringUtils.isEmpty(value) ? this.dicValue : value);
		} catch (IOException e) {
			throw new JspTagException("IO Error", e);
		}
		return SKIP_BODY;
	}
}
