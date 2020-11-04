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
package com.pax.tms.pub;

import java.io.File;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.pax.common.util.SendEmailTask;

public class MailSender {

	@Autowired
	@Qualifier("mailSender")
	private JavaMailSenderImpl sender;

	@Autowired
	@Qualifier("sendEmailTaskExecutor")
	private ThreadPoolTaskExecutor emailExecutorPool;

	private InternetAddress internetAddress;

	@PostConstruct
	void init() throws Exception {
		Properties p = new Properties();
		p.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		String logFile = null;
		String tmsHome = System.getProperty("tms.home");
		if (StringUtils.isNotEmpty(tmsHome)) {
			logFile = new File(tmsHome, "logs/velocity.log").getPath();
		} else {
			logFile = "logs/velocity.log";
		}
		p.put("runtime.log", logFile);
		Velocity.init(p);
	}

	@SuppressWarnings("rawtypes")
	protected String executeVelocity(Map root, String templateFile) {
		try {
			VelocityContext context = new VelocityContext(root);
			org.apache.velocity.Template template = Velocity.getTemplate(templateFile);
			StringWriter sw = new StringWriter();
			template.merge(context, sw);
			sw.close();
			return sw.toString();
		} catch (Exception e) {
			throw new SendEmailException("render velocity template failed", e);
		}
	}

	public void sendHtmlMail(String to, String subject, String template, Map<String, Object> context) {
		String content = executeVelocity(context, template);
		Properties prop = new Properties();
		Session session = Session.getInstance(prop);
		MimeMessage message = new MimeMessage(session);
		try {

			message.setFrom(getInternetAddress());

			Address[] toAddrs = new Address[] { new InternetAddress(to) };
			message.setRecipients(Message.RecipientType.TO, toAddrs);

			message.setSubject(subject);

			Multipart part = new MimeMultipart();

			MimeBodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(content, "text/html");
			part.addBodyPart(messageBodyPart);

			message.setContent(part);

			emailExecutorPool.execute(new SendEmailTask(message, sender));
		} catch (UnsupportedEncodingException e) {
			throw new SendEmailException("unsupported encoding", e);
		} catch (AddressException e) {
			throw new SendEmailException("invalid email address", e);
		} catch (MessagingException e) {
			throw new SendEmailException("create email message failed", e);
		}
	}

	private InternetAddress getInternetAddress() throws AddressException, UnsupportedEncodingException {
		if (internetAddress == null) {
			String from = getMailFrom();
			String name = getMailName();
			InternetAddress address = null;
			if (StringUtils.isEmpty(name)) {
				address = new InternetAddress(from);
			} else {
				address = new InternetAddress(from, name);
			}
			this.internetAddress = address;
		}
		return internetAddress;
	}

	private String getMailFrom() {
		Object from = sender.getJavaMailProperties().get("mail.from");
		return from == null ? sender.getUsername() : from.toString();
	}

	private String getMailName() {
		return sender.getJavaMailProperties().getProperty("mail.name");
	}

	public void setMailSender(JavaMailSenderImpl mailSender) {
		this.sender = mailSender;
	}

	public void setEmailExecutorPool(ThreadPoolTaskExecutor emailExecutorPool) {
		this.emailExecutorPool = emailExecutorPool;
	}
}
