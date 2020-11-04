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
import com.pax.tms.location.model.Country;
import com.pax.tms.location.web.form.AddCountryForm;
import com.pax.tms.location.web.form.EditCountryForm;

public interface CountryService extends IBaseService<Country, Long> {

	Country getCountryByCode(String code);

	Country getCountryByName(String name);

	void add(AddCountryForm command);

	void edit(long id, EditCountryForm command);

	void delete(Long[] idList);

	List<Country> getCounryList();
	
	Country getCountryByImportName(String countryName);
}
