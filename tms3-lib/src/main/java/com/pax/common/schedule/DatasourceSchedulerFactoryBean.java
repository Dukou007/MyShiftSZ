package com.pax.common.schedule;

import java.util.Properties;

import org.springframework.scheduling.quartz.SchedulerFactoryBean;

public class DatasourceSchedulerFactoryBean extends SchedulerFactoryBean {
	private String driver;
	private String url;
	private String user;
	private String password;
	private String validationQuery;

	@Override
	public void afterPropertiesSet() throws Exception {
		Properties quartzProperties = new Properties();
		if (driver != null) {
			quartzProperties.put("org.quartz.dataSource.myDS.driver", driver);
		}
		if (url != null) {
			quartzProperties.put("org.quartz.dataSource.myDS.URL", url);
		}
		if (user != null) {
			quartzProperties.put("org.quartz.dataSource.myDS.user", user);
		}

		if (password != null) {
			quartzProperties.put("org.quartz.dataSource.myDS.password", password);
		}

		if (validationQuery != null) {
			quartzProperties.put("org.quartz.dataSource.myDS.validationQuery", validationQuery);
		}

		super.setQuartzProperties(quartzProperties);
		super.afterPropertiesSet();
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getValidationQuery() {
		return validationQuery;
	}

	public void setValidationQuery(String validationQuery) {
		this.validationQuery = validationQuery;
	}

}
