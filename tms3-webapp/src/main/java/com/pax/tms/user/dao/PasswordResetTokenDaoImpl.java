package com.pax.tms.user.dao;

import org.springframework.stereotype.Repository;

import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.tms.user.model.PasswordResetToken;

@Repository("pwdTokenDaoImpl")
public class PasswordResetTokenDaoImpl extends BaseHibernateDao<PasswordResetToken, Long>
		implements PasswordResetTokenDao {

	@Override
	public void deletePasswordResetToken(Long userId) {
		String hql = "delete from PasswordResetToken where userId=:userId";
		createQuery(hql).setParameter("userId", userId).executeUpdate();
	}

}
