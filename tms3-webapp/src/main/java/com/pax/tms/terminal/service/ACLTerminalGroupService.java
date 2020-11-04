package com.pax.tms.terminal.service;

import java.util.Collection;

import com.pax.common.service.IBaseService;
import com.pax.tms.terminal.model.ACLTerminalGroup;

public interface ACLTerminalGroupService extends IBaseService<ACLTerminalGroup, Long> {

	void saveACLTerminalGroups(Collection<String> tsns, Long groupId);

	/*
	 * reomve terminal operator
	 * 
	 */
	void deleteACLTerminalGroups(Collection<String> tsns, Long groupId);
	
	/*
	 * delete terminal operator
	 * 
	 */
	void deleteACLTerminalGroupByUser(Collection<String> tsns,Long loginUserId);

}
