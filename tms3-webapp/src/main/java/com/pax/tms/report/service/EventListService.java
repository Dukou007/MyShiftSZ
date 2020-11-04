package com.pax.tms.report.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PathVariable;

import com.pax.common.service.IBaseService;
import com.pax.tms.monitor.model.EventTrm;
import com.pax.tms.monitor.web.form.QueryTerminalEventForm;

public interface EventListService extends IBaseService<EventTrm, Long> {
	//导出接口
	void export(QueryTerminalEventForm command, Long groupId, String searchType, @PathVariable int days, List<Map<String, Object>> list, HttpServletResponse response);
	//获取数据
	public List<Map<String, Object>> listAllEvents(QueryTerminalEventForm command, Long groupId, String searchType, int days);
}
