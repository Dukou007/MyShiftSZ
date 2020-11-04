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
import com.pax.tms.deploy.model.GroupDeploy;

public interface GroupDeployDao extends IBaseDao<GroupDeploy, Long> {

	void copyGroupTaskToTerminals(Map<String, Deploy> tsnDeploys, BaseForm command);

	DeployInfo getDeployInfo(Long deployId);

	void deleteGroupDeploy(Long deployId);

	List<Object[]> getGroupDeploys(Long groupId);

	Map<String, List<Object[]>> getAllGroupDeploys(Long batchId);

	void insertInheritDeploys(Map<String, List<Deploy>> tsnNeedToCopyGroupTask, BaseForm command);

	void deleteGroupDeployCascading(Long groupId);

	void updateDeployTime(Date requestTime, Long deployId);

	void deleteGroupDeploys(List<Long> deployIds);

	List<Object[]> getSelfGroupDeploys(Collection<Long> needInheritGroupIds);

	List<Long> getGroupDownloadedTask();

}
