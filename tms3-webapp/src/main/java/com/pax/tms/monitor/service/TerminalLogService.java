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
 * 2019年12月19日 上午11:16:22           liming                   TerminalLogService
 * ============================================================================
 */
package com.pax.tms.monitor.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pax.common.service.IBaseService;
import com.pax.tms.monitor.model.TerminalLog;
import com.pax.tms.monitor.web.form.TerminalLogFrom;

public interface TerminalLogService extends IBaseService<TerminalLog, Long>{
    void saveTerminalLog(TerminalLog terminalLog);
    void export(TerminalLogFrom command,List<Map<String, Object>> list, HttpServletRequest request, HttpServletResponse response);
    void delete(Long groupId);
    void sendTerminalLogToSplunk(String splunkUrl, String content,String splunkToken);
    void setTerminalLogLevel(String loggingLevel);
    String getTerminalLogLevel();
}
