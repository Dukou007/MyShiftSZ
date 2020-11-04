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
 * 2019年12月19日 上午11:16:54           liming                   TerminalLogServiceImpl
 * ============================================================================
 */
package com.pax.tms.monitor.service;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.pax.common.dao.IBaseDao;
import com.pax.common.service.impl.BaseService;
import com.pax.common.util.CSVWritter;
import com.pax.common.util.HttpClientUtil;
import com.pax.tms.monitor.dao.TerminalLogDao;
import com.pax.tms.monitor.model.TerminalLog;
import com.pax.tms.monitor.web.form.TerminalLogFrom;
import com.pax.tms.pub.dao.SystemParameterDao;
import com.pax.tms.pub.model.SystemParameter;

@Service("terminalLogService")
public class TerminalLogServiceImpl extends BaseService<TerminalLog, Long> implements TerminalLogService {
    private static final Logger logger = LoggerFactory.getLogger(TerminalLogServiceImpl.class);
    @Autowired
    private TerminalLogDao terminalLogDao;
    
    @Autowired
    private SystemParameterDao systemParameterDao;
    
    @Autowired(required = false)
    @Qualifier("messageSource")
    protected ReloadableResourceBundleMessageSource messageSource;

    @Override
    public IBaseDao<TerminalLog, Long> getBaseDao() {
        return terminalLogDao;
    }
    
    @Override
    public void saveTerminalLog(TerminalLog terminalLog){
        terminalLogDao.save(terminalLog);
    }
    
    @Override
    public void export(TerminalLogFrom command, List<Map<String, Object>> list, HttpServletRequest request,
            HttpServletResponse response) {
        String caption = this.getMessage("title.export.terminallog") + "_"
                + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String filename = caption + ".csv";
        List<List<Object>> csvContent = new LinkedList<>();
        List<Object> head = new LinkedList<>();
        head.add("Terminal Type");
        head.add("Terminal SN/Device Name");
        head.add("Source");
        head.add("Source Version");
        head.add("Source Type");
        head.add("Severity");
        head.add("Message");
        head.add("Event Local Time");
        csvContent.add(head);
        for (Map<String, Object> map : list) {
            List<Object> content = new LinkedList<>();
            content.add(map.get("deviceType"));
            String sn = (String) map.get("trmId");
            if(StringUtils.isNoneBlank(sn)){
                content.add(map.get("trmId"));
            }else {
                content.add(map.get("deviceName"));
            }
            content.add(map.get("source"));
            content.add(map.get("sourceVersion"));
            content.add(map.get("sourceType"));
            content.add(map.get("severity"));
            content.add(map.get("message"));
            content.add(map.get("eventLocalTime"));
            csvContent.add(content);
        }
        // 文件导出
        try {
            OutputStream os = response.getOutputStream();
            CSVWritter.responseSetProperties(filename, response);
            CSVWritter.doExport(csvContent, os);
            os.close();
        } catch (Exception e) {
            logger.error("导出失败", e.getMessage());
        }
    }
    
    @Override
    public void delete(Long groupId){
        terminalLogDao.deleteTerminalLogs(groupId);
    }
    
    @Async
    @Override
    public void sendTerminalLogToSplunk(String splunkUrl, String content,String splunkToken){
        HttpClientUtil.doPostAuth(splunkUrl, content, splunkToken);
    }
    
    @Override
    public void setTerminalLogLevel(String loggingLevel){
        String paraKey = "TerminalLogLevel";
        String category = "logSetting ";
        SystemParameter systemParameter = systemParameterDao.findByParaKey(paraKey);
        if(null == systemParameter){
            systemParameter = new SystemParameter();
            systemParameter.setCategory(category);
            systemParameter.setKey(paraKey);
            systemParameter.setValue(loggingLevel);
            systemParameterDao.save(systemParameter);
        }else{
            systemParameter.setValue(loggingLevel);
            systemParameterDao.update(systemParameter);
        }
        
    }
    
    @Override
    public String getTerminalLogLevel(){
        String paraKey = "TerminalLogLevel";
        SystemParameter systemParameter = systemParameterDao.findByParaKey(paraKey);
        if(null == systemParameter){
           return "ALL";
        }else{
            return systemParameter.getValue();
        }
    }
    
    private String getMessage(String code) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
        Locale locale = localeResolver.resolveLocale(request);
        return messageSource.getMessage(code, new Object[] {}, locale);
    }
}
