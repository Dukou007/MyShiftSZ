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
import java.util.Map;

import com.pax.common.web.form.AddressForm;

public interface AddressService {

	AddressInfo checkAddress(String countryName, String provinceName, String cityName, String zipCode, String address);

	AddressInfo checkAddress(Long countryId, Long provinceId, Long cityId, String zipCode, String address);

	AddressInfo checkAddress(AddressForm form);

	List<Map<String, String>> getTimeZonesByCountry(String countryName);
	
	//只获取美国和加拿大的时区
	List<Map<String, String>> getAllTimeZones();
	
	//获取所有时区
	List<Map<String, String>> getTimeZones();

	Map<String, Object> getParentTimeZone(String countryName, String timeZoneId);
	
	List<String> getTimeZoneIds(String countryName);
	
	String getAbbreviationByTimeZoneId(String timeZoneId);
	void checkTimeZoneData(String countryName,String timeZoneId,String daylightSaving);
}
