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
package com.pax.tms.user.security;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapEntry;
import org.ldaptive.LdapException;
import org.ldaptive.ReturnAttributes;
import org.ldaptive.SearchExecutor;
import org.ldaptive.SearchFilter;
import org.ldaptive.SearchResult;
import org.ldaptive.pool.PooledConnectionFactory;

import com.pax.tms.user.web.form.UserForm;

public class LdapSearchExecutor {
	
	private SearchExecutor searchExecutor;

	private PooledConnectionFactory pooledConnectionFactory;

	private String userFilter;

	protected Map<String, String> attributeMap = Collections.emptyMap();

	private String[] attributes = ReturnAttributes.NONE.value();

	/**
	 * Initialize the handler, setup the authentication entry attributes.
	 */
	@PostConstruct
	public void initialize() {
		/**
		 * Use a set to ensure we ignore duplicates.
		 */
		final Set<String> attributeSet = new HashSet<>();
		if (!this.attributeMap.isEmpty()) {
			final Set<String> attrs = this.attributeMap.keySet();
			attributeSet.addAll(attrs);
		}

		if (!attributeSet.isEmpty()) {
			this.attributes = attributeSet.toArray(new String[attributeSet.size()]);
		}
	}

	public Map<String, String> searchUser(String username) throws LdapException {
		SearchFilter searchFilter = new SearchFilter(userFilter);
		searchFilter.setParameter("user", username);
		SearchResult result = searchExecutor.search(pooledConnectionFactory, searchFilter, attributes).getResult();

		LdapEntry ldapEntry = result.getEntry();

		return ldapEntry == null ? null : createAttributeMap(ldapEntry);
	}

	protected Map<String, String> createAttributeMap(LdapEntry ldapEntry) {
		Map<String, String> attrs = new LinkedHashMap<>(this.attributeMap.size());
		for (Map.Entry<String, String> ldapAttr : this.attributeMap.entrySet()) {
			LdapAttribute attr = ldapEntry.getAttribute(ldapAttr.getKey());
			if (attr != null) {
				String principalAttrName = ldapAttr.getValue();
				if (attr.size() > 1) {
					attrs.put(principalAttrName, StringUtils.join(attr.getStringValues(), ","));
				} else {
					attrs.put(principalAttrName, attr.getStringValue());
				}
			}
		}
		return attrs;
	}

	public UserForm getUserInfo(String ldapName) throws LdapException {
		Map<String, String> attrs = searchUser(ldapName);
		if (attrs == null) {
			return null;
		}
		UserForm userForm = new UserForm();
		userForm.setUsername(attrs.get("username"));
		userForm.setFullname(attrs.get("fullname"));
		userForm.setEmail(attrs.get("email"));
		userForm.setPhone(attrs.get("phone"));
		userForm.setCountryName(attrs.get("countryName"));
		userForm.setProvinceName(attrs.get("provinceName"));
		userForm.setCityName(attrs.get("cityName"));
		userForm.setZipCode(attrs.get("zipCode"));
		userForm.setAddress(attrs.get("address"));
		return userForm;
	}

	public SearchExecutor getSearchExecutor() {
		return searchExecutor;
	}

	public void setSearchExecutor(SearchExecutor searchExecutor) {
		this.searchExecutor = searchExecutor;
	}

	public PooledConnectionFactory getPooledConnectionFactory() {
		return pooledConnectionFactory;
	}

	public void setPooledConnectionFactory(PooledConnectionFactory pooledConnectionFactory) {
		this.pooledConnectionFactory = pooledConnectionFactory;
	}

	public String getUserFilter() {
		return userFilter;
	}

	public void setUserFilter(String userFilter) {
		this.userFilter = userFilter;
	}

	public Map<String, String> getAttributeMap() {
		return attributeMap;
	}

	public void setAttributeMap(Map<String, String> attributeMap) {
		this.attributeMap = attributeMap;
	}

}
