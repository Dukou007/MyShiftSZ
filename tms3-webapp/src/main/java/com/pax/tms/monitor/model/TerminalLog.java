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
package com.pax.tms.monitor.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.pax.common.model.AbstractModel;

@Entity
@Table(name = "TMSTTRM_LOG")
public class TerminalLog extends AbstractModel {
    private static final long serialVersionUID = 1L;
    
    public static final String ID_SEQUENCE_NAME = "TMSTTRM_LOG_ID";
    private static final String INCREMENT_SIZE = "20";
    
    public static final String LIST_URL = "/dashboard/usage/";
    public static final String TITLE = "TERMINALLOG";

    @Id
    @GeneratedValue(generator = ID_SEQUENCE_NAME + "_GEN")
    @GenericGenerator(name = ID_SEQUENCE_NAME + "_GEN", strategy = "enhanced-table", parameters = {
            @Parameter(name = "table_name", value = "PUBTSEQUENCE"),
            @Parameter(name = "value_column_name", value = "NEXT_VALUE"),
            @Parameter(name = "segment_column_name", value = "SEQ_NAME"),
            @Parameter(name = "segment_value", value = ID_SEQUENCE_NAME),
            @Parameter(name = "increment_size", value = INCREMENT_SIZE),
            @Parameter(name = "optimizer", value = "pooled-lo") })
    @Column(name = "ID")
    private Long id;
    
    @Column(name = "TRM_ID")
	private String trmId ;
    
    @Column(name = "DEVICE_NAME")
    private String deviceName;
    
    @Column(name = "DEVICE_TYPE")
    private String deviceType ;
    
    @Column(name = "SOURCE")
    private String source ;
    
    @Column(name = "SOURCE_VERSION")
    private String sourceVersion ;
    
    @Column(name = "SOURCE_TYPE")
    private String sourceType ;
    
    @Column(name = "SEVERITY")
    private String severity ;
    
    @Column(name = "MESSAGE")
    private String message ;
    
    @Column(name = "EVENT_LOCAL_TIME")
    private String eventLocalTime ;
    
    @Column(name = "COMM_TYPE")
    private String commType ;
    
    @Column(name = "LOCAL_IP")
    private String localIp ;
    
    @Column(name = "APP_INFO")
    private String appInfo ;
    
    @Column(name = "CREATE_DATE")
    private Date createDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTrmId() {
        return trmId;
    }

    public void setTrmId(String trmId) {
        this.trmId = trmId;
    }
    
    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSourceVersion() {
        return sourceVersion;
    }

    public void setSourceVersion(String sourceVersion) {
        this.sourceVersion = sourceVersion;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEventLocalTime() {
        return eventLocalTime;
    }

    public void setEventLocalTime(String eventLocalTime) {
        this.eventLocalTime = eventLocalTime;
    }

    public String getCommType() {
        return commType;
    }

    public void setCommType(String commType) {
        this.commType = commType;
    }

    public String getLocalIp() {
        return localIp;
    }

    public void setLocalIp(String localIp) {
        this.localIp = localIp;
    }

    public String getAppInfo() {
        return appInfo;
    }

    public void setAppInfo(String appInfo) {
        this.appInfo = appInfo;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
    
}
