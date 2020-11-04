/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description: SubscribeMailSender
 * Revision History:		
 * Date	                 Author	                Action
 * 20170111  	         Frank           	    Modify 
 * ============================================================================		
 */
package com.pax.tms.monitor.web;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.pax.tms.monitor.domain.UserSubscribeInfo;
import com.pax.tms.monitor.model.AlertEvent;
import com.pax.tms.user.UserMailSender;

public class SubscribeMailSender extends UserMailSender implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1311222232445013072L;

	public void sendSubscribeEmail(UserSubscribeInfo user, AlertEvent event) {
		if (StringUtils.isEmpty(user.getEmail())) {
			return;
		}
		Date date = new Date();
		SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sdformat.format(date);
		Map<String, Object> map = createContext(user);
		map.put("alertValue", event.getAlertValue());
		map.put("alertSeverity", event.getAlertSeverity());
		map.put("alertMessage", event.getAlertMsg());
		map.put("groupPath", event.getGroup().getNamePath());
		map.put("time", time);
		String subject = emailSubject.replace("{0}", map.get("fullname").toString());
		super.sendEmail(user.getEmail(), subject, emailTemplate, map);

	}

	@Override
	@Autowired
	public void setEmailSubject(@Value("${tms.user.subscribeEmail.emailSubject:}") String emailSubject) {
		this.emailSubject = emailSubject;
	}

	@Override
	@Autowired
	public void setEmailTemplate(@Value("${tms.user.subscribeEmail.emailTemplate:}") String emailTemplate) {
		this.emailTemplate = emailTemplate;
	}
}
