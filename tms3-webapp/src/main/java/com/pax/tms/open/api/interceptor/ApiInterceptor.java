package com.pax.tms.open.api.interceptor;

import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.pac4j.core.profile.CommonProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.pax.tms.open.api.annotation.ApiPermission;
import com.pax.tms.open.api.model.AppClient;
import com.pax.tms.user.service.AppKeyService;
import com.pax.tms.user.service.UserService;

import io.buji.pac4j.token.Pac4jToken;

public class ApiInterceptor implements HandlerInterceptor {

	@Autowired
	private AppKeyService appKeyService;
	
	@Autowired
	private UserService userService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		AppClient appClient = checkAppKey(request, response);
		if (appClient == null) {
			return false;
		}
		login(appClient.getUserName());
		boolean isPermit = checkUserPermission(handler);
		if (!isPermit) {
			response.setStatus(403);
		}
		return isPermit;
	}

	private boolean checkUserPermission(Object handler) {
		if (handler instanceof HandlerMethod) {
			HandlerMethod hm = (HandlerMethod) handler;
			ApiPermission apiPermission = hm.getMethodAnnotation(ApiPermission.class);
			if (apiPermission == null) {
				return true;
			}
			return (SecurityUtils.getSubject().isPermitted(apiPermission.value()));
		}
		return true;
	}

	private AppClient checkAppKey(HttpServletRequest request, HttpServletResponse response) {
		String appKey = request.getHeader("app_key");
		if (StringUtils.isEmpty(appKey)) {
			appKey = request.getParameter("app_key");
		}
		if (StringUtils.isEmpty(appKey)) {
			response.setStatus(401);
			return null;
		}
		AppClient appClient = appKeyService.getAppClient(appKey);
		if (appClient == null) {
			response.setStatus(401);
			return null;
		}
		return appClient;
	}

	private void login(String userName) {
		CommonProfile profile = new CommonProfile();
		profile.setId(userName);
		LinkedHashMap<String, CommonProfile> profiles = new LinkedHashMap<>();
		profiles.put("userName", profile);
		Pac4jToken pac4jToken = new Pac4jToken(profiles, false);
		SecurityUtils.getSubject().login(pac4jToken);
		userService.updateLastLoginDate(userName);

	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e)
			throws Exception {

	}
}