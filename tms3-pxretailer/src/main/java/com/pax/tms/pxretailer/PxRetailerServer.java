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

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import com.pax.common.util.ApplicationContextUtils;
import com.pax.common.vertx.NetServerOptionsSetting;
import com.pax.tms.download.FileDataStore;
import com.pax.tms.download.PackageManager;
import com.pax.tms.download.VertxUtils;
import com.pax.tms.pxretailer.action.CallHomeAction;
import com.pax.tms.pxretailer.action.DownloadFileAction;
import com.pax.tms.pxretailer.action.GetConfigsAction;
import com.pax.tms.pxretailer.action.GetFileInfoAction;
import com.pax.tms.pxretailer.action.GetScheduledPackageAction;
import com.pax.tms.pxretailer.action.HmsAction;
import com.pax.tms.pxretailer.action.TerminalLogsAction;
import com.pax.tms.pxretailer.action.UpdateDeploymentStatusAction;

public class PxRetailerServer extends AbstractVerticle {

	private final Logger logger = LoggerFactory.getLogger(PxRetailerServer.class);

	private HttpServer server;

	private PxRetailerTerminalManager terminalManager;

	private PackageManager packageManager;

	private FileDataStore fileDataStore;

	private String redispassword;
	private String redisMaster;
	private String redisSentinelNodes;

	@Autowired
	private PxRetailerConfig config;

	@Autowired
	public void setRedispassword(@Value("${redis.pass:''}") String redispassword) {
		this.redispassword = redispassword;
	}

	@Autowired
	public void setRedisMaster(@Value("${spring.redis.sentinel.master:''}") String redisMaster) {
		this.redisMaster = redisMaster;
	}

	@Autowired
	public void setRedisSentinelNodes(@Value("${spring.redis.sentinel.nodes:''}") String redisSentinelNodes) {
		this.redisSentinelNodes = redisSentinelNodes;
	}

	@Override
	public void start(Future<Void> startFuture) {
		createBeans();
		startHttpServer(startFuture);
	}

	private void createBeans() {
		ConfigurableListableBeanFactory beanFactory = (ConfigurableListableBeanFactory) ApplicationContextUtils
				.getApplicationContext().getAutowireCapableBeanFactory();
		beanFactory.autowireBean(this);

		RedisOptions redisOptions = VertxUtils.createRedisOptions(redispassword, redisMaster, redisSentinelNodes);
		RedisClient redisClient = RedisClient.create(vertx, redisOptions);

		terminalManager = new PxRetailerTerminalManager(vertx, redisClient);
		beanFactory.autowireBean(terminalManager);

		packageManager = new PackageManager(vertx);
		beanFactory.autowireBean(packageManager);

		fileDataStore = new FileDataStore(vertx);
		beanFactory.autowireBean(fileDataStore);
	}

	private void startHttpServer(Future<Void> startFuture) {

		Router router = Router.router(vertx);
		if (config().getBoolean("http.log", false)) {
			/*
			 * A handler which logs request information to the Vert.x logger.
			 */
			router.route().handler(LoggerHandler.create());
		}

		registerTerminalActions(router);

		HttpServerOptions options = new HttpServerOptions();
		NetServerOptionsSetting.setHttpServerOptions(options, config());
		options.setPort(config.getTcpPort());

		server = vertx.createHttpServer(options);

		server.requestHandler(router::accept);
		server.listen(res -> {
			if (res.succeeded()) {
				String msg = "PxRetailer server is now listening on actual port: " + server.actualPort();
				logger.info(msg);
				startFuture.complete();
			} else {
				String msg = "PxRetailer server failed to bind!";
				logger.error(msg, res.cause());
				startFuture.fail(res.cause());
			}
		});
	}

	private void registerTerminalActions(Router router) {
		ConfigurableListableBeanFactory beanFactory = (ConfigurableListableBeanFactory) ApplicationContextUtils
				.getApplicationContext().getAutowireCapableBeanFactory();
		beanFactory.autowireBean(this);

		CallHomeAction chsAction = new CallHomeAction();
		chsAction.setTerminalManager(terminalManager);
		beanFactory.autowireBean(chsAction);
		chsAction.registerAction(router, config());

		HmsAction hmsAction = new HmsAction();
		hmsAction.setTerminalManager(terminalManager);
		beanFactory.autowireBean(hmsAction);
		hmsAction.registerAction(router, config());

		GetScheduledPackageAction getSchedulePackageAction = new GetScheduledPackageAction();
		getSchedulePackageAction.setTerminalManager(terminalManager);
		beanFactory.autowireBean(getSchedulePackageAction);
		getSchedulePackageAction.registerAction(router, config());

		UpdateDeploymentStatusAction updateDeploymentStatusAction = new UpdateDeploymentStatusAction();
		updateDeploymentStatusAction.setTerminalManager(terminalManager);
		updateDeploymentStatusAction.setPackageManager(packageManager);
		beanFactory.autowireBean(updateDeploymentStatusAction);
		updateDeploymentStatusAction.registerAction(router, config());

		GetFileInfoAction getFileInfoAction = new GetFileInfoAction();
		getFileInfoAction.setTerminalManager(terminalManager);
		getFileInfoAction.setPackageManager(packageManager);
		beanFactory.autowireBean(getFileInfoAction);
		getFileInfoAction.registerAction(router, config());

		DownloadFileAction downloadFileAction = new DownloadFileAction();
		downloadFileAction.setTerminalManager(terminalManager);
		downloadFileAction.setPackageManager(packageManager);
		downloadFileAction.setFileDataStore(fileDataStore);
		beanFactory.autowireBean(downloadFileAction);
		downloadFileAction.registerAction(router, config());

		GetConfigsAction getConfigsAction = new GetConfigsAction();
		beanFactory.autowireBean(getConfigsAction);
		getConfigsAction.registerAction(router, config());
		
		TerminalLogsAction termialLogsAction = new TerminalLogsAction();
		beanFactory.autowireBean(termialLogsAction);
		termialLogsAction.registerAction(router, config());
	}

	@Override
	public void stop(Future<Void> stopFuture) throws Exception {
		if (server == null) {
			stopFuture.complete();
			return;
		}

		server.close(res -> {
			if (res.succeeded()) {
				logger.info("PxRetailer server is now closed");
			} else {
				logger.error("PxRetailer server close failed", res.cause());
			}
			stopFuture.complete();
		});
	}

}
