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
package com.pax.tms.webservice.pxmaster.dao;

import java.util.List;

import org.hibernate.transform.ResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.pax.common.dao.hibernate.MyAliasToBeanResultTransformer;
import com.pax.tms.group.dao.GroupDao;
import com.pax.tms.webservice.pxmaster.form.OrganizationWebServiceForm;

@Repository("pxmasterOrganizationWebServiceDao")
public class OrganizationDao {

	@Autowired
	@Qualifier("groupDaoImpl")
	private GroupDao groupDao;

	private ResultTransformer organizationWebServiceFormTransformer = new MyAliasToBeanResultTransformer(
			OrganizationWebServiceForm.class);

	public List<OrganizationWebServiceForm> getOrganizations() {
		return groupDao.getAllGroupNames(OrganizationWebServiceForm.class, organizationWebServiceFormTransformer);
	}

	public List<OrganizationWebServiceForm> getUserOrganizations(long userId) {
		return groupDao.getUserGroups(userId, OrganizationWebServiceForm.class, organizationWebServiceFormTransformer);
	}

	public List<OrganizationWebServiceForm> getSelfAndDescendantOrganizations(long orgId) {
		return groupDao.getSelfAndDescendantOrganizationNames(orgId, OrganizationWebServiceForm.class,
				organizationWebServiceFormTransformer);
	}

}
