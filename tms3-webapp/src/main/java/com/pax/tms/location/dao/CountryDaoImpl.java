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
package com.pax.tms.location.dao;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.common.dao.hibernate.CriteriaWrapper;
import com.pax.tms.location.model.Country;
import com.pax.tms.location.web.form.QueryCountryForm;

@Repository("countryDaoImpl")
public class CountryDaoImpl extends BaseHibernateDao<Country, Long> implements CountryDao {
	private String defaultCountry;

	@Override
	public List<Country> list() {
		return list("name", true);
	}

	@Override
	public Country getCountryByName(String name) {
		String hql = "from Country cty where cty.name=:name";
		return (Country) this.getSession().createQuery(hql).setParameter("name", name).uniqueResult();
	}

	@Override
	public Country getCountryByCode(String countryCode) {
		String hql = "from Country cty where cty.code=:code";
		return (Country) this.getSession().createQuery(hql).setParameter("code", countryCode).getSingleResult();
	}

	@Override
	public <S extends Serializable> CriteriaWrapper getCriteria(S command, boolean ordered) {
		QueryCountryForm form = (QueryCountryForm) command;

		CriteriaWrapper wrapper = createCriteriaWrapper(Country.class);
		wrapper.like("code", form.getCode());
		wrapper.like("name", form.getName());

		if (ordered) {
			wrapper.addOrder(form, "modifyDate", ORDER_DESC);
			wrapper.addOrder("id", ORDER_ASC);
		}

		return wrapper;
	}

	@Override
	public List<Country> getCounryList() {
		String hql = "from Country cty order by cty.name asc";
		List<Country> list = createQuery(hql, Country.class).getResultList();
		if (StringUtils.isBlank(defaultCountry)) {
			return list;
		}
		for (int i = 0; i < list.size(); i++) {
			Country c = list.get(i);
			if (defaultCountry.equalsIgnoreCase(c.getName())) {
				if (i == 0) {
					return list;
				}
				list.remove(i);
				list.add(0, c);
				return list;
			}
		}
		return list;
	}

	@Override
	public Country getCountryByImportName(String countryName) {
		String hql = "from Country cty where upper(cty.name) = ?";
		return uniqueResult(hql, countryName.toUpperCase());
	}

	public String getDefaultCountry() {
		return defaultCountry;
	}

	@Autowired
	public void setDefaultCountry(@Value("${tms.defaultCountryName:}") String defaultCountry) {
		this.defaultCountry = StringUtils.trim(defaultCountry);
	}

}
