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

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.pax.common.dao.IBaseDao;
import com.pax.tms.monitor.model.EventTrm;
import com.pax.tms.monitor.web.form.OperatorEventForm;

public interface EventTrmDao extends IBaseDao<EventTrm, Long> {

	void addEventLog(Collection<String> objects, OperatorEventForm form);

	List<EventTrm> getEventsBySource(String source);

	int deleteEventTrmByTime(Date eventTime);

}
