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
package com.pax.common.util;

import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public class SendEmailTask implements Runnable {

	private static final Logger LOGGER = LoggerFactory.getLogger(SendEmailTask.class);

	private IEmailSenderListener iEmailSenderListener;

	private JavaMailSender javaMailSender;

	SimpleMailMessage simpleMailMessage;
	MimeMessage mimeMessage;

	public SendEmailTask(SimpleMailMessage simpleMailMessage, JavaMailSender javaMailSender) {
		this.simpleMailMessage = simpleMailMessage;
		this.javaMailSender = javaMailSender;
	}

	public SendEmailTask(MimeMessage mimeMessage, JavaMailSender javaMailSender) {
		this.mimeMessage = mimeMessage;
		this.javaMailSender = javaMailSender;
	}

	@Override
	public void run() {
		if (iEmailSenderListener != null) {
			iEmailSenderListener.beforeSendEmail();
		}

		boolean flag = false;
		try {
			flag = doAction();
		} catch (Exception e) {
			if (iEmailSenderListener != null) {
				iEmailSenderListener.catchException(e);
			}
			LOGGER.error("Send email failed", e);
		}
		if (iEmailSenderListener != null) {
			iEmailSenderListener.afterSendEmail(flag);
		}
	}

	private boolean doAction() {
		boolean flag = true;
		if (this.simpleMailMessage != null) {
			javaMailSender.send(this.simpleMailMessage);
		} else if (this.mimeMessage != null) {
			javaMailSender.send(this.mimeMessage);
		}
		return flag;
	}

	public IEmailSenderListener getiEmailSenderListener() {
		return iEmailSenderListener;
	}

	public void setiEmailSenderListener(IEmailSenderListener iEmailSenderListener) {
		this.iEmailSenderListener = iEmailSenderListener;
	}

	public JavaMailSender getJavaMailSender() {
		return javaMailSender;
	}

	public void setJavaMailSender(JavaMailSender javaMailSender) {
		this.javaMailSender = javaMailSender;
	}

	public SimpleMailMessage getSimpleMailMessage() {
		return simpleMailMessage;
	}

	public void setSimpleMailMessage(SimpleMailMessage simpleMailMessage) {
		this.simpleMailMessage = simpleMailMessage;
	}

	public MimeMessage getMimeMessage() {
		return mimeMessage;
	}

	public void setMimeMessage(MimeMessage mimeMessage) {
		this.mimeMessage = mimeMessage;
	}

}
