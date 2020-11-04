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
package com.pax.tms.schedule;

import javax.servlet.http.HttpSession;

import org.jasig.cas.client.session.SessionMappingStorage;

public class TmsHashMapBackedSessionMappingStorage implements SessionMappingStorage {

	@Override
	public HttpSession removeSessionByMappingId(String mappingId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeBySessionById(String sessionId) {
		// TODO Auto-generated method stub
	}

	@Override
	public void addSessionById(String mappingId, HttpSession session) {
		// TODO Auto-generated method stub

	}

}
