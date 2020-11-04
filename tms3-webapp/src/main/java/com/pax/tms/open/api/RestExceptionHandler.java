package com.pax.tms.open.api;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.pax.common.exception.BusinessException;
import com.pax.tms.open.api.rsp.Result;
import com.pax.tms.open.api.rsp.ResultUtil;



@RestControllerAdvice("com.pax.tms.open.api.controller")
public class RestExceptionHandler {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RestExceptionHandler.class);
	// 【{log msg}】【{uri}】【{Exception.getMessage}】
	private static final String LOG_FORMAT = "【{}】【{}】【{}】";

	@Autowired
	private HttpServletRequest request;
	
	@Autowired(required = false)
    @Qualifier("messageSource")
    protected ReloadableResourceBundleMessageSource messageSource;
	
	private Result<T> formatHttpStatus(HttpStatus httpStatus) {
		return ResultUtil.error(httpStatus.value(), httpStatus.getReasonPhrase());
	}
	
	/**
	 * 400 - [Bad Request] 缺少请求参数
	 */
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public Result<T> ex4001(Exception e) {
	    LOGGER.warn(LOG_FORMAT, "Lack of request parameters", request.getServletPath(), e.getMessage());
		return formatHttpStatus(HttpStatus.BAD_REQUEST);
	}

	/**
	 * 400 - [Bad Request] 参数解析失败
	 */
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public Result<T> ex4002(Exception e) {
	    LOGGER.warn(LOG_FORMAT, "Failure of parameter parsing", request.getServletPath(), e.getMessage());
		return formatHttpStatus(HttpStatus.BAD_REQUEST);
	}

	/**
	 * 400 - [Bad Request] 参数绑定失败
	 */
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler({ BindException.class, MethodArgumentTypeMismatchException.class })
	public Result<T> ex4003(Exception e) {
		String errorMesssage = "Parameter binding failure: ";
		if(e instanceof BindException) {
			BindException be = (BindException)e;
			BindingResult bindingResult = be.getBindingResult();
			//返回第一个参数错误结果即可
			errorMesssage += bindingResult.getFieldError().getDefaultMessage();
		}else if(e instanceof MethodArgumentTypeMismatchException) {
			MethodArgumentTypeMismatchException me = (MethodArgumentTypeMismatchException)e;
			errorMesssage += me.getMessage();
		}
		LOGGER.warn(LOG_FORMAT, "Parameter binding failure", request.getServletPath(), e.getMessage());
		return ResultUtil.error(500, errorMesssage);
	}

	/**
	 * 400 - [Bad Request] 参数验证失败
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler({ ConstraintViolationException.class, MethodArgumentNotValidException.class, ValidationException.class })
	public Result<T> ex4004(Exception e) {
		String errorMesssage = "Parameter validation failure: ";
		if(e instanceof ConstraintViolationException) {
			ConstraintViolationException ce = (ConstraintViolationException)e;
			errorMesssage += ce.getMessage();
		}else if(e instanceof MethodArgumentNotValidException) {
			MethodArgumentNotValidException me = (MethodArgumentNotValidException)e;
			BindingResult bindingResult = me.getBindingResult();
			//返回第一个参数错误结果即可
			errorMesssage += bindingResult.getFieldError().getDefaultMessage();
		}else if(e instanceof ValidationException) {
			ValidationException ve = (ValidationException)e;
			errorMesssage += ve.getMessage();
		}
		LOGGER.warn(LOG_FORMAT, "Parameter validation failure", request.getServletPath(), e.getMessage());
		return ResultUtil.error(500, errorMesssage);
	}


	/**
	 * 404 - [Not Found]
	 */
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(NoHandlerFoundException.class)
	public Result<T> ex404(Exception e) {
	    LOGGER.warn(LOG_FORMAT, "404 / None handler Found", request.getServletPath(), e.getMessage());
		return formatHttpStatus(HttpStatus.NOT_FOUND);
	}

	/**
	 * 405 - [Method Not Allowed] 不支持当前请求方法
	 */
	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public Result<T> ex405(Exception e) {
	    LOGGER.warn(LOG_FORMAT, "Request method not supported", request.getServletPath(), e.getMessage());
		return formatHttpStatus(HttpStatus.METHOD_NOT_ALLOWED);
	}

	/**
	 * 415 - [Unsupported Media Type] 不支持当前媒体类型
	 */
	@ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public Result<T> ex415(Exception e) {
	    LOGGER.warn(LOG_FORMAT, "Content type not supported", request.getServletPath(), e.getMessage());
		return formatHttpStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
	}

	/**
	 * 501 - [Not Implemented] Entry约束验证失败
	 */
	@ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
	@ExceptionHandler({ InvalidDataAccessApiUsageException.class })
	public Result<T> ex501(Exception e) {
	    LOGGER.error(LOG_FORMAT, "Entity constraint validation failure", request.getServletPath(),
				e.getMessage(), e);
		return formatHttpStatus(HttpStatus.NOT_IMPLEMENTED);
	}

	/**
	 * 500 - [Internal Server Error] 其它/未知异常，包括500
	 * 
	 * @param ex
	 * @return
	 */
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	public Result<T> ex(Exception e) {
	    LOGGER.error(LOG_FORMAT, "500 / Unknown exception", request.getServletPath(), e.getMessage(), e);
		return formatHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({ BusinessException.class})
    public Result<T> exBusiness(Exception e) {
        String errorMesssage ;
        if(e instanceof BusinessException) {
            BusinessException be = (BusinessException)e;
            errorMesssage = getMessage(be.getErrorCode(),be.getArgs());
        }else {
            errorMesssage = "Internal server error ";
        }
        LOGGER.warn(LOG_FORMAT, "Parameter binding failure", request.getServletPath(), e.getMessage());
        return ResultUtil.error(300, errorMesssage);
    }
	
    protected String getMessage(String code) {
        return this.getMessage(code, new Object[] {});
    }
    
    protected String getMessage(String code, Object[] args) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
        Locale locale = localeResolver.resolveLocale(request);
        return messageSource.getMessage(code, args, locale);
    }
}
