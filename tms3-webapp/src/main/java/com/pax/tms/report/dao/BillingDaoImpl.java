package com.pax.tms.report.dao;

import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.hibernate.query.NativeQuery;
import org.hibernate.transform.ResultTransformer;
import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.common.dao.hibernate.MyAliasToBeanResultTransformer;
import com.pax.common.util.DateTimeUtils;
import com.pax.tms.report.domain.BillingTerminalStatus;
import com.pax.tms.res.model.Model;
import com.pax.tms.terminal.model.TerminalStatus;

@Repository("billingDaoImpl")
public class BillingDaoImpl extends BaseHibernateDao<TerminalStatus, Long> implements BillingDao {
	
	private ResultTransformer billingRT = new MyAliasToBeanResultTransformer(BillingTerminalStatus.class);
	/**
	 * Billin的统计有三种情况，分为
	 *  1）当月出现过online过的终端
		2）当月没有online但是有offline的终端
		3）当月没有online和offline但一直在线的终端
		所以需要三种SQL语句去分别统计
	 */
	
	@Override
	public void truncateTmsttrmStatusTemp() {
		String sql = "truncate table tmsttrmstatus_temp";
		createNativeQuery(sql).executeUpdate();
	}
	
	@Override
	public void initTmsttrmStatusTemp() {
		String sql = "INSERT INTO tmsttrmstatus_temp SELECT * FROM tmsttrmstatus";
		createNativeQuery(sql).executeUpdate();
	}
	
	@Override
	public Long getBillingMonthCount(String beginTime, String endTime, Long groupId) {
		//情况一 当月online过的终端
		String sql = "SELECT COUNT(1) FROM TerminalStatusTemp ts "+
				"WHERE ts.tsn IN (SELECT tg.terminal.tsn FROM TerminalGroup tg WHERE tg.group.id=:groupId) "+
				" AND ((ts.onlineSince > ts.offlineSince AND ts.onlineSince <:endTime) "+
				" OR ts.offlineSince IS NULL)";
		//情况二 当月没有online但是有offline
		String sql2 = "SELECT COUNT(1) FROM TerminalStatusTemp ts "+
				"WHERE ts.tsn IN (SELECT tg.terminal.tsn FROM TerminalGroup tg WHERE tg.group.id=:groupId) "+
				"AND ts.onlineSince < ts.offlineSince "+
				"AND (ts.offlineSince BETWEEN :beginTime AND :endTime)";
		Date beginDate = null;
		Date endDate = null;
        try {
            beginDate = DateTimeUtils.string2Date(beginTime, DateTimeUtils.PATTERN_STANDARD);
            endDate = DateTimeUtils.string2Date(endTime, DateTimeUtils.PATTERN_STANDARD);
        } catch (ParseException e) {
            e.printStackTrace();
        }
		Long r1 = createQuery(sql, Long.class).setParameter("groupId", groupId)
				  .setParameter("endTime", endDate).getSingleResult().longValue();
		Long r2 = createQuery(sql2, Long.class).setParameter("groupId", groupId)
				  .setParameter("beginTime", beginDate)
				  .setParameter("endTime", endDate).getSingleResult().longValue();
		Long result = r1+r2;
		return result;
	}

	@Override
	public List<BillingTerminalStatus> getBillingTerminalStatusTsnList(String beginTime, String endTime, Long groupId) {
		//情况一
		String sql = "SELECT ts.TRM_SN,ts.MODEL_ID,ts.LAST_CONN_TIME,ts.ONLINE_SINCE,ts.OFFLINE_SINCE FROM tmsttrmstatus_temp ts "+
				"WHERE ts.TRM_SN IN (SELECT tg.TRM_ID FROM tmsttrm_group tg WHERE tg.GROUP_ID=:groupId) "+
				" AND ((ts.ONLINE_SINCE > ts.OFFLINE_SINCE AND ts.ONLINE_SINCE <:endTime) "+
                " OR ts.OFFLINE_SINCE IS NULL)";
		//情况二
		String sql2 = "SELECT ts.TRM_SN,ts.MODEL_ID,ts.LAST_CONN_TIME,ts.ONLINE_SINCE,ts.OFFLINE_SINCE FROM tmsttrmstatus_temp ts "+
				"WHERE ts.TRM_SN IN (SELECT tg.TRM_ID FROM tmsttrm_group tg WHERE tg.GROUP_ID=:groupId) "+
				"AND ts.ONLINE_SINCE < ts.OFFLINE_SINCE "+
				"AND (ts.OFFLINE_SINCE BETWEEN :beginTime AND :endTime)";
		NativeQuery<BillingTerminalStatus>  q1 = createNativeQuery(sql,BillingTerminalStatus.class).setParameter("groupId", groupId)
				  .setParameter("endTime", endTime);
		super.setResultTransformer(q1, billingRT);
		List<BillingTerminalStatus> bts1 = q1.getResultList();
		
		NativeQuery<BillingTerminalStatus> q2 = createNativeQuery(sql2,BillingTerminalStatus.class).setParameter("groupId", groupId)
				  .setParameter("beginTime", beginTime)
				  .setParameter("endTime", endTime);
		super.setResultTransformer(q2, billingRT);
		List<BillingTerminalStatus> bts2 = q2.getResultList();
		
		List<BillingTerminalStatus> bts4 = new LinkedList<BillingTerminalStatus>();
		bts4.addAll(bts1);
		bts4.addAll(bts2);
		return bts4;
	}
	
	public List<TerminalStatus> objectToTsList(List<Object> objs) {
		List<TerminalStatus> list = new LinkedList<>();
		for(Object obj:objs) {
			Map<String, Object> m = JSONObject.parseObject(JSON.toJSONString(obj));
			TerminalStatus ts = new TerminalStatus();
			ts.setTsn(m.get("TRM_SN").toString());
			ts.setModel((Model)m.get("MODEL_ID"));
			list.add(ts);
		}
		return list;
	}


}
