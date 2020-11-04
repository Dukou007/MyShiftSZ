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
package com.pax.tms.group.dao;

import java.util.List;
import java.util.Map;

import org.hibernate.transform.ResultTransformer;

import com.pax.common.dao.IBaseDao;
import com.pax.tms.group.GroupInSearch;
import com.pax.tms.group.GroupOrTerminal;
import com.pax.tms.group.model.Group;
import com.pax.tms.group.model.GroupAncestor;
import com.pax.tms.terminal.model.Terminal;
import com.pax.tms.terminal.model.TerminalGroup;

public interface GroupDao extends IBaseDao<Group, Long> {

	boolean existGroupName(Long parentId, String name);

	Group getGroup(long parentId, String groupName);

	Group getGroupByNamePath(String namePath);
	
	List<String> getGroupNamesById(List<Long> groupIds);

	List<Group> getSelfAndDescendantGroup(Long groupId);

	List<Group> getSelfAndAncestorGroup(Long groupId);

	boolean isSelfOrDescendantGroup(long groupId, long descendantGroupId);

	void deleteGroupAncestor(long groupId);

	void updateGroupAncestor(Group sourceGroup, Group targetGroup);

	List<String> getTerminalTypesByGroupId(Long groupId);

	Long getTerminalNumbersByGroupId(Long groupId);

	List<TerminalGroup> getTerminalGroupsById(Long groupId);

	boolean existTerminalGroup(Long groupId, String tsn);

	void deleteGroupCascading(Long groupId);

	List<String> searchGroupByNamePath(String keywords, Long userId);

	void addChildGroupCount(long groupId, int increment);

	void minusChildGroupCount(long groupId, int decrease);

	List<Map<String, Object>> getUserTopGroups(long userId, int startPosition, int maxResult);

	int countUserTopGroups(long groupId);

	List<Map<String, Object>> getChildGroups(long groupId, int startPosition, int maxResult);

	List<Map<String, Object>> searchGroupTree(String keyword, long groupId, int startPosition, int maxResult);

	int countSearchGroupTree(String keyword, long groupId);

	List<Group> getEnterpriseGroups();
	List<Group> getEnterpriseAndRootGroups();

	Map<Long, List<Group>> getParentMappingChild();

	boolean isCreateByUpLevelUser(Long groupId, Long loginUserId);

	Group getExistGroup(Long groupId, String groupName);

	List<GroupOrTerminal> searchGroupOrTerminal(String keyword, Long loginUserId, String[] groupPathAndTsn);
	//只搜缩组
	List<GroupInSearch> searchGroup(String keyword, Long loginUserId);
	
	<T> List<T> searchGroupByGroupPath(long currentGroupId, String groupPath, Class<T> resultType,
			ResultTransformer resultTransformer);

	<T> List<T> getAllGroupNames(Class<T> resultType, ResultTransformer resultTransformer);

	<T> List<T> getSelfAndDescendantOrganizationNames(long currentGroupId, Class<T> resultType,
			ResultTransformer resultTransformer);

	<T> List<T> getUserGroups(long userId, Class<T> resultType, ResultTransformer resultTransformer);
	
	List<Long> getGrantedTopGroups(long userId);
	
	List<Long> getGrantedAllGroups(long userId);
	
	List<Long> getSelfAndDescendantGroupIds(Long groupId);

	Group findGroupByGroupPath(long currentGroupId, String groupPath);

	boolean isGroupHasTerminal(Long groupId);

    List<Group> getAllChildGroups(long groupId);
    
    List<Map<String, Object>> groupToMap(List<Group> groupList);
   
    Map<Long, List<TerminalGroup>> getGroupMappingTerminal();
   
    Map<Long, List<GroupAncestor>> getGroupMappingDescendantGroup();
   
    //更新某个组下的所有终端属性
    void syncProfileToTerminals(Long groupId, Terminal terminal);
    
    //根据ParentId查询所有该组下的所有子分组
    List<Group> getGroupsByParentId(Long parentId);

}