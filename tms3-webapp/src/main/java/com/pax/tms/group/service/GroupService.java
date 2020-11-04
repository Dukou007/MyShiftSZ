/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description: // Detail description about the function of this module,		
 *             // interfaces with the other modules, and dependencies. 		
 * Revision History:		
 * Date	                 Author	                Action
 * 20170112	             Jaden           	    Modify
 * ============================================================================		
 */
package com.pax.tms.group.service;

import java.util.List;
import java.util.Map;

import com.pax.common.service.IBaseService;
import com.pax.common.web.form.BaseForm;
import com.pax.tms.group.GroupInfo;
import com.pax.tms.group.model.Group;
import com.pax.tms.group.web.form.AddGroupForm;
import com.pax.tms.group.web.form.CopyGroupForm;
import com.pax.tms.group.web.form.EditGroupForm;
import com.pax.tms.group.web.form.MoveGroupForm;
import com.pax.tms.open.api.rsp.AddGroupResponse;
import com.pax.tms.terminal.model.TerminalGroup;

public interface GroupService extends IBaseService<Group, Long> {

	AddGroupResponse save(AddGroupForm command);

	void edit(Long id, EditGroupForm command);

	void move(MoveGroupForm command);

	void copy(CopyGroupForm command);

	void delete(Long groupId, BaseForm command);
	
	void syncProfileToTerminals(Long groupId, BaseForm command);

	Group getGroupByNamePath(String namePath);

	List<String> searchGroupByNamePath(String keywords, Long userId);

	List<Group> getSelfAndDescendantGroup(Long groupId);

	List<Group> getSelfAndAncestorGroup(Long groupId);

	List<String> getTerminalTypesByGroupId(Long groupId);

	Long getTerminalNumbersByGroupId(Long groupId);

	List<TerminalGroup> getTerminalGroupsById(Long groupId);

	Group validateGroup(Long groupId);

	Group validateTargetGroup(Long targetGroupId);

	Group validateSourceGroup(Long sourceGroupId);

	List<Group> validateGroups(Long[] groupIds);

	List<Group> validateGroups(String[] groupNamePaths);

	List<Group> getEnterpriseGroups();

	void handleGroup(Group parent, Group group, BaseForm command);

	Group createGroup(GroupInfo rootGroupInfo, Group parent, BaseForm command);

	Group getExistGroup(Long groupId, String groupName);

	Group validateParentGroup(Long groupId);

	void checkPermissionOfGroup(BaseForm command, Long groupId);

	boolean isGroupHasTerminal(Long groupId,Map<Long, List<TerminalGroup>> groupTerminalMap);
	
	boolean isUserHasGroup(Long groupId, Long userId);

}
