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

import com.pax.common.dao.IBaseDao;
import com.pax.common.web.form.BaseForm;
import com.pax.tms.terminal.model.TerminalGroup;

public interface TerminalGroupDao extends IBaseDao<TerminalGroup, Long> {

	void deleteTerminalGroupCascading(Collection<String> tsns, Long groupId);

	void insertTerminalGroup(Collection<String> tsns, Long groupId, BaseForm form);

	void insertTerminalGroups(String tsn, Collection<Long> groupIds, BaseForm command);

	Collection<String> getTerminalUnassignedToGroup(Long batchId);

	Map<Long, Collection<String>> getLevelOneGroup(Long batchId);

	void deleteTerminalGroupByTsn(List<String> tsnList);

	Collection<String> getTerminalGroupCascading(Long groupId);

	List<String> getGroupNamesByTsn(String tsn);

	List<String> getNeedAssignGroupTsns(Long batchId, Long groupId);

	List<String> getTargetGroupExistTsns(Long batchId, Long groupId);

	List<String> getNeedMoveGroupTsns(Long groupId);

	List<String> getTerminalByGroupId(Long sourceGroupId);

	Map<String, Collection<Long>> tsnAssignGroups(List<String> tsnList);

	Map<Long, Collection<String>> getTsnsDistinctGroupAncestor(Long batchId, Long groupId);

	Collection<String> insertTerminalGroups(Map<Long, Collection<String>> tsnAncestorGroupMap, BaseForm command);
	
	List<Long> getTsnsDistinctGroupIds(String terminalTsn, Long groupId);

	Map<String, Collection<Long>> getTerminalGroupIds(long batchId);

}
