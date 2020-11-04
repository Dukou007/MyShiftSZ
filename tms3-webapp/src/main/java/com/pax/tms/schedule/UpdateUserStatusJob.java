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
package com.pax.tms.schedule;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.pax.common.exception.BusinessException;
import com.pax.common.util.DateTimeUtils;
import com.pax.tms.user.InactiveAccountMailSender;
import com.pax.tms.user.PasswordExpiredMailSender;
import com.pax.tms.user.model.PasswordResetToken;
import com.pax.tms.user.model.User;
import com.pax.tms.user.service.PasswordResetTokenService;
import com.pax.tms.user.service.UserService;

public class UpdateUserStatusJob extends QuartzJobBean implements Serializable {

	private static final long serialVersionUID = -8335935011071696277L;

	private static final Logger LOGGER = LoggerFactory.getLogger(UpdateUserStatusJob.class);

	private int inactiveUserLockDays = 30;
	private String inactiveUserLockAlertInDaysBefore = getInacUserLockAlertInDaysBefore();
	private int inactiveUserDeleteDays = 90;
	private int passwordExpirationDays = 28;
	private String passwordExpirationAlertInDaysBefore = getPwdExpirationAlertInDaysBefore();

	@Autowired
	private UserService userService;

	@Autowired
	private PasswordResetTokenService pwdTokenSvc;

	@Autowired(required = false)
	private InactiveAccountMailSender inactiveAccountMailSender;

	@Autowired(required = false)
	private PasswordExpiredMailSender passwordExpiredMailSender;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		long startTime = System.currentTimeMillis();
		try {
			LOGGER.info("Update user status start");
			alertInactiveUser();
			lockInactiveUser();
			deleteInactiveUser();

			alertPassowordExpiration();
			setPasswordExpired();
			LOGGER.info("Update user status completed, spend time: {} second(s), start time: {}",
					((System.currentTimeMillis() - startTime) + 999) / 1000, dateFormat.format(new Date(startTime)));
		} catch (Exception e) {
			LOGGER.info("Update user status failed, spend time: {} second(s), start time: {}",
					((System.currentTimeMillis() - startTime) + 999) / 1000, dateFormat.format(new Date(startTime)));
			LOGGER.error("Failed to update user status", e);
		}
	}

	private String getInacUserLockAlertInDaysBefore() {
		return "7";
	}

	private String getPwdExpirationAlertInDaysBefore() {
		return "7";
	}

	private List<Integer> getInactiveUserLockAlertInDaysBefore() {
		return paserNumberList(inactiveUserLockAlertInDaysBefore);
	}

	private List<Integer> getPasswordExpirationAlertInDaysBefore() {
		return paserNumberList(passwordExpirationAlertInDaysBefore);
	}

	private List<Integer> paserNumberList(String numberListStr) {
		if (StringUtils.isEmpty(numberListStr)) {
			return Collections.emptyList();
		}

		String[] arr = numberListStr.split(",");
		List<Integer> list = new ArrayList<Integer>(arr.length);
		for (String s : arr) {
			s = StringUtils.trimToNull(s);
			if (s != null) {
				list.add(Integer.parseInt(s));
			}
		}
		return list;
	}

	private void alertPassowordExpiration() {
		if (passwordExpiredMailSender == null) {
			return;
		}

		int expiredDays = passwordExpirationDays;
		if (expiredDays <= 0) {
			return;
		}
		try {
			LOGGER.info("start to alert user of password expiration time");
			List<Integer> alertInDaysBeforeList = getPasswordExpirationAlertInDaysBefore();
			for (Integer alertDays : alertInDaysBeforeList) {
				if (alertDays > 0) {
					alertPasswordExpirationInDaysBefore(expiredDays, alertDays);
				}
				LOGGER.info("finish to alert user of password expiration time");
			}
		} catch (Exception e) {
			LOGGER.error("failed to alert user of password expiration time", e);
		}
	}

	private void alertPasswordExpirationInDaysBefore(int expiredDays, int alertDays) {
		int days = expiredDays - alertDays;
		if (days <= 0) {
			return;
		}

		Date endLastChangeDate = DateTimeUtils.addDate(new Date(), "day", -days);
		Date startLastChangeDate = DateTimeUtils.addDate(endLastChangeDate, "day", -1);
		List<User> users = userService.getPasswordExpiredUsers(endLastChangeDate, startLastChangeDate);
		for (User user : users) {
			try {
				PasswordResetToken pwdToken = validatePwdToken(user.getId());
				passwordExpiredMailSender.sendPasswordExpiredEmail(user, pwdToken, expiredDays, alertDays);
			} catch (Exception e) {
				LOGGER.error("alert password expiration - failed to send email to ?", e, user.getUsername());
			}
		}
	}

	private PasswordResetToken validatePwdToken(Long userId) {
		PasswordResetToken pwdToke = pwdTokenSvc.get(userId);
		if (pwdToke == null) {
			throw new BusinessException("msg.passwordResetToken.notFound");
		}
		return pwdToke;
	}

	private void setPasswordExpired() {
		int expiredDays = passwordExpirationDays;
		if (expiredDays <= 0) {
			return;
		}
		Date lastChangeDate = DateTimeUtils.addDate(new Date(), "day", -expiredDays);
		try {
			LOGGER.info("start to set password expired");
			userService.setPasswordExpired(lastChangeDate);
			LOGGER.info("finish to set password expired");
		} catch (Exception e) {
			LOGGER.error("failed to set password expired", e);
		}
	}

	private void alertInactiveUserInDaysBefore(int lockDays, int alertDays) {
		int inactiveDays = lockDays - alertDays;
		if (inactiveDays <= 0) {
			return;
		}

		Date endLoginDate = DateTimeUtils.addDate(new Date(), "day", -inactiveDays);
		Date startLoginDate = DateTimeUtils.addDate(endLoginDate, "day", -1);
		List<User> inactiveUsers = userService.getInactiveUser(startLoginDate, endLoginDate);
		for (User user : inactiveUsers) {
			try {
				inactiveAccountMailSender.sendInactiveAccountEmail(user, inactiveDays, alertDays);
			} catch (Exception e) {
				LOGGER.error("alert inactive user - failed to send email to ?", e, user.getUsername());
			}
		}
	}

	private void alertInactiveUser() {
		if (inactiveAccountMailSender == null) {
			return;
		}

		int lockDays = inactiveUserLockDays;
		if (lockDays <= 0) {
			return;
		}
		try {
			LOGGER.info("start to alert inactive user");
			List<Integer> alertInDaysBeforeList = getInactiveUserLockAlertInDaysBefore();
			for (Integer alertDays : alertInDaysBeforeList) {
				if (alertDays > 0) {
					alertInactiveUserInDaysBefore(lockDays, alertDays);
				}
			}
			LOGGER.info("finish to alert inactive user");
		} catch (Exception e) {
			LOGGER.error("failed to alert inactive user", e);
		}
	}

	private void lockInactiveUser() {
		int lockDays = inactiveUserLockDays;
		if (lockDays <= 0) {
			return;
		}

		Date lastLoginDate = DateTimeUtils.addDate(new Date(), "day", -lockDays);
		try {
			LOGGER.info("start to lock inactive user");
			userService.lockInactiveUsers(lastLoginDate);
			LOGGER.info("finish to lock inactive user");
		} catch (Exception e) {
			LOGGER.error("failed to lock inactive user", e);
		}
	}

	private void deleteInactiveUser() {
		int deleteDays = inactiveUserDeleteDays;
		if (deleteDays <= 0) {
			return;
		}

		Date lastLoginDate = DateTimeUtils.addDate(new Date(), "day", -deleteDays);
		try {
			LOGGER.info("[Delete] start to Delete inactive user");
			userService.deleteDisabledInactiveUser(lastLoginDate);
			LOGGER.info("[Delete] finish to Delete inactive user");
		} catch (Exception e) {
			LOGGER.error("[Delete] failed to Delete inactive user", e);
		}
	}

	@Autowired
	public void setInactiveUserLockDays(@Value("${tms.user.inactiveUser.lock.days:0}") int inactiveUserLockDays) {
		this.inactiveUserLockDays = inactiveUserLockDays;
	}

	@Autowired
	public void setInactiveUserLockAlertInDaysBefore(
			@Value("${tms.user.inactiveUser.lock.warningDays:}") String inactiveUserLockAlertInDaysBefore) {
		this.inactiveUserLockAlertInDaysBefore = inactiveUserLockAlertInDaysBefore;
	}

	@Autowired
	public void setInactiveUserDeleteDays(@Value("${tms.user.inactiveUser.delete.days:0}") int inactiveUserDeleteDays) {
		this.inactiveUserDeleteDays = inactiveUserDeleteDays;
	}

	@Autowired
	public void setPasswordExpirationDays(@Value("${tms.user.passwordExpiration.days:0}") int passwordExpirationDays) {
		this.passwordExpirationDays = passwordExpirationDays;
	}

	@Autowired
	public void setPasswordExpirationAlertInDaysBefore(
			@Value("${tms.user.passwordExpiration.warningDays:}") String passwordExpirationAlertInDaysBefore) {
		this.passwordExpirationAlertInDaysBefore = passwordExpirationAlertInDaysBefore;
	}

}
