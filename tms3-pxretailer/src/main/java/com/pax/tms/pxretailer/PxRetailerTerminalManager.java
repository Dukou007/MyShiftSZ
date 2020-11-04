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

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.redis.RedisClient;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pax.common.redis.Caches;
import com.pax.tms.deploy.model.DownOrActvStatus;
import com.pax.tms.download.model.Deployment;
import com.pax.tms.download.model.Terminal;
import com.pax.tms.download.service.TerminalDownloadService;
import com.pax.tms.download.util.TerminalCache;
import com.pax.tms.pxretailer.action.TerminalActionType;
import com.pax.tms.pxretailer.message.TerminalInstalledApp;
import com.pax.tms.pxretailer.message.TerminalInstalledAppList;

/**
 * @author Elliott.Z
 */
public class PxRetailerTerminalManager {

	private final Logger logger = LoggerFactory.getLogger(PxRetailerTerminalManager.class);

	private Vertx vertx;
	private RedisClient redisClient;

	private TerminalDownloadService terminalStorageService;

	private TerminalCache terminalCache;

	public PxRetailerTerminalManager(Vertx vertx, RedisClient redisClient) {
		this.vertx = vertx;
		this.redisClient = redisClient;
	}

	@Autowired
	public void setTerminalStorageService(TerminalDownloadService terminalStorageService) {
		this.terminalStorageService = terminalStorageService;
	}

	@Autowired
	public void setConfig(PxRetailerConfig config) {
		this.terminalCache = new TerminalCache(redisClient, config.getTerminalCacheMaxTime());
	}

	public void getTerminal(String deviceType, String deviceSerialNumber, TerminalActionType actionType,
			Handler<AsyncResult<Terminal>> handler) {
		getTerminalFromCache(deviceType, deviceSerialNumber, actionType, handler);
	}

	private void getTerminalFromCache(String deviceType, String deviceSerialNumber, TerminalActionType actionType,
			Handler<AsyncResult<Terminal>> handler) {
		terminalCache.getTerminal(deviceSerialNumber, actionType, res -> {
			if (res.failed()) {
				logger.error("deviceType={}, deviceSerialNumber={} - failed to get terminal info from cache",
						res.cause(), deviceType, deviceSerialNumber);
				handler.handle(Future.failedFuture("Failed to get terminal info from cache"));
			} else {
				Terminal terminal = res.result();
				if (terminal != null) {
					afterGetTerminalFromCache(deviceType, deviceSerialNumber, actionType, handler, terminal);
				} else {
					getTerminalFromDB(deviceType, deviceSerialNumber, handler);
				}
			}
		});
	}

	private void afterGetTerminalFromCache(String deviceType, String deviceSerialNumber, TerminalActionType reqType,
			Handler<AsyncResult<Terminal>> handler, Terminal terminal) {
		terminal.setTerminalSn(deviceSerialNumber);
		terminal.setModelId(deviceType);
		if (reqType == TerminalActionType.CALLHOME && !terminal.isUnregistered()) {
			checkScheduledPackage(terminal, handler);
		} else {
			handler.handle(Future.succeededFuture(terminal));
		}
	}

	private void checkScheduledPackage(Terminal terminal, Handler<AsyncResult<Terminal>> handler) {
		if (terminal.getScheduled() == null) {
			reloadScheduledPackage(terminal, handler);
		} else if (terminal.isScheduledPackageExpired()) {
			terminalCache.clearScheduledPackage(terminal, removeCacheRes -> {
				if (removeCacheRes.failed()) {
					logger.error(
							"deviceType={}, deviceSerialNumber={} - failed to remove expired scheduled package from cache",
							removeCacheRes.cause(), terminal.getModelId(), terminal.getTerminalSn());
					terminal.setScheduled(null);
					handler.handle(Future.succeededFuture(terminal));
				} else {
					reloadScheduledPackage(terminal, handler);
				}
			});
		} else {
			handler.handle(Future.succeededFuture(terminal));
		}
	}

	private void reloadScheduledPackage(Terminal terminal, Handler<AsyncResult<Terminal>> handler) {
		this.getScheduledPackage(terminal, getSchRes -> {
			if (getSchRes.failed()) {
				handler.handle(Future.failedFuture(getSchRes.cause()));
			} else {
				Deployment deployment = getSchRes.result();
				if (deployment == null) {
					terminal.setScheduled("0");
				} else {
					terminal.setScheduled("1");
					terminal.setDwnlStartTm(deployment.getDwnlStartTm());
					terminal.setDwnlEndTm(deployment.getDwnlEndTm());
				}
				handler.handle(Future.succeededFuture(terminal));
			}
		});
	}

	private void getTerminalFromDB(String deviceType, String deviceSerialNumber,
			Handler<AsyncResult<Terminal>> handler) {
		vertx.<Terminal>executeBlocking(fut -> {
			logger.debug("deviceType={}, deviceSerialNumber={} - load terminal info from database", deviceType,
					deviceSerialNumber);
			fut.complete(terminalStorageService.getTerminalBySn(deviceType, deviceSerialNumber));
		}, false, res -> {
			if (res.failed()) {
				logger.error("deviceType={}, deviceSerialNumber={} - failed to get terminal info from database",
						res.cause(), deviceType, deviceSerialNumber);
				handler.handle(Future.failedFuture("Failed to get terminal info from database"));
			} else {
				Terminal terminal = res.result();
				if (terminal != null && terminal.getTerminalSn() == null) {
					terminal.setTerminalSn(deviceSerialNumber);
					terminal.setModelId(deviceType);
				}
				afterGetTerminalFromDB(deviceType, deviceSerialNumber, terminal, handler);
			}
		});
	}

	private void afterGetTerminalFromDB(String deviceType, String deviceSerialNumber, Terminal terminal,
			Handler<AsyncResult<Terminal>> handler) {
		if (terminal != null) {
			terminalCache.saveTerminal(terminal, handler);
		} else {
			logger.debug("deviceType={}, deviceSerialNumber={} - terminal does not exist in database", deviceType,
					deviceSerialNumber);
			handler.handle(Future.succeededFuture(terminal));
		}
	}

	public void getScheduledPackage(Terminal terminal, Handler<AsyncResult<Deployment>> handler) {
		vertx.<Deployment>executeBlocking(fut -> {
			logger.debug("deviceType={}, deviceSerialNumber={} - load scheduled package from database",
					terminal.getModelId(), terminal.getTerminalSn());
			fut.complete(terminalStorageService.getScheduledPackage(terminal));
		}, false, res -> {
			if (res.failed()) {
				logger.error("deviceType={}, deviceSerialNumber={} - failed to load scheduled package from database",
						res.cause(), terminal.getModelId(), terminal.getTerminalSn());
				handler.handle(Future.failedFuture("Failed to load scheduled package from database"));
			} else {
				terminalCache.setScheduledPackage(terminal, res.result(), handler);
			}
		});
	}

	public void setNeedHmsFlag(Terminal terminal, Handler<AsyncResult<Void>> handler) {
		terminalCache.setNeedHmsFlag(terminal, handler);
	}

	public void setTerminalOnline(Terminal terminal, String sourceIp, Date accessTime,
			Handler<AsyncResult<Void>> handler) {
		terminalCache.setTerminalOnline(terminal, sourceIp, updateCacheRes -> {
			if (updateCacheRes.failed()) {
				handler.handle(Future.failedFuture(updateCacheRes.cause()));
			} else {
				vertx.executeBlocking(fut -> {
					terminalStorageService.setTerminalOnline(terminal, sourceIp, accessTime);
					fut.complete();
				}, false, res -> {
					if (res.succeeded()) {
						handler.handle(Future.succeededFuture());
					} else {
						logger.error("deviceType={}, deviceSerialNumber={} - failed to set terminal online in database",
								res.cause(), terminal.getModelId(), terminal.getTerminalSn());
						terminalCache.delete(terminal,
								deleteCacheRes -> handler.handle(Future.failedFuture(res.cause())));
					}
				});
			}
		});
	}

	public void updateLastAccessTime(Terminal terminal, Date accessTime) {
		terminalCache.updateAccessTime(terminal, accessTime);
	}

	public void updateLasSyncTime(Terminal terminal, long syncTime) {
		terminalCache.updateLastSyncTime(terminal, syncTime);
	}

	public void saveHealthMonitorInfo(Terminal terminal, Handler<AsyncResult<Void>> handler) {
		vertx.executeBlocking(fut -> {
			terminalStorageService.saveHealthMonitorInfo(terminal);
			fut.complete();
		}, false, res -> {
			if (res.succeeded()) {
				handler.handle(Future.succeededFuture());
			} else {
				logger.error("deviceType={}, deviceSerialNumber={} - failed to save health monitor info into database",
						res.cause(), terminal.getModelId(), terminal.getTerminalSn());
				handler.handle(Future.failedFuture("Failed to save health monitor info into database"));
			}
		});
	}

	public void clearTerminalCache(Terminal terminal) {
		terminalCache.delete(terminal, null);
	}

	public void updateDeploymentStatus(Terminal terminal, long deployId, DownOrActvStatus downloadStatus,
			DownOrActvStatus activationStatus, String activationCode, Handler<AsyncResult<Void>> handler) {
		if (downloadStatus != null) {
			switch (downloadStatus) {
			case SUCCESS:
			case FAILED:
			case CANCELED:
				clearScheduledPackage(terminal, deployId, downloadStatus, activationStatus, activationCode,handler);
				break;
			default:
				saveDeploymentStatus(terminal, deployId, downloadStatus, activationStatus, activationCode, handler);
			}
		} else {
			saveDeploymentStatus(terminal, deployId, downloadStatus, activationStatus, activationCode, handler);
		}
	}

	private void clearScheduledPackage(Terminal terminal, long deployId, DownOrActvStatus downloadStatus,
			DownOrActvStatus activationStatus,String activationCode, Handler<AsyncResult<Void>> handler) {
		terminalCache.clearScheduledPackage(terminal, res -> {
			if (res.failed()) {
				handler.handle(Future.failedFuture(res.cause()));
			} else {
				saveDeploymentStatus(terminal, deployId, downloadStatus, activationStatus, activationCode, handler);
			}
		});
	}

	private void saveDeploymentStatus(Terminal terminal, long deployId, DownOrActvStatus downloadStatus,
			DownOrActvStatus activationStatus,String activationCode, Handler<AsyncResult<Void>> handler) {
		vertx.<Deployment>executeBlocking(fut -> {
			terminalStorageService.updateDeploymentStatus(terminal, new Date(), deployId, downloadStatus,
					activationStatus, activationCode);
			fut.complete();
		}, false, res -> {
			if (res.succeeded()) {
				handler.handle(Future.succeededFuture());
			} else {
				handler.handle(Future.failedFuture(res.cause()));
			}
		});
	}

	public void addUnregisteredTerminal(String deviceType, String deviceSerialNumber, String sourceIp, Date accessTime,
			Future<Void> future) {
		vertx.executeBlocking(fut -> {
			// logger.debug("deviceType={}, deviceSerialNumber={} - add
			// unregistered terminal into database", deviceType,
			// deviceSerialNumber);
			terminalStorageService.addUnregisteredTerminal(deviceType, deviceSerialNumber, sourceIp, accessTime);
			fut.complete();
		}, false, res -> {
			if (res.failed()) {
				logger.error("deviceType={}, deviceSerialNumber={} - failed to add unregistered terminal into database",
						res.cause(), deviceType, deviceSerialNumber);
			}
			future.complete();
		});
	}

	public void updateUnregisteredTerminal(String deviceType, String deviceSerialNumber, String sourceIp,
			Date accessTime, Future<Void> future) {
		vertx.executeBlocking(fut -> {
			logger.debug("deviceType={}, deviceSerialNumber={} - update unregistered terminal in database", deviceType,
					deviceSerialNumber);
			terminalStorageService.updateUnregisteredTerminal(deviceType, deviceSerialNumber, sourceIp, accessTime);
			fut.complete();
		}, false, res -> {
			if (res.failed()) {
				logger.error(
						"deviceType={}, deviceSerialNumber={} - failed to update unregistered terminal in database",
						res.cause(), deviceType, deviceSerialNumber);
			}
			future.complete();
		});
	}

	public void updateTerminalInstallApps(Terminal terminal, Handler<AsyncResult<Void>> handler) {
		if (StringUtils.isEmpty(terminal.getTerminalInstallations())) {
			handler.handle(Future.succeededFuture());
			return;
		}

		String cacheKey = Caches.getTerminalInstalledAppHashCacheKey(terminal.getTerminalSn());

		redisClient.get(cacheKey, res -> {

			if (res.failed()) {

				logger.error("failed to get terminal install apps info from redis cache");
				handler.handle(Future.failedFuture("failed to get terminal install apps info from redis cache"));

			} else {

				String redisTerminalInstallApps = res.result();
				String terminalInstallApps = terminal.getTerminalInstallations();
				boolean isInstallAppSame = checkInstallAppSame(terminal.getTerminalSn(), redisTerminalInstallApps,
						terminalInstallApps);

				if (isInstallAppSame) {
					updateStorgeTerminalInstallAppReportTime(terminal, handler);

				} else {
					updateStorgeTerminalInstallAppsAndEvent(terminal, handler);

				}

			}

		});

	}

	private void updateStorgeTerminalInstallAppReportTime(Terminal terminal, Handler<AsyncResult<Void>> handler) {
		vertx.executeBlocking(fut -> {
			terminalStorageService.updateTerminalInstallAppReportTime(terminal.getTerminalSn());
			fut.complete();
		}, false, res -> {
			if (res.succeeded()) {
				handler.handle(Future.succeededFuture());
			} else {
				logger.error(
						"deviceType={}, deviceSerialNumber={} - failed to update terminal  install apps report time into database",
						res.cause(), terminal.getModelId(), terminal.getTerminalSn());
				handler.handle(
						Future.failedFuture("Failed  to update terminal  install apps report time into database"));
			}
		});

	}

	private boolean checkInstallAppSame(String terminalSn, String redisTerminalInstallApps,
			String terminalInstallApps) {

		if (StringUtils.isEmpty(redisTerminalInstallApps)) {
			redisTerminalInstallApps = terminalStorageService.getTerminalInstalledApps(terminalSn);
			if (StringUtils.isEmpty(redisTerminalInstallApps)) {
				return false;
			}

		}
		List<TerminalInstalledApp> redisTerminalInstallAppList = Json.decodeValue(redisTerminalInstallApps,
				TerminalInstalledAppList.class);
		List<TerminalInstalledApp> terminalInstallAppList = Json.decodeValue(terminalInstallApps,
				TerminalInstalledAppList.class);

		return redisTerminalInstallAppList.equals(terminalInstallAppList);
	}

	private void updateStorgeTerminalInstallAppsAndEvent(Terminal terminal, Handler<AsyncResult<Void>> handler) {

		vertx.executeBlocking(fut -> {
			terminalStorageService.updateStorgeTerminalInstallAppsAndEvent(terminal);
			fut.complete();
		}, false, res -> {
			if (res.succeeded()) {
				terminalCache.updateTerminalAppInstallCache(terminal, handler);
			} else {
				logger.error(
						"deviceType={}, deviceSerialNumber={} - failed to update terminal  install apps into database",
						res.cause(), terminal.getModelId(), terminal.getTerminalSn());
				handler.handle(Future.failedFuture("Failed  to update terminal  install apps into database"));
			}
		});
	}
	
	public void updateTerminalSysmetricKeys(Terminal terminal, Handler<AsyncResult<Void>> handler) {
	    String terminalSysmetricKeys = terminal.getTerminalSysmetricKeys();
        if (StringUtils.isEmpty(terminalSysmetricKeys)) {
            handler.handle(Future.succeededFuture());
            return;
        }

        String cacheKey = Caches.getTerminalSysmetricKeysHashCacheKey(terminal.getTerminalSn());

        redisClient.get(cacheKey, res -> {

            if (res.failed()) {

                logger.error("failed to get terminal Sysmetric Keys info from redis cache");
                handler.handle(Future.failedFuture("failed to get terminal Sysmetric Keys info from redis cache"));

            } else {

                String redisTerminalSysmetricKeys = res.result();
                boolean isInstallAppSame = checkSysmetricKeysSame(terminal.getTerminalSn(), redisTerminalSysmetricKeys,
                        terminalSysmetricKeys);

                if (isInstallAppSame) {
                    updateStorgeTerminalSysmetricKeysReportTime(terminal, handler);

                } else {
                    updateStorgeTerminalSysmetricKeysAndEvent(terminal, handler);
                }

            }

        });

    }

    private void updateStorgeTerminalSysmetricKeysReportTime(Terminal terminal, Handler<AsyncResult<Void>> handler) {
        vertx.executeBlocking(fut -> {
            terminalStorageService.updateTerminalSysmetricKeysReportTime(terminal.getTerminalSn());
            fut.complete();
        }, false, res -> {
            if (res.succeeded()) {
                handler.handle(Future.succeededFuture());
            } else {
                logger.error(
                        "deviceType={}, deviceSerialNumber={} - failed to update terminal Sysmetric Keys report time into database",
                        res.cause(), terminal.getModelId(), terminal.getTerminalSn());
                handler.handle(
                        Future.failedFuture("Failed  to update terminal  Sysmetric Keys report time into database"));
            }
        });

    }

    private boolean checkSysmetricKeysSame(String terminalSn, String redisTerminalSysmetricKeys,
            String terminalSysmetricKeys) {
        if (StringUtils.isEmpty(redisTerminalSysmetricKeys)) {
            redisTerminalSysmetricKeys = terminalStorageService.getTerminalSysmetricKeys(terminalSn);
            if (StringUtils.isEmpty(redisTerminalSysmetricKeys)) {
                return false;
            }
        }
        return redisTerminalSysmetricKeys.equals(terminalSysmetricKeys);
    }

    private void updateStorgeTerminalSysmetricKeysAndEvent(Terminal terminal, Handler<AsyncResult<Void>> handler) {

        vertx.executeBlocking(fut -> {
            terminalStorageService.updateStorgeTerminalSysmetricKeysAndEvent(terminal);
            fut.complete();
        }, false, res -> {
            if (res.succeeded()) {
                terminalCache.updateTerminalSysmetricKeysCache(terminal, handler);
            } else {
                logger.error(
                        "deviceType={}, deviceSerialNumber={} - failed to update terminal Sysmetric Keys into database",
                        res.cause(), terminal.getModelId(), terminal.getTerminalSn());
                handler.handle(Future.failedFuture("Failed  to update terminal Sysmetric Keys into database"));
            }
        });
    }

}
