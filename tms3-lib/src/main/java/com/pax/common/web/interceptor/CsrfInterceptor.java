package com.pax.common.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.security.web.csrf.MissingCsrfTokenException;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.pax.common.security.CsrfProtect;

public class CsrfInterceptor implements HandlerInterceptor {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private CsrfTokenRepository tokenRepository = new HttpSessionCsrfTokenRepository();
	private AccessDeniedHandler accessDeniedHandler = new AccessDeniedHandlerImpl();

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		CsrfToken csrfToken = this.tokenRepository.loadToken(request);
		final boolean missingToken = csrfToken == null;
		if (missingToken) {
			csrfToken = this.tokenRepository.generateToken(request);
			this.tokenRepository.saveToken(csrfToken, request, response);
		}
		request.setAttribute(CsrfToken.class.getName(), csrfToken);
		request.setAttribute(csrfToken.getParameterName(), csrfToken);

		if (handler instanceof HandlerMethod) {
			HandlerMethod hm = (HandlerMethod) handler;
			CsrfProtect csrfProtect = hm.getMethodAnnotation(CsrfProtect.class);
			if (csrfProtect == null) {
				return true;
			}
			String actualToken = request.getHeader(csrfToken.getHeaderName());
			if (actualToken == null) {
				actualToken = request.getParameter(csrfToken.getParameterName());
			}

			if (!csrfToken.getToken().equals(actualToken)) {
				if (this.logger.isDebugEnabled()) {
					this.logger.debug("Invalid CSRF token found for " + UrlUtils.buildFullRequestUrl(request));
				}
				if (missingToken) {
					this.accessDeniedHandler.handle(request, response, new MissingCsrfTokenException(actualToken));
				} else {
					this.accessDeniedHandler.handle(request, response,
							new InvalidCsrfTokenException(csrfToken, actualToken));
				}
				return false;
			}
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// nothing to do
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// nothing to do
	}

	public CsrfTokenRepository getTokenRepository() {
		return tokenRepository;
	}

	public void setTokenRepository(CsrfTokenRepository tokenRepository) {
		this.tokenRepository = tokenRepository;
	}

	public AccessDeniedHandler getAccessDeniedHandler() {
		return accessDeniedHandler;
	}

	public void setAccessDeniedHandler(AccessDeniedHandler accessDeniedHandler) {
		this.accessDeniedHandler = accessDeniedHandler;
	}

}
