package com.pax.tms.report.service;

import java.util.List;
import java.util.Map;

import com.pax.common.pagination.Page;
import com.pax.common.service.IBaseService;
import com.pax.tms.report.web.form.QueryBillingForm;
import com.pax.tms.terminal.model.TerminalStatus;


public interface BillingService extends IBaseService<TerminalStatus, Long> {
	//获取Billing列表
	Page<Map<String, Object>> getBillingList(QueryBillingForm command);
	//通过执行定时任务统计billing
	void getCountBillingListTask();
	
	List<TerminalStatus> getBillingTerminalStatusList(String month, Long groupId);
	
	void sendEmailTaskTailedToSizeAdmin();
}
