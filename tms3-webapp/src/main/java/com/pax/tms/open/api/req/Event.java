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
 * 2019年12月23日 下午1:56:28           liming                   Event
 * ============================================================================
 */
package com.pax.tms.open.api.req;

public class Event{
    private String requestType;
    private String deviceType ;
    private String deviceSerialNumber ;
    private String source;
    private String sourceversion ;
    private String message ;
    private String severity;
    private String time;
    private String localip;
    private String commtype;
    
    public String getRequestType() {
        return requestType;
    }
    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }
    public String getDeviceType() {
        return deviceType;
    }
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
    public String getDeviceSerialNumber() {
        return deviceSerialNumber;
    }
    public void setDeviceSerialNumber(String deviceSerialNumber) {
        this.deviceSerialNumber = deviceSerialNumber;
    }
    public String getSource() {
        return source;
    }
    public void setSource(String source) {
        this.source = source;
    }
    public String getSourceversion() {
        return sourceversion;
    }
    public void setSourceversion(String sourceversion) {
        this.sourceversion = sourceversion;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getSeverity() {
        return severity;
    }
    public void setSeverity(String severity) {
        this.severity = severity;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getLocalip() {
        return localip;
    }
    public void setLocalip(String localip) {
        this.localip = localip;
    }
    public String getCommtype() {
        return commtype;
    }
    public void setCommtype(String commtype) {
        this.commtype = commtype;
    }
    
} 
