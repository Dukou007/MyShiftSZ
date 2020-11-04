package com.pax.tms.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pax.common.dao.IBaseDao;
import com.pax.common.service.impl.BaseService;
import com.pax.tms.user.dao.PasswordResetTokenDao;
import com.pax.tms.user.model.PasswordResetToken;

@Service("pwdTokenServiceImpl")
public class PasswordResetTokenServiceImpl extends BaseService<PasswordResetToken, Long>
		implements PasswordResetTokenService {

	@Autowired
	private PasswordResetTokenDao pwdTokenDao;

	@Override
	public IBaseDao<PasswordResetToken, Long> getBaseDao() {
		return pwdTokenDao;
	}

	@Override
	public List<PasswordResetToken> list() {
		return pwdTokenDao.list();
	}
}
