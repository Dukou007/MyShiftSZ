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

import java.util.List;

import com.pax.common.pagination.IPageForm;
import com.pax.common.pagination.Page;

public interface IBaseService<M extends java.io.Serializable, PK extends java.io.Serializable> {

	public M save(M model);

	public void update(M model);

	public void delete(PK id);

	public void deleteObject(M model);

	public M get(PK id);

	public long count();

	public List<M> list();

	public List<M> list(int start, int length);

	<S extends java.io.Serializable> long count(S command);

	<E, S extends IPageForm> List<E> list(S command);

	<E, S extends IPageForm> Page<E> page(S command);
}
