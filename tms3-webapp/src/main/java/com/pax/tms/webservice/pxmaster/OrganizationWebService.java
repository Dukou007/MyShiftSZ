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
package com.pax.tms.webservice.pxmaster;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.cxf.annotations.WSDLDocumentation;
import org.apache.cxf.annotations.WSDLDocumentationCollection;

import com.pax.tms.webservice.pxmaster.form.OrganizationWebServiceForm;

@WebService(serviceName = "OrganizationWebService", targetNamespace = "http://webservice.paxsz.com/")
@WSDLDocumentationCollection(value = {
		@WSDLDocumentation(value = "1.0.50", placement = WSDLDocumentation.Placement.TOP) })
public interface OrganizationWebService {

	@WebMethod
	List<OrganizationWebServiceForm> getOrganizations();

	@WebMethod
	List<OrganizationWebServiceForm> getUserOrganizations(@WebParam(name = "userId") long userId);

	@WebMethod
	List<OrganizationWebServiceForm> getSelfAndDescendantOrganizations(@WebParam(name = "orgId") long orgId);

}