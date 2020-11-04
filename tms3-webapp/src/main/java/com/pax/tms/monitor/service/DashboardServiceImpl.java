/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description: List UsagePie
 * Revision History:		
 * Date	                 Author	                Action
 * 20170111  	         Frank           	    Modify 
 * ============================================================================		
 */
package com.pax.tms.monitor.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pax.common.dao.IBaseDao;
import com.pax.common.exception.BusinessException;
import com.pax.common.service.impl.BaseService;
import com.pax.common.util.DateTimeUtils;
import com.pax.tms.monitor.dao.GroupUsageDao;
import com.pax.tms.monitor.model.GroupUsageStatus;
import com.pax.tms.monitor.web.AlertConstants;
import com.pax.tms.monitor.web.form.UsagePie;
import com.pax.tms.user.model.User;
import com.pax.tms.user.service.UserService;

@Service("dashboardServiceImpl")
public class DashboardServiceImpl extends BaseService<GroupUsageStatus, Long> implements DashboardService {

	@Autowired
	@Qualifier("groupUsageDaoImpl")
	private GroupUsageDao groupUsageDao;

	@Autowired
	private UserService userService;

	@Override
	public IBaseDao<GroupUsageStatus, Long> getBaseDao() {
		return groupUsageDao;
	}

	@Override
	public List<UsagePie> getUsagePielist(Long groupId, Long userId) {
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		List<UsagePie> list = new ArrayList<UsagePie>(AlertConstants.getUsageItemsCount());
		Map<String, UsagePie> map = new HashMap<String, UsagePie>();
		for (String itemName : AlertConstants.getUsageItems()) {
			GroupUsageStatus usage = groupUsageDao.getUsageByGroupId(itemName, groupId);
			UsagePie usagePie = this.transferPie(usage, itemName);
			list.add(usagePie);
			map.put(itemName, usagePie);
		}

		User user = userService.get(userId);
		String realTime = user.getUsage();
		String[] realTimes = realTime.split(",");
		List<UsagePie> newPieList = new ArrayList<UsagePie>();
		for (String str : realTimes) {
			UsagePie pie = map.get(str.split(":")[0]);
			if (pie != null) {
				pie.setIsshow(Integer.parseInt(str.split(":")[1]));
				newPieList.add(pie);
			} else {
				throw new BusinessException("msg.usageName.notExists");
			}

		}

		return newPieList;
	}

	@Override
	public List<List<UsagePie>> getUsageBarList(Long groupId, Long userId) {
		List<List<UsagePie>> result = new ArrayList<>(AlertConstants.getUsageItemsCount());
		Map<String, List<UsagePie>> map = new HashMap<>();
		for (String itemName : AlertConstants.getUsageItems()) {
			List<GroupUsageStatus> usageList = groupUsageDao.getUsageDetail(itemName, groupId);
			List<UsagePie> pieList = new ArrayList<UsagePie>();
			if (usageList == null || usageList.isEmpty()) {
				pieList.add(this.transferPie(null, itemName));
			} else {
				for (GroupUsageStatus usage : usageList) {
					pieList.add(this.transferPie(usage, itemName));
				}
				pieList.sort((a, b) -> a.getPieDate().compareTo(b.getPieDate()));
			}
			map.put(itemName, pieList);
		}

		User user = userService.get(userId);
		String realTime = user.getUsage();
		String[] realTimes = realTime.split(",");
		for (String str : realTimes) {
			List<UsagePie> usagePie = map.get(str.split(":")[0]);
			if (usagePie != null && usagePie.size() > 0) {
				for (UsagePie usage : usagePie) {
					usage.setIsshow(Integer.parseInt(str.split(":")[1]));
				}
				result.add(usagePie);
			} else {
				throw new BusinessException("msg.usageName.notExists");
			}
		}

		return result;
	}
	
	private UsagePie transferPie(GroupUsageStatus usage, String itemName) {
		UsagePie pie = new UsagePie();
		pie.setName(itemName);
		if (usage != null) {
			pie.setIsshow(1);
			pie.setGroupId(usage.getGroupId());
			pie.setGroupName(usage.getGroupName());
			pie.setTotal(usage.getTotalTrms() - usage.getUnKnownTrms());
			pie.setAlertValue(usage.getAlertValue());
			pie.setThreshold(usage.getAlertThreshold());
			pie.setAlertLevel(usage.getAlertSeverity());
			pie.setRedCount(usage.getAbnormalTrms());
			pie.setGreenCount(usage.getNormalTrms());
			pie.setGreyCount(0);
			try {
                pie.setStartTime(DateTimeUtils.string2Date(usage.getStartTime(), DateTimeUtils.PATTERN_STANDARD));
                pie.setEndTime(DateTimeUtils.string2Date(usage.getEndTime(), DateTimeUtils.PATTERN_STANDARD));
            } catch (ParseException e) {
                e.printStackTrace();
            }
			pie.setReportCycle(usage.getReportCycle());
			pie.setPieDate(usage.getCreateDate());
		}
		pie.setGreyTitle("Unavailable");
		pie.setGreenTitle("Normal");
		pie.setRedTitle("Abnormal");
		return pie;
	}

}
