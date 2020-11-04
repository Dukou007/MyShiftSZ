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
package com.pax.tms.res.dao.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.common.web.form.BaseForm;
import com.pax.tms.res.dao.PkgGroupDao;
import com.pax.tms.res.model.PkgGroup;
import com.pax.tms.res.web.form.AbstractPkgForm;
import com.pax.tms.user.security.UTCTime;

@Repository("pkgGroupDaoImpl")
public class PkgGroupDaoImpl extends BaseHibernateDao<PkgGroup, Long> implements PkgGroupDao {

	@Override
	public Map<Long, Collection<Long>> getLevelOneGroup(Collection<Long> pkgIds) {
		String hql = "select distinct g.id, pkgGroup.pkg.id from PkgGroup pkgGroup,Group g where"
				+ " pkgGroup.group.id=g.id and g.treeDepth=1 and pkgGroup.pkg.id in(:pkgIds)";
		List<Object[]> result = createQuery(hql, Object[].class).setParameterList("pkgIds", pkgIds).getResultList();

		Long groupId;
		Long pkgId;
		Collection<Long> values;
		Map<Long, Collection<Long>> map = new HashMap<Long, Collection<Long>>();
		for (Object[] arr : result) {
			groupId = (Long) arr[0];
			pkgId = (Long) arr[1];
			values = map.get(groupId);
			if (values == null) {
				values = new ArrayList<Long>();
				map.put(groupId, values);
			}
			values.add(pkgId);
		}
		return map;
	}

	@Override
	public void deletePkgGroupCascade(Collection<Long> pkgIds, Long groupId) {
		String sql = "delete from TMSTPKG_GROUP where "
				+ " (GROUP_ID in (select GROUP_ID from PUBTGROUP_PARENTS where PARENT_ID=?)) and PKG_ID =?";

		doBatchExecute(sql, pkgIds.iterator(), (st, pkgId) -> {
			st.setLong(1, groupId);
			st.setLong(2, pkgId);

		});

	}

	@Override
	public Collection<Long> getPkgUnAssignToGroup(Collection<Long> pkgList) {
		String hql = "select p.id from Pkg p where p.id in (:pkgList) and "
				+ " not exists (select pkgGroup.id from PkgGroup pkgGroup where pkgGroup.pkg.id=p.id)";
		return createQuery(hql, Long.class).setParameterList("pkgList", pkgList).getResultList();
	}

	@Override
	public void insertPkgGroup(Collection<Long> pkgIds, Long groupId, BaseForm form) {

		String sql = "insert into TMSTPKG_GROUP (REL_ID,PKG_ID, GROUP_ID, CREATOR, CREATE_DATE, MODIFIER, MODIFY_DATE)"
				+ " values (?, ?, ?, ?, ?, ?, ?)";
		Timestamp timestamp = new Timestamp(form.getRequestTime().getTime());
		String username = form.getLoginUsername();

		doBatchInsert(sql, PkgGroup.ID_SEQUENCE_NAME, pkgIds, (st, pkgId, relId) -> {
			st.setLong(1, relId);
			st.setLong(2, pkgId);
			st.setLong(3, groupId);
			st.setString(4, username);
			st.setTimestamp(5, timestamp, UTCTime.UTC_CLENDAR);
			st.setString(6, username);
			st.setTimestamp(7, timestamp, UTCTime.UTC_CLENDAR);
		});
	}

	@Override
	public void insertPkgGroups(Map<Long, Collection<Long>> pkgAncestorGroupMap, BaseForm form) {
		int nums = getLength(pkgAncestorGroupMap);
		if (nums <= 0) {
			return;
		}
		String sql = "insert into TMSTPKG_GROUP (REL_ID,PKG_ID, GROUP_ID, CREATOR, CREATE_DATE, MODIFIER, MODIFY_DATE)"
				+ " values (?, ?, ?, ?, ?, ?, ?)";
		Timestamp timestamp = new Timestamp(form.getRequestTime().getTime());
		String username = form.getLoginUsername();
		Iterator<Entry<Long, Collection<Long>>> it = pkgAncestorGroupMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Long, Collection<Long>> entry = it.next();
			Long pkgId = entry.getKey();
			Collection<Long> groupIds = entry.getValue();
			doBatchInsert(sql, PkgGroup.ID_SEQUENCE_NAME, groupIds, (st, groupId, relId) -> {
				st.setLong(1, relId);
				st.setLong(2, pkgId);
				st.setLong(3, groupId);
				st.setString(4, username);
				st.setTimestamp(5, timestamp, UTCTime.UTC_CLENDAR);
				st.setString(6, username);
				st.setTimestamp(7, timestamp, UTCTime.UTC_CLENDAR);
			});

		}

	}

	@Override
	public void insertPkgGroup(Long pkgId, Collection<Long> needAddGroups, AbstractPkgForm form) {
		String sql = "insert into TMSTPKG_GROUP (REL_ID,PKG_ID, GROUP_ID, CREATOR, CREATE_DATE, MODIFIER, MODIFY_DATE) values (?, ?, ?, ?, ?, ?, ?)";

		Timestamp timestamp = new Timestamp(form.getRequestTime().getTime());
		String username = form.getLoginUsername();

		doBatchInsert(sql, PkgGroup.ID_SEQUENCE_NAME, needAddGroups, (st, needAddGroupId, relId) -> {
			st.setLong(1, relId);
			st.setLong(2, pkgId);
			st.setLong(3, needAddGroupId);
			st.setString(4, username);
			st.setTimestamp(5, timestamp, UTCTime.UTC_CLENDAR);
			st.setString(6, username);
			st.setTimestamp(7, timestamp, UTCTime.UTC_CLENDAR);
		});

	}

	@Override
	public void deletePkgGroupByPkgId(Collection<Long> pkgIds) {
		String sql = "delete from TMSTPKG_GROUP where PKG_ID=? ";

		doBatchExecute(sql, pkgIds.iterator(), (st, pkgId) -> {
			st.setLong(1, pkgId);

		});

	}

	@Override
	public Map<Long, Collection<Long>> getPkgAssignGroups(Collection<Long> pkgIdList) {
		String hql = "select pkgGroup.pkg.id,pkgGroup.group.id  from PkgGroup pkgGroup where pkgGroup.pkg.id in(:pkgIdList)";
		List<Object[]> result = createQuery(hql, Object[].class).setParameterList("pkgIdList", pkgIdList)
				.getResultList();

		Map<Long, Collection<Long>> resultMap = new HashMap<Long, Collection<Long>>();

		Long pkgId = null;
		Long groupId;
		Collection<Long> value = null;

		for (Object[] object : result) {
			pkgId = (Long) object[0];
			groupId = (Long) object[1];
			value = resultMap.get(pkgId);
			if (value == null) {
				value = new ArrayList<Long>();
				resultMap.put(pkgId, value);
			}
			value.add(groupId);

		}

		return resultMap;
	}
	
	@Override
	public List<String> getGroupNamesByPkgId(Long pkgId) {
		String hql = "select pg.group.namePath from PkgGroup pg where pg.pkg.id=:pkgId";
		return createQuery(hql, String.class).setParameter("pkgId", pkgId).getResultList();
	}

	@Override
	public List<String> getPkgNamesByGroupId(Long groupId, String type) {
		String hql = "select distinct p.name from Pkg p where exists(select pkgGroup.id from"
				+ " PkgGroup pkgGroup where pkgGroup.pkg.id=p.id and "
				+ " pkgGroup.group.id=:groupId) and p.status=:status";
		if (StringUtils.isNotEmpty(type)) {
			hql += " and p.type=:type";
		}
		Query<String> query = createQuery(hql, String.class).setParameter("groupId", groupId).setParameter("status",
				true);
		if (StringUtils.isNotEmpty(type)) {
			query.setParameter("type", type);
		}
		return query.getResultList();
	}

	@Override
	public List<String> getPkgNamesByGroupIdAndDestmodel(Long groupId, String destModel, String type) {
		String hql = "select distinct p.name from Pkg p where exists(select pkgGroup.id from"
				+ " PkgGroup pkgGroup where pkgGroup.pkg.id=p.id and "
				+ " pkgGroup.group.id=:groupId) and p.model.id=:destModel " + " and p.status=:status ";
		if (StringUtils.isNotEmpty(type)) {
			hql += " and p.type=:type";
		}
		Query<String> query = createQuery(hql, String.class).setParameter("groupId", groupId)
				.setParameter("destModel", destModel).setParameter("status", true);
		if (StringUtils.isNotEmpty(type)) {
			query.setParameter("type", type);
		}
		return query.getResultList();
	}

	@Override
	public Map<Long, Collection<Long>> getPkgDistinctGroupAncestor(Collection<Long> pkgIds, Long groupId) {
		if (CollectionUtils.isEmpty(pkgIds)) {
			return Collections.emptyMap();
		}
		String insertSql = "select p.PKG_ID,ga.PARENT_ID from PUBTGROUP g inner join PUBTGROUP_PARENTS ga "
				+ " on g.GROUP_ID=ga.GROUP_ID, TMSTPACKAGE p  where g.GROUP_ID=:groupId and p.PKG_ID "
				+ " in (:pkgIds) and not exists (select REL_ID from "
				+ "  TMSTPKG_GROUP pg where pg.GROUP_ID=ga.PARENT_ID and pg.PKG_ID=p.PKG_ID) ";
		List<Object[]> result = createNativeQuery(insertSql, Object[].class).setParameter("pkgIds", pkgIds)
				.setParameter("groupId", groupId).getResultList();

		Map<Long, Collection<Long>> resultMap = new HashMap<Long, Collection<Long>>();

		Long pkgId = null;
		Long ancestorGroupId = null;
		Collection<Long> value = null;

		for (Object[] object : result) {
			pkgId = ((BigDecimal) object[0]).longValue();
			ancestorGroupId = ((BigDecimal) object[1]).longValue();
			value = resultMap.get(pkgId);
			if (value == null) {
				value = new ArrayList<Long>();
				resultMap.put(pkgId, value);
			}
			value.add(ancestorGroupId);

		}
		return resultMap;
	}

	private int getLength(Map<Long, Collection<Long>> pkgAncestorGroupMap) {

		Iterator<Entry<Long, Collection<Long>>> it = pkgAncestorGroupMap.entrySet().iterator();
		int count = 0;
		while (it.hasNext()) {
			count += it.next().getValue().size();
		}
		return count;
	}

}
