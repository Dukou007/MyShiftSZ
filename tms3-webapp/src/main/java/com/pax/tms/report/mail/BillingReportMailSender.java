package com.pax.tms.report.mail;


import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.pax.tms.pub.MailSender;

@Component
public class BillingReportMailSender extends MailSender {
	
	@Value("${tms.user.billingReport.emailSubject}")
	private String emailSubject;
	@Value("${tms.user.billingReport.emailTemplate}")
	private String emailTemplate;
	
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
	
	public void sendMail(String to,String fullname) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("fullname", fullname);
		sendHtmlMail(to, emailSubject, emailTemplate, map);
	}
}
