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

import java.lang.reflect.Method;
import java.util.Arrays;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.property.access.internal.PropertyAccessStrategyBasicImpl;
import org.hibernate.property.access.internal.PropertyAccessStrategyChainedImpl;
import org.hibernate.property.access.spi.Setter;
import org.hibernate.transform.AliasedTupleSubsetResultTransformer;

@SuppressWarnings("rawtypes")
public class MyAliasToBeanResultTransformer extends AliasedTupleSubsetResultTransformer {

	private static final long serialVersionUID = -7887889109226067635L;

	private final Class resultClass;
	private boolean isInitialized;
	private String[] aliases;
	private Setter[] setters;

	private static class SetterLongImpl implements Setter {

		private static final long serialVersionUID = 5919599974728103685L;

		private Setter setter;

		public SetterLongImpl(Setter setter) {
			this.setter = setter;
		}

		@Override
		public void set(Object target, Object value, SessionFactoryImplementor factory) {
			setter.set(target, value == null ? null : ((Number) value).longValue(), factory);
		}

		@Override
		public String getMethodName() {
			return setter.getMethodName();
		}

		@Override
		public Method getMethod() {
			return setter.getMethod();
		}
	}

	public MyAliasToBeanResultTransformer(Class resultClass) {
		if (resultClass == null) {
			throw new IllegalArgumentException("resultClass cannot be null");
		}
		isInitialized = false;
		this.resultClass = resultClass;
	}

	@Override
	public boolean isTransformedValueATupleElement(String[] aliases, int tupleLength) {
		return false;
	}

	@Override
	public Object transformTuple(Object[] tuple, String[] aliases) {
		Object result;

		try {
			if (!isInitialized) {
				initialize(aliases);
			} else {
				check(aliases);
			}

			result = resultClass.newInstance();

			for (int i = 0; i < aliases.length; i++) {
				if (setters[i] != null) {
					setters[i].set(result, tuple[i], null);
				}
			}
		} catch (InstantiationException e) {
			throw new HibernateException("Could not instantiate resultclass: " + resultClass.getName());
		} catch (IllegalAccessException e) {
			throw new HibernateException("Could not instantiate resultclass: " + resultClass.getName());
		}

		return result;
	}

	private synchronized void initialize(String[] aliases) {
		if (isInitialized) {
			return;
		}
		PropertyAccessStrategyChainedImpl propertyAccessStrategy = new PropertyAccessStrategyChainedImpl(
				PropertyAccessStrategyBasicImpl.INSTANCE);
		this.aliases = new String[aliases.length];
		setters = new Setter[aliases.length];
		for (int i = 0; i < aliases.length; i++) {
			String alias = aliases[i];
			if (alias != null) {
				this.aliases[i] = alias;
				setters[i] = propertyAccessStrategy.buildPropertyAccess(resultClass, alias).getSetter();
				Method method = setters[i].getMethod();
				if (method != null) {
					String parameterTypes = method.getParameterTypes()[0].getName();
					if ("long".equals(parameterTypes) || "java.lang.Long".equals(parameterTypes)) {
						setters[i] = new SetterLongImpl(setters[i]);
					}
				}
			}
		}
		isInitialized = true;
	}

	private void check(String[] aliases) {
		if (!Arrays.equals(aliases, this.aliases)) {
			throw new IllegalStateException("aliases are different from what is cached; aliases="
					+ Arrays.asList(aliases) + " cached=" + Arrays.asList(this.aliases));
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		MyAliasToBeanResultTransformer that = (MyAliasToBeanResultTransformer) o;

		if (!resultClass.equals(that.resultClass)) {
			return false;
		}
		if (!Arrays.equals(aliases, that.aliases)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = resultClass.hashCode();
		result = 31 * result + (aliases != null ? Arrays.hashCode(aliases) : 0);
		return result;
	}

}
