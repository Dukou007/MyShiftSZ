package com.pax.common.cache;

import java.net.URI;
import java.util.Properties;

import javax.cache.CacheManager;
import javax.cache.Caching;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

public class EhcacheJCacheManagerFactoryBean
		implements FactoryBean<CacheManager>, BeanClassLoaderAware, InitializingBean, DisposableBean {

	private URI cacheManagerUri;

	private Properties cacheManagerProperties;

	private ClassLoader beanClassLoader;

	private CacheManager cacheManager;

	/**
	 * Specify the URI for the desired CacheManager. Default is {@code null}
	 * (i.e. JCache's default).
	 */
	public void setCacheManagerUri(URI cacheManagerUri) {
		this.cacheManagerUri = cacheManagerUri;
	}

	/**
	 * Specify properties for the to-be-created CacheManager. Default is
	 * {@code null} (i.e. no special properties to apply).
	 * 
	 * @see javax.cache.spi.CachingProvider#getCacheManager(URI, ClassLoader,
	 *      Properties)
	 */
	public void setCacheManagerProperties(Properties cacheManagerProperties) {
		this.cacheManagerProperties = cacheManagerProperties;
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.beanClassLoader = classLoader;
	}

	@Override
	public void afterPropertiesSet() {
		this.cacheManager = Caching.getCachingProvider("org.ehcache.jsr107.EhcacheCachingProvider")
				.getCacheManager(this.cacheManagerUri, this.beanClassLoader, this.cacheManagerProperties);
	}

	@Override
	public CacheManager getObject() {
		return this.cacheManager;
	}

	@Override
	public Class<?> getObjectType() {
		return (this.cacheManager != null ? this.cacheManager.getClass() : CacheManager.class);
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public void destroy() {
		this.cacheManager.close();
	}

}