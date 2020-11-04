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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pax.common.pagination.Page;
import com.pax.common.web.controller.BaseController;
import com.pax.tms.location.model.Country;
import com.pax.tms.location.service.CountryService;
import com.pax.tms.location.web.form.QueryCountryForm;

@Controller
@RequestMapping(value = "/location/country")
public class CountryController extends BaseController {

	@Autowired
	private CountryService countryService;

	@RequestMapping(value = "list")
	@ResponseBody
	public Page<Country> list(QueryCountryForm command) {
		return countryService.page(command);
	}

}
