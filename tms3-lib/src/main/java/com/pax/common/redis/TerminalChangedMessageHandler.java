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
package com.pax.common.redis;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class TerminalChangedMessageHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(TerminalChangedMessageHandler.class);

	private RedisTemplate<String, String> redisTemplate;

	private int timeout = 30;
	private boolean stop;

	public void start() {
		Thread thread = new Thread() {
			@Override
			public void run() {
				while (!stop && !this.isInterrupted()) {
					try {
						BoundListOperations<String, String> listOp = redisTemplate
								.boundListOps(Caches.TERMINAL_CHANGED_MESSAGE_QUEUE);
						while (!stop && !this.isInterrupted()) {
							String message = listOp.rightPop(timeout, TimeUnit.SECONDS);
							if (message != null) {
								handleMessage(message);
							}
						}
					} catch (Exception e) {
						LOGGER.error("Failed to proccess terminal changed message", e);
					}
					delay();
				}

				LOGGER.warn("Stop to proccess terminal changed message");
			}
		};
		thread.setDaemon(true);
		thread.start();
	}

	private void delay() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			LOGGER.warn("Interrupted!", e);
			Thread.currentThread().interrupt();
		}
	}

	public void stop() {
		this.stop = true;
	}

	// tms:topic:terminalStatusChanged
	public void handleMessage(String message) {
		JsonArray jsonArray = new JsonArray((String) message);

		Set<String> terminals = new HashSet<>(jsonArray.size());
		Iterator<Object> it = jsonArray.iterator();
		while (it.hasNext()) {
			terminals.add(Caches.getTerminalCacheKey(((JsonObject) it.next()).getString("tsn")));
		}
		redisTemplate.delete(terminals);
	}

	@Autowired
	@Qualifier("redisTemplate")
	public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public void setTimeout(@Value("${tms.getTerminalChangedMessageTimeout:30}") int timeout) {
		this.timeout = timeout;
	}

}
