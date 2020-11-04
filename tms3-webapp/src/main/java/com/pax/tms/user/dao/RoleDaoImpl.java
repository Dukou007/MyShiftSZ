package com.pax.tms.user.dao;

import org.springframework.stereotype.Repository;

import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.tms.user.model.Role;

@Repository("roleDaoImpl")
public class RoleDaoImpl extends BaseHibernateDao<Role, Long> implements RoleDao {

	@Override
	public Role getRole(String roleName, String appName) {
	
		String sql="select * from pubtrole where UPPER(ROLE_NAME)=:roleName and UPPER(APP_NAME)=:appName";
		return uniqueResult(createNativeQuery(sql, Role.class, true).setParameter("roleName", roleName).setParameter("appName", appName));
	
		
	}

}
