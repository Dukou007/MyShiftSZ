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
package com.pax.tms.deploy.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.pax.common.dao.IBaseDao;
import com.pax.common.web.form.BaseForm;
import com.pax.tms.deploy.domain.DeployInfo;
import com.pax.tms.deploy.model.Deploy;
import com.pax.tms.deploy.web.form.GroupChangeParaForm;

public interface DeployDao extends IBaseDao<Deploy, Long> {

	List<DeployInfo> getDeployInfos(Long groupId, String destModel, Long pkgId);

	List<Long> getDeployIds(Long groupId, String destModel, Long pkgId);

	void updateDeploys(List<Long> deployIds, Deploy deploy, GroupChangeParaForm command);

	boolean isDeploy(Long pkgSchemaId);

	List<Deploy> getUnDealDeployList();

	void deleteDeployByTsns(Long batchId);

	Map<String, List<Long>> getTsnInheritSourceGroupId(Long batchId, Long groupId);

	void deleteTaskFromGroupCascading(Collection<Long> deployIds);

	List<Long> getDeployIds(Long batchId);

	List<Object[]> getTerminalSelfTask(String tsn);

	List<Long> getTerminalInheritDeployIds(Long groupId);

	void updateDeployStatus(List<Long> deployIds, int status, BaseForm command);

	void replaceDeployPkgIds(Collection<Long> oldPkgIds, Long pkgId);

	Map<String, List<Long>> getTsnAllInheritDeployIds(Long batchId);
	
	Map<String, List<Long>> getTsnInheritDeployIds(Long batchId, Long groupId);

	Map<String, List<Long>> getDeployTimeTsns(Collection<String> tsnNeedToCopyTask, Long batchId);

	List<Long> getDeployPkgIds();

	List<Long> getOverdueDeployIds(Date when);

}
