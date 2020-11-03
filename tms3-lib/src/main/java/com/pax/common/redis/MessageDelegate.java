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
package com.pax.common.redis;

import java.io.Serializable;
import java.util.Map;

public interface MessageDelegate {
	void handleMessage(String message);

	@SuppressWarnings("rawtypes")
	void handleMessage(Map message);

	void handleMessage(byte[] message);

	void handleMessage(Serializable message);

	// pass the channel/pattern as well
	void handleMessage(Serializable message, String channel);
}
