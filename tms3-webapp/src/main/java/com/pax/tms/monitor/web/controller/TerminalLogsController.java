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
 * 2019年12月19日 上午9:29:29           liming                   TerminalLogsController
 * ============================================================================
 */
package com.pax.tms.monitor.web.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.pax.common.exception.BusinessException;
import com.pax.common.pagination.Page;
import com.pax.common.web.controller.BaseController;
import com.pax.common.web.form.BaseForm;
import com.pax.tms.group.model.Group;
import com.pax.tms.group.service.GroupService;
import com.pax.tms.monitor.model.TerminalLog;
import com.pax.tms.monitor.service.TerminalLogService;
import com.pax.tms.monitor.web.form.TerminalLogFrom;
import com.pax.tms.user.security.AclManager;

@Controller
@RequestMapping("/terminal/log")
public class TerminalLogsController extends BaseController  {
    
    @Autowired
    private GroupService groupService;
    @Autowired
    private TerminalLogService terminalLogService;
    
    private static final String LOG_LIST_URL = "/terminal/log/";
    //Excel导入数据范围
    private static final long EXCEL_SIZE= 65535;

    @RequiresPermissions(value = "tms:terminal log:view")
    @RequestMapping("/{groupId}")
    public ModelAndView getTerminalLogList(@PathVariable Long groupId, BaseForm command) {
        ModelAndView mv = new ModelAndView("terminal/log");
        groupService.checkPermissionOfGroup(command, groupId);
        Map<String, String> levelMap = new LinkedHashMap<>();
        levelMap.put("ALL", "ALL");
        levelMap.put("DEBUG", "DEBUG");
        levelMap.put("ERROR", "ERROR");
        levelMap.put("WARNING", "WARNING");
        levelMap.put("INFO", "INFO");
        levelMap.put("OFF", "OFF");
        mv.addObject("group", groupService.get(groupId));
        mv.addObject("activeUrl", LOG_LIST_URL);
        mv.addObject("title", TerminalLog.TITLE);
        mv.addObject("logLevel", terminalLogService.getTerminalLogLevel());
        mv.addObject("levelMap", levelMap);
        return mv;
    }

    /***
     * service
     * 
     * @throws ParseException
     ***/

    @ResponseBody
    @RequiresPermissions(value = "tms:terminal log:view")
    @RequestMapping("/list/{groupId}")
    public Page<Map<String, Object>> getTerminalLogListJSON(@PathVariable Long groupId, TerminalLogFrom command) {
        command.setGroupId(groupId);
        return terminalLogService.page(command);
    }
    
    @RequiresPermissions(value = { "tms:terminal log:view" })
    @RequestMapping(value = "/details/{groupId}/{tsn}")
    public ModelAndView details(@PathVariable Long groupId, @PathVariable Long id, BaseForm command) {
        ModelAndView mav = new ModelAndView("terminal/logDetail");
        if (id == null || id == 0) {
            throw new BusinessException("tsn.Required");
        }
        if (groupId == null) {
            throw new BusinessException("msg.group.groupRequired");
        }
        Group group = groupService.get(groupId);
        if (group == null) {
            throw new BusinessException("msg.group.groupNotFound");
        }
        AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
        TerminalLog terminalLog = terminalLogService.get(id);
        if (terminalLog == null) {
            throw new BusinessException("tsn.notFound", new String[] { id+"" });
        }
        mav.addObject("terminalLog", terminalLog);
        return mav;
    }
    
    /**
     * 先判断条数再下载Excel
     * @param command
     * @param groupId
     * @param response
     * @return
     * @throws IOException
     */
    @RequiresPermissions(value = { "tms:terminal log:view" })
    @RequestMapping(value = "/isexport/{groupId}", method = { RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    public Map<String, Object> isExport(TerminalLogFrom command, @PathVariable Long groupId, HttpServletResponse response){
        Group group = groupService.get(groupId);
        if (group == null) {
            throw new BusinessException("msg.group.groupNotFound");
        }
        AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
        command.setGroupId(groupId);
        long qureySize = terminalLogService.count(command);
        if(EXCEL_SIZE < qureySize) {
            return ajaxDoneError(this.getMessage("msg.event.tooManyRecords"));
        }
        return ajaxDoneSuccess();
    }
    
    /**
     * 导出event list
     * @param 
     * @return
     * @throws IOException 
     **/
    @RequiresPermissions(value = { "tms:terminal log:view" })
    @RequestMapping(value = "/export/{groupId}", method = { RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    public void export(TerminalLogFrom command, @PathVariable Long groupId, HttpServletRequest request, HttpServletResponse response) {
        Group group = groupService.get(groupId);
        if (group == null) {
            throw new BusinessException("msg.group.groupNotFound");
        }
        AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
        command.setGroupId(groupId);
        List<Map<String, Object>> list = terminalLogService.list(command);
        terminalLogService.export(command, list,request, response);
    }
    
    /**
     * 删除terminalLog
     * @param groupId
     * @param response
     * @return
     * @throws IOException
     */
    @RequiresPermissions(value = { "tms:terminal log:delete" })
    @RequestMapping(value = "/delete/{groupId}", method = { RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    public Map<String, Object> delete(TerminalLogFrom command, @PathVariable Long groupId, HttpServletResponse response){
        Group group = groupService.get(groupId);
        if (group == null) {
            throw new BusinessException("msg.group.groupNotFound");
        }
        AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
        command.setGroupId(groupId);
        terminalLogService.delete(groupId);
        return ajaxDoneSuccess();
    }
    
    @RequiresPermissions(value = { "tms:terminal log:level" })
    @RequestMapping(value = "/level", method = { RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    public Map<String, Object> setTerminalLogLevel(@Param(value="terminalLogLevel") String terminalLogLevel){
        terminalLogService.setTerminalLogLevel(terminalLogLevel);
        return ajaxDoneSuccess();
    }
}
