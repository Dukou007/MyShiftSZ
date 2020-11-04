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
package com.pax.common.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.orm.hibernate5.HibernateJdbcException;
import org.springframework.orm.hibernate5.HibernateObjectRetrievalFailureException;
import org.springframework.orm.hibernate5.HibernateOptimisticLockingFailureException;
import org.springframework.orm.hibernate5.HibernateQueryException;
import org.springframework.orm.hibernate5.HibernateSystemException;
import org.springframework.util.Assert;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.HtmlUtils;

import com.pax.common.exception.AppException;
import com.pax.common.exception.BusinessException;
import com.pax.common.monitor.IAlertManager;
import com.pax.common.web.message.MessageUtils;

public class FrameworkHandlerExceptionResolver implements HandlerExceptionResolver {

	private static final Logger LOGGER = LoggerFactory.getLogger(FrameworkHandlerExceptionResolver.class);
	private JsonRequestResolver jsonRequestResolver = new JsonRequestResolverImpl();
	private static final String UNDEFINED_EXCEPTION = "undefined.exception";
	private static final String UNCATEGORIZED_DATABASE_ERROR = "database error, uncategorized data access exception";

	private boolean htmlEscape = true;

	private String defaultErrorPage = "error";
	private String jsonErrorPage = "ajaxDone";

	private IAlertManager alertManager;

	@Autowired
	@Qualifier("messageSource")
	protected ReloadableResourceBundleMessageSource messageSource;

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception exception) {

		ModelAndView modelAndView = null;
		String code = null;
		String message = "";

		if (exception instanceof BusinessException) {
			BusinessException busEx = (BusinessException) exception;
			code = busEx.getErrorCode();
			message = MessageUtils.getMessage(messageSource, code, busEx.getArgs());
			LOGGER.info("FrameworkHandlerExceptionResolver caught a BusinessException:{}", message);

		} else if (exception instanceof AppException) {
			AppException appEx = (AppException) exception;
			code = UNDEFINED_EXCEPTION;
			message = appEx.toString();
			LOGGER.error("FrameworkHandlerExceptionResolver caught an AppException", exception);
			if (alertManager != null) {
				alertManager.alert(this, (AppException) exception);
			}

		} else if (exception instanceof DataAccessException) {
			code = UNDEFINED_EXCEPTION;
			message = resolveDataAccessException((DataAccessException) exception);
			LOGGER.error("FrameworkHandlerExceptionResolver caught a DataAccessException", exception);
			if (alertManager != null) {
				alertManager.alert(this, (DataAccessException) exception);
			}

		} else {
			code = UNDEFINED_EXCEPTION;
			message = exception.toString();
			LOGGER.error("FrameworkHandlerExceptionResolver caught an Exception", exception);
			if (alertManager != null) {
				alertManager.alert(this, exception);
			}

		}

		if (htmlEscape) {
			message = HtmlUtils.htmlEscape(message);
		}

		// if (HttpServletUtils.isJsonRequest(request)) {
		// modelAndView = jsonErrorView();
		// } else {
		// modelAndView = defaultErrorView();
		// }

		if (jsonRequestResolver.isJsonRequest(request) || jsonRequestResolver.isSwaggerRequest(request, handler,response)) {
			modelAndView = jsonErrorView();
		} else {
			modelAndView = defaultErrorView();
		}
		modelAndView.addObject("statusCode", code);
		modelAndView.addObject("message", message);
		return modelAndView;
	}

	private ModelAndView defaultErrorView() {

		return new ModelAndView(defaultErrorPage);
	}

	private ModelAndView jsonErrorView() {
		ModelAndView mv = new ModelAndView(jsonErrorPage);
		mv.addObject("statusCode", 300);
		return mv;
	}

	private String resolveDataAccessException(DataAccessException ex) {
		// JDBCConnectionException
		if (ex instanceof DataAccessResourceFailureException) {
			// Data access exception thrown when a resource fails
			// completely:
			return "database error, can't connect to database";
		}
		// QueryException
		else if (ex instanceof HibernateQueryException) {
			// Hibernate-specific subclass of
			// InvalidDataAccessResourceUsageException, thrown on invalid
			// HQL query syntax.
			return "database error, invalid HQL query syntax";

		}
		// SQLGrammarException
		else if (ex instanceof InvalidDataAccessResourceUsageException) {
			// use a data access resource incorrectly
			return "database error, bad SQL";

		}
		// LockAcquisitionException
		else if (ex instanceof CannotAcquireLockException) {
			// Exception thrown on failure to aquire a lock during an update
			return "database error, failure to aquire a lock";

		}
		// ConstraintViolationException,DataException,PropertyValueException
		else if (ex instanceof DataIntegrityViolationException) {
			// Exception thrown when an attempt to insert or update data
			// results in violation of an integrity constraint.
			return "database error, violation of an integrity constraint";

		}
		// JDBCException
		else if (ex instanceof HibernateJdbcException) {
			// Hibernate-specific subclass of
			// UncategorizedDataAccessException
			return UNCATEGORIZED_DATABASE_ERROR;
		}
		// NonUniqueResultException
		else if (ex instanceof IncorrectResultSizeDataAccessException) {
			// Data access exception thrown when a result was not of the
			// expected size, for example when expecting a single row but
			// getting 0 or more than 1 rows.
			return "database error, non unique result";
		}
		// NonUniqueObjectException
		else if (ex instanceof DuplicateKeyException) {
			// Exception thrown when an attempt to insert or update data,
			// results in violation of an primary key or unique constraint.
			return "database error, duplicate key exception";
		}
		// PersistentObjectException,TransientObjectException,ObjectDeletedException
		else if (ex instanceof InvalidDataAccessApiUsageException) {
			// Exception thrown on incorrect usage of the API, such as
			// failing to, "compile" a query object that needed compilation
			// before execution.
			return "database error, incorrect usage of the API";
		}
		// UnresolvableObjectException,WrongClassException
		else if (ex instanceof HibernateObjectRetrievalFailureException) {
			// Hibernate-specific subclass of
			// ObjectRetrievalFailureException. Converts Hibernate's
			// UnresolvableObjectException and WrongClassException.
			return "database error, object retrieval failure";
		}
		// StaleObjectStateException,StaleStateException
		else if (ex instanceof HibernateOptimisticLockingFailureException) {
			// Hibernate-specific subclass of
			// ObjectOptimisticLockingFailureException. Converts Hibernate's
			// StaleObjectStateException and StaleStateException.
			return "database error, optimistic locking failure";
		}
		// fallback
		else if (ex instanceof HibernateSystemException) {
			/*
			 * Hibernate-specific subclass of UncategorizedDataAccessException,
			 * for Hibernate system errors that do not match any concrete {@code
			 * org.springframework.dao} exceptions.
			 */
			return UNCATEGORIZED_DATABASE_ERROR;
		} else {
			// fallback
			return UNCATEGORIZED_DATABASE_ERROR;
		}

	}

	public String getDefaultErrorPage() {
		return defaultErrorPage;
	}

	public void setDefaultErrorPage(String defaultErrorPage) {
		Assert.notNull(defaultErrorPage);
		this.defaultErrorPage = defaultErrorPage;
	}

	public String getJsonErrorPage() {
		return jsonErrorPage;
	}

	public void setJsonErrorPage(String jsonErrorPage) {
		Assert.notNull(jsonErrorPage);
		this.jsonErrorPage = jsonErrorPage;
	}

	public void setMessageSource(ReloadableResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

}
