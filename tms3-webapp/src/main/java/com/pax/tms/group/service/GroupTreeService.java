/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description: // Detail description about the function of this module,		
 *             // interfaces with the other modules, and dependencies. 		
 * Revision History:		
 * Date	                 Author	                Action
 * 20170112	             Jaden           	    Modify
 * ============================================================================		
 */
package com.pax.tms.group.service;

import java.util.List;
import java.util.Map;

import com.pax.common.pagination.Page;
import com.pax.common.service.IBaseService;
import com.pax.tms.group.GroupInSearch;
import com.pax.tms.group.GroupOrTerminal;
import com.pax.tms.group.model.Group;
import com.pax.tms.group.web.form.QueryGroupForm;

public interface GroupTreeService extends IBaseService<Group, Long> {

	Page<Map<String, Object>> getUserTopGroups(QueryGroupForm command);

	Page<Map<String, Object>> getGroupContext(QueryGroupForm command);

	Page<Map<String, Object>> getSubgroup(QueryGroupForm command);

	Page<Map<String, Object>> getDescantGroup(QueryGroupForm command);

	Page<Map<String, Object>> searchGroupTree(QueryGroupForm command);

	List<GroupOrTerminal> searchGroupOrTerminal(QueryGroupForm command);
	
	List<GroupInSearch> searchGroup(QueryGroupForm command);
}
