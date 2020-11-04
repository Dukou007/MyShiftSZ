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
package com.pax.tms.pub.dao;

import com.pax.common.dao.IBaseDao;
import com.pax.tms.pub.model.SystemParameter;

public interface SystemParameterDao extends IBaseDao<SystemParameter, String> {
    SystemParameter findByParaKey(String paraKey);
}
