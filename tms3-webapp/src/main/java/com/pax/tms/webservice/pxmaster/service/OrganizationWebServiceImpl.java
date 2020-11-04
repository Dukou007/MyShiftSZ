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
package com.pax.tms.webservice.pxmaster.service;

import java.util.List;

import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.pax.tms.webservice.pxmaster.OrganizationWebService;
import com.pax.tms.webservice.pxmaster.dao.OrganizationDao;
import com.pax.tms.webservice.pxmaster.form.OrganizationWebServiceForm;

@WebService(targetNamespace = "http://impl.service.admin.pxmaster.paxsz.com/")
public class OrganizationWebServiceImpl implements OrganizationWebService {

	@Autowired
	@Qualifier("pxmasterOrganizationWebServiceDao")
	private OrganizationDao orgDao;

	@Override
	public List<OrganizationWebServiceForm> getOrganizations() {
		return this.orgDao.getOrganizations();
	}

	@Override
	public List<OrganizationWebServiceForm> getUserOrganizations(long userId) {
		return orgDao.getUserOrganizations(userId);
	}

	@Override
	public List<OrganizationWebServiceForm> getSelfAndDescendantOrganizations(long orgId) {
		return this.orgDao.getSelfAndDescendantOrganizations(orgId);
	}

	public OrganizationDao getOrgDao() {
		return orgDao;
	}

	public void setOrgDao(OrganizationDao orgDao) {
		this.orgDao = orgDao;
	}

}
