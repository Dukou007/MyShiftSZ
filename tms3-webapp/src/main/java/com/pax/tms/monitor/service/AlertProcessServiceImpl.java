/*
 * ============================================================================
 * = COPYRIGHT				
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *   	Copyright (C) 2009-2020 PAX Technology, Inc. All rights reserved.		
 * ============================================================================		
 */
package com.pax.tms.monitor.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.codehaus.jettison.json.JSONException;
import org.pac4j.core.profile.CommonProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.pax.common.dao.IBaseDao;
import com.pax.common.exception.BusinessException;
import com.pax.common.service.impl.BaseService;
import com.pax.common.util.DateTimeUtils;
import com.pax.common.util.DateTimeUtils.DateTimeRange;
import com.pax.common.web.support.editor.UTCDateEditor;
import com.pax.login.TmsPac4jPrincipal;
import com.pax.tms.group.dao.GroupDao;
import com.pax.tms.group.model.Group;
import com.pax.tms.group.model.GroupAncestor;
import com.pax.tms.group.model.UsageThreshold;
import com.pax.tms.group.service.GroupService;
import com.pax.tms.group.service.UsageThresholdService;
import com.pax.tms.monitor.amazon.sns.PaxSmsUtil;
import com.pax.tms.monitor.dao.AlertProcessDao;
import com.pax.tms.monitor.dao.GroupUsageDao;
import com.pax.tms.monitor.domain.GroupUsageCount;
import com.pax.tms.monitor.domain.ResultCount;
import com.pax.tms.monitor.domain.UsageMessageInfo;
import com.pax.tms.monitor.domain.UsageStatusInfo;
import com.pax.tms.monitor.domain.UserSubscribeInfo;
import com.pax.tms.monitor.model.AlertCondition;
import com.pax.tms.monitor.model.AlertEvent;
import com.pax.tms.monitor.model.AlertOff;
import com.pax.tms.monitor.model.GroupUsageStatus;
import com.pax.tms.monitor.model.PublishEmail;
import com.pax.tms.monitor.model.PublishSms;
import com.pax.tms.monitor.model.TerminalReportMessage;
import com.pax.tms.monitor.web.AlertConstants;
import com.pax.tms.monitor.web.SubscribeMailSender;
import com.pax.tms.monitor.web.form.Pie;
import com.pax.tms.monitor.web.form.UsagePie;
import com.pax.tms.terminal.model.TerminalGroup;
import com.pax.tms.user.dao.UserDao;
import com.pax.tms.user.model.User;
import com.pax.tms.user.security.UTCTime;
import com.pax.tms.user.service.UserService;

@Service("alertProcessServiceImpl")
public class AlertProcessServiceImpl extends BaseService<TerminalReportMessage, Long> implements AlertProcessService {

	@Autowired
	@Qualifier("alertProcessDaoImpl")
	private AlertProcessDao alertProcessDao;
	@Autowired(required = false)
	private SubscribeMailSender subscribeMailSender;

	@Autowired
	private UsageThresholdService thdService;

	@Autowired
	private UserService userService;

	@Autowired
	private UserDao userDao;
	
	@Autowired
    private GroupDao groupDao;

	@Autowired
	@Qualifier("alertConditionServiceImpl")
	private AlertConditionService condService;

	@Autowired
	private GroupService groupService;
	
	@Autowired
    @Qualifier("groupUsageDaoImpl")
    private GroupUsageDao groupUsageDao;

	@Override
	public IBaseDao<TerminalReportMessage, Long> getBaseDao() {
		return alertProcessDao;
	}

	private int lastNhours;

	private static final Logger LOGGER = LoggerFactory.getLogger(AlertProcessServiceImpl.class);

	@Override
	public void usageMessageByDay() {
		LOGGER.debug("====getUsageMessageByDay() start====");
		DateTimeRange yesterday = DateTimeUtils.getYesterday();
		String msgCycle = "per day";
		List<UsageMessageInfo> usageList = new LinkedList<>();
		for (String itemName : AlertConstants.getUsageItems()) {
			boolean isGetMessage = alertProcessDao.isGetUsageMessage(itemName, yesterday.getFrom(), yesterday.getTo());
			if (!isGetMessage) {
				usageList.addAll(alertProcessDao.getUsageMessageList(itemName, yesterday.getFrom(), yesterday.getTo(),
						msgCycle));
			}
		}

		if (!usageList.isEmpty()) {
			alertProcessDao.insertUsageMessage(usageList, yesterday.getFrom(), yesterday.getTo(), msgCycle);
		}
		LOGGER.debug("====getUsageMessageByDay() end====");
	}

	@Override
	public void usageStatusByThreshold() {
		LOGGER.debug("====getUsageStatusByThreshold() start====");
		List<UsageStatusInfo> usageList = new LinkedList<>();
		List<Group> enterpriseList = groupService.getEnterpriseGroups();
		List<UsageThreshold> thdList;
		for (Group enterprise : enterpriseList) {
			thdList = thdService.list(enterprise.getId());
			if (thdList == null || thdList.isEmpty()) {
				continue;
			}
			for (UsageThreshold thd : thdList) {
				DateTimeRange dtr = this.getDateTimeRange(thd.getReportCycle(), enterprise);
				boolean isStatus = alertProcessDao.isGetUsageStatus(thd, dtr.getFrom(), dtr.getTo());
				if (!isStatus) {
				    //统计每个终端的失败率
					List<UsageStatusInfo> usList = alertProcessDao.getUsageStatusList(thd, dtr.getFrom(), dtr.getTo());
					//根据组设置的失败率阈值和每个终端的失败率对比，设置对应的值
					final int thdValue = Integer.parseInt(thd.getThdValue());
					final Date startTime = dtr.getFrom();
					final Date endTime = dtr.getTo();
					usList.forEach(usageStatus -> {
						if (usageStatus.getItemRate() == null) {
							/*
							 * If this terminal don't have this item
							 * information, the status 2 - unknown
							 */
							usageStatus.setItemStatus(2);
						} else {
							usageStatus.setItemStatus(usageStatus.getItemRate() > thdValue ? 1 : 0);
						}
						usageStatus.setStartTime(startTime);
						usageStatus.setEndTime(endTime);
						usageStatus.setItemName(thd.getItemName());
						usageStatus.setReportCycle(thd.getReportCycle());
						usageStatus.setThdId(thd.getId());
					});

					usageList.addAll(usList);
				}
			}
		}
		if (!usageList.isEmpty()) {
			alertProcessDao.insertUsageStatus(usageList);
		}
		LOGGER.debug("====getUsageStatusByThreshold() end====");
	}

	public List<UsagePie> getUsagePieList(Group group, Map<String, String> itemsCycle) {
		LOGGER.debug("==== getUsagePieList start====");
		boolean isAlertOff = this.isGroupAlertOff(group);
		List<AlertCondition> condList = condService.getAlertConditionListByGroupId(group.getId());
		List<UsagePie> pieList = new LinkedList<>();
		List<AlertEvent> eventList = new LinkedList<>();
		
		Date groupDate = DateTimeUtils.getTimeZoneDate(new Date(), group.getTimeZone());
	    DateTimeRange yesterday = DateTimeUtils.getYesterday(groupDate);       
		for (String itemName : AlertConstants.getUsageItems()) {
			AlertCondition condition = null;
			for (AlertCondition cond : condList) {
				if (StringUtils.equals(itemName, cond.getAlertItem())) {
					condition = cond;
				}
			}
			DateTimeRange dtr = this.getDateTimeRange(itemsCycle.get(itemName),group);
			boolean isGetGroupUsageStatus = alertProcessDao.isGetGroupUsageStatus(group.getId(), itemName,
					dtr.getFrom(), dtr.getTo(), itemsCycle.get(itemName));
			if (isGetGroupUsageStatus) {
				continue;
			}
			//统计组中失败率是否超过阈值每种状态的设备数
			List<ResultCount> list = alertProcessDao.getGroupUsageStatus(group.getId(), itemName, dtr.getFrom(),
					dtr.getTo(), itemsCycle.get(itemName));
			//统计昨天的记录
			List<GroupUsageStatus> usageList = groupUsageDao.getUsageDetailByDate(group.getId(), DateTimeUtils.format(yesterday.getDate(), DateTimeUtils.PATTERN_DATE));
			GroupUsageCount groupUsage = groupUsageDao.getGroupUsageCount(itemName, group.getId(),  DateTimeUtils.format(yesterday.getDate(), DateTimeUtils.PATTERN_DATE));
			UsagePie pie = this.getUsageItemResultPie(itemName, list, condition,groupUsage);
            pie.setStartTime(yesterday.getFrom());
            pie.setEndTime(yesterday.getTo());
            pie.setReportCycle(UsageThreshold.CYCLE_PER_DAY);
            pie.setGroupId(group.getId());
            pie.setGroupName(group.getName());
			if(null == usageList || usageList.isEmpty()){
			    pieList.add(pie);
            } else {
                for (GroupUsageStatus groupUsageStatus : usageList) {
                    if (StringUtils.equals(itemName, groupUsageStatus.getItemName())) {
                        int total = 0;
                        int errCount = 0;
                        int pendingCount = 0;
                        if(null != groupUsage){
                            total = groupUsage.getTotal() == null ? 0 : groupUsage.getTotal();
                            errCount = groupUsage.getErrCount() == null ? 0 : groupUsage.getErrCount();
                            pendingCount = groupUsage.getPendingCount() == null ? 0 : groupUsage.getPendingCount();
                        }
                        groupUsageStatus.setUnKnownTrms(pendingCount);
                        groupUsageStatus.setTotalTrms(total);
                        groupUsageStatus.setAbnormalTrms(errCount);
                        groupUsageStatus.setNormalTrms(total - errCount - pendingCount);
                        groupUsageDao.updateGroupUsageStatus(groupUsageStatus);
                    }
                }
            }
			
			if (!isAlertOff && pie.isAlertFlag() && condition != null) {
				AlertEvent event = new AlertEvent();
				event.setAlertMsg(condition.getAlertMessage());
				event.setAlertSeverity(condition.getAlertSeverity());
				event.setAlertTime(pie.getPieDate());
				event.setAlertValue(pie.getAlertValue());
				event.setCondId(condition.getCondId());
				event.setGroup(group);
				eventList.add(event);
			}
		}
		LOGGER.debug("==== getUsagePieList end====");
		this.saveAlertEvent(eventList);
		return pieList;
	}

	private DateTimeRange getDateTimeRange(String cycle,Group group) {
		DateTimeRange dtr;
		Date groupDate = DateTimeUtils.getTimeZoneDate(new Date(), group.getTimeZone());
		switch (cycle) {
			case UsageThreshold.CYCLE_PER_MONTH:
				dtr = DateTimeUtils.lastNMonths(1,groupDate);
				break;
			case UsageThreshold.CYCLE_PER_WEEK:
				dtr = DateTimeUtils.getLastWeek(groupDate);
				break;
			case UsageThreshold.CYCLE_PER_DAY:
			default:
				dtr = DateTimeUtils.getYesterday(groupDate);
				break;
		}
		return dtr;
	}
	
	@SuppressWarnings("unused")
	private UsagePie getUsageItemResultPie(String itemName, List<ResultCount> list, AlertCondition condition,GroupUsageCount groupUsage) {
		UsagePie pie = new UsagePie();

		int totals = 0;
		int abNormals = 0;
		int normals = 0;
		int unknowns = 0;
		for (ResultCount result : list) {
			int sts = 2;
			if (result.getItemStatus() != null) {
				sts = result.getItemStatus();
			}
			int cnt = result.getItemCount();
			totals += cnt;
			switch (sts) {
			case 0:
				normals = cnt;
				break;
			case 1:
				abNormals = cnt;
				break;
			case 2:
			default:
				unknowns += cnt;
				break;
			}
		}

		String alertValue = "0";
		int severity = 0;
		int threshold = 0;
		double result = 0;
		if (totals > unknowns) {
			result = (abNormals * 100.0) / (totals - unknowns);
		}
		alertValue = new DecimalFormat("0.0").format(result);
		if (condition == null || condition.getAlertSeverity() == 0) {
			severity = 1; // default Informational
		} else {
			threshold = Integer.valueOf(condition.getAlertThreshold());
			if (result > threshold) {
				// severity = condition.getAlertSeverity();
				severity = 3;
				pie.setAlertFlag(true);
			}
		}

		pie.setName(itemName);
		pie.setAlertValue(alertValue);
		pie.setThreshold(threshold);
		pie.setAlertLevel(severity);
		if(null != groupUsage && null != groupUsage.getTotal() && null != groupUsage.getErrCount()){
		    int total = 0;
            int errCount = 0;
            int pendingCount = 0;
            if(null != groupUsage){
                total = groupUsage.getTotal() == null ? 0 : groupUsage.getTotal();
                errCount = groupUsage.getErrCount() == null ? 0 : groupUsage.getErrCount();
                pendingCount = groupUsage.getPendingCount() == null ? 0 : groupUsage.getPendingCount();
            }
		    pie.setGreenCount(total - errCount - pendingCount);
	        pie.setRedCount(errCount);
	        pie.setGreyCount(pendingCount);
	        pie.setTotal(total);
		}else{
		    pie.setGreenCount(0);
            pie.setRedCount(0);
            pie.setGreyCount(0);
            pie.setTotal(0);
		}
		
		pie.setPieDate(new Date());
		

		return pie;
	}

	@Override
	public void doProcessTerminalUsageStatus() {
		LOGGER.debug("====doProcessTerminalUsageStatus() start====");
		/*
		 * Enterprise Group
		 */
		List<Group> enterpriseList = groupService.getEnterpriseGroups();
		List<UsageThreshold> thdList = null;
		for (Group enterprise : enterpriseList) {
			thdList = thdService.list(enterprise.getId());
			if (thdList == null || thdList.isEmpty()) {
				continue;
			}
			Map<String, String> itemsCycle = new HashMap<>(thdList.size());
			for (UsageThreshold thd : thdList) {
				itemsCycle.put(thd.getItemName(), thd.getReportCycle());
			}
			List<Group> groupList = groupService.getSelfAndDescendantGroup(enterprise.getId());
			List<UsagePie> pieList = new LinkedList<>();
			for (Group group : groupList) {
				pieList.addAll(this.getUsagePieList(group, itemsCycle));
			}
			if (!pieList.isEmpty()) {
				alertProcessDao.insertGroupUsageStatus(pieList);
			}
		}
		LOGGER.debug("====doProcessTerminalUsageStatus() end====");
	}
	
	
	public Long getLoginUserId() {
		Object principal = SecurityUtils.getSubject().getPrincipal();
		Object userId = null;
		if (principal instanceof io.buji.pac4j.subject.Pac4jPrincipal) {
			CommonProfile profile = ((io.buji.pac4j.subject.Pac4jPrincipal) principal).getProfile();
			userId = profile.getAttribute("userId");
			if (userId instanceof Long) {
				return (Long) userId;
			}
		} else if (principal instanceof TmsPac4jPrincipal) {
			CommonProfile profile = ((TmsPac4jPrincipal) principal).getProfile();
			userId = profile.getAttribute("userId");
			if (userId instanceof Long) {
				return (Long) userId;
			}
		}
		return userId == null ? null : Long.parseLong(userId.toString());
	}

	@Override
	public void doProcessGroupRealStatus() {
		LOGGER.debug("====doProcessGroupRealStatus() start====");
		//查询所有一级组
		List<Group> enterpriseList = groupService.getEnterpriseGroups();
		Map<Long, List<GroupAncestor>> parentGroupMap = groupDao.getGroupMappingDescendantGroup();
		Map<Long, List<TerminalGroup>> groupTerminalMap = groupDao.getGroupMappingTerminal();
		for (Group enterprise : enterpriseList) {
			LOGGER.debug("====enterprise: {}====", enterprise.getName());
			//获取一级组以及组下的子组、子孙组
			List<GroupAncestor> groupAncestorList = parentGroupMap.get(enterprise.getId());
			for (GroupAncestor groupAncestor : groupAncestorList) {
			    Group group = groupAncestor.getGroup();
				LOGGER.debug("====Group: {}====id:{}", group.getName(),group.getId());
				// Get day start time, for count the download and activation.
				Date dayStart = UTCTime.getLastNHours(lastNhours);
				//判断组是否绑定终端
				if (groupService.isGroupHasTerminal(group.getId(),groupTerminalMap)) {
					boolean isAlertOff = this.isGroupAlertOff(group);
					this.getRealPieList(group, dayStart, !isAlertOff);
				}
			}
		}
		LOGGER.debug("====doProcessGroupRealStatus() end====");
	}

	private List<Pie> getRealPieList(Group group, Date dayStart, boolean alertFlag) {
		List<AlertCondition> condList = condService.getAlertConditionListByGroupId(group.getId());

		List<Pie> pieList = new ArrayList<>(AlertConstants.getRealItemsCount());
		List<AlertEvent> eventList = new LinkedList<>();
		for (String itemName : AlertConstants.getRealItems()) {
			AlertCondition condition = null;
			for (AlertCondition cond : condList) {
				if (StringUtils.equals(itemName, cond.getAlertItem())) {
					condition = cond;
				}
			}
			List<Object[]> list = alertProcessDao.getGroupRealStatus(group.getId(), itemName, dayStart);
			Pie pie = this.getRealItemResultPie(itemName, list, condition);
			pieList.add(pie);
			if (condition != null && alertFlag && pie.isAlertFlag()) {
				AlertEvent event = new AlertEvent();
				event.setAlertMsg(condition.getAlertMessage());
				event.setAlertSeverity(condition.getAlertSeverity());
				event.setAlertTime(pie.getPieDate());
				event.setAlertValue(pie.getAlertValue());
				event.setCondId(condition.getCondId());
				event.setGroup(group);
				eventList.add(event);
			}
		}
		if (!eventList.isEmpty()) {
			this.saveAlertEvent(eventList);
		}

		return pieList;
	}

	@Cacheable(value = "dashboardCache", key = "'dashboard_'+#group.getIdPath()", sync = false)
	@Override
	public List<Pie> getRealDashboard(Group group, Long userId) throws JSONException {
		Date dayStart = UTCTime.getLastNHours(lastNhours);
		List<Pie> pieList = this.getRealPieList(group, dayStart, false);
		User user = userService.get(userId);
		String realTime = user.getRealTime();
		String[] realTimes = realTime.split(",");
		List<Pie> newPieList = new ArrayList<>();
		Map<String, Pie> map = new HashMap<>();
		for (Pie pie : pieList) {
			map.put(pie.getName(), pie);
		}
		for (String str : realTimes) {
			Pie pie1 = map.get(str.split(":")[0]);
			if (pie1 != null) {
				pie1.setIsshow(Integer.parseInt(str.split(":")[1]));
				newPieList.add(pie1);
			} else {
				throw new BusinessException("msg.realName.notExists");
			}

		}
		return newPieList;
	}

	private void saveAlertEvent(List<AlertEvent> eventList) {
		LOGGER.debug("====saveAlertEvent====");
		eventList.forEach(event -> {
			event.save();
			this.sendAlertMessage(event);
		});
	}

	@Override
	public Pie getRealItemResultPie(String itemName, List<Object[]> list, AlertCondition condition) {
		Pie pie = new Pie();
		Map<String, Integer> resultMap = null;
		switch (itemName) {
			case AlertConstants.TAMPERS:
				this.doCaseTamper(pie, resultMap, list);
				resultMap = this.analyzeTamperResult(list);
				break;
			case AlertConstants.OFFLINE:
				this.doCaseOffline(pie, resultMap, list);
				resultMap = this.analyzeOnlineResult(list);
				break;
			case AlertConstants.PRIVACY_SHIELD:
				this.doCasePrivacyShield(pie, resultMap, list);
				resultMap = this.analyzeIntResult(list);
				break;
			case AlertConstants.SRED:
                this.doCaseSred(pie, resultMap, list);
                resultMap = this.analyzeSredOrRkiResult(list);
                break;
			case AlertConstants.RKI:
                this.doCaseRki(pie, resultMap, list);
                resultMap = this.analyzeSredOrRkiResult(list);
                break;
			case AlertConstants.STYLUS:
				this.doCaseStylus(pie, resultMap, list);
				resultMap = this.analyzeIntResult(list);
				break;
			case AlertConstants.DOWNLOADS:
				this.doCaseDownLoads(pie, resultMap, list);
				resultMap = this.analyzePendingResult(list);
				break;
			case AlertConstants.ACTIVATIONS:
				this.doCaseActivations(pie, resultMap, list);
				resultMap = this.analyzePendingResult(list);
				break;
			default:
				return null;
		}
		int totals = resultMap.get("totals");
		int abNormals = resultMap.get("abNormals");
		int normals = resultMap.get("normals");
		int unknowns = resultMap.get("unknowns");

		String alertValue = "0";
		int severity = 0;
		int threshold = 0;
		double result = 0;
		if (totals > unknowns) {
			result = (abNormals * 100.0) / (totals - unknowns);
		}
		alertValue = new DecimalFormat("0.0").format(result);
		if (condition == null || condition.getAlertSeverity() == 0) {
			severity = 1; // default Informational
		} else {
			threshold = Integer.valueOf(condition.getAlertThreshold());
			if (result > threshold) {
				// severity = condition.getAlertSeverity();
				severity = 3;
				pie.setAlertFlag(true);
			}
		}

		pie.setAlertValue(alertValue);
		pie.setThreshold(threshold);
		pie.setAlertLevel(severity);
		pie.setPieDate(new Date());
		pie.setGreenCount(normals);
		pie.setRedCount(abNormals);
		pie.setGreyCount(unknowns);
		pie.setTotal(totals);
		return pie;
	}

	private void doCaseTamper(Pie pie, Map<String, Integer> resultMap, List<Object[]> list) {
		pie.setName(AlertConstants.TAMPERS);
		pie.setGreyTitle("Unavailable");
		pie.setGreenTitle("Not Tampered");
		pie.setRedTitle("Tampered");
	}

	private void doCaseOffline(Pie pie, Map<String, Integer> resultMap, List<Object[]> list) {
		pie.setName(AlertConstants.OFFLINE);
		pie.setGreyTitle("");
		pie.setGreenTitle("Online");
		pie.setRedTitle("Offline");
	}

	private void doCasePrivacyShield(Pie pie, Map<String, Integer> resultMap, List<Object[]> list) {
		pie.setName(AlertConstants.PRIVACY_SHIELD);
		pie.setGreyTitle("Unavailable");
		pie.setGreenTitle("Attached");
		pie.setRedTitle("Removed");
	}
	
	private void doCaseSred(Pie pie, Map<String, Integer> resultMap, List<Object[]> list) {
        pie.setName(AlertConstants.SRED);
        pie.setGreyTitle("Unavailable");
        pie.setGreenTitle("Encrypted");
        pie.setRedTitle("Not Encrypted");
    }
	
	private void doCaseRki(Pie pie, Map<String, Integer> resultMap, List<Object[]> list) {
        pie.setName(AlertConstants.RKI);
        pie.setGreyTitle("Unavailable");
        pie.setGreenTitle("Support");
        pie.setRedTitle("Not Support");
    }

	private void doCaseStylus(Pie pie, Map<String, Integer> resultMap, List<Object[]> list) {
		pie.setName(AlertConstants.STYLUS);
		pie.setGreyTitle("Unavailable");
		pie.setGreenTitle("Attached");
		pie.setRedTitle("Removed");
	}

	private void doCaseDownLoads(Pie pie, Map<String, Integer> resultMap, List<Object[]> list) {
		pie.setName(AlertConstants.DOWNLOADS);
		pie.setGreyTitle("Pending");
		pie.setGreenTitle("Successful");
		pie.setRedTitle("Failed");
	}

	private void doCaseActivations(Pie pie, Map<String, Integer> resultMap, List<Object[]> list) {
		pie.setName(AlertConstants.ACTIVATIONS);
		pie.setGreyTitle("Pending");
		pie.setGreenTitle("Successful");
		pie.setRedTitle("Failed");

	}

	private Map<String, Integer> analyzeOnlineResult(List<Object[]> list) {
		Map<String, Integer> map = this.analyzeIntResult(list);
		int unknowns = map.get("unknowns");
		int abNormals = map.get("abNormals");
		map.put("abNormals", abNormals + unknowns);
		map.put("unknowns", 0);
		return map;
	}

	private Map<String, Integer> analyzeIntResult(List<Object[]> list) {
		Map<String, Integer> map = new HashMap<>(4);
		int totals = 0;
		int abNormals = 0;
		int normals = 0;
		int unknowns = 0;
		for (Object[] obj : list) {
			int sts;
			if (obj[0] != null) {
				sts = ((Number) obj[0]).intValue();
			} else {
				sts = 3;
			}
			int cnt = ((Number) obj[1]).intValue();
			totals += cnt;
			switch (sts) {
			case 1:
				normals = cnt;
				break;
			case 2:
                abNormals = cnt;
                break;
			case 3:
				unknowns += cnt;
				break;
			default:
				unknowns += cnt;
				break;
			}
		}
		map.put("totals", totals);
		map.put("abNormals", abNormals);
		map.put("normals", normals);
		map.put("unknowns", unknowns);
		return map;
	}
	
	private Map<String, Integer> analyzeSredOrRkiResult(List<Object[]> list) {
        Map<String, Integer> map = new HashMap<>(4);
        int totals = 0;
        int abNormals = 0;
        int normals = 0;
        int unknowns = 0;
        for (Object[] obj : list) {
            int sts;
            if (obj[0] != null) {
                sts = ((Number) obj[0]).intValue();
            } else {
                sts = 3;
            }
            int cnt = ((Number) obj[1]).intValue();
            totals += cnt;
            switch (sts) {
            case 1:
                normals = cnt;
                break;
            case 0:
                abNormals = cnt;
                break;
            case 3:
                unknowns += cnt;
                break;
            default:
                unknowns += cnt;
                break;
            }
        }
        map.put("totals", totals);
        map.put("abNormals", abNormals);
        map.put("normals", normals);
        map.put("unknowns", unknowns);
        return map;
    }

	private Map<String, Integer> analyzeTamperResult(List<Object[]> list) {
		Map<String, Integer> map = new HashMap<>(4);
		int totals = 0;
		int abNormals = 0;
		int normals = 0;
		int unknowns = 0;
		for (Object[] obj : list) {
			int cnt = ((Number) obj[1]).intValue();
			String sts = "";
			if (obj[0] != null) {
				sts = (String) obj[0];
			}
			totals += cnt;
			if (StringUtils.isEmpty(sts)) {
				unknowns += cnt;
			} else if ("0000".equals(sts)) {
				normals += cnt;
			} else {
				abNormals += cnt;
			}
		}
		map.put("totals", totals);
		map.put("abNormals", abNormals);
		map.put("normals", normals);
		map.put("unknowns", unknowns);
		return map;
	}

	private Map<String, Integer> analyzePendingResult(List<Object[]> list) {
		Map<String, Integer> map = new HashMap<>(4);
		int totals = 0;
		int abNormals = 0; // FAILED
		int normals = 0; // SUCCESS, CANCELED
		int unknowns = 0; // PENDING, DOWNLOADING
		for (Object[] obj : list) {
			int cnt = ((Number) obj[1]).intValue();
			String sts = "";
			if (obj[0] != null) {
				sts = (String) obj[0];
			}
			totals += cnt;
			switch (sts) {
			case "SUCCESS":
				normals += cnt;
				break;
			case "PENDING":
				unknowns += cnt;
				break;
			case "DOWNLOADING":
				unknowns += cnt;
				break;
			case "FAILED":
				abNormals += cnt;
				break;
			case "CANCELED":
				normals += cnt;
				break;
			default:
				break;
			}
		}
		map.put("totals", totals);
		map.put("abNormals", abNormals);
		map.put("normals", normals);
		map.put("unknowns", unknowns);
		return map;
	}

	/**
	 * @param group
	 * @return 当下是否是这个Group的Alert Off时间
	 */
	public boolean isGroupAlertOff(Group group) {
		Date now = new Date();
		String nowStr = UTCDateEditor.formatDate(now, group.getTimeZone(), group.isDaylightSaving(),
				"yyyy-MM-dd HH:mm:ss");
		List<AlertOff> alertOffList = condService.getAlertOffListByGroupId(group.getId());
		if (alertOffList == null || alertOffList.isEmpty()) {
			return false;
		}
		for (AlertOff alertOff : alertOffList) {
			switch (alertOff.getRepeatType()) {
			case 0: // one time
				if (doCaseOneTime(alertOff, nowStr)) {
					return true;
				}
				break;
			case 1: // every day
				if (doCaseEveryDay(alertOff, nowStr)) {
					return true;
				}
				break;
			case 2: // every week
				int w = UTCTime.getDayForWeek(now, group.getTimeZone(), group.isDaylightSaving());
				if (doCaseEveryWeek(alertOff, nowStr, w)) {
					return true;
				}
				break;
			case 3: // every month
				int m = UTCTime.getDayForMonth(now, group.getTimeZone(), group.isDaylightSaving());
				if (doCaseEveryMonth(alertOff, nowStr, m)) {
					return true;
				}
				break;
			case 4: // every year
				if (doCaseEveryYear(alertOff, nowStr, now)) {
					return true;
				}
				break;
			}
		}
		return false;
	}

	// case:0
	private boolean doCaseOneTime(AlertOff alertOff, String nowStr) {
		if (alertOff.getOffDate().equals(nowStr.substring(0, 10))) {
			return compareTime(nowStr.substring(11, 16), alertOff.getOffStartTime(), alertOff.getOffEndTime()) > 0;
		}
		return false;
	}

	// case:1
	private boolean doCaseEveryDay(AlertOff alertOff, String nowStr) {
		return compareTime(nowStr.substring(11, 16), alertOff.getOffStartTime(), alertOff.getOffEndTime()) > 0;
	}

	// case:2
	private boolean doCaseEveryWeek(AlertOff alertOff, String nowStr, int w) {
		for (String str : alertOff.getOffDate().split(",")) {
			if (w == Integer.valueOf(str)) {
				return compareTime(nowStr.substring(11, 16), alertOff.getOffStartTime(), alertOff.getOffEndTime()) > 0;
			}
		}
		return false;
	}

	// case:3
	private boolean doCaseEveryMonth(AlertOff alertOff, String nowStr, int m) {
		if (m == Integer.valueOf(alertOff.getOffDate())) {
			return compareTime(nowStr.substring(11, 16), alertOff.getOffStartTime(), alertOff.getOffEndTime()) > 0;
		}
		return false;
	}

	// case:4
	private boolean doCaseEveryYear(AlertOff alertOff, String nowStr, Date now) {
		if (alertOff.getOffDate().equals(nowStr.substring(5, 10))) {
			return compareTime(nowStr.substring(11, 16), alertOff.getOffStartTime(), alertOff.getOffEndTime()) > 0;
		}
		return false;
	}

	public int compareTime(String tgt, String start, String end) {
		if (tgt.compareTo(start) >= 0 && tgt.compareTo(end) <= 0) {
			return 1;
		}
		return 0;
	}

	@Async
	public void sendAlertMessage(AlertEvent event) {
		Long condId = event.getCondId();
		List<UserSubscribeInfo> sbsUserList = alertProcessDao.getByCond(condId);
		for (UserSubscribeInfo userInfo : sbsUserList) {
			if (userInfo.isEmail()) {
				subscribeMailSender.sendSubscribeEmail(userInfo, event);
				PublishEmail pe = new PublishEmail();
				pe.setEventId(event.getEventId());
				pe.setCondId(event.getCondId());
				pe.setPublishTime(new Date());
				pe.setUserId(userInfo.getUserId());
				pe.save();
			}

			if (userInfo.isSms() && !StringUtils.isEmpty(userInfo.getPhone())) {
				PaxSmsUtil.sendMsg(userInfo.getPhone(), event.getAlertMsg());
				PublishSms ps = new PublishSms();
				ps.setEventId(event.getEventId());
				ps.setCondId(event.getCondId());
				ps.setPublishTime(new Date());
				ps.setUserId(userInfo.getUserId());
				ps.save();
			}
		}
	}

	private void checkUserExists(Long userId) {
		User user = userService.get(userId);
		if (user == null) {
			throw new BusinessException("msg.user.notFound");
		}
	}

	@Override
	public void updateRealAndUsageDashboardPie(String data, Long userId, String type) {
		checkUserExists(userId);
		userDao.updateRealAndUsageDashboardPie(data, userId, type);
	}

	@Override
	public void deleteUsageData(Date date) {
		alertProcessDao.deleteTerminalReportMessage(date);
		alertProcessDao.deleteTerminalUsageMessage(date);
		alertProcessDao.deleteTerminalUsageSts(date);
		alertProcessDao.deleteGroupUsageSts(date);
	}

	@Autowired
	public void setLastNhours(@Value("${tms.monitor.process.lastNhours:24}") int lastNhours) {
		this.lastNhours = lastNhours;
	}

	@Override
	public int getLastNhours() {
		return lastNhours;
	}
}
