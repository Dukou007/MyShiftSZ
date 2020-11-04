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
import com.pax.tms.location.model.Province;
import com.pax.tms.location.web.form.AddProvinceForm;
import com.pax.tms.location.web.form.EditProvinceForm;

public interface ProvinceService extends IBaseService<Province, Long> {

	Province getProvinceByName(long countryId, String provinceName);

	Province getProvinceByCode(long countryId, String provinceCode);

	List<Province> getProvinceList(long countryId);

	void add(AddProvinceForm command);

	void edit(long id, EditProvinceForm command);

	void delete(Long[] idList);

	void onDeleteCountry(Country country);
	
	Province getProvinceByImportName(Long countryId,String provinceName);

}
