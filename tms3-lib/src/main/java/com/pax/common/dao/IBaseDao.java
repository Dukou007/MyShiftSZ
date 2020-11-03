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
package com.pax.common.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

public interface IBaseDao<M extends java.io.Serializable, PK extends java.io.Serializable> {

	PK save(M model);

	void update(M model);

	void delete(PK id);

	void deleteObject(M model);

	M get(PK id);

	M getReference(PK id);

	Optional<M> loadOptional(PK id);

	M getByNaturalId(Object value);

	M getByNatrualId(Map<String, Object> values);

	M getReferenceByNaturalId(Object value);

	M getReferenceByNaturalId(Map<String, Object> values);

	Optional<M> loadOptionalByNaturalId(Serializable value);

	Optional<M> loadOptionalByNatrualId(Map<String, Object> values);

	boolean exists(PK id);

	boolean existsNaturalId(Object value);

	boolean existsNatrualId(Map<String, Object> values);

	long count();

	List<M> list();

	List<M> list(int start, int length);

	Stream<M> stream();

	void stream(Consumer<M> consumer);

	void refresh(M model);

	void evict(M model);

	void flush();

	void clear();

	// page
	<S extends java.io.Serializable> long count(S command);

	<T, S extends java.io.Serializable> List<T> list(S command);

	<T, S extends java.io.Serializable> List<T> page(S command, int start, int length);
}
