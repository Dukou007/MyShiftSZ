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
package com.pax.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailSenderListenerImpl implements IEmailSenderListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(EmailSenderListenerImpl.class);

	@Override
	public void beforeSendEmail() {
		// do nothing
	}

	@Override
	public void catchException(Exception e) {
		LOGGER.error("send email failed", e);
	}

	@Override
	public void afterSendEmail(boolean flag) {
		// do nothing
	}

}
