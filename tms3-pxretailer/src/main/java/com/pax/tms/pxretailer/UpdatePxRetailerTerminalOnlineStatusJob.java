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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import com.pax.common.redis.Caches;
import com.pax.tms.download.service.TerminalDownloadService;

@Component
public class UpdatePxRetailerTerminalOnlineStatusJob {

	private final Logger logger = LoggerFactory.getLogger(UpdatePxRetailerTerminalOnlineStatusJob.class);

	private TerminalDownloadService terminalService;

	private RedisTemplate<String, String> redisTemplate;

	private PxRetailerConfig config;

	private UpdateTerminalOnlineStatusTask updateTerminalOnlineStatusTask = new UpdateTerminalOnlineStatusTask();
	private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

	@PostConstruct
	public void start() {
		scheduledExecutorService.schedule(updateTerminalOnlineStatusTask,
				config.getUpdateTerminalOnlineStatusInterval(), TimeUnit.MILLISECONDS);
	}

	@Autowired
	public void setTerminalService(TerminalDownloadService terminalService) {
		this.terminalService = terminalService;
	}

	@Autowired
	@Qualifier("stringRedisTemplate")
	public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Autowired
	public void setConfig(PxRetailerConfig config) {
		this.config = config;
	}

	private class UpdateTerminalOnlineStatusTask implements Runnable {
		@Override
		public void run() {
			try {
				if (lock()) {
					updateTerminalAccessTime();
					updateTerminalOnlineStatus();
					checkOfflineTerminal();
				}
			} finally {
				if (!scheduledExecutorService.isShutdown()) {
					scheduledExecutorService.schedule(updateTerminalOnlineStatusTask,
							config.getUpdateTerminalOnlineStatusInterval(), TimeUnit.MILLISECONDS);
				}
			}
		}

		private boolean lock() {
			try {
				logger.debug("try to get PXRETAILER_UPDATE_ONLINE_STATUS_LOCK");
				String result = redisTemplate.execute(new RedisCallback<String>() {
					@Override
					public String doInRedis(RedisConnection connection) {
						byte[] result = (byte[]) connection.execute("SET",
								Caches.PXRETAILER_UPDATE_ONLINE_STATUS_LOCK.getBytes(), "1".getBytes(), "PX".getBytes(),
								String.valueOf(config.getUpdateTerminalOnlineStatusInterval()).getBytes(),
								"NX".getBytes());
						return result == null ? null : new String(result);
					}
				});

				if (!"OK".equalsIgnoreCase(result)) {
					logger.debug("can not get PXRETAILER_UPDATE_ONLINE_STATUS_LOCK, result {}", result);
					return false;
				} else {
					logger.debug("success to get PXRETAILER_UPDATE_ONLINE_STATUS_LOCK");
					return true;
				}
			} catch (Exception e) {
				logger.error("failed to get PXRETAILER_UPDATE_ONLINE_STATUS_LOCK： {}", e.getMessage());
			}
			return false;
		}

		private void updateTerminalOnlineStatus() {
			try {
				logger.debug("start to update pxretailer terminal online status");
				long cur = System.currentTimeMillis();
				long maxLastAccessTime = cur - config.getMaxHeartBeatInterval();
				BoundZSetOperations<String, String> zsetOp = redisTemplate.boundZSetOps(Caches.PXRETAILER_ONLINE);
				Set<String> terminals = zsetOp.rangeByScore(0, maxLastAccessTime);

				if (!terminals.isEmpty()) {
					Set<String> terminalCacheKeys = new HashSet<>(terminals.size());
					for (String str : terminals) {
						terminalCacheKeys.add(Caches.getTerminalCacheKey(str));
					}
					redisTemplate.delete(terminalCacheKeys);
					zsetOp.removeRangeByScore(0, maxLastAccessTime);
					terminalService.setTerminalOffline(terminals, maxLastAccessTime);
				}

				logger.debug("finish to update pxretailer terminal online status");
			} catch (Exception e) {
				logger.error("failed to update pxretailer terminal online status {}", e.getMessage());
			}
		}

		private void checkOfflineTerminal() {
			try {
				logger.debug("start to check offline terminal");

				boolean checkAllTerminal = true;
				ValueOperations<String, String> valueOp = redisTemplate.opsForValue();
				String checkTime = valueOp.get(Caches.TERMINAL_ONLINE_CHECK);
				if ("-1".equals(checkTime)) {
					checkAllTerminal = false;
				}

				if (checkAllTerminal) {
					String result = redisTemplate.execute(new RedisCallback<String>() {
						@Override
						public String doInRedis(RedisConnection connection) throws DataAccessException {
							byte[] result = (byte[]) connection.execute("SET",
									Caches.TERMINAL_ONLINE_CHECK_ALL.getBytes(), "1".getBytes(), "PX".getBytes(),
									String.valueOf(config.getCheckOfflineTerminalInterval()).getBytes(),
									"NX".getBytes());
							return result == null ? null : new String(result);
						}
					});
					if (!"OK".equalsIgnoreCase(result)) {
						checkAllTerminal = false;
					}
				}

				if (checkAllTerminal) {
					long cur = System.currentTimeMillis();
					long maxLastAccessTime = cur - config.getMaxHeartBeatInterval();
					terminalService.setTerminalOffline(maxLastAccessTime);
					if (checkTime == null) {
						valueOp.setIfAbsent(Caches.TERMINAL_ONLINE_CHECK, String.valueOf(cur));
					} else if (maxLastAccessTime > Long.parseLong(checkTime)) {
						valueOp.set(Caches.TERMINAL_ONLINE_CHECK, "-1");
					}
				}

				logger.debug("finish to check offline terminal");
			} catch (Exception e) {
				logger.error("failed to check offline terminal: {}", e.getMessage());
			}
		}

		private void updateTerminalAccessTime() {
			try {
				logger.debug("start to update pxretailer terminal access time");
				if (redisTemplate.hasKey(Caches.PXRETAILER_ACCESS_TIME)) {
					redisTemplate.rename(Caches.PXRETAILER_ACCESS_TIME, Caches.PXRETAILER_ACCESS_TIME_OLD);
					Map<String, String> map = redisTemplate
							.<String, String>boundHashOps(Caches.PXRETAILER_ACCESS_TIME_OLD).entries();
					redisTemplate.delete(Caches.PXRETAILER_ACCESS_TIME_OLD);
					terminalService.updateTerminalAccessTime(map);
				}
				logger.debug("finish to update pxretailer terminal access time");
			} catch (Exception e) {
				logger.error("failed to update pxretailer terminal access time: {}", e.getMessage());
			}
		}
	}
}
