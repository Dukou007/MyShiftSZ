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
package com.pax.tms.terminal.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.pax.common.service.IBaseService;
import com.pax.common.web.form.BaseForm;
import com.pax.tms.group.model.Group;
import com.pax.tms.monitor.model.UnregisteredTerminal;
import com.pax.tms.monitor.web.form.Pie;
import com.pax.tms.monitor.web.form.TerminalUsagePie;
import com.pax.tms.terminal.model.Terminal;
import com.pax.tms.terminal.web.form.AddTerminalForm;
import com.pax.tms.terminal.web.form.AssignTerminalForm;
import com.pax.tms.terminal.web.form.CopyTerminalForm;
import com.pax.tms.terminal.web.form.EditTerminalForm;
import com.pax.tms.terminal.web.form.QueryTerminalForm;

public interface TerminalService extends IBaseService<Terminal, String> {

	Map<String, Object> save(AddTerminalForm command);

	void edit(String tsn, EditTerminalForm command);

	Map<String, Object> copy(String tsn, CopyTerminalForm command);

	void activate(String[] tsns, BaseForm command);

	void deactivate(String[] tsns, BaseForm command);

	void dismiss(String[] tsns, BaseForm command);

	void delete(String[] tsns, BaseForm command);

	void moveToGroup(String[] tsns, QueryTerminalForm command);

	List<TerminalUsagePie> getUsageStatus(String terminalId, Long userId);

	List<List<TerminalUsagePie>> getUsageStatusBarList(String terminalId, Long userId);

	Terminal validateTerminal(String tsn);
	
	UnregisteredTerminal validateUnregisteredTerminal(String tsn);

	List<String> getGroupNameByTsn(String tsn);

	void dismissByGroup(Group group, BaseForm command);

	List<Pie> getRealStatus(String terminalId, Long userId);

	Collection<String> getNewTsns(Collection<String> totalTsns, Collection<String> existsTsns);

	Object[] getExistTsnNeedToAssignGroup(Map<String, List<Long>> existedTsnGroups, List<String> notAcceptableTsns,
			Group targetGroup);

	Object[] getExistTsnNeedAndIgnoreToAssignGroup(Map<String, List<Long>> existedTsnGroups,
			List<String> notAcceptableTsns, Group targetGroup);

	void checkExistTsnTerminalType(Collection<String> existTsnNeedToAssignGroup, String destModel);

	Map<String, Object> assign(String[] tsns, AssignTerminalForm command);

	List<Map<String, String>> getTerminalStatusChangedMessage(Collection<String> tsnList, String model, String op);

	List<Map<String, String>> getTerminalStatusChangedMessage(Collection<String> tsnList,
			Map<String, Terminal> terminalMap, String op);

	List<Map<String, String>> getTerminalStatusChangedMessage(Collection<String> tsnList, String op);

	void sendTerminalStatusChangedMessage(List<Map<String, String>> msgList);

	List<String> getTerminalModels(Long groupId);

	Map<String, String> getTrmGroupNames();

	List<Map<String, Object>> exportList(QueryTerminalForm command);
	
	Map<String, Object> assignNotRegistered(String[] tsns, AssignTerminalForm command);
	//判断某个终端是否属于某个用户对应的分组
	boolean checkTerminalInGroup(String sn,Long userId);
}
