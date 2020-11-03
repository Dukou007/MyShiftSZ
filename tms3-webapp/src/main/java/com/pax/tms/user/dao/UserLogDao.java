/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) YYYY-? PAX Technology, Inc. All rights reserved.		
 * Description:user dao interface    		
 * Revision History:		
 * Date	        Author	               Action
 * 20170111     Carry            user dao interface  
 * ============================================================================		
 */
package com.pax.tms.user.dao;

import java.util.Date;
import java.util.List;

import com.pax.common.dao.IBaseDao;
import com.pax.tms.user.model.UserLog;

public interface UserLogDao extends IBaseDao<UserLog, Long> {

	List<String> getUserRoles(Long id);

	void systemClearUserLog(Date when);

}
