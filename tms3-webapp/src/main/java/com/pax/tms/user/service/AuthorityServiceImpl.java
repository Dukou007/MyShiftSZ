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
package com.pax.tms.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pax.common.dao.IBaseDao;
import com.pax.common.service.impl.BaseService;
import com.pax.tms.user.dao.AuthorityDao;
import com.pax.tms.user.model.Authority;

@Service("authorityServiceImpl")
public class AuthorityServiceImpl extends BaseService<Authority, Long> implements AuthorityService {

	@Autowired
	private AuthorityDao authorityDao;

	@Override
	public IBaseDao<Authority, Long> getBaseDao() {
		return authorityDao;
	}

	@Override
	public List<Authority> list() {
		return authorityDao.list();
	}

}
