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
package com.pax.tms.user;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.pax.tms.user.model.User;

public class InactiveAccountMailSender extends UserMailSender implements Serializable {

	private static final long serialVersionUID = -3421926728057902287L;

	public void sendInactiveAccountEmail(User user, int inactiveDays, int remainderDays) {
		if (StringUtils.isEmpty(user.getEmail())) {
			return;
		}
		Map<String, Object> map = createContext(user);
		map.put("inactiveDays", inactiveDays);
		map.put("remainderDays", remainderDays);
		super.sendEmail(user.getEmail(), emailSubject, emailTemplate, map);
	}

	@Override
	@Autowired
	public void setEmailSubject(@Value("${tms.user.inactiveAccount.emailSubject:}") String emailSubject) {
		this.emailSubject = emailSubject;
	}

	@Override
	@Autowired
	public void setEmailTemplate(@Value("${tms.user.inactiveAccount.emailTemplate:}") String emailTemplate) {
		this.emailTemplate = emailTemplate;
	}

	@Override
	@Autowired
	public void setInactiveAccountUrl(@Value("${tms.user.inactiveAccount.url:}") String inactiveAccountUrl) {
		this.inactiveAccountUrl = inactiveAccountUrl;
	}
}
