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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import com.pax.common.dao.DbHelper;
import com.pax.common.dao.IBaseDao;
import com.pax.common.exception.BusinessException;
import com.pax.common.service.impl.BaseService;
import com.pax.common.web.form.BaseForm;
import com.pax.tms.deploy.service.DeployParaService;
import com.pax.tms.deploy.service.GroupDeployService;
import com.pax.tms.deploy.service.TerminalDeployService;
import com.pax.tms.group.GroupInfo;
import com.pax.tms.group.dao.GroupAncestorDao;
import com.pax.tms.group.dao.GroupDao;
import com.pax.tms.group.model.Group;
import com.pax.tms.group.model.GroupAncestor;
import com.pax.tms.group.service.GroupService;
import com.pax.tms.group.service.UsageThresholdService;
import com.pax.tms.group.web.form.AddGroupForm;
import com.pax.tms.group.web.form.CopyGroupForm;
import com.pax.tms.group.web.form.EditGroupForm;
import com.pax.tms.group.web.form.MoveGroupForm;
import com.pax.tms.location.service.AddressInfo;
import com.pax.tms.location.service.AddressService;
import com.pax.tms.monitor.service.AlertConditionService;
import com.pax.tms.monitor.service.EventGrpService;
import com.pax.tms.monitor.web.form.OperatorEventForm;
import com.pax.tms.open.api.rsp.AddGroupResponse;
import com.pax.tms.pub.web.form.RedisOperatorForm;
import com.pax.tms.report.dao.TerminalDownloadDao;
import com.pax.tms.res.dao.PkgDao;
import com.pax.tms.res.dao.PkgGroupDao;
import com.pax.tms.res.model.Pkg;
import com.pax.tms.res.service.PkgService;
import com.pax.tms.terminal.dao.TerminalDao;
import com.pax.tms.terminal.dao.TerminalGroupDao;
import com.pax.tms.terminal.model.Terminal;
import com.pax.tms.terminal.model.TerminalGroup;
import com.pax.tms.terminal.service.TerminalService;
import com.pax.tms.user.dao.UserGroupDao;
import com.pax.tms.user.model.User;
import com.pax.tms.user.security.AclManager;
import com.pax.tms.user.service.ACLTUserGroupService;
import com.pax.tms.user.service.AuditLogService;
import com.pax.tms.user.service.UserGroupService;
import com.pax.tms.user.service.UserService;
import com.pax.tms.user.web.form.OperatorLogForm;

@Service("groupServiceImpl")
public class GroupServiceImpl extends BaseService<Group, Long> implements GroupService {

	@Autowired
	private GroupDao groupDao;
	
	@Autowired
	GroupAncestorDao groupAncestorDao;

	@Autowired
	private AddressService addressService;

	@Autowired
	private TerminalService terminalService;

	@Autowired
	private UserService userService;

	@Autowired
	private TerminalGroupDao terminalGroupDao;

	@Autowired
	private TerminalDao terminalDao;

	@Autowired
	private PkgDao pkgDao;

	@Autowired
	private PkgGroupDao pkgGroupDao;

	@Autowired
	private AuditLogService auditLogService;

	@Autowired
	private EventGrpService eventService;
	@Autowired
	private GroupDeployService groupDeployService;

	@Autowired
	private PkgService pkgService;

	@Autowired
	private UsageThresholdService usageThresholdService;

	@Autowired
	private AlertConditionService alertConditionService;
	@Autowired
	private DeployParaService deployParaService;

	@Autowired
	private TerminalDeployService terminalDeployService;

	@Autowired
	private TerminalDownloadDao terminalDownloadDao;

	@Autowired
	private UserGroupService userGroupService;

	@Autowired
	private ACLTUserGroupService aclTUserGroupService;

	@Autowired
	private AlertConditionService condService;

	@Autowired
	private GroupService groupService;
	
	@Autowired
	private UserGroupDao userGroupDao;

	@Override
	public IBaseDao<Group, Long> getBaseDao() {
		return groupDao;
	}

	public User getUserInfo(BaseForm command) {
		Long userId = command.getLoginUserId();
		User user = userService.get(userId);
		if (user == null) {
			throw new BusinessException("msg.user.notFound");
		}
		return user;
	}

	@Override
	public AddGroupResponse save(AddGroupForm command) {
		validateInput(command);

		Group parent = validateParentGroup(command.getParentId());
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), parent);

		AddressInfo addressInfo = addressService.checkAddress(command);

		/*** business rules ***/
		// check parentId, name unique
		if (groupDao.existGroupName(command.getParentId(), command.getName())) {
			throw new BusinessException("msg.group.groupNameExist");
		}

		/*** save to database ***/
		Group group = createGroup(command, parent);
		group.setAddressInfo(addressInfo);
		groupDao.save(group);

		Long id = group.getId();
		group.setIdPath(parent.getIdPath() + "/" + id);
		groupDao.update(group);
		addGroupAnscetor(group);

		groupDao.flush();
		groupDao.addChildGroupCount(parent.getId(), 1);

		if (group.isEnterPriceGroup()) {
			usageThresholdService.saveUsageThreshold(command, group);
		}
		alertConditionService.addAlertCondition(group, command);
		aclTUserGroupService.saveACLUserGroups(command.getLoginUserId(), group.getId());
		// add audit log
		auditLogService.addAuditLog(Arrays.asList(group.getNamePath()), command, OperatorLogForm.ADD_GROUP, null);
		eventService.addEventLog(group.getId(), OperatorEventForm.ADD_GROUP + ":" + group.getNamePath());

		AddGroupResponse response = createAddGroupResponse(command, group);
		return response;

	}

	private AddGroupResponse createAddGroupResponse(AddGroupForm command, Group group) {
		AddGroupResponse response = new AddGroupResponse();
		response.setGroupId(group.getId());
		response.setGroupName(group.getName());
		response.setGroupId(group.getId());
		response.setIdPath(group.getIdPath());
		response.setNamePath(group.getNamePath());
		response.setTimeZone(command.getTimeZone());
		response.setDaylightSaving(command.isDaylightSaving());
		response.setCountryName(command.getCountryName());
		response.setStateProvinceName(command.getProvinceName());
		response.setCityName(command.getCityName());
		response.setZipCode(command.getZipCode());
		response.setAddress(command.getAddress());
		response.setDescription(group.getDescription());
		response.setCreatedBy(command.getLoginUsername());
		response.setCreatedOn(command.getRequestTime());
		response.setUpdatedBy(command.getLoginUsername());
		response.setUpdatedOn(command.getRequestTime());
		return response;
	}

	private Group createGroup(AddGroupForm command, Group parent) {
		Group group = new Group();
		group.setCode(UUID.randomUUID().toString());
		group.setName(command.getName());
		group.setParent(parent);
		group.setTreeDepth(getTreeDepth(parent));
		group.setNamePath(getNamePath(parent, command.getName()));
		group.setDescription(command.getDescription());
		group.setTimeZone(command.getTimeZone());
		group.setDaylightSaving(command.isDaylightSaving());
		group.setCreator(command.getLoginUsername());
		group.setCreateDate(command.getRequestTime());
		group.setModifier(command.getLoginUsername());
		group.setModifyDate(command.getRequestTime());
		return group;
	}

	private int getTreeDepth(Group parent) {
		return parent.getTreeDepth() + 1;
	}

	private String getNamePath(Group parent, String groupName) {
		if (parent.getNamePath() == null) {
			return groupName;
		}
		String namePath = parent.getNamePath() + "/" + groupName;
		if (namePath.length() > Group.GROUP_PATH_MAX_LENGTH) {
			throw new BusinessException("groupPath.overLength", new String[] { namePath });
		}

		return namePath;
	}

	private void validateInput(AddGroupForm command) {
		/*** input validation ***/
		if (command.getParentId() == null) {
			throw new BusinessException("msg.group.parentRequired");
		}
		if (StringUtils.isEmpty(command.getTimeZone())) {
			throw new BusinessException("msg.timeZone.required");
		}
		Group.validateGroupName(command.getName());
	}

	@Override
	public void edit(Long id, EditGroupForm command) {
		/*** input validation ***/
		validateInput(id, command);

		Group group = validateGroup(id);
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);

		AddressInfo addressInfo = addressService.checkAddress(command);

		/*** business rules ***/
		String oldName = group.getName();

		if ((!oldName.equalsIgnoreCase(command.getName()))
				&& groupDao.existGroupName(command.getParentId(), command.getName())) {
			throw new BusinessException("msg.group.groupNameExist");
		}
		String oldNamePath = group.getNamePath();
		group.setName(command.getName());
		group.setNamePath(updateGroupNamePath(group.getNamePath(), group.getName()));
		group.setDescription(command.getDescription());
		group.setTimeZone(command.getTimeZone());
		group.setDaylightSaving(command.isDaylightSaving());
		group.setAddressInfo(addressInfo);

		group.setModifier(command.getLoginUsername());
		group.setModifyDate(command.getRequestTime());

		updateDescantGroupNamePath(group, oldNamePath);

		// add audit log

		auditLogService.addAuditLog(Arrays.asList(group.getNamePath()), command, OperatorLogForm.EDIT_GROUP, null);
		eventService.addEventLog(group.getId(), OperatorEventForm.EDIT_GROUP + ":" + group.getNamePath());
	}

	private void updateDescantGroupNamePath(Group sourceGroup, String oldNamePath) {
		List<Group> selfAndDescantGroups = getSelfAndDescendantGroup(sourceGroup.getId());
		if (CollectionUtils.isNotEmpty(selfAndDescantGroups)) {
			for (Group group : selfAndDescantGroups) {
				// target Group 先不做任何处理
				if (group.getId() == sourceGroup.getId()) {
					continue;
				}
				String newNamePath = replaceGroupNamePath(group, sourceGroup.getNamePath(), oldNamePath);
				group.setNamePath(newNamePath);
			}
		}

	}

	private String replaceGroupNamePath(Group group, String replaceNamePath, String oldNamePath) {

		String namePath = group.getNamePath();
		String newNamePath = namePath.replace(oldNamePath, replaceNamePath);
		return newNamePath;
	}

	private void validateInput(Long id, EditGroupForm command) {
		/*** input validation ***/
		if (id == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		Group.validateGroupName(command.getName());

		if (command.getCountryId() == null) {
			throw new BusinessException("msg.country.required");
		}
		if (command.getProvinceId() == null) {
			throw new BusinessException("msg.province.required");
		}
		if (StringUtils.isEmpty(command.getCityName())) {
			throw new BusinessException("msg.city.required");
		}
	}

	private String updateGroupNamePath(String namePath, String groupName) {
		if (StringUtils.isEmpty(namePath)) {
			return null;
		}

		int index = namePath.lastIndexOf("/");
		if (index == -1) {
			return groupName;
		} else {
			return namePath.substring(0, index) + "/" + groupName;
		}
	}

	@CacheEvict(value = "dashboardCache", key = "'dashboard'")
	@Override
	public void move(MoveGroupForm command) {
		validateInput(command);

		Group sourceGroup = validateSourceGroup(command.getSourceGroupId());
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), sourceGroup);

		Group targetGroup = validateTargetGroup(command.getTargetGroupId());
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), targetGroup);

		if (targetGroup.getId().equals(sourceGroup.getParent().getId())) {
			return;
		}

		if (targetGroup.getId().equals(sourceGroup.getId())) {
			throw new BusinessException("msg.group.cannotMoveToSelf");
		}

		// 父 Group不能移动到子Group
		if (groupDao.isSelfOrDescendantGroup(sourceGroup.getId(), targetGroup.getId())) {
			throw new BusinessException("msg.group.cannotMoveToChildGroup");
		}

		if (groupDao.existGroupName(targetGroup.getId(), sourceGroup.getName())) {
			throw new BusinessException("msg.group.targetExistSource");
		}
		List<String> needMoveGroupTsns = terminalGroupDao.getNeedMoveGroupTsns(sourceGroup.getId());
		List<Long> needMoveGroupPkgIds = pkgDao.getpkgListByGroupId(sourceGroup.getId(), Pkg.QUERY_PKG);

		updateUserGroup(sourceGroup.getId(), targetGroup.getId());

		if (targetGroup.isRoot()) {
			auditLogService.addAuditLog(Arrays.asList(sourceGroup.getNamePath()), command, OperatorLogForm.MOVE_GROUP,
					" to Group PAX");
			eventService.addEventLog(sourceGroup.getId(),
					OperatorEventForm.MOVE_GROUP + " " + sourceGroup.getNamePath() + " To Group PAX");
		} else {
			auditLogService.addAuditLog(Arrays.asList(sourceGroup.getNamePath()), command, OperatorLogForm.MOVE_GROUP,
					" to Group " + targetGroup.getNamePath());
			eventService.addEventLog(sourceGroup.getId(), OperatorEventForm.MOVE_GROUP + " " + sourceGroup.getNamePath()
					+ " To Group " + targetGroup.getNamePath());
		}

		Group oldParent = sourceGroup.getParent();
		sourceGroup.setParent(targetGroup);
		sourceGroup.setTreeDepth(getTreeDepth(targetGroup));
		updateGroupPath(sourceGroup, targetGroup);
		groupDao.updateGroupAncestor(sourceGroup, targetGroup);
		groupDao.flush();
		deployParaService.doProcessDeployList();
		long batchId = DbHelper.generateId("TMSPTSNS_BATCH_ID", 1);
		terminalDao.addTemporaryTsns(batchId, needMoveGroupTsns);
		Map<Long, Collection<String>> tsnAncestorGroupMap = terminalGroupDao.getTsnsDistinctGroupAncestor(batchId,
				targetGroup.getId());
		Collection<String> needSendMsgTsns = terminalGroupDao.insertTerminalGroups(tsnAncestorGroupMap, command);
		Map<Long, Collection<Long>> pkgAncestorGroupMap = pkgGroupDao.getPkgDistinctGroupAncestor(needMoveGroupPkgIds,
				targetGroup.getId());
		pkgGroupDao.insertPkgGroups(pkgAncestorGroupMap, command);

		groupDao.addChildGroupCount(targetGroup.getId(), 1);
		groupDao.minusChildGroupCount(oldParent.getId(), 1);
		groupDeployService.deleteAllTaskFromGroup(batchId);
		groupDeployService.copyAllGroupTaskToTerminalCascading(batchId, command);

		// send redis message
		List<Map<String, String>> terminlMsgList = terminalService.getTerminalStatusChangedMessage(needSendMsgTsns,
				RedisOperatorForm.MOVE);
		terminalService.sendTerminalStatusChangedMessage(terminlMsgList);

		List<Map<String, Object>> pkgMsgList = pkgService.getPkgStatusChangedMessage(pkgAncestorGroupMap.keySet(),
				RedisOperatorForm.MOVE);
		pkgService.sendPkgStatusChangedMessage(pkgMsgList);
		terminalDao.deleteTemporaryTsns(batchId);

	}

	private void updateUserGroup(Long sourceGroupId, Long targetGroupId) {
		List<Group> selfAndDescantGroups = getSelfAndDescendantGroup(sourceGroupId);

		for (Group group : selfAndDescantGroups) {
			List<User> users = userGroupService.getAssignedGroupUsers(group.getId());
			if (CollectionUtils.isEmpty(users)) {
				continue;
			}
			userGroupService.deleteUserGroup(users, group.getId(), targetGroupId);
		}

	}

	private void updateGroupPath(Group sourceGroup, Group targetGroup) {
		List<Group> selfAndDescantGroups = getSelfAndDescendantGroup(sourceGroup.getId());
		if (CollectionUtils.isNotEmpty(selfAndDescantGroups)) {
			for (Group group : selfAndDescantGroups) {
				// target Group 先不做任何处理
				if (group.getId() == sourceGroup.getId()) {
					continue;
				}
				String newIdPath = relpaceGroupIdPath(group, sourceGroup, targetGroup);
				String newNamePath = replaceGroupNamePath(group, sourceGroup, targetGroup);

				group.setTreeDepth(getTreeDepth(group.getParent()));
				group.setIdPath(newIdPath);
				group.setNamePath(newNamePath);
			}
		}

		sourceGroup.setNamePath(targetGroup.getNamePath() == null ? sourceGroup.getName()
				: targetGroup.getNamePath() + "/" + sourceGroup.getName());
		sourceGroup.setIdPath(targetGroup.getIdPath() + "/" + sourceGroup.getId());
	}

	private void validateInput(MoveGroupForm command) {
		if (command.getSourceGroupId() == null) {
			throw new BusinessException("msg.group.sourceGroupRequired");
		}

		if (command.getTargetGroupId() == null) {
			throw new BusinessException("msg.group.targetGroupRequired");
		}
	}

	private void validateInput(CopyGroupForm command) {
		if (command.getSourceGroupId() == null) {
			throw new BusinessException("msg.group.sourceGroupRequired");
		}

		if (command.getTargetGroupId() == null) {
			throw new BusinessException("msg.group.targetGroupRequired");
		}
	}

	private void addGroupAnscetor(Group group) {
		GroupAncestor groupAncestor = new GroupAncestor(group, group);
		groupAncestor.save();
		Group parent = group.getParent();
		while (parent != null) {
			GroupAncestor ancestor = new GroupAncestor(group, parent);
			ancestor.save();
			parent = parent.getParent();
		}
	}

	private String replaceGroupNamePath(Group group, Group sourceGroup, Group targetGroup) {
		String namePath = group.getNamePath();
		String sourceNamePath = sourceGroup.getNamePath();
		String targetNamePath = targetGroup.getNamePath();
		if (StringUtils.isEmpty(targetNamePath)) {
			return namePath.replace(sourceNamePath, sourceGroup.getName());
		} else {
			return namePath.replace(sourceNamePath, targetNamePath + "/" + sourceGroup.getName());
		}
	}

	private String relpaceGroupIdPath(Group group, Group sourceGroup, Group targetGroup) {
		String idPath = group.getIdPath();
		String sourceIdPath = sourceGroup.getIdPath();
		String targetIdPath = targetGroup.getIdPath();
		String newIdPath = idPath.replace(sourceIdPath, targetIdPath + "/" + sourceGroup.getId());
		return newIdPath;
	}

	@CacheEvict(value = "dashboardCache", key = "'dashboard_'")
	@Override
	public void delete(Long id, BaseForm command) {
		if (id == null) {
			throw new BusinessException("msg.group.groupRequired");
		}

		Group group = validateGroup(id);
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		if (group.getTreeDepth() == 0) {
			throw new BusinessException("msg.group.cannotDeleteSystemGroup");
		}
		if (groupDao.isCreateByUpLevelUser(id, command.getLoginUserId())) {
			throw new BusinessException("msg.group.createByUpLevelUser");
		}
		List<Long> deployIds = terminalDeployService.getInheritGroupDeployIds(group.getId());
		terminalDownloadDao.deleteTerminalDownloads(deployIds);
		terminalService.dismissByGroup(group, command);
		pkgService.dismissByGroup(group, command);
		userService.dismissGroup(id, command);
		groupDeployService.deleteByGroup(group.getId());
		condService.deleteCondition(id, command);
		groupDao.deleteGroupCascading(id);

		groupDao.flush();
		groupDao.minusChildGroupCount(group.getParent().getId(), 1);

		// add audit log
		auditLogService.addAuditLog(Arrays.asList(group.getNamePath()), command, OperatorLogForm.DELETE_GROUP, null);
		eventService.addEventLog(group.getId(), OperatorEventForm.DELETE_GROUP + ":" + group.getNamePath());

	}
	
	@Override
	public void syncProfileToTerminals(Long groupId, BaseForm command) {
		Group group = groupDao.get(groupId);
		Terminal terminal = new Terminal();
		terminal.setCountry(null == group.getCountry()?"":group.getCountry());
		terminal.setCity(null == group.getCity()?"":group.getCity());
		terminal.setProvince(null == group.getProvince()?"":group.getProvince());
		terminal.setZipCode(null == group.getZipCode()?"":group.getZipCode());
		terminal.setTimeZone(null == group.getTimeZone()?"":group.getTimeZone());
		terminal.setDaylightSaving(group.isDaylightSaving());
		groupDao.syncProfileToTerminals(groupId, terminal);
	}

	@Override
	public void handleGroup(Group parent, Group group, BaseForm command) {
		Long id = group.getId();
		group.setIdPath(parent.getIdPath() + "/" + id);
		groupDao.update(group);

		addGroupAnscetor(group);

		groupDao.flush();
		groupDao.addChildGroupCount(parent.getId(), 1);

		if (group.isEnterPriceGroup()) {
			usageThresholdService.saveUsageThreshold(command, group);
		}
		alertConditionService.addAlertCondition(group, command);

	}

	@Override
	public Group createGroup(GroupInfo rootGroupInfo, Group parent, BaseForm command) {
		Group group = new Group();
		group.setZipCode(rootGroupInfo.getZipCode().toUpperCase());
		group.setCode(UUID.randomUUID().toString());
		group.setName(rootGroupInfo.getName());
		group.setParent(parent);
		group.setTreeDepth(getTreeDepth(parent));
		group.setNamePath(getNamePath(parent, rootGroupInfo.getName()));
		group.setDescription(rootGroupInfo.getDescription());
		group.setProvince(rootGroupInfo.getStateOrProvice());
		group.setCountry(rootGroupInfo.getCountry());
		group.setCity(rootGroupInfo.getCity());
		group.setTimeZone(rootGroupInfo.getTimeZone());
		if (StringUtils.equalsIgnoreCase("Enable", rootGroupInfo.getDaylight())) {
			group.setDaylightSaving(true);
		} else {
			group.setDaylightSaving(false);
		}
		group.setAddress(rootGroupInfo.getAddress());
		group.setCreator(command.getLoginUsername());
		group.setCreateDate(command.getRequestTime());
		group.setModifier(command.getLoginUsername());
		group.setModifyDate(command.getRequestTime());
		return group;

	}

	@Override
	public List<String> searchGroupByNamePath(String keywords, Long userId) {
		if (userId == null || StringUtils.isEmpty(keywords)) {
			return Collections.emptyList();
		}
		return groupDao.searchGroupByNamePath(keywords, userId);
	}

	@Override
	public Group getGroupByNamePath(String namePath) {
		if (StringUtils.isEmpty(namePath)) {
			return null;
		}
		return groupDao.getGroupByNamePath(namePath);
	}

	@Override
	public List<String> getTerminalTypesByGroupId(Long groupId) {
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		return groupDao.getTerminalTypesByGroupId(groupId);
	}

	@Override
	public Long getTerminalNumbersByGroupId(Long groupId) {
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		return groupDao.getTerminalNumbersByGroupId(groupId);
	}

	@Override
	public boolean isGroupHasTerminal(Long groupId,Map<Long, List<TerminalGroup>> groupTerminalMap) {
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		if(null == groupTerminalMap || groupTerminalMap.isEmpty()){
		    return false;
		}
		List<TerminalGroup> terminalGroups = groupTerminalMap.get(groupId);
		if(null == terminalGroups || terminalGroups.isEmpty()){
		    return false;
		}
		return true;
	}

	@Override
	public List<TerminalGroup> getTerminalGroupsById(Long groupId) {
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		return groupDao.getTerminalGroupsById(groupId);
	}

	@Override
	public List<Group> getSelfAndDescendantGroup(Long groupId) {
		if (groupId == null) {

			throw new BusinessException("msg.group.groupRequired");
		}
		return groupDao.getSelfAndDescendantGroup(groupId);
	}

	@Override
	public List<Group> getSelfAndAncestorGroup(Long groupId) {
		if (groupId == null) {

			throw new BusinessException("msg.group.groupRequired");
		}
		return groupDao.getSelfAndAncestorGroup(groupId);
	}

	@Override
	public Group validateParentGroup(Long groupId) {
		Group group = groupDao.get(groupId);
		if (group == null) {
			throw new BusinessException("msg.group.parentNotFound");
		}
		return group;
	}

	@Override
	public Group validateGroup(Long groupId) {
		Group group = groupDao.get(groupId);
		if (group == null) {
			throw new BusinessException("msg.group.groupNotFound");
		}
		return group;
	}

	@Override
	public Group validateTargetGroup(Long targetGroupId) {
		Group group = groupDao.get(targetGroupId);
		if (group == null) {
			throw new BusinessException("msg.group.targetGroupNotFound");
		}
		return group;
	}

	@Override
	public Group validateSourceGroup(Long sourceGroupId) {
		Group group = groupDao.get(sourceGroupId);
		if (group == null) {
			throw new BusinessException("msg.group.sourceGroupNotFound");
		}
		return group;
	}

	@Override
	public List<Group> validateGroups(Long[] groupIds) {
		if (groupIds == null || groupIds.length == 0) {
			throw new BusinessException("msg.user.groupRequired");
		}

		Set<Long> groupIdSet = new HashSet<Long>(Arrays.asList(groupIds));
		List<Group> groupList = new ArrayList<Group>(groupIdSet.size());

		for (Long groupId : groupIdSet) {
			groupList.add(validateGroup(groupId));
		}
		return groupList;
	}

	@Override
	public List<Group> validateGroups(String[] groupNamePaths) {

		if (groupNamePaths == null || groupNamePaths.length == 0) {
			return Collections.emptyList();
		}

		Set<String> groupNamePathSet = new HashSet<String>(Arrays.asList(groupNamePaths));
		List<Group> groupList = new ArrayList<Group>(groupNamePathSet.size());

		for (String groupNamePath : groupNamePathSet) {
			Group group = groupDao.getGroupByNamePath(groupNamePath);
			if (group == null) {
				throw new BusinessException("msg.group.groupNamePathNotFound", new String[] { groupNamePath });
			}
			groupList.add(group);
		}
		return groupList;
	}

	@CacheEvict(value = "dashboardCache", key = "'dashboard_'")
	@Override
	public void copy(CopyGroupForm command) {
		validateInput(command);
		Group sourceGroup = validateSourceGroup(command.getSourceGroupId());
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), sourceGroup);

		Group targetGroup = validateTargetGroup(command.getTargetGroupId());
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), targetGroup);

		if (targetGroup.getId().equals(sourceGroup.getParent().getId())) {
			throw new BusinessException("msg.group.targetExistSource");
		}

		if (targetGroup.getId().equals(sourceGroup.getId())) {
			throw new BusinessException("msg.group.cannotCopyToSelf");
		}

		// 父 Group不能copy到子Group
		if (groupDao.isSelfOrDescendantGroup(sourceGroup.getId(), targetGroup.getId())) {
			throw new BusinessException("msg.group.cannotCopyToChildGroup");
		}

		if (groupDao.existGroupName(targetGroup.getId(), sourceGroup.getName())) {
			throw new BusinessException("msg.group.targetExistSource");
		}

		Map<Long, List<Group>> parentIdMappingChild = groupDao.getParentMappingChild();
		Set<String> tsnSet = new HashSet<>();
		Set<Long> pkgIdSet = new HashSet<>();
		deepCopyGroup(sourceGroup, targetGroup, parentIdMappingChild, command, tsnSet, pkgIdSet);
		groupDao.flush();
		deployParaService.doProcessDeployList();

		Group newGroup = groupDao.getGroup(targetGroup.getId(), sourceGroup.getName());
		// add audit log
		if (targetGroup.isRoot()) {
			auditLogService.addAuditLog(Arrays.asList(sourceGroup.getNamePath()), command, OperatorLogForm.COPY_GROUP,
					" to Group PAX");
			eventService.addEventLog(newGroup.getId(),
					OperatorEventForm.COPY_GROUP + " " + sourceGroup.getNamePath() + " To Group PAX");
		} else {
			auditLogService.addAuditLog(Arrays.asList(sourceGroup.getNamePath()), command, OperatorLogForm.COPY_GROUP,
					" to Group " + targetGroup.getNamePath());
			eventService.addEventLog(newGroup.getId(), OperatorEventForm.COPY_GROUP + " " + sourceGroup.getNamePath()
					+ " To Group " + targetGroup.getNamePath());
		}

		// send redis message
		List<Map<String, String>> msgList = terminalService.getTerminalStatusChangedMessage(tsnSet,
				RedisOperatorForm.COPY);
		terminalService.sendTerminalStatusChangedMessage(msgList);

		List<Map<String, Object>> pkgMsgList = pkgService.getPkgStatusChangedMessage(pkgIdSet, RedisOperatorForm.COPY);
		pkgService.sendPkgStatusChangedMessage(pkgMsgList);

	}

	private void deepCopyGroup(Group sourceGroup, Group targetGroup, Map<Long, List<Group>> parentIdMappingChild,
			CopyGroupForm command, Set<String> tsnSet, Set<Long> pkgIdSet) {
		Group group = new Group();
		group.setCode(sourceGroup.getCode());
		group.setName(sourceGroup.getName());
		group.setParent(targetGroup);
		group.setTreeDepth(getTreeDepth(targetGroup));
		group.setNamePath(getNamePath(targetGroup, sourceGroup.getName()));
		group.setDescription(sourceGroup.getDescription());
		group.setCountry(sourceGroup.getCountry());
		group.setProvince(sourceGroup.getProvince());
		group.setCity(sourceGroup.getCity());
		group.setZipCode(sourceGroup.getZipCode());
		group.setTimeZone(sourceGroup.getTimeZone());
		group.setDaylightSaving(sourceGroup.isDaylightSaving());
		group.setCreator(command.getLoginUsername());
		group.setCreateDate(command.getRequestTime());
		group.setModifier(command.getLoginUsername());
		group.setModifyDate(command.getRequestTime());
		groupDao.save(group);

		Long id = group.getId();
		group.setIdPath(targetGroup.getIdPath() + "/" + id);
		groupDao.update(group);
		addGroupAnscetor(group);
		groupDao.flush();
		groupDao.addChildGroupCount(targetGroup.getId(), 1);
		alertConditionService.addAlertCondition(group, command);
		Collection<String> tsns = terminalGroupDao.getTerminalByGroupId(command.getSourceGroupId());
		if (CollectionUtils.isNotEmpty(tsns)) {
			long batchId = DbHelper.generateId("TMSPTSNS_BATCH_ID", 1);
			terminalDao.addTemporaryTsns(batchId, tsns);
			// list the terminal group before bulid relationship between
			// terminal and group
			Map<String, Collection<Long>> tsnGroupIdsMap = terminalGroupDao.getTerminalGroupIds(batchId);
			Map<Long, Collection<String>> tsnAncestorGroupMap = terminalGroupDao.getTsnsDistinctGroupAncestor(batchId,
					group.getId());
			terminalGroupDao.insertTerminalGroups(tsnAncestorGroupMap, command);
			groupDeployService.copyNewLineGroupTaskToTerminals(tsnGroupIdsMap, group, command);
			tsnSet.addAll(tsns);
			terminalDao.deleteTemporaryTsns(batchId);
		}
		Collection<Long> pkgIds = pkgDao.getpkgListByGroupId(command.getSourceGroupId(), Pkg.QUERY_PKG);
		if (CollectionUtils.isNotEmpty(pkgIds)) {

			Map<Long, Collection<Long>> pkgAncestorGroupMap = pkgGroupDao.getPkgDistinctGroupAncestor(pkgIds,
					group.getId());
			pkgGroupDao.insertPkgGroups(pkgAncestorGroupMap, command);
			pkgIdSet.addAll(pkgIds);

		}
		aclTUserGroupService.saveACLUserGroups(command.getLoginUserId(), group.getId());

		List<Group> groups = parentIdMappingChild.get(sourceGroup.getId());

		if (CollectionUtils.isNotEmpty(groups)) {
			for (Group childGroup : groups) {
				command.setSourceGroupId(childGroup.getId());
				deepCopyGroup(childGroup, group, parentIdMappingChild, command, tsnSet, pkgIdSet);
			}

		}

	}

	@Override
	public List<Group> getEnterpriseGroups() {
		List<Group> list = groupDao.getEnterpriseGroups();
		return list;
	}

	@Override
	public Group getExistGroup(Long groupId, String groupName) {

		return groupDao.getExistGroup(groupId, groupName);
	}

	@Override
	public void checkPermissionOfGroup(BaseForm command, Long groupId) {
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		Group group = groupService.get(groupId);
		if (group == null) {
			throw new BusinessException("msg.group.groupNotFound");
		}
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
	}

	//根据userId找到user的parent组，然后根据parent组找到其子组
	@Override
	public boolean isUserHasGroup(Long groupId, Long userId) {
		List<Long> groupIds = userGroupDao.getGroupIdsOld(userId);
		List<Long> ugroupIds = new ArrayList<>();
		for(Long gid:groupIds){
			List<Long> temp = groupAncestorDao.getGroupIdByParentId(gid);
			ugroupIds.addAll(temp);
		}
		if(ugroupIds.isEmpty()){
			return false;
		}
		for(Long id:ugroupIds){
			if(id.intValue() == groupId.intValue()){
				return true;
			}
		}
		return false;
	}

}
