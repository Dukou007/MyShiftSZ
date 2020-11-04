package com.pax.tms.report.web.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pax.common.web.controller.BaseController;
import com.pax.tms.monitor.web.form.QueryTerminalEventForm;
import com.pax.tms.report.service.EventListService;

@Controller
@RequestMapping("/report")
public class EventListController extends BaseController {
	
	@Autowired
	private EventListService eventListService;
	
	//Excel导入数据范围
	private static final int EXCEL_SIZE= 65535;
	
	/**
	 * 导出event list
	 * @param 
	 * @return
	 * @throws IOException 
	 **/
	@RequiresPermissions(value = { "tms:event:export" })
	@RequestMapping(value = "/eventlist/service/export/{groupId}", method = { RequestMethod.GET,
			RequestMethod.POST })
	@ResponseBody
	public void export(QueryTerminalEventForm command, @PathVariable Long groupId,
			HttpServletResponse response) throws IOException{
		String searchType = command.getActiveEvent();
		int days = command.getActiveTime();
		command.setGroupId(groupId);
		List<Map<String, Object>> list = eventListService.listAllEvents(command, groupId, searchType, days);
		eventListService.export(command, groupId, searchType, days, list, response);
	}
	
	/**
	 * 先判断条数再下载Excel
	 * @param command
	 * @param groupId
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequiresPermissions(value = { "tms:event:export" })
	@RequestMapping(value = "/eventlist/service/isexport/{groupId}", method = { RequestMethod.GET,
			RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> isExport(QueryTerminalEventForm command, @PathVariable Long groupId,
			HttpServletResponse response) throws IOException{
		String searchType = command.getActiveEvent();
		int days = command.getActiveTime();
		command.setGroupId(groupId);
		List<Map<String, Object>> list = eventListService.listAllEvents(command, groupId, searchType, days);
		if(EXCEL_SIZE < list.size()) {
			return ajaxDoneError(this.getMessage("msg.event.tooManyRecords"));
		}
		return ajaxDoneSuccess();
	}
	
}
