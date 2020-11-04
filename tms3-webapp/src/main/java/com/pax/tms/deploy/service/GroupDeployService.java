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
package com.pax.tms.deploy.service;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import com.pax.common.pagination.Page;
import com.pax.common.service.IBaseService;
import com.pax.common.web.form.BaseForm;
import com.pax.tms.deploy.domain.DeployInfo;
import com.pax.tms.deploy.model.GroupDeploy;
import com.pax.tms.deploy.web.form.EditGroupDeployForm;
import com.pax.tms.deploy.web.form.GroupChangeParaForm;
import com.pax.tms.deploy.web.form.GroupDeployForm;
import com.pax.tms.deploy.web.form.GroupDeployOperatorForm;
import com.pax.tms.deploy.web.form.QueryCurrentGroupDeployForm;
import com.pax.tms.deploy.web.form.QueryHistoryGroupDeployForm;
import com.pax.tms.group.model.Group;
import com.pax.tms.terminal.model.Terminal;

public interface GroupDeployService extends IBaseService<GroupDeploy, Long> {

	void deploy(GroupDeployForm command);
	
	void deploykey(GroupDeployForm command);

	void changeDeployParas(GroupChangeParaForm command);

	Page<DeployInfo> getCurrentDeploysByGroupId(QueryCurrentGroupDeployForm command);

	Page<DeployInfo> getLineDeploysByGroupId(QueryCurrentGroupDeployForm command);

	Page<DeployInfo> getHistoryDeploysByGroupId(QueryHistoryGroupDeployForm command);

	void activate(GroupDeployOperatorForm command);

	void deactivate(GroupDeployOperatorForm command);

	void delete(GroupDeployOperatorForm command);

	void edit(EditGroupDeployForm command);

	void copyAncestorGroupTaskToTerminals(Long batchId, Collection<String> tsns, Long groupId, BaseForm command,
			boolean isNew);

	void copyNewLineGroupTaskToTerminals(Map<String, Collection<Long>> tsnGroupIdsMap, Group targetGroup,BaseForm command);

	void copySourceTerminalTask(Collection<String> tsns, BaseForm command, Terminal terminal, boolean isNew);

	void deleteByGroup(Long groupId);
	
	void deleteChildGroupTaskCacading(long batchId,Group targetGroup);

	void deleteAllTaskFromGroup(Long batchId);

	void copyAllGroupTaskToTerminalCascading(Long batchId, BaseForm command);

	void deleteOverDueDeployment(Date when);
	
	void deleteDownloadedTask();
	
}
