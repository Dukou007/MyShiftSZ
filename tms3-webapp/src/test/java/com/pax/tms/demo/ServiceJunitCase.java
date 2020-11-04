package com.pax.tms.demo;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import junit.framework.TestCase;

@ContextConfiguration(locations = { "classpath:ppm/spring-configuration/propertyFileConfigurer.xml",
		"classpath:ppm/spring-configuration/emailContext.xml",
		"classpath:ppm/spring-configuration/spring-config.xml",
		"classpath:ppm/spring-configuration/spring-fastdfs.xml",
		"classpath:ppm/spring-configuration/spring-shiro.xml",
		"classpath:ppm/spring-configuration/scheduler.xml",
		"classpath:ppm/spring-configuration/spring-redis.xml" })

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@Transactional(transactionManager = "txManager")
@Rollback(true)
// @Commit
public class ServiceJunitCase extends TestCase {
	public AtomicInteger counter = new AtomicInteger();
	public Date date = new Date();

	@Test
	public void testMethod() {

	}
}
