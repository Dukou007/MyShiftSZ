package com.pax.tms.open.api.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class QueryTerminalRequest {

	@ApiModelProperty(value = "group id", required = true)
	private Long groupId;

	@ApiModelProperty(value = "serarch in terminal sn and terminal type", required = false)
	private String keyword;

	@ApiModelProperty(value = "page size default 10 records", required = false)
	private Integer pageSize = 10;

	@ApiModelProperty(value = "page num default 1 page", required = false)
	private Integer pageNum = 1;

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getPageNum() {
		return pageNum;
	}

	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
	}

}
