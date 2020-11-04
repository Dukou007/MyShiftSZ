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
package com.pax.tms.location.service;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pax.common.dao.IBaseDao;
import com.pax.common.exception.BusinessException;
import com.pax.common.service.impl.BaseService;
import com.pax.tms.location.dao.CountryDao;
import com.pax.tms.location.model.Country;
import com.pax.tms.location.web.form.AddCountryForm;
import com.pax.tms.location.web.form.EditCountryForm;

@Service("countryServiceImpl")
public class CountryServiceImpl extends BaseService<Country, Long> implements CountryService {

	@Autowired
	private CountryDao countryDao;

	@Autowired
	private ProvinceService stateService;

	@Override
	public IBaseDao<Country, Long> getBaseDao() {
		return countryDao;
	}

	@Override
	public Country getCountryByName(String countryName) {
		return countryDao.getCountryByName(countryName);
	}

	@Override
	public Country getCountryByCode(String countryCode) {
		return countryDao.getCountryByCode(countryCode);
	}

	@Override
	public void delete(Long[] idList) {
		if (ArrayUtils.isEmpty(idList)) {
			return;
		}

		for (Long id : idList) {
			if (id == null) {
				continue;
			}
			Country country = get(id);
			if (country == null) {
				continue;
			}

			stateService.onDeleteCountry(country);

			delete(id);
		}
	}

	@Override
	public void add(AddCountryForm command) {
		// check input
		if (StringUtils.isEmpty(command.getName())) {
			throw new BusinessException("country.name.required");
		}
		if (StringUtils.isEmpty(command.getCode())) {
			throw new BusinessException("country.code.required");
		}

		// check whether country name already exists
		if (this.getCountryByName(command.getName()) != null) {
			throw new BusinessException("country.name.alreadyExists");
		}

		// check whether country code already exists
		if (this.getCountryByCode(command.getCode()) != null) {
			throw new BusinessException("country.code.alreadyExists");
		}

		// add new country
		Country country = new Country();
		country.setName(command.getName());
		country.setCode(command.getCode());
		country.setAbbrName(command.getAbbrName());
		country.setDescription(command.getDescription());

		country.setTransCurrencyCode(command.getTransCurrencyCode());
		country.setTransCurrentExp(command.getTransCurrentExp());
		country.setTransReferCurrencyCode(command.getTransReferCurrencyCode());
		country.setTransReferCurrencyExp(command.getTransReferCurrencyExp());

		country.setCreator(command.getLoginUsername());
		country.setCreateDate(command.getRequestTime());
		country.setModifier(command.getLoginUsername());
		country.setModifyDate(command.getRequestTime());

		save(country);
	}

	@Override
	public void edit(long id, EditCountryForm command) {
		// check input
		if (StringUtils.isEmpty(command.getName())) {
			throw new BusinessException("country.name.required");
		}
		if (StringUtils.isEmpty(command.getCode())) {
			throw new BusinessException("country.code.required");
		}

		Country cty = get(id);
		if (cty == null) {
			throw new BusinessException("country.notFound");
		}

		cty.setAbbrName(command.getAbbrName());
		cty.setDescription(command.getDescription());

		// check whether country name already exists
		if (!StringUtils.equals(command.getName(), cty.getName())) {
			if (this.getCountryByName(command.getName()) != null) {
				throw new BusinessException("country.name.alreadyExists");
			}

			cty.setName(command.getName());
		}

		// check whether country code already exists
		if (!StringUtils.equals(command.getCode(), cty.getCode())) {
			if (this.getCountryByCode(command.getCode()) != null) {
				throw new BusinessException("country.code.alreadyExists");
			}

			cty.setCode(command.getCode());
		}

		cty.setTransCurrencyCode(command.getTransCurrencyCode());
		cty.setTransCurrentExp(command.getTransCurrentExp());
		cty.setTransReferCurrencyCode(command.getTransReferCurrencyCode());
		cty.setTransReferCurrencyExp(command.getTransReferCurrencyExp());

		cty.setModifier(command.getLoginUsername());
		cty.setModifyDate(command.getRequestTime());

		update(cty);
	}

	@Override
	public List<Country> getCounryList() {
		return countryDao.getCounryList();
	}

	@Override
	public Country getCountryByImportName(String countryName) {
		return countryDao.getCountryByImportName(countryName);
	}

}
