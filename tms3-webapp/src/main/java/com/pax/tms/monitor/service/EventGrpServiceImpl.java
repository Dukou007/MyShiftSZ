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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pax.common.dao.IBaseDao;
import com.pax.common.pagination.IPageForm;
import com.pax.common.pagination.Page;
import com.pax.common.service.impl.BaseService;
import com.pax.tms.monitor.dao.EventDao;
import com.pax.tms.monitor.dao.EventGrpDao;
import com.pax.tms.monitor.model.EventGrp;
import com.pax.tms.monitor.model.EventTrm;
import com.pax.tms.monitor.web.form.OperatorEventForm;
import com.pax.tms.monitor.web.form.QueryTerminalEventForm;

@Service("eventGrpServiceImpl")
public class EventGrpServiceImpl extends BaseService<EventGrp, Long> implements EventGrpService {

	@Autowired
	private EventGrpDao eventGroupDao;

	@Autowired
	@Qualifier("eventDaoImpl")
	private EventDao eventDao;

	@Override
	public IBaseDao<EventGrp, Long> getBaseDao() {
		return eventDao;
	}

	@Override
	public void addEventLog(Collection<String> objects, String message, String additionMessage) {

		OperatorEventForm form = new OperatorEventForm();
		form.setMessage(message);
		form.setAdditionMessage(additionMessage);
		eventGroupDao.addEventLog(objects, form);

	}

	@Override
	public void addEventLog(Long groupId, String message) {
		EventGrp event = new EventGrp();
		event.setServerity(EventTrm.INFO);
		event.setSource(String.valueOf(groupId));
		event.setTime(new Date());
		event.setMessage(message);
		eventGroupDao.save(event);
	}

	@Override
	public <E, S extends IPageForm> Page<E> page(S command) {
		long totalCount = eventDao.count(command);
		int index = command.getPageIndex();
		int size = command.getPageSize();
		int firstResult = Page.getPageStart(totalCount, index, size);
		return Page.getPage(index, size, totalCount, eventDao.page(command, firstResult, size));
	}

	@Override
	public List<Map<String, Object>> callGrp(QueryTerminalEventForm command, int start, int length) {
		return eventDao.getAllGroupPage(command, start, length);
	}

	@Override
	public List<Map<String, Object>> callTrm(QueryTerminalEventForm command, int start, int length) {
		return eventDao.getAllTerminalPage(command, start, length);
	}

	@Override
	public long countDao(QueryTerminalEventForm command) {
		return eventDao.getAllCount(command);
	}

	@Override
	public int deleteEventGrp(Date eventTime) {
		return eventGroupDao.deleteEventGrpByTime(eventTime);
	}
}
