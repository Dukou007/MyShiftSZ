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

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.common.web.form.BaseForm;
import com.pax.tms.terminal.dao.TerminalGroupDao;
import com.pax.tms.terminal.model.TerminalGroup;
import com.pax.tms.user.security.UTCTime;

@Repository("terminalGroupDaoImpl")
public class TerminalGroupDaoImpl extends BaseHibernateDao<TerminalGroup, Long> implements TerminalGroupDao {

	@Override
	public void deleteTerminalGroupCascading(final Collection<String> tsns, final Long groupId) {
		String sql = "delete from TMSTTRM_GROUP where"
				+ " (GROUP_ID in (select GROUP_ID from PUBTGROUP_PARENTS where PARENT_ID=?))and TRM_ID=?";
		doBatchExecute(sql, tsns.iterator(), (st, tsn) -> {
			st.setLong(1, groupId);
			st.setString(2, tsn);
		});
	}

	@Override
	public void insertTerminalGroup(final Collection<String> tsns, final Long groupId, BaseForm form) {
		String sql = "insert into TMSTTRM_GROUP (REL_ID,TRM_ID, GROUP_ID, CREATOR, CREATE_DATE, MODIFIER, MODIFY_DATE)"
				+ "  values (?, ?, ?, ?, ?, ?, ?)";

		Timestamp timestamp = new Timestamp(form.getRequestTime().getTime());
		String username = form.getLoginUsername();

		doBatchInsert(sql, TerminalGroup.ID_SEQUENCE_NAME, tsns, (st, tsn, relId) -> {
			st.setLong(1, relId);
			st.setString(2, tsn);
			st.setLong(3, groupId);
			st.setString(4, username);
			st.setTimestamp(5, timestamp, UTCTime.UTC_CLENDAR);
			st.setString(6, username);
			st.setTimestamp(7, timestamp, UTCTime.UTC_CLENDAR);
		});
	}

	@Override
	public void insertTerminalGroups(String tsn, Collection<Long> groupIds, BaseForm form) {
		String sql = "insert into TMSTTRM_GROUP (REL_ID,TRM_ID, GROUP_ID, CREATOR, CREATE_DATE, MODIFIER, MODIFY_DATE)"
				+ " values (?, ?, ?, ?, ?, ?, ?)";
		Timestamp timestamp = new Timestamp(form.getRequestTime().getTime());
		String username = form.getLoginUsername();
		doBatchInsert(sql, TerminalGroup.ID_SEQUENCE_NAME, groupIds, (st, groupId, relId) -> {
			st.setLong(1, relId);
			st.setString(2, tsn);
			st.setLong(3, groupId);
			st.setString(4, username);
			st.setTimestamp(5, timestamp, UTCTime.UTC_CLENDAR);
			st.setString(6, username);
			st.setTimestamp(7, timestamp, UTCTime.UTC_CLENDAR);

		});

	}

	@Override
	public Collection<String> insertTerminalGroups(Map<Long, Collection<String>> tsnAncestorGroupMap, BaseForm form) {
		int nums = getLength(tsnAncestorGroupMap);
		if (nums <= 0) {
			return Collections.emptySet();
		}
		Timestamp timestamp = new Timestamp(form.getRequestTime().getTime());
		String username = form.getLoginUsername();
		String sql = "insert into TMSTTRM_GROUP (REL_ID,TRM_ID, GROUP_ID, CREATOR, CREATE_DATE, MODIFIER, MODIFY_DATE)"
				+ " values (?, ?, ?, ?, ?, ?, ?)";
		Iterator<Entry<Long, Collection<String>>> it = tsnAncestorGroupMap.entrySet().iterator();
		Set<String> distinctTsns = new HashSet<String>();
		while (it.hasNext()) {
			Entry<Long, Collection<String>> entry = it.next();
			Long groupId = entry.getKey();
			Collection<String> tsns = entry.getValue();
			if (CollectionUtils.isEmpty(tsns)) {
				continue;
			}
			distinctTsns.addAll(tsns);
			doBatchInsert(sql, TerminalGroup.ID_SEQUENCE_NAME, tsns, (st, tsn, relId) -> {
				st.setLong(1, relId);
				st.setString(2, tsn);
				st.setLong(3, groupId);
				st.setString(4, username);
				st.setTimestamp(5, timestamp, UTCTime.UTC_CLENDAR);
				st.setString(6, username);
				st.setTimestamp(7, timestamp, UTCTime.UTC_CLENDAR);

			});
		}
		return distinctTsns;

	}

	@Override
	public Collection<String> getTerminalUnassignedToGroup(Long batchId) {

		String hql = "select t.tid from TMSPTSNS p,Terminal t where t.tid=p.tsn and p.batchId=:batchId and"
				+ " not exists (select t.id from TerminalGroup tg where tg.terminal.tid=t.tid)";

		return createQuery(hql, String.class).setParameter("batchId", batchId).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<Long, Collection<String>> getLevelOneGroup(Long batchId) {

		String hql = "select distinct g.id, tg.terminal.tid from  TMSPTSNS p,TerminalGroup tg,Group g"
				+ " where tg.group.id=g.id and g.treeDepth=1 and tg.terminal.tid=p.tsn and p.batchId=:batchId";

		List<Object[]> result = createQuery(hql).setParameter("batchId", batchId).getResultList();

		Long groupId;
		String tsn;
		Collection<String> values;
		Map<Long, Collection<String>> map = new HashMap<Long, Collection<String>>();
		for (Object[] arr : result) {
			groupId = (Long) arr[0];
			tsn = (String) arr[1];
			values = map.get(groupId);
			if (values == null) {
				values = new ArrayList<String>();
				map.put(groupId, values);
			}
			values.add(tsn);
		}
		return map;
	}

	@Override
	public void deleteTerminalGroupByTsn(List<String> tsnList) {
		String sql = "delete from TMSTTRM_GROUP where TRM_ID=? ";

		doBatchExecute(sql, tsnList.iterator(), (st, tsn) -> {
			st.setString(1, tsn);

		});
	}

	@Override
	public Collection<String> getTerminalGroupCascading(Long groupId) {
		Assert.notNull(groupId);
		String hql = "select distinct tg.terminal.tid from TerminalGroup tg,Group g where"
				+ " tg.group.id=g.id and g.id=:groupId)";
		return createQuery(hql, String.class).setParameter("groupId", groupId).getResultList();

	}

	@Override
	public List<String> getGroupNamesByTsn(String tsn) {
		String hql = "select tg.group.namePath from TerminalGroup tg where tg.terminal.tid=:tsn";
		return createQuery(hql, String.class).setParameter("tsn", tsn).getResultList();
	}

	@Override
	public List<String> getNeedAssignGroupTsns(Long batchId, Long groupId) {
		return getTargetGroupExistTsns(batchId, groupId);
	}

	@Override
	public List<String> getTargetGroupExistTsns(Long batchId, Long groupId) {

		String hql = " select t.tid from TMSPTSNS p ,Terminal t where t.tid =p.tsn and p.batchId=:batchId "
				+ " and exists (select t.tid from TerminalGroup tg where tg.terminal.tid=t.tid "
				+ " and tg.group.id=:groupId)";
		return createQuery(hql, String.class).setParameter("batchId", batchId).setParameter("groupId", groupId)
				.getResultList();
	}

	@Override
	public List<String> getNeedMoveGroupTsns(Long groupId) {
		String hql = "select t.tid from Terminal t where exists" + " (select tg.id from TerminalGroup tg where"
				+ "  tg.terminal.id=t.id and tg.group.id=:groupId)";
		return createQuery(hql, String.class).setParameter("groupId", groupId).getResultList();
	}

	@Override
	public List<String> getTerminalByGroupId(Long groupId) {
		String hql = "select t.tid from Terminal t,TerminalGroup tg where t.tid=tg.terminal.tid"
				+ "  and tg.group.id=:groupId";
		return createQuery(hql, String.class).setParameter("groupId", groupId).getResultList();
	}

	@Override
	public Map<String, Collection<Long>> tsnAssignGroups(List<String> tsnList) {
		String hql = "select tg.terminal.tid,tg.group.id from TerminalGroup tg where tg.terminal.tid in (:tsnList)";
		List<Object[]> result = createQuery(hql, Object[].class).setParameterList("tsnList", tsnList).getResultList();

		Map<String, Collection<Long>> resultMap = new HashMap<String, Collection<Long>>();

		String tsn = null;
		Long groupId = null;
		Collection<Long> value = null;

		for (Object[] object : result) {
			tsn = (String) object[0];
			groupId = (Long) object[1];
			value = resultMap.get(tsn);
			if (value == null) {
				value = new ArrayList<Long>();
				resultMap.put(tsn, value);
			}
			value.add(groupId);

		}

		return resultMap;
	}

	@Override
	public Map<Long, Collection<String>> getTsnsDistinctGroupAncestor(Long batchId, Long groupId) {
		String insertSql = "select t.TRM_ID,p.PARENT_ID from PUBTGROUP g inner join PUBTGROUP_PARENTS p "
				+ " on g.GROUP_ID=p.GROUP_ID, TMSTTERMINAL t inner join TMSPTSNS temp on t.TRM_ID=temp.TSN where "
				+ " temp.BATCH_ID=:batchId  and g.GROUP_ID=:groupId " + " and not exists (select TRM_ID from "
				+ "  TMSTTRM_GROUP tg where tg.GROUP_ID=p.PARENT_ID and tg.TRM_ID=t.TRM_ID) ";
		List<Object[]> result = createNativeQuery(insertSql, Object[].class).setParameter("batchId", batchId)
				.setParameter("groupId", groupId).getResultList();

		Map<Long, Collection<String>> resultMap = new HashMap<Long, Collection<String>>();

		String tsn = null;
		Long ancestorGroupId = null;
		Collection<String> value = null;

		for (Object[] object : result) {
			tsn = (String) object[0];
			ancestorGroupId = ((BigDecimal) object[1]).longValue();
			value = resultMap.get(ancestorGroupId);
			if (value == null) {
				value = new ArrayList<String>();
				resultMap.put(ancestorGroupId, value);
			}
			value.add(tsn);

		}
		return resultMap;
	}

	private int getLength(Map<Long, Collection<String>> tsnAncestorGroupMap) {

		Iterator<Entry<Long, Collection<String>>> it = tsnAncestorGroupMap.entrySet().iterator();
		int count = 0;
		while (it.hasNext()) {
			count += it.next().getValue().size();
		}
		return count;
	}

	public List<Long> getTsnsDistinctGroupIds(String terminalTsn, Long groupId) {
		String insertSql = "select p.PARENT_ID from PUBTGROUP g inner join PUBTGROUP_PARENTS p "
				+ " on g.GROUP_ID=p.GROUP_ID, TMSTTERMINAL t where t.TRM_ID=:terminalTsn and g.GROUP_ID=:groupId"
				+ "	 and not exists (select TRM_ID from "
				+ "  TMSTTRM_GROUP tg where tg.GROUP_ID=p.PARENT_ID and tg.TRM_ID=t.TRM_ID) ";
		List<Long> groupIds = createNativeQuery(insertSql, Long.class).setParameter("terminalTsn", terminalTsn)
				.setParameter("groupId", groupId).getResultList();

		return groupIds;
	}

	@Override
	public Map<String, Collection<Long>> getTerminalGroupIds(long batchId) {
		String hql = "select p.tsn,tg.group.id from TerminalGroup tg ,TMSPTSNS p"
				+ "  where p.tsn=tg.terminal.tid and p.batchId=:batchId";

		List<Object[]> result = createQuery(hql, Object[].class).setParameter("batchId", batchId).getResultList();
		Map<String, Collection<Long>> resultMap = new HashMap<String, Collection<Long>>();
		String tsn = null;
		Long groupId = null;
		Collection<Long> value = null;

		for (Object[] object : result) {
			tsn = (String) object[0];
			groupId = (Long) object[1];
			value = resultMap.get(tsn);
			if (value == null) {
				value = new ArrayList<Long>();
				resultMap.put(tsn, value);
			}
			value.add(groupId);

		}
		return resultMap;
	}

}
