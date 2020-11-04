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
package com.pax.tms.message;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("redisMessageSender")
@SuppressWarnings("rawtypes")
public class RedisMessageSender implements MessageSender {

	@Autowired(required = false)
	@Qualifier("noTransactionRedisTemplate")

	private RedisTemplate template;

	@Autowired(required = false)
	@Qualifier("transactionRedisTemplate")
	private RedisTemplate transactionTemplate;

	@Override
	public void publish(String topic, Object message) {
		template.convertAndSend(topic, message);
	}

	public RedisTemplate getTemplate() {
		return template;
	}

	public void setTemplate(RedisTemplate template) {
		this.template = template;
	}

	public RedisTemplate getTransactionTemplate() {
		return transactionTemplate;
	}

	public void setTransactionTemplate(RedisTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}

	@Transactional
	@Override
	public <T> void batchPublish(String topic, Collection<T> messages) {
		messages.forEach(message -> {
			template.convertAndSend(topic, message);
		});

	}

}
