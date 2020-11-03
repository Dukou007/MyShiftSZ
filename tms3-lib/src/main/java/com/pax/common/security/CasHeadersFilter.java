package com.pax.common.security;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

public class CasHeadersFilter implements Filter{

    private String casServerPrefix;
    private String tmsServerPrefix;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            Properties configProperties = new Properties();
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("tms.properties");
            configProperties.load(inputStream);
            casServerPrefix = configProperties.getProperty("cas.server.prefix");
            tmsServerPrefix = configProperties.getProperty("tms.server.prefix");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        HttpServletResponse servletResponse = (HttpServletResponse) response;
        String host = servletRequest.getHeader("Host");
        String referer = servletRequest.getHeader("Referer");
        if(!StringUtils.contains(casServerPrefix, host)){
            servletResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
        if(StringUtils.isNotBlank(referer) && !StringUtils.contains(referer, casServerPrefix) && !StringUtils.contains(casServerPrefix, referer) && !StringUtils.contains(tmsServerPrefix, referer)&& !StringUtils.contains(referer, tmsServerPrefix)){
            servletResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
        // 设置cookie secure 和 httponly
        Cookie[] cookies = servletRequest.getCookies();
        if (null != cookies) {
            for (Cookie cookie : cookies) {
                cookie.setHttpOnly(true);
                cookie.setSecure(true);
            }
        }
        chain.doFilter(request, response);
        
    }

    @Override
    public void destroy() {
        
        
    }

}
