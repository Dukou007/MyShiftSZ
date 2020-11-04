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
package com.pax.tms.monitor.amazon.sns;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.ResourceUtils;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.sns.AmazonSNSClient;

public class AmazonSNSFactoryBean implements FactoryBean<AmazonSNSClient> {

	private String profilesConfigFilePath;

	private String profileName;

	@Override
	public AmazonSNSClient getObject() throws Exception {
		return new AmazonSNSClient(new ProfileCredentialsProvider(
				ResourceUtils.getFile(profilesConfigFilePath).getAbsolutePath(), profileName));
	}

	@Override
	public Class<?> getObjectType() {
		return AmazonSNSClient.class;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

	public String getProfilesConfigFilePath() {
		return profilesConfigFilePath;
	}

	public void setProfilesConfigFilePath(String profilesConfigFilePath) {
		this.profilesConfigFilePath = profilesConfigFilePath;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

}
