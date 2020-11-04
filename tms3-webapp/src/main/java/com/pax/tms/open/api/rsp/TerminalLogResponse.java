/**
 * ============================================================================       
 * = COPYRIGHT		          
 *          PAX Computer Technology(Shenzhen) CO., LTD PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or nondisclosure 	
 *   agreement with PAX Computer Technology(Shenzhen) CO., LTD and may not be copied or 
 *   disclosed except in accordance with the terms in that agreement.       
 *       Copyright (C) 2020-? PAX Computer Technology(Shenzhen) CO., LTD All rights reserved.    
 * Description:       
 * Revision History:      
 * Date                         Author                    Action
 * 2020年3月30日 下午2:24:33           liming                   TerminalLogResponse
 * ============================================================================
 */
package com.pax.tms.open.api.rsp;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class TerminalLogResponse  implements Serializable {

    private static final long serialVersionUID = 548063203015838237L;
    @ApiModelProperty(value = "Terminal SN", required = true)
    private String tsn;
    @ApiModelProperty(value = "Success|Failed:[reason]", required = true)
    private String status;
    
    public String getTsn() {
        return tsn;
    }
    public void setTsn(String tsn) {
        this.tsn = tsn;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    
}
