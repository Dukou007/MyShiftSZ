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
 * 20170209              Aaron                  Modify
 * ============================================================================		
 */
package com.pax.tms.res.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pax.common.dao.IBaseDao;
import com.pax.common.exception.BusinessException;
import com.pax.common.service.impl.BaseService;
import com.pax.tms.res.dao.ModelDao;
import com.pax.tms.res.model.Model;
import com.pax.tms.res.service.ModelService;

@Service("modelServiceImpl")
public class ModelServiceImpl extends BaseService<Model, String> implements ModelService {

	@Autowired
	private ModelDao modelDao;

	@Override
	public IBaseDao<Model, String> getBaseDao() {
		return modelDao;
	}

	@Override
	public Model validateModel(String modelId) {
		if (StringUtils.isEmpty(modelId)) {
			return null;
		}
		Model model = modelDao.get(modelId);
		if (model == null) {
			throw new BusinessException("model.NotFound", new String[] { modelId });
		}
		return model;
	}

	@Override
	public List<Model> getListByGroupId(Long groupId) {
		List<Model> modelList = modelDao.getListByGroupId(groupId);
		return modelList;
	}

	@Override
	public List<Model> getList() {
		return modelDao.getList();
	}

}
