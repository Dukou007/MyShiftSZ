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
package com.pax.tms.terminal.service.impl;

import io.vertx.core.json.Json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.pax.common.dao.DbHelper;
import com.pax.common.dao.IBaseDao;
import com.pax.common.exception.BusinessException;
import com.pax.common.pagination.IPageForm;
import com.pax.common.pagination.Page;
import com.pax.common.redis.RedisClient;
import com.pax.common.service.impl.BaseService;
import com.pax.common.util.RegexMatchUtils;
import com.pax.common.web.form.BaseForm;
import com.pax.common.web.form.SystemForm;
import com.pax.tms.deploy.dao.DeployDao;
import com.pax.tms.deploy.dao.TerminalDeployDao;
import com.pax.tms.deploy.service.DeployParaService;
import com.pax.tms.deploy.service.GroupDeployService;
import com.pax.tms.group.dao.GroupDao;
import com.pax.tms.group.model.Group;
import com.pax.tms.group.service.GroupService;
import com.pax.tms.location.service.AddressInfo;
import com.pax.tms.location.service.AddressService;
import com.pax.tms.monitor.dao.TerminalStatusDao;
import com.pax.tms.monitor.domain.UsageMessageInfo;
import com.pax.tms.monitor.model.UnregisteredTerminal;
import com.pax.tms.monitor.service.AlertProcessService;
import com.pax.tms.monitor.service.EventTrmService;
import com.pax.tms.monitor.web.AlertConstants;
import com.pax.tms.monitor.web.form.OperatorEventForm;
import com.pax.tms.monitor.web.form.Pie;
import com.pax.tms.monitor.web.form.TerminalUsagePie;
import com.pax.tms.pub.web.form.RedisOperatorForm;
import com.pax.tms.report.dao.TerminalDownloadDao;
import com.pax.tms.report.dao.TerminalNotRegisteredDao;
import com.pax.tms.res.model.Model;
import com.pax.tms.res.service.ModelService;
import com.pax.tms.terminal.dao.TerminalDao;
import com.pax.tms.terminal.dao.TerminalGroupDao;
import com.pax.tms.terminal.dao.TerminalRealStatusDao;
import com.pax.tms.terminal.model.Terminal;
import com.pax.tms.terminal.model.TerminalStatus;
import com.pax.tms.terminal.service.TerminalService;
import com.pax.tms.terminal.web.form.AddTerminalForm;
import com.pax.tms.terminal.web.form.AssignTerminalForm;
import com.pax.tms.terminal.web.form.CopyTerminalForm;
import com.pax.tms.terminal.web.form.EditTerminalForm;
import com.pax.tms.terminal.web.form.QueryTerminalForm;
import com.pax.tms.user.dao.UserGroupDao;
import com.pax.tms.user.model.User;
import com.pax.tms.user.security.AclManager;
import com.pax.tms.user.security.UTCTime;
import com.pax.tms.user.service.AuditLogService;
import com.pax.tms.user.service.UserService;
import com.pax.tms.user.web.form.OperatorLogForm;

@Service("terminalServiceImpl")
public class TerminalServiceImpl extends BaseService<Terminal, String> implements TerminalService {

	private static final Logger LOGGER = LoggerFactory.getLogger(TerminalServiceImpl.class);

	private int maxBatchSize = 100000;

	@Autowired
	private TerminalDao terminalDao;

	@Autowired
	private TerminalGroupDao terminalGroupDao;

	@Autowired
	private ModelService modelService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private GroupDeployService groupDeployService;

	@Autowired
	private AddressService addressService;
	@Autowired
	private DeployParaService deployParaService;

	@Autowired
	private TerminalStatusDao terminalStatusDao;
	
	@Autowired
	private TerminalRealStatusDao  terminalRealStatusDao;

	@Autowired
	private TerminalDeployDao terminalDeployDao;

	@Autowired
	private DeployDao deployDao;

	@Autowired
	private AuditLogService auditLogService;

	@Autowired
	private EventTrmService eventService;

	@Autowired
	private UserService userService;

	@Autowired
	private TerminalNotRegisteredDao terminalNotRegisteredDao;
	@Autowired
	private TerminalDownloadDao terminalDownloadDao;

	@Autowired
	private GroupDao groupDao;
	
	@Autowired
	private UserGroupDao userGroupDao;

	@Autowired
	@Lazy(true)
	@Qualifier("alertProcessServiceImpl")
	private AlertProcessService alertProcessService;

	@Autowired(required = false)
	private RedisClient redisClient;

	@Override
	public IBaseDao<Terminal, String> getBaseDao() {
		return terminalDao;
	}

	@CacheEvict(value = "dashboardCache", key = "'dashboard_'")
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> save(AddTerminalForm command) {
		/*** input validation ***/
		validateInput(command);

		/** business **/
		Group group = groupService.validateGroup(command.getGroupId());
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);

		Model model = modelService.validateModel(command.getDestModel());
		Set<String> totalTsns = validateTsnRanges(command.getTsnRanges());
		AddressInfo addressInfo = addressService.checkAddress(command);
		Object[] arr = terminalDao.getExistedAndNotAcceptableTsns(totalTsns, command.getLoginUserId());
		Map<String, List<Long>> existedTsns = (Map<String, List<Long>>) arr[0];
		List<String> notAcceptableTsns = (List<String>) arr[1];
		Terminal terminal = new Terminal();
		terminal.setModel(model);
		terminal.setAddressInfo(addressInfo);
		terminal.setTimeZone(command.getTimeZone());
		terminal.setDaylightSaving(command.isDaylightSaving());
		terminal.setSyncToServerTime(command.isSyncToServerTime());
		terminal.setDescription(command.getDescription());

		Object[] needAndIgnoreToAssignGroup = getExistTsnNeedAndIgnoreToAssignGroup(existedTsns, notAcceptableTsns,
				group);
		List<String> existTsnNeedToAssignGroup = (List<String>) needAndIgnoreToAssignGroup[0];
		List<String> existTsnIgnoreToAssignGroup = (List<String>) needAndIgnoreToAssignGroup[1];
		if (CollectionUtils.isNotEmpty(existTsnNeedToAssignGroup)) {

			if (!command.isOverride()) {
				Map<String, Object> resultMap = new HashMap<String, Object>();
				resultMap.put("ignoreTsns", existTsnNeedToAssignGroup);
				resultMap.put("totalTsns", totalTsns);
				resultMap.put("existTsnNeedToAssignGroup", existTsnNeedToAssignGroup);
				resultMap.put("existTsnIgnoreToAssignGroup", existTsnIgnoreToAssignGroup);
				return resultMap;
			}
			long batchId = DbHelper.generateId("TMSPTSNS_BATCH_ID", 1);
			terminalDao.addTemporaryTsns(batchId, existTsnNeedToAssignGroup);
			Map<Long, Collection<String>> tsnAncestorGroupMap = terminalGroupDao.getTsnsDistinctGroupAncestor(batchId,
					group.getId());
			// list the terminal group before bulid relationship between
			// terminal and group
			Map<String, Collection<Long>> tsnGroupIdsMap = terminalGroupDao.getTerminalGroupIds(batchId);
			terminalGroupDao.insertTerminalGroups(tsnAncestorGroupMap, command);
			groupDeployService.copyNewLineGroupTaskToTerminals(tsnGroupIdsMap, group, command);
			// send redis message
			List<Map<String, String>> msgList = getTerminalStatusChangedMessage(existTsnNeedToAssignGroup,
					RedisOperatorForm.EDIT);
			sendTerminalStatusChangedMessage(msgList);
			terminalDao.deleteTemporaryTsns(batchId);
		}
		Collection<String> newTsns = getNewTsns(totalTsns, existedTsns.keySet());
		if (CollectionUtils.isNotEmpty(newTsns)) {
			Map<String, Terminal> terminalMap = getTerminalMap(newTsns, terminal);
			terminalDao.addTerminals(newTsns, terminalMap, command);
			long batchId = DbHelper.generateId("TMSPTSNS_BATCH_ID", 1);
			terminalDao.addTemporaryTsns(batchId, newTsns);
			Map<Long, Collection<String>> tsnAncestorGroupMap = terminalGroupDao.getTsnsDistinctGroupAncestor(batchId,
					group.getId());
			terminalGroupDao.insertTerminalGroups(tsnAncestorGroupMap, command);
			groupDeployService.copyAncestorGroupTaskToTerminals(batchId, newTsns, group.getId(), command, true);
			terminalNotRegisteredDao.deleteTerminalNotRegisters(newTsns);

			// send redis message
			List<Map<String, String>> msgList = getTerminalStatusChangedMessage(newTsns, command.getDestModel(),
					RedisOperatorForm.ADD);
			sendTerminalStatusChangedMessage(msgList);
			terminalDao.deleteTemporaryTsns(batchId);

		}
		terminalDao.flush();
		deployParaService.doProcessDeployList();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("totalTsns", totalTsns);
		resultMap.put("existTsnNeedToAssignGroup", existTsnNeedToAssignGroup);
		resultMap.put("existTsnIgnoreToAssignGroup", existTsnIgnoreToAssignGroup);
		resultMap.put("newTsns", newTsns);
		resultMap.put("ownByOtherParallerGroupTsn", notAcceptableTsns);
		resultMap.put("groupId", group.getId());
		resultMap.put("terminalType", command.getDestModel());
		resultMap.put("groupNamePath", group.getNamePath());
		resultMap.put("createdBy", command.getLoginUsername());
		resultMap.put("createdOn", command.getRequestTime());
		resultMap.put("updatedBy", command.getLoginUsername());
		resultMap.put("updatedOn", command.getRequestTime());
		resultMap.put("address", command.getAddress());
		resultMap.put("description", command.getDescription());
		// add audit log, add event log
		if (!group.isRoot()) {
			auditLogService.addAuditLog(newTsns, command, OperatorLogForm.ADD_TERMINAL,
					" in Group " + group.getNamePath());
			eventService.addEventLog(newTsns, OperatorEventForm.ADD_TERMINAL, " in Group " + group.getNamePath());
		} else {
			auditLogService.addAuditLog(newTsns, command, OperatorLogForm.ADD_TERMINAL, " in Group " + group.getName());
			eventService.addEventLog(newTsns, OperatorEventForm.ADD_TERMINAL, " in Group " + group.getName());
		}

		return resultMap;
	}

	@Override
	public void checkExistTsnTerminalType(Collection<String> existTsnNeedToAssignGroup, String terminalType) {
		for (String tsn : existTsnNeedToAssignGroup) {
			String destModel = terminalDao.get(tsn).getModel().getId();
			if (!StringUtils.equals(destModel, terminalType)) {
				throw new BusinessException("tsn.terminalType.notSame", new String[] { tsn });
			}
		}

	}

	private Map<String, Terminal> getTerminalMap(Collection<String> existTsnNeedToAssignGroup, Terminal terminal) {
		Map<String, Terminal> terminalMap = new HashMap<String, Terminal>();
		for (String tsn : existTsnNeedToAssignGroup) {
			terminalMap.put(tsn, terminal);
		}
		return terminalMap;
	}

	@Override
	public Object[] getExistTsnNeedToAssignGroup(Map<String, List<Long>> existedTsnGroups,
			List<String> notAcceptableTsns, Group targetGroup) {
		return getExistTsnNeedAndIgnoreToAssignGroup(existedTsnGroups, notAcceptableTsns, targetGroup);
	}

	@Override
	public Object[] getExistTsnNeedAndIgnoreToAssignGroup(Map<String, List<Long>> existedTsnGroups,
			List<String> notAcceptableTsns, Group targetGroup) {
		Object[] object = new Object[2];
		if (existedTsnGroups == null || existedTsnGroups.isEmpty()) {
			object[0] = Collections.emptyList();
			object[1] = Collections.emptyList();
			return object;
		}
		List<String> needAssignGroupTsns = new ArrayList<String>();
		List<String> ignoreAssignGroupTsns = new ArrayList<String>();
		Collection<String> notAcceptableTsnSet = notAcceptableTsns;
		if (!(notAcceptableTsnSet instanceof Set)) {
			notAcceptableTsnSet = new HashSet<String>(notAcceptableTsns);
		}
		for (String tsn : existedTsnGroups.keySet()) {
			if (CollectionUtils.isNotEmpty(notAcceptableTsns) && notAcceptableTsns.contains(tsn)) {
				continue;
			}
			if (!existedTsnGroups.get(tsn).contains(targetGroup.getId())) {
				needAssignGroupTsns.add(tsn);
			} else {
				ignoreAssignGroupTsns.add(tsn);
			}
		}
		object[0] = needAssignGroupTsns;
		object[1] = ignoreAssignGroupTsns;
		return object;
	}

	private Set<String> validateTsnRanges(String[] tsnRanges) {
		Set<String> totalTsns = TerminalSerialNumber.parseTsns(tsnRanges, maxBatchSize);
		if (CollectionUtils.isEmpty(totalTsns)) {
			throw new BusinessException("tsn.Required");
		}
		return totalTsns;
	}

	private void validateInput(AddTerminalForm command) {
		/*** input validation ***/
		if (ArrayUtils.isEmpty(command.getTsnRanges())) {
			throw new BusinessException("tsn.Required");
		}
		for(String tsn: command.getTsnRanges()){
			if (RegexMatchUtils.contains(tsn,"\\s+")) {
				throw new BusinessException("tsn.illegalSpace", new String[] { tsn });
			}
		}
		

		if (StringUtils.isEmpty(command.getDestModel())) {
			throw new BusinessException("model.Required");
		}

		if (command.getGroupId() == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		if (StringUtils.isEmpty(command.getTimeZone())) {
			throw new BusinessException("msg.timeZone.required");
		}

		if (StringUtils.isEmpty(command.getCityName())) {
			throw new BusinessException("msg.city.required");
		}

	}

	@Override
	public Collection<String> getNewTsns(Collection<String> totalTsns, Collection<String> existsTsns) {
		if (CollectionUtils.isEmpty(totalTsns)) {
			return Collections.emptyList();
		}
		if (CollectionUtils.isEmpty(existsTsns)) {
			return totalTsns;
		}
		List<String> newTsns = new ArrayList<>();
		for (String tsn : totalTsns) {
			if (!existsTsns.contains(tsn)) {
				newTsns.add(tsn);
			}
		}
		return newTsns;
	}

	@Override
	public void edit(String tsn, EditTerminalForm command) {

		validateInput(tsn, command);

		Terminal terminal = validateTerminal(tsn);
		AclManager.checkPermissionOfTerminal(command.getLoginUserId(), terminal);

		AddressInfo addressInfo = addressService.checkAddress(command);
		terminal.setAddressInfo(addressInfo);
		terminal.setDescription(command.getDescription());
		terminal.setTimeZone(command.getTimeZone());
		terminal.setDaylightSaving(command.isDaylightSaving());
		terminal.setSyncToServerTime(command.isSyncToServerTime());
		terminal.setModifier(command.getLoginUsername());
		terminal.setModifyDate(command.getRequestTime());
		Group group = groupService.get(command.getGroupId());
		if (!group.isRoot()) {
			auditLogService.addAuditLog(Arrays.asList(tsn), command, OperatorLogForm.EDIT_TERMINAL,
					" in Group " + group.getNamePath());
			eventService.addEventLog(Arrays.asList(tsn), OperatorEventForm.EDIT_TERMINAL,
					" in Group " + group.getNamePath());
		} else {
			auditLogService.addAuditLog(Arrays.asList(tsn), command, OperatorLogForm.EDIT_TERMINAL, " in Group " + group.getName());
			eventService.addEventLog(Arrays.asList(tsn), OperatorEventForm.EDIT_TERMINAL, " in Group " + group.getName());
		}

		// send redis message
		List<Map<String, String>> msgList = getTerminalStatusChangedMessage(Arrays.asList(tsn),
				terminal.getModel().getName(), RedisOperatorForm.EDIT);

		sendTerminalStatusChangedMessage(msgList);
	}

	private void validateInput(String tsn, EditTerminalForm command) {
		if (StringUtils.isEmpty(tsn)) {
			throw new BusinessException("tsn.Required");
		}

		if (command.getCountryId() == null) {
			throw new BusinessException("msg.country.required");
		}

		if (StringUtils.isEmpty(command.getCityName())) {
			throw new BusinessException("msg.city.required");
		}

	}

	@CacheEvict(value = "dashboardCache", key = "'dashboard_'")
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> copy(String tsn, CopyTerminalForm command) {
		validateInput(tsn, command);

		Terminal terminal = validateTerminal(tsn);
		command.setDestModel(terminal.getModel().getId());
		AclManager.checkPermissionOfTerminal(command.getLoginUserId(), terminal);

		Group targetGroup = groupService.validateTargetGroup(command.getTargetGroupId());
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), targetGroup);

		Set<String> totalTsns = validateTsnRanges(command.getTsnRanges());

		Object[] arr = terminalDao.getExistedAndNotAcceptableTsns(totalTsns, command.getLoginUserId());
		Map<String, List<Long>> existedTsnGroups = (Map<String, List<Long>>) arr[0];
		List<String> notAcceptableTsns = (List<String>) arr[1];
		Object[] needAndIgnoreToAssignGroup = getExistTsnNeedAndIgnoreToAssignGroup(existedTsnGroups, notAcceptableTsns,
				targetGroup);
		List<String> existTsnNeedToAssignGroup = (List<String>) needAndIgnoreToAssignGroup[0];
		List<String> existTsnIgnoreToAssignGroup = (List<String>) needAndIgnoreToAssignGroup[1];
		Set<String> existTsns = existedTsnGroups.keySet();
		if (CollectionUtils.isNotEmpty(existTsns)) {

			Map<String, Object> resultMap = handleTsnNeedToAssignGroup(existTsnNeedToAssignGroup, targetGroup, command);
			if (resultMap != null && resultMap.size() > 0) {
				return resultMap;
			}
			List<String> needHandleTsns = getNeedHandleTsns(existTsns, terminal.getTsn());
			terminalDao.updateTerminals(terminal, needHandleTsns, command);
			// copy self task must remove self
			// need to check if had already copy the task to terminals
			groupDeployService.copySourceTerminalTask(needHandleTsns, command, terminal, false);

			// send redis message
			List<Map<String, String>> msgList = getTerminalStatusChangedMessage(existTsns, RedisOperatorForm.EDIT);
			sendTerminalStatusChangedMessage(msgList);
		}
		Collection<String> newTsns = getNewTsns(totalTsns, existedTsnGroups.keySet());
		if (CollectionUtils.isNotEmpty(newTsns)) {
			Map<String, Terminal> terminalMap = getTerminalMap(newTsns, terminal);
			terminalDao.addTerminals(newTsns, terminalMap, command);
			long batchId = DbHelper.generateId("TMSPTSNS_BATCH_ID", 1);
			terminalDao.addTemporaryTsns(batchId, newTsns);
			Map<Long, Collection<String>> tsnAncestorGroupMap = terminalGroupDao.getTsnsDistinctGroupAncestor(batchId,
					targetGroup.getId());
			terminalGroupDao.insertTerminalGroups(tsnAncestorGroupMap, command);
			groupDeployService.copyAncestorGroupTaskToTerminals(batchId, newTsns, targetGroup.getId(), command, true);
			// 拷贝Terminal自己的任务
			groupDeployService.copySourceTerminalTask(newTsns, command, terminal, true);
			terminalNotRegisteredDao.deleteTerminalNotRegisters(newTsns);

			// send redis message
			List<Map<String, String>> msgList = getTerminalStatusChangedMessage(newTsns, command.getDestModel(),
					RedisOperatorForm.CLONE);
			sendTerminalStatusChangedMessage(msgList);
			terminalDao.deleteTemporaryTsns(batchId);
		}
		terminalDao.flush();
		deployParaService.doProcessDeployList();
		for (String newTsn : newTsns) {
			auditLogService.addAuditLog(Arrays.asList(""), command, OperatorLogForm.CLONE_TERMINAL,
					" from " + tsn + " to " + newTsn);
		}
		eventService.addEventLog(newTsns, OperatorEventForm.CLONE_TERMINAL + " from " + tsn + " to ", null);

		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("totalTsns", totalTsns);
		resultMap.put("existTsnNeedToAssignGroup", existedTsnGroups.keySet());
		resultMap.put("existTsnIgnoreToAssignGroup", existTsnIgnoreToAssignGroup);
		resultMap.put("newTsns", newTsns);
		resultMap.put("ownByOtherParallerGroupTsn", notAcceptableTsns);
		return resultMap;
	}

	private List<String> getNeedHandleTsns(Set<String> existTsns, String sourceTsn) {
		List<String> needCopySelfTaskTsns = new ArrayList<String>();
		for (String tsn : existTsns) {
			if (StringUtils.equals(tsn, sourceTsn)) {
				continue;
			}
			needCopySelfTaskTsns.add(tsn);

		}
		return needCopySelfTaskTsns;
	}

	private Map<String, Object> handleTsnNeedToAssignGroup(List<String> existTsnNeedToAssignGroup, Group targetGroup,
			CopyTerminalForm command) {
		if (CollectionUtils.isNotEmpty(existTsnNeedToAssignGroup)) {
			if (!command.isOverride()) {

				Map<String, Object> resultMap = new HashMap<String, Object>();
				resultMap.put("ignoreTsns", StringUtils.join(existTsnNeedToAssignGroup, ","));
				return resultMap;
			}
			long batchId = DbHelper.generateId("TMSPTSNS_BATCH_ID", 1);
			terminalDao.addTemporaryTsns(batchId, existTsnNeedToAssignGroup);
			Map<Long, Collection<String>> tsnAncestorGroupMap = terminalGroupDao.getTsnsDistinctGroupAncestor(batchId,
					targetGroup.getId());
			// list the terminal group before bulid relationship between
			// terminal and group
			Map<String, Collection<Long>> tsnGroupIdsMap = terminalGroupDao.getTerminalGroupIds(batchId);
			terminalGroupDao.insertTerminalGroups(tsnAncestorGroupMap, command);
			groupDeployService.copyNewLineGroupTaskToTerminals(tsnGroupIdsMap, targetGroup, command);
			terminalDao.deleteTemporaryTsns(batchId);
		}
		return Collections.emptyMap();
	}

	private void validateInput(String tsn, CopyTerminalForm command) {
		if (StringUtils.isEmpty(tsn)) {
			throw new BusinessException("tsn.Required");
		}

		if (command.getTargetGroupId() == null) {
			throw new BusinessException("msg.group.targetGroupRequired");
		}
	}

	@Override
	public void activate(String[] tsns, BaseForm command) {
		if (ArrayUtils.isEmpty(tsns)) {
			throw new BusinessException("tsn.Required");
		}
		List<String> tsnList = Arrays.asList(tsns);
		long batchId = DbHelper.generateId("TMSPTSNS_BATCH_ID", 1);
		terminalDao.addTemporaryTsns(batchId, tsnList);
		List<String> notAcceptableTsns = terminalDao.getNotAcceptableTsns(batchId, command.getLoginUserId());
		if (CollectionUtils.isNotEmpty(notAcceptableTsns)) {
			throw new BusinessException("msg.user.notGrantedTerminal",
					new String[] { StringUtils.join(notAcceptableTsns, ",") });
		}
		terminalDao.activate(tsnList, command);
		auditLogService.addAuditLog(tsnList, command, OperatorLogForm.ACTIVATE_TERMINAL, null);
		eventService.addEventLog(tsnList, OperatorEventForm.ACTIVATE_TERMINAL, null);

		// send redis message
		List<Map<String, String>> msgList = getTerminalStatusChangedMessage(tsnList, RedisOperatorForm.ACTIVATE);
		sendTerminalStatusChangedMessage(msgList);
		terminalDao.deleteTemporaryTsns(batchId);
	}

	@Override
	public void deactivate(String[] tsns, BaseForm command) {
		if (ArrayUtils.isEmpty(tsns)) {
			throw new BusinessException("tsn.Required");
		}
		List<String> tsnList = Arrays.asList(tsns);
		long batchId = DbHelper.generateId("TMSPTSNS_BATCH_ID", 1);
		terminalDao.addTemporaryTsns(batchId, tsnList);
		List<String> notAcceptableTsns = terminalDao.getNotAcceptableTsns(batchId, command.getLoginUserId());
		if (CollectionUtils.isNotEmpty(notAcceptableTsns)) {
			throw new BusinessException("msg.user.notGrantedTerminal",
					new String[] { StringUtils.join(notAcceptableTsns, ",") });
		}
		terminalDao.deactivate(tsnList, command);

		auditLogService.addAuditLog(tsnList, command, OperatorLogForm.DEACTIVATE_TERMINAL, null);
		eventService.addEventLog(tsnList, OperatorEventForm.DEACTIVATE_TERMINAL, null);

		// send redis message
		List<Map<String, String>> msgList = getTerminalStatusChangedMessage(tsnList, RedisOperatorForm.DEACTIVATE);
		sendTerminalStatusChangedMessage(msgList);
		terminalDao.deleteTemporaryTsns(batchId);
	}

	@CacheEvict(value = "dashboardCache", key = "'dashboard_'")
	@Override
	public void dismiss(String[] tsns, BaseForm command) {
		if (ArrayUtils.isEmpty(tsns)) {
			throw new BusinessException("tsn.Required");
		}
		if (command.getGroupId() == null) {
			throw new BusinessException("msg.group.groupRequired");
		}

		Group group = groupService.validateGroup(command.getGroupId());

		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);

		List<String> tsnList = Arrays.asList(tsns);
		long batchId = DbHelper.generateId("TMSPTSNS_BATCH_ID", 1);
		List<String> notAcceptableTsns = terminalDao.getNotAcceptableTsns(batchId, command.getLoginUserId());
		if (CollectionUtils.isNotEmpty(notAcceptableTsns)) {
			throw new BusinessException("msg.user.notGrantedTerminal",
					new String[] { StringUtils.join(notAcceptableTsns, ",") });
		}
		terminalDao.deleteTemporaryTsns(batchId);
		dismiss(tsnList, group, command);
		auditLogService.addAuditLog(tsnList, command, OperatorLogForm.REMOVE_TERMINAL,
				" in Group " + group.getNamePath());
		eventService.addEventLog(tsnList, OperatorEventForm.DISMISS_TERMINAL, " in Group " + group.getNamePath());

	}

	private void dismiss(List<String> tsnList, Group group, BaseForm command) {
		if (CollectionUtils.isEmpty(tsnList)) {
			return;
		}
		// send redis message
		List<Map<String, String>> msgList = getTerminalStatusChangedMessage(tsnList, RedisOperatorForm.REMOVE);
		sendTerminalStatusChangedMessage(msgList);
		long batchId = DbHelper.generateId("TMSPTSNS_BATCH_ID", 1);
		terminalDao.addTemporaryTsns(batchId, tsnList);
		Map<Long, Collection<String>> levelOneGroups = terminalGroupDao.getLevelOneGroup(batchId);
		terminalGroupDao.deleteTerminalGroupCascading(tsnList, group.getId());
		Collection<String> unassignedTerminals = terminalGroupDao.getTerminalUnassignedToGroup(batchId);
		if (unassignedTerminals.isEmpty()) {
			groupDeployService.deleteChildGroupTaskCacading(batchId, group);
			terminalDao.deleteTemporaryTsns(batchId);
			return;
		}
		terminalDao.deactivate(unassignedTerminals, SystemForm.instance);

		Map<Long, Collection<String>> utlog = getLevelOneGroupUnassigedTerminals(levelOneGroups, unassignedTerminals);
		for (Long groupId : utlog.keySet()) {
			terminalGroupDao.insertTerminalGroup(utlog.get(groupId), groupId, SystemForm.instance);
		}
		terminalGroupDao.flush();
		groupDeployService.deleteChildGroupTaskCacading(batchId, group);
		terminalDao.deleteTemporaryTsns(batchId);

	}

	private Map<Long, Collection<String>> getLevelOneGroupUnassigedTerminals(
			Map<Long, Collection<String>> levelOneGroups, Collection<String> unassignedTerminals) {

		Collection<String> ut = unassignedTerminals;
		if (!(ut instanceof Set)) {
			ut = new HashSet<String>(unassignedTerminals);
		}

		Collection<String> values;
		Iterator<Entry<Long, Collection<String>>> it = levelOneGroups.entrySet().iterator();
		while (it.hasNext()) {
			values = it.next().getValue();
			values.retainAll(ut);
			if (values.isEmpty()) {
				it.remove();
			}
		}

		return levelOneGroups;
	}

	@CacheEvict(value = "dashboardCache", key = "'dashboard_'")
	@Override
	public void delete(String[] tsns, BaseForm command) {
		if (ArrayUtils.isEmpty(tsns)) {
			throw new BusinessException("tsn.Required");
		}

		List<String> tsnList = Arrays.asList(tsns);
		long batchId = DbHelper.generateId("TMSPTSNS_BATCH_ID", 1);
		terminalDao.addTemporaryTsns(batchId, tsnList);
		List<String> notAcceptableTsns = terminalDao.getNotAcceptableTsns(batchId, command.getLoginUserId());
		if (CollectionUtils.isNotEmpty(notAcceptableTsns)) {
			throw new BusinessException("msg.user.notGrantedTerminal",
					new String[] { StringUtils.join(notAcceptableTsns, ",") });
		}
		terminalDao.deleteTemporaryTsns(batchId);
		delete(tsnList);

		auditLogService.addAuditLog(tsnList, command, OperatorLogForm.DELETE_TERMINAL, null);
		eventService.addEventLog(tsnList, OperatorEventForm.DELETE_TERMINAL, null);

	}

	private void delete(List<String> tsnList) {
		if (CollectionUtils.isEmpty(tsnList)) {
			return;
		}
		// send redis message
		List<Map<String, String>> msgList = getTerminalStatusChangedMessage(tsnList, RedisOperatorForm.DELETE);
		sendTerminalStatusChangedMessage(msgList);
		long batchId = DbHelper.generateId("TMSPTSNS_BATCH_ID", 1);
		terminalDao.addTemporaryTsns(batchId, tsnList);
		terminalGroupDao.deleteTerminalGroupByTsn(tsnList);
		List<Long> deployIds = deployDao.getDeployIds(batchId);
		deployParaService.deleteDeployParas(deployIds);
		terminalDownloadDao.deleteTerminalDownloads(deployIds);
		deployDao.deleteDeployByTsns(batchId);
		terminalDeployDao.deleteTerminalDeploy(tsnList);

		Collection<String> unassignedTerminals = terminalGroupDao.getTerminalUnassignedToGroup(batchId);
		if (CollectionUtils.isEmpty(unassignedTerminals)) {
			return;
		}
		terminalDao.delete(unassignedTerminals);
		terminalDao.deleteTemporaryTsns(batchId);

	}

	@CacheEvict(value = "dashboardCache", key = "'dashboard_'")
	@Override
	public void moveToGroup(String[] tsns, QueryTerminalForm command) {

		if (ArrayUtils.isEmpty(tsns)) {
			throw new BusinessException("tsn.Required");
		}

		if (command.getGroupId() == null) {
			throw new BusinessException("msg.group.sourceGroupRequired");
		}
		if (command.getTargetGroupId() == null) {
			throw new BusinessException("msg.group.targetGroupRequired");
		}

		for (String tsn : tsns) {
			Terminal terminal = validateTerminal(tsn);
			AclManager.checkPermissionOfTerminal(command.getLoginUserId(), terminal);

		}
		Group sourceGroup = groupService.validateSourceGroup(command.getGroupId());
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), sourceGroup);

		Group targetGroup = groupService.validateTargetGroup(command.getTargetGroupId());
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), targetGroup);

		if (groupDao.isSelfOrDescendantGroup(command.getGroupId(), command.getTargetGroupId())
				|| groupDao.isSelfOrDescendantGroup(command.getTargetGroupId(), command.getGroupId())) {
			throw new BusinessException("tsn.cannotMoveToFiliation");
		}

		List<String> tsnList = Arrays.asList(tsns);
		long batchId = DbHelper.generateId("TMSPTSNS_BATCH_ID", 1);
		List<String> targetGroupExistTsns = terminalGroupDao.getTargetGroupExistTsns(batchId, targetGroup.getId());
		if (CollectionUtils.isNotEmpty(targetGroupExistTsns)) {
			throw new BusinessException("tsn.exist", new String[] { StringUtils.join(targetGroupExistTsns, ",") });
		}
		terminalDao.addTemporaryTsns(batchId, tsnList);
		terminalGroupDao.deleteTerminalGroupCascading(tsnList, sourceGroup.getId());

		Map<Long, Collection<String>> tsnAncestorGroupMap = terminalGroupDao.getTsnsDistinctGroupAncestor(batchId,
				targetGroup.getId());
		terminalGroupDao.insertTerminalGroups(tsnAncestorGroupMap, command);
		terminalDao.flush();
		groupDeployService.deleteAllTaskFromGroup(batchId);
		groupDeployService.copyAllGroupTaskToTerminalCascading(batchId, command);
		deployParaService.doProcessDeployList();

		if (targetGroup.isRoot()) {
			auditLogService.addAuditLog(tsnList, command, OperatorLogForm.MOVE_TERMINAL,
					" from Group " + sourceGroup.getNamePath() + " to Group " + targetGroup.getName());
			eventService.addEventLog(tsnList, OperatorEventForm.MOVE_TERMINAL,
					" from Group " + sourceGroup.getNamePath() + " to Group " + targetGroup.getName());
		} else {
			auditLogService.addAuditLog(tsnList, command, OperatorLogForm.MOVE_TERMINAL,
					" from Group " + sourceGroup.getNamePath() + " to Group " + targetGroup.getNamePath());
			eventService.addEventLog(tsnList, OperatorEventForm.MOVE_TERMINAL,
					" from Group " + sourceGroup.getNamePath() + " to Group " + targetGroup.getNamePath());
		}

		// send redis message
		List<Map<String, String>> msgList = getTerminalStatusChangedMessage(tsnList, RedisOperatorForm.MOVE);
		sendTerminalStatusChangedMessage(msgList);
		terminalDao.deleteTemporaryTsns(batchId);

	}

	@CacheEvict(value = "dashboardCache", key = "'dashboard_'")
	@Override
	public Map<String, Object> assign(String[] tsns, AssignTerminalForm command) {

		validateInput(tsns, command);
		List<Long> groupIds = Arrays.asList(command.getGroupIds());
		List<Group> groups = groupService.validateGroups(command.getGroupIds());
		for (Group group : groups) {
			AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		}
		for (String tsn : tsns) {
			Terminal terminal = validateTerminal(tsn);
			AclManager.checkPermissionOfTerminal(command.getLoginUserId(), terminal);
		}
		List<String> tsnList = Arrays.asList(tsns);
		long batchId = DbHelper.generateId("TMSPTSNS_BATCH_ID", 1);
		terminalDao.addTemporaryTsns(batchId, tsnList);
		// list the terminal group before bulid relationship between terminal
		// and group
		Map<String, Collection<Long>> tsnGroupIdsMap = terminalGroupDao.getTerminalGroupIds(batchId);
		Set<String> existTsns = new HashSet<>();
		Set<String> changeTsns = new HashSet<>();
		for (Long groupId : groupIds) {
			Set<String> allTsns = new HashSet<>(tsnList);
			Map<Long, Collection<String>> tsnAncestorGroupMap = terminalGroupDao.getTsnsDistinctGroupAncestor(batchId,
					groupId);
			Set<String> distinctTsns = (Set<String>) terminalGroupDao.insertTerminalGroups(tsnAncestorGroupMap,
					command);
			allTsns.removeAll(distinctTsns);
			changeTsns.addAll(distinctTsns);
			existTsns.addAll(allTsns);
		}
		terminalDao.flush();
		for (Group group : groups) {
			groupDeployService.copyNewLineGroupTaskToTerminals(tsnGroupIdsMap, group, command);
		}
		for (Long groupId : groupIds) {
			//目标组copy名
			String grpNamePath = groupDao.get(groupId).getNamePath();
			//源组copy名
			String sourceNamePath = groupService.get(command.getGroupId()).getNamePath();
			if(groupService.get(command.getGroupId()).isRoot()){
				sourceNamePath = groupService.get(command.getGroupId()).getName();
			}
			auditLogService.addAuditLog(tsnList, command, OperatorLogForm.COPY_TERMINAL,
					" from Group " + sourceNamePath + " to Group " + grpNamePath);

			eventService.addEventLog(tsnList, OperatorEventForm.COPY_TERMINAL,
					" from Group " + sourceNamePath + " to Group " + grpNamePath);
		}

		// send redis message
		List<Map<String, String>> msgList = getTerminalStatusChangedMessage(changeTsns, RedisOperatorForm.COPY);
		sendTerminalStatusChangedMessage(msgList);
		terminalDao.deleteTemporaryTsns(batchId);

		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("existTsns", existTsns);
		return resultMap;
	}

	private void validateInput(String[] tsns, AssignTerminalForm command) {
		if (ArrayUtils.isEmpty(tsns)) {
			throw new BusinessException("tsn.Required");
		}
		if (ArrayUtils.isEmpty(command.getGroupIds())) {
			throw new BusinessException("msg.group.groupRequired");
		}

	}

	@Autowired
	public void setMaxBatchSize(@Value("${tms.terminal.add.maxBatchSize:100000}") int maxBatchSize) {
		this.maxBatchSize = maxBatchSize;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E, S extends IPageForm> Page<E> page(S command) {
		QueryTerminalForm form = (QueryTerminalForm) command;
		if (form.getGroupId() == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		Group group = groupService.validateGroup(form.getGroupId());
		AclManager.checkPermissionOfGroup(form.getLoginUserId(), group);

		long totalCount = getBaseDao().count(form);
		int index = form.getPageIndex();
		int size = form.getPageSize();
		int start = (index - 1) * size;
		List<Map<String, Object>> list = new ArrayList<>();
        if (start < totalCount) {
            list = getBaseDao().page(command, start, size); 
        }

		list.forEach(map -> {
			List<String> groupNames = this.getGroupNameByTsn(map.get("tsn").toString());
			map.put("groupNames", String.join("//", groupNames));
		});

		return Page.getPage(index, size, totalCount, (List<E>) list);
	}

	@Override
	public List<TerminalUsagePie> getUsageStatus(String terminalId, Long userId) {
		List<TerminalUsagePie> pieList = new ArrayList<TerminalUsagePie>();
		Map<String, TerminalUsagePie> map = new HashMap<String, TerminalUsagePie>();
		for (String itemName : AlertConstants.getUsageItems()) {
			UsageMessageInfo usage = terminalStatusDao.getUsageStatus(terminalId, itemName);
			TerminalUsagePie usagePie = this.transferUsagePie(usage, itemName);
			pieList.add(usagePie);
			map.put(itemName, usagePie);
		}

		User user = userService.get(userId);
		String realTime = user.getUsage();
		String[] realTimes = realTime.split(",");
		List<TerminalUsagePie> newPieList = new ArrayList<TerminalUsagePie>();
		for (String str : realTimes) {
			TerminalUsagePie pie = map.get(str.split(":")[0]);
			pie.setIsshow(Integer.parseInt(str.split(":")[1]));
			newPieList.add(pie);
		}

		return newPieList;
	}

	@Override
	public List<List<TerminalUsagePie>> getUsageStatusBarList(String terminalId, Long userId) {
		List<List<TerminalUsagePie>> result = new ArrayList<List<TerminalUsagePie>>(
				AlertConstants.getUsageItemsCount());
		Map<String, List<TerminalUsagePie>> map = new HashMap<String, List<TerminalUsagePie>>();
		for (String itemName : AlertConstants.getUsageItems()) {
			List<UsageMessageInfo> usageList = terminalStatusDao.getUsageStatusBar(terminalId, itemName);
			List<TerminalUsagePie> pieList = new ArrayList<TerminalUsagePie>();
			if (usageList == null || usageList.isEmpty()) {
				pieList.add(this.transferUsagePie(null, itemName));
			} else {
				usageList.forEach(usage -> pieList.add(this.transferUsagePie(usage, itemName)));
				pieList.sort((a, b) -> a.getPieDate().compareTo(b.getPieDate()));
			}
			map.put(itemName, pieList);
		}

		User user = userService.get(userId);
		String realTime = user.getUsage();
		String[] realTimes = realTime.split(",");
		for (String str : realTimes) {
			List<TerminalUsagePie> usagePie = map.get(str.split(":")[0]);
			for (TerminalUsagePie usage : usagePie) {
				usage.setIsshow(Integer.parseInt(str.split(":")[1]));
			}
			result.add(usagePie);
		}

		return result;
	}

	@Override
	public List<Pie> getRealStatus(String terminalId, Long userId) {
		List<Pie> pieList = new ArrayList<Pie>();
		Map<String, Pie> map = new HashMap<String, Pie>();
		Date dayStart = UTCTime.getLastNHours(alertProcessService.getLastNhours());
		for (String itemName : AlertConstants.getRealItems()) {
			List<Object[]> list = terminalStatusDao.getTerminalRealStatus(terminalId, itemName, dayStart);
			Pie pie = alertProcessService.getRealItemResultPie(itemName, list, null);
			pieList.add(pie);
			map.put(pie.getName(), pie);
		}
		User user = userService.get(userId);
		String realTime = user.getRealTime();
		String[] realTimes = realTime.split(",");
		List<Pie> newPieList = new ArrayList<Pie>();
		for (String str : realTimes) {
			Pie pie = map.get(str.split(":")[0]);
			pie.setIsshow(Integer.parseInt(str.split(":")[1]));
			newPieList.add(pie);
		}

		return newPieList;
	}

	private TerminalUsagePie transferUsagePie(UsageMessageInfo usage, String itemName) {
		TerminalUsagePie pie = new TerminalUsagePie();
		pie.setName(itemName);
		if (usage != null) {
			int totals = usage.getItemTotals() == null ? 0 : usage.getItemTotals().intValue();
			int errors = usage.getItemErrors() == null ? 0 : usage.getItemErrors().intValue();
			pie.setTotal(totals);
			pie.setRedCount(errors);
			pie.setGreenCount(totals - errors);
			pie.setReportCycle(usage.getMsgCycle());
			pie.setPieDate(usage.getCreateDate());
			pie.setStartTime(usage.getStartTime());
			pie.setEndTime(usage.getEndTime());
		}
		pie.setGreyTitle("Unavailable");
		pie.setGreenTitle("Normal");
		pie.setRedTitle("Abnormal");
		return pie;
	}

	@Override
	public Terminal validateTerminal(String tsn) {
		Terminal terminal = get(tsn);
		if (terminal == null) {
			throw new BusinessException("tsn.notFound", new String[] { tsn });
		}
		return terminal;

	}
	
	@Override
	public UnregisteredTerminal validateUnregisteredTerminal(String tsn) {
		UnregisteredTerminal uterminal = terminalNotRegisteredDao.getUnregisteredTerminalBySN(tsn);
		if (uterminal == null) {
			throw new BusinessException("tsn.notFound", new String[] { tsn });
		}
		return uterminal;
	}

	@Override
	public List<String> getGroupNameByTsn(String tsn) {
		if (StringUtils.isEmpty(tsn)) {
			throw new BusinessException("tsn.Required");
		}
		List<String> list = terminalGroupDao.getGroupNamesByTsn(tsn);
		List<String> result = Terminal.removeParentGroupName(list);
		return result;
	}

	@Override
	public void dismissByGroup(Group group, BaseForm command) {

		List<String> tsnList = terminalDao.getTsnsByGroupId(group.getId());
		if (CollectionUtils.isEmpty(tsnList)) {
			return;
		}

		if (group.isEnterPriceGroup()) {

			delete(tsnList);

		} else {

			dismiss(tsnList, group, command);
		}

	}

	@Override
	public List<Map<String, String>> getTerminalStatusChangedMessage(Collection<String> tsnList, String model,
			String op) {
		if (CollectionUtils.isEmpty(tsnList)) {
			return Collections.emptyList();
		}
		List<Map<String, String>> msgList = new ArrayList<>();
		for (String tsn : tsnList) {
			Map<String, String> msgMap = new HashMap<>();
			msgMap.put("tid", tsn);
			msgMap.put("tsn", tsn);
			msgMap.put("model", model);
			msgMap.put("op", op);
			msgList.add(msgMap);
		}
		return msgList;
	}

	@Override
	public List<Map<String, String>> getTerminalStatusChangedMessage(Collection<String> tsnList,
			Map<String, Terminal> terminalMap, String op) {
		if (CollectionUtils.isEmpty(tsnList)) {
			return Collections.emptyList();
		}
		List<Map<String, String>> msgList = new ArrayList<>();
		for (String tsn : tsnList) {
			Map<String, String> msgMap = new HashMap<>();
			Terminal terminal = terminalMap.get(tsn);
			msgMap.put("tid", tsn);
			msgMap.put("tsn", tsn);
			msgMap.put("model", terminal.getModel().getName());
			msgMap.put("op", op);
			msgList.add(msgMap);
		}
		return msgList;
	}

	@Override
	public List<Map<String, String>> getTerminalStatusChangedMessage(Collection<String> tsnList, String op) {
		if (CollectionUtils.isEmpty(tsnList)) {
			return Collections.emptyList();
		}
		List<Map<String, String>> msgList = new ArrayList<>();
		for (String tsn : tsnList) {
			Map<String, String> msgMap = new HashMap<>();
			Terminal terminal = get(tsn);
			msgMap.put("tid", tsn);
			msgMap.put("tsn", tsn);
			if(null != terminal){
			    msgMap.put("model", terminal.getModel().getId());
			}
			msgMap.put("op", op);
			msgList.add(msgMap);
		}
		return msgList;
	}

	@Override
	public void sendTerminalStatusChangedMessage(List<Map<String, String>> msgList) {
		if (redisClient != null && CollectionUtils.isNotEmpty(msgList)) {
			try {
				redisClient.getRedisTemplate().boundListOps("TERMINAL_CHANGED_MESSAGE_QUEUE")
						.leftPush(Json.encode(msgList));
			} catch (Exception e) {
				LOGGER.error("Failed to publish terminal status changed message", e);
			}

		}
	}

	@Override
	public List<String> getTerminalModels(Long groupId) {
		return terminalDao.getTerminalModels(groupId);
	}

	@Override
	public Map<String, String> getTrmGroupNames() {
		List<Map<String, Object>> trmGroupList = terminalDao.getTerminalGroupList();

		Map<String, String> namesMap = new HashMap<>();
		for (Map<String, Object> map : trmGroupList) {
			String tsn = (String) map.get("tsn");
			String groupName = (String) map.get("groupName");
			if (StringUtils.isEmpty(groupName)) {
				continue;
			}
			if (namesMap.containsKey(tsn)) {
				// 在Excel中自动换行显示
				namesMap.put(tsn, namesMap.get(tsn) + (char) 10 + groupName);
			} else {
				namesMap.put(tsn, groupName);
			}
		}
		return namesMap;
	}

	@Override
	public List<Map<String, Object>> exportList(QueryTerminalForm command) {
		List<Map<String, Object>> terminalList = terminalDao.list(command);
		List<TerminalStatus> terminalStatusList = terminalRealStatusDao.list();
		Map<String, String> groups = this.getTrmGroupNames();
		terminalList.forEach(item -> {
			String tsn = (String) item.get("tsn");
			item.put("groupNames", groups.get(tsn));
			Object isOnline = item.get("ts.isOnline");
			if (isOnline == null || "2".equals(isOnline.toString())) {
				item.put("ts.isOnline", "Offline");
			} else {
				item.put("ts.isOnline", "Online");
			}

			boolean status = (boolean) item.get("status");
			if (status) {
				item.put("status", "Active");
			} else {
				item.put("status", "Deactivated");
			}
			
			//新增实时状态
			item.put("tamper", "Unavailable");
			item.put("privacyShield", "Unavailable");
			item.put("stylus", "Unavailable");
			item.put("sred", "Unavailable");
			item.put("rki", "Unavailable");
			
			for(TerminalStatus ter:terminalStatusList)
			{
				if(tsn.equals(ter.getTsn()))
				{
					String sts = ter.getTamper();
					if (StringUtils.isEmpty(sts)) {
						item.put("tamper", "Unavailable");
					} else if ("0000".equals(sts)) {
						item.put("tamper", "Not Tampered");
					} else {
						item.put("tamper", "Tampered");
					}
					
					if(ter.getPrivacyShield()!=null)
					{
						if(ter.getPrivacyShield().intValue()==1)
						{
							item.put("privacyShield", "Attached");
						}else if(ter.getPrivacyShield().intValue()==2)
						{
							item.put("privacyShield", "Removed");
						}
					}
					
					if(ter.getStylus()!=null)
					{
						if(ter.getStylus().intValue()==1)
						{
							item.put("stylus", "Attached");
						}else if(ter.getStylus().intValue()==2)
						{
							item.put("stylus", "Removed");
						}
					}
					
					if(ter.getSred()!=null)
					{
						if(ter.getSred().intValue()==0)
						{
							item.put("sred", "Not Encrypted");
						}else if(ter.getSred().intValue()==1)
						{
							item.put("sred", "Encrypted");
						}
					}
					
					if(ter.getRki() !=null)
                    {
                        if(ter.getRki().intValue()==0)
                        {
                            item.put("rki", "Not Support");
                        }else if(ter.getRki().intValue()==1)
                        {
                            item.put("rki", "Support");
                        }
                    }
					
					if(null != ter.getLastConnTime())
					{
						item.put("lastAccessed", ter.getLastConnTime());
					}
				}
			}
		});
		auditLogService.addAuditLog(Arrays.asList(""), command, OperatorLogForm.EXPORT_TERMINALS, null);
		return terminalList;
	}
	
	@Override
	public Map<String, Object> assignNotRegistered(String[] tsns, AssignTerminalForm command) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Group group = groupService.validateGroup(command.getGroupId());
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		for (String tsn : tsns) {
			validateUnregisteredTerminal(tsn);
		}
		//新增terminal
		for(String tsn:tsns) {
			UnregisteredTerminal uterminal = terminalNotRegisteredDao.getUnregisteredTerminalBySN(tsn);
			saveNotRegisteredTerminal(group, uterminal.getModel(), tsn, command);
		}
		return resultMap;
	}
	//目前一个用户只能属于一个组，但是这里的处理当成一个用户可以属于多个组
	@Override
	public boolean checkTerminalInGroup(String sn, Long userId) {
		List<Long> groups = userGroupDao.getGroupIdsOld(userId);
		List<String> tids = new ArrayList<>();
		if(null == groups || groups.isEmpty()){
			return false;
		}
		for(Long gId:groups){
			List<String> temp = terminalGroupDao.getTerminalByGroupId(gId);
			for(String s:temp){
				tids.add(s);
			}
		}
		if(tids.isEmpty()){
			return false;
		}
		for(String s:tids){
			if(s.equalsIgnoreCase(sn)){
				return true;
			}
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	private void saveNotRegisteredTerminal(Group group, Model model, String tsn, AssignTerminalForm command){
		/** business **/
		String start = tsn;
		String end = tsn;
		String tsnRange = start;
		if (StringUtils.isNotEmpty(end)) {
			tsnRange = tsnRange + "-" + end;
		}
		String[] tsnRanges = new String[] { tsnRange };
		Set<String> totalTsns = validateTsnRanges(tsnRanges);
		Object[] arr = terminalDao.getExistedAndNotAcceptableTsns(totalTsns, command.getLoginUserId());
		Map<String, List<Long>> existedTsns = (Map<String, List<Long>>) arr[0];
		List<String> notAcceptableTsns = (List<String>) arr[1];
		Terminal terminal = new Terminal();
		terminal.setModel(model);
		terminal.setCountry(group.getCountry());
		terminal.setProvince(group.getProvince());
		terminal.setCity(group.getCity());
		terminal.setAddress(group.getAddress());
		terminal.setZipCode(group.getZipCode());
		terminal.setTimeZone(group.getTimeZone());
		terminal.setDaylightSaving(group.isDaylightSaving());
		terminal.setSyncToServerTime(false);//默认不同步
		terminal.setDescription(group.getDescription());

		Object[] needAndIgnoreToAssignGroup = getExistTsnNeedAndIgnoreToAssignGroup(existedTsns, notAcceptableTsns,
				group);
		List<String> existTsnNeedToAssignGroup = (List<String>) needAndIgnoreToAssignGroup[0];
		if (CollectionUtils.isNotEmpty(existTsnNeedToAssignGroup)) {
			long batchId = DbHelper.generateId("TMSPTSNS_BATCH_ID", 1);
			terminalDao.addTemporaryTsns(batchId, existTsnNeedToAssignGroup);
			Map<Long, Collection<String>> tsnAncestorGroupMap = terminalGroupDao.getTsnsDistinctGroupAncestor(batchId,
					group.getId());
			// list the terminal group before bulid relationship between
			// terminal and group
			Map<String, Collection<Long>> tsnGroupIdsMap = terminalGroupDao.getTerminalGroupIds(batchId);
			terminalGroupDao.insertTerminalGroups(tsnAncestorGroupMap, command);
			groupDeployService.copyNewLineGroupTaskToTerminals(tsnGroupIdsMap, group, command);
			// send redis message
			List<Map<String, String>> msgList = getTerminalStatusChangedMessage(existTsnNeedToAssignGroup,
					RedisOperatorForm.EDIT);
			sendTerminalStatusChangedMessage(msgList);
			terminalDao.deleteTemporaryTsns(batchId);
		}
		Collection<String> newTsns = getNewTsns(totalTsns, existedTsns.keySet());
		if (CollectionUtils.isNotEmpty(newTsns)) {
			Map<String, Terminal> terminalMap = getTerminalMap(newTsns, terminal);
			terminalDao.addTerminals(newTsns, terminalMap, command);
			long batchId = DbHelper.generateId("TMSPTSNS_BATCH_ID", 1);
			terminalDao.addTemporaryTsns(batchId, newTsns);
			Map<Long, Collection<String>> tsnAncestorGroupMap = terminalGroupDao.getTsnsDistinctGroupAncestor(batchId,
					group.getId());
			terminalGroupDao.insertTerminalGroups(tsnAncestorGroupMap, command);
			groupDeployService.copyAncestorGroupTaskToTerminals(batchId, newTsns, group.getId(), command, true);
			terminalNotRegisteredDao.deleteTerminalNotRegisters(newTsns);

			// send redis message
			List<Map<String, String>> msgList = getTerminalStatusChangedMessage(newTsns, model.getName(),
					RedisOperatorForm.ASSIGN);
			sendTerminalStatusChangedMessage(msgList);
			terminalDao.deleteTemporaryTsns(batchId);

		}
		terminalDao.flush();
		deployParaService.doProcessDeployList();
		// add audit log
		if (!group.isRoot()) {
			auditLogService.addAuditLog(newTsns, command, OperatorLogForm.ASSIGN_TERMINAL," to Group " + group.getNamePath());
			eventService.addEventLog(newTsns, OperatorEventForm.ASSIGN_TERMINAL, " to Group " + group.getNamePath());
		} else {
			auditLogService.addAuditLog(newTsns, command, OperatorLogForm.ASSIGN_TERMINAL, " to Group " + group.getName());
			eventService.addEventLog(newTsns, OperatorEventForm.ASSIGN_TERMINAL, " to Group " + group.getName());
		}
	}
	
}
