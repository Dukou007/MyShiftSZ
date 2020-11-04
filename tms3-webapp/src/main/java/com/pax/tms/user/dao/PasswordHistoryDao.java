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
package com.pax.tms.user.dao;

import java.util.List;

import com.pax.common.dao.IBaseDao;
import com.pax.tms.user.model.PasswordHistory;

public interface PasswordHistoryDao extends IBaseDao<PasswordHistory, Long> {

	List<PasswordHistory> getLastPasswords(long userId, int count);

	void deletePasswordHistory(long userId);

}
