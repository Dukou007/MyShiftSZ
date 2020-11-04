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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pax.common.dao.DbHelper;
import com.pax.common.dao.IBaseDao;
import com.pax.common.exception.BusinessException;
import com.pax.common.fs.FileManagerUtils;
import com.pax.common.pagination.IPageForm;
import com.pax.common.pagination.Page;
import com.pax.common.service.impl.BaseService;
import com.pax.tms.deploy.service.GroupDeployService;
import com.pax.tms.group.GroupInfo;
import com.pax.tms.group.GroupXmlFile;
import com.pax.tms.group.dao.GroupDao;
import com.pax.tms.group.dao.ImportFileDao;
import com.pax.tms.group.model.Group;
import com.pax.tms.group.model.ImportFile;
import com.pax.tms.group.service.GroupService;
import com.pax.tms.group.service.ImportFileService;
import com.pax.tms.group.web.form.ImportFileForm;
import com.pax.tms.group.web.form.QueryFileForm;
import com.pax.tms.monitor.service.EventGrpService;
import com.pax.tms.monitor.service.EventTrmService;
import com.pax.tms.monitor.web.form.OperatorEventForm;
import com.pax.tms.pub.web.form.RedisOperatorForm;
import com.pax.tms.report.dao.TerminalNotRegisteredDao;
import com.pax.tms.res.service.ModelService;
import com.pax.tms.terminal.dao.TerminalDao;
import com.pax.tms.terminal.dao.TerminalGroupDao;
import com.pax.tms.terminal.domain.TerminalInfo;
import com.pax.tms.terminal.model.Terminal;
import com.pax.tms.terminal.service.TerminalService;
import com.pax.tms.user.security.AclManager;
import com.pax.tms.user.service.AuditLogService;
import com.pax.tms.user.service.UserService;
import com.pax.tms.user.web.form.OperatorLogForm;

@Service("importFileServiceImpl")
@SuppressWarnings("unchecked")
public class ImportFileServiceImpl extends BaseService<ImportFile, Long> implements ImportFileService {

	@Autowired
	private ImportFileDao impotFileDao;

	@Autowired
	private GroupDao groupDao;

	@Autowired
	private TerminalGroupDao terminalGroupDao;

	@Autowired
	private GroupService groupService;

	@Autowired
	private TerminalDao terminalDao;
	@Autowired
	private GroupDeployService groupDeployService;
	@Autowired
	private AuditLogService auditLogService;

	@Autowired
	private EventTrmService eventTrmService;

	@Autowired
	private EventGrpService eventGrpService;

	@Autowired
	private ModelService modelService;

	@Autowired
	private TerminalService terminalService;

	@Autowired
	private UserService userService;

	@Autowired
	private TerminalNotRegisteredDao terminalNotRegisteredDao;

	@Override
	public IBaseDao<ImportFile, Long> getBaseDao() {

		return impotFileDao;
	}

	@Override
	public GroupInfo getGroupInfo(ImportFile importFile, boolean isPermissionCreateGroup, ImportFileForm command) {
		byte[] data = FileManagerUtils.getFileManager().downloadFile(importFile.getFilePath(),
				(inputStream) -> convertInputStreamToByte(inputStream));
		GroupInfo rootGroupInfo = null;
		try {
			rootGroupInfo = GroupXmlFile.parse(new ByteArrayInputStream(data), isPermissionCreateGroup, command);
		} catch (DocumentException e) {

			throw new BusinessException("msg.importFile.invalid", e);
		}
		return rootGroupInfo;
	}

	private byte[] convertInputStreamToByte(InputStream inputStream) {
		BufferedInputStream bis = new BufferedInputStream(inputStream);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len;
		try {
			while ((len = bis.read(buffer)) > -1) {
				baos.write(buffer, 0, len);
			}
		} catch (IOException e) {
			throw new BusinessException("msg.importFile.convertFailed", e);
		}
		try {
			baos.flush();
		} catch (IOException e) {
			throw new BusinessException("msg.importFile.convertFailed", e);
		}

		return baos.toByteArray();
	}

	@Override
	public void saveImport(GroupInfo rootGroupInfo, Group parent, ImportFileForm command, Map<String, Object> resultMap,
			boolean isPermissionCreateGroup) {
		if (isPermissionCreateGroup) {
			handleGroupTerminal(rootGroupInfo, parent, command, resultMap, isPermissionCreateGroup);
		} else {

			handleTerminal(rootGroupInfo, command, parent, resultMap);

		}

	}

	private void handleGroupTerminal(GroupInfo rootGroupInfo, Group parent, ImportFileForm command,
			Map<String, Object> resultMap, boolean isPermissionCreateGroup) {
		List<GroupInfo> childGroupInfos = rootGroupInfo.getChildens();
		// 文件中只含有Terminal isHandleTerminal 防止导入Group的时候再次去处理Group下的Terminal

		if (CollectionUtils.isEmpty(childGroupInfos) && !parent.isHandleTerminal()) {
			handleTerminal(rootGroupInfo, command, parent, resultMap);
		} else {

			// 导入文件中同时有Group 和Terminal
			if (CollectionUtils.isEmpty(childGroupInfos)) {
				return;
			}
			List<Group> groupList = groupDao.getAllChildGroups(parent.getId());
			Map<String, Group> groupNameMap = groupList.stream().collect(Collectors.toMap(Group::getName, group -> group));
			for (GroupInfo childGroupInfo : childGroupInfos) {
//				if (groupDao.existGroupName(parent.getId(), childGroupInfo.getName())) {
			    if(groupNameMap.containsKey(childGroupInfo.getName())){
					Group existGroup = groupService.getExistGroup(parent.getId(), childGroupInfo.getName());
					handleTerminal(childGroupInfo, command, existGroup, resultMap);
					existGroup.setHandleTerminal(true);
					saveImport(childGroupInfo, existGroup, command, resultMap, isPermissionCreateGroup);
				} else {
					Group group = groupService.createGroup(childGroupInfo, parent, command);
					groupDao.save(group);
					groupService.handleGroup(parent, group, command);
					group.setHandleTerminal(true);
					// add audit log
					auditLogService.addAuditLog(Arrays.asList(group.getNamePath()), command, OperatorLogForm.ADD_GROUP,
							null);
					eventGrpService.addEventLog(group.getId(), OperatorEventForm.ADD_GROUP + ":" + group.getNamePath());
					// 处理终端
					handleTerminal(childGroupInfo, command, group, resultMap);
					saveImport(childGroupInfo, group, command, resultMap, isPermissionCreateGroup);

				}
			}
		}

	}

	private void handleTerminal(GroupInfo rootGroupInfo, ImportFileForm command, Group group,
			Map<String, Object> resultMap) {
		List<TerminalInfo> terminalInfos = rootGroupInfo.getTerminalInfos();
		if (CollectionUtils.isNotEmpty(terminalInfos)) {
			Set<String> totalTsns = getTotalsTsn(terminalInfos);
			Map<String, Terminal> terminalMap = getTerminalMap(terminalInfos);
			Object[] arr = terminalDao.getExistedAndNotAcceptableTsns(totalTsns, command.getLoginUserId());
			Map<String, List<Long>> existedTsns = (Map<String, List<Long>>) arr[0];

			List<String> notAcceptableTsns = (List<String>) arr[1];
			Object[] needAndIgnoreToAssignGroup = terminalService.getExistTsnNeedAndIgnoreToAssignGroup(existedTsns,
					notAcceptableTsns, group);
			List<String> existTsnNeedToAssignGroup = (List<String>) needAndIgnoreToAssignGroup[0];

			if (CollectionUtils.isNotEmpty(existTsnNeedToAssignGroup)) {
				long batchId = DbHelper.generateId("TMSPTSNS_BATCH_ID", 1);
				terminalDao.addTemporaryTsns(batchId, existTsnNeedToAssignGroup);
				// list the terminal group before bulid relationship between
				// terminal and group
				Map<String, Collection<Long>> tsnGroupIdsMap = terminalGroupDao.getTerminalGroupIds(batchId);
				Map<Long, Collection<String>> tsnAncestorGroupMap = terminalGroupDao
						.getTsnsDistinctGroupAncestor(batchId, group.getId());
				terminalGroupDao.insertTerminalGroups(tsnAncestorGroupMap, command);
				groupDeployService.copyNewLineGroupTaskToTerminals(tsnGroupIdsMap, group, command);
				// send redis message
				List<Map<String, String>> msgList = terminalService
						.getTerminalStatusChangedMessage(existTsnNeedToAssignGroup, RedisOperatorForm.EDIT);
				terminalService.sendTerminalStatusChangedMessage(msgList);
				terminalDao.deleteTemporaryTsns(batchId);
			}
			Collection<String> newTsns = terminalService.getNewTsns(totalTsns, existedTsns.keySet());
			if (CollectionUtils.isNotEmpty(newTsns)) {

				terminalDao.addTerminals(newTsns, terminalMap, command);
				long batchId = DbHelper.generateId("TMSPTSNS_BATCH_ID", 1);
				terminalDao.addTemporaryTsns(batchId, newTsns);
				
				Map<Long, Collection<String>> tsnAncestorGroupMap = terminalGroupDao
						.getTsnsDistinctGroupAncestor(batchId, group.getId());
				terminalGroupDao.insertTerminalGroups(tsnAncestorGroupMap, command);
				groupDeployService.copyAncestorGroupTaskToTerminals(batchId, newTsns, group.getId(), command, true);
				terminalNotRegisteredDao.deleteTerminalNotRegisters(newTsns);
				auditLogService.addAuditLog(newTsns, command, OperatorLogForm.ADD_TERMINAL,
						" in Group " + group.getNamePath());
				eventTrmService.addEventLog(newTsns, OperatorEventForm.ADD_TERMINAL,
						" in Group " + group.getNamePath());

				// send redis message
				List<Map<String, String>> msgList = terminalService.getTerminalStatusChangedMessage(newTsns,
						terminalMap, RedisOperatorForm.ADD);
				terminalService.sendTerminalStatusChangedMessage(msgList);
				terminalDao.deleteTemporaryTsns(batchId);

			}
			handleResultMap(resultMap, totalTsns, newTsns, needAndIgnoreToAssignGroup, notAcceptableTsns);

		}

	}

	private void handleResultMap(Map<String, Object> resultMap, Set<String> totalTsns, Collection<String> newTsns,
			Object[] needAndIgnoreToAssignGroup, List<String> notAcceptableTsns) {

		List<String> existTsnNeedToAssignGroup = (List<String>) needAndIgnoreToAssignGroup[0];
		List<String> existTsnIgnoreToAssignGroup = (List<String>) needAndIgnoreToAssignGroup[1];
		Set<String> totalTsnsList = (Set<String>) resultMap.get("totalTsns");
		Set<String> existTsnNeedToAssignGroups = (Set<String>) resultMap.get("existTsnNeedToAssignGroup");
		Set<String> existTsnIgnoreToAssignGroups = (Set<String>) resultMap.get("existTsnIgnoreToAssignGroup");
		Set<String> newTsnsList = (Set<String>) resultMap.get("newTsns");
		Set<String> ownByOtherParallerGroupTsns = (Set<String>) resultMap.get("ownByOtherParallerGroupTsn");

		totalTsnsList.addAll(totalTsns);
		existTsnNeedToAssignGroups.addAll(existTsnNeedToAssignGroup);
		existTsnIgnoreToAssignGroups.addAll(existTsnIgnoreToAssignGroup);
		newTsnsList.addAll(newTsns);
		ownByOtherParallerGroupTsns.addAll(notAcceptableTsns);

	}

	private Map<String, Terminal> getTerminalMap(List<TerminalInfo> terminalInfos) {
		Map<String, Terminal> terminalMap = new HashMap<String, Terminal>();
		for (TerminalInfo terminalInfo : terminalInfos) {
			Terminal terminal = createTerminal(terminalInfo);
			terminalMap.put(terminalInfo.getTid(), terminal);

		}
		return terminalMap;
	}

	private Terminal createTerminal(TerminalInfo terminalInfo) {
		Terminal terminal = new Terminal();
		terminal.setTid(terminalInfo.getTid());
		terminal.setTsn(terminalInfo.getTid());
		terminal.setModel(modelService.get(terminalInfo.getDestModel()));
		terminal.setCountry(terminalInfo.getCountry());
		terminal.setProvince(terminalInfo.getProvince());
		terminal.setCity(terminalInfo.getCity());
		terminal.setAddress(terminalInfo.getAddress());
		terminal.setZipCode(terminalInfo.getZipcode().toUpperCase());
		if (StringUtils.equalsIgnoreCase("Enable", terminalInfo.getDaylight())) {
			terminal.setDaylightSaving(true);
		} else {
			terminal.setDaylightSaving(false);
		}
		if (StringUtils.equalsIgnoreCase("Enable", terminalInfo.getSyncToServerTime())) {
			terminal.setSyncToServerTime(true);
		} else {
			terminal.setSyncToServerTime(false);
		}
		terminal.setDescription(terminalInfo.getDescription());
		terminal.setTimeZone(terminalInfo.getTimeZone());

		return terminal;
	}

	private Set<String> getTotalsTsn(List<TerminalInfo> terminalInfos) {
		Set<String> result = new TreeSet<String>();
		for (TerminalInfo terminalInfo : terminalInfos) {
			result.add(terminalInfo.getTid());
		}
		return result;
	}

	@Override
	public Map<String, Object> save(ImportFileForm command) {

		validateInput(command);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		initResultMap(resultMap);
		Group parent = groupService.validateParentGroup(command.getParentId());
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), parent);
		ImportFile importFile = null;
		if (impotFileDao.getImportFile(command.getFileName(), command.getParentId(), command.getFileType()) == null) {
			importFile = createImportFile(command, parent);
			impotFileDao.save(importFile);
		} else {
			importFile = impotFileDao.getImportFile(command.getFileName(), command.getParentId(),
					command.getFileType());
			importFile.setFileSize(new File(command.getFilePath()).length());
			importFile.setFilePath(FileManagerUtils.saveLocalFileToFdfs(command.getFilePath()));
			importFile.setModifier(command.getLoginUsername());
			importFile.setModifyDate(command.getRequestTime());
			importFile.setUser(userService.get(command.getLoginUserId()));
			impotFileDao.update(importFile);
		}
		if(!StringUtils.equals(command.getFileType(), ImportFile.KEY_TYPE)){
		    boolean isPermissionCreateGroup = SecurityUtils.getSubject().isPermitted(Group.ADD_GROUP_PERMISSION);
	        GroupInfo rootGroupInfo = getGroupInfo(importFile, isPermissionCreateGroup, command);
	        Set<String> totalExistTsnNeedAssGroup = (Set<String>) resultMap.get("existTsnNeedToAssignGroup");
	        validateInput(rootGroupInfo, parent, command, isPermissionCreateGroup, totalExistTsnNeedAssGroup);
	        if (!command.isOverride()) {
	            resultMap.put("existTsnNeedToAssignGroup", totalExistTsnNeedAssGroup);
	            return resultMap;
	        }
	        saveImport(rootGroupInfo, parent, command, resultMap, isPermissionCreateGroup);
	        if ("GROUP_TYPE".equals(command.getFileType())) {
	            auditLogService.addAuditLog(Arrays.asList("(" + command.getFileName() + ")"), command,
	                    OperatorLogForm.IMPORT_GROUPS, " in Group " + parent.getNamePath());
	        } else {
	            auditLogService.addAuditLog(Arrays.asList("(" + command.getFileName() + ")"), command,
	                    OperatorLogForm.IMPORT_TERMINALS, " in Group " + parent.getNamePath());
	        }
		}
		
		return resultMap;

	}

	private void initResultMap(Map<String, Object> resultMap) {
		resultMap.put("totalTsns", new HashSet<String>());
		resultMap.put("existTsnNeedToAssignGroup", new HashSet<String>());
		resultMap.put("existTsnIgnoreToAssignGroup", new HashSet<String>());
		resultMap.put("newTsns", new HashSet<String>());
		resultMap.put("ownByOtherParallerGroupTsn", new HashSet<String>());
	}

	private void validateInput(GroupInfo rootGroupInfo, Group group, ImportFileForm command,
			boolean isPermissionCreateGroup, Set<String> totalExistTsnNeedAssGroup) {
		if (isPermissionCreateGroup) {
			validateGroupInput(rootGroupInfo, group, command, isPermissionCreateGroup, totalExistTsnNeedAssGroup);
		} else {
			validateTerminalInput(rootGroupInfo, group, command, totalExistTsnNeedAssGroup);

		}

	}

	private void validateGroupInput(GroupInfo rootGroupInfo, Group parent, ImportFileForm command,
			boolean isPermissionCreateGroup, Set<String> totalExistTsnNeedAssGroup) {
		List<GroupInfo> childGroupInfos = rootGroupInfo.getChildens();
		// 文件中只含有Terminal isHandleTerminal 防止导入Group的时候再次去处理Group下的Terminal
		if (CollectionUtils.isEmpty(childGroupInfos) && parent != null && !parent.isHandleTerminal()) {
			validateTerminalInput(rootGroupInfo, parent, command, totalExistTsnNeedAssGroup);
		} else {
			// 导入文件中同时有Group 和Terminal
			if (CollectionUtils.isEmpty(childGroupInfos)) {
				return;
			}
			for (GroupInfo childGroupInfo : childGroupInfos) {

				if (parent != null && groupDao.existGroupName(parent.getId(), childGroupInfo.getName())) {
					Group existGroup = groupService.getExistGroup(parent.getId(), childGroupInfo.getName());
					validateTerminalInput(childGroupInfo, existGroup, command, totalExistTsnNeedAssGroup);
					existGroup.setHandleTerminal(true);
					validateInput(childGroupInfo, existGroup, command, isPermissionCreateGroup,
							totalExistTsnNeedAssGroup);
				} else {
					validateTerminalInput(childGroupInfo, null, command, totalExistTsnNeedAssGroup);
					validateInput(childGroupInfo, null, command, isPermissionCreateGroup, totalExistTsnNeedAssGroup);

				}

			}
		}

	}

	private void validateTerminalInput(GroupInfo rootGroupInfo, Group group, ImportFileForm command,
			Set<String> totalExistTsnNeedAssGroup) {
		List<TerminalInfo> terminalInfos = rootGroupInfo.getTerminalInfos();
		if (CollectionUtils.isNotEmpty(terminalInfos)) {
			Set<String> totalTsns = getTotalsTsn(terminalInfos);
			Object[] arr = terminalDao.getExistedAndNotAcceptableTsns(totalTsns, command.getLoginUserId());
			Map<String, List<Long>> existedTsns = (Map<String, List<Long>>) arr[0];

			List<String> notAcceptableTsns = (List<String>) arr[1];
			Set<String> existedTsnSet = existedTsns.keySet();
			existedTsnSet.removeAll(notAcceptableTsns);
			if (group == null) {
				totalExistTsnNeedAssGroup.addAll(existedTsnSet);
			} else {
				Object[] needAndIgnoreToAssignGroup = terminalService.getExistTsnNeedAndIgnoreToAssignGroup(existedTsns,
						notAcceptableTsns, group);
				totalExistTsnNeedAssGroup.addAll((List<String>) needAndIgnoreToAssignGroup[0]);

			}

		}

	}

	private void validateInput(ImportFileForm command) {
		if (command.getParentId() == null) {
			throw new BusinessException("msg.group.parentRequired");
		}
		if (StringUtils.isEmpty(command.getFilePath())) {
			throw new BusinessException("msg.importFile.filePath");
		}
		if (!StringUtils.equals(command.getFileType(), ImportFile.KEY_TYPE) && !command.getFileName().endsWith(".xml")) {
			throw new BusinessException("msg.importFile.noSuffixXml");
		}

	}

	private ImportFile createImportFile(ImportFileForm command, Group group) {
		ImportFile importFile = new ImportFile();
		importFile.setFileName(command.getFileName());
		importFile.setFileSize(new File(command.getFilePath()).length());
		importFile.setFileType(command.getFileType());
		importFile.setStatus(ImportFile.SUCCESS);
		importFile.setFilePath(FileManagerUtils.saveLocalFileToFdfs(command.getFilePath()));
		importFile.setCreator(command.getLoginUsername());
		importFile.setCreateDate(command.getRequestTime());
		importFile.setModifier(command.getLoginUsername());
		importFile.setModifyDate(command.getRequestTime());
		importFile.setUser(userService.get(command.getLoginUserId()));
		importFile.setGroup(group);
		importFile.setSn(command.getSn());
		return importFile;
	}

	@Override
	public <E, S extends IPageForm> Page<E> page(S command) {
		QueryFileForm form = (QueryFileForm) command;
		if (form.getGroupId() == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		Group group = groupService.validateGroup(form.getGroupId());
		AclManager.checkPermissionOfGroup(form.getLoginUserId(), group);

		return super.page(form);
	}

}
