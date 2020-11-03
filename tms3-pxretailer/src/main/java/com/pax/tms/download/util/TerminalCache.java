package com.pax.tms.download.util;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.pax.common.redis.Caches;
import com.pax.tms.download.model.Deployment;
import com.pax.tms.download.model.Terminal;
import com.pax.tms.download.model.TerminalStatus;
import com.pax.tms.pxretailer.PxRetailerTerminalManager;
import com.pax.tms.pxretailer.action.TerminalActionType;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.redis.RedisClient;

public class TerminalCache {

	private static final String STATUS_KEY = "status";
	private static final String ONLINE_KEY = "online";
	private static final String SCHEDULED_KEY = "scheduled";
	private static final String DWNLSTARTTM_KEY = "dwnlStartTm";
	private static final String DWNLENDTM_KEY = "dwnlEndTm";
	private static final String DEPLOYID_KEY = "deployId";
	private static final String PKGID_KEY = "pkgId";
	private static final String ID_KEY = "id";
	private static final String TZ_KEY = "tz";
	private static final String DS_KEY = "ds";
	private static final String SYNCTIME_KEY = "syncTime";
	private static final String SYN_KEY = "syn";
	private static final String IP_KEY = "ip";
	private static final String DWNLSTATUS_KEY = "dwnlStatus";
	private static final String NEED_HMS_KEY = "needHms";

	private static final List<String> CALLHOME_HMGET_FIELDS = Arrays.asList(STATUS_KEY, ID_KEY, TZ_KEY, DS_KEY,
			SYNCTIME_KEY, IP_KEY, ONLINE_KEY, SCHEDULED_KEY, DWNLSTARTTM_KEY, DWNLENDTM_KEY, NEED_HMS_KEY, SYN_KEY);
	private static final List<String> HMS_HMGET_FIELDS = Arrays.asList(STATUS_KEY, ID_KEY, IP_KEY, ONLINE_KEY);
	private static final List<String> DOWNLOAD_HMGET_FIELDS = Arrays.asList(STATUS_KEY, ID_KEY, IP_KEY, ONLINE_KEY,
			DEPLOYID_KEY, PKGID_KEY, TZ_KEY, DS_KEY);
	private static final List<String> DELETE_SCHEDULE_FIELDS = Arrays.asList(SCHEDULED_KEY, DEPLOYID_KEY, PKGID_KEY,
			DWNLSTARTTM_KEY, DWNLENDTM_KEY, DWNLSTATUS_KEY);

	private final Logger logger = LoggerFactory.getLogger(PxRetailerTerminalManager.class);

	private RedisClient redisClient;

	private int terminalCacheMaxTime;

	public TerminalCache(RedisClient redisClient, int terminalCacheMaxTime) {
		this.redisClient = redisClient;
		this.terminalCacheMaxTime = terminalCacheMaxTime;
	}

	public void getTerminal(String deviceSerialNumber, TerminalActionType actionType,
			Handler<AsyncResult<Terminal>> handler) {
		String cacheKey = Caches.getTerminalCacheKey(deviceSerialNumber);
		List<String> hmgetFields = hmgetFields(actionType);
		getTerminal(cacheKey, actionType, hmgetFields, handler);
	}

	private void getTerminal(String cacheKey, TerminalActionType actionType, List<String> hmgetFields,
			Handler<AsyncResult<Terminal>> handler) {
		redisClient.hmget(cacheKey, hmgetFields, hmgetRes -> {
			if (hmgetRes.failed()) {
				logger.error("cacheKey={} - failed to get terminal info from redis", hmgetRes.cause(), cacheKey);
				handler.handle(Future.failedFuture("Failed to get terminal info from redis"));
			} else {
				JsonArray jsonArray = hmgetRes.result();
				Terminal terminal = toTerminal(jsonArray, actionType);
				if (terminal != null && terminal.isNeedHms() && actionType == TerminalActionType.CALLHOME) {
					removeNeedHmsFlag(cacheKey, hdelRes -> {
						if (hdelRes.failed()) {
							handler.handle(Future.failedFuture(hdelRes.cause()));
						} else {
							handler.handle(Future.succeededFuture(terminal));
							setExpirationTime(cacheKey);
						}
					});
				} else {
					handler.handle(Future.succeededFuture(terminal));
					setExpirationTime(cacheKey);
				}
			}
		});
	}

	private List<String> hmgetFields(TerminalActionType actionType) {
		switch (actionType) {
		case HMS:
			return HMS_HMGET_FIELDS;
		case DOWNLOAD:
			return DOWNLOAD_HMGET_FIELDS;
		default:
			return CALLHOME_HMGET_FIELDS;
		}
	}

	private Terminal toTerminal(JsonArray jsonArray, TerminalActionType reqType) {
		String terminalStatus = jsonArray.getString(0);
		if (terminalStatus == null) {
			return null;
		}
		Terminal terminal = new Terminal();
		terminal.setTerminalStatus(Integer.parseInt(terminalStatus));
		if (terminal.isUnregistered()) {
			return terminal;
		}

		terminal.setTerminalId(jsonArray.getString(1));

		switch (reqType) {
		case HMS:
			terminal.setLastSourceIp(jsonArray.getString(2));
			terminal.setOnline(Integer.parseInt(jsonArray.getString(3)));
			break;
		case DOWNLOAD:
			terminal.setLastSourceIp(jsonArray.getString(2));
			terminal.setOnline(Integer.parseInt(jsonArray.getString(3)));
			if (StringUtils.isNotEmpty(jsonArray.getString(4))) {
				terminal.setDeployId(Long.parseLong(jsonArray.getString(4)));
			}
			if (StringUtils.isNotEmpty(jsonArray.getString(5))) {
				terminal.setPkgId(Long.parseLong(jsonArray.getString(5)));
			}
			terminal.setTimeZone(jsonArray.getString(6));
			terminal.setUseDaylightTime(StringUtils.equals("1", jsonArray.getString(7)));
			break;
		default:
			toTerminalDefault(terminal, jsonArray);
		}
		return terminal;
	}

	private void toTerminalDefault(Terminal terminal, JsonArray jsonArray) {
		terminal.setTimeZone(jsonArray.getString(2));
		terminal.setUseDaylightTime("1".equals(jsonArray.getString(3)));

		if (StringUtils.isNotEmpty(jsonArray.getString(4))) {
			terminal.setLastSyncTime(new Date(Long.parseLong(jsonArray.getString(4))));
		}
		terminal.setLastSourceIp(jsonArray.getString(5));
		terminal.setOnline(Integer.parseInt(jsonArray.getString(6)));
		String scheduled = jsonArray.getString(7);
		terminal.setScheduled(scheduled);
		if ("1".equals(scheduled) && StringUtils.isNotEmpty(jsonArray.getString(8))) {
			terminal.setDwnlStartTm(new Date(Long.parseLong(jsonArray.getString(8))));
		}

		if ("1".equals(jsonArray.getString(10))) {
			terminal.setNeedHms(true);
		}
		terminal.setSyncToServerTime(StringUtils.equals("1", jsonArray.getString(11)));
	}

	public void saveTerminal(Terminal terminal, Handler<AsyncResult<Terminal>> handler) {
		String cacheKey = Caches.getTerminalCacheKey(terminal.getTerminalSn());
		JsonObject jsonObj = toJson(terminal);
		redisClient.hmset(cacheKey, jsonObj, hmsetRes -> {
			if (!hmsetRes.succeeded()) {
				logger.error("deviceType={}, deviceSerialNumber={} - failed to save terminal info to redis",
						hmsetRes.cause(), terminal.getModelId(), terminal.getTerminalSn());
				handler.handle(Future.failedFuture("Failed to save terminal info to redis"));
			} else {
				setExpirationTime(cacheKey);
				handler.handle(Future.succeededFuture(terminal));
			}
		});
	}

	private void setExpirationTime(String cacheKey) {
		if (terminalCacheMaxTime > 0) {
			redisClient.expire(cacheKey, terminalCacheMaxTime, exipreRes -> {
				if (exipreRes.failed()) {
					logger.error("cacheKey={} - failed to set expire time", exipreRes.cause(), cacheKey);
				}
			});
		}
	}

	private JsonObject toJson(Terminal terminal) {
		JsonObject jsonObj = new JsonObject();
		if (terminal == null || terminal.isUnregistered()) {
			jsonObj.put(STATUS_KEY, "-1");
			return jsonObj;
		}

		jsonObj.put(STATUS_KEY, terminal.getTerminalStatus());
		jsonObj.put(ID_KEY, terminal.getTerminalId());
		jsonObj.put(IP_KEY, terminal.getLastSourceIp());
		jsonObj.put(ONLINE_KEY, terminal.getOnline());

		setTimeZoneInfo(jsonObj, terminal);
		setScheduleInfo(jsonObj, terminal);
		return jsonObj;
	}

	private void setTimeZoneInfo(JsonObject jsonObj, Terminal terminal) {

		jsonObj.put(TZ_KEY, terminal.getTimeZone());
		jsonObj.put(DS_KEY, terminal.isUseDaylightTime() ? "1" : "0");
		jsonObj.put(SYN_KEY, terminal.isSyncToServerTime() ? "1" : 0);

	}

	// set schedule info
	private void setScheduleInfo(JsonObject jsonObj, Terminal terminal) {
		jsonObj.put(SCHEDULED_KEY, terminal.getScheduled());
		if ("1".equals(terminal.getScheduled())) {
			jsonObj.put(DWNLSTARTTM_KEY,
					terminal.getDwnlStartTm() != null ? terminal.getDwnlStartTm().getTime() : null);
			jsonObj.put(DWNLENDTM_KEY, terminal.getDwnlEndTm() != null ? terminal.getDwnlEndTm().getTime() : null);
			jsonObj.put(DWNLSTATUS_KEY, terminal.getDwnlStatus());
			jsonObj.put(DEPLOYID_KEY, terminal.getDeployId());
			jsonObj.put(PKGID_KEY, terminal.getPkgId());
		}
	}

	public void clearScheduledPackage(Terminal terminal, Handler<AsyncResult<Void>> handler) {
		String cacheKey = Caches.getTerminalCacheKey(terminal.getTerminalSn());
		redisClient.hdelMany(cacheKey, DELETE_SCHEDULE_FIELDS, hdelManyRes -> {
			if (!hdelManyRes.succeeded()) {
				logger.error("deviceType={}, deviceSerialNumber={} - failed to clear scheduled package in redis",
						hdelManyRes.cause(), terminal.getModelId(), terminal.getTerminalSn());
				handler.handle(Future.failedFuture("Failed to clear scheduled package in redis"));
			} else {
				handler.handle(Future.succeededFuture());
			}
		});
	}

	public void setScheduledPackage(Terminal terminal, Deployment deployment,
			Handler<AsyncResult<Deployment>> handler) {
		String cacheKey = Caches.getTerminalCacheKey(terminal.getTerminalSn());
		JsonObject jsonObj = new JsonObject();
		if (deployment == null) {
			jsonObj.put(SCHEDULED_KEY, 0);
		} else {
			jsonObj.put(SCHEDULED_KEY, 1);
			jsonObj.put(DEPLOYID_KEY, deployment.getDeployId());
			jsonObj.put(PKGID_KEY, deployment.getPkgId());
			jsonObj.put(DWNLSTARTTM_KEY,
					deployment.getDwnlStartTm() != null ? deployment.getDwnlStartTm().getTime() : null);
			jsonObj.put(DWNLENDTM_KEY, deployment.getDwnlEndTm() != null ? deployment.getDwnlEndTm().getTime() : null);
			jsonObj.put(DWNLSTATUS_KEY, deployment.getDwnlStatus());
		}

		redisClient.hmset(cacheKey, jsonObj, hmsetRes -> {
			if (!hmsetRes.succeeded()) {
				logger.error("deviceType={}, deviceSerialNumber={} - failed to put scheduled package into redis",
						hmsetRes.cause(), terminal.getModelId(), terminal.getTerminalSn());
				handler.handle(Future.failedFuture("Failed to put scheduled package into redis"));
			} else {
				handler.handle(Future.succeededFuture(deployment));
			}
		});
	}

	public void setNeedHmsFlag(Terminal terminal, Handler<AsyncResult<Void>> handler) {
		String cacheKey = Caches.getTerminalCacheKey(terminal.getTerminalSn());
		redisClient.hset(cacheKey, NEED_HMS_KEY, "1", hmsetRes -> {
			if (hmsetRes.failed()) {
				logger.error("deviceType={}, deviceSerialNumber={} - failed to set needHms flag in redis",
						hmsetRes.cause(), terminal.getModelId(), terminal.getTerminalSn());
				handler.handle(Future.failedFuture("Failed to set needHms flag in redis"));
			} else {
				handler.handle(Future.succeededFuture());
			}
		});
	}

	public void removeNeedHmsFlag(String cacheKey, Handler<AsyncResult<Void>> handler) {
		redisClient.hdel(cacheKey, NEED_HMS_KEY, hdelRes -> {
			if (!hdelRes.succeeded()) {
				logger.error("cacheKey={} - failed to remove needHms flag in redis", hdelRes.cause(), cacheKey);
				handler.handle(Future.failedFuture("Failed to remove needHms flag in redis"));
			} else {
				handler.handle(Future.succeededFuture());
			}
		});
	}

	public void setTerminalOnline(Terminal terminal, String sourceIp, Handler<AsyncResult<Void>> handler) {
		String cacheKey = Caches.getTerminalCacheKey(terminal.getTerminalSn());
		JsonObject jsonObj = new JsonObject();
		jsonObj.put(ONLINE_KEY, TerminalStatus.ONLINE_STATUS);
		jsonObj.put(IP_KEY, sourceIp);

		redisClient.hmset(cacheKey, jsonObj, hmsetRes -> {
			if (hmsetRes.failed()) {
				logger.error("deviceType={}, deviceSerialNumber={} - failed to set terminal online in redis",
						hmsetRes.cause(), terminal.getModelId(), terminal.getTerminalSn());
				handler.handle(Future.failedFuture("Failed to set terminal online in redis"));
			} else {
				handler.handle(Future.succeededFuture());
			}
		});
	}

	public void delete(Terminal terminal, Handler<AsyncResult<Void>> handler) {
		String cacheKey = Caches.getTerminalCacheKey(terminal.getTerminalSn());
		redisClient.del(cacheKey, delRes -> {
			if (delRes.failed()) {
				logger.error("deviceType={}, deviceSerialNumber={} - failed to delete terminal info from redis",
						delRes.cause(), terminal.getModelId(), terminal.getTerminalSn());
				if (handler != null) {
					handler.handle(Future.failedFuture(delRes.cause()));
				}
			} else {
				if (handler != null) {
					handler.handle(Future.succeededFuture());
				}
			}
		});
	}

	public void updateAccessTime(Terminal terminal, Date accessTime) {
		redisClient.zadd("PXRETAILER_ONLINE", accessTime.getTime(), terminal.getTerminalSn(), res -> {
			if (res.failed()) {
				logger.error("deviceType={}, deviceSerialNumber={} - failed to update online terminals set in redis",
						res.cause(), terminal.getModelId(), terminal.getTerminalSn());
			}
		});

		redisClient.hset("PXRETAILER_ACCESS_TIME", terminal.getTerminalSn(), Long.toString(accessTime.getTime()),
				res -> {
					if (res.failed()) {
						logger.error(
								"deviceType={}, deviceSerialNumber={} - failed to update terminal last access time in redis",
								res.cause(), terminal.getModelId(), terminal.getTerminalSn());
					}
				});
	}

	public void updateLastSyncTime(Terminal terminal, long syncTime) {
		String cacheKey = Caches.getTerminalCacheKey(terminal.getTerminalSn());
		redisClient.hset(cacheKey, SYNCTIME_KEY, Long.toString(syncTime), hsetRec -> {
			if (hsetRec.failed()) {
				logger.error("deviceType={}, deviceSerialNumber={} - failed to update terminal last sync time in redis",
						hsetRec.cause(), terminal.getModelId(), terminal.getTerminalSn());
			}
		});
	}

	public void updateTerminalAppInstallCache(Terminal terminal, Handler<AsyncResult<Void>> handler) {
		String cacheKeyForHashValue = Caches.getTerminalInstalledAppHashCacheKey(terminal.getTerminalSn());

		redisClient.set(cacheKeyForHashValue, terminal.getTerminalInstallations(), res -> {
			if (res.failed()) {
				logger.error(
						"deviceType={}, deviceSerialNumber={} - failed to update terminal  install apps into redis cache",
						res.cause(), terminal.getModelId(), terminal.getTerminalSn());
				handler.handle(Future.failedFuture("Failed  to update terminal  install apps into redis cache"));
			} else {
				setExpirationTime(cacheKeyForHashValue);
				handler.handle(Future.succeededFuture());

			}

		});
	}
	
	public void updateTerminalSysmetricKeysCache(Terminal terminal, Handler<AsyncResult<Void>> handler) {
        String cacheKeyForHashValue = Caches.getTerminalSysmetricKeysHashCacheKey(terminal.getTerminalSn());

        redisClient.set(cacheKeyForHashValue, terminal.getTerminalSysmetricKeys(), res -> {
            if (res.failed()) {
                logger.error(
                        "deviceType={}, deviceSerialNumber={} - failed to update terminal  SysmetricKeys into redis cache",
                        res.cause(), terminal.getModelId(), terminal.getTerminalSn());
                handler.handle(Future.failedFuture("Failed  to update terminal  SysmetricKeys into redis cache"));
            } else {
                setExpirationTime(cacheKeyForHashValue);
                handler.handle(Future.succeededFuture());

            }

        });
    }
}
