/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description: list/add/edit/delete AlertCondition list/add/delete AlertOff
 * Revision History:		
 * Date	                 Author	                Action
 * 20170111  	         Frank           	    Modify 
 * ============================================================================		
 */
package com.pax.tms.monitor.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pax.common.dao.IBaseDao;
import com.pax.common.exception.BusinessException;
import com.pax.common.service.impl.BaseService;
import com.pax.common.web.form.BaseForm;
import com.pax.tms.group.model.Group;
import com.pax.tms.group.service.GroupService;
import com.pax.tms.monitor.dao.AlertConditionDao;
import com.pax.tms.monitor.dao.AlertOffDao;
import com.pax.tms.monitor.dao.AlertSettingDao;
import com.pax.tms.monitor.dao.AlertSubscribeDao;
import com.pax.tms.monitor.model.AlertCondition;
import com.pax.tms.monitor.model.AlertOff;
import com.pax.tms.monitor.model.AlertSetting;
import com.pax.tms.monitor.model.AlertSubscribe;
import com.pax.tms.monitor.web.form.AddAlertOffForm;
import com.pax.tms.monitor.web.form.EditConditionForm;
import com.pax.tms.monitor.web.form.OperatorEventForm;
import com.pax.tms.user.model.User;
import com.pax.tms.user.service.AuditLogService;
import com.pax.tms.user.service.UserService;
import com.pax.tms.user.web.form.OperatorLogForm;

import io.vertx.core.json.Json;

@Transactional
@Service("alertConditionServiceImpl")
public class AlertConditionServiceImpl extends BaseService<AlertCondition, Long> implements AlertConditionService {

	@Autowired
	@Qualifier("alertConditionDaoImpl")
	private AlertConditionDao alertConditionDao;

	@Autowired
	@Qualifier("alertSettingDaoImpl")
	private AlertSettingDao alertSettingDao;

	@Autowired
	@Qualifier("alertSubscribeDaoImpl")
	private AlertSubscribeDao alertSbsDao;

	@Autowired
	@Qualifier("alertOffDaoImpl")
	private AlertOffDao alertOffDao;

	@Autowired
	@Qualifier("eventGrpServiceImpl")
	private EventGrpService eventGrpService;

	@Autowired
	private AuditLogService auditLogService;
	@Autowired
	@Qualifier("userServiceImpl")
	private UserService userService;
	@Autowired
	private GroupService groupService;

	@Override
	public IBaseDao<AlertCondition, Long> getBaseDao() {
		return alertConditionDao;
	}

	public User getUserInfo(BaseForm form) {
		Long userId = form.getLoginUserId();
		User user = userService.get(userId);
		if (user == null) {
			throw new BusinessException("msg.user.notFound");
		}
		return user;

	}

	/**
	 * call by add group
	 */
	@Override
	public void addAlertCondition(Group group, BaseForm command) {
		Long groupId = group.getId();
		AlertSetting selfSetting = alertSettingDao.findByGroupId(groupId);
		Long settingId = 0L;
		if (selfSetting != null) {
			settingId = selfSetting.getSettingId();
		} else {
			AlertSetting setting = new AlertSetting();
			setting.setGroupId(group.getId());
			setting.setCreator(command.getLoginUsername());
			setting.setCreateDate(new Date());
			setting.setModifier(command.getLoginUsername());
			setting.setModifyDate(new Date());
			settingId = alertSettingDao.save(setting);
		}
		final Long condSetId = settingId;
		/**
		 * 是否继承父组的Condition Setting
		 */
		boolean inherit = false;
		List<AlertCondition> condList = alertConditionDao.getListByGroupId(group.getParent().getId());
		if (!condList.isEmpty()) {
			inherit = true;
		}
		if (inherit) {
			condList.forEach(sourceCond -> {
				AlertCondition targetCond = new AlertCondition();
				targetCond.setSettingId(condSetId);
				targetCond.setAlertItem(sourceCond.getAlertItem());
				targetCond.setAlertMessage(sourceCond.getAlertMessage().replace(sourceCond.getGroupName(), "?0")
						.replace(sourceCond.getAlertThreshold(), "?1"));
				targetCond.setAlertSeverity(sourceCond.getAlertSeverity());
				targetCond.setAlertThreshold(sourceCond.getAlertThreshold());
				targetCond.setCreator(command.getLoginUsername());
				targetCond.setCreateDate(new Date());
				targetCond.setModifier(command.getLoginUsername());
				targetCond.setModifyDate(new Date());
				alertConditionDao.save(targetCond);
			});
		} else {
			String condJson = "";
			try {
				InputStream in = AlertConditionServiceImpl.class.getClassLoader()
						.getResourceAsStream("alertCondition.json");
				condJson = IOUtils.toString(in, Charsets.toCharset("UTF-8"));
			} catch (IOException e) {
				throw new BusinessException("msg.initCondition.fileNotFound", e);
			}
			@SuppressWarnings("unchecked")
			List<Map<String, String>> list = Json.decodeValue(condJson, List.class);
			list.forEach(map -> {
				AlertCondition targetCond = new AlertCondition();
				targetCond.setSettingId(condSetId);
				targetCond.setAlertItem(map.get("alertItem"));
				targetCond.setAlertMessage(map.get("alertMessage"));
				targetCond.setAlertSeverity(Integer.valueOf(map.get("alertSeverity")));
				targetCond.setAlertThreshold(map.get("alertThreshold"));
				targetCond.setCreator(command.getLoginUsername());
				targetCond.setCreateDate(new Date());
				targetCond.setModifier(command.getLoginUsername());
				targetCond.setModifyDate(new Date());
				alertConditionDao.save(targetCond);
			});
		}
		// add log
		auditLogService.addAuditLog(Arrays.asList(group.getNamePath()), command, OperatorLogForm.ADD_CONDITION, null);
		eventGrpService.addEventLog(group.getId(), group.getNamePath() + ":" + OperatorEventForm.ADD_CONDITION);

	}

	@Override
	public void deleteCondition(Long groupId, BaseForm command) {
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		Long settingId = alertSettingDao.findByGroupId(groupId).getSettingId();
		List<AlertCondition> condList = alertConditionDao.getListByGroupId(groupId);
		this.deleteByGroupId(groupId);
		for (AlertCondition cond : condList) {
			alertConditionDao.delete(cond.getCondId());
		}
		alertOffDao.deleteBySettingId(settingId);
		alertSettingDao.delete(settingId);
	}

	@Override
	public void editCondition(EditConditionForm[] command, Long groupId) {
		for (EditConditionForm form : command) {
			form.setAlertThreshold(validateThreshold(form.getAlertThreshold()));
			Group group = groupService.get(groupId);
			// add audit log
			AlertCondition cond = alertConditionDao.get(form.getCondId());
			if (cond == null) {
				throw new BusinessException("msg.group.conditionNotFound");
			} else if (cond.getAlertSeverity() != form.getAlertSeverity()
					|| !cond.getAlertThreshold().equals(form.getAlertThreshold())) {

				cond.setAlertThreshold(form.getAlertThreshold());
				cond.setModifier(form.getLoginUsername());
				cond.setModifyDate(new Date());
				// add log
				if (form.getAlertSeverity() == 0) {
					auditLogService.addAuditLog(Arrays.asList(cond.getAlertItem()), form,
							OperatorLogForm.DISABLE_CONDITION, " in " + group.getNamePath());
					eventGrpService.addEventLog(group.getId(), group.getNamePath() + ":"
							+ OperatorEventForm.DISABLE_CONDITION + " of " + cond.getAlertItem());
				} else if (cond.getAlertSeverity() == 0) {
					auditLogService.addAuditLog(Arrays.asList(cond.getAlertItem()), form,
							OperatorLogForm.ENABLE_CONDITION, " in " + group.getNamePath());
					eventGrpService.addEventLog(group.getId(),
							group.getNamePath() + ":" + OperatorEventForm.ADD_CONDITION + " of " + cond.getAlertItem());
				} else {
					auditLogService.addAuditLog(Arrays.asList(cond.getAlertItem()), form,
							OperatorLogForm.EDIT_CONDITION, " in " + group.getNamePath());
					eventGrpService.addEventLog(group.getId(), group.getNamePath() + ":"
							+ OperatorEventForm.EDIT_CONDITION + " of " + cond.getAlertItem());
				}
				cond.setAlertSeverity(form.getAlertSeverity());
				alertConditionDao.update(cond);
			}

			/*
			 * subscribe
			 */
			boolean isSms = "1".equals(form.getScbSms());
			boolean isEmail = "1".equals(form.getScbEmail());
			AlertSubscribe alertSbs = alertSbsDao.getByUserCond(form.getLoginUserId(), cond.getCondId());
			if (isSms || isEmail) {
				if (alertSbs == null) {
					alertSbs = new AlertSubscribe();
					alertSbs.setCondId(cond.getCondId());
					alertSbs.setUserId(form.getLoginUserId());
					alertSbs.setScbSms(form.getScbSms());
					alertSbs.setScbEmail(form.getScbEmail());
					alertSbs.setCreator(form.getLoginUsername());
					alertSbs.setCreateDate(new Date());
					alertSbs.setModifier(form.getLoginUsername());
					alertSbs.setModifyDate(new Date());
					alertSbsDao.save(alertSbs);
				} else {
					if (!alertSbs.getScbEmail().equals(form.getScbEmail())) {
						alertSbs.setScbEmail(form.getScbEmail());
					}
					if (!alertSbs.getScbSms().equals(form.getScbSms())) {
						alertSbs.setScbSms(form.getScbSms());
					}
					alertSbs.setModifier(form.getLoginUsername());
					alertSbs.setModifyDate(new Date());
					alertSbsDao.update(alertSbs);
				}
				auditLogService.addAuditLog(Arrays.asList(cond.getAlertItem() + " notification mode in Group "), form,
						OperatorLogForm.EDIT_ALERT_SUBSCRIBE, group.getNamePath());
			} else {
				/**
				 * 取消两种订阅
				 */
				if (alertSbs != null) {
					alertSbsDao.delete(alertSbs.getSbscrbId());
					// add log
					auditLogService.addAuditLog(Arrays.asList(cond.getAlertItem() + " notification mode in Group "),
							form, OperatorLogForm.EDIT_ALERT_SUBSCRIBE, group.getNamePath());
				}
			}
		}

	}

	@Override
	public List<AlertCondition> getAlertConditionList(Long groupId, Long userId) {
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		return alertConditionDao.getListByCondUser(groupId, userId);
	}

	@Override
	public List<AlertCondition> getAlertConditionListByGroupId(Long groupId) {
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		return alertConditionDao.getListByGroupId(groupId);
	}

	@Override
	public void addAlertOff(AddAlertOffForm command) {
		Group group = groupService.get(command.getGroupId());
		AlertOff alertOff = new AlertOff();
		Long settingId = 0L;
		AlertSetting selfSetting = alertSettingDao.findByGroupId(command.getGroupId());
		if (selfSetting != null) {
			settingId = selfSetting.getSettingId();
		} else {
			AlertSetting setting = new AlertSetting();
			setting.setGroupId(group.getId());
			setting.setCreator(command.getLoginUsername());
			setting.setCreateDate(new Date());
			setting.setModifier(command.getLoginUsername());
			setting.setModifyDate(new Date());
			settingId = alertSettingDao.save(setting);
		}
		alertOff.setSettingId(settingId);
		alertOff.setRepeatType(command.getRepeatType());
		alertOff.setOffDate(command.getOffDate());
		alertOff.setOffStartTime(command.getOffStartTime());
		alertOff.setOffEndTime(command.getOffEndTime());
		alertOff.setCreator(command.getLoginUsername());
		alertOff.setCreateDate(new Date());
		alertOff.setModifier(command.getLoginUsername());
		alertOff.setModifyDate(new Date());
		alertOffDao.save(alertOff);
		// add log
		String[] type = new String[] { "One Time", "Daily", "Weekly", "Monthly", "Yearly" };
		auditLogService.addAuditLog(Arrays.asList(type[command.getRepeatType()]), command,
				OperatorLogForm.ADD_ALERT_OFF, " in " + group.getNamePath());
		eventGrpService.addEventLog(group.getId(),
				group.getNamePath() + ":" + OperatorEventForm.ADD_ALERT_OFF + " of " + type[command.getRepeatType()]);
	}

	@Override
	public List<AlertOff> getAlertOffList(Long settingId) {
		if (settingId == null) {
			throw new BusinessException("msg.setting.settingRequired");
		}
		return alertOffDao.getListBySettingId(settingId);
	}

	@Override
	public List<AlertOff> getAlertOffListByGroupId(Long groupId) {
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		AlertSetting as = alertSettingDao.findByGroupId(groupId);
		if (as == null) {
			throw new BusinessException("msg.setting.settingRequired");
		}
		return alertOffDao.getListBySettingId(as.getSettingId());
	}

	@Override
	public void deleteAlertOff(Long offId, BaseForm command) {
		Group group = groupService.get(command.getGroupId());
		if (offId == null) {
			throw new BusinessException("msg.setting.alertOffNotFound");
		}
		// add log
		String[] type = new String[] { "One Time", "Daily", "Weekly", "Monthly", "Yearly" };
		auditLogService.addAuditLog(Arrays.asList(type[alertOffDao.get(offId).getRepeatType()]), command,
				OperatorLogForm.DELETE_ALERT_OFF, " in " + group.getNamePath());
		eventGrpService.addEventLog(group.getId(), group.getNamePath() + ": " + OperatorEventForm.DELETE_ALERT_OFF
				+ " of " + type[alertOffDao.get(offId).getRepeatType()]);
		alertOffDao.delete(offId);
	}

	@Override
	public AlertSetting findSettingByGroupId(Long groupId) {
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		return alertSettingDao.findByGroupId(groupId);
	}

	@Override
	public void deleteAlertOffInfo(Long userId, Long groupId) {
		List<AlertCondition> condList = getAlertConditionList(groupId, userId);
		for (AlertCondition cond : condList) {
			alertSbsDao.deleteUserScribe(cond.getCondId(), userId);
		}

	}

	@Override
	public void deleteByGroupId(Long groupId) {
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		List<AlertCondition> condList = alertConditionDao.getListByGroupId(groupId);
		for (AlertCondition cond : condList) {
			alertSbsDao.deleteByCondId(cond.getCondId());
		}
	}

	public String validateThreshold(String threshold) {
		if (!Pattern.matches("[0-9]+$", threshold)) {
			throw new BusinessException("msg.group.illegalThreshold");
		}
		return threshold;
	}
}
