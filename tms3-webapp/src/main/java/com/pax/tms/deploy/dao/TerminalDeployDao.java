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

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.pax.common.dao.IBaseDao;
import com.pax.tms.deploy.domain.DeployInfo;
import com.pax.tms.deploy.domain.TerminalDeployInfo;
import com.pax.tms.deploy.model.TerminalDeploy;

public interface TerminalDeployDao extends IBaseDao<TerminalDeploy, Long> {

	List<TerminalDeployInfo> getTerminalDeployInfos(Long groupId, String destModel);

	public DeployInfo getDeployInfo(Long deployId);

	void deleteTerminalDeploy(String tsn, Long deployId);

	void deleteTerminalDeploy(List<String> tsnList);

	TerminalDeployInfo getTerminalLastedDeploy(String terminalId);

	void deleteTerminalDeploys(Map<String, List<Long>> tsnDeployIdsMap);

	void updateDeployTime(Date requestTime, Long deployId);

	List<Long> getDeployIdsByGroupId(Long deployId);

	void deleteTerminalDeploys(List<Long> deployIds);

	List<Long> getInheritGroupDeployIds(Long groupId);

	TerminalDeploy getTerminalDeploy(Long deployId, String terminalId);

	List<String> getTsnsByDeployGroupId(Long deployId);

	List<Long> getLatedDeployId(String terminalId);

}
