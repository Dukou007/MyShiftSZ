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
 * 2018/11/5 15:13           liming                   ResultUtil
 * ============================================================================
 */
package com.pax.tms.open.api.rsp;

import org.apache.poi.ss.formula.functions.T;


public class ResultUtil{

    public static Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg("SUCCESS");
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }
    
	public static Result<T> error(Integer code, String msg) {
	    Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }

}
