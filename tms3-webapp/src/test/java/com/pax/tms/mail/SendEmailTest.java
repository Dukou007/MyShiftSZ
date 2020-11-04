package com.pax.tms.mail;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import junit.framework.TestCase;

@ContextConfiguration(
		locations = { "classpath:ppm/spring-configuration/propertyFileConfigurer.xml",
				"classpath:ppm/spring-configuration/emailContext.xml" })

@RunWith(SpringJUnit4ClassRunner.class)
public class SendEmailTest extends TestCase {

	@Autowired
	@Qualifier("mailSender")
	private JavaMailSenderImpl sender;

	private String to = "elliottzhang@paxsz.com";
	private String from = "TMS@pax.us";
	private String name = "PAX Technical Support";

	@Test
	public void testSendEmail() throws UnsupportedEncodingException, MessagingException, InterruptedException {
		Properties prop = new Properties();
		Session session = Session.getInstance(prop);
		MimeMessage message = new MimeMessage(session);

		message.setFrom(new InternetAddress(from, name));

		Address[] toAddrs = new Address[] { new InternetAddress(to) };
		message.setRecipients(Message.RecipientType.TO, toAddrs);

		message.setSubject("Test Send Email");

		Multipart part = new MimeMultipart();

		MimeBodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent("Hello world!", "text/html");
		part.addBodyPart(messageBodyPart);

		message.setContent(part);

		sender.send(message);
		Thread.sleep(20 * 1000 * 1000);
	}

}
