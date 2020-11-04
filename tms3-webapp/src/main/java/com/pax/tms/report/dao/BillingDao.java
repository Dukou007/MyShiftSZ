package com.pax.tms.report.dao;

import java.util.List;

import com.pax.common.dao.IBaseDao;
import com.pax.tms.report.domain.BillingTerminalStatus;
import com.pax.tms.terminal.model.TerminalStatus;
/**
 * BillingDao是底层统计层
 * @author zengpeng
 *
 */
public interface BillingDao extends IBaseDao<TerminalStatus, Long> {
	
	//删除TmsttrmStatusTemp数据
	void truncateTmsttrmStatusTemp();
	//初始化数据
	void initTmsttrmStatusTemp();
	
	Long getBillingMonthCount(String beginTime, String endTime, Long groupId);
	
	List<BillingTerminalStatus> getBillingTerminalStatusTsnList(String beginTime, String endTime, Long groupId);
}
