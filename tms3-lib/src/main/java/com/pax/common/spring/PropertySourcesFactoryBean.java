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
package com.pax.common.spring;

import java.io.IOException;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.boot.env.PropertySourcesLoader;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.PropertySources;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

//load application.yml file
public class PropertySourcesFactoryBean implements FactoryBean<PropertySources> {

	private ResourceLoader resourceLoader = new DefaultResourceLoader();

	private Environment environment = new StandardEnvironment();

	private PropertySources propertySources;

	private boolean merge = true;

	private String[] locations;

	@Override
	public PropertySources getObject() throws Exception {
		return loadPropertySources(locations, merge);
	}

	@Override
	public Class<?> getObjectType() {
		return PropertySources.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	private PropertySources loadPropertySources(String[] locations, boolean mergeDefaultSources) {
		try {
			PropertySourcesLoader loader = new PropertySourcesLoader();
			for (String location : locations) {
				Resource resource = this.resourceLoader.getResource(this.environment.resolvePlaceholders(location));
				String[] profiles = this.environment.getActiveProfiles();
				for (int i = profiles.length; i-- > 0;) {
					String profile = profiles[i];
					loader.load(resource, profile);
				}
				loader.load(resource);
			}
			MutablePropertySources loaded = loader.getPropertySources();
			if (mergeDefaultSources && propertySources != null) {
				for (PropertySource<?> propertySource : this.propertySources) {
					loaded.addLast(propertySource);
				}
			}
			return loaded;
		} catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	}

	public ResourceLoader getResourceLoader() {
		return resourceLoader;
	}

	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	public Environment getEnvironment() {
		return environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	public PropertySources getPropertySources() {
		return propertySources;
	}

	public void setPropertySources(PropertySources propertySources) {
		this.propertySources = propertySources;
	}

	public boolean isMerge() {
		return merge;
	}

	public void setMerge(boolean merge) {
		this.merge = merge;
	}

	public String[] getLocations() {
		return locations;
	}

	public void setLocations(String[] locations) {
		this.locations = locations;
	}

}
