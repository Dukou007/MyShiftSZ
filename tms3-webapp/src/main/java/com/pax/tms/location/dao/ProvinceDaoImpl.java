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
import com.pax.tms.location.model.Province;
import com.pax.tms.location.web.form.QueryProvinceForm;

@Repository("provinceDaoImpl")
public class ProvinceDaoImpl extends BaseHibernateDao<Province, Long> implements ProvinceDao {

	@Override
	public Province getProvinceByName(long countryId, String name) {
		CriteriaWrapper wrapper = createCriteriaWrapper(Province.class);
		wrapper.eq("name", name);
		wrapper.eq("country.id", countryId);
		return uniqueResult(wrapper);
	}

	@Override
	public Province getProvinceByCode(long countryId, String code) {
		CriteriaWrapper wrapper = createCriteriaWrapper(Province.class);
		wrapper.eq("code", code);
		wrapper.eq("country.id", countryId);
		return uniqueResult(wrapper);
	}

	@Override
	public <S extends Serializable> CriteriaWrapper getCriteria(S command, boolean ordered) {
		QueryProvinceForm form = (QueryProvinceForm) command;

		CriteriaWrapper wrapper = createCriteriaWrapper(Province.class);
		wrapper.like("code", form.getCode());
		wrapper.like("name", form.getName());
		wrapper.eq("country.id", form.getCountryId());

		if (ordered) {
			wrapper.addOrder(form, "modifyDate", ORDER_DESC, (orderField) -> {
				if ("country".equals(orderField)) {
					wrapper.createAlias("country", "cty");
					return "cty.name";
				}
				return orderField;
			});
			wrapper.addOrder("id", ORDER_ASC);
		}
		return wrapper;
	}

	@Override
	public Province getProvinceByName(String provinceName) {
		String hql = "from Province pro where pro.name=:name";
		return (Province) this.getSession().createQuery(hql).setParameter("name", provinceName).getSingleResult();
	}

	@Override
	public Province getProvinceByImportName(Long countryId, String provinceName) {
		String hql = "from Province pro where pro.country.id = ? and upper(pro.name) = ?";
		return uniqueResult(hql, countryId, provinceName.toUpperCase());
	}

	@Override
	public Province getProvinceByCountryName(String countryName, String provinceName) {
		String hql = "select pro from Province pro,Country c  where pro.country.id=c.id and c.name=:countryName"
				+ " and pro.name=:provinceName";
		Province p = this.getSession().createNativeQuery(hql, Province.class).setParameter("countryName", countryName)
				.setParameter("provinceName", provinceName).getSingleResult();
		 return p;
	}

}
