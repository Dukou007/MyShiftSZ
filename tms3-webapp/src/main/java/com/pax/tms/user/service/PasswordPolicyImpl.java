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
package com.pax.tms.user.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.pax.common.exception.BusinessException;
import com.pax.common.security.HashedCredentials;
import com.pax.tms.user.dao.PasswordHistoryDao;
import com.pax.tms.user.model.PasswordHistory;
import com.pax.tms.user.model.User;

@Component("passwordPolicyImpl")
public class PasswordPolicyImpl implements PasswordPolicy {

	@Autowired
	private PasswordHistoryDao passwordHistoryDao;

	private int compareToOldPasswordTimes = 13;

	@Override
	public void validateNewPassword(String newPassword, User user,String oldPwdPreserveNumber) {
		if (StringUtils.isNotEmpty(user.getPassword())) {
			String digestedPassword = HashedCredentials.encodedPassword(newPassword, user.getPwdEncAlg(),
					HashedCredentials.getStatcSalt(), user.getPwdEncSalt(), user.getPwdEncIt());
			if (user.getPassword().equals(digestedPassword)) {
				throw new BusinessException("msg.user.cannotEqaulToOldPassword");
			}
		}

		// check user's password can not same as before 13 times
		int lastPwdTimes = compareToOldPasswordTimes - 1;
		List<PasswordHistory> lastPasswordsList = passwordHistoryDao.getLastPasswords(user.getId(), lastPwdTimes);
		for (PasswordHistory ph : lastPasswordsList) {
			if (StringUtils.isEmpty(ph.getPassword())) {
				continue;
			}
			String digestedPassword = HashedCredentials.encodedPassword(newPassword, ph.getPwdEncAlg(),
					HashedCredentials.getStatcSalt(), ph.getPwdEncSalt(), ph.getPwdEncIt());
			if (ph.getPassword().equals(digestedPassword)) {
				throw new BusinessException("msg.user.cannotEqaulToLastPasswords",new String[] { oldPwdPreserveNumber });
			}
		}
	}

	@Autowired
	public void setCompareToOldPasswordTimes(
			@Value("${tms.user.oldPassword.preserveNumber:13}") int compareToOldPasswords) {
		this.compareToOldPasswordTimes = compareToOldPasswords;
	}
}
