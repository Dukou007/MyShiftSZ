package com.pax.tms.report.dao;


import java.sql.Timestamp;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.tms.report.model.TerminalBillingDetail;
import com.pax.tms.user.security.UTCTime;

@Repository("terminalBillingDetailDaoImpl")
public class TerminalBillingDetailDaoImpl extends BaseHibernateDao<TerminalBillingDetail, Long> implements TerminalBillingDetailDao {

	@Override
	public void batchInsert(List<TerminalBillingDetail> tbds) {
		String sql = "insert into TMSTTRM_BILLING_DETAIL (BILLING_DETAIL_ID,BILLING_DETAIL_GROUP_ID,BILLING_DETAIL_GROUP_NAME,BILLING_DETAIL_MONTH,"
				+ "BILLING_DETAIL_TRM_SN,BILLING_DETAIL_TRM_TYPE,LAST_ACCESS_TIME,CREATE_TIME) values (?,?,?,?,?,?,?,?)";
		doBatchInsert(sql, TerminalBillingDetail.ID_SEQUENCE_NAME, tbds, (st,tbd,id) ->{
			st.setLong(1, id);
			st.setLong(2, tbd.getGroupId());
			st.setString(3, tbd.getGroupName());
			st.setString(4, tbd.getMonth());
			st.setString(5, tbd.getTsn());
			st.setString(6, tbd.getType());
			st.setTimestamp(7, new Timestamp(tbd.getLastAccessTime().getTime()), UTCTime.UTC_CLENDAR);
			st.setTimestamp(8, new Timestamp(tbd.getCreateTime().getTime()), UTCTime.UTC_CLENDAR);
		});
	}

	@Override
	public List<TerminalBillingDetail> getDetailByGroupId(Long groupId, String month) {
		String hql = "from TerminalBillingDetail tbd where tbd.groupId =:groupId AND tbd.month =:month";
		List<TerminalBillingDetail> tbds = createQuery(hql, TerminalBillingDetail.class)
											.setParameter("groupId", groupId)
											.setParameter("month", month).getResultList();
		return tbds;
	}


}
