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
import com.pax.tms.app.broadpos.para.ApplicationModule;
import com.pax.tms.res.model.MetaInfo;
import com.pax.tms.res.model.Pkg;
import com.pax.tms.res.model.PkgProgram;
import com.pax.tms.res.model.PkgType;

public interface PkgDao extends IBaseDao<Pkg, Long> {

	List<Long> getNotAcceptancePkgIds(List<Long> pkgList, Long userId);

	void deactivate(Collection<Long> pkgList, BaseForm command);

	void activate(Collection<Long> pkgList, BaseForm command);

	void delete(Collection<Long> pkgIds);

	boolean existNameAndVersion(String name, String version);

	List<String> getPkgVersionsByName(String name, Long groupId);

	Pkg getPkgByNameAndVersion(String name, String version);

	List<MetaInfo> getMetaInfo(Long pkgId);

	List<PkgProgram> getPkgProgramInfo(Long pkgId);

	List<Pkg> getExistDeployPkgs(List<Long> pkgIds);

	List<String> programFilePaths(List<Long> pkgIds);

	List<String> getPkgFilePaths(List<Long> pkgIds);

	List<Pkg> getNamesById(List<Long> pkgIds);

	List<Long> getpkgListByGroupId(Long groupId, Integer queryType);

	List<ApplicationModule> getAppModuleList(Long pkgId);

	List<Long> getPackageIds(String pkgName, String pkgVersion, String pgmType, PkgType pkgType);

	List<Pkg> getPackages(String pkgName, String pkgVersion, String pgmType, PkgType pkgType);

	List<Pkg> getPackages(String name, PkgType pkgType);

	List<Long> getGrantedGroups(long pkgId);

	List<Map<String, Object>> getPkgIdsAndTime();

	List<Map<String, Object>> getPkgListByName(String name, Long groupId);
	
	List<Pkg> getPkgByName(String name, Long groupId);
	
	List<Pkg> getLatestPkgByGroup(Long groupId, Integer queryType);
	
	List<Pkg> getLatestPkgByGroupAndModel(Long groupId,String model, Integer latestType);
	
	List<Pkg> getPackageUnCheck();
}
