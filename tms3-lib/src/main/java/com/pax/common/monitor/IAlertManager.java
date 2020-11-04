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
package com.pax.common.monitor;

import org.springframework.dao.DataAccessException;

import com.pax.common.exception.AppException;

public interface IAlertManager {

	void alert(Object source, AppException exception);

	void alert(Object source, DataAccessException exception);

	void alert(Object source, Exception exception);

}
