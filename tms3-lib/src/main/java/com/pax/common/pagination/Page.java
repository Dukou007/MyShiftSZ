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

import java.util.Collections;
import java.util.List;

/**
 * 表示分页中的一页。
 */
public class Page<E> {

	/**
	 * 默认设定每页显示记录数为20
	 */
	static final int DEFAULT_PAGE_SIZE = 20;

	private List<E> items;// 当前页包含的记录列表
	private int pageIndex;// 当前页页码(起始为1)
	private long totalCount;// 总记录数
	private int pageSize; // 每页显示记录数

	public Page(int pageIndex, int pageSize) {
		this.pageIndex = pageIndex <= 0 ? 1 : pageIndex;
		this.pageSize = pageSize == 0 ? DEFAULT_PAGE_SIZE : pageSize;
	}

	public Page(int pageIndex, int pageSize, long totalCount, List<E> items) {
		this(pageIndex, pageSize);
		this.totalCount = totalCount;
		this.items = items;
	}

	/**
	 * 不关心总记录数
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public static int getPageStart(int index, int pageSize) {
		return (index - 1) * pageSize;
	}

	/**
	 * 计算分页获取数据时游标的起始位置
	 * 
	 * @param totalCount
	 *            所有记录总和
	 * @param pageNumber
	 *            页码,从1开始
	 * @return
	 */
	public static int getPageStart(long totalCount, int index, int pageSize) {
		int start = (index - 1) * pageSize;
		if (start >= totalCount) {
			start = 0;
		}
		return start;
	}

	public static <E> Page<E> getPage(int index, int pageSize, long totalCount, List<E> items) {
		int ix = index;
		if ((index - 1) * pageSize >= totalCount) {
			ix = 0;
		}
		return new Page<E>(ix, pageSize, totalCount, items);
	}

	/**
	 * 计算总页数.
	 * 
	 * @return
	 */
	public int getPageCount() {
		long div = totalCount / pageSize;
		long result = (totalCount % pageSize == 0) ? div : div + 1;
		return (int) result;
	}

	public boolean hasPre() {
		return pageIndex > 1;
	}

	public boolean hasNext() {
		return pageIndex < getPageCount();
	}

	public List<E> getItems() {
		return this.items == null ? Collections.<E>emptyList() : this.items;
	}

	public void setItems(List<E> items) {
		this.items = items;
	}

	public long getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int index) {
		this.pageIndex = index;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
}
