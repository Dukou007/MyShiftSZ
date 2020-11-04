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
import com.pax.tms.location.dao.CityDao;
import com.pax.tms.location.model.City;
import com.pax.tms.location.model.Province;
import com.pax.tms.location.web.form.AddCityForm;
import com.pax.tms.location.web.form.EditCityForm;
import com.pax.tms.location.web.form.QueryCityForm;

@Service("cityServiceImpl")
public class CityServiceImpl extends BaseService<City, Long> implements CityService {

	@Autowired
	private CityDao cityDao;

	@Autowired
	private ProvinceService stateService;

	@Override
	public IBaseDao<City, Long> getBaseDao() {
		return cityDao;
	}

	@Override
	public void delete(Long[] ids) {
		if (ArrayUtils.isEmpty(ids)) {
			return;
		}

		for (Long id : ids) {
			if (id == null) {
				continue;
			}
			City city = get(id);
			if (city == null) {
				continue;
			}

			delete(id);
		}
	}

	@Override
	public boolean add(AddCityForm command) {
		// check input
		if (command.getProvinceId() == null) {
			throw new BusinessException("city.msg.stateRequired");
		}
		if (StringUtils.isEmpty(command.getName())) {
			throw new BusinessException("city.msg.nameRequired");
		}

		// verify state exists
		Province state = stateService.get(command.getProvinceId());
		if (state == null) {
			throw new BusinessException("state.notFound");
		}

		// verify name is unique
		if (cityDao.getCityByName(command.getProvinceId(), command.getName()) != null) {
			return false;
		}

		// add new city
		City city = new City();
		city.setProvince(state);
		city.setName(command.getName());
		city.setAbbrName(command.getAbbrName());
		city.setDescription(command.getDescription());
		city.setCreator(command.getLoginUsername());
		city.setCreateDate(command.getRequestTime());
		city.setModifier(command.getLoginUsername());
		city.setModifyDate(command.getRequestTime());
		save(city);
		cityDao.flush();

		return true;
	}

	@Override
	public void edit(long id, EditCityForm command) {
		// check input
		if (command.getProvinceId() == null) {
			throw new BusinessException("city.msg.stateRequired");
		}
		if (StringUtils.isEmpty(command.getName())) {
			throw new BusinessException("city.msg.nameRequired");
		}

		// verify state exists
		City city = get(id);
		if (city == null) {
			throw new BusinessException("city.msg.notFound");
		}

		if (!command.getProvinceId().equals(city.getProvince().getId())) {
			// verify state exists
			Province state = stateService.get(command.getProvinceId());
			if (state == null) {
				throw new BusinessException("state.notFound");
			}
			city.setProvince(state);
		}

		if (!StringUtils.equals(command.getName(), city.getName())) {
			// verify name is unique
			if (cityDao.getCityByName(command.getProvinceId(), command.getName()) != null) {
				throw new BusinessException("city.msg.nameAlreadyExists");
			}

			city.setName(command.getName());
		}

		// save city
		city.setAbbrName(command.getAbbrName());
		city.setDescription(command.getDescription());
		city.setModifier(command.getLoginUsername());
		city.setModifyDate(command.getRequestTime());
		update(city);
	}


	@Override
	public List<City> getCityList(long provinceId) {
		QueryCityForm form = new QueryCityForm();
		form.setProvinceId(provinceId);
		form.setOrderField("name");
		form.setOrderAsc();
        return cityDao.list(form);
	}
	
}
