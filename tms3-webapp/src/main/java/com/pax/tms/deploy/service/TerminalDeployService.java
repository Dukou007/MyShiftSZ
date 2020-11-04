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

import java.util.List;
import java.util.Map;

import com.pax.common.pagination.Page;
import com.pax.common.service.IBaseService;
import com.pax.tms.deploy.domain.DeployInfo;
import com.pax.tms.deploy.model.TerminalDeploy;
import com.pax.tms.deploy.web.form.EditTerminalDeployForm;
import com.pax.tms.deploy.web.form.QueryCurrentTsnDeployForm;
import com.pax.tms.deploy.web.form.QueryHistotyTsnDeployForm;
import com.pax.tms.deploy.web.form.TerminalDeployForm;
import com.pax.tms.deploy.web.form.TerminalDeployOperatorForm;

import io.vertx.core.json.JsonArray;

public interface TerminalDeployService extends IBaseService<TerminalDeploy, Long> {

	void deploy(TerminalDeployForm command);

	Page<DeployInfo> getCurrentDeploysByTsn(QueryCurrentTsnDeployForm command);

	Page<DeployInfo> getHistorytDeploysByTsn(QueryHistotyTsnDeployForm command);

	void activate(TerminalDeployOperatorForm command);

	void deactivate(TerminalDeployOperatorForm command);

	void delete(TerminalDeployOperatorForm command);

	void edit(EditTerminalDeployForm command);

	Map<String, String> getTrmLastParams(String terminalId);

	JsonArray getTrmlastSchemaHtml(Long pkgSchemaId, String terminalId);

	void deleteTerminalInheritTaskFromGroup(Long deployId);

	List<Long> getInheritGroupDeployIds(Long groupId);
}
