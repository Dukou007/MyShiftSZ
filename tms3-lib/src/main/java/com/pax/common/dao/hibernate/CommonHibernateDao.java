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
package com.pax.common.dao.hibernate;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.pax.common.dao.ICommonDao;
import com.pax.common.model.AbstractModel;

@Component("CommonHibernateDao")
public class CommonHibernateDao implements ICommonDao {

	@Autowired
	@Qualifier("sessionFactory")
	protected SessionFactory sessionFactory;

	protected Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public <T extends AbstractModel> T save(T model) {
		getSession().save(model);
		return model;
	}

	@Override
	public <T extends AbstractModel> void update(T model) {
		getSession().update(model);
	}

	@Override
	public <T extends AbstractModel, PK extends Serializable> void delete(Class<T> entityClass, PK id) {
		getSession().delete(get(entityClass, id));
	}

	@Override
	public <T extends AbstractModel> void deleteObject(T model) {
		getSession().delete(model);
	}

	@Override
	public <T extends AbstractModel, PK extends Serializable> T get(Class<T> entityClass, PK id) {
		return (T) getSession().get(entityClass, id);
	}

	@Override
	@SuppressWarnings("deprecation")
	public <T extends AbstractModel> int count(Class<T> entityClass) {
		Criteria criteria = getSession().createCriteria(entityClass);
		criteria.setProjection(Projections.rowCount());
		return ((Long) criteria.uniqueResult()).intValue();
	}

	@Override
	@SuppressWarnings({ "unchecked", "deprecation" })
	public <T extends AbstractModel> List<T> list(Class<T> entityClass) {
		Criteria criteria = getSession().createCriteria(entityClass);
		return criteria.list();
	}

	@Override
	@SuppressWarnings({ "unchecked", "deprecation" })
	public <T extends AbstractModel> List<T> list(Class<T> entityClass, int start, int length) {
		Criteria criteria = getSession().createCriteria(entityClass);
		if (start > 0) {
			criteria.setFirstResult(start);
		}
		if (length > 0) {
			criteria.setMaxResults(length);
		}
		return criteria.list();
	}
}
