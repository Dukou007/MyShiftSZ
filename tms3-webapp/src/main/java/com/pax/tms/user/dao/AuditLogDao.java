/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description:  audit log dao interfaces
 * Revision History:		
 * Date	        Author	            Action
 * 20170111     Carry         audit log  dao interfaces
 * ============================================================================		
 */
package com.pax.tms.user.dao;

import java.util.Collection;
import java.util.Date;

import com.pax.common.dao.IBaseDao;
import com.pax.tms.user.model.PubAuditLog;
import com.pax.tms.user.web.form.OperatorLogForm;

public interface AuditLogDao extends IBaseDao<PubAuditLog, Long> {

	void addAuditLog(Collection<String> objects, OperatorLogForm form);

	boolean checkAuditLogs(String action);

	void systemClear(Date when);

}
