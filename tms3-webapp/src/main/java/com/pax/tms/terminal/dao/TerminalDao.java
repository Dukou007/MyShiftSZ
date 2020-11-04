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
package com.pax.tms.terminal.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pax.common.dao.IBaseDao;
import com.pax.common.web.form.BaseForm;
import com.pax.tms.group.model.Group;
import com.pax.tms.terminal.model.Terminal;

public interface TerminalDao extends IBaseDao<Terminal, String> {

	Object[] getExistedAndNotAcceptableTsns(Set<String> tsns, long userId);

	List<String> getNotAcceptableTsns(Long batchId, long userId);

	void activate(Collection<String> tsnList, BaseForm command);

	void deactivate(Collection<String> tsns, BaseForm form);

	void delete(Collection<String> tsns);

	void addTerminals(Collection<String> tsns, Map<String, Terminal> terminalMap, BaseForm command);

	List<String> getTsnsByGroupId(Long groupId);

	void addTerminalGroup(Collection<String> tsns, Group group, BaseForm command);

	void addTemporaryTsns(final long batchId, final Collection<String> tsns);

	void deleteTemporaryTsns(long batchId);

	void updateTerminals(Terminal source, Collection<String> existTsnNeedToAssignGroup,
			BaseForm command);

	List<String> getTerminalModels(Long groupId);

	List<Map<String, Object>> getTerminalGroupList();
}
