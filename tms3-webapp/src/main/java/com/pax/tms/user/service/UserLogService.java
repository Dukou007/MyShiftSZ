/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description:  user log service interfaces
 * Revision History:		
 * Date	        Author	            Action
 * 20170111     Carry        user log service interfaces
 * ============================================================================		
 */
package com.pax.tms.user.service;

import java.util.Date;

import com.pax.common.service.IBaseService;
import com.pax.common.web.form.BaseForm;
import com.pax.tms.user.model.User;
import com.pax.tms.user.model.UserLog;

public interface UserLogService extends IBaseService<UserLog, Long> {

	void addUserLog(BaseForm command, User user, String string);

	void addUserLogInfo(String string);

	String getLogRoleInfo(Long id);

	void systemClearUserLog(Date when);

}
