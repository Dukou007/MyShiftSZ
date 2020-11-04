package com.pax.tms.cas.login;

import java.lang.reflect.Field;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.jasig.cas.logout.LogoutManager;
import org.jasig.cas.logout.LogoutManagerImpl;
import org.jasig.cas.util.http.SimpleHttpClientFactoryBean.NoRedirectHttpClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class AllowAllHostnameHttpClient extends NoRedirectHttpClient implements InitializingBean {

	@Autowired
	@Qualifier("logoutManager")
	private LogoutManager logoutManager;

	@Override
	@Autowired
	public void setSslSocketFactory(
			@Qualifier("allowAllHostnameTrustStoreSslSocketFactory") final SSLConnectionSocketFactory sslSocketFactory) {
		super.setSslSocketFactory(sslSocketFactory);
	}

	public void setLogoutManager(LogoutManager logoutManager) {
		this.logoutManager = logoutManager;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (logoutManager instanceof LogoutManagerImpl) {
			Field field = logoutManager.getClass().getDeclaredField("httpClient");
			field.setAccessible(true);
			field.set(logoutManager, this.getObject());
		}
	}
}
