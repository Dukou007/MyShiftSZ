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
 * 2019年8月19日 下午3:56:56           liming                   GroupMsg
 * ============================================================================
 */
package com.pax.tms.download.model;

import java.io.Serializable;

public class GroupMsg implements Serializable {

    /**
     * @fieldName: serialVersionUID
     * @fieldType: long
     */
    private static final long serialVersionUID = 1L;
    
    private Long groupId;
    
    private String groupName;
    
    private String timeZone;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }
    
}
