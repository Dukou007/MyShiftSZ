package com.pax.tms.user.service;

import com.pax.common.service.IBaseService;
import com.pax.tms.user.model.ACLTUserGroup;

public interface ACLTUserGroupService extends IBaseService<ACLTUserGroup, Long> {

	void saveACLUserGroups(Long loginUserId, Long groupId);

	void deleteACLUserGroups(Long loginUserId, Long groupId);

	void deleteACLUserGroups(Long userId);

}
