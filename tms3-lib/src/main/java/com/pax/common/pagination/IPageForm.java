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
package com.pax.common.pagination;

public interface IPageForm extends java.io.Serializable {

	Integer getPageIndex();

	void setPageIndex(int pageIndex);

	Integer getPageSize();

	void setPageSize(int pageSize);

	boolean isOrderAsc();

	void setOrderAsc();

	void setOrderDesc();

	String getOrderField();

	void setOrderField(String orderField);

}
