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


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pax.common.dao.IBaseDao;
import com.pax.common.exception.BusinessException;
import com.pax.common.service.impl.BaseService;
import com.pax.tms.monitor.dao.AlertEventDao;
import com.pax.tms.monitor.model.AlertEvent;


@Transactional
@Service("alertEventServiceImpl")
public class AlertEventServiceImpl extends BaseService<AlertEvent, Long> implements AlertEventService {
	@Autowired
	private AlertEventDao alertEventDao;

	@Override
	public IBaseDao<AlertEvent, Long> getBaseDao() {
		return alertEventDao;
	}

	@Override
	public AlertEvent findByGroupId(Long groupId) {
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		return alertEventDao.findByGroupId(groupId);
	}

}
