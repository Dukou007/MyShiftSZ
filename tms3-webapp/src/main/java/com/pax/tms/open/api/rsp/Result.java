/**
 * ============================================================================
 * = COPYRIGHT
 * PAX Computer Technology(Shenzhen) CO., LTD PROPRIETARY INFORMATION
 * This software is supplied under the terms of a license agreement or nondisclosure
 * agreement with PAX Computer Technology(Shenzhen) CO., LTD and may not be copied or
 * disclosed except in accordance with the terms in that agreement.
 * Copyright (C) 2018/11/5-? PAX Computer Technology(Shenzhen) CO., LTD All rights reserved.
 * Description:
 * Revision History:
 * Date                         Author                    Action
 * 2018/11/5 15:13           liming                   Result
 * ============================================================================
 */
package com.pax.tms.open.api.rsp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import org.apache.poi.ss.formula.functions.T;

@SuppressWarnings("hiding")
@ApiModel(value = "Result", description = "response")
public class Result<T> {
    @ApiModelProperty(value = "Return status code", example = "200", required = true)
	private Integer code;
    @ApiModelProperty(value = "Returned messages", example = "Success", required = true)
	private String msg;
    @ApiModelProperty(value = "Returned data", required = false)
	private T data;
    private long timestamp = System.currentTimeMillis();// 返回时间戳

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public void success(T object) {
        this.code = 200;
        this.msg = "SUCCESS";
        this.data = object;
        this.timestamp = System.currentTimeMillis();
    }
}
