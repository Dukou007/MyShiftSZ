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

import org.springframework.stereotype.Repository;

import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.common.dao.hibernate.CriteriaWrapper;
import com.pax.tms.location.model.City;
import com.pax.tms.location.web.form.QueryCityForm;

@Repository("cityDaoImpl")
public class CityDaoImpl extends BaseHibernateDao<City, Long> implements CityDao {

	@Override
	public <S extends Serializable> CriteriaWrapper getCriteria(S command, boolean ordered) {
		QueryCityForm form = (QueryCityForm) command;

		CriteriaWrapper wrapper = createCriteriaWrapper(City.class);
		wrapper.like("name", form.getName());
		wrapper.eq("province.id", form.getProvinceId());

		if (ordered) {
			wrapper.addOrder(form, "modifyDate", ORDER_DESC, (orderField) -> {
				if ("state".equals(orderField)) {
					wrapper.createAlias("state", "state");
					return "state.name";
				}
				return orderField;
			});
			wrapper.addOrder("id", ORDER_ASC);
		}
		return wrapper;
	}

	@Override
	public City getCityByName(long stateId, String name) {
		CriteriaWrapper wrapper = createCriteriaWrapper(City.class);
		wrapper.eq("name", name);
		wrapper.eq("state.id", stateId);
		return uniqueResult(wrapper);
	}

	@Override
	public City getCityByName(String cityName) {
		String hql = "from City ct where ct.name=:name";
		return (City) this.getSession().createQuery(hql).setParameter("name", cityName).getSingleResult();
	}

}
