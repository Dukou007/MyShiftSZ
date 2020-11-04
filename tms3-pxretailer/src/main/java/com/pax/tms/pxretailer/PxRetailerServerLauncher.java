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
package com.pax.tms.pxretailer;

import java.util.Properties;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.io.ResourceLoader;

import com.pax.common.util.ApplicationContextUtils;

import io.vertx.core.Launcher;
import io.vertx.core.VertxOptions;

@Configuration
@ImportResource(locations = { "classpath:pxretailer/propertyFileConfigurer.xml",
		"classpath:pxretailer/spring-config.xml", "classpath:pxretailer/spring-fastdfs.xml",
		"classpath:pxretailer/spring-redis.xml" })
public class PxRetailerServerLauncher {

	public static void main(String[] args) {
		System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
		ConfigurableApplicationContext context = SpringApplication.run(PxRetailerServerLauncher.class);
		ApplicationContextUtils.setApplicationContext(context);

		Properties prop = context.getBean("downloadProperties", Properties.class);
		System.out.println("===========");
		new Launcher() {
			@Override
			public void beforeStartingVertx(VertxOptions options) {
				System.out.println("Event Loop Pool Size: " + options.getEventLoopPoolSize());
				System.out.println("Worker Pool Size: " + options.getWorkerPoolSize());
			}
		}.dispatch(args);

	}
}
