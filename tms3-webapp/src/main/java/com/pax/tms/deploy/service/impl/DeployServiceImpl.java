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
package com.pax.tms.deploy.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pax.common.dao.IBaseDao;
import com.pax.common.service.impl.BaseService;
import com.pax.tms.deploy.dao.DeployDao;
import com.pax.tms.deploy.model.Deploy;
import com.pax.tms.deploy.service.DeployService;

@Service("deployServiceImpl")
public class DeployServiceImpl extends BaseService<Deploy, Long> implements DeployService {

	@Autowired
	private DeployDao deployDao;

	@Override
	public IBaseDao<Deploy, Long> getBaseDao() {
		return deployDao;
	}

}
