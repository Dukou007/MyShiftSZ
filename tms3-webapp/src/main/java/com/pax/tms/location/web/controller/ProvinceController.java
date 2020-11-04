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
import com.pax.tms.location.model.Province;
import com.pax.tms.location.service.ProvinceService;
import com.pax.tms.location.web.form.QueryProvinceForm;

@Controller
@RequestMapping(value = "/location/province")
public class ProvinceController extends BaseController {

	@Autowired
	private ProvinceService provinceService;

	@RequestMapping(value = "list")
	@ResponseBody
	public Page<Province> list(@ModelAttribute("command") QueryProvinceForm command) {
		return provinceService.page(command);
	}

	@RequestMapping(value = "/select")
    @ResponseBody
    public Object select(Long countryId, Boolean filter) {
        List<Province> list = new ArrayList<Province>();
        if (countryId != null) {
            list = provinceService.getProvinceList(countryId);
        }
        List<String[]> result = new ArrayList<String[]>(list.size() + 1);
        String[] arr = null;
        if (filter != null && filter.booleanValue()) {
            arr = new String[2];
            arr[0] = "";
            arr[1] = this.getMessage("form.all");
            result.add(arr);
        }
        for (Province province : list) {
            arr = new String[2];
            arr[0] = province.getId() + "";
            arr[1] = province.getName();
            result.add(arr);
        }
        return result;
    }
	
}
