package com.pax.tms.web;

import java.util.Set;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.StringUtils;

public class RedisCache implements Cache {

	private static final Logger LOGGER = LoggerFactory.getLogger(RedisCache.class);

	private static String DASHBOARD_KEY_PREFIX = "dashboard";

	private RedisTemplate<String, Object> redisTemplate;
	private String name;
	private long timeout = 30;
	private int dbIndex = 3;

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public Object getNativeCache() {
		return null;
	}

	@Override
	public ValueWrapper get(Object key) {
		LOGGER.debug("===ValueWrapper get (key: {}) ===", key);
		if (StringUtils.isEmpty(key)) {
			return null;
		}

		final String finalKey;
		if (key instanceof String) {
			finalKey = (String) key;
		} else {
			finalKey = key.toString();
		}
		Object object = redisTemplate.execute(new RedisCallback<Object>() {
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				connection.select(dbIndex);
				byte[] bkey = redisTemplate.getStringSerializer().serialize(finalKey);
				byte[] value = connection.get(bkey);
				if (value == null) {
					return null;
				}
				@SuppressWarnings("rawtypes")
				RedisSerializer serializer = redisTemplate.getValueSerializer();
				return serializer.deserialize(value);
			}
		});
		return (object != null ? new SimpleValueWrapper(object) : null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(Object key, Class<T> type) {
		if (StringUtils.isEmpty(key) || null == type) {
			return null;
		}
		final String finalKey;
		final Class<T> finalType = type;
		if (key instanceof String) {
			finalKey = (String) key;
		} else {
			finalKey = key.toString();
		}
		final Object object = redisTemplate.execute(new RedisCallback<Object>() {
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				connection.select(dbIndex);
				byte[] bKey = redisTemplate.getStringSerializer().serialize(finalKey);
				byte[] value = connection.get(bKey);
				if (value == null) {
					return null;
				}
				@SuppressWarnings("rawtypes")
				RedisSerializer serializer = redisTemplate.getValueSerializer();
				return serializer.deserialize(value);
			}
		});
		if (object != null && finalType != null && finalType.isInstance(object)) {
			return (T) object;
		} else {
			return null;
		}
	}

	@Override
	public <T> T get(Object key, Callable<T> valueLoader) {
		return null;
	}

	@Override
	public void put(Object key, Object value) {
		if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value)) {
			return;
		}
		final String finalKey;
		if (key instanceof String) {
			finalKey = (String) key;
		} else {
			finalKey = key.toString();
		}
		Boolean result = false;
		if (!StringUtils.isEmpty(finalKey)) {
			final Object finalValue = value;
			result = redisTemplate.execute(new RedisCallback<Boolean>() {
				@SuppressWarnings({ "rawtypes", "unchecked" })
				@Override
				public Boolean doInRedis(RedisConnection connection) {
					RedisSerializer serializer = redisTemplate.getValueSerializer();
					byte[] bkey = redisTemplate.getStringSerializer().serialize(finalKey);
					byte[] bValue = serializer.serialize(finalValue);
					connection.select(dbIndex);
					connection.setEx(bkey, timeout, bValue);
					return true;
				}
			});
		}
		LOGGER.debug("===put (key:{} - result:{})===", key, result);

	}

	@Override
	public ValueWrapper putIfAbsent(Object key, Object value) {
		return null;
	}

	@Override
	public void evict(Object key) {
		LOGGER.debug("===evict (key:{})===", key);
		if (StringUtils.isEmpty(key)) {
			return;
		}
		String keyStr;
		if (key instanceof String) {
			keyStr = (String) key;
		} else {
			keyStr = key.toString();
		}
		/*
		 * pattern: dashboard_1/enterpriseId
		 */
		if (keyStr.startsWith(DASHBOARD_KEY_PREFIX)) {
			if (keyStr.indexOf("/") == keyStr.lastIndexOf("/")) {
				keyStr = keyStr + "*";
			} else {
				keyStr = keyStr.substring(0, keyStr.indexOf("/", keyStr.indexOf("/") + 1)) + "*";
			}
		}
		final String pattern = keyStr;
		redisTemplate.execute(new RedisCallback<Object>() {
			public Long doInRedis(RedisConnection connection) throws DataAccessException {
				connection.select(dbIndex);
				Set<byte[]> keySet = connection.keys(pattern.getBytes());

				byte[][] bb = keySet.toArray(new byte[][] {});

				return connection.del(bb);
			}
		});
	}

	@Override
	public void clear() {
		LOGGER.debug("===clear()===");
		redisTemplate.execute(new RedisCallback<Object>() {
			public String doInRedis(RedisConnection connection) throws DataAccessException {
				connection.select(dbIndex);
				connection.flushDb();
				return "ok";
			}
		});
	}

	public RedisTemplate<String, Object> getRedisTemplate() {
		return redisTemplate;
	}

	public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public int getDbIndex() {
		return dbIndex;
	}

	public void setDbIndex(int dbIndex) {
		this.dbIndex = dbIndex;
	}

}
