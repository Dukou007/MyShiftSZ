package com.pax.tms.user.service;

import com.pax.common.service.IBaseService;
import com.pax.tms.user.model.Role;

public interface RoleService extends IBaseService<Role, Long>{

	Role getRole(String roleName, String appName);

}
