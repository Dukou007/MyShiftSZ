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
import com.pax.tms.location.dao.ProvinceDao;
import com.pax.tms.location.model.Country;
import com.pax.tms.location.model.Province;
import com.pax.tms.location.web.form.AddProvinceForm;
import com.pax.tms.location.web.form.EditProvinceForm;
import com.pax.tms.location.web.form.QueryProvinceForm;

@Service("provinceServiceImpl")
public class ProvinceServiceImpl extends BaseService<Province, Long> implements ProvinceService {

	@Autowired
	private ProvinceDao provinceDao;

	@Autowired
	private CountryService countryService;

	@Override
	public IBaseDao<Province, Long> getBaseDao() {
		return provinceDao;
	}

	@Override
	public Province getProvinceByName(long countryId, String name) {
		return provinceDao.getProvinceByName(countryId, name);
	}

	@Override
	public Province getProvinceByCode(long countryId, String code) {
		return provinceDao.getProvinceByCode(countryId, code);
	}

	@Override
	public List<Province> getProvinceList(long countryId) {
		QueryProvinceForm form = new QueryProvinceForm();
		form.setCountryId(countryId);
		form.setOrderField("name");
		form.setOrderAsc();
		return provinceDao.list(form);
	}

	@Override
	public void delete(Long[] idList) {
		if (ArrayUtils.isEmpty(idList)) {
			return;
		}

		for (Long stateId : idList) {
			Province state = null;
			if (stateId != null) {
				state = get(stateId);
			}

			if (state == null) {
				continue;
			}
			delete(stateId);
		}
	}

	@Override
	public void add(AddProvinceForm command) {
		// check input
		if (command.getCountryId() == null) {
			throw new BusinessException("state.country.required");
		}
		if (StringUtils.isEmpty(command.getName())) {
			throw new BusinessException("state.name.required");
		}
		if (StringUtils.isEmpty(command.getCode())) {
			throw new BusinessException("state.code.required");
		}

		// verify country exists
		Country cty = countryService.get(command.getCountryId());
		if (cty == null) {
			throw new BusinessException("country.notFound");
		}

		// verify name is unique
		if (this.getProvinceByName(command.getCountryId(), command.getName()) != null) {
			throw new BusinessException("state.name.alreadyExists");
		}

		// verify code is unique
		if (this.getProvinceByCode(command.getCountryId(), command.getCode()) != null) {
			throw new BusinessException("state.code.alreadyExists");
		}

		// add new state
		Province s = new Province();
		s.setCountry(cty);
		s.setName(command.getName());
		s.setCode(command.getCode());
		s.setAbbrName(command.getAbbrName());
		s.setDescription(command.getDescription());

		s.setCreator(command.getLoginUsername());
		s.setCreateDate(command.getRequestTime());
		s.setModifier(command.getLoginUsername());
		s.setModifyDate(command.getRequestTime());

		save(s);
	}

	@Override
	public void edit(long id, EditProvinceForm command) {
		// check input
		if (command.getCountryId() == null) {
			throw new BusinessException("state.country.required");
		}
		if (StringUtils.isEmpty(command.getName())) {
			throw new BusinessException("state.name.required");
		}
		if (StringUtils.isEmpty(command.getCode())) {
			throw new BusinessException("state.code.required");
		}

		// verify state exists
		Province s = get(id);
		if (s == null) {
			throw new BusinessException("state.notFound");
		}

		if (!command.getCountryId().equals(s.getCountry().getId())) {
			// verify country exists
			Country cty = countryService.get(command.getCountryId());
			if (cty == null) {
				throw new BusinessException("country.notFound");
			}

			s.setCountry(cty);
		}

		if (!StringUtils.equals(command.getName(), s.getName())) {
			// verify name is unique
			if (this.getProvinceByName(command.getCountryId(), command.getName()) != null) {
				throw new BusinessException("state.name.alreadyExists");
			}

			s.setName(command.getName());
		}

		if (!StringUtils.equals(command.getCode(), s.getCode())) {
			// verify code is unique
			if (this.getProvinceByCode(command.getCountryId(), command.getCode()) != null) {
				throw new BusinessException("state.code.alreadyExists");
			}

			s.setCode(command.getCode());
		}

		// save state
		s.setAbbrName(command.getAbbrName());
		s.setDescription(command.getDescription());

		s.setModifier(command.getLoginUsername());
		s.setModifyDate(command.getRequestTime());

		update(s);

	}

	@Override
	public void onDeleteCountry(Country country) {
		QueryProvinceForm form = new QueryProvinceForm();
		form.setCountryId(country.getId());
		if (count(form) > 0) {
			throw new BusinessException("country.delete.msg1");
		}
	}

	@Override
	public Province getProvinceByImportName(Long countryId, String provinceName) {
		return provinceDao.getProvinceByImportName(countryId, provinceName);
	}

}
