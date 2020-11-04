package com.pax.tms.demo;

import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@ContextConfiguration(locations = { "classpath:webapp/spring-configuration/propertyFileConfigurer.xml",
		"classpath:webapp/spring-configuration/emailContext.xml",
		"classpath:webapp/spring-configuration/spring-config.xml",
		"classpath:webapp/spring-configuration/spring-shiro.xml", "classpath:webapp/spring-configuration/scheduler.xml",
		"classpath:webapp/spring-configuration/spring-servlet.xml" })

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional(transactionManager = "txManager")
@Rollback
// @Commit
public class ServletJunitCase {

}
