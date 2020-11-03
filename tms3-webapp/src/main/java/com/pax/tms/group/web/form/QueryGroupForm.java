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
package com.pax.tms.group.web.form;

import com.pax.common.web.form.QueryForm;

public class QueryGroupForm extends QueryForm {

	private static final long serialVersionUID = 1L;
	public static final String SEARCH_GROUP = "SEARCH_DESCANT_BY_KEYWORDS";
	public static final String GET_TOP_GROUP = "SEARCH_CHILD_BY_USER";
	public static final String GET_CHILD_GROUP = "SEARCH_CHILD_BY_GROUP";

	private String keyword;
	private boolean loadAll;

	public boolean isLoadAll() {
		return loadAll;
	}

	public void setLoadAll(boolean loadAll) {
		this.loadAll = loadAll;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

}
