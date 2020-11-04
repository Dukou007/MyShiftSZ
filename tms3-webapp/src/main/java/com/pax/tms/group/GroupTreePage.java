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
package com.pax.tms.group;

import java.util.List;

import com.pax.common.pagination.Page;

public class GroupTreePage<E> extends Page<E> {

	private Long activeGroup;

	public GroupTreePage(int pageIndex, int pageSize, long totalCount, List<E> items) {
		super(pageIndex, pageSize, totalCount, items);
	}

	public static <E> GroupTreePage<E> getGroupTreePage(int index, int pageSize, long totalCount, List<E> items) {
		int ix = index;
		if ((index - 1) * pageSize >= totalCount) {
			ix = 0;
		}
		return new GroupTreePage<E>(ix, pageSize, totalCount, items);
	}

	public Long getActiveGroup() {
		return activeGroup;
	}

	public void setActiveGroup(Long activeGroup) {
		this.activeGroup = activeGroup;
	}

}
