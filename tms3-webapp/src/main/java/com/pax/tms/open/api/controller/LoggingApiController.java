package com.pax.tms.open.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.vertx.core.json.Json;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pax.common.web.controller.BaseController;
import com.pax.common.web.form.BaseForm;
import com.pax.tms.monitor.model.TerminalLog;
import com.pax.tms.monitor.service.TerminalLogService;
import com.pax.tms.open.api.annotation.ApiPermission;
import com.pax.tms.open.api.req.Event;
import com.pax.tms.open.api.req.TerminalLogSplunk;
import com.pax.tms.open.api.req.TerminalLogsRequest;
import com.pax.tms.open.api.rsp.Result;
import com.pax.tms.open.api.rsp.TerminalLogResponse;
import com.pax.tms.terminal.service.TerminalService;
import com.pax.tms.web.component.PPMConfiguration;

@RestController
@Api(tags = {"Logging"})
@RequestMapping("/logging/api")
public class LoggingApiController extends BaseController {

    @Autowired
    private TerminalService terminalService;
    
    @Autowired
    private TerminalLogService terminalLogService;

    @Autowired
    private PPMConfiguration config;

    @ApiOperation(value = "add terminal log", notes = "add terminal log")
    @PostMapping("/add")
    @ApiPermission(value = "tms:terminal log:add")
    public Result<TerminalLogResponse> saveTerminalLog(@Valid @RequestBody  TerminalLogsRequest terminalLogsRequest, BindingResult bindingResult) {
        Result<TerminalLogResponse> result = new Result<>();
        TerminalLogResponse terminalLogResponse = new TerminalLogResponse();
        String sn = terminalLogsRequest.getTsn() == null ? null:terminalLogsRequest.getTsn().toUpperCase();
        terminalLogResponse.setTsn(sn);
        if (bindingResult.hasErrors()){
            terminalLogResponse.setStatus("Failed:" + bindingResult.getFieldError().getDefaultMessage());
            result.setCode(300);
            result.setMsg(bindingResult.getFieldError().getDefaultMessage());
            result.setData(terminalLogResponse);
            result.setTimestamp(System.currentTimeMillis());
            return result;
        }
        String checkResult = checkLogSeverity(terminalLogsRequest);
        if(null != checkResult){
            terminalLogResponse.setStatus("Failed:" + checkResult);
            result.setCode(300);
            result.setMsg(checkResult);
            result.setData(terminalLogResponse);
            result.setTimestamp(System.currentTimeMillis());
            return result;
        }
        BaseForm command = new BaseForm();
        boolean checkT = terminalService.checkTerminalInGroup(sn, command.getLoginUserId());
        if(!checkT){
            terminalLogResponse.setStatus("Failed:" + this.getMessage("tsn.notFound",new String[] {sn}));
            result.setCode(300);
            result.setMsg(this.getMessage("tsn.notFound",new String[] {sn}));
            result.setData(terminalLogResponse);
            result.setTimestamp(System.currentTimeMillis());
            return result;
        }
        if(StringUtils.isNotBlank(config.getTerminalLogPPM()) && StringUtils.equalsIgnoreCase(config.getTerminalLogPPM(), "true")){
            TerminalLog terminalLog = new TerminalLog();
            terminalLog.setTrmId(sn);
            terminalLog.setDeviceType(terminalLogsRequest.getTerminalType());
            terminalLog.setSource(terminalLogsRequest.getSource());
            terminalLog.setSourceVersion(terminalLogsRequest.getSourceversion());
            terminalLog.setSeverity(terminalLogsRequest.getSeverity());
            terminalLog.setMessage(terminalLogsRequest.getMessage());
            terminalLog.setEventLocalTime(terminalLogsRequest.getTime());
            terminalLog.setCreateDate(new Date());
            terminalLogService.saveTerminalLog(terminalLog);
        }
        if(StringUtils.isNotBlank(config.getTerminalLogSplunk()) && StringUtils.equalsIgnoreCase(config.getTerminalLogSplunk(), "true")){
            TerminalLogSplunk terminalLogSplunk = new TerminalLogSplunk();
            terminalLogSplunk.setSource(terminalLogsRequest.getSource());
            terminalLogSplunk.setSourcetype("PAXLog");
            Event event = new Event();
            event.setDeviceType(terminalLogsRequest.getTerminalType());
            event.setDeviceSerialNumber(sn);
            event.setSource(terminalLogsRequest.getSource());
            event.setSourceversion(terminalLogsRequest.getSourceversion());
            event.setMessage(terminalLogsRequest.getMessage());
            event.setSeverity(terminalLogsRequest.getSeverity());
            event.setTime(terminalLogsRequest.getTime());
            terminalLogSplunk.setEvent(event);
            String reqJson = Json.encode(terminalLogSplunk);
            terminalLogService.sendTerminalLogToSplunk(config.getTerminalLogSplunkUrl(), reqJson, config.getTerminalLogSplunkToken());
         }
        
        terminalLogResponse.setStatus("Success");
        result.success(terminalLogResponse);
        return result;
    }
    
    private String checkLogSeverity(TerminalLogsRequest terminalLogsRequest){
        String logLevel = terminalLogService.getTerminalLogLevel();
        if(null == logLevel){
            return null;
        }
        Map<String,Integer> levelMap = new HashMap<>();
        levelMap.put("INFO", 0);
        levelMap.put("WARNING", 1);
        levelMap.put("ERROR", 2);
        levelMap.put("DEBUG", 3);
        String terminalLogSeverity = terminalLogsRequest.getSeverity().toUpperCase();
        if(StringUtils.equalsIgnoreCase("OFF", logLevel)){
            return "No level logs shall be accepted.";
        }else if(StringUtils.equalsIgnoreCase("INFO", logLevel) && (!levelMap.containsKey(terminalLogSeverity) || levelMap.get(terminalLogSeverity) > 0 )){
            return "Only INFO logs shall be accepted.";
        } else if(StringUtils.equalsIgnoreCase("WARNING", logLevel) && (!levelMap.containsKey(terminalLogSeverity) || levelMap.get(terminalLogSeverity) > 1)){
            return "Only INFO and WARNING logs shall be accepted.";
        }else if(StringUtils.equalsIgnoreCase("ERROR", logLevel) && (!levelMap.containsKey(terminalLogSeverity) || levelMap.get(terminalLogSeverity) > 2)){
            return "Only INFO,WARNING,ERROR logs shall be accepted.";
        }else if(StringUtils.equalsIgnoreCase("DEBUG", logLevel) && !levelMap.containsKey(terminalLogSeverity)){
            return "Only INFO, WARNING,ERROR,DEBUG logs shall be accepted.";
        }
        return null;
    }

}
