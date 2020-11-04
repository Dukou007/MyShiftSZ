package com.pax.tms.report.dao;


import java.util.List;

import org.springframework.stereotype.Repository;

import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.tms.report.model.TerminalBilling;

@Repository("terminalBillingImpl")
public class TerminalBillingDaoImpl extends BaseHibernateDao<TerminalBilling, Long> implements TerminalBillingDao {

	@Override
	public void batchInsert(List<TerminalBilling> tbs) {
		String sql = "insert into TMSTTRM_BILLING (BILLING_ID,BILLING_GROUP_ID,BILLING_MONTH,BILLING_CONNECTED_DEVICES,BILLING_STATEMENT) values (?,?,?,?,?)";
		doBatchInsert(sql, TerminalBilling.ID_SEQUENCE_NAME, tbs, (st,tb,id) -> {
			st.setLong(1, id);
			st.setLong(2, tb.getGroupId());
			st.setString(3, tb.getMonth());
			st.setLong(4, tb.getConnectedDevices());
			st.setString(5, tb.getStatement());
		});
		
	}

	@Override
	public List<TerminalBilling> getTerminalBillingListByGroupId(Long groupId) {
		String hql = "from TerminalBilling tb where tb.groupId =:groupId ORDER BY tb.createTime DESC";
		List<TerminalBilling> tbs = createQuery(hql,TerminalBilling.class).setParameter("groupId", groupId).setMaxResults(13).getResultList();
		return tbs;
	}


}
