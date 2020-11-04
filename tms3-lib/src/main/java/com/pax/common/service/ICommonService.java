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
package com.pax.common.service;

import java.io.Serializable;
import java.util.List;

import com.pax.common.model.AbstractModel;

public interface ICommonService {

	public <T extends AbstractModel> T save(T model);

	public <T extends AbstractModel> void update(T model);

	public <T extends AbstractModel, PK extends Serializable> void delete(Class<T> entityClass, PK id);

	public <T extends AbstractModel> void deleteObject(T model);

	public <T extends AbstractModel, PK extends Serializable> T get(Class<T> entityClass, PK id);

	public <T extends AbstractModel> int count(Class<T> entityClass);

	public <T extends AbstractModel> List<T> list(Class<T> entityClass);

	public <T extends AbstractModel> List<T> list(Class<T> entityClass, int start, int length);

}
