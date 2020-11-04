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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pax.common.dao.IBaseDao;
import com.pax.common.service.impl.BaseService;
import com.pax.tms.monitor.dao.EventTrmDao;
import com.pax.tms.monitor.model.EventTrm;
import com.pax.tms.monitor.web.form.OperatorEventForm;

@Service("eventTrmServiceImpl")
public class EventTrmServiceImpl extends BaseService<EventTrm, Long> implements EventTrmService {

	@Autowired
	private EventTrmDao eventDao;

	@Override
	public IBaseDao<EventTrm, Long> getBaseDao() {

		return eventDao;
	}

	@Override
	public void addEventLog(Collection<String> objects, String message, String additionMessage) {

		OperatorEventForm form = new OperatorEventForm();
		form.setMessage(message);
		form.setAdditionMessage(additionMessage);
		eventDao.addEventLog(objects, form);

	}

	@Override
	public void addEventLog(Long groupId, String message) {
		EventTrm event = new EventTrm();
		event.setServerity(EventTrm.INFO);
		event.setSource(String.valueOf(groupId));
		event.setTime(new Date());
		event.setMessage(message);
		eventDao.save(event);

	}

	@Override
	public int deleteEventTrm(Date eventTime) {
		return eventDao.deleteEventTrmByTime(eventTime);
	}

}
