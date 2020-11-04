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
package com.pax.common.web.form;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.pax.common.pagination.IPageForm;

public class QueryForm extends BaseForm implements IPageForm, Serializable {

	private static final long serialVersionUID = -1027472164710183047L;

	public static final String ASC_ORDER = "asc";
	public static final String DESC_ORDER = "desc";
	public static final Integer DEFAULT_PAGSIZE = 20;

	private Integer pageIndex;
	private Integer pageSize;
	private String orderField;
	private String orderDirection;
	private String fuzzyCondition;

	private String searchType;

	@Override
	public boolean isOrderAsc() {
		return StringUtils.isNotEmpty(orderDirection) && orderDirection.equalsIgnoreCase(ASC_ORDER) ? true : false;
	}

	@Override
	public void setOrderAsc() {
		orderDirection = ASC_ORDER;
	}

	@Override
	public void setOrderDesc() {
		orderDirection = DESC_ORDER;
	}

	@Override
	public Integer getPageIndex() {

		if (pageIndex == null || pageIndex <= 0) {
			pageIndex = 1;
		}
		return pageIndex;
	}

	@Override
	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	@Override
	public Integer getPageSize() {
		if (pageSize == null || pageSize <= 0) {
			pageSize = DEFAULT_PAGSIZE;
		}
		return pageSize;
	}

	@Override
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	@Override
	public String getOrderField() {
		return orderField;
	}

	@Override
	public void setOrderField(String orderField) {
		this.orderField = orderField;
	}

	public String getOrderDirection() {
		return orderDirection;
	}

	public void setOrderDirection(String orderDirection) {
		this.orderDirection = orderDirection;
	}

	public String getFuzzyCondition() {
		return fuzzyCondition;
	}

	public void setFuzzyCondition(String fuzzyCondition) {
		this.fuzzyCondition = fuzzyCondition;
	}

	public String getSearchType() {
		return searchType;
	}

	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

}
