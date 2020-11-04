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

public class DefaultMessageDelegate implements MessageDelegate {

	@Override
	public void handleMessage(String message) {
		// do nothing
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void handleMessage(Map message) {
		// do nothing
	}

	@Override
	public void handleMessage(byte[] message) {
		// do nothing
	}

	@Override
	public void handleMessage(Serializable message) {
		// do nothing
	}

	@Override
	public void handleMessage(Serializable message, String channel) {
		// do nothing
	}

}
