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
package com.pax.common.dao.hibernate;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.Configuration;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.spi.CachingProvider;

import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.jcache.JCacheCollectionRegion;
import org.hibernate.cache.jcache.JCacheEntityRegion;
import org.hibernate.cache.jcache.JCacheMessageLogger;
import org.hibernate.cache.jcache.JCacheNaturalIdRegion;
import org.hibernate.cache.jcache.JCacheQueryResultsRegion;
import org.hibernate.cache.jcache.JCacheTimestampsRegion;
import org.hibernate.cache.jcache.time.Timestamper;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.CollectionRegion;
import org.hibernate.cache.spi.EntityRegion;
import org.hibernate.cache.spi.NaturalIdRegion;
import org.hibernate.cache.spi.QueryResultsRegion;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.cache.spi.TimestampsRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.jboss.logging.Logger;

// used by hibernate ehcache
public class JCacheRegionFactory implements RegionFactory {

	private static final long serialVersionUID = -8353963385207704695L;

	private static final String PROP_PREFIX = "hibernate.javax.cache";
	public static final String PROVIDER = PROP_PREFIX + ".provider";
	public static final String CONFIG_URI = PROP_PREFIX + ".uri";

	private static final String URL_EXCEPTION = "Couldn't create URI from ";

	private static final JCacheMessageLogger LOG = Logger.getMessageLogger(JCacheMessageLogger.class,
			JCacheRegionFactory.class.getName());

	private final AtomicBoolean started = new AtomicBoolean(false);
	private transient volatile CacheManager cacheManager;
	private transient SessionFactoryOptions options;

	@Override
	public void start(final SessionFactoryOptions options, final Properties properties) {
		if (started.compareAndSet(false, true)) {
			synchronized (this) {
				doStart(options, properties);
			}
		} else {
			LOG.attemptToRestartAlreadyStartedJCacheProvider();
		}
	}

	private void doStart(final SessionFactoryOptions options, final Properties properties) {
		this.options = options;
		try {
			final CachingProvider cachingProvider;
			final String provider = getProp(properties, PROVIDER);
			if (provider != null) {
				cachingProvider = Caching.getCachingProvider(provider);
			} else {
				cachingProvider = Caching.getCachingProvider();
			}
			final CacheManager cm;
			final String cacheManagerUri = getProp(properties, CONFIG_URI);
			if (cacheManagerUri != null) {
				cm = getCacheManger(cachingProvider, cacheManagerUri);
			} else {
				cm = cachingProvider.getCacheManager();
			}
			this.cacheManager = cm;
		} finally {
			if (this.cacheManager == null) {
				started.set(false);
			}
		}
	}

	private CacheManager getCacheManger(CachingProvider cachingProvider, String cacheManagerUri) {
		URI uri;
		try {
			URL url = new URL(cacheManagerUri);
			uri = url.toURI();
		} catch (MalformedURLException e) {
			try {
				uri = loadResource(cacheManagerUri);
			} catch (URISyntaxException e2) {
				throw new CacheException(URL_EXCEPTION + cacheManagerUri, e2);
			}
			if (uri == null) {
				throw new CacheException(URL_EXCEPTION + cacheManagerUri, e);
			}
		} catch (URISyntaxException e) {
			throw new CacheException(URL_EXCEPTION + cacheManagerUri, e);
		}

		return cachingProvider.getCacheManager(uri, cachingProvider.getDefaultClassLoader());
	}

	/**
	 * Load a resource from the classpath.
	 * 
	 * @throws URISyntaxException
	 */
	protected static URI loadResource(String configurationResourceName) throws URISyntaxException {
		ClassLoader standardClassloader = getStandardClassLoader();
		URL url = null;
		if (standardClassloader != null) {
			url = standardClassloader.getResource(configurationResourceName);
		}
		if (url == null) {
			url = JCacheRegionFactory.class.getResource(configurationResourceName);
		}

		LOG.debugf("Creating JCacheRegionFactory from a specified resource: {}.  Resolved to URL: {}",
				configurationResourceName, url);
		if (url == null) {
			LOG.warnf("A cacheManagerUri was set to {} but the resource could not be loaded from the classpath.",
					configurationResourceName);
			return null;
		}
		return url.toURI();
	}

	public static ClassLoader getStandardClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	@Override
	public void stop() {
		if (started.compareAndSet(true, false)) {
			synchronized (this) {
				cacheManager.close();
				cacheManager = null;
			}
		} else {
			LOG.attemptToRestopAlreadyStoppedJCacheProvider();
		}
	}

	@Override
	public boolean isMinimalPutsEnabledByDefault() {
		return true;
	}

	@Override
	public AccessType getDefaultAccessType() {
		return AccessType.READ_WRITE;
	}

	@Override
	public long nextTimestamp() {
		return nextTS();
	}

	@Override
	public EntityRegion buildEntityRegion(final String regionName, final Properties properties,
			final CacheDataDescription metadata) {
		final Cache<Object, Object> cache = getOrCreateCache(regionName, properties, metadata);
		return new JCacheEntityRegion(cache, metadata, options);
	}

	@Override
	public NaturalIdRegion buildNaturalIdRegion(final String regionName, final Properties properties,
			final CacheDataDescription metadata) {
		final Cache<Object, Object> cache = getOrCreateCache(regionName, properties, metadata);
		return new JCacheNaturalIdRegion(cache, metadata, options);
	}

	@Override
	public CollectionRegion buildCollectionRegion(final String regionName, final Properties properties,
			final CacheDataDescription metadata) {
		final Cache<Object, Object> cache = getOrCreateCache(regionName, properties, metadata);
		return new JCacheCollectionRegion(cache, metadata, options);
	}

	@Override
	public QueryResultsRegion buildQueryResultsRegion(final String regionName, final Properties properties) {
		final Cache<Object, Object> cache = getOrCreateCache(regionName, properties, null);
		return new JCacheQueryResultsRegion(cache);
	}

	@Override
	public TimestampsRegion buildTimestampsRegion(final String regionName, final Properties properties) {
		final Cache<Object, Object> cache = getOrCreateCache(regionName, properties, null);
		return new JCacheTimestampsRegion(cache);
	}

	boolean isStarted() {
		return started.get() && cacheManager != null;
	}

	protected Cache<Object, Object> getOrCreateCache(String regionName, Properties properties,
			CacheDataDescription metadata) {
		checkStatus();
		final Cache<Object, Object> cache = cacheManager.getCache(regionName);
		if (cache == null) {
			try {
				return cacheManager.createCache(regionName, newDefaultConfig(properties, metadata));
			} catch (CacheException e) {
				final Cache<Object, Object> existing = cacheManager.getCache(regionName);
				if (existing != null) {
					return existing;
				}
				throw e;
			}
		}
		return cache;
	}

	protected Configuration<Object, Object> newDefaultConfig(Properties properties, CacheDataDescription metadata) {
		return new MutableConfiguration<>();
	}

	CacheManager getCacheManager() {
		return cacheManager;
	}

	static long nextTS() {
		return Timestamper.next();
	}

	static int timeOut() {
		return (int) TimeUnit.SECONDS.toMillis(60) * Timestamper.ONE_MS;
	}

	private String getProp(Properties properties, String prop) {
		return properties != null ? properties.getProperty(prop) : null;
	}

	private void checkStatus() {
		if (!isStarted()) {
			throw new IllegalStateException("JCacheRegionFactory not yet started!");
		}
	}

}
