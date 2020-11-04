package com.pax.tms.user.dev;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pax.common.exception.BusinessException;
import com.pax.common.util.DateTimeUtils;
import com.pax.tms.demo.ServiceJunitCase;
import com.pax.tms.user.CreateUserMailSender;
import com.pax.tms.user.ForgetPasswordMailSender;
import com.pax.tms.user.InactiveAccountMailSender;
import com.pax.tms.user.PasswordExpiredMailSender;
import com.pax.tms.user.ResetPasswordMailSender;
import com.pax.tms.user.model.PasswordResetToken;
import com.pax.tms.user.model.User;
import com.pax.tms.user.service.PasswordResetTokenService;
import com.pax.tms.user.service.UserService;

public class SendEmailTest extends ServiceJunitCase {

	@Autowired(required = false)
	private ResetPasswordMailSender resetPasswordEmailSendExecutor;

	@Autowired(required = false)
	private CreateUserMailSender createUserMailSender;

	@Autowired
	private UserService userSvc;

	@Autowired
	private PasswordResetTokenService pwdTokenSvc;

	@Autowired(required = false)
	private ForgetPasswordMailSender forgetPasswordMailSender;

	@Autowired(required = false)
	private InactiveAccountMailSender inactiveAccountMailSender;

	@Autowired(required = false)
	private PasswordExpiredMailSender passwordExpiredMailSender;

	private String fullname = "Carry Wang";
	private String username = "carry.w";
	private String email = "alan.j@paxdata.com";

	@Test
	public void testInactiveAccountEmail() {
		if (inactiveAccountMailSender == null) {
			return;
		}
		User user = new User();
		user.setFullname(fullname);
		user.setUsername(username);
		user.setEmail(email);
		inactiveAccountMailSender.sendInactiveAccountEmail(user, 25, 5);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testPasswordExpiredMailSenderEmail() {
		if (passwordExpiredMailSender == null) {
			return;
		}
		User user = new User();
		user.setFullname(fullname);
		user.setUsername(username);
		user.setEmail(email);
		PasswordResetToken pwdToken = validatePwdToken(1036L);
		passwordExpiredMailSender.sendPasswordExpiredEmail(user, pwdToken, 28, 7);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void alertPassowordExpiration() {
		if (passwordExpiredMailSender == null) {
			return;
		}
		Date endLoginDate = DateTimeUtils.addDate(new Date(), "day", -23);
		Date startLoginDate = DateTimeUtils.addDate(endLoginDate, "day", -1);
		List<User> inactiveUsers = userSvc.getInactiveUser(startLoginDate, endLoginDate);
		for (User user : inactiveUsers) {
			PasswordResetToken pwdToken = validatePwdToken(user.getId());
			passwordExpiredMailSender.sendPasswordExpiredEmail(user, pwdToken, 28, 7);
		}
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testForgetPasswordEmail() {
		if (forgetPasswordMailSender == null) {
			return;
		}
		String resetToken = UUID.randomUUID().toString();
		User user = new User();
		PasswordResetToken pwdToken = new PasswordResetToken();
		user.setFullname(fullname);
		user.setUsername(username);
		pwdToken.setResetPwdToken(resetToken);
		user.setEmail(email);
		forgetPasswordMailSender.sendForgetPasswordEmail(user, pwdToken);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSendResetPasswordEmail() {
		if (resetPasswordEmailSendExecutor == null) {
			return;
		}
		String resetToken = UUID.randomUUID().toString();
		User user = new User();
		PasswordResetToken pwdToken = new PasswordResetToken();
		user.setFullname(fullname);
		user.setUsername(username);
		pwdToken.setResetPwdToken(resetToken);
		user.setEmail(email);
		resetPasswordEmailSendExecutor.sendResetPasswordEmail(user, pwdToken);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSendWelcomeEmail() {
		if (createUserMailSender == null) {
			return;
		}
		String resetToken = UUID.randomUUID().toString();
		User user = new User();
		PasswordResetToken pwdToken = new PasswordResetToken();
		user.setFullname(fullname);
		user.setUsername(username);
		pwdToken.setResetPwdToken(resetToken);
		user.setEmail(email);
		createUserMailSender.sendWelcomeEmail(user,pwdToken);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testLockInactiveUser() {

		Date lastLoginDate = DateTimeUtils.addDate(new Date(), "day", -30);
		try {
			System.out.println("start to lock inactive user");
			userSvc.lockInactiveUsers(lastLoginDate);
			System.out.println("finish to lock inactive user");
		} catch (Exception e) {
			System.out.println("failed to lock inactive user");
		}
	}

	@Test
	public void testDeleteInactiveUser() {

		Date lastLoginDate = DateTimeUtils.addDate(new Date(), "day", -60);
		try {
			System.out.println("start to Delete inactive user");
			userSvc.deleteDisabledInactiveUser(lastLoginDate);
			System.out.println("finish to Delete inactive user");
		} catch (Exception e) {
			System.out.println("failed to Delete inactive user");
		}
	}

	@Test
	public void testAlertPassowordExpiration() {

		Date endLastChangeDate = DateTimeUtils.addDate(new Date(), "day", -21);
		Date startLastChangeDate = DateTimeUtils.addDate(endLastChangeDate, "day", -1);
		System.out.println("-----endLastChangeDate=" + endLastChangeDate + "         ----startLastChangeDate="
				+ startLastChangeDate);
		List<User> users = userSvc.getPasswordExpiredUsers(endLastChangeDate, startLastChangeDate);
		System.out.println("--size=====" + users.size());
		for (User user : users) {
			PasswordResetToken pwdToken = validatePwdToken(user.getId());
			passwordExpiredMailSender.sendPasswordExpiredEmail(user, pwdToken, 28, 7);
		}
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private PasswordResetToken validatePwdToken(Long userId) {
		PasswordResetToken pwdToke = pwdTokenSvc.get(userId);
		if (pwdToke == null) {
			throw new BusinessException("msg.passwordResetToken.notFound");
		}
		return pwdToke;
	}

	@Test
	public void testSetPasswordExpired() {

		Date lastChangeDate = DateTimeUtils.addDate(new Date(), "day", -28);
		try {
			System.out.println("start to Delete inactive user");
			userSvc.setPasswordExpired(lastChangeDate);
			System.out.println("finish to Delete inactive user");
		} catch (Exception e) {
			System.out.println("failed to Delete inactive user");
		}
	}

	public static void main(String[] args) {
		Date endLastChangeDate = DateTimeUtils.addDate(new Date(), "day", -21);
		Date startLastChangeDate = DateTimeUtils.addDate(endLastChangeDate, "day", -1);
		System.out.println("-------endLastChangeDate---" + endLastChangeDate + "   --------startLastChangeDate=="
				+ startLastChangeDate);
	}
}
