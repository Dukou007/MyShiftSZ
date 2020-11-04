/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description: Process Group Alert
 * Revision History:		
 * Date	                 Author	                Action
 * 20161214  	         Crazy.W           	    Create
 * ============================================================================
 */
package com.pax.tms.monitor.dao;

import java.util.Date;
import java.util.List;

import com.pax.common.dao.IBaseDao;
import com.pax.tms.group.model.UsageThreshold;
import com.pax.tms.monitor.domain.ResultCount;
import com.pax.tms.monitor.domain.TimeZoneInfo;
import com.pax.tms.monitor.domain.UserSubscribeInfo;
import com.pax.tms.monitor.domain.UsageMessageInfo;
import com.pax.tms.monitor.domain.UsageStatusInfo;
import com.pax.tms.monitor.model.TerminalReportMessage;
import com.pax.tms.monitor.web.form.UsagePie;

public interface AlertProcessDao extends IBaseDao<TerminalReportMessage, Long> {

	boolean isGetUsageStatus(UsageThreshold thd, Date startTime, Date endTime);

	boolean isGetUsageMessage(String itemName, Date startTime, Date endTime);

	List<UserSubscribeInfo> getByCond(Long condId);

	List<Object[]> getGroupRealStatus(Long groupId, String itemName, Date dayStart);

	List<UsageMessageInfo> getUsageMessageList(String itemName, Date startTime, Date endTime, String msgCycle);

	void insertUsageMessage(List<UsageMessageInfo> usageList, Date startTime, Date endTime, String msgCycle);

	List<UsageStatusInfo> getUsageStatusList(UsageThreshold thd, Date startTime, Date endTime);

	void insertUsageStatus(List<UsageStatusInfo> usageList);

	List<ResultCount> getGroupUsageStatus(Long groupId, String itemName, Date startTime, Date endTime,
			String reportCycle);

	void insertGroupUsageStatus(List<UsagePie> pieList);

	boolean isGetGroupUsageStatus(long groupId, String itemName, Date startTime, Date endTime, String reportCycle);

	List<TimeZoneInfo> listTimeZone();

	int deleteTerminalReportMessage(Date date);

	int deleteTerminalUsageMessage(Date date);

	int deleteTerminalUsageSts(Date date);

	int deleteGroupUsageSts(Date date);

}
