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
package com.pax.tms.pxretailer.message;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GetScheduledPackagesResponse extends BaseResponse {

	private static final long serialVersionUID = 6247015816308911191L;

	private static final String GET_SCHEDULED_PACKAGES = "GETSCHEDULEDPACKAGES";

	private List<PackageInformation> packageInformation;

	public GetScheduledPackagesResponse() {
		super();
		this.responseType = GET_SCHEDULED_PACKAGES;
	}

	public GetScheduledPackagesResponse(int statusCode, String statusMessage) {
		this();
		this.statusCode = statusCode;
		this.statusMessage = statusMessage;
	}

	public void addPackageInformation(String deploymentUUID, String packageName, String packageVersion, String fileUUID,
			String activationTime) {
		if (packageInformation == null) {
			packageInformation = new ArrayList<>();
		}

		packageInformation
				.add(new PackageInformation(deploymentUUID, packageName, packageVersion, fileUUID, activationTime));
	}

	public List<PackageInformation> getPackageInformation() {
		return packageInformation;
	}

	public void setPackageInformation(List<PackageInformation> packageInformation) {
		this.packageInformation = packageInformation;
	}

}
