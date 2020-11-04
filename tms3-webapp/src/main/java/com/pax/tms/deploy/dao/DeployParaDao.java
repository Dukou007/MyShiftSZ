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

import java.util.List;

import com.pax.common.dao.IBaseDao;
import com.pax.common.web.form.BaseForm;
import com.pax.tms.deploy.domain.DeployParaInfo;
import com.pax.tms.deploy.model.DeployPara;

public interface DeployParaDao extends IBaseDao<DeployPara, Long> {

	List<Long> getDeployParaIds(Long groupId, String destModel, Long pkgId);

	void updateDeployParas(List<Long> deployParaIds, DeployParaInfo deployInfo, BaseForm command);

	List<String> getDeployParaPaths(List<Long> deployIds);

	void deleteDeployParas(List<Long> deployIds);

}
