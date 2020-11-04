/*
 * ============================================================================
 * = COPYRIGHT				
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *   	Copyright (C) 2009-2020 PAX Technology, Inc. All rights reserved.		
 * ============================================================================		
 */
package com.pax.common.service.impl;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pax.common.dao.ICommonDao;
import com.pax.common.model.AbstractModel;
import com.pax.common.service.ICommonService;

@Service("CommonService")
public class CommonService implements ICommonService {

	@Autowired
	@Qualifier("CommonHibernateDao")
	private ICommonDao commonDao;

	@Override
	public <T extends AbstractModel> T save(T model) {
		return commonDao.save(model);
	}

	@Override
	public <T extends AbstractModel> void update(T model) {
		commonDao.update(model);
	}

	@Override
	public <T extends AbstractModel, PK extends Serializable> void delete(Class<T> entityClass, PK id) {
		commonDao.delete(entityClass, id);
	}

	@Override
	public <T extends AbstractModel> void deleteObject(T model) {
		commonDao.deleteObject(model);
	}

	@Override
	public <T extends AbstractModel, PK extends Serializable> T get(Class<T> entityClass, PK id) {
		return commonDao.get(entityClass, id);

	}

	@Override
	public <T extends AbstractModel> int count(Class<T> entityClass) {
		return commonDao.count(entityClass);
	}

	@Override
	public <T extends AbstractModel> List<T> list(Class<T> entityClass) {
		return commonDao.list(entityClass);
	}

	@Override
	public <T extends AbstractModel> List<T> list(Class<T> entityClass, int start, int length) {
		return commonDao.list(entityClass, start, length);
	}

}
