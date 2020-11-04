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
package com.pax.tms.pxretailer.message;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pax.tms.pxretailer.PxRetailerException;
import com.pax.tms.pxretailer.PxRetailerProtocol;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TerminalLogsRequest extends BaseRequest {

	private static final long serialVersionUID = -6621223089253907690L;

	private static final String CLS = "CENTRALLOGGING";

	private String deviceName;
	private String source;
	private String sourceversion;
	private String sourcetype;
	private String message;
	private String severity;
	private String time;
	private String commtype;
	private String localip;
	private TerminalLogAppInfo[] appinfo;

	public TerminalLogsRequest() {
		super();
		this.requestType = CLS;
	}

	public void validateInput() {
        if (version < 1) {
            throw new PxRetailerException(PxRetailerProtocol.INVALID_REQUEST_RESPONSE, "The version is unsupported");
        }

        deviceName = StringUtils.trimToNull(deviceName);
        deviceSerialNumber = StringUtils.trimToNull(deviceSerialNumber);
        if (deviceName == null && deviceSerialNumber == null) {
            throw new PxRetailerException(PxRetailerProtocol.INVALID_REQUEST_RESPONSE, "The deviceName and deviceSerialNumber cannot both be empty");
        }
        
        source = StringUtils.trimToNull(source);
        if (source == null) {
            throw new PxRetailerException(PxRetailerProtocol.INVALID_REQUEST_RESPONSE,
                    "The source is empty");
        }
        
        sourceversion = StringUtils.trimToNull(sourceversion);
        if (sourceversion == null) {
            throw new PxRetailerException(PxRetailerProtocol.INVALID_REQUEST_RESPONSE,
                    "The sourceversion is empty");
        }
        
        message = StringUtils.trimToNull(message);
        if (message == null) {
            throw new PxRetailerException(PxRetailerProtocol.INVALID_REQUEST_RESPONSE,
                    "The message is empty");
        }
        
        severity = StringUtils.trimToNull(severity);
        if (severity == null) {
            throw new PxRetailerException(PxRetailerProtocol.INVALID_REQUEST_RESPONSE,
                    "The severity is empty");
        }
        
        time = StringUtils.trimToNull(time);
        if (time == null) {
            throw new PxRetailerException(PxRetailerProtocol.INVALID_REQUEST_RESPONSE,
                    "The time is empty");
        }
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

    public String getSourcetype() {
        return sourcetype;
    }

    public void setSourcetype(String sourcetype) {
        this.sourcetype = sourcetype;
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

    public String getCommtype() {
        return commtype;
    }

    public void setCommtype(String commtype) {
        this.commtype = commtype;
    }

    public String getLocalip() {
        return localip;
    }

    public void setLocalip(String localip) {
        this.localip = localip;
    }

    public TerminalLogAppInfo[] getAppinfo() {
        return appinfo;
    }

    public void setAppinfo(TerminalLogAppInfo[] appinfo) {
        this.appinfo = appinfo;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

}