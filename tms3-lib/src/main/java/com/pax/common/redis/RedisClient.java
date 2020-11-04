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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.HyperLogLogOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;

/**
 * Example: <code>
  	public void addLink(String userId, URL url) {
		listOps.leftPush(userId, url.toExternalForm());
		// or use template directly
		redisTemplate.boundListOps(userId).leftPush(url.toExternalForm());
	}
  </code>
 * 
 * @author Elliott.Z
 *
 */
@SuppressWarnings("rawtypes")
public class RedisClient {
	// inject the actual template
	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	/*
	 * Operational views inject the template as ListOperations can also inject
	 * as Value, Set, ZSet, and HashOperations
	 */
	// Redis list operations
	// @Resource(name = "redisTemplate")
	private ListOperations<String, String> listOps;

	// Redis string (or value) operations
	// @Resource(name = "redisTemplate")
	private ValueOperations valueOps;

	// Redis set operations
	// @Resource(name = "redisTemplate")
	private SetOperations setOps;

	// Redis zset (or sorted set) operations
	// @Resource(name = "redisTemplate")
	private ZSetOperations zsetOps;

	// Redis hash operations
	// @Resource(name = "redisTemplate")
	private HashOperations hashOps;

	// Redis HyperLogLog operations like (pfadd, pfcount,…​)
	// @Resource(name = "redisTemplate")
	private HyperLogLogOperations hllOps;

	// Redis string (or value) key bound operations

	// @Resource(name = "redisTemplate")
	private BoundValueOperations bvalueOps;

	// Redis list key bound operations
	// @Resource(name = "redisTemplate")
	private BoundListOperations blistOps;

	// Redis set key bound operations
	// @Resource(name = "redisTemplate")
	private BoundSetOperations bsetOps;

	// Redis zset (or sorted set) key bound operations
	// @Resource(name = "redisTemplate")
	private BoundZSetOperations bzsetOps;

	// Redis hash key bound operations
	// @Resource(name = "redisTemplate")
	private BoundHashOperations bhashOps;

	/**
	 * Example:<code>
	redisTemplate.execute(new RedisCallback<Object>() {
		public Object doInRedis(RedisConnection connection) throws DataAccessException {
	  		Long size = connection.dbSize();
	  		// Can cast to StringRedisConnection if using a StringRedisTemplate
	  		((StringRedisConnection)connection).set("key", "value");
		}
	});
	 </code>
	 * 
	 * @param action
	 * @return
	 */
	public <T> T execute(RedisCallback<T> action) {
		return redisTemplate.execute(action);
	}

	public <T> List<Object> executePipelined(RedisCallback<T> action) {
		return redisTemplate.executePipelined(action);
	}

	/**
	 * Example:<code>
	//execute a transaction<code>
	List<Object> txResults = redisTemplate.execute(new SessionCallback<List<Object>>() {
	public List<Object> execute(RedisOperations operations) throws DataAccessException {
	operations.multi();
	operations.opsForSet().add("key", "value1");
	
	// This will contain the results of all ops in the transaction
	return operations.exec();
	}
	});
	System.out.println("Number of items added to set: " + txResults.get(0));
	 </code>
	 * 
	 * @param action
	 * @return
	 */
	public <T> T executeTransaction(SessionCallback<T> session) {
		return redisTemplate.execute(session);
	}

	public void sendMessage(String channel, Object message) {
		redisTemplate.convertAndSend(channel, message);
	}

	public RedisTemplate<String, String> getRedisTemplate() {
		return redisTemplate;
	}

	public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public ListOperations<String, String> getListOps() {
		return listOps;
	}

	public void setListOps(ListOperations<String, String> listOps) {
		this.listOps = listOps;
	}

	public ValueOperations getValueOps() {
		return valueOps;
	}

	public void setValueOps(ValueOperations valueOps) {
		this.valueOps = valueOps;
	}

	public SetOperations getSetOps() {
		return setOps;
	}

	public void setSetOps(SetOperations setOps) {
		this.setOps = setOps;
	}

	public ZSetOperations getZsetOps() {
		return zsetOps;
	}

	public void setZsetOps(ZSetOperations zsetOps) {
		this.zsetOps = zsetOps;
	}

	public HashOperations getHashOps() {
		return hashOps;
	}

	public void setHashOps(HashOperations hashOps) {
		this.hashOps = hashOps;
	}

	public HyperLogLogOperations getHllOps() {
		return hllOps;
	}

	public void setHllOps(HyperLogLogOperations hllOps) {
		this.hllOps = hllOps;
	}

	public BoundValueOperations getBvalueOps() {
		return bvalueOps;
	}

	public void setBvalueOps(BoundValueOperations bvalueOps) {
		this.bvalueOps = bvalueOps;
	}

	public BoundListOperations getBlistOps() {
		return blistOps;
	}

	public void setBlistOps(BoundListOperations blistOps) {
		this.blistOps = blistOps;
	}

	public BoundSetOperations getBsetOps() {
		return bsetOps;
	}

	public void setBsetOps(BoundSetOperations bsetOps) {
		this.bsetOps = bsetOps;
	}

	public BoundZSetOperations getBzsetOps() {
		return bzsetOps;
	}

	public void setBzsetOps(BoundZSetOperations bzsetOps) {
		this.bzsetOps = bzsetOps;
	}

	public BoundHashOperations getBhashOps() {
		return bhashOps;
	}

	public void setBhashOps(BoundHashOperations bhashOps) {
		this.bhashOps = bhashOps;
	}

}
