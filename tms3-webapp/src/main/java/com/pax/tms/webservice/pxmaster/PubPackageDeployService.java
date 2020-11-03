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

import java.util.Date;
import java.util.List;

import com.pax.tms.res.model.Pkg;

public interface PubPackageDeployService {

	void deployPackage(List<String> tsnList, Pkg pubPackage, Date deployTime, String activation_mode,
			Date activation_time, Long userId, String remoteAddress);

	void deployPackageByGroupId(Long groupId, Pkg pubPackage, Date deployTime, String activation_mode,
			Date activation_time, Long userId, String remoteAddresss);

}
