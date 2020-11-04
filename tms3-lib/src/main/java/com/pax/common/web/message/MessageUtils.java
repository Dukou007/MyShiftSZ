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

import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

public class MessageUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(MessageUtils.class);

	private static final String LANG_ATTR = "_LANG_TAG";

	private MessageUtils() {
	}

	public static String getLang(HttpServletRequest request) {
		String lang = (String) request.getSession().getAttribute(LANG_ATTR);
		if (StringUtils.isEmpty(lang)) {
			Cookie[] cookies = request.getCookies();
			if (cookies != null) {
				for (Cookie c : cookies) {
					if ("clientLanguage".equals(c.getName())) {
						lang = c.getValue();
						request.getSession().setAttribute(LANG_ATTR, lang);
					}
				}
			}
		}

		if (StringUtils.isEmpty(lang)) {
			lang = "en";
			request.getSession().setAttribute(LANG_ATTR, lang);
		}

		return lang;
	}

	public static Locale getLocale() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
		return localeResolver.resolveLocale(request);
	}

	public static String getMessage(MessageSource messageSource, String code) {
		return getMessage(messageSource, code, null);
	}

	public static String getMessage(MessageSource messageSource, String code, Object[] args) {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
		Locale locale = localeResolver.resolveLocale(request);

		try {
			return messageSource.getMessage(code, args, locale);
		} catch (NoSuchMessageException e) {
			LOGGER.debug("No such message exception: {}", e, code);
			return code;
		}
	}

	public static String getDictValue(HttpServletRequest request, String convertName, String argName) {
		return getDictValue(convertName, argName, getLang(request));
	}

	public static String getDictValue(String convertName, String argName, String lang) {
		MessageConverter converter = MessageConverter.getInstance();
		return converter.convert(convertName, argName, lang);
	}

}
