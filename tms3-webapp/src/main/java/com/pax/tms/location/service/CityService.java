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

import com.pax.common.service.IBaseService;
import com.pax.tms.location.model.City;
import com.pax.tms.location.web.form.AddCityForm;
import com.pax.tms.location.web.form.EditCityForm;

public interface CityService extends IBaseService<City, Long> {

	List<City> getCityList(long countryId);

	boolean add(AddCityForm command);

	void edit(long id, EditCityForm command);

	void delete(Long[] idList);

}
