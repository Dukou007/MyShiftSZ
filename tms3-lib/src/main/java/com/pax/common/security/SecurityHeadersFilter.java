package com.pax.common.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class SecurityHeadersFilter extends OncePerRequestFilter {

	private String cacheControl;
	private String pragma;
	private String expires;
	private String xContextTypeOptions;
	private String xFrameOptions;
	private String xxssProtection;
	private String contentSecurityPolicy;
	private String referrerPolicy;
	private String strictTransportSecurity;
	private String publicKeyPinsReportOnly;
	private String tmsServerPrefix;
	private String casLoginUrl;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		// cache control
		if (cacheControl != null) {
			response.setHeader("Cache-Control", cacheControl);
		}
		if (pragma != null) {
			response.setHeader("Pragma", pragma);
		}

		if (expires != null) {
			response.setHeader("Expires", expires);
		}

		// XSS
		if (xContextTypeOptions != null) {
			response.setHeader("X-Content-Type-Options", xContextTypeOptions);
		}

		if (xFrameOptions != null) {
			response.setHeader("X-Frame-Options", xFrameOptions);
		}

		if (xxssProtection != null) {
			response.setHeader("X-XSS-Protection", xxssProtection);
		}

		if (contentSecurityPolicy != null) {
			response.setHeader("Content-Security-Policy", contentSecurityPolicy);
		}

		if (referrerPolicy != null) {
			response.setHeader("Referrer-Policy", referrerPolicy);
		}

		// ssl
		if (strictTransportSecurity != null) {
			response.setHeader("Strict-Transport-Security", strictTransportSecurity);
		}

		if (publicKeyPinsReportOnly != null) {
			response.setHeader("Public-Key-Pins-Report-Only", strictTransportSecurity);
		}
		if(tmsServerPrefix != null){
		    String host = request.getHeader("Host");
	        String referer = request.getHeader("Referer");
		    if(!StringUtils.contains(tmsServerPrefix, host)){
		        response.sendError(HttpServletResponse.SC_FORBIDDEN);
		    }
            if(StringUtils.isNotBlank(referer) && !StringUtils.contains(referer, casLoginUrl) && !StringUtils.contains(casLoginUrl, referer) && !StringUtils.contains(tmsServerPrefix, referer) && !StringUtils.contains(referer, tmsServerPrefix)){
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
            }
		}
		
		HttpServletRequest servletRequest = (HttpServletRequest)request;
        //设置cookie secure 和 httponly
        Cookie[] cookies = servletRequest.getCookies();
        if (null != cookies) {
            for (Cookie cookie : cookies) {
                cookie.setHttpOnly(true);
                cookie.setSecure(true);
            }
        }
		filterChain.doFilter(request, response);
	}

	public void setCacheControl(String cacheControl) {
		this.cacheControl = StringUtils.trimToNull(cacheControl);
	}

	public void setPragma(String pragma) {
		this.pragma = StringUtils.trimToNull(pragma);
	}

	public void setExpires(String expires) {
		this.expires = StringUtils.trimToNull(expires);
	}

	public void setXContextTypeOptions(String xContextTypeOptions) {
		this.xContextTypeOptions = StringUtils.trimToNull(xContextTypeOptions);
	}

	public void setXFrameOptions(String xFrameOptions) {
		this.xFrameOptions = StringUtils.trimToNull(xFrameOptions);
	}

	public void setXxssProtection(String xxssProtection) {
		this.xxssProtection = StringUtils.trimToNull(xxssProtection);
	}

	public void setContentSecurityPolicy(String contentSecurityPolicy) {
		this.contentSecurityPolicy = StringUtils.trimToNull(contentSecurityPolicy);
	}

	public void setReferrerPolicy(String referrerPolicy) {
		this.referrerPolicy = StringUtils.trimToNull(referrerPolicy);
	}

	public void setStrictTransportSecurity(String strictTransportSecurity) {
		this.strictTransportSecurity = StringUtils.trimToNull(strictTransportSecurity);
	}

	public void setPublicKeyPinsReportOnly(String publicKeyPinsReportOnly) {
		this.publicKeyPinsReportOnly = StringUtils.trimToNull(publicKeyPinsReportOnly);
	}

    public void setTmsServerPrefix(String tmsServerPrefix) {
        this.tmsServerPrefix = StringUtils.trimToNull(tmsServerPrefix);
    }

    public void setCasLoginUrl(String casLoginUrl) {
        this.casLoginUrl = StringUtils.trimToNull(casLoginUrl);;
    }
	
	

}
