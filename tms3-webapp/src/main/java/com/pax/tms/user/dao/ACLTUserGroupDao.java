package com.pax.tms.user.dao;

import java.util.List;

import com.pax.common.dao.IBaseDao;
import com.pax.tms.user.model.ACLTUserGroup;

public interface ACLTUserGroupDao extends IBaseDao<ACLTUserGroup, Long> {

	void saveACLUserGroups(Long loginUserId, Long groupId);

	void deleteACLUserGroups(Long loginUserId, Long groupId);

	void deleteACLUserGroups(Long userId);
	
	List<Long> getGroupsByUserId(Long userId);

}
