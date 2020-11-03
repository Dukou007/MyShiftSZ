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
package com.pax.tms.location.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pax.common.pagination.Page;
import com.pax.common.web.controller.BaseController;
import com.pax.tms.location.model.City;
import com.pax.tms.location.service.CityService;
import com.pax.tms.location.web.form.QueryCityForm;

@Controller
@RequestMapping(value = "/location/city")
public class CityController extends BaseController {

	@Autowired
	private CityService cityService;

	@RequestMapping(value = "list")
	@ResponseBody
	public Page<City> list(@ModelAttribute("command") QueryCityForm command) {
		return cityService.page(command);
	}
	
	@RequestMapping(value = "/select")
    @ResponseBody
    public Object select(Long provinceId, Boolean filter) {
        List<City> list = new ArrayList<City>();
        if (provinceId != null) {
            list = cityService.getCityList(provinceId);
        }
        List<String[]> result = new ArrayList<String[]>(list.size() + 1);
        String[] arr = null;
        if (filter != null && filter.booleanValue()) {
            arr = new String[2];
            arr[0] = "";
            arr[1] = this.getMessage("form.all");
            result.add(arr);
        }
        for (City city : list) {
            arr = new String[2];
            arr[0] = city.getId() + "";
            arr[1] = city.getName();
            result.add(arr);
        }
        return result;
    }

}
