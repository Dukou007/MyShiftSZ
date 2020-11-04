/**
 * ============================================================================       
 * = COPYRIGHT		          
 *          PAX Computer Technology(Shenzhen) CO., LTD PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or nondisclosure 	
 *   agreement with PAX Computer Technology(Shenzhen) CO., LTD and may not be copied or 
 *   disclosed except in accordance with the terms in that agreement.       
 *       Copyright (C) 2019-? PAX Computer Technology(Shenzhen) CO., LTD All rights reserved.    
 * Description:       
 * Revision History:      
 * Date                         Author                    Action
 * 2019年12月19日 下午2:56:42           liming                   TerminalLogFrom
 * ============================================================================
 */
package com.pax.tms.monitor.web.form;

import com.pax.common.web.form.QueryForm;

public class TerminalLogFrom extends QueryForm{

    private static final long serialVersionUID = 1L;
    
    private String trmId;
    private String message;
    private Long groupId;
    
    public String getTrmId() {
        return trmId;
    }
    public void setTrmId(String trmId) {
        this.trmId = trmId;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public Long getGroupId() {
        return groupId;
    }
    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }
    
}
