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
 * 2019年7月25日 上午10:21:15           liming                   GroupUsageInfo
 * ============================================================================
 */
package com.pax.tms.monitor.domain;

public class GroupUsageCount {
    
    private String itemName;
    private String dateStr;
    private Integer total;
    private Integer errCount;
    private Integer pendingCount;
    
    public String getItemName() {
        return itemName;
    }
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    public String getDateStr() {
        return dateStr;
    }
    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }
    public Integer getTotal() {
        return total;
    }
    public void setTotal(Integer total) {
        this.total = total;
    }
    public Integer getErrCount() {
        return errCount;
    }
    public void setErrCount(Integer errCount) {
        this.errCount = errCount;
    }
    public Integer getPendingCount() {
        return pendingCount;
    }
    public void setPendingCount(Integer pendingCount) {
        this.pendingCount = pendingCount;
    }
    
    @Override
    public String toString() {
        return "GroupUsageCount [itemName=" + itemName + ", dateStr=" + dateStr + ", total=" + total + ", errCount="
                + errCount + ", pendingCount=" + pendingCount + "]";
    }
    
}
