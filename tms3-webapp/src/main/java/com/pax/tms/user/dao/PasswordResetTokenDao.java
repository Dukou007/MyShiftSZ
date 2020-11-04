package com.pax.tms.user.dao;

import com.pax.common.dao.IBaseDao;
import com.pax.tms.user.model.PasswordResetToken;

public interface PasswordResetTokenDao extends IBaseDao<PasswordResetToken, Long> {

	void deletePasswordResetToken(Long id);

}
