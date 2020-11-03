/*
* ========================================================================
* COPYRIGHT
*              PAX TECHNOLOGY, INC. PROPRIETARY INFORMATION
*   This software is supplied under the terms of a license agreement or
*   nondisclosure agreement with PAX Technology, Inc. and may not be copied 
 *   or disclosed except in accordance with the terms in that agreement.
*      Copyright (C) 2009-2015 PAX Technology, Inc. All rights reserved.
*
* ========================================================================
*/
package com.pax.tms.webservice.pxmaster;

import java.util.Date;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.cxf.annotations.WSDLDocumentation;
import org.apache.cxf.annotations.WSDLDocumentationCollection;
import org.hibernate.validator.constraints.NotEmpty;

import com.pax.tms.webservice.pxmaster.form.BaseResponse;
import com.pax.tms.webservice.pxmaster.form.PackageFile;
import com.pax.tms.webservice.pxmaster.form.PubUser;

/**
 * @author Dai.L
 * @date 2014-10-23
 */

@WebService(serviceName = "DeployWebService", targetNamespace = "http://ppm.paxsz.com/")
@WSDLDocumentationCollection(
		value = { @WSDLDocumentation(value = "1.0.50", placement = WSDLDocumentation.Placement.TOP) })
public interface DeployWebService {

	/**
	 * Description: save the package to database
	 * 
	 * @param groupIds
	 * @param uploadFileList
	 * @param user
	 * @return
	 */
	@WebMethod
	BaseResponse addPackageByGroupId(@WebParam(name = "groupId") @Valid @NotEmpty List<Long> groupIds,
			@WebParam(name = "packageFile") @Valid @NotEmpty List<PackageFile> uploadFileList,
			@WebParam(name = "user") @Valid @NotNull PubUser user);

	/**
	 * Description: deploy the terminals with the given uuid's package
	 * activation_mode:0 Now,1 special time, 2 lane closed.
	 * 
	 * @param tsnList
	 * @param uuid
	 * @param deployTime
	 * @param activationMode
	 * @param activationTime
	 * @param user
	 * @return
	 */
	@WebMethod
	BaseResponse deployPackageBySn(@WebParam(name = "tsn") @Valid @NotNull List<String> tsnList,
			@WebParam(name = "uuid") @Valid @NotNull String uuid, @WebParam(name = "deployTime") Date deployTime,
			@WebParam(name = "activationMode") String activationMode,
			@WebParam(name = "activationTime") Date activationTime,
			@WebParam(name = "user") @Valid @NotNull PubUser user);

	/**
	 * Description: deploy the terminals with the given uuid's package
	 * activation_mode:0 Now,1 special time, 2 lane closed.
	 * 
	 * @param groupId
	 * @param uuid
	 * @param deployTime
	 * @param activationMode
	 * @param activationTime
	 * @param user
	 * @return
	 */
	@WebMethod
	BaseResponse deployPackageByGroupId(@WebParam(name = "groupId") @Valid @NotNull Long groupId,
			@WebParam(name = "uuid") @Valid @NotNull String uuid, @WebParam(name = "deployTime") Date deployTime,
			@WebParam(name = "activationMode") String activationMode,
			@WebParam(name = "activationTime") Date activationTime,
			@WebParam(name = "user") @Valid @NotNull PubUser user);

}
