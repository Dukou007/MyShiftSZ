package com.pax.tms.pxretailer;

import com.pax.tms.protocol.service.Deployment;
import com.pax.tms.protocol.service.TerminalService;
import com.pax.tms.res.model.PkgType;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.shareddata.AsyncMap;

public class DeploymentStore {

	public static final String DEPLOYMENT_MAP_STORE_NAME = "pos.deployment";
	public static final String SCHEDULEDPACKAGE_MAP_STORE_NAME = "pos.scheduled";

	private static final Logger LOGGER = LoggerFactory.getLogger(DeploymentStore.class);

	private static class NullDeployment extends Deployment {
		private static final long serialVersionUID = -439603004035725853L;
	}

	private static final Deployment nullDeploy = new NullDeployment();

	private TerminalService terminalService;

	private Vertx vertx;
	private AsyncMap<String, Deployment> schedulePackageMap;
	private AsyncMap<Long, Deployment> deploymentMap;

	public DeploymentStore(Vertx vertx, AsyncMap<String, Deployment> schedulePackageMap,
			AsyncMap<Long, Deployment> deploymentMap) {
		super();
		this.vertx = vertx;
		this.schedulePackageMap = schedulePackageMap;
		this.deploymentMap = deploymentMap;
	}

	public void getScheduledPackage(String terminalId, PkgType packageType, Future<Deployment> future) {
		schedulePackageMap.get(terminalId, res -> {
			if (res.succeeded()) {
				Deployment deploy = res.result();
				if (deploy == null) {
					getScheduledPackageFromDb(terminalId, packageType, future);
				} else if (deploy instanceof NullDeployment) {
					future.complete();
				} else {
					future.complete(deploy);
				}
			} else {
				LOGGER.error("Failed to fetch scheduled package from cache", res.cause());
				getScheduledPackageFromDb(terminalId, packageType, future);
			}
		});
	}

	private void getScheduledPackageFromDb(String terminalId, PkgType packageType, Future<Deployment> future) {
		vertx.<Deployment>executeBlocking(fut -> {
			fut.complete(terminalService.getScheduledPackage(terminalId, packageType));
		}, res -> {
			if (res.succeeded()) {
				Deployment deploy = res.result();
				future.complete(deploy);
				putScheduledPackageToCache(terminalId, res.result());
			} else {
				LOGGER.error("Failed to fetch scheduled package from DB", res.cause());
			}
		});
	}

	public void removeScheduledPackage(String terminalId) {
		schedulePackageMap.remove(terminalId, res -> {
			if (res.failed()) {
				LOGGER.error("Failed to remove scheduled package from cache", res.cause());
			}
		});
	}

	private void putScheduledPackageToCache(String terminalId, Deployment deploy) {
		schedulePackageMap.put(terminalId, deploy == null ? nullDeploy : deploy, res -> {
			if (res.failed()) {
				LOGGER.error("Failed to put scheduled package to cache", res.cause());
			}
		});

		if (deploy != null) {
			putDeploymentToCache(deploy.getDeployId(), deploy);
		}
	}

	private void putDeploymentToCache(long deployId, Deployment deploy) {
		deploymentMap.put(deployId, deploy == null ? nullDeploy : deploy, res -> {
			if (res.failed()) {
				LOGGER.error("Failed to put deployment to cache", res.cause());
			}
		});
	}

	public void getDeployment(long deployId, Future<Deployment> future) {
		deploymentMap.get(deployId, res -> {
			if (res.succeeded()) {
				Deployment deploy = res.result();
				if (deploy == null) {
					getDeploymentFromDb(deployId, future);
				} else if (deploy instanceof NullDeployment) {
					future.complete();
				} else {
					future.complete(deploy);
				}
			} else {
				LOGGER.error("Failed to fetch deployment from cache", res.cause());
				getDeploymentFromDb(deployId, future);
			}
		});
	}

	private void getDeploymentFromDb(long deployId, Future<Deployment> future) {
		vertx.<Deployment>executeBlocking(fut -> {
			fut.complete(terminalService.getDeployment(deployId));
		}, res -> {
			if (res.succeeded()) {
				Deployment deploy = res.result();
				future.complete(deploy);
				putDeploymentToCache(deployId, deploy);
			} else {
				LOGGER.error("Failed to fetch deployment from DB", res.cause());
			}
		});
	}

	public TerminalService getTerminalService() {
		return terminalService;
	}

	public void setTerminalService(TerminalService terminalService) {
		this.terminalService = terminalService;
	}

}
