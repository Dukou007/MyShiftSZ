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

import com.pax.common.pagination.Page;
import com.pax.common.service.IBaseService;
import com.pax.tms.deploy.domain.DeployInfo;
import com.pax.tms.deploy.model.TerminalDeployHistory;
import com.pax.tms.deploy.web.form.QueryHistotyTsnDeployForm;

public interface TerminalDeployHistoryService extends IBaseService<TerminalDeployHistory, Long> {

	Page<DeployInfo> getHistorytDeploysByTsn(QueryHistotyTsnDeployForm command);

}
