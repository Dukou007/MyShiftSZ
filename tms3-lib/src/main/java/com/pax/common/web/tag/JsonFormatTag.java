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
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class JsonFormatTag extends BodyTagSupport {
	private static final long serialVersionUID = 1L;
	private String value;

	@Override
	public int doEndTag() throws JspException {
		try {
			if (bodyContent != null) {
				String format = stringToJson(bodyContent.getString());
				bodyContent.getEnclosingWriter().write(format);
			}
		} catch (java.io.IOException e) {
			throw new JspTagException("IO Error: " + e.getMessage(), e);
		}
		return EVAL_PAGE;

	}

	@Override
	public int doStartTag() throws JspException {
		if (value != null) {
			String format = stringToJson(value);
			try {
				pageContext.getOut().write(format);
			} catch (IOException e) {
				throw new JspTagException("IO Error: " + e.getMessage(), e);
			}
			return SKIP_BODY;
		}
		return EVAL_BODY_BUFFERED;
	}

	@Override
	public int doAfterBody() throws JspException {
		return SKIP_BODY;
	}

	@Override
	public void setBodyContent(BodyContent b) {
		this.bodyContent = b;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public static String stringToJson(String s) {
		if (s == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			switch (ch) {
			case '"':
				sb.append("\\\"");
				break;
			case '\\':
				sb.append("\\\\");
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '/':
				sb.append("\\/");
				break;
			default:
				normal(sb, ch);
			}
		}
		return sb.toString();
	}

	private static void normal(StringBuilder sb, char ch) {
		if (ch >= '\u0000' && ch <= '\u001F') {
			String ss = Integer.toHexString(ch);
			sb.append("\\u");
			for (int k = 0; k < 4 - ss.length(); k++) {
				sb.append('0');
			}
			sb.append(ss.toUpperCase());
		} else {
			sb.append(ch);
		}
	}
}
