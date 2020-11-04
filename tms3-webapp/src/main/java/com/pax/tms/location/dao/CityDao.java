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
import com.pax.tms.location.model.City;

public interface CityDao extends IBaseDao<City, Long> {

    City getCityByName(long stateId, String name);

	City getCityByName(String cityName);

}
