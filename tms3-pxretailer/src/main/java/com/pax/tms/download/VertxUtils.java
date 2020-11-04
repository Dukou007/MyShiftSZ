package com.pax.tms.download;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import io.vertx.redis.RedisOptions;

public class VertxUtils {

	private VertxUtils() {
	}

	public static RedisOptions createRedisOptions(String password, String redisMaster, String redisSentinelNodes) {
		RedisOptions options = new RedisOptions();
		String pwd = password;
		if (pwd != null) {
			pwd = pwd.trim();
		}
		if (!StringUtils.isEmpty(pwd)) {
			options.setAuth(pwd);
		}

		if (redisSentinelNodes != null && !redisSentinelNodes.trim().isEmpty()) {
			options.setMasterName(redisMaster);
			List<String> list = new ArrayList<>();
			for (String node : redisSentinelNodes.split(",")) {
				if (!node.trim().isEmpty()) {
					list.add(node.trim());
				}
			}
			options.setSentinels(list);
		}
		options.setConnectTimeout(30000);
		options.setIdleTimeout(30);
		return options;
	}
}
