package com.pax.tms.open.api.rsp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

import com.pax.tms.open.api.info.TerminalInfo;

@ApiModel
public class QueryTerminalResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	@ApiModelProperty(value = "TerminalInfo List", required = true)
	private List<TerminalInfo> items;// 录列表 当前页包含的记录
	@ApiModelProperty(value = "pageIndex", required = true)
	private int pageIndex;// 当前页页码(起始为1)
	@ApiModelProperty(value = "totalCount", required = true)
	private long totalCount;// 总记录数
	@ApiModelProperty(value = "pageSize", required = true)
	private int pageSize; // 每页显示记录数

	public List<TerminalInfo> getItems() {
		return items;
	}

	public void setItems(List<TerminalInfo> items) {
		this.items = items;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
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
