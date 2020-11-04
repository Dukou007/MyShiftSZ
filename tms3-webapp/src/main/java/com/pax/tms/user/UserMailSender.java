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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pax.tms.monitor.domain.UserSubscribeInfo;
import com.pax.tms.pub.MailSender;
import com.pax.tms.user.model.PasswordResetToken;
import com.pax.tms.user.model.User;

public class UserMailSender extends MailSender {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserMailSender.class);

	protected String emailSubject;
	protected String emailTemplate;
	protected String resetPasswordUrl;
	protected String inactiveAccountUrl;

	public void sendEmail(String mail, String subject, String template, Map<String, Object> context) {
		if (StringUtils.isEmpty(mail)) {
			return;
		}
		sendHtmlMail(mail, subject, emailTemplate, context);

	}

	protected Map<String, Object> createContext(User user) {
		String fullname = null;
		if (StringUtils.isEmpty(user.getFullname())) {
			fullname = user.getUsername();
		} else {
			fullname = user.getFullname();
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("fullname", fullname);
		map.put("username", user.getUsername());
		if (inactiveAccountUrl != null) {
			map.put("inactiveAccountUrl", inactiveAccountUrl);
		}
		return map;
	}

	protected Map<String, Object> createContext(User user, PasswordResetToken pwdToken) {
		String fullname = null;
		if (StringUtils.isEmpty(user.getFullname())) {
			fullname = user.getUsername();
		} else {
			fullname = user.getFullname();
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("fullname", fullname);
		map.put("username", user.getUsername());
		if (resetPasswordUrl != null) {
			String url = createResetPasswordUrl(user, pwdToken);
			map.put("resetPasswordUrl", url);
		}

		return map;
	}

	protected Map<String, Object> createContext(UserSubscribeInfo user) {
		String fullname = null;
		if (StringUtils.isEmpty(user.getFullname())) {
			fullname = user.getUsername();
		} else {
			fullname = user.getFullname();
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("fullname", fullname);
		map.put("username", user.getUsername());
		return map;
	}

	protected String createResetPasswordUrl(User user, PasswordResetToken pwdToken) {
		String url = resetPasswordUrl;
		if (url.contains("?")) {
			url += "&token=" + pwdToken.getResetPwdToken();
		} else {
			url += "?token=" + pwdToken.getResetPwdToken();
		}
		try {
			url += "&username=" + URLEncoder.encode(user.getUsername(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// never happen
			LOGGER.error("unsupported encoding - UTF-8", e);
		}
		return url;
	}

	public String getEmailSubject() {
		return emailSubject;
	}

	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}

	public String getEmailTemplate() {
		return emailTemplate;
	}

	public void setEmailTemplate(String emailTemplate) {
		this.emailTemplate = emailTemplate;
	}

	public String getResetPasswordUrl() {
		return resetPasswordUrl;
	}

	public void setResetPasswordUrl(String resetPasswordUrl) {
		this.resetPasswordUrl = resetPasswordUrl;
	}

	public String getInactiveAccountUrl() {
		return inactiveAccountUrl;
	}

	public void setInactiveAccountUrl(String inactiveAccountUrl) {
		this.inactiveAccountUrl = inactiveAccountUrl;
	}

}
