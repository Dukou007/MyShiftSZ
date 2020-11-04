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
package com.pax.tms.group.dao.impl;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.criterion.MatchMode;
import org.hibernate.query.Query;
import org.hibernate.transform.ResultTransformer;
import org.springframework.stereotype.Repository;

import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.tms.group.GroupInSearch;
import com.pax.tms.group.GroupOrTerminal;
import com.pax.tms.group.dao.GroupDao;
import com.pax.tms.group.model.Group;
import com.pax.tms.group.model.GroupAncestor;
import com.pax.tms.terminal.model.Terminal;
import com.pax.tms.terminal.model.TerminalGroup;

@Repository("groupDaoImpl")
public class GroupDaoImpl extends BaseHibernateDao<Group, Long> implements GroupDao {

	@Override
	public boolean existGroupName(Long parentId, String name) {
		String hql = "select count(id) from Group where parent.id=:parentId and name=:name";
		Query<Long> query = createQuery(hql, Long.class).setParameter("parentId", parentId).setParameter("name", name);
		return query.getSingleResult() > 0;
	}

	@Override
	public Group getGroup(long parentId, String groupName) {
		String hql = "select g from Group g where g.parent.id=:parentId and g.name=:name";
		Query<Group> query = createQuery(hql, Group.class).setParameter("parentId", parentId).setParameter("name",
				groupName);
		return uniqueResult(query);
	}

	@Override
	public List<String> searchGroupByNamePath(String keyword, Long userId) {
		String hql = "select g.namePath from Group g left join GroupAncestor ga on ga.group.id=g.id "
		        + " left join UserGroup ug on ug.group.id=ga.ancestor.id "
				+ " where ug.user.id=:userId"
				+ " and lower(g.namePath) like :keyword escape '!' order by namePath";
		return createQuery(hql, String.class).setParameter("userId", userId)
				.setParameter("keyword", toMatchString(keyword, MatchMode.ANYWHERE, true)).setMaxResults(50)
				.getResultList();
	}

	@Override
	public Group getGroupByNamePath(String namePath) {
		String hql = "select group from Group group where group.namePath =:namePath";
		Query<Group> query = createQuery(hql, Group.class).setParameter("namePath", namePath);
		return uniqueResult(query);
	}

	@Override
	public List<String> getTerminalTypesByGroupId(Long groupId) {

		String hql = "select model.name from Model model where exists "
				+ " (select t.id from Terminal t left join TerminalGroup tg on t.id=tg.terminal.id"
				+ " where t.model.id=model.id and tg.group.id=:groupId)";
		return createQuery(hql, String.class).setParameter("groupId", groupId).getResultList();

	}

	@Override
	public Long getTerminalNumbersByGroupId(Long groupId) {
		String hql = " select count(t.id) from Terminal t where exists (select tg.id from TerminalGroup tg"
				+ " where tg.terminal.id=t.id and tg.group.id=:groupId) ";
		Query<Long> query = createQuery(hql, Long.class).setParameter("groupId", groupId);
		return uniqueResult(query);
	}

	@Override
	public List<TerminalGroup> getTerminalGroupsById(Long groupId) {
		String hql = "select terminalGroup from TerminalGroup terminalGroup where terminalGroup.group.id=:groupId";
		return createQuery(hql, TerminalGroup.class).setParameter("groupId", groupId).getResultList();

	}

	@Override
	public boolean isGroupHasTerminal(Long groupId) {
		String hql = "select terminalGroup from TerminalGroup terminalGroup where terminalGroup.group.id=:groupId";
		Query<TerminalGroup> query = createQuery(hql, TerminalGroup.class).setParameter("groupId", groupId)
				.setMaxResults(1);
		return super.uniqueResult(query) != null ? true : false;
	}

	@Override
	public boolean existTerminalGroup(Long groupId, String tsn) {
		String hql = "select count(*) from TerminalGroup terminalGroup "
				+ " where terminalGroup.group.id=:groupId and terminalGroup.terminal.tsn=:tsn";
		return createQuery(hql, Integer.class).setParameter("groupId", groupId).setParameter("tsn", tsn)
				.getSingleResult() > 0;

	}

	@Override
	public List<Group> getSelfAndDescendantGroup(Long groupId) {
		String hql = "select group from Group group left join GroupAncestor groupAncestor on group.id=groupAncestor.group.id "
				+ "  where groupAncestor.ancestor.id=:groupId";
		return createQuery(hql, Group.class).setParameter("groupId", groupId).getResultList();
	}

	@Override
	public boolean isSelfOrDescendantGroup(long groupId, long descendantGroupId) {
		String hql = "select count(*) from GroupAncestor groupAncestor where groupAncestor.group.id=:descendantGroupId and groupAncestor.ancestor.id=:groupId";
		return createQuery(hql, Long.class).setParameter("descendantGroupId", descendantGroupId)
				.setParameter("groupId", groupId).getSingleResult() > 0;
	}

	@Override
	public List<Group> getSelfAndAncestorGroup(Long groupId) {
		String hql = "select group from Group group left join GroupAncestor groupAncestor on group.id=groupAncestor.ancestor.id "
		        + " where groupAncestor.group.id=:groupId order by group.id";
		return createQuery(hql, Group.class).setParameter("groupId", groupId).getResultList();
	}

	@Override
	public void deleteGroupAncestor(long groupId) {
		String hql = "delete from GroupAncestor where group.id=:groupId";
		createQuery(hql).setParameter("groupId", groupId).executeUpdate();
	}

	@Override
	public void updateGroupAncestor(Group sourceGroup, Group targetGroup) {
		Long sourceGroupId = sourceGroup.getId();
		Long targetGroupId = targetGroup.getId();

		String oldParentGroupHql = "select ga.ancestor.id from GroupAncestor ga"
				+ " where ga.group.id = :sourceGroupId and ga.ancestor <> :sourceGroupId";
		List<Long> oldParentGroupIds = createQuery(oldParentGroupHql, Long.class)
				.setParameter("sourceGroupId", sourceGroupId).getResultList();

		String childrenGroupHql = "select ga.group.id from GroupAncestor ga where ga.ancestor.id=:sourceGroupId";
		List<Long> childrenGroupIds = createQuery(childrenGroupHql, Long.class)
				.setParameter("sourceGroupId", sourceGroupId).getResultList();

		String sql = "delete from PUBTGROUP_PARENTS where GROUP_ID=? and PARENT_ID=?";

		doWork(conn -> {
			try (PreparedStatement st = conn.prepareStatement(sql)) {
				int count = 0;
				for (Long parentId : oldParentGroupIds) {
					for (Long groupId : childrenGroupIds) {
						st.setLong(1, groupId);
						st.setLong(2, parentId);
						st.addBatch();
						count++;
						if (count % 100 == 0) {
							st.executeBatch();
							st.clearBatch();
						}
					}
				}
				if (count % 100 != 0) {
					st.executeBatch();
					st.clearBatch();
				}
			}
		});

		String insertSql = "insert into PUBTGROUP_PARENTS (GROUP_ID,PARENT_ID)"
				+ " select gp1.GROUP_ID, gp2.PARENT_ID from PUBTGROUP_PARENTS gp1, PUBTGROUP_PARENTS gp2"
				+ " where gp1.PARENT_ID=:sourceGroupId and gp2.GROUP_ID = :targetGroupId";
		this.createNativeQuery(insertSql).setParameter("sourceGroupId", sourceGroupId)
				.setParameter("targetGroupId", targetGroupId).executeUpdate();
	}

	@Override
	public void deleteGroupCascading(Long groupId) {
		String hql = "select ga.group.id from GroupAncestor ga where ga.ancestor.id=:groupId";
		List<Long> groupIds = createQuery(hql, Long.class).setParameter("groupId", groupId).getResultList();
		String deleteGroup = "delete from Group g where g.id in "
				+ " (select ga.group.id from GroupAncestor ga where ga.ancestor.id=:groupId)";
		createQuery(deleteGroup).setParameter("groupId", groupId).executeUpdate();

		String deleteGroupAncestor = "delete from PUBTGROUP_PARENTS  where GROUP_ID =?";
		doBatchExecute(deleteGroupAncestor, groupIds.iterator(), (st, childGroupId) -> st.setLong(1, childGroupId));

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<Map<String, Object>> getUserTopGroups(long loginUserId, int startPosition, int maxResult) {
		String hql = "select g.id as id, g.name as name, g.idPath as idPath,g.namePath as namePath, g.subCount as subCount"
				+ " from Group g left join UserGroup ug on ug.group.id=g.id where ug.user.id=:userId";

		Query query = createQuery(hql).setParameter("userId", loginUserId);
		if (startPosition > 0) {
			query.setFirstResult(startPosition);
		}
		if (maxResult > 0) {
			query.setMaxResults(maxResult);
		}
		return mapResult(query).getResultList();
	}

	@Override
	public int countUserTopGroups(long groupId) {
		String hql = "select count(g.id) from Group g left join UserGroup ug on ug.group.id=g.id  where ug.user.id=:userId";
		return createQuery(hql, Long.class).setParameter("userId", groupId).getSingleResult().intValue();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<Map<String, Object>> getChildGroups(long groupId, int startPosition, int maxResult) {
		String hql = "select g.id as id, g.name as name,g.idPath as idPath, g.namePath as namePath, g.subCount as subCount"
				+ " from Group g where g.parent.id=:parentId order by g.name";

		Query query = createQuery(hql).setParameter("parentId", groupId);
		if (startPosition > 0) {
			query.setFirstResult(startPosition);
		}
		if (maxResult > 0) {
			query.setMaxResults(maxResult);
		}
		return mapResult(query).getResultList();
	}
	
    @Override
    public List<Group> getAllChildGroups(long groupId) {
        String hql = "select g  from Group g where g.parent.id=:parentId ";
        return createQuery(hql, Group.class).setParameter("parentId", groupId).getResultList();
    }

	@Override
	public void addChildGroupCount(long groupId, int increment) {
		String sql = "update PUBTGROUP set SUB_COUNT=SUB_COUNT + " + increment + " where GROUP_ID=:groupId";
		createNativeQuery(sql).setParameter("groupId", groupId).executeUpdate();
	}

	@Override
	public void minusChildGroupCount(long groupId, int decrease) {
		String sql = "update PUBTGROUP set SUB_COUNT=SUB_COUNT - " + decrease + " where GROUP_ID=:groupId";
		createNativeQuery(sql).setParameter("groupId", groupId).executeUpdate();
	}

	@Override
	public int countSearchGroupTree(String keyword, long groupId) {
		String hql = "select count(g.id) from Group g left join GroupAncestor ga on  g.id=ga.group.id "
				+ " where ga.ancestor.id=:groupId"
				+ " and lower(g.namePath) like :keyword escape '!'";
		return createQuery(hql, Long.class).setParameter("groupId", groupId)
				.setParameter("keyword", toMatchString(keyword, MatchMode.ANYWHERE, true)).getSingleResult().intValue();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<Map<String, Object>> searchGroupTree(String keyword, long groupId, int startPosition, int maxResult) {

		String hql = "select g.id as id, g.name as name, g.namePath as namePath, g.idPath as idPath, g.subCount as subCount"
				+ " from Group g left join GroupAncestor ga on g.id=ga.group.id where ga.ancestor.id=:groupId"
				+ " and lower(g.name) like :keyword escape '!' order by name";

		Query query = createQuery(hql).setParameter("groupId", groupId).setParameter("keyword",
				toMatchString(keyword, MatchMode.ANYWHERE, true));
		if (startPosition > 0) {
			query.setFirstResult(startPosition);
		}
		if (maxResult > 0) {
			query.setMaxResults(maxResult);
		}

		return mapResult(query).getResultList();
	}

	@Override
	public List<GroupOrTerminal> searchGroupOrTerminal(String keyword, Long loginUserId, String[] groupPathAndTsn) {
		List<GroupOrTerminal> result = new ArrayList<GroupOrTerminal>();
		if (ArrayUtils.isNotEmpty(groupPathAndTsn)) {
			String hql = "select g.id as groupId, g.namePath as groupPath,tg.terminal.tid as tsn"
					+ " from Group g left join TerminalGroup tg on g.id=tg.group.id "
					+ " left join GroupAncestor ga on ga.group.id=g.id left join UserGroup ug on ug.group.id=ga.ancestor.id "
					+ " where ug.user.id=:userId and tg.terminal.tid=:tsn"
					+ " and lower(g.namePath) like :namePath escape '!' order by groupPath";
			result.addAll(createQuery(hql, GroupOrTerminal.class, false).setParameter("tsn", groupPathAndTsn[1])
					.setParameter("namePath", toMatchString(groupPathAndTsn[0], MatchMode.ANYWHERE, true))
					.setParameter("userId", loginUserId).setMaxResults(50).getResultList());
		}
		if (result.size() < 50) {
			String hql1 = "select g.id as groupId, g.namePath as groupPath,tg.terminal.tid as tsn"
					+ " from Group g left join TerminalGroup tg on g.id=tg.group.id "
					+ " left join GroupAncestor ga on ga.group.id=g.id left join UserGroup ug on ug.group.id=ga.ancestor.id "
					+ " where ug.user.id=:userId and tg.terminal.tid=:tsn order by tsn";

			result.addAll(createQuery(hql1, GroupOrTerminal.class, false).setParameter("tsn", keyword)
					.setParameter("userId", loginUserId).setMaxResults(50 - result.size()).getResultList());
		}

		if (result.size() < 50) {
			String hql2 = "select g.id as groupId, g.namePath as groupPath"
					+ " from Group g left join GroupAncestor ga on ga.group.id=g.id left join UserGroup ug on ug.group.id=ga.ancestor.id "
					+ " where ug.user.id=:userId and lower(g.namePath) like :namePath escape '!'"
					+ " order by groupPath";

			result.addAll(createQuery(hql2, GroupOrTerminal.class, false)
					.setParameter("namePath", toMatchString(keyword, MatchMode.ANYWHERE, true))
					.setParameter("userId", loginUserId).setMaxResults(50 - result.size()).getResultList());

		}

		return result;
	}
	
	@Override
	public List<GroupInSearch> searchGroup(String keyword, Long loginUserId) {
		List<GroupInSearch> result = new ArrayList<GroupInSearch>();
		String hql2 = "select g.id as groupId, g.namePath as groupPath"
					+ " from Group g left join GroupAncestor ga on ga.group.id=g.id left join UserGroup ug on ug.group.id=ga.ancestor.id "
					+ " where ug.user.id=:userId and lower(g.namePath) like :namePath escape '!'"
					+ " order by groupPath";
		result.addAll(createQuery(hql2, GroupInSearch.class, false)
					.setParameter("namePath", toMatchString(keyword, MatchMode.ANYWHERE, true))
					.setParameter("userId", loginUserId).setMaxResults(50 - result.size()).getResultList());
		return result;
	}

	@Override
	public List<Group> getEnterpriseGroups() {
		String hql = "select g from Group g where g.treeDepth=1";
		return createQuery(hql, Group.class).getResultList();
	}
	
	@Override
	public List<Group> getEnterpriseAndRootGroups() {
		String hql = "select g from Group g where g.treeDepth=1 or g.treeDepth=0";
		return createQuery(hql, Group.class).getResultList();
	}
	
	@Override
	public List<Map<String, Object>> groupToMap(List<Group> groupList){
	    List<Map<String, Object>> groupMapList = new ArrayList<>();
	    if(groupList == null){
	        return groupMapList;
	    }
	    for (Group group : groupList) {
	        Map<String, Object> groupMap = new HashMap<>();
	        groupMap.put("id", group.getId());
	        groupMap.put("name", group.getName());
	        groupMap.put("treeDepth", group.getTreeDepth());
	        groupMap.put("idPath", group.getIdPath());
	        groupMap.put("namePath", group.getNamePath());
	        groupMap.put("subCount", group.getSubCount());
	        groupMapList.add(groupMap);
        }
	    return groupMapList;
	}

	@Override
	public Map<Long, List<Group>> getParentMappingChild() {
	    String hql = "select g from Group g order by g.id";
		List<Group> groups = createQuery(hql, Group.class).getResultList();
		if(null != groups && !groups.isEmpty()){
		    return groups.stream().collect(Collectors.groupingBy(Group::getParentId)); 
		}else{
		    return new HashMap<Long, List<Group>>();
		}
		
	}

	@Override
	public boolean isCreateByUpLevelUser(Long groupId, Long loginUserId) {
		String hql = "select count(g.id) from Group g where g.id=:groupId"
				+ " and exists (select ug.id from UserGroup ug where ug.group.id=g.id"
				+ " and ug.user.id=:loginUserId)";

		return createQuery(hql, Long.class).setParameter("groupId", groupId).setParameter("loginUserId", loginUserId)
				.getSingleResult().longValue() > 0 ? true : false;
	}

	@Override
	public Group getExistGroup(Long groupId, String groupName) {

		String hql = "select g from Group g where g.parent.id=:groupId and g.name=:groupName";
		return uniqueResult(
				createQuery(hql, Group.class).setParameter("groupId", groupId).setParameter("groupName", groupName));
	}

	@Override
	public <T> List<T> searchGroupByGroupPath(long currentGroupId, String groupPath, Class<T> resultType,
			ResultTransformer resultTransformer) {
		String hql = "select g.id as id, g.namePath as name from Group g left join GroupAncestor ga on g.id=ga.group.id "
				+ " where ga.ancestor.id=:groupId"
				+ " and lower(g.namePath) like :keyword escape '!' order by g.id";
		return createQuery(hql, resultType, resultTransformer).setParameter("groupId", currentGroupId)
				.setParameter("keyword", toMatchString(groupPath, MatchMode.ANYWHERE, true)).getResultList();
	}

	@Override
	public <T> List<T> getAllGroupNames(Class<T> resultType, ResultTransformer resultTransformer) {
		String hql = "select g.id as id, g.name as name from Group g order by g.id";
		return createQuery(hql, resultType, resultTransformer).getResultList();
	}

	@Override
	public <T> List<T> getSelfAndDescendantOrganizationNames(long currentGroupId, Class<T> resultType,
			ResultTransformer resultTransformer) {
		String hql = "select g.id as id, g.namePath as name from Group g left join GroupAncestor ga on g.id=ga.group.id "
				+ "  where ga.ancestor.id=:groupId order by g.id";
		return createQuery(hql, resultType, resultTransformer).setParameter("groupId", currentGroupId).getResultList();
	}

	@Override
	public Group findGroupByGroupPath(long currentGroupId, String groupPath) {
		String hql = "select g from Group g left join GroupAncestor ga on g.id=ga.group.id "
		        + " where  ga.ancestor.id=:groupId"
				+ " and lower(g.namePath)=:keyword";
		Query<Group> query = createQuery(hql, Group.class).setParameter("groupId", currentGroupId)
				.setParameter("keyword", groupPath != null ? groupPath.toLowerCase() : null);
		return uniqueResult(query);
	}

	@Override
	public <T> List<T> getUserGroups(long userId, Class<T> resultType, ResultTransformer resultTransformer) {
		String hql = "select g.id as id, g.name as name"
				+ " from Group g left join UserGroup ug on  ug.group.id=g.id "
				+ " where ug.user.id=:userId";
		return createQuery(hql, resultType, resultTransformer).setParameter("userId", userId).getResultList();
	}

	@Override
	public List<String> getGroupNamesById(List<Long> groupIds) {
		if (CollectionUtils.isEmpty(groupIds)) {
			return Collections.emptyList();
		}
		String hql = "select g.namePath from Group g where g.id in (:groupIds)";
		return createQuery(hql, String.class).setParameter("groupIds", groupIds).getResultList();
	}

	@Override
	public List<Long> getGrantedTopGroups(long userId) {
		String hql = "select ug.group.id as id" + " from UserGroup ug where ug.user.id=:userId";
		return createQuery(hql, Long.class).setParameter("userId", userId).getResultList();
	}

	@Override
	public List<Long> getGrantedAllGroups(long userId) {
		String hql = "select ga.group.id as id"
				+ " from GroupAncestor ga left join UserGroup ug on ug.group.id=ga.ancestor.id where ug.user.id=:userId";
		return createQuery(hql, Long.class).setParameter("userId", userId).getResultList();
	}

	@Override
	public List<Long> getSelfAndDescendantGroupIds(Long groupId) {
		String hql = "select group.id from Group group left join GroupAncestor groupAncestor on group.id=groupAncestor.group.id "
				+ "  where groupAncestor.ancestor.id=:groupId";
		return createQuery(hql, Long.class).setParameter("groupId", groupId).getResultList();
	}
	
	@Override
    public Map<Long, List<TerminalGroup>> getGroupMappingTerminal() {
	        String hql = "select terminalGroup from TerminalGroup terminalGroup ";
        List<TerminalGroup> groups = createQuery(hql, TerminalGroup.class).getResultList();
        if(null != groups && !groups.isEmpty()){
            return groups.stream().collect(Collectors.groupingBy(TerminalGroup::getGroupId)); 
        }else{
            return new HashMap<Long, List<TerminalGroup>>();
        }
        
    }
	
	@Override
    public Map<Long, List<GroupAncestor>> getGroupMappingDescendantGroup() {
        String hql = "select groupAncestor from GroupAncestor groupAncestor order by groupAncestor.group.id ";
        List<GroupAncestor> groupAncestorList = createQuery(hql, GroupAncestor.class).getResultList();
        if(null != groupAncestorList && !groupAncestorList.isEmpty()){
            return groupAncestorList.stream().collect(Collectors.groupingBy(GroupAncestor::getParentId)); 
        }else{
            return new HashMap<Long, List<GroupAncestor>>();
        }
    }

	@Override
	public void syncProfileToTerminals(Long groupId, Terminal terminal) {
		String sql = "UPDATE tmstterminal t SET t.COUNTRY =:country,t.CITY =:city,t.PROVINCE =:province,t.ZIP_CODE =:zipCode,t.TIME_ZONE =:timeZone,t.DAYLIGHT_SAVING=:daylightSaving"+
				" WHERE TRM_ID IN (SELECT TRM_ID FROM tmsttrm_group tg WHERE tg.GROUP_ID =:groupId )";
		createNativeQuery(sql).setParameter("country", terminal.getCountry())
							  .setParameter("city", terminal.getCity())
							  .setParameter("province", terminal.getProvince())
							  .setParameter("zipCode", terminal.getZipCode())
							  .setParameter("timeZone", terminal.getTimeZone())
							  .setParameter("daylightSaving", terminal.isDaylightSaving())
							  .setParameter("groupId", groupId)
							  .executeUpdate();
		
	}
	
	@Override
	public List<Group> getGroupsByParentId(Long parentId) {
		String hql = "select group from Group group where group.parent.id=:parentId";
		return  createQuery(hql, Group.class).setParameter("parentId", parentId).getResultList();
	}
	
}
