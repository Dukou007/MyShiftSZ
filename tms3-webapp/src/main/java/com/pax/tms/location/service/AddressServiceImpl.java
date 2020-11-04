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

import io.vertx.core.json.Json;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pax.common.exception.BusinessException;
import com.pax.common.util.RegexMatchUtils;
import com.pax.common.web.form.AddressForm;
import com.pax.tms.location.dao.CityDao;
import com.pax.tms.location.dao.CountryDao;
import com.pax.tms.location.dao.ProvinceDao;
import com.pax.tms.location.model.City;
import com.pax.tms.location.model.Country;
import com.pax.tms.location.model.Province;

@Service("addressServiceImpl")
public class AddressServiceImpl implements AddressService {

	@Autowired
	private CountryDao countryDao;

	@Autowired
	private ProvinceDao proDao;

	@Autowired
	private CityDao cityDao;
	
	private String DEFAULT_NAME = "default";
	private Date DEFAULT_TIME = new Date(); 

	@Override
	public AddressInfo checkAddress(Long countryId, Long provinceId, Long cityId, String zipCode, String address) {
		Country country = validateCountryId(countryId);
		Province pro = validateProvince(provinceId);
		City city = validateCity(cityId);

		AddressInfo info = new AddressInfo();
		info.setCountry(country);
		info.setProvince(pro);
		info.setCity(city);
		info.setZipCode(zipCode);
		info.setAddress(address);

		if (country != null) {
			info.setCountryName(country.getName());
		}

		if (pro != null) {
			info.setProvinceName(pro.getName());
		}

		if (city != null) {
			info.setCityName(city.getName());
		}
		return info;
	}

	private Country validateCountryId(Long countryId, String countryName, AddressForm form) {
		Country country = null;
		if (null != countryId) {
			country = countryDao.get(countryId);
			if (null == country) {
				throw new BusinessException("msg.country.notFound");
			}
		} else {
			if (null != countryName && !"".equals(countryName)) {
				Country temp = countryDao.getCountryByName(countryName);
				if (null == temp) {
					throw new BusinessException("msg.country.notFound");
				} else {
					form.setCountryId(null==form.getCountryId()?temp.getId():form.getCountryId());
					country = temp;
				}
			}
		}
		return country;
	}
	
	private Country validateCountryId(Long countryId) {
		Country country = null;
		if (null != countryId) {
			country = countryDao.get(countryId);
			if (null == country) {
				throw new BusinessException("msg.country.notFound");
			}
		}
		return country;
	}

	private Province validateProvince(Long provinceId) {
		Province pro = null;
		if (provinceId != null) {
			pro = proDao.get(provinceId);
			if (pro == null) {
				throw new BusinessException("msg.province.notFound");
			}
		}
		return pro;
	}

	private City validateCity(Long cityId) {
		City city = null;

		if (cityId != null) {
			city = cityDao.get(cityId);
			if (city == null) {
				throw new BusinessException("msg.city.notFound");
			}
		}

		return city;
	}

	@Override
	public AddressInfo checkAddress(String countryName, String provinceName, String cityName, String zipCode,
			String address) {
		AddressInfo info = new AddressInfo();

		info.setCountryName(countryName);

		info.setProvinceName(provinceName);

		info.setCityName(cityName);

		info.setZipCode(zipCode);
		info.setAddress(address);

		return info;
	}

	@Override
	public AddressInfo checkAddress(AddressForm form) {

		Country country = validateCountryId(form.getCountryId(),form.getCountryName(),form);
		Province pro = validateProvince(form.getProvinceId());
		City city = validateCity(form.getCityId());
		City.validateCityName(form.getCityName());

		AddressInfo info = new AddressInfo();
		info.setCountry(country);
		info.setProvince(pro);
		info.setCity(city);
		if (form.getZipCode() != null) {
			if (form.getZipCode().trim().length() > 7) {	
				throw new BusinessException("msg.address.zipcodeOverLength");
			}
			info.setZipCode(form.getZipCode().toUpperCase());
		}
		if (form.getAddress() != null) {
			validateAddress(form.getAddress());
			info.setAddress(form.getAddress());
		}

		if (country != null) {
			info.setCountryName(country.getName());
			form.setCountryName(country.getName());
		} else {
			info.setCountryName(form.getCountryName());
		}

		if (null != pro) {
			info.setProvinceName(pro.getName());
		} else if("".equals(form.getProvinceName())){
			info.setProvinceName(form.getProvinceName());
		} else {
			//这里就是导致ID唯一错误的原因
			Province p = proDao.getProvinceByName(form.getCountryId(), form.getProvinceName());
			if(null == p)
			{
				//如果省份不存在，则新增
				//检查自定义的省份province是否已经存在
				info.setProvinceName(form.getProvinceName());
				Province s = new Province();
				s.setCountry(country);
				s.setName(form.getProvinceName());
				s.setCreator(null==form.getLoginUsername()?DEFAULT_NAME:form.getLoginUsername());
				s.setCreateDate(null==form.getRequestTime()?DEFAULT_TIME:form.getRequestTime());
				s.setModifier(null==form.getLoginUsername()?DEFAULT_NAME:form.getLoginUsername());
				s.setModifyDate(null==form.getRequestTime()?DEFAULT_TIME:form.getRequestTime());
				proDao.save(s);
			}
			else{
				info.setProvinceName(form.getProvinceName());
			}
		}
		
		if (city != null) {
			info.setCityName(city.getName());
		} else {
			info.setCityName(form.getCityName());
		}
		if (StringUtils.isNotEmpty(form.getCountryName())) {
			if (countryDao.getCountryByName(form.getCountryName()) == null) {
				throw new BusinessException("msg.country.countryNotFound",new String[]{form.getCountryName()});
			}

		}

		return info;
	}

	private void validateAddress(String address) {
		if (RegexMatchUtils.contains(address, "[/\"<>{}()=]|\\$$")) {
			throw new BusinessException("msg.user.illegalAddress");
		}
	}

	@Override
	public List<Map<String, String>> getTimeZonesByCountry(String countryName) {
		if (countryName == null) {
			throw new BusinessException("msg.country.required");
		}
		String timeZoneJson = "";
		try {
			InputStream in = getResourceAsStream("timeZone.json");
			timeZoneJson = IOUtils.toString(in, Charsets.toCharset("UTF-8"));
		} catch (IOException e) {
			throw new BusinessException("msg.initTimeZone.fileNotFound", e);
		}
		@SuppressWarnings("unchecked")
		List<Map<String, String>> list = Json.decodeValue(timeZoneJson, List.class);
		List<Map<String, String>> timeZones = new ArrayList<>();
		for (Map<String, String> map : list) {
			Map<String, String> timeZone = new HashMap<>();
			String country = map.get("country");
			if (countryName.equals(country)) {
				timeZone.put("timeZoneId", map.get("timeZoneId"));
				timeZone.put("timeZoneName", map.get("timeZoneName"));
				timeZone.put("isDaylightSaving", map.get("isDaylightSaving"));
				timeZone.put("abbreviation", map.get("abbreviation"));
				timeZones.add(timeZone);
			}
		}
		return timeZones;
	}

	private static InputStream getResourceAsStream(String resource) {
		String stripped = resource.startsWith("/") ? resource.substring(1) : resource;
		InputStream stream = null;
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader != null) {
			stream = classLoader.getResourceAsStream(stripped);
		}
		if (stream == null) {
			stream = AddressServiceImpl.class.getResourceAsStream(resource);
		}
		if (stream == null) {
			stream = AddressServiceImpl.class.getClassLoader().getResourceAsStream(stripped);
		}
		return stream;
	}

	@Override
	public List<Map<String, String>> getAllTimeZones() {
		List<Map<String, String>> cTimeZones = getTimeZonesByCountry("Canada");
		List<Map<String, String>> uTimeZones = getTimeZonesByCountry("United States");
		cTimeZones.addAll(uTimeZones);

		Collections.sort(cTimeZones, new Comparator<Map<String, String>>() {

			@Override
			public int compare(Map<String, String> map1, Map<String, String> map2) {
				return map1.get("timeZoneName").compareTo(map2.get("timeZoneName"));
			}
		});

		return cTimeZones;
	}
	
	@Override
	public List<Map<String, String>> getTimeZones() {
		String timeZoneJson = "";
		try {
			InputStream in = getResourceAsStream("timeZone.json");
			timeZoneJson = IOUtils.toString(in, Charsets.toCharset("UTF-8"));
		} catch (IOException e) {
			throw new BusinessException("msg.initTimeZone.fileNotFound", e);
		}
		@SuppressWarnings("unchecked")
		List<Map<String, String>> list = Json.decodeValue(timeZoneJson, List.class);
		List<Map<String, String>> timeZones = new ArrayList<>();
		for (Map<String, String> map : list) {
			Map<String, String> timeZone = new HashMap<>();
			timeZone.put("timeZoneId", map.get("timeZoneId"));
			timeZone.put("timeZoneName", map.get("timeZoneName"));
			timeZone.put("isDaylightSaving", map.get("isDaylightSaving"));
			timeZone.put("abbreviation", map.get("abbreviation"));
			timeZones.add(timeZone);
		}
		Collections.sort(timeZones, new Comparator<Map<String, String>>() {

			@Override
			public int compare(Map<String, String> map1, Map<String, String> map2) {
				return map1.get("timeZoneName").compareTo(map2.get("timeZoneName"));
			}
		});
		return timeZones;
	}

	@Override
	public Map<String, Object> getParentTimeZone(String countryName, String timeZoneId) {
		List<Map<String, String>> timeZones = getTimeZonesByCountry(countryName);
		Map<String, Object> map = new HashMap<>();
		for (Map<String, String> timeZone : timeZones) {
			if (StringUtils.equalsIgnoreCase(timeZoneId, timeZone.get("timeZoneId"))) {
				map.put("timeZoneName", timeZone.get("timeZoneName"));
				map.put("timeZoneId", timeZone.get("timeZoneId"));
				map.put("isDaylightSaving", timeZone.get("isDaylightSaving"));
				map.put("abbreviation", timeZone.get("abbreviation"));
			}
		}
		return map;
	}

	@Override
	public List<String> getTimeZoneIds(String countryName) {
		List<Map<String, String>> timeZones = getTimeZonesByCountry(countryName);
		List<String> timeZoneIds = new ArrayList<>();
		for (Map<String, String> timeZone : timeZones) {
			timeZoneIds.add(timeZone.get("timeZoneId"));
		}
		return timeZoneIds;
	}

	@Override
	public String getAbbreviationByTimeZoneId(String timeZoneId) {
		List<Map<String, String>> timeZones = getAllTimeZones();
		String abbreviation = null;
		for (Map<String, String> timeZone : timeZones) {
			if (timeZoneId.equals(timeZone.get("timeZoneId"))) {
				abbreviation = timeZone.get("abbreviation");
			}
		}
		return abbreviation;
	}
	@Override
	public void checkTimeZoneData(String countryName,String timeZoneId,String daylightSaving){
	    List<Map<String, String>> timeZoneList = getTimeZonesByCountry(countryName);
        if(CollectionUtils.isEmpty(timeZoneList)){
            throw new BusinessException("msg.timeZone.timeZoneNotFound", new String[] { countryName, timeZoneId });
        }
        List<String> timeZoneIds = new ArrayList<>();
        for (Map<String, String> timeZone : timeZoneList) {
            timeZoneIds.add(timeZone.get("timeZoneId"));
        }
        if (CollectionUtils.isNotEmpty(timeZoneIds) && !timeZoneIds.contains(timeZoneId)) {
            throw new BusinessException("msg.timeZone.timeZoneNotFound", new String[] { countryName, timeZoneId });
        }
        
        for (Map<String, String> timeZoneMap : timeZoneList) {
            if(StringUtils.equalsIgnoreCase(timeZoneId, timeZoneMap.get("timeZoneId")) && StringUtils.equalsIgnoreCase("0", timeZoneMap.get("isDaylightSaving")) && !StringUtils.equalsIgnoreCase(daylightSaving, timeZoneMap.get("isDaylightSaving"))){
                throw new BusinessException("msg.timeZone.daylightSaving.notmatch", new String[] { daylightSaving, timeZoneId });
            }
        }
	}
}
