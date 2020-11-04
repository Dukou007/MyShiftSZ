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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import com.pax.common.pagination.IPageForm;

public class CriteriaWrapper {

	private Map<String, Object> aliasMap;
	private Set<String> orderFields;

	private Criteria criteria;

	private ProjectionList projectionList = Projections.projectionList();

	public CriteriaWrapper(Criteria criteria) {
		super();
		this.criteria = criteria;
	}

	public DetachedCriteria subquery(Class<?> clazz, String alias) {
		return DetachedCriteria.forClass(clazz, alias);
	}

	public DetachedCriteria subquery(Class<?> clazz, String alias, String joinPropertyName,
			String otherJoinPropertyName) {
		return DetachedCriteria.forClass(clazz, alias)
				.add(Restrictions.eqProperty(joinPropertyName, otherJoinPropertyName));
	}

	public DetachedCriteria subquery(Class<?> clazz, String alias, String joinPropertyName1,
			String otherJoinPropertyName1, String joinPropertyName2, String otherJoinPropertyName2) {
		return DetachedCriteria.forClass(clazz, alias)
				.add(Restrictions.eqProperty(joinPropertyName1, otherJoinPropertyName1))
				.add(Restrictions.eqProperty(joinPropertyName2, otherJoinPropertyName2));
	}

	public void addCriterion(Criterion criterion) {
		criteria.add(criterion);
	}

	public void notExists(DetachedCriteria dc) {
		criteria.add(Subqueries.notExists(dc));
	}

	public void exists(DetachedCriteria dc) {
		criteria.add(Subqueries.exists(dc));
	}

	public void propertyIn(String propertyName, DetachedCriteria dc) {
		criteria.add(Subqueries.propertyIn(propertyName, dc));
	}

	public void propertyNotIn(String propertyName, DetachedCriteria dc) {
		criteria.add(Subqueries.propertyNotIn(propertyName, dc));
	}

	public void propertyEq(String propertyName, DetachedCriteria dc) {
		criteria.add(Subqueries.propertyEq(propertyName, dc));
	}

	public void propertyNe(String propertyName, DetachedCriteria dc) {
		criteria.add(Subqueries.propertyEq(propertyName, dc));
	}

	private Map<String, Object> getAliasMap() {
		if (aliasMap == null) {
			aliasMap = new HashMap<String, Object>();
		}
		return aliasMap;
	}

	public Criteria getCriteria() {
		return criteria;
	}

	public void createAlias(String associationPath, String alias) {
		createAlias(associationPath, alias, JoinType.INNER_JOIN);
	}

	public void createAlias(String associationPath, String alias, JoinType joinType) {
		if (getAliasMap().containsKey(alias)) {
			return;
		}

		if (joinType != null) {
			criteria.createAlias(associationPath, alias, joinType);
		} else {
			criteria.createAlias(associationPath, alias);
		}
		getAliasMap().put(alias, criteria);
	}

	public void addAlias(Criteria criteria, String alias) {
		getAliasMap().put(alias, criteria);
	}

	public Criteria getAlias(String alias) {
		return (Criteria) getAliasMap().get(alias);
	}

	public boolean hasAlias(String alias) {
		if (aliasMap == null) {
			return false;
		}
		return getAliasMap().containsKey(alias);
	}

	// Criterion
	public static Criterion likeAnywhere(String property, String value) {
		return new LikeExpressionImpl(property, value, MatchMode.ANYWHERE, '!', true);
	}

	public static Criterion likeStart(String property, String value) {
		return new LikeExpressionImpl(property, value, MatchMode.START, '!', true);
	}

	public void like(String property, String value) {
		if (StringUtils.isNotEmpty(value)) {
			criteria.add(CriteriaWrapper.likeAnywhere(property, value));
		}
	}

	public void like_start(String property, String value) {
		if (StringUtils.isNotEmpty(value)) {
			criteria.add(CriteriaWrapper.likeStart(property, value));
		}
	}

	public void fuzzy(String fuzzyCondition, String... properties) {
		if (StringUtils.isNotEmpty(fuzzyCondition)) {
			Criterion[] criterions = new Criterion[properties.length];
			int i = 0;
			for (String property : properties) {
				criterions[i++] = CriteriaWrapper.likeAnywhere(property, fuzzyCondition);
			}
			criteria.add(Restrictions.or(criterions));
		}
	}

	public void fuzzy(String fuzzyCondition, Function<String, String> processor, String... properties) {
		if (StringUtils.isNotEmpty(fuzzyCondition)) {
			Criterion[] criterions = new Criterion[properties.length];
			int i = 0;
			if (processor != null) {
				for (String property : properties) {
					property = processor.apply(property);
					criterions[i++] = CriteriaWrapper.likeAnywhere(property, fuzzyCondition);
				}
			} else {
				for (String property : properties) {
					criterions[i++] = CriteriaWrapper.likeAnywhere(property, fuzzyCondition);
				}
			}
			criteria.add(Restrictions.or(criterions));
		}
	}

	public void eq(String property, Object value) {
		if (value != null) {
			criteria.add(Restrictions.eq(property, value));
		}
	}

	public void or(String[] propeties, Object value) {
		if (value != null && ArrayUtils.isNotEmpty(propeties)) {
			Disjunction dis = Restrictions.disjunction();
			for (String property : propeties) {
				dis.add(Restrictions.eq(property, value));

			}
			criteria.add(dis);
		}
	}

	public void or(Criterion... criterions) {
		criteria.add(Restrictions.or(criterions));
	}

	public void ge(String property, Object value) {
		if (value != null) {
			criteria.add(Restrictions.ge(property, value));
		}
	}

	public void le(String property, Object value) {
		if (value != null) {
			criteria.add(Restrictions.le(property, value));
		}
	}

	public void eq(String property, String value) {
		if (StringUtils.isNotEmpty(value)) {
			criteria.add(Restrictions.eq(property, value));
		}
	}

	public void ne(String property, String value) {
        if (StringUtils.isNotEmpty(value)) {
            criteria.add(Restrictions.ne(property, value));
        }
    }

	
	public void ge(String property, String value) {
		if (StringUtils.isNotEmpty(value)) {
			criteria.add(Restrictions.ge(property, value));
		}
	}

	public void le(String property, String value) {
		if (StringUtils.isNotEmpty(value)) {
			criteria.add(Restrictions.le(property, value));
		}
	}

	// order
	public void addOrder(String orderBy, boolean ascending, String defaultOrderBy, boolean defaultAscending) {
		addOrder(orderBy, ascending);
		addDefaultOrder(defaultOrderBy, defaultAscending);
	}

	public void addOrder(IPageForm form) {
		addOrder(form.getOrderField(), form.isOrderAsc());
	}

	public void addOrder(IPageForm form, String defaultOrderBy, boolean defaultAscending) {
		addOrder(form.getOrderField(), form.isOrderAsc());
		addDefaultOrder(defaultOrderBy, defaultAscending);
	}

	public void addOrder(IPageForm form, String defaultOrderBy, boolean defaultAscending,
			Function<String, String> processor) {
		addOrder(form.getOrderField(), form.isOrderAsc(), processor);
		addDefaultOrder(defaultOrderBy, defaultAscending, processor);
	}

	public void addOrder(String orderField, boolean ascending, Function<String, String> processor) {
		if (StringUtils.isEmpty(orderField)) {
			return;
		}

		String resolveredField = orderField;
		if (processor != null) {
			resolveredField = processor.apply(resolveredField);
		}

		if (orderFields == null) {
			orderFields = new HashSet<String>();
			orderFields.add(resolveredField);
			criteria.addOrder(ascending ? Order.asc(resolveredField) : Order.desc(resolveredField));
			return;
		}

		if (!orderFields.contains(resolveredField)) {
			orderFields.add(resolveredField);
			criteria.addOrder(ascending ? Order.asc(resolveredField) : Order.desc(resolveredField));
		}
	}

	public void addOrder(String orderField, boolean ascending) {
		addOrder(orderField, ascending, null);
	}

	public void addDefaultOrder(String orderField, boolean ascending, Function<String, String> processor) {
		if (orderFields != null) {
			return;
		}

		String resolvedField = orderField;
		if (processor != null) {
			resolvedField = processor.apply(resolvedField);
		}

		orderFields = new HashSet<String>();
		orderFields.add(resolvedField);
		criteria.addOrder(ascending ? Order.asc(resolvedField) : Order.desc(resolvedField));
	}

	public void addDefaultOrder(String orderField, boolean ascending) {
		addDefaultOrder(orderField, ascending, null);
	}

	public void addDefaultOrder(String orderField1, boolean ascending1, String orderField2, boolean ascending2) {
		if (orderFields != null) {
			return;
		}

		orderFields = new HashSet<String>();
		orderFields.add(orderField1);
		criteria.addOrder(ascending1 ? Order.asc(orderField1) : Order.desc(orderField1));
		orderFields.add(orderField2);
		criteria.addOrder(ascending2 ? Order.asc(orderField2) : Order.desc(orderField2));
	}

	public void addDefaultOrder(String orderField1, boolean ascending1, String orderField2, boolean ascending2,
			String orderField3, boolean ascending3) {
		if (orderFields != null) {
			return;
		}

		orderFields = new HashSet<String>();
		orderFields.add(orderField1);
		criteria.addOrder(ascending1 ? Order.asc(orderField1) : Order.desc(orderField1));

		orderFields.add(orderField2);
		criteria.addOrder(ascending2 ? Order.asc(orderField2) : Order.desc(orderField2));

		orderFields.add(orderField3);
		criteria.addOrder(ascending3 ? Order.asc(orderField3) : Order.desc(orderField3));
	}

	public void setProjection(Map<String, String> map) {
		// ProjectionList pl = Projections.projectionList();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			projectionList.add(Projections.property(entry.getKey()), entry.getValue());
		}
		criteria.setProjection(projectionList);
		criteria.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
	}

	public void setProjection(List<String> list) {

		for (String prop : list) {

			projectionList.add(Projections.property(prop), prop);
		}
		criteria.setProjection(projectionList);
		criteria.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
	}

	public void group() {
		if (projectionList == null) {
			projectionList = Projections.projectionList();
			criteria.setProjection(projectionList);
		}
	}

	public void rowCount(String alias) {
		if (projectionList == null) {
			projectionList = Projections.projectionList();
			criteria.setProjection(projectionList);
		}
		projectionList.add(Projections.rowCount(), alias);
	}

	public void min(String propertyName, String alias) {
		if (projectionList == null) {
			projectionList = Projections.projectionList();
			criteria.setProjection(projectionList);
		}
		projectionList.add(Projections.min(propertyName), alias);
	}

	public void max(String propertyName, String alias) {
		if (projectionList == null) {
			projectionList = Projections.projectionList();
			criteria.setProjection(projectionList);
		}
		projectionList.add(Projections.max(propertyName), alias);
	}

	public void avg(String propertyName, String alias) {
		if (projectionList == null) {
			projectionList = Projections.projectionList();
			criteria.setProjection(projectionList);
		}
		projectionList.add(Projections.avg(propertyName), alias);
	}

	public void groupProperty(String propertyName, String alias) {
		if (projectionList == null) {
			projectionList = Projections.projectionList();
			criteria.setProjection(projectionList);
		}
		projectionList.add(Projections.groupProperty(propertyName), alias);
	}

	public ProjectionList getProjectionList() {
		return projectionList;
	}

	public void setProjectionList(ProjectionList projectionList) {
		this.projectionList = projectionList;
	}

}
