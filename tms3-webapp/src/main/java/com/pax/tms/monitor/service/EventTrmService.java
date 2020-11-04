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

import com.pax.common.service.IBaseService;
import com.pax.tms.monitor.model.EventTrm;

public interface EventTrmService extends IBaseService<EventTrm, Long> {

	void addEventLog(Collection<String> objects, String message, String additionMessage);

	void addEventLog(Long groupId, String message);

	int deleteEventTrm(Date eventTime);

}
