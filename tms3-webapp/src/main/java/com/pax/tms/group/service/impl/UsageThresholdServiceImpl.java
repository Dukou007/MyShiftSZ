/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description: Create/Modify usageThreshold
 * Revision History:		
 * Date	                 Author	                Action
 * 20170111  	         Aaron           	    Modify UsageThresholdServiceImpl
 * ============================================================================		
 */
package com.pax.tms.group.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pax.common.dao.IBaseDao;
import com.pax.common.exception.BusinessException;
import com.pax.common.service.impl.BaseService;
import com.pax.common.web.form.BaseForm;
import com.pax.tms.group.dao.UsageThresholdDao;
import com.pax.tms.group.model.Group;
import com.pax.tms.group.model.UsageThreshold;
import com.pax.tms.group.service.GroupService;
import com.pax.tms.group.service.UsageThresholdService;
import com.pax.tms.group.web.form.EditUsageThresholdForm;
import com.pax.tms.monitor.service.EventGrpService;
import com.pax.tms.monitor.web.form.OperatorEventForm;
import com.pax.tms.user.model.User;
import com.pax.tms.user.security.AclManager;
import com.pax.tms.user.service.AuditLogService;
import com.pax.tms.user.service.UserService;
import com.pax.tms.user.web.form.OperatorLogForm;

import io.vertx.core.json.Json;

@Service("usageThresholdServiceImpl")
public class UsageThresholdServiceImpl extends BaseService<UsageThreshold, Long> implements UsageThresholdService {

	@Autowired
	private UsageThresholdDao usageThresholdDao;

	@Autowired
	private GroupService groupService;

	@Autowired
	private AuditLogService auditLogService;

	@Autowired
	private UserService userService;

	@Autowired
	private EventGrpService eventService;

	@Override
	public IBaseDao<UsageThreshold, Long> getBaseDao() {
		return usageThresholdDao;
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
	public void editUsageThreshold(EditUsageThresholdForm command) {

		Long groupId = command.getGroupId();
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		Group group = groupService.validateGroup(groupId);
		List<UsageThreshold> usageList = usageThresholdDao.list(groupId);
		if (!(command.getItemName().length == command.getThdValue().length
				&& command.getItemName().length == command.getReportCycle().length)) {
			throw new BusinessException("msg.usage.notEqualLength");
		}
		int length = command.getItemName().length;

		AclManager.checkPermissionOfUsage(command.getLoginUserId(), usageList);
		for (int i = 0; i < length; i++) {
			UsageThreshold usageThreshold = usageList.get(i);

			String thdValue = command.getThdValue()[i];

			if (!Pattern.matches("[0-9]+$", thdValue)) {
				throw new BusinessException("msg.usage.illegalThdValue");
			}

			int value = Integer.parseInt(thdValue);
			if (i != length - 1 && (value < 0 || value > 99)) {
				throw new BusinessException("msg.usage.illegalPreThdValue");
			}

			if (!usageThreshold.getThdValue().equals(thdValue)) {
				eventService.addEventLog(group.getId(), group.getNamePath() + ":"
						+ OperatorEventForm.EDIT_GLOBAL_SETTING + " " + usageThreshold.getItemName());
			}

			if (!usageThreshold.getReportCycle().equals(command.getReportCycle()[i])) {
				eventService.addEventLog(group.getId(), group.getNamePath() + ":"
						+ OperatorEventForm.EDIT_GLOBAL_SETTING + " " + usageThreshold.getItemName());
			}

			usageThreshold.setItemName(command.getItemName()[i]);
			usageThreshold.setThdValue(thdValue);
			usageThreshold.setReportCycle(command.getReportCycle()[i]);
			usageThreshold.setModifier(command.getLoginUsername());
			usageThreshold.setModifyDate(command.getRequestTime());

			usageThresholdDao.update(usageThreshold);
		}

		// add audit log
		auditLogService.addAuditLog(Arrays.asList(group.getNamePath()), command, OperatorLogForm.EDIT_GLOBAL_SETTING,
				null);

	}

	@Override
	public void saveUsageThreshold(BaseForm command, Group group) {
		String usageJson = "";
		try {
			InputStream in = getResourceAsStream("globalSetting.json");
			usageJson = IOUtils.toString(in, Charsets.toCharset("UTF-8"));
		} catch (IOException e) {
			throw new BusinessException("msg.usage.fileNotFound", e);
		}
		@SuppressWarnings("unchecked")
		List<Map<String, String>> list = Json.decodeValue(usageJson, List.class);
		for (Map<String, String> map : list) {
			UsageThreshold usageThreshold = new UsageThreshold();
			usageThreshold.setItemName(map.get("itemName"));
			usageThreshold.setThdValue(map.get("thdValue"));
			usageThreshold.setReportCycle(map.get("reportCycle"));
			usageThreshold.setGroup(group);
			usageThreshold.setCreator(command.getLoginUsername());
			usageThreshold.setCreateDate(command.getRequestTime());
			usageThreshold.setModifier(command.getLoginUsername());
			usageThreshold.setModifyDate(command.getRequestTime());
			usageThresholdDao.save(usageThreshold);
		}

		eventService.addEventLog(group.getId(), group.getNamePath() + ":" + OperatorEventForm.ADD_GLOBAL_SETTING);

	}

	@Override
	public List<UsageThreshold> list(Long groupId) {
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		List<UsageThreshold> usageList = usageThresholdDao.list(groupId);
		return usageList;
	}

	private static InputStream getResourceAsStream(String resource) {
		String stripped = resource.startsWith("/") ? resource.substring(1) : resource;
		InputStream stream = null;
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader != null) {
			stream = classLoader.getResourceAsStream(stripped);
		}
		if (stream == null) {
			stream = UsageThresholdServiceImpl.class.getResourceAsStream(resource);
		}
		if (stream == null) {
			stream = UsageThresholdServiceImpl.class.getClassLoader().getResourceAsStream(stripped);
		}
		return stream;
	}

}
