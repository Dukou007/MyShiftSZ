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
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.persistence.Id;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.NaturalIdLoadAccess;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.jdbc.Work;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;
import org.hibernate.type.TimestampType;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.pax.common.dao.DbHelper;
import com.pax.common.dao.IBaseDao;
import com.pax.common.pagination.Page;
import com.pax.common.util.InsertStatementHandler;
import com.pax.common.util.StatementHandler;

public class BaseHibernateDao<M extends java.io.Serializable, PK extends java.io.Serializable>
		implements IBaseDao<M, PK> {

	public static final boolean ORDER_ASC = true;
	public static final boolean ORDER_DESC = false;

	private final Class<M> entityClass;
	private String pkName;
	private final String hqlListAll;
	private final String hqlCountAll;

	@Autowired
	@Qualifier("sessionFactory")
	protected SessionFactory sessionFactory;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public BaseHibernateDao() {
		java.lang.reflect.Type reflectType = getClass().getGenericSuperclass();
		while (reflectType != null && !(reflectType instanceof ParameterizedType)) {
			reflectType = ((Class) reflectType).getGenericSuperclass();
		}
		if (reflectType == null) {
			throw new RuntimeException("Illegal hibernate dao " + this.getClass());
		}
		this.entityClass = (Class<M>) ((ParameterizedType) reflectType).getActualTypeArguments()[0];
		Field[] fields = this.entityClass.getDeclaredFields();
		for (Field f : fields) {
			if (f.isAnnotationPresent(Id.class)) {
				this.pkName = f.getName();
			}
		}

		hqlListAll = "from " + this.entityClass.getSimpleName() + " order by " + pkName + " desc";
		hqlCountAll = " select count(*) from " + this.entityClass.getSimpleName();
	}

	protected Session getSession() {
		// transaction is required, or else return null.
		return sessionFactory.getCurrentSession();
	}

	@SuppressWarnings("unchecked")
	@Override
	public PK save(M model) {
		return (PK) getSession().save(model);
	}

	@Override
	public void update(M model) {
		getSession().update(model);
	}

	@Override
	public void delete(PK id) {
		getSession().delete(this.get(id));

	}

	@Override
	public void deleteObject(M model) {
		getSession().delete(model);

	}

	@Override
	public M get(PK id) {
		return (M) getSession().get(entityClass, id);
	}

	@Override
	public M getReference(PK id) {
		return getSession().load(entityClass, id);
	}

	@Override
	public Optional<M> loadOptional(PK id) {
		return getSession().byId(entityClass).loadOptional(id);
	}

	@Override
	public M getByNaturalId(Object value) {
		return getSession().bySimpleNaturalId(entityClass).load(value);
	}

	@Override
	public M getByNatrualId(Map<String, Object> values) {
		NaturalIdLoadAccess<M> loadAccess = getSession().byNaturalId(entityClass);
		for (Map.Entry<String, Object> entry : values.entrySet()) {
			loadAccess.using(entry.getKey(), entry.getValue());
		}
		return loadAccess.load();
	}

	@Override
	public M getReferenceByNaturalId(Object value) {
		return getSession().bySimpleNaturalId(entityClass).getReference(value);
	}

	@Override
	public M getReferenceByNaturalId(Map<String, Object> values) {
		NaturalIdLoadAccess<M> loadAccess = getSession().byNaturalId(entityClass);
		for (Map.Entry<String, Object> entry : values.entrySet()) {
			loadAccess.using(entry.getKey(), entry.getValue());
		}
		return loadAccess.getReference();
	}

	@Override
	public Optional<M> loadOptionalByNaturalId(Serializable value) {
		return getSession().bySimpleNaturalId(entityClass).loadOptional(value);
	}

	@Override
	public Optional<M> loadOptionalByNatrualId(Map<String, Object> values) {
		NaturalIdLoadAccess<M> loadAccess = getSession().byNaturalId(entityClass);
		for (Map.Entry<String, Object> entry : values.entrySet()) {
			loadAccess.using(entry.getKey(), entry.getValue());
		}
		return loadAccess.loadOptional();
	}

	@Override
	public boolean exists(PK id) {
		return get(id) != null;
	}

	@Override
	public boolean existsNaturalId(Object value) {
		return getByNaturalId(value) != null;
	}

	@Override
	public boolean existsNatrualId(Map<String, Object> values) {
		return getByNaturalId(values) != null;
	}

	@Override
	public long count() {
		Query<Long> query = getSession().createQuery(hqlCountAll, Long.class);
		return query.getSingleResult();
	}

	@Override
	public List<M> list() {
		Query<M> query = getSession().createQuery(hqlListAll, entityClass);
		return query.getResultList();
	}

	@Override
	public List<M> list(int start, int length) {
		Query<M> query = getSession().createQuery(hqlListAll, entityClass);
		limit(query, start, length);
		return query.getResultList();
	}

	@Override
	public Stream<M> stream() {
		Query<M> query = getSession().createQuery(hqlListAll, entityClass);
		return query.stream();
	}

	@Override
	public void stream(Consumer<M> consumer) {
		try (Stream<M> stream = stream();) {
			stream.forEach(consumer);
		}
	}

	@Override
	public void refresh(M model) {
		getSession().refresh(model);
	}

	@Override
	public void evict(M model) {
		getSession().evict(model);
	}

	@Override
	public void flush() {
		getSession().flush();
	}

	@Override
	public void clear() {
		getSession().clear();
	}

	@Override
	public <S extends Serializable> long count(S command) {
		return count(getCriteria(command, false).getCriteria());
	}

	@Override
	public <T, S extends Serializable> List<T> list(S command) {
		return list(getCriteria(command, true).getCriteria());
	}

	@Override
	public <T, S extends Serializable> List<T> page(S command, int start, int length) {
		return limitAndList(getCriteria(command, true).getCriteria(), start, length);
	}

	/*
	 * others
	 */
	@SuppressWarnings({ "deprecation", "rawtypes" })
	protected Criteria createCriteria(Class persistentClass) {
		return getSession().createCriteria(persistentClass);
	}

	@SuppressWarnings({ "deprecation", "rawtypes" })
	protected Criteria createCriteria(Class persistentClass, String alias) {
		return getSession().createCriteria(persistentClass, alias);
	}

	@SuppressWarnings("rawtypes")
	protected CriteriaWrapper createCriteriaWrapper(Class persistentClass) {
		return new CriteriaWrapper(createCriteria(persistentClass));
	}

	@SuppressWarnings("rawtypes")
	protected CriteriaWrapper createCriteriaWrapper(Class persistentClass, String alias) {
		return new CriteriaWrapper(createCriteria(persistentClass, alias));
	}

	@SuppressWarnings({ "rawtypes" })
	protected Query createQuery(String hql) {
		return getSession().createQuery(hql);
	}

	protected <T> Query<T> createQuery(String hql, Class<T> resultType) {
		return createQuery(hql, resultType, true);
	}

	@SuppressWarnings({ "deprecation", "rawtypes", "unchecked" })
	protected <T> Query<T> createQuery(String hql, Class<T> resultType, boolean isEntity) {
		if (isEntity) {
			return getSession().createQuery(hql, resultType);
		} else {
			Query query = getSession().createQuery(hql);
			query.setResultTransformer(new AliasToBeanResultTransformer(resultType));
			return query;
		}
	}

	@SuppressWarnings({ "deprecation", "rawtypes", "unchecked" })
	protected <T> Query<T> createQuery(String hql, Class<T> resultType, ResultTransformer resultTransformer) {
		Query query = getSession().createQuery(hql);
		query.setResultTransformer(resultTransformer);
		return query;
	}

	@SuppressWarnings({ "rawtypes" })
	protected NativeQuery createNativeQuery(String nativeSQL) {
		return getSession().createNativeQuery(nativeSQL);
	}

	@SuppressWarnings("unchecked")
	protected <T> NativeQuery<T> createNativeQuery(String nativeSQL, Class<T> resultType) {
		return getSession().createNativeQuery(nativeSQL);
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	protected <T> NativeQuery<T> createNativeQuery(String nativeSQL, Class<T> resultType, boolean isEntity) {
		if (isEntity) {
			return getSession().createNativeQuery(nativeSQL, resultType);
		} else {
			NativeQuery<T> query = getSession().createNativeQuery(nativeSQL);
			query.setResultTransformer(new AliasToBeanResultTransformer(resultType));
			return query;
		}
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	protected <T> NativeQuery<T> createNativeQuery(String nativeSQL, Class<T> resultType,
			List<Entry<String, Type>> scalarList) {
		NativeQuery<T> query = getSession().createNativeQuery(nativeSQL);
		if (scalarList != null) {
			for (Entry<String, Type> entity : scalarList) {
				query.addScalar(entity.getKey(), entity.getValue());
			}
		}
		query.setResultTransformer(new AliasToBeanResultTransformer(resultType));
		return query;
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	protected <T> NativeQuery<T> createNativeQuery(String nativeSQL, Class<T> resultType, Map<String, Type> scalarMap) {
		NativeQuery<T> query = getSession().createNativeQuery(nativeSQL);
		if (scalarMap != null) {
			Set<Entry<String, Type>> set = scalarMap.entrySet();
			for (Entry<String, Type> entity : set) {
				query.addScalar(entity.getKey(), entity.getValue());
			}
		}
		query.setResultTransformer(new AliasToBeanResultTransformer(resultType));
		return query;
	}

	@SuppressWarnings("rawtypes")
	protected void setParameters(Query query, Object... paramlist) {
		if (paramlist != null) {
			for (int i = 0; i < paramlist.length; i++) {
				if (paramlist[i] instanceof Date) {
					query.setParameter(i, (Date) paramlist[i], TimestampType.INSTANCE);
				} else {
					query.setParameter(i, paramlist[i]);
				}
			}
		}
	}

	@SuppressWarnings("rawtypes")
	protected void setSqlParameters(Query query, Object... paramlist) {
		if (paramlist != null) {
			for (int i = 0; i < paramlist.length; i++) {
				if (paramlist[i] instanceof Date) {
					query.setParameter(i + 1, (Date) paramlist[i], TimestampType.INSTANCE);
				} else {
					query.setParameter(i + 1, paramlist[i]);
				}
			}
		}
	}

	@SuppressWarnings("rawtypes")
	protected void setParameters(Query query, Map<String, Object> params) {
		if (params != null) {
			for (Map.Entry<String, Object> param : params.entrySet()) {
				if (param.getValue() instanceof Date) {
					query.setParameter(param.getKey(), (Date) param.getValue(), TimestampType.INSTANCE);
				} else if (param.getValue() instanceof Collection) {
					query.setParameterList(param.getKey(), (Collection<?>) param.getValue());
				} else if (param.getValue().getClass().isArray()) {
					query.setParameterList(param.getKey(), (Object[]) param.getValue());
				} else {
					query.setParameter(param.getKey(), param.getValue());
				}
			}
		}
	}

	protected void limit(Criteria criteria, int start, int length) {
		if (start > 0) {
			criteria.setFirstResult(start);
		}

		if (length > 0) {
			criteria.setMaxResults(length);
		}
	}

	@SuppressWarnings("unchecked")
	protected <T> List<T> limitAndList(Criteria criteria, int start, int length) {
		if (start > 0) {
			criteria.setFirstResult(start);
		}

		if (length > 0) {
			criteria.setMaxResults(length);
		}
		return criteria.list();
	}

	protected <T> List<T> limitAndList(CriteriaWrapper wrapper, int start, int length) {
		return limitAndList(wrapper.getCriteria(), start, length);
	}

	protected <R> void limit(Query<R> query, int start, int length) {
		if (start > 0) {
			query.setFirstResult(start);
		}

		if (length > 0) {
			query.setMaxResults(length);
		}
	}

	@SuppressWarnings("unchecked")
	protected <T> List<T> list(Criteria criteria) {
		return criteria.list();
	}

	protected <T> List<T> list(CriteriaWrapper wrapper) {
		return list(wrapper.getCriteria());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected <T> List<T> list(Query query) {
		return query.getResultList();
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	protected <T> List<T> list(String orderField, boolean ascending) {
		Criteria criteria = getSession().createCriteria(entityClass).addOrder(Order.asc(orderField));
		if (ascending) {
			criteria.addOrder(Order.asc(orderField));
		} else {
			criteria.addOrder(Order.desc(orderField));
		}
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	protected <T> List<T> page(Criteria criteria, int pageNumber, int pageSize) {
		if (pageSize > -1) {
			criteria.setMaxResults(pageSize);
		}

		if (pageNumber > -1 && pageSize > -1) {
			int start = Page.getPageStart(pageNumber, pageSize);
			if (start > 0) {
				criteria.setFirstResult(start);
			}
		}
		return criteria.list();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected <T> List<T> page(Query query, int pageNumber, int pageSize) {
		if (pageSize > -1) {
			query.setMaxResults(pageSize);
		}

		if (pageNumber > -1 && pageSize > -1) {
			int start = Page.getPageStart(pageNumber, pageSize);
			if (start > 0) {
				query.setFirstResult(start);
			}
		}

		return query.getResultList();
	}

	protected long count(Criteria criteria) {
		criteria.setProjection(Projections.rowCount());
		return ((Number) criteria.uniqueResult()).intValue();
	}

	protected long count(CriteriaWrapper wrapper) {
		return count(wrapper.getCriteria());
	}

	@SuppressWarnings("unchecked")
	protected <T> T uniqueResult(Criteria criteria) {
		return (T) criteria.uniqueResult();
	}

	protected <T> T uniqueResult(CriteriaWrapper wrapper) {
		return uniqueResult(wrapper.getCriteria());
	}

	@SuppressWarnings({ "unchecked" })
	protected <T> T uniqueResult(String hql, Object... paramlist) {
		Query<?> query = getSession().createQuery(hql);
		setParameters(query, paramlist);
		return (T) query.uniqueResult();
	}

	@SuppressWarnings("rawtypes")
	protected int updateByHql(String hql, Object... paramlist) {
		Query query = getSession().createQuery(hql);
		setParameters(query, paramlist);
		Object result = query.executeUpdate();
		return result == null ? 0 : ((Integer) result).intValue();
	}

	@SuppressWarnings("rawtypes")
	protected int updateByNative(String nativeSQL, Object... paramlist) {
		NativeQuery query = getSession().createNativeQuery(nativeSQL);
		setParameters(query, paramlist);
		Object result = query.executeUpdate();
		return result == null ? 0 : ((Integer) result).intValue();
	}

	protected static String toMatchString(String str, MatchMode matchMode, boolean ignoreCase) {
		String s = str;
		if (ignoreCase && s != null) {
			s = s.toLowerCase();
		}
		return matchMode.toMatchString(escapeLikeValue(s));
	}

	private static String escapeLikeValue(String str) {
		if (str == null) {
			return null;
		}
		return str.replaceAll("!", "!!").replaceAll("%", "!%").replaceAll("_", "!_");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected <T> T uniqueResult(Query query) {
		return (T) query.uniqueResult();
	}

	protected <S extends Serializable> CriteriaWrapper getCriteria(S command, boolean ordered) {
		return null;
	}

	protected void doWork(Work work) {
		getSession().doWork(work);
	}

	protected <T> void doBatchExecute(String sql, Iterator<T> it, StatementHandler<T> handler) {
		doWork(conn -> {
			try (PreparedStatement st = conn.prepareStatement(sql)) {
				int count = 0;
				while (it.hasNext()) {
					handler.process(st, it.next());
					st.addBatch();
					count++;
					if (count % 100 == 0) {
						st.executeBatch();
						st.clearBatch();
					}
				}
				if (count % 100 != 0) {
					st.executeBatch();
					st.clearBatch();
				}
			}
		});
	}

	protected <T> void doBatchInsert(String sql, String pkSequnceName, Collection<T> coll,
			InsertStatementHandler<T> handler) {

		if (CollectionUtils.isEmpty(coll)) {
			return;
		}

		long relId = DbHelper.generateId(pkSequnceName, coll.size());

		doWork(conn -> {
			try (PreparedStatement st = conn.prepareStatement(sql)) {
				int count = 0;
				long id = relId;
				Iterator<T> it = coll.iterator();
				while (it.hasNext()) {
					handler.process(st, it.next(), id++);
					st.addBatch();
					count++;
					if (count % 100 == 0) {
						st.executeBatch();
						st.clearBatch();
					}
				}
				if (count % 100 != 0) {
					st.executeBatch();
					st.clearBatch();
				}
			}
		});
	}

	@SuppressWarnings({ "deprecation", "rawtypes" })
	protected Query mapResult(Query query) {
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return query;
	}

	@SuppressWarnings({ "deprecation", "rawtypes" })
	protected void addScalar(NativeQuery query, String columnAlias, Type type) {
		query.addScalar(columnAlias, type);
	}

	@SuppressWarnings({ "deprecation", "rawtypes" })
	protected void setResultTransformer(NativeQuery query, ResultTransformer fransformer) {
		query.setResultTransformer(fransformer);
	}

}
