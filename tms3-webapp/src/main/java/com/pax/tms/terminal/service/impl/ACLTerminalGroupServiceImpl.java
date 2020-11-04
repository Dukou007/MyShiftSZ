package com.pax.tms.terminal.service.impl;

import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pax.common.dao.IBaseDao;
import com.pax.common.service.impl.BaseService;
import com.pax.tms.terminal.dao.ACLTerminalGroupDao;
import com.pax.tms.terminal.model.ACLTerminalGroup;
import com.pax.tms.terminal.service.ACLTerminalGroupService;

@Service("aclTerminalGroupServiceImpl")
public class ACLTerminalGroupServiceImpl extends BaseService<ACLTerminalGroup, Long>
		implements ACLTerminalGroupService {

	@Autowired
	private ACLTerminalGroupDao aclTerminalGroupDao;

	public ACLTerminalGroupDao getAclTerminalGroupDao() {
		return aclTerminalGroupDao;
	}

	public void setAclTerminalGroupDao(ACLTerminalGroupDao aclTerminalGroupDao) {
		this.aclTerminalGroupDao = aclTerminalGroupDao;
	}

	@Override
	public IBaseDao<ACLTerminalGroup, Long> getBaseDao() {

		return aclTerminalGroupDao;
	}

	@Override
	public void saveACLTerminalGroups(Collection<String> tsns, Long groupId) {

		if (CollectionUtils.isEmpty(tsns) || groupId == null) {
			return;
		}
		aclTerminalGroupDao.saveACLTerminalGroups(tsns, groupId);

	}

	@Override
	public void deleteACLTerminalGroups(Collection<String> tsns, Long groupId) {

		if (CollectionUtils.isEmpty(tsns) || groupId == null) {
			return;
		}
		aclTerminalGroupDao.deleteACLTerminalGroups(tsns, groupId);

	}

	@Override
	public void deleteACLTerminalGroupByUser(Collection<String> tsns, Long loginUserId) {

		if (CollectionUtils.isEmpty(tsns)) {
			return;
		}
		aclTerminalGroupDao.deleteACLTerminalGroupByUser(tsns, loginUserId);

	}

}
