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
package com.pax.common.web.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.pax.common.web.support.editor.DoubleEditor;
import com.pax.common.web.support.editor.IntegerEditor;
import com.pax.common.web.support.editor.LongEditor;
import com.pax.common.web.support.editor.UTCDateEditor;

public abstract class BaseController {

	/**
	 * The paging size parameter
	 */
	protected static final String PAGING_PARAM_PAGESIZE = "numPerPage";

	/**
	 * The paging number parameter
	 */
	protected static final String PAGING_PARAM_PAGENUM = "pageNum";

	protected static final String OPERATION_SUC_CODE = "msg.operation.success";

	public static final Integer SUCCESS_STATUS_CODE = 200;

	@Autowired(required = false)
	@Qualifier("messageSource")
	protected ReloadableResourceBundleMessageSource messageSource;

	@InitBinder
	protected void initBinder(ServletRequestDataBinder binder) {
		binder.registerCustomEditor(int.class, new IntegerEditor());
		binder.registerCustomEditor(long.class, new LongEditor());
		binder.registerCustomEditor(double.class, new DoubleEditor());
		binder.registerCustomEditor(Date.class, new UTCDateEditor());
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
	}

	protected String getMessage(String code) {
		return this.getMessage(code, new Object[] {});
	}

	protected String getMessage(String code, Object arg0) {
		return getMessage(code, new Object[] { arg0 });
	}

	protected String getMessage(String code, Object arg0, Object arg1) {
		return getMessage(code, new Object[] { arg0, arg1 });
	}

	protected String getMessage(String code, Object arg0, Object arg1, Object arg2) {
		return getMessage(code, new Object[] { arg0, arg1, arg2 });
	}

	protected String getMessage(String code, Object arg0, Object arg1, Object arg2, Object arg3) {
		return getMessage(code, new Object[] { arg0, arg1, arg2, arg3 });
	}

	protected String getMessage(String code, Object[] args) {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
		Locale locale = localeResolver.resolveLocale(request);
		return messageSource.getMessage(code, args, locale);
	}

	protected Map<String, Object> ajaxDone(int statusCode, String message) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("statusCode", statusCode);
		map.put("message", message);
		return map;
	}
	
	protected Map<String, Object> ajaxDone(int statusCode, Object obj) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("statusCode", statusCode);
		map.put("message", obj);
		return map;
	}

	protected Map<String, Object> ajaxDoneSuccess() {
		return ajaxDone(200, this.getMessage("msg.operation.success"));
	}

	protected Map<String, Object> ajaxDoneSuccess(String message) {
		return ajaxDone(200, message);
	}
	
	protected Map<String, Object> ajaxDoneSuccess(Object obj) {
		return ajaxDone(200, obj);
	}

	protected Map<String, Object> ajaxDoneWarn(String message) {
		return ajaxDone(250, message);
	}

	protected Map<String, Object> ajaxDoneError() {
		return ajaxDone(300, this.getMessage("msg.operation.failure"));
	}

	protected Map<String, Object> ajaxDoneError(String message) {
		return ajaxDone(300, message);
	}

	protected Map<String, Object> ajaxDoneError(BindingResult bindingResult) {
		List<ObjectError> errorList = bindingResult.getAllErrors();
		String[] errorMessages = new String[errorList.size()];
		for (int i = 0; i < errorList.size(); i++) {
			ObjectError objectError = errorList.get(i);
			String message = objectError.getDefaultMessage();
			errorMessages[i] = message;
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("statusCode", 300);
		map.put("errors", errorMessages);
		return map;
	}

}
