package com.pax.tms.report.dao;

import java.util.List;

import com.pax.common.dao.IBaseDao;
import com.pax.tms.report.model.TerminalBilling;
/**
 * TerminalBillingDao是针对总表tmsttrm_billing
 * @author zengpeng
 *
 */
public interface TerminalBillingDao extends IBaseDao<TerminalBilling, Long> {
	void batchInsert(List<TerminalBilling> tbs);
	
	List<TerminalBilling> getTerminalBillingListByGroupId(Long groupId);
}
