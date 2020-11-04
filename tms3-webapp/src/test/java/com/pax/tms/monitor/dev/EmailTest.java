package com.pax.tms.monitor.dev;

import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.pax.tms.demo.ServiceJunitCase;
import com.pax.tms.monitor.domain.UserSubscribeInfo;
import com.pax.tms.monitor.model.AlertEvent;
import com.pax.tms.monitor.web.SubscribeMailSender;

public class EmailTest extends ServiceJunitCase {

	@Autowired
	private SubscribeMailSender subscribeMailSender;

	@Autowired
	@Qualifier("mailSender")
	private JavaMailSenderImpl sender;

	@Test
	public void testJavaMail() {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom("broadpos@pax.us");
		message.setTo("crazywang@paxsz.com");
		message.setSubject("crazy");
		message.setText("just a test for crazy");
		sender.send(message);
	}

	@Test
	public void test() throws InterruptedException {
		UserSubscribeInfo user = new UserSubscribeInfo();
		user.setEmail("crazy.w@paxdata.com");
		user.setFullname("crazy");
		AlertEvent event = new AlertEvent();
		event.setAlertMsg("crazy test message");
		event.setAlertSeverity(1);
		event.setAlertTime(new Date());
		event.setAlertValue("100");

		subscribeMailSender.sendSubscribeEmail(user, event);

		Thread.sleep(10000);
	}

	@Test
	public void simpleMessage() throws GeneralSecurityException {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost("smtp.office365.com");
		mailSender.setPort(587);
		mailSender.setUsername("broadpos@pax.us");
		mailSender.setPassword("Adm49!e0");
		Properties prop = new Properties();

		prop.put("mail.smtp.auth", true);
		prop.put("mail.smtp.timeout", 5000);
		prop.put("mail.smtp.ssl.enable", false);
		prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		prop.put("mail.smtp.starttls.enable", true);

		mailSender.setJavaMailProperties(prop);

		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom("support@pax.us");
		message.setTo("crazywang@paxsz.com");
		message.setSubject("crazy");
		message.setText("just a test for crazy");
		sender.send(message);
	}
	
	@Test
	public void javaMailTest() throws Exception {
		String toStr = "crazywang@paxsz.com";
		Properties props = new Properties();
		props.put("mail.debug", "true");
		props.put("mail.smtp.auth", "true");
		props.put("mail.host", "smtp.office365.com");
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.starttls.enable", true);

		Session session = Session.getInstance(props);

		Message msg = new MimeMessage(session);
		msg.setSubject("JavaMail Test!!!");
		msg.setText("abcdefg crazy");
		msg.setFrom(new InternetAddress("support@pax.us"));

		Transport transport = session.getTransport();
		transport.connect("smtp.office365.com", 587, "broadpos@pax.us", "Adm49!e0");
		transport.sendMessage(msg, new Address[] { new InternetAddress(toStr) });
		transport.close();
	}
}
