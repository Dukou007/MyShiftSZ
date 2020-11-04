/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description:  audit log interfaces
 * Revision History:		
 * Date	        Author	            Action
 * 20170111     Carry         audit log interfaces
 * ============================================================================		
 */
package com.pax.tms.user.service;

import java.util.Collection;
import java.util.Date;

import com.pax.common.service.IBaseService;
import com.pax.common.web.form.BaseForm;
import com.pax.tms.user.model.PubAuditLog;

public interface AuditLogService extends IBaseService<PubAuditLog, Long> {

	void addAuditLog(Collection<String> objects, BaseForm command, String message, String additionMessage);

	void addAuditLogs(String actionName);

	boolean checkAuditLogs(String action);

	void systemClear(Date when);

}
