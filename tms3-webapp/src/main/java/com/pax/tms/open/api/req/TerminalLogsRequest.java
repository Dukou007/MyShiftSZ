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
package com.pax.tms.open.api.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

@ApiModel
public class TerminalLogsRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @ApiModelProperty(value = "Terminal SN", required = true)
    @NotBlank(message = "Please enter TSN")
    @Length(min = 0,max = 36,message = "TSN length 0-36")
    private String tsn;
    
    @ApiModelProperty(value = "terminal type", required = false, example = "A920")
    private String terminalType;
    
    @ApiModelProperty(value = "log source", required = true, example = "PxResetService")
    @NotBlank(message = "Please enter source")
    @Length(min = 0,max = 50,message = "source length 0-50")
    private String source;
    
    @ApiModelProperty(value = "version of log source ", required = true, example = " 1.23.00")
    @NotBlank(message = "Please enter sourceversion")
    @Length(min = 0,max = 36,message = "TSN length 0-36")
	private String sourceversion;
    
    @ApiModelProperty(value = "log message", required = true, example="Exception: Application failed to obtain device data: GPS data access denied.")
    @NotBlank(message = "Please enter message")
    private String message;
   
    @ApiModelProperty(value = "severity of the log", required = true, example = "INFO,WARNING, DEBUG, ERROR")
    @NotBlank(message = "Please enter severity")
    @Length(min = 0,max = 16,message = "severity length 0-16")
	private String severity;
   
    @ApiModelProperty(value = "date/time UTC format", required = true, example = "2019/12/18 19:45:23.321")
    @NotBlank(message = "Please enter time")
    @Length(min = 0,max = 24,message = "time length 0-24")
	private String time;

    public String getTsn() {
        return tsn;
    }

    public void setTsn(String tsn) {
        this.tsn = tsn;
    }

    public String getTerminalType() {
        return terminalType;
    }

    public void setTerminalType(String terminalType) {
        this.terminalType = terminalType;
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

}