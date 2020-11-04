package com.pax.tms.terminal.dao;

import java.util.Collection;

import com.pax.common.dao.IBaseDao;
import com.pax.tms.terminal.model.ACLTerminalGroup;

public interface ACLTerminalGroupDao extends IBaseDao<ACLTerminalGroup, Long> {

	void saveACLTerminalGroups(Collection<String> tsns, Long groupId);

	void deleteACLTerminalGroups(Collection<String> tsns, Long groupId);

	void deleteACLTerminalGroupByUser(Collection<String> tsns, Long loginUserId);
	
	

}
