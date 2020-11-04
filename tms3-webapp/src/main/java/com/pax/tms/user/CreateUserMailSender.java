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

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.pax.tms.user.model.PasswordResetToken;
import com.pax.tms.user.model.User;

public class CreateUserMailSender extends UserMailSender {

	public void sendWelcomeEmail(User user, PasswordResetToken pwdToken) {
		if (StringUtils.isEmpty(user.getEmail())) {
			return;
		}

		Map<String, Object> map = createContext(user, pwdToken);
		String subject = emailSubject.replace("{0}", map.get("fullname").toString());
		super.sendEmail(user.getEmail(), subject, emailTemplate, map);

	}

	@Override
	@Autowired
	public void setEmailSubject(@Value("${tms.user.welcome.emailSubject:}") String emailSubject) {
		super.setEmailSubject(emailSubject);
	}

	@Override
	@Autowired
	public void setEmailTemplate(@Value("${tms.user.welcome.emailTemplate:}") String emailTemplate) {
		super.setEmailTemplate(emailTemplate);
	}

	@Override
	@Autowired
	public void setResetPasswordUrl(@Value("${tms.user.resetPassword.url:}") String resetPasswordUrl) {
		super.setResetPasswordUrl(resetPasswordUrl);
	}

}
