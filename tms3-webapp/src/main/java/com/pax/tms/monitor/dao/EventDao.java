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
package com.pax.tms.monitor.dao;

import java.util.List;
import java.util.Map;

import com.pax.common.dao.IBaseDao;
import com.pax.tms.monitor.model.EventGrp;
import com.pax.tms.monitor.web.form.QueryTerminalEventForm;

public interface EventDao extends IBaseDao<EventGrp, Long> {

	List<Map<String, Object>> getAllGroupPage(QueryTerminalEventForm command, int start, int length);

	List<Map<String, Object>> getAllTerminalPage(QueryTerminalEventForm command, int start, int length);

	long getAllCount(QueryTerminalEventForm command);

}
