package com.pax.tms.user.dao;

import com.pax.common.dao.IBaseDao;
import com.pax.tms.user.model.Role;

public interface RoleDao extends IBaseDao<Role, Long> {

	Role getRole(String roleName, String appName);

}
