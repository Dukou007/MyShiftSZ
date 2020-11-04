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
package com.pax.tms.pxretailer.action;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pax.tms.download.model.SystemConf;
import com.pax.tms.download.model.Terminal;
import com.pax.tms.download.model.TerminalLog;
import com.pax.tms.pxretailer.PxRetailerConfig;
import com.pax.tms.pxretailer.PxRetailerProtocol;
import com.pax.tms.pxretailer.message.Event;
import com.pax.tms.pxretailer.message.TerminalLogAppInfo;
import com.pax.tms.pxretailer.message.TerminalLogSplunk;
import com.pax.tms.pxretailer.message.TerminalLogsRequest;
import com.pax.tms.pxretailer.message.TerminalLogsResponse;

@Service
public class TerminalLogsAction extends AbstractTerminalAction<TerminalLogsRequest> {
    private static final String TERMINAL_LOG_ROUTE = "/cls/request";
    @Autowired
    private PxRetailerConfig configs;
    
    @Override
    public void registerAction(Router router, JsonObject config) {
        router.post(TERMINAL_LOG_ROUTE).handler(createBodyHandler(config));
        router.post(TERMINAL_LOG_ROUTE).handler(this::handle).failureHandler(this::handleException);
    }
    /**
     * Handle download file requests
     * @param routingContext
     */
    @Override
    public void handle(RoutingContext routingContext) {
        TerminalLogsRequest terminalLogsRequest = Json.decodeValue(routingContext.getBodyAsString(), TerminalLogsRequest.class);
        terminalLogsRequest.validateInput();
        if(!checkLogSeverity(routingContext, terminalLogsRequest)){
            return;
        }
        if(StringUtils.isNotBlank(configs.getTerminalLogPPM()) && StringUtils.equalsIgnoreCase(configs.getTerminalLogPPM(), "true")){
            logger.debug("terminalLog save PPM");
            TerminalLog terminalLog = new TerminalLog();
            terminalLog.setTrmId(terminalLogsRequest.getDeviceSerialNumber());
            terminalLog.setDeviceName(terminalLogsRequest.getDeviceName());
            terminalLog.setDeviceType(terminalLogsRequest.getDeviceType());
            terminalLog.setSource(terminalLogsRequest.getSource());
            terminalLog.setSourceVersion(terminalLogsRequest.getSourceversion());
            terminalLog.setSourceType(terminalLogsRequest.getSourcetype());
            terminalLog.setSeverity(terminalLogsRequest.getSeverity());
            terminalLog.setMessage(terminalLogsRequest.getMessage());
            terminalLog.setEventLocalTime(terminalLogsRequest.getTime());
            terminalLog.setCommType(terminalLogsRequest.getCommtype());
            terminalLog.setLocalIp(terminalLogsRequest.getLocalip());
            TerminalLogAppInfo[] appinfo = terminalLogsRequest.getAppinfo();
            if(appinfo != null && appinfo.length > 0){
                String appInfoMsg = Json.encode(appinfo);
                terminalLog.setAppInfo(appInfoMsg);
            }else {
                terminalLog.setAppInfo("");
            }
            terminalLog.setCreateDate(new Date());
            terminalStorageService.saveTerminalLog(terminalLog);
        }
        if(StringUtils.isNotBlank(configs.getTerminalLogSplunk()) && StringUtils.equalsIgnoreCase(configs.getTerminalLogSplunk(), "true")){
            logger.debug("terminalLog send to splunk");
            TerminalLogSplunk terminalLogSplunk = new TerminalLogSplunk();
            terminalLogSplunk.setSource(terminalLogsRequest.getSource());
            terminalLogSplunk.setSourcetype("PAXLog");
            Event event = new Event();
            event.setRequestType(terminalLogsRequest.getRequestType());
            event.setDeviceType(terminalLogsRequest.getDeviceType());
            event.setDeviceSerialNumber(terminalLogsRequest.getDeviceSerialNumber());
            event.setDeviceName(terminalLogsRequest.getDeviceName());
            event.setSource(terminalLogsRequest.getSource());
            event.setSourceversion(terminalLogsRequest.getSourceversion());
            event.setMessage(terminalLogsRequest.getMessage());
            event.setSeverity(terminalLogsRequest.getSeverity());
            event.setTime(terminalLogsRequest.getTime());
            event.setLocalip(terminalLogsRequest.getLocalip());
            event.setCommtype(terminalLogsRequest.getCommtype());
            event.setAppinfo(terminalLogsRequest.getAppinfo());
            terminalLogSplunk.setEvent(event);
            String reqJson = Json.encode(terminalLogSplunk);
            terminalStorageService.sendTerminalLogToSplunk(configs.getTerminalLogSplunkUrl(), reqJson, configs.getTerminalLogSplunkToken());
         }
        sendResponse(routingContext, new TerminalLogsResponse());
    }
    @Override
    public void process(RoutingContext routingContext, TerminalLogsRequest req, Terminal terminal) {
        
    }
    
    @Override
    public TerminalLogsResponse createResponse(int statusCode, String statusMessage) {
        return new TerminalLogsResponse(statusCode, statusMessage);
    }
 
    private boolean checkLogSeverity(RoutingContext routingContext, TerminalLogsRequest terminalLogsRequest){
        SystemConf systemConf = terminalStorageService.findByParaKey("TerminalLogLevel");
        if(null == systemConf){
            return true;
        }
        Map<String,Integer> levelMap = new HashMap<>();
        levelMap.put("INFO", 0);
        levelMap.put("WARNING", 1);
        levelMap.put("ERROR", 2);
        levelMap.put("DEBUG", 3);
        String logLevel = systemConf.getValue();
        String terminalLogSeverity = terminalLogsRequest.getSeverity().toUpperCase();
        if(StringUtils.equalsIgnoreCase("OFF", logLevel)){
            sendResponse(routingContext, this.createResponse(PxRetailerProtocol.INVALID_REQUEST_RESPONSE,
                    "No level logs shall be accepted."));
            return false;
        }else if(StringUtils.equalsIgnoreCase("INFO", logLevel) && (!levelMap.containsKey(terminalLogSeverity) || levelMap.get(terminalLogSeverity) > 0 )){
            sendResponse(routingContext, this.createResponse(PxRetailerProtocol.INVALID_REQUEST_RESPONSE,
                    "Only INFO logs shall be accepted."));
            return false;
        } else if(StringUtils.equalsIgnoreCase("WARNING", logLevel) && (!levelMap.containsKey(terminalLogSeverity) || levelMap.get(terminalLogSeverity) > 1)){
            sendResponse(routingContext, this.createResponse(PxRetailerProtocol.INVALID_REQUEST_RESPONSE,
                    "Only INFO and WARNING logs shall be accepted."));
            return false;
        }else if(StringUtils.equalsIgnoreCase("ERROR", logLevel) && (!levelMap.containsKey(terminalLogSeverity) || levelMap.get(terminalLogSeverity) > 2)){
            sendResponse(routingContext, this.createResponse(PxRetailerProtocol.INVALID_REQUEST_RESPONSE,
                    "Only INFO,WARNING,ERROR logs shall be accepted."));
            return false;
        }else if(StringUtils.equalsIgnoreCase("DEBUG", logLevel) && !levelMap.containsKey(terminalLogSeverity)){
            sendResponse(routingContext, this.createResponse(PxRetailerProtocol.INVALID_REQUEST_RESPONSE,
                    "Only INFO, WARNING,ERROR,DEBUG logs shall be accepted."));
            return false;
        }
        return true;
    }
    
    
}
