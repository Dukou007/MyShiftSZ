package com.pax.tms.report.dao;

import java.util.List;

import com.pax.common.dao.IBaseDao;
import com.pax.tms.report.model.TerminalBillingDetail;
/**
 * TerminalBillingDetailDao层针对详细月份数据表tmsttrm_billing_detail
 * @author zengpeng
 *
 */
public interface TerminalBillingDetailDao extends IBaseDao<TerminalBillingDetail, Long> {
	void batchInsert(List<TerminalBillingDetail> tbds);
	
	List<TerminalBillingDetail> getDetailByGroupId(Long groupId, String month);
}
