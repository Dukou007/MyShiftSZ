/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description:  add audit log info	
 * Revision History:		
 * Date	        Author	            Action
 * 20170111     Carry         add audit log info
 * ============================================================================		
 */
package com.pax.tms.user.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pax.common.dao.IBaseDao;
import com.pax.common.exception.BusinessException;
import com.pax.common.pagination.IPageForm;
import com.pax.common.pagination.Page;
import com.pax.common.service.impl.BaseService;
import com.pax.common.web.form.BaseForm;
import com.pax.tms.group.service.GroupService;
import com.pax.tms.user.dao.AuditLogDao;
import com.pax.tms.user.dao.UserDao;
import com.pax.tms.user.dao.UserLogDao;
import com.pax.tms.user.model.PubAuditLog;
import com.pax.tms.user.model.User;
import com.pax.tms.user.web.form.OperatorLogForm;
import com.pax.tms.user.web.form.QueryAuditLogForm;

@Service("auditLogServiceImpl")
public class AuditLogServiceImpl extends BaseService<PubAuditLog, Long> implements AuditLogService {

	@Autowired
	private AuditLogDao auditLogDao;

	@Autowired
	private UserLogDao userLogDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private GroupService groupService;

	@Override
	public IBaseDao<PubAuditLog, Long> getBaseDao() {
		return auditLogDao;
	}

	@Override
	public <E, S extends IPageForm> Page<E> page(S command) {
		QueryAuditLogForm form = (QueryAuditLogForm) command;
		// check group permission
		groupService.checkPermissionOfGroup(form, form.getGroupId());
		return super.page(form);
	}

	@Override
	public void addAuditLog(Collection<String> objects, BaseForm command, String message, String additionMessage) {
		User user = null;
		OperatorLogForm form = new OperatorLogForm();
		// check user have login
		if (command.getLoginUserId() == null) {
			for (String userName : objects) {
				if (user == null) {
					user = userDao.findByUsername(userName);
				}
			}
			if (user == null) {
				return;
			}
			form.setUsername(user.getUsername());
			String roles = getUserRoleInfos(user.getId());
			form.setRoles(roles);
			form.setMessage(message);
			form.setAdditionMessage(additionMessage);
			form.setUserId(user.getId());
		} else {
			form.setUsername(command.getLoginUsername());
			String role = getUserRoleInfos(joinUserRoles(command.getLoginUserRoles()));
			form.setRoles(role);
			form.setMessage(message);
			form.setAdditionMessage(additionMessage);
			form.setUserId(command.getLoginUserId());
		}
		auditLogDao.addAuditLog(objects, form);
	}

	public User getUserInfo(BaseForm form) {
		Long userId = form.getLoginUserId();
		User user = userDao.get(userId);
		if (user == null) {
			throw new BusinessException("msg.user.notFound");
		}
		return user;
	}

	// get user's PPM roles
	public String getUserRoleInfos(Long userId) {
		List<String> roleList = userLogDao.getUserRoles(userId);
		String roles = joinUserRoles(roleList);
		return getUserRoleInfos(roles);
	}

	// get user's PPM roles
	public String getUserRoleInfos(String role) {
		if (role != null && role.length() > 253) {
			return role.substring(0, 252) + " ...";
		} else {
			return role;
		}
	}

	private String joinUserRoles(Collection<String> roles) {
		if (roles == null || roles.isEmpty()) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String str : roles) {
			if (!first) {
				sb.append(",");
			} else {
				first = false;
			}
			sb.append(str);
		}
		return sb.toString();
	}

	@Override
	public void addAuditLogs(String actionName) {
		PubAuditLog log = new PubAuditLog();
		log.setUsername("System");
		log.setRole("");
		User user = new User();
		user.setId(1L);
		log.setUser(user);
		String clientIp = getClientIp();
		log.setClientIp(clientIp);
		log.setActionName(actionName);
		log.setActionDate(new Date());
		auditLogDao.save(log);
	}

	public String getClientIp() {
		String ip = null;
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			throw new BusinessException(e);
		}
		return ip;
	}

	@Override
	public boolean checkAuditLogs(String action) {
		return auditLogDao.checkAuditLogs(action);
	}

	@Override
	public void systemClear(Date when) {
		auditLogDao.systemClear(when);
	}
}
