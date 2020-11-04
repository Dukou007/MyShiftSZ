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
package com.pax.tms.res.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.pax.common.service.IBaseService;
import com.pax.common.web.form.BaseForm;
import com.pax.tms.group.model.Group;
import com.pax.tms.res.model.MetaInfo;
import com.pax.tms.res.model.Pkg;
import com.pax.tms.res.model.PkgProgram;
import com.pax.tms.res.web.form.AddPkgForm;
import com.pax.tms.res.web.form.AssignPkgForm;
import com.pax.tms.res.web.form.EditPkgForm;

public interface PkgService extends IBaseService<Pkg, Long> {

	Pkg save(AddPkgForm command);

	/**
	 * Save the new package or assign the package to new groups.
	 * 
	 * @param form
	 * @return
	 */
	Pkg saveOrUpdate(AddPkgForm form);

	void edit(Long pkgId, EditPkgForm command);

	void deactivate(Long[] pkgId, BaseForm command);

	void activate(Long[] pkgId, BaseForm command);

	void dismiss(List<Long> pkgList, BaseForm command);

	void delete(List<Long> pkgList, BaseForm command);

	void assign(Long[] pkgId, AssignPkgForm command);

	List<String> getGroupNames(Long pkgId);

	List<String> getPkgNamesByGroupId(Long groupId, String type);

	List<String> getPkgNamesByGroupIdAndDestmodel(Long groupId, String destModel, String type);

	List<String> getPkgVersionsByName(String name, Long groupId);

	Pkg getPkgByNameAndVersion(String name, String version);

	Pkg validatePkg(Long pkgId);

	List<MetaInfo> getMetaInfo(Long pkgId);

	List<PkgProgram> getPkgProgramInfo(Long pkgId);

	void dismissByGroup(Group group, BaseForm command);

	List<Map<String, Object>> getPkgStatusChangedMessage(Collection<Long> pkgIds, String op);

	void sendPkgStatusChangedMessage(List<Map<String, Object>> msgList);

	List<Map<String, Object>> getPkgListByName(String name, Long groupId);

	Map<String, Object> checkHistoryPackage();
	
	List<Pkg> getPkgByName(String name, Long groupId);
	
	List<Pkg> getLatestPkgByGroup(Long groupId, Integer queryType);
	
	List<Pkg> getLatestPkgByGroupAndModel(Long groupId,String model, Integer latestType);
}
