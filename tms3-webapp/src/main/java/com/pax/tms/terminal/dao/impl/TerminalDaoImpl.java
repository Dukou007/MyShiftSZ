/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description: // Detail description about the function of this module,		
 *             // interfaces with the other modules, and dependencies. 		
 * Revision History:		
 * Date	                 Author	                Action
 * 20170112	             Jaden           	    Modify
 * ============================================================================		
 */
package com.pax.tms.terminal.dao.impl;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.NativeQuery;
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.pax.common.dao.DbHelper;
import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.common.dao.hibernate.CriteriaWrapper;
import com.pax.common.util.DateTimeUtils;
import com.pax.common.web.form.BaseForm;
import com.pax.tms.deploy.model.DownOrActvStatus;
import com.pax.tms.group.model.Group;
import com.pax.tms.monitor.web.AlertConstants;
import com.pax.tms.terminal.dao.TerminalDao;
import com.pax.tms.terminal.model.Terminal;
import com.pax.tms.terminal.model.TerminalGroup;
import com.pax.tms.terminal.web.form.QueryTerminalForm;
import com.pax.tms.user.security.UTCTime;

@Repository("terminalDaoImpl")
public class TerminalDaoImpl extends BaseHibernateDao<Terminal, String> implements TerminalDao {

	@Override
	protected <S extends Serializable> CriteriaWrapper getCriteria(S command, boolean ordered) {
		QueryTerminalForm form = (QueryTerminalForm) command;
		Assert.notNull(form.getGroupId());

		CriteriaWrapper wrapper = createCriteriaWrapper(Terminal.class, "trm");
		wrapper.createAlias("trm.model", "model");
		wrapper.createAlias("trm.terminalStatus", "ts", JoinType.LEFT_OUTER_JOIN);
		wrapper.setProjection(
				Arrays.asList("tsn", "status", "model.name", "ts.isOnline", "ts.onlineSince", "ts.offlineSince",
						"ts.lastSourceIP", "installApps", "reportTime", "sysmetricKeys", "keyReportTime", "localTime"));

		DetachedCriteria permissionCriteria = wrapper.subquery(TerminalGroup.class, "tg", "trm.tid", "tg.terminal.tid");

		permissionCriteria.add(Restrictions.eq("tg.group.id", form.getGroupId()));
		permissionCriteria.setProjection(Projections.property("tg.id"));

		if (StringUtils.isNotEmpty(form.getTsn())) {
			wrapper.eq("trm.tsn", form.getTsn());
		}

		if (StringUtils.isNotEmpty(form.getItemName())) {
			switch (form.getItemName()) {
				case AlertConstants.TAMPERS:
					this.doTampers(form.getItemStatus(), wrapper);
					break;
				case AlertConstants.OFFLINE:
					this.doOffline(form.getItemStatus(), wrapper);
					break;
				case AlertConstants.PRIVACY_SHIELD:
					this.doPrivacyShield(form.getItemStatus(), wrapper);
					break;
				case AlertConstants.STYLUS:
					this.doStylus(form.getItemStatus(), wrapper);
					break;
				case AlertConstants.DOWNLOADS:
					this.doDownloads(form.getItemStatus(), wrapper);
					break;
				case AlertConstants.ACTIVATIONS:
					this.doActivations(form.getItemStatus(), wrapper);
					break;
				case AlertConstants.SRED:
                    this.doSred(form.getItemStatus(), wrapper);
                    break;
				case AlertConstants.RKI:
                    this.doRki(form.getItemStatus(), wrapper);
                    break;
				default:
					break;
			}
		}
		wrapper.exists(permissionCriteria);
		if (StringUtils.isNotEmpty(form.getFuzzyCondition())) {
			wrapper.fuzzy(form.getFuzzyCondition(), "tid", "tsn", "model.name");
		}

		if (StringUtils.isNotEmpty(form.getDestModel())) {
			wrapper.eq("model.id", form.getDestModel());
		}

		if (ordered) {
			if (AlertConstants.OFFLINE.equals(form.getItemName()) && "2".equals(form.getItemStatus())) {
				wrapper.addOrder(form, "ts.onlineSince", ORDER_DESC);
			} else if (AlertConstants.OFFLINE.equals(form.getItemName()) && "1".equals(form.getItemStatus())) {
				wrapper.addOrder(form, "ts.offlineSince", ORDER_DESC);
			} else {
				wrapper.addOrder(form, "modifyDate", ORDER_DESC);
			}
			wrapper.addOrder("tid", ORDER_ASC);
		}
		return wrapper;
	}

	private void doTampers(String itemStatus, CriteriaWrapper wrapper) {
		if ("3".equals(itemStatus)) {
			wrapper.getCriteria().add(Restrictions.isNull("ts.tamper"));
		} else if ("2".equals(itemStatus)) {
			wrapper.eq("ts.tamper", "0000");
		} else if ("1".equals(itemStatus)) {
			wrapper.getCriteria().add(Restrictions.isNotNull("ts.tamper"));
			wrapper.getCriteria().add(Restrictions.ne("ts.tamper", "0000"));
		}
	}

	private void doOffline(String itemStatus, CriteriaWrapper wrapper) {
		if ("2".equals(itemStatus)) {
			wrapper.eq("ts.isOnline", 1);
		} else if ("1".equals(itemStatus)) {
			Disjunction dis = Restrictions.disjunction();
			dis.add(Restrictions.ne("ts.isOnline", 1));
			dis.add(Restrictions.isNull("ts.isOnline"));

			wrapper.addCriterion(dis);
		}
	}

	private void doPrivacyShield(String itemStatus, CriteriaWrapper wrapper) {
		if ("3".equals(itemStatus)) {
			wrapper.getCriteria().add(Restrictions.or(Restrictions.isNull("ts.privacyShield"),
					Restrictions.or(Restrictions.in("ts.privacyShield", 3,0))));
		} else if ("2".equals(itemStatus)) {
			wrapper.eq("ts.privacyShield", 1);
		} else if ("1".equals(itemStatus)) {
			wrapper.eq("ts.privacyShield", 2);
		}
	}

	private void doSred(String itemStatus, CriteriaWrapper wrapper) {
        if ("3".equals(itemStatus)) {
            wrapper.getCriteria().add(Restrictions.or(Restrictions.isNull("ts.sred"),
                    Restrictions.or(Restrictions.in("ts.sred", 3))));
        } else if ("2".equals(itemStatus)) {
            wrapper.eq("ts.sred", 1);
        } else if ("1".equals(itemStatus)) {
            wrapper.eq("ts.sred", 0);
        }
    }
	
	private void doRki(String itemStatus, CriteriaWrapper wrapper) {
        if ("3".equals(itemStatus)) {
            wrapper.getCriteria().add(Restrictions.or(Restrictions.isNull("ts.rki"),
                    Restrictions.or(Restrictions.eq("ts.rki", 3))));
        } else if ("2".equals(itemStatus)) {
            wrapper.eq("ts.rki", 1);
        } else if ("1".equals(itemStatus)) {
            wrapper.eq("ts.rki", 0);
        }
    }
	
	private void doStylus(String itemStatus, CriteriaWrapper wrapper) {
		if ("3".equals(itemStatus)) {
			wrapper.getCriteria().add(Restrictions.or(Restrictions.isNull("ts.stylus"),
					Restrictions.or(Restrictions.in("ts.stylus", 3,0))));
		} else if ("2".equals(itemStatus)) {
			wrapper.eq("ts.stylus", 1);
		} else if ("1".equals(itemStatus)) {
			wrapper.eq("ts.stylus", 2);
		}
	}

	private void doDownloads(String itemStatus, CriteriaWrapper wrapper) {
		Date dayStart = DateTimeUtils.getDayStart(new Date());
		wrapper.createAlias("trm.terminalDeploys", "td", JoinType.LEFT_OUTER_JOIN);
		if ("3".equals(itemStatus)) {
			Disjunction dis = Restrictions.disjunction();
			dis.add(Restrictions.eq("td.downStatus", DownOrActvStatus.PENDING));
			dis.add(Restrictions.eq("td.downStatus", DownOrActvStatus.DOWNLOADING));
			wrapper.addCriterion(dis);
		} else if ("2".equals(itemStatus)) {
			Disjunction dis = Restrictions.disjunction();
			dis.add(Restrictions.eq("td.downStatus", DownOrActvStatus.CANCELED));
			dis.add(Restrictions.eq("td.downStatus", DownOrActvStatus.SUCCESS));

			wrapper.addCriterion(dis);
			wrapper.ge("td.dwnlTime", dayStart);
		} else if ("1".equals(itemStatus)) {
			wrapper.eq("td.downStatus", DownOrActvStatus.FAILED);
			wrapper.ge("td.dwnlTime", dayStart);
		}
	}

	private void doActivations(String itemStatus, CriteriaWrapper wrapper) {
		Date dayStart1 = DateTimeUtils.getDayStart(new Date());
		wrapper.createAlias("trm.terminalDeploys", "td", JoinType.LEFT_OUTER_JOIN);
		if ("3".equals(itemStatus)) {
			Disjunction dis = Restrictions.disjunction();
			dis.add(Restrictions.eq("td.actvStatus", DownOrActvStatus.PENDING));
			dis.add(Restrictions.eq("td.actvStatus", DownOrActvStatus.DOWNLOADING));
			wrapper.addCriterion(dis);
		} else if ("2".equals(itemStatus)) {
			Disjunction dis = Restrictions.disjunction();
			dis.add(Restrictions.eq("td.actvStatus", DownOrActvStatus.CANCELED));
			dis.add(Restrictions.eq("td.actvStatus", DownOrActvStatus.SUCCESS));

			wrapper.addCriterion(dis);
			wrapper.ge("td.actvTime", dayStart1);
		} else if ("1".equals(itemStatus)) {
			wrapper.eq("td.actvStatus", DownOrActvStatus.FAILED);
			wrapper.ge("td.actvTime", dayStart1);
		}
	}

	@Override
	public Object[] getExistedAndNotAcceptableTsns(Set<String> tsns, long userId) {
		long batchId = DbHelper.generateId("TMSPTSNS_BATCH_ID", 1);
		Object[] arr = new Object[2];
		try {
			addTemporaryTsns(batchId, tsns);
			arr[0] = getTsnGroupsByBatch(batchId);
			arr[1] = getNotAcceptableTsnsByBatch(batchId, userId);
		} finally {
			deleteTemporaryTsns(batchId);
		}
		return arr;
	}

	public Map<String, List<Long>> getTsnGroupsByBatch(long batchId) {
		String hql = "select tg.terminal.tid, tg.group.id from TerminalGroup tg, TMSPTSNS p where tg.terminal.tid=p.tsn and p.batchId=:batchId order by tg.terminal.tid";
		List<Object[]> results = createQuery(hql, Object[].class).setParameter("batchId", batchId).getResultList();

		Map<String, List<Long>> map = new HashMap<String, List<Long>>();
		String tsn;
		Long groupId;
		List<Long> values;
		for (Object[] item : results) {
			tsn = (String) item[0];
			groupId = (Long) item[1];
			values = map.get(tsn);
			if (values == null) {
				values = new ArrayList<Long>();
				map.put(tsn, values);
			}
			values.add(groupId);
		}
		return map;
	}

	public List<String> getNotAcceptableTsnsByBatch(long batchId, long userId) {
		String hql = "select t.tid from Terminal t, TMSPTSNS p where t.tid=p.tsn and p.batchId=:batchId and"
				+ " not exists (select tg.group.id from TerminalGroup tg, UserGroup ug where"
				+ "  ug.group.id=tg.group.id and ug.user.id=:userId and t.tid=tg.terminal.tid)";

		return createQuery(hql, String.class).setParameter("batchId", batchId).setParameter("userId", userId)
				.getResultList();
	}

	@Override
	public List<String> getNotAcceptableTsns(Long batchId, long userId) {
		String hql = "select t.tid from TMSPTSNS p ,Terminal t where t.tid=p.tsn and p.batchId=:batchId and"
				+ " not exists (select tg.group.id from TerminalGroup tg, UserGroup ug where"
				+ "  ug.group.id=tg.group.id and ug.user.id=:userId and t.tid=tg.terminal.tid)";

		return createQuery(hql, String.class).setParameter("batchId", batchId).setParameter("userId", userId)
				.getResultList();
	}

	@Override
	public void activate(Collection<String> tsns, BaseForm form) {
		updateTerminalStatus(tsns, Terminal.STATUS_ENABLE, form);
	}

	@Override
	public void deactivate(Collection<String> tsns, BaseForm form) {
		updateTerminalStatus(tsns, Terminal.STATUS_DISABLE, form);
	}

	public void updateTerminalStatus(Collection<String> tsns, int status, BaseForm form) {
		String sql = "update TMSTTERMINAL set TRM_STATUS=?, MODIFIER=?, MODIFY_DATE=? where TRM_ID=?";

		Timestamp timestamp = new Timestamp(new Date().getTime());
		String username = form.getLoginUsername();

		doBatchExecute(sql, tsns.iterator(), (st, tsn) -> {
			st.setInt(1, status);
			st.setString(2, username);
			st.setTimestamp(3, timestamp, UTCTime.UTC_CLENDAR);
			st.setString(4, tsn);
		});
	}

	@Override
	public void delete(Collection<String> tsns) {
		String sql = "delete from TMSTTERMINAL where TRM_ID=?";
		doBatchExecute(sql, tsns.iterator(), (st, tsn) -> st.setString(1, tsn));
	}

	public void addTemporaryTsns(final long batchId, final Collection<String> tsns) {
		String sql = "insert into TMSPTSNS (BATCH_ID, TSN) values (?,?)";
		doBatchExecute(sql, tsns.iterator(), (st, tsn) -> {
			st.setLong(1, batchId);
			st.setString(2, tsn);
		});
	}

	public void deleteTemporaryTsns(long batchId) {
		String hql = "delete from TMSPTSNS where BATCH_ID=:batchId";
		createNativeQuery(hql).setParameter("batchId", batchId).executeUpdate();
	}

	@Override
	public void addTerminals(Collection<String> tsns, Map<String, Terminal> terminalMap, BaseForm command) {

		if (CollectionUtils.isEmpty(tsns)) {
			return;
		}

		String username = command.getLoginUsername();
		Timestamp timestamp = new Timestamp(command.getRequestTime().getTime());
		String termianlSql = "insert into TMSTTERMINAL (TRM_ID,TRM_SN,COUNTRY,PROVINCE,CITY,"
				+ " MODEL_ID,ADDRESS,TRM_STATUS,ZIP_CODE,TIME_ZONE,DAYLIGHT_SAVING,SYNC_TO_SERVER_TIME,DESCRIPTION,"
				+ "CREATOR,CREATE_DATE,MODIFIER,MODIFY_DATE ) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		doWork(conn -> {
			int count = 0;

			try (PreparedStatement terminalSt = conn.prepareStatement(termianlSql)) {
				for (String tsn : tsns) {

					processTerminal(terminalSt, tsn, terminalMap.get(tsn), username, timestamp);
					terminalSt.addBatch();
					count++;
					if (count % 100 == 0) {

						terminalSt.executeBatch();
						terminalSt.clearBatch();
					}

				}
				if (count % 100 != 0) {

					terminalSt.executeBatch();
					terminalSt.clearBatch();
				}

			}

		});

	}

	private void processTerminal(PreparedStatement terminalSt, String tsn, Terminal source, String username,
			Timestamp timestamp) throws SQLException {
		terminalSt.setString(1, tsn);
		terminalSt.setString(2, tsn);
		terminalSt.setString(3, source.getCountry());
		terminalSt.setString(4, source.getProvince());
		terminalSt.setString(5, source.getCity());
		terminalSt.setString(6, source.getModel().getId());
		terminalSt.setString(7, source.getAddress());
		terminalSt.setBoolean(8, source.isStatus());
		terminalSt.setString(9, source.getZipCode());
		terminalSt.setString(10, source.getTimeZone());
		terminalSt.setBoolean(11, source.isDaylightSaving());
		terminalSt.setBoolean(12, source.isSyncToServerTime());
		terminalSt.setString(13, source.getDescription());
		terminalSt.setString(14, username);
		terminalSt.setTimestamp(15, timestamp, UTCTime.UTC_CLENDAR);
		terminalSt.setString(16, username);
		terminalSt.setTimestamp(17, timestamp, UTCTime.UTC_CLENDAR);

	}

	@Override
	public List<String> getTsnsByGroupId(Long groupId) {
		String hql = "select t.tid from Terminal t where exists (select tg.id from TerminalGroup tg"
				+ " where  tg.terminal.id=t.id and tg.group.id=:groupId)";
		return createQuery(hql, String.class).setParameter("groupId", groupId).getResultList();
	}

	@Override
	public void addTerminalGroup(Collection<String> tsns, Group group, BaseForm command) {
		String username = command.getLoginUsername();
		Timestamp timestamp = new Timestamp(command.getRequestTime().getTime());
		String sql = "insert into TMSTTRM_GROUP (REL_ID,TRM_ID,GROUP_ID,CREATOR,"
				+ " CREATE_DATE,MODIFIER,MODIFY_DATE) values(?,?,?,?,?,?,?)";
		doBatchInsert(sql, TerminalGroup.ID_SEQUENCE_NAME, tsns, (st, tsn, relId) -> {
			st.setLong(1, relId);
			st.setString(2, tsn);
			st.setLong(3, group.getId());
			st.setString(4, username);
			st.setTimestamp(5, timestamp, UTCTime.UTC_CLENDAR);
			st.setString(6, username);
			st.setTimestamp(7, timestamp, UTCTime.UTC_CLENDAR);
		});
	}

	protected int getLength(Map<String, Collection<Long>> tsnAncestorGroupMap) {

		Iterator<Entry<String, Collection<Long>>> it = tsnAncestorGroupMap.entrySet().iterator();
		int count = 0;
		while (it.hasNext()) {
			count += it.next().getValue().size();
		}
		return count;
	}

	@Override
	public void updateTerminals(Terminal source, Collection<String> tsns, BaseForm command) {
		if (CollectionUtils.isEmpty(tsns)) {
			return;
		}
		String username = command.getLoginUsername();
		Timestamp timestamp = new Timestamp(command.getRequestTime().getTime());
		String termianlSql = "update TMSTTERMINAL set COUNTRY=?,PROVINCE=?,CITY=?,"
				+ " MODEL_ID=?,ADDRESS=?,TRM_STATUS=?,ZIP_CODE=?,TIME_ZONE=?,DAYLIGHT_SAVING=?,SYNC_TO_SERVER_TIME=?,"
				+ "DESCRIPTION=?, MODIFIER=?,MODIFY_DATE=? where TRM_ID=?";

		doBatchExecute(termianlSql, tsns.iterator(), (terminalSt, tsn) -> {
			processUpdateTerminal(terminalSt, tsn, source, username, timestamp);

		});
	}

    @SuppressWarnings("unchecked")
	@Override
    public List<String> getTerminalModels(Long groupId) {
	    String hql = "SELECT t.model.id FROM TerminalGroup tg,Terminal t  where tg.group.id=:groupId and tg.terminal.tid = t.tid group by t.model.id";
        return createQuery(hql).setParameter("groupId", groupId).list();
    }

    private void processUpdateTerminal(PreparedStatement terminalSt, String tsn, Terminal source, String username,
			Timestamp timestamp) throws SQLException {

		terminalSt.setString(1, source.getCountry());
		terminalSt.setString(2, source.getProvince());
		terminalSt.setString(3, source.getCity());
		terminalSt.setString(4, source.getModel().getId());
		terminalSt.setString(5, source.getAddress());
		terminalSt.setBoolean(6, source.isStatus());
		terminalSt.setString(7, source.getZipCode());
		terminalSt.setString(8, source.getTimeZone());
		terminalSt.setBoolean(9, source.isDaylightSaving());
		terminalSt.setBoolean(10, source.isSyncToServerTime());
		terminalSt.setString(11, source.getDescription());
		terminalSt.setString(12, username);
		terminalSt.setTimestamp(13, timestamp);
		terminalSt.setString(14, tsn);

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getTerminalGroupList() {
//		String sql = "SELECT T.TRM_SN AS \"tsn\", G.NAME_PATH AS \"groupName\" "
//				+ " FROM PUBTGROUP G "
//				+ " INNER JOIN PUBTGROUP_PARENTS GP ON GP.GROUP_ID=G.GROUP_ID "
//				+ " INNER JOIN TMSTTRM_GROUP TG ON G.GROUP_ID=TG.GROUP_ID "
//				+ " INNER JOIN TMSTTERMINAL T ON TG.TRM_ID=T.TRM_ID "
//				+ "WHERE  GP.PARENT_ID=:groupId";
		String sql = "SELECT T.TRM_SN AS \"tsn\", G.NAME_PATH AS \"groupName\" " + " FROM TMSTTERMINAL T "
				+ " LEFT JOIN TMSTTRM_GROUP TG ON T.TRM_ID = TG.TRM_ID "
				+ " LEFT JOIN PUBTGROUP G ON G.GROUP_ID = TG.GROUP_ID ";
		NativeQuery<Map<String, Object>> query = super.createNativeQuery(sql);
		// query.setParameter("groupId", groupId);
		return super.mapResult(query).getResultList();
	}
}
