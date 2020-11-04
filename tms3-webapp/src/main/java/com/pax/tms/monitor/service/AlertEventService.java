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



import com.pax.common.service.IBaseService;
import com.pax.tms.monitor.model.AlertEvent;


public interface AlertEventService extends IBaseService<AlertEvent, Long> {
	AlertEvent findByGroupId(Long trId);
}