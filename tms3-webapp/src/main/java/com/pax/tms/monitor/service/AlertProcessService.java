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

import java.util.Date;
import java.util.List;

import org.codehaus.jettison.json.JSONException;

import com.pax.common.service.IBaseService;
import com.pax.tms.group.model.Group;
import com.pax.tms.monitor.model.AlertCondition;
import com.pax.tms.monitor.model.TerminalReportMessage;
import com.pax.tms.monitor.web.form.Pie;

public interface AlertProcessService extends IBaseService<TerminalReportMessage, Long> {

	void doProcessTerminalUsageStatus();

	void doProcessGroupRealStatus();

	List<Pie> getRealDashboard(Group group, Long userId) throws JSONException;

	void usageMessageByDay();

	void usageStatusByThreshold();

	Pie getRealItemResultPie(String itemName, List<Object[]> list, AlertCondition condition);

	void updateRealAndUsageDashboardPie(String data, Long userId, String type);

	void deleteUsageData(Date date);

	int getLastNhours();
}
