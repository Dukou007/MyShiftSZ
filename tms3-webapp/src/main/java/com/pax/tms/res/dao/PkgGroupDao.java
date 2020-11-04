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
package com.pax.tms.res.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.pax.common.dao.IBaseDao;
import com.pax.common.web.form.BaseForm;
import com.pax.tms.res.model.PkgGroup;
import com.pax.tms.res.web.form.AbstractPkgForm;

public interface PkgGroupDao extends IBaseDao<PkgGroup, Long> {
	Map<Long, Collection<Long>> getLevelOneGroup(Collection<Long> pkgList);

	void deletePkgGroupCascade(Collection<Long> pkgList, Long groupId);

	Collection<Long> getPkgUnAssignToGroup(Collection<Long> pkgList);

	void insertPkgGroup(Collection<Long> pkgIds, Long groupId, BaseForm command);

	void deletePkgGroupByPkgId(Collection<Long> pkgList);

	Map<Long, Collection<Long>> getPkgAssignGroups(Collection<Long> pkgIdList);

	void insertPkgGroup(Long pkgId, Collection<Long> needAddGroups, AbstractPkgForm command);

	List<String> getGroupNamesByPkgId(Long pkgId);

	List<String> getPkgNamesByGroupId(Long groupId, String type);

	List<String> getPkgNamesByGroupIdAndDestmodel(Long groupId, String destModel, String type);

	Map<Long, Collection<Long>> getPkgDistinctGroupAncestor(Collection<Long> pkgIds, Long groupId);

	void insertPkgGroups(Map<Long, Collection<Long>> pkgAncestorGroupMap, BaseForm command);
}
