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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.pac4j.core.profile.CommonProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.pax.tms.deploy.service.GroupDeployService;
import com.pax.tms.deploy.service.TerminalDeployService;
import com.pax.tms.deploy.web.form.GroupDeployForm;
import com.pax.tms.deploy.web.form.TerminalDeployForm;
import com.pax.tms.res.model.Pkg;
import com.pax.tms.webservice.pxmaster.BaseServiceImpl;
import com.pax.tms.webservice.pxmaster.PubPackageDeployService;

@Service("pxmasterPubPackageDeployService")
public class PubPackageDeployServiceImpl extends BaseServiceImpl implements PubPackageDeployService {

	@Autowired
	private TerminalDeployService terminalDeployService;

	@Autowired
	private GroupDeployService groupDeployService;

	private String defaultTimeZone;

	@Override
	public void deployPackageByGroupId(Long groupId, Pkg pubPackage, Date deployTime, String activationMode,
			Date activationTime, Long userId, String remoteAddress) {
		Date activationDate = activationTime != null ? activationTime : new Date();
		Date deployDate = deployTime != null ? deployTime : new Date();
		CommonProfile userProfile = loadTmsUserProfile(userId);
		if (userProfile != null) {
			userProfile.addAttribute("host", remoteAddress);
		}
		GroupDeployForm command = new GroupDeployForm();
		setTmsUserProfile(command, userProfile);
		command.setTimeZone(defaultTimeZone);
		command.setGroupId(groupId);
		command.setPkgId(pubPackage.getId());
		command.setDwnlStartTime(convertDate(deployDate));
		command.setActvStartTime(convertDate(activationDate));
		groupDeployService.deploy(command);
	}

	@Override
	public void deployPackage(List<String> tsnList, Pkg pubPackage, Date deployTime, String activation_mode,
			Date activation_time, Long userId, String remoteAddress) {
		Date activationDate = activation_time != null ? activation_time : new Date();
		Date deployDate = deployTime != null ? deployTime : new Date();
		CommonProfile userProfile = loadTmsUserProfile(userId);
		if (userProfile != null) {
			userProfile.addAttribute("host", remoteAddress);
		}
		for (String tsn : tsnList) {
			TerminalDeployForm command = new TerminalDeployForm();
			setTmsUserProfile(command, userProfile);
			command.setTsn(tsn);
			command.setGroupId(1L);
			command.setTimeZone(defaultTimeZone);
			command.setPkgId(pubPackage.getId());
			command.setDwnlStartTime(convertDate(deployDate));
			command.setActvStartTime(convertDate(activationDate));
			terminalDeployService.deploy(command);
		}
	}

	protected CommonProfile createPxDesignerUserProfile(Long userId) {
		if (userId != null) {
			return loadTmsUserProfile(userId);
		} else {
			// CommonProfile profile = new CommonProfile();
			// profile.addAttribute(USER_ID, 1L);
			// profile.setId("PXDesigner");
			// profile.addRole("PXDesigner");
			// Collection<String> roles = profile.getRoles();
			// profile.addAttribute(USER_ROLES, roles == null ? null :
			// StringUtils.join(roles, ","));
			return null;
		}
	}

	protected String convertDate(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		TimeZone timeZone = TimeZone.getTimeZone(defaultTimeZone);
		format.setTimeZone(timeZone);
		return format.format(date);
	}

	@Autowired
	public void setDefaultTimeZone(@Value("${tms.compatibilitySetting.defaultTimeZone:}") String defaultTimeZone) {
		this.defaultTimeZone = defaultTimeZone;
	}

}
