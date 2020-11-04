package com.pax.tms.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pax.common.dao.IBaseDao;
import com.pax.common.service.impl.BaseService;
import com.pax.tms.user.dao.ACLTUserGroupDao;
import com.pax.tms.user.model.ACLTUserGroup;

@Service("aclTUserGroupServiceImpl")
public class ACLTUserGroupServiceImpl extends BaseService<ACLTUserGroup, Long> implements ACLTUserGroupService {

	@Autowired
	private ACLTUserGroupDao aclTUserGroupDao;

	@Override
	public IBaseDao<ACLTUserGroup, Long> getBaseDao() {

		return aclTUserGroupDao;
	}

	@Override
	public void saveACLUserGroups(Long loginUserId, Long groupId) {

		if (loginUserId == null || groupId == null) {
			return;
		}
		aclTUserGroupDao.saveACLUserGroups(loginUserId, groupId);

	}

	@Override
	public void deleteACLUserGroups(Long loginUserId, Long groupId) {

		if (loginUserId == null || groupId == null) {
			return;
		}
		aclTUserGroupDao.deleteACLUserGroups(loginUserId, groupId);
	}

	@Override
	public void deleteACLUserGroups(Long userId) {

		if (userId == null) {
			return;
		}
		aclTUserGroupDao.deleteACLUserGroups(userId);

	}

}
