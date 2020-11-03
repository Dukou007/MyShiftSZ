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

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.TypedValue;

public class LikeExpressionImpl implements Criterion {

	private static final long serialVersionUID = 1L;

	private final String propertyName;
	private final String value;
	private final Character escapeChar;
	private final boolean ignoreCase;

	protected LikeExpressionImpl(String propertyName, String value, Character escapeChar, boolean ignoreCase) {
		this.propertyName = propertyName;
		this.value = value;
		this.escapeChar = escapeChar;
		this.ignoreCase = ignoreCase;
	}

	protected LikeExpressionImpl(String propertyName, String value) {
		this(propertyName, value, null, false);
	}

	protected LikeExpressionImpl(String propertyName, String value, MatchMode matchMode) {
		this(propertyName, matchMode.toMatchString(value));
	}

	public LikeExpressionImpl(String propertyName, String value, MatchMode matchMode, Character escapeChar,
			boolean ignoreCase) {
		this(propertyName, matchMode.toMatchString(escape(value)), escapeChar, ignoreCase);
	}

	@Override
	@SuppressWarnings("deprecation")
	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) {
		Dialect dialect = criteriaQuery.getFactory().getDialect();
		String[] columns = criteriaQuery.findColumns(propertyName, criteria);
		if (columns.length != 1) {
			throw new HibernateException("Like may only be used with single-column properties");
		}
		String escape = escapeChar == null ? "" : " escape \'" + escapeChar + "\'";
		String column = columns[0];
		if (ignoreCase) {
			if (dialect.supportsCaseInsensitiveLike()) {
				return column + " " + dialect.getCaseInsensitiveLike() + " ?" + escape;
			} else {
				return dialect.getLowercaseFunction() + '(' + column + ')' + " like ?" + escape;
			}
		} else {
			return column + " like ?" + escape;
		}
	}

	@Override
	public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) {
		return new TypedValue[] { criteriaQuery.getTypedValue(criteria, propertyName,
				ignoreCase ? value.toString().toLowerCase() : value.toString()) };
	}

	private static String escape(String str) {
		if (str == null) {
			return null;
		}
		return str.replaceAll("!", "!!").replaceAll("%", "!%").replaceAll("_", "!_");
	}

}
