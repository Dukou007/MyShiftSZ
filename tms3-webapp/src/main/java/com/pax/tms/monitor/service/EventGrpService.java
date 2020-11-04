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

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.pax.common.service.IBaseService;
import com.pax.tms.monitor.model.EventGrp;
import com.pax.tms.monitor.web.form.QueryTerminalEventForm;

public interface EventGrpService extends IBaseService<EventGrp, Long> {

	void addEventLog(Collection<String> objects, String message, String additionMessage);

	void addEventLog(Long groupId, String message);

	List<Map<String, Object>> callGrp(QueryTerminalEventForm command, int start, int length);

	List<Map<String, Object>> callTrm(QueryTerminalEventForm command, int start, int length);

	long countDao(QueryTerminalEventForm command);

	int deleteEventGrp(Date eventTime);
}
