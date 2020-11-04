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

import com.pax.common.dao.IBaseDao;
import com.pax.tms.location.model.Province;

public interface ProvinceDao extends IBaseDao<Province, Long> {

	Province getProvinceByName(long countryId, String name);

	Province getProvinceByCode(long countryId, String code);

	Province getProvinceByName(String provinceName);

	Province getProvinceByImportName(Long countryId, String provinceName);

	Province getProvinceByCountryName(String countryName,String provinceName);

}
