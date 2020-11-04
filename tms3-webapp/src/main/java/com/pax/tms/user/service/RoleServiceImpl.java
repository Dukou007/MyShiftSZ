package com.pax.tms.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pax.common.dao.IBaseDao;
import com.pax.common.service.impl.BaseService;
import com.pax.tms.user.dao.RoleDao;
import com.pax.tms.user.model.Role;

@Service("roleServiceImpl")
public class RoleServiceImpl extends BaseService<Role, Long> implements RoleService {

	@Autowired
	private RoleDao roleDao;

	@Override
	public IBaseDao<Role, Long> getBaseDao() {

		return roleDao;
	}

	@Override
	public Role getRole(String roleName, String appName) {
		
		return roleDao.getRole(roleName, appName);
	}

}
