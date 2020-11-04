/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) YYYY-? PAX Technology, Inc. All rights reserved.		
 * Description:  add user log info  		
 * Revision History:		
 * Date	        Author	               Action
 * 20170111     Carry            add user log info  
 * ============================================================================		
 */
package com.pax.tms.user.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pax.common.dao.IBaseDao;
import com.pax.common.exception.BusinessException;
import com.pax.common.service.impl.BaseService;
import com.pax.common.web.form.BaseForm;
import com.pax.tms.user.dao.UserLogDao;
import com.pax.tms.user.model.User;
import com.pax.tms.user.model.UserLog;

@Service("userLogServiceImpl")
public class UserLogServiceImpl extends BaseService<UserLog, Long> implements UserLogService {

	@Autowired
	private UserLogDao userLogDao;

	@Override
	public IBaseDao<UserLog, Long> getBaseDao() {
		return userLogDao;
	}

	@Override
	public void addUserLog(BaseForm command, User user, String action) {
		UserLog log = new UserLog();
		// check if user has login
		if (command.getLoginUserId() == null) {
			log.setUsername(user.getUsername());
			String role = getUserRoleInfos(user.getId());
			log.setRole(role);
		} else {
			log.setUsername(command.getLoginUsername());
			String role = getUserRoleInfos(command.getLoginUserId());
			log.setRole(role);
		}
		log.setClientIp(command.getClientIp());
		log.setEventTime(new Date());
		log.setEventAction(action);
		userLogDao.save(log);
	}

	@Override
	public void addUserLogInfo(String action) {
		UserLog log = new UserLog();
		log.setUsername("System");
		log.setRole("");
		String clientIp = getClientIp();
		log.setClientIp(clientIp);
		log.setEventTime(new Date());
		log.setEventAction(action);
		userLogDao.save(log);
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
	public String getLogRoleInfo(Long userId) {
		return getUserRoleInfos(userId);
	}

	private String getUserRoleInfos(Long userId) {
		List<String> roleList = userLogDao.getUserRoles(userId);
		String roles = joinUserRoles(roleList);
		String role = roles.toString();
		if (role != null && role.length() > 253) {
			role = role.substring(0, 252) + " ...";
		}
		return role;
	}

	private String joinUserRoles(List<String> roles) {
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
	public void systemClearUserLog(Date when) {
		userLogDao.systemClearUserLog(when);
	}

}
