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
package com.pax.tms.group.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pax.common.dao.IBaseDao;
import com.pax.common.exception.BusinessException;
import com.pax.common.pagination.Page;
import com.pax.common.service.impl.BaseService;
import com.pax.tms.group.GroupInSearch;
import com.pax.tms.group.GroupOrTerminal;
import com.pax.tms.group.GroupTreePage;
import com.pax.tms.group.dao.GroupDao;
import com.pax.tms.group.model.Group;
import com.pax.tms.group.service.GroupService;
import com.pax.tms.group.service.GroupTreeService;
import com.pax.tms.group.web.form.QueryGroupForm;
import com.pax.tms.user.security.AclManager;
import com.pax.tms.user.service.UserGroupService;

@Service("groupTreeServiceImpl")
public class GroupTreeServiceImpl extends BaseService<Group, Long> implements GroupTreeService {

	@Autowired
	private GroupDao groupDao;

	@Autowired
	private UserGroupService userGroupService;

	@Autowired
	private GroupService groupService;

	@Override
	public IBaseDao<Group, Long> getBaseDao() {
		return groupDao;
	}

	@Override
	public GroupTreePage<Map<String, Object>> getUserTopGroups(QueryGroupForm command) {
		int index = command.getPageIndex();
		int size = command.getPageSize();
		long totalCount = groupDao.countUserTopGroups(command.getLoginUserId());

		int startPosition = Page.getPageStart(totalCount, index, size);
		int maxResult = size;
		List<Map<String, Object>> items = groupDao.getUserTopGroups(command.getLoginUserId(), startPosition, maxResult);
		GroupTreePage<Map<String, Object>> page = GroupTreePage.getGroupTreePage(index, size, totalCount, items);
		page.setActiveGroup(!items.isEmpty() ? (Long) items.get(0).get("id") : null);
		return page;
	}

	@Override
	public GroupTreePage<Map<String, Object>> getGroupContext(QueryGroupForm command) {
		Long groupId = command.getGroupId();
		if (groupId == null) {
			groupId = userGroupService.getFavoriteGroup(command.getLoginUserId());
		}
		Map<Long, List<Group>> parentGroupMap = groupDao.getParentMappingChild();
		if (groupId == null) {
			if (command.isLoadAll()) {
				GroupTreePage<Map<String, Object>> page = getUserTopGroups(command);
				loadDescantGroup(command, page.getItems(),parentGroupMap);
				return page;
			}
			return getUserTopGroups(command);
		}

		Group group = groupService.validateGroup(groupId);
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);

		List<Long> idPathArrayList = group.getIdPathArrayList();
		GroupTreePage<Map<String, Object>> page = getUserTopGroups(command);
		page.setActiveGroup(group.getId());
		
		if (command.isLoadAll()) {
			loadDescantGroup(command, page.getItems(),parentGroupMap);
		} else {
			loadChildGroup(idPathArrayList.iterator(), page.getItems(), command,parentGroupMap);
		}
		return page;
	}

	private void loadChildGroup(Iterator<Long> iterator, List<Map<String, Object>> items, QueryGroupForm command, Map<Long, List<Group>> parentGroupMap) {
		while (iterator.hasNext()) {
			Long groupId = iterator.next();
			for (Map<String, Object> group : items) {
                if (groupId.equals(group.get("id")) && parentGroupMap.containsKey(groupId)) {
                    int index = command.getPageIndex();
                    // int size = command.getPageSize();
                    int size = 2000;
                    long totalCount = ((Number) group.get("subCount")).longValue();
                    
//                    int startPosition = Page.getPageStart(totalCount, index, size);
//                    int maxResult = size;
//                    List<Map<String, Object>> childItems = groupDao.getChildGroups(groupId, startPosition, maxResult);
                    List<Group> childGroupList = parentGroupMap.get(groupId);
                    List<Map<String, Object>> childItems = groupDao.groupToMap(childGroupList);
                    Page<Map<String, Object>> page = Page.getPage(index, size, totalCount, childItems);
                    group.put("subgroup", page);
                    loadChildGroup(iterator, childItems, command,parentGroupMap);
                    break;
                }
			}
		}
	}

	@Override
	public Page<Map<String, Object>> getSubgroup(QueryGroupForm command) {
		Long groupId = command.getGroupId();
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		Group group = groupService.validateGroup(groupId);
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);

		int index = command.getPageIndex();
		// int size = command.getPageSize();
		int size = 2000;
		long totalCount = group.getSubCount();

		int startPosition = Page.getPageStart(totalCount, index, size);
		int maxResult = size;
		List<Map<String, Object>> childItems = groupDao.getChildGroups(groupId, startPosition, maxResult);
		Page<Map<String, Object>> page = Page.getPage(index, size, totalCount, childItems);
		return page;
	}

	@Override
	public Page<Map<String, Object>> getDescantGroup(QueryGroupForm command) {
		Long groupId = command.getGroupId();
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		Group group = groupService.validateGroup(groupId);
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);

		int index = command.getPageIndex();
		int size = 2000;
		long totalCount = group.getSubCount();

		int startPosition = Page.getPageStart(totalCount, index, size);
		int maxResult = size;
		Map<String, Object> rootMap = new HashMap<String, Object>();
		rootMap.put("id", group.getId());
		rootMap.put("name", group.getName());
		rootMap.put("treeDepth", group.getTreeDepth());
		rootMap.put("idPath", group.getIdPath());
		rootMap.put("namePath", group.getNamePath());
		rootMap.put("subCount", totalCount);
		List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
		items.add(rootMap);
		GroupTreePage<Map<String, Object>> page = GroupTreePage.getGroupTreePage(startPosition, maxResult, totalCount,
				items);
		page.setActiveGroup(groupId);
		Map<Long, List<Group>> parentGroupMap = groupDao.getParentMappingChild();
		loadDescantGroup(command, page.getItems(),parentGroupMap);

		return page;
	}

	private void loadDescantGroup(QueryGroupForm command, List<Map<String, Object>> items,Map<Long, List<Group>> parentGroupMap) {
		for (Map<String, Object> group : items) {
			int index = command.getPageIndex();
			int size = 2000;
			long totalCount = ((Number) group.get("subCount")).longValue();
			Long groupId = (Long) group.get("id");

//			int startPosition = Page.getPageStart(totalCount, index, size);
//			int maxResult = size;
//			List<Map<String, Object>> childItems = groupDao.getChildGroups(groupId, startPosition, maxResult);
			List<Group> childGroupList = parentGroupMap.get(groupId);
			List<Map<String, Object>> childItems = groupDao.groupToMap(childGroupList);
			Page<Map<String, Object>> page = Page.getPage(index, size, totalCount, childItems);
			group.put("subgroup", page);
			if (command.isLoadAll()) {
				loadDescantGroup(command, childItems,parentGroupMap);
			}
		}

	}

	@Override
	public Page<Map<String, Object>> searchGroupTree(QueryGroupForm command) {

		if (command.getGroupId() == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		Group group = groupService.validateGroup(command.getGroupId());
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);

		int index = command.getPageIndex();
		// int size = command.getPageSize();
		int size = 2000;
		long totalCount = groupDao.countSearchGroupTree(command.getKeyword(), command.getGroupId());

		int startPosition = Page.getPageStart(totalCount, index, size);
		int maxResult = size;
		List<Map<String, Object>> items = groupDao.searchGroupTree(command.getKeyword(), command.getGroupId(),
				startPosition, maxResult);
		Page<Map<String, Object>> page = Page.getPage(index, size, totalCount, items);
		return page;
	}

	@Override
	public List<GroupOrTerminal> searchGroupOrTerminal(QueryGroupForm command) {
		String keyword = command.getKeyword();
		if (StringUtils.isEmpty(keyword)) {
			return Collections.emptyList();
		}
		String[] groupPathAndTsn = getGroupPathAndTsn(keyword);

		return groupDao.searchGroupOrTerminal(keyword, command.getLoginUserId(), groupPathAndTsn);
	}
	
	@Override
	public List<GroupInSearch> searchGroup(QueryGroupForm command) {
		String keyword = command.getKeyword();
		return groupDao.searchGroup(keyword, command.getLoginUserId());
	}

	private String[] getGroupPathAndTsn(String keyword) {

		int index = keyword.lastIndexOf("/");
		if (index == -1) {
			return new String[0];
		}
		String[] groupPathAndTsn = new String[2];
		groupPathAndTsn[0] = keyword.substring(0, index);
		groupPathAndTsn[1] = keyword.substring(index + 1, keyword.length());
		return groupPathAndTsn;
	}

}
