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

import com.pax.tms.res.model.Pkg;
import com.pax.tms.webservice.pxmaster.form.BaseResponse;
import com.pax.tms.webservice.pxmaster.form.PubPackage;
import com.pax.tms.webservice.pxmaster.form.PubUser;
import com.pax.tms.webservice.pxmaster.form.PackageFile;

public interface PubPackageService {

	void addPackageByGroupId(BaseResponse response, List<Long> groupIds, List<PackageFile> uploadFileList, PubUser user,
			String remoteAddress);

	List<Pkg> getPubPackagesByUuid(String uuid);

	List<PubPackage> getPackages(String name, String type, String version);

	PubPackage get(Long id);

	List<PubPackage> getPackagesByName(String name);

}
